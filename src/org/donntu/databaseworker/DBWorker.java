package org.donntu.databaseworker;

import javafx.util.Pair;
import org.donntu.GregorianCalendar;
import org.donntu.generator.*;
import org.donntu.generator.generatorException.NodeRelationsException;
import org.donntu.generator.generatorException.OneselfConnection;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class DBWorker {
    private static DBConnector dbConnector;
    private static Statement statement;
    private static String query;
    private static ResultSet resultSet;

    private DBWorker() {
    }

    public static void setDbConnector(DBConnector dbConnector) throws SQLException {
        if (dbConnector == null) {
            throw new NullPointerException();
        }
        if (DBWorker.dbConnector != dbConnector) {
            System.out.println("Сам себя");
            DBWorker.dbConnector = dbConnector;
            if (!dbConnector.isOpen()) {
                if (!dbConnector.connectToDB()) {
                    throw new SQLException("Соединение не установлено");
                }
            }
            statement = dbConnector.getConnection().createStatement();
        }
    }

    private static boolean checkGroupInDB(String group) {
        if (group != null && !group.isEmpty()) {
            try {
                query = "SELECT Название FROM группы " +
                        "WHERE (`Название` = \'" + group + "\');";
                resultSet = statement.executeQuery(query);
                return resultSet.next();
            } catch (SQLException e) {
                return false;
            }
        }

        return false;
    }

    private static Integer getGroupID(String group) {
        if (group != null && !group.isEmpty()) {
            try {
                query = "SELECT idГруппы,Название FROM группы " +
                        "WHERE (`Название` = \'" + group + "\');";
                resultSet = statement.executeQuery(query);
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                } else {
                    return null;
                }
            } catch (SQLException e) {
                return null;
            }
        }
        return null;
    }

    private static Integer getStudentID(String name, String surname, String group) {
        if (name != null && !name.isEmpty() && surname != null && !surname.isEmpty()) {
            try {
                query = "SELECT `idстудента` FROM студенты " +
                        "WHERE `Имя` = \'" + name + "\' AND `Фамилия` = \'" + surname + "\' AND `idГруппы` = \'" + getGroupID(group) + "\';";
                resultSet = statement.executeQuery(query);
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                } else {
                    return null;
                }
            } catch (SQLException e) {
                return null;
            }
        }
        return null;
    }

    private static boolean checkStudentInDB(String name, String surname, String group) {
        try {
            query = "SELECT Имя, Фамилия FROM студенты " +
                    "WHERE `Имя` = \'" + name + "\' AND `Фамилия` = \'" + surname + "\' AND `idГруппы` = \'" + getGroupID(group) + "\';";
            resultSet = statement.executeQuery(query);
            return resultSet.next();
        } catch (SQLException e) {
            return false;
        }
    }

    public static boolean addGroup(String group) throws SQLException {
        setDbConnector(DBConnector.getInstance());
        if (!checkGroupInDB(group)) {
            query = "INSERT INTO группы(Название) VALUES (\'" + group + "\');";
            statement.execute(query);
        } else {
            return false;
        }
        return true;
    }

    public static boolean addStudent(String name, String surname, String group) throws SQLException {
        setDbConnector(DBConnector.getInstance());
        if (!checkGroupInDB(group)) {
            addGroup(group);
        }
        if (!checkStudentInDB(name, surname, group)) {
            query = "INSERT INTO студенты(Имя, Фамилия, idГруппы) " +
                    "VALUES (\'" + name + "\',\'" + surname + "\',\'" + getGroupID(group) + "\');";
            statement.execute(query);
        } else {
            return false;
        }
        return true;
    }

    private static Integer addTaskForStudent(String name, String surname, String group, GregorianCalendar date) throws SQLException {

        Integer idStudent = getStudentID(name, surname, group);
        if (idStudent == null) {
            addStudent(name, surname, group);
            idStudent = getStudentID(name, surname, group);
        }


        query = "INSERT INTO задания(idстудента, `Дата создания`) " +
                "VALUE (\'" + idStudent + "\',\'" + new Date(date.getTimeInMillis()) + "\');";
        statement.execute(query);
        resultSet = statement.executeQuery("SELECT last_insert_id() AS \'last_id\' FROM задания");
        resultSet.next();
        return resultSet.getInt("last_id");

    }

    private static int addNetworksConnection(int idNetworkFrom, int idNetworkTo, int idNodeFrom, int idNodeTo) throws SQLException {
        query = "INSERT INTO соединения(`idсети`, `idузла`,`idсети соединенного узла`,`idсоединенного узла`)\n" +
                "VALUES ('" + idNetworkFrom + "','" + idNodeFrom + "','" + idNetworkTo
                + "','" + idNodeTo + "');";
        statement.execute(query);
        resultSet = statement.executeQuery("SELECT last_insert_id() AS last_id FROM `задания`");
        resultSet.next();
        return resultSet.getInt("last_id");
    }

    private static int addNetwork(int idTask, Network network, boolean addNodes) throws SQLException {
        final IP ip = network.getIp();
        query = "INSERT INTO сети(`Тип сети`, `Первый октет`,`Второй октет`,`Третий октет`,`Четвертый октет`,`Маска`, idЗадания)" +
                "VALUES (\'" + network.getType() + "\',\'" + ip.getFirst()
                + "\',\'" + ip.getSecond()
                + "\',\'" + ip.getThird()
                + "\',\'" + ip.getFourth()
                + "\',\'" + ip.getMask()
                + "\',\'" + idTask + "\');";
        statement.execute(query);
        resultSet = statement.executeQuery("SELECT last_insert_id() AS last_id FROM `задания`");
        resultSet.next();
        final int lastNetworkID = resultSet.getInt("last_id");
        if (addNodes) {
            final List<Node> nodes = network.getNodes();
            for (Node node : nodes) {
                addNode(lastNetworkID, node);
            }
            final List<Pair<Node, Node>> connections = network.getUniqueConnections();
            for (Pair<Node, Node> pair : connections) {
                addNodeConnection(lastNetworkID, pair.getKey().getID(), lastNetworkID, pair.getValue().getID());
            }
        }
        return lastNetworkID;
    }

    private static int addNodeConnection(int idNetworkFrom, int idFrom, int idNetworkTo, int idTo) throws SQLException {
        query = "INSERT INTO `соединения`(`idузла`,`idсоединенного узла`,`idсети`, `idсети соединенного узла`)" +
                "VALUES (\'" + idFrom + "\', " +
                "\'" + idTo + "\', " +
                "\'" + idNetworkFrom + "\', " +
                "\'" + idNetworkTo + "\');";
        statement.execute(query);
        resultSet = statement.executeQuery("SELECT last_insert_id() AS last_id FROM `узлы`");
        resultSet.next();
        return resultSet.getInt("last_id");
    }

    private static int addNode(int idNetwork, Node node) throws SQLException {
        query = "INSERT INTO `узлы`(`idСети`,`НомерУзла`,`X`,`Y`)" +
                "VALUES (\'" + idNetwork + "\', " +
                "\'" + node.getID() + "\', " +
                "\'" + node.getCellNumber_X() + " \'," +
                "\'" + node.getCellNumber_Y() + "\');";
        statement.execute(query);
        resultSet = statement.executeQuery("SELECT last_insert_id() AS last_id FROM `узлы`");
        resultSet.next();
        return resultSet.getInt("last_id");
    }

    public static boolean addStudentTask(StudentTask studentTask) throws NullPointerException, SQLException {
        setDbConnector(DBConnector.getInstance());
        if (studentTask == null) {
            throw new NullPointerException();
        }
        try {

            addGroup(studentTask.getGroup());
            addStudent(studentTask.getName(), studentTask.getSurname(), studentTask.getGroup());
            int idTask = addTaskForStudent(
                    studentTask.getName(),
                    studentTask.getSurname(),
                    studentTask.getGroup(),
                    studentTask.getCreationDate());
            Topology topology = studentTask.getTopology();
            int idWAN = addNetwork(
                    idTask,
                    topology.getWAN(), true);
            List<Pair<Integer, Network>> idLAN = new ArrayList<>();
            final List<Network> laNs = topology.getLANs();
            for (Network lan : laNs) {
                idLAN.add(new Pair<>(addNetwork(idTask, lan, true), lan));
            }

            final List<NetworksConnection> networksConnections = topology.getUniqueNetworksConnections();
            for (NetworksConnection networksConnection : networksConnections) {
                if (networksConnection.getFromNetwork().getType() != NetworkType.WAN) {
                    if (networksConnection.getToNetwork().getType() != NetworkType.WAN) {
                        continue;
                    }
                    networksConnection = networksConnection.getInvertedConnection();
                }

                int index = 0;
                for (Pair<Integer, Network> pair : idLAN) {
                    if (pair.getValue().equals(networksConnection.getToNetwork())) {
                        break;
                    }
                    index++;
                }
                if (index == idLAN.size()) {
                    continue;
                }
                addNetworksConnection(idWAN,
                        idLAN.get(index).getKey(),
                        networksConnection.getFromNetworkNode().getID(),
                        networksConnection.getToNetworkNode().getID());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static List<String> getGroups() throws SQLException {
        setDbConnector(DBConnector.getInstance());
        List<String> groups = new ArrayList<>();
        query = "SELECT `Название` FROM `Группы`";
        resultSet = statement.executeQuery(query);
        while (resultSet.next()) {
            groups.add(resultSet.getString(1));
        }
        return groups;
    }

    public static List<HashMap<String, String>> getStudentsTaskList() throws SQLException {
        setDbConnector(DBConnector.getInstance());
        List<HashMap<String, String>> students = new ArrayList<>();

        query = "SELECT `Студенты`.`Имя` AS `Имя`, " +
                "`Студенты`.`Фамилия` AS `Фамилия`, " +
                "`Группы`.`Название` AS `Группа`, " +
                "`Задания`.`Дата создания` AS `Дата создания` " +
                "FROM `Студенты`, `группы`, `задания`" +
                "WHERE `Студенты`.`idГруппы` = `Группы`.`idГруппы` " +
                "AND `Задания`.`idстудента` = `Студенты`.`idстудента`";
        resultSet = statement.executeQuery(query);
        while (resultSet.next()) {
            HashMap<String, String> data = new HashMap<>();
            data.put("Имя", resultSet.getString("Имя"));
            data.put("Фамилия", resultSet.getString("Фамилия"));
            data.put("Группа", resultSet.getString("Группа"));
            data.put("Дата", resultSet.getString("Дата создания"));
            students.add(data);
        }
        return students;
    }

    public static List<Pair<String, String>> getStudentsByGroup(String group) throws SQLException {
        setDbConnector(DBConnector.getInstance());
        if (!checkGroupInDB(group)) {
            throw new SQLException("Этой группы не существует в базе");
        } else {
            List<Pair<String, String>> students = new ArrayList<>();
            int groupID = getGroupID(group);
            query = "SELECT `Студенты`.`Имя` AS `Имя`, " +
                    "`Студенты`.`Фамилия` AS `Фамилия` " +
                    "FROM `Студенты` " +
                    "WHERE idГруппы = " + groupID + ";";
            resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                Pair<String, String> data = new Pair<>(
                        resultSet.getString("Имя"),
                        resultSet.getString("Фамилия"));
                students.add(data);
            }
            return students;
        }
    }

    public static List<Integer> getNetworksID(int taskID) throws SQLException {

        query = "SELECT idСети FROM сети " +
                "WHERE `idзадания` = \'" + taskID + "\';";

        resultSet = statement.executeQuery(query);
        List<Integer> id = new ArrayList<>();
        while (resultSet.next()) {
            id.add(resultSet.getInt(1));
        }

        if (id.isEmpty()) {
            return null;
        } else {
            return id;
        }
    }

    private static List<Integer> getTasksID(String name, String surname, String group) throws SQLException {
        int ID = getStudentID(name, surname, group);
        query = "SELECT idзадания " +
                "FROM задания " +
                "WHERE `idстудента` = \'" + ID + "\';";

        resultSet = statement.executeQuery(query);
        List<Integer> id = new ArrayList<>();
        while (resultSet.next()) {
            id.add(resultSet.getInt(1));
        }

        if (id.isEmpty()) {
            return null;
        } else {
            return id;
        }
    }

    private static Network getNetworkByID(int networkID) throws SQLException {
        query = "SELECT `Тип сети`, `Первый октет`,`Второй октет`, `Третий октет`, `Четвертый октет`, `Маска`" +
                "FROM сети " +
                "WHERE `idСети` = \'" + networkID + "\';";

        resultSet = statement.executeQuery(query);
        if (resultSet.next()) {
            Network network = new Network();
            if (resultSet.getString(1).equals("WAN")) {
                network.setType(NetworkType.WAN);
            } else {
                network.setType(NetworkType.LAN);
            }
            try {
                network.setIp(new IP(resultSet.getInt(2),
                        resultSet.getInt(3),
                        resultSet.getInt(4),
                        resultSet.getInt(5),
                        resultSet.getInt(6)));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return network;
        } else {
            throw new SQLException("Сети с ID = " + networkID + " нет в базе");
        }
    }

    private static List<Node> getNodesByNetworkID(int NetworkID) throws SQLException {
        query = "SELECT `НомерУзла`,`X`,`Y` " +
                "FROM узлы " +
                "WHERE `idСети` = \'" + NetworkID + "\';";
        resultSet = statement.executeQuery(query);
        List<Node> nodes = new ArrayList<>();
        while (resultSet.next()) {
            nodes.add(new Node(
                    resultSet.getInt(2),
                    resultSet.getInt(3),
                    resultSet.getInt(1)));
        }
        if (nodes.isEmpty()) {
            return null;
        } else {
            return nodes;
        }
    }

    private static List<NodeConnection> getConnectionsByNetworksID(List<Integer> idNetworks) throws Exception {
        if (idNetworks.size() == 0) {
            throw new Exception("Нужен хотя бы один ID сети");
        }

        List<NodeConnection> nodeConnections = new ArrayList<>();

        for (int idNetwork : idNetworks) {
            query = "SELECT `idузла`, `idсети`, `idсоединенного узла`,`idсети соединенного узла` " +
                    "FROM  соединения " +
                    "WHERE `idСети` = \'" + idNetwork + "\' OR `idсоединенного узла` = \'" + idNetwork + "\';";
            resultSet = statement.executeQuery(query);
            while(resultSet.next()) {
                nodeConnections.add(
                        new NodeConnection(
                                resultSet.getInt(1),
                                resultSet.getInt(2),
                                resultSet.getInt(3),
                                resultSet.getInt(4)));
            }
        }
        if (nodeConnections.isEmpty()) {
            return null;
        } else {
            return nodeConnections;
        }
    }

    private static GregorianCalendar getCreationDate(int taskID) throws SQLException, ParseException {
        query = "SELECT `дата создания`" +
                "FROM задания " +
                "WHERE `idзадания` = \'" + taskID + "\';";

        resultSet = statement.executeQuery(query);
        if (resultSet.next()) {
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.fromString(resultSet.getString(1), new SimpleDateFormat("yyyy-MM-dd"));
            return calendar;
        } else {
            return null;
        }
    }

    public static List<StudentTask> getStudentTasks(String group, String name, String surname) throws SQLException {
        setDbConnector(DBConnector.getInstance());
        if (!checkGroupInDB(group)) {
            throw new SQLException("Группа " + group + " не существует в базе");
        }
        if (!checkStudentInDB(name, surname, group)) {
            throw new SQLException("Студент " + surname + " " + name + " не существует в базе");
        }
        final List<Integer> tasksID = getTasksID(name, surname, group);
        List<StudentTask> studentTasks = new ArrayList<>();
        if (tasksID == null) {
            throw new SQLException("Для этого студента нет заданий");
        }
        for (int taskID : tasksID) {
            final List<Integer> networksID = getNetworksID(taskID);
            if (networksID == null) {
                //drop task
                continue;
            }

            Topology topology = new Topology();
            List<Network> networks = new ArrayList<>();

            for (int networkID : networksID) {
                Network network = getNetworkByID(networkID);
                network.setID(networkID);
                network.setNodes(getNodesByNetworkID(networkID));
                networks.add(network);
            }
            final List<NodeConnection> nodeConnections;
            try {
                nodeConnections = getConnectionsByNetworksID(networksID);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }

            if (nodeConnections == null) {
                continue;
            }
            topology.setNetworks(networks);
            for (NodeConnection connection : nodeConnections) {
                try {
                    if (connection.idConnectedNetwork == connection.idNetwork) {
                        Network network = topology.getNetworkByID(connection.idNetwork);
                        Network c_network = topology.getNetworkByID(connection.idConnectedNetwork);
                        network.getNodeByID(connection.idNode).connectNode(
                                c_network.getNodeByID(connection.idConnectedNode),
                                false);
                    } else {
                        Network network = topology.getNetworkByID(connection.idNetwork);
                        Network c_network = topology.getNetworkByID(connection.idConnectedNetwork);
                        network.connectNetworks(
                                network.getNodeByID(connection.idNode),
                                c_network,
                                c_network.getNodeByID(connection.idConnectedNode)
                        );
                    }

                } catch (OneselfConnection | NodeRelationsException ignored) {
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }



            StudentTask task = new StudentTask();
            task.setTopology(topology);
            task.setName(name);
            task.setSurname(surname);
            task.setGroup(group);
            try {
                task.setCreationDate(getCreationDate(taskID));
            } catch (ParseException e) {
                task.setCreationDate(null);
                e.printStackTrace();
            }
            studentTasks.add(task);
        }
        return studentTasks;
    }
}
