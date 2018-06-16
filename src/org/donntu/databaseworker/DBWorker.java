package org.donntu.databaseworker;

import javafx.util.Pair;
import org.donntu.GregorianCalendar;
import org.donntu.generator.*;
import org.donntu.generator.generatorException.NodeRelationsException;
import org.donntu.generator.generatorException.OneselfConnection;

import java.io.FileNotFoundException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class DBWorker {
    enum CurrentStatement{NETWORK, NODE, TASK, CONNECTION, NONE};
    private static DBConnector dbConnector;
    private static Statement statement;
    private static String query;
    private static ResultSet resultSet;
    private static PreparedStatement preparedStatement;
    private static CurrentStatement currentStatement = CurrentStatement.NONE;

    private DBWorker() {
    }

    public static void setDBConnector(DBConnector dbConnector) throws SQLException {
        if (dbConnector == null) {
            throw new SQLException("Необходимо установить коннектор");
        }
        if (DBWorker.dbConnector != dbConnector) {
            DBWorker.dbConnector = dbConnector;
            if (!dbConnector.isOpen()) {
                try {
                    if (!dbConnector.connectToDB()) {
                        throw new SQLException("Соединение не установлено");
                    }
                } catch (FileNotFoundException e) {
                    throw new SQLException("Файл с параметрами базы не найден");
                } catch (Exception e){
                    throw new SQLException("Соединение не установлено");
                }
            }
            statement = dbConnector.getConnection().createStatement();

        }
    }

    private static boolean checkGroupInDB(String group) {
        if (group != null && !group.isEmpty()) {
            try {
                query = "SELECT `name` FROM `groups` " +
                        "WHERE (`name` = \'" + group + "\');";
                resultSet = statement.executeQuery(query);
                return resultSet.next();
            } catch (SQLException e) {
                return false;
            }
        }

        return false;
    }

    private static Long getGroupID(String group) {
        if (group != null && !group.isEmpty()) {
            try {
                query = "SELECT `idgroup`, `name` FROM `groups` " +
                        "WHERE (`name` = \'" + group + "\');";
                resultSet = statement.executeQuery(query);
                if (resultSet.next()) {
                    return resultSet.getLong(1);
                } else {
                    return null;
                }
            } catch (SQLException e) {
                return null;
            }
        }
        return null;
    }

    private static Long getStudentID(String name, String surname, String group) {
        if (name != null && !name.isEmpty() && surname != null && !surname.isEmpty()) {
            try {
                query = "SELECT `idstudent` FROM `students` " +
                        "WHERE `name` = \'" + name + "\' AND `surname` = \'" + surname + "\' AND `idgroup` = \'" + getGroupID(group) + "\';";
                resultSet = statement.executeQuery(query);
                if (resultSet.next()) {
                    return resultSet.getLong(1);
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
            query = "SELECT `name`, `surname` FROM `students` " +
                    "WHERE `name` = \'" + name + "\' " +
                    "AND `surname` = \'" + surname + "\' " +
                    "AND `idgroup` = \'" + getGroupID(group) + "\';";
            resultSet = statement.executeQuery(query);
            return resultSet.next();
        } catch (SQLException e) {
            return false;
        }
    }

    public static boolean addGroup(String group) throws SQLException {
        setDBConnector(dbConnector);
        if (!checkGroupInDB(group)) {
            query = "INSERT INTO `networksdb`.`groups` (`name`) VALUES (\'" + group + "\');";
            statement.execute(query);
        } else {
            return false;
        }
        return true;
    }

    public static boolean addStudent(String name, String surname, String group) throws SQLException {
        setDBConnector(dbConnector);
        if (!checkGroupInDB(group)) {
            addGroup(group);
        }
        if (!checkStudentInDB(name, surname, group)) {
            query = "INSERT INTO `students`(`name`, `surname`, `idgroup`) " +
                    "VALUES (\'" + name + "\',\'" + surname + "\',\'" + getGroupID(group) + "\');";
            statement.execute(query);
        } else {
            return false;
        }
        return true;
    }

    private static Long addTaskForStudent(String name, String surname, String group, GregorianCalendar date, int cells_x, int cells_y) throws SQLException {

        Long idStudent = getStudentID(name, surname, group);
        if (idStudent == null) {
            addStudent(name, surname, group);
            idStudent = getStudentID(name, surname, group);
        }


        query = "INSERT INTO `tasks`(`idstudent`, `creation_date`, `cells_x`, `cells_y`) " +
                "VALUES (\'" + idStudent + "\',\'" + new Date(date.getTimeInMillis()) + "\', \'" +  cells_x + "\', \'" + cells_y + "\');";
        statement.execute(query);
        resultSet = statement.executeQuery("SELECT MAX(`idtask`) AS last_id FROM `tasks`");
        resultSet.next();
        return resultSet.getLong("last_id");

    }

    private static long addNetworksConnection(long idNetworkFrom, long idNetworkTo, long idNodeFrom, long idNodeTo) throws SQLException {
        query = "INSERT INTO `connections`(`idnetwork`, `idnode`,`id_connected_network`,`id_connected_node`) " +
                "VALUES ('" + idNetworkFrom + "','" + idNodeFrom + "','" + idNetworkTo
                + "','" + idNodeTo + "');";
        statement.execute(query);
        resultSet = statement.executeQuery("SELECT MAX(`idconnection`) AS last_id FROM `connections`");
        resultSet.next();
        return resultSet.getLong("last_id");
    }

    private static long addNetwork(long idTask, Network network, boolean addNodes) throws SQLException {

        final IP ip = network.getIp();
        query = "INSERT INTO `networks`(`type`, `first`,`second`,`third`,`fourth`, `mask`, idtask) " +
                "VALUES (\'" + network.getType() + "\',\'" + ip.getFirst()
                + "\',\'" + ip.getSecond()
                + "\',\'" + ip.getThird()
                + "\',\'" + ip.getFourth()
                + "\',\'" + ip.getMask()
                + "\',\'" + idTask + "\');";
        statement.execute(query);
        resultSet = statement.executeQuery("SELECT MAX(`idnetwork`) AS last_id FROM `networks`");
        resultSet.next();
        final long lastNetworkID = resultSet.getLong("last_id");

        if (addNodes) {
            final List<Node> nodes = network.getNodes();
            for (Node node : nodes) {
                addNode(lastNetworkID, node);
            }
            preparedStatement.executeBatch();
            final List<Pair<Node, Node>> connections = network.getUniqueConnections();
            for (Pair<Node, Node> pair : connections) {
                addNodeConnection(lastNetworkID, pair.getKey().getId(), lastNetworkID, pair.getValue().getId());
            }
            preparedStatement.executeBatch();
        }
        return lastNetworkID;
    }

    private static void addNodeConnection(long idNetworkFrom, long idFrom, long idNetworkTo, long idTo) throws SQLException {
        if(currentStatement != CurrentStatement.CONNECTION){
            query = "INSERT INTO `connections`(`idnode`,`id_connected_node`,`idnetwork`, `id_connected_network`) " +
                    "VALUES (?,?,?,?)";
            preparedStatement = dbConnector.getConnection().prepareStatement(query);
            currentStatement = CurrentStatement.CONNECTION;
        }

        preparedStatement.setLong(1, idFrom);
        preparedStatement.setLong(2, idTo);
        preparedStatement.setLong(3, idNetworkFrom);
        preparedStatement.setLong(4, idNetworkTo);
        preparedStatement.addBatch();

    }

    private static void addNode(long idNetwork, Node node) throws SQLException {
        if(currentStatement != CurrentStatement.NODE){
            query = "INSERT INTO `nodes`(`idnetwork`,`nodeindex`,`X`,`Y`) " +
            "VALUES (?,?,?,?)";
            preparedStatement = dbConnector.getConnection().prepareStatement(query);
            currentStatement = CurrentStatement.NODE;
        }

        preparedStatement.setLong(1, idNetwork);
        preparedStatement.setInt(2, node.getId());
        preparedStatement.setInt(3, node.getCellNumberX());
        preparedStatement.setInt(4, node.getCellNumberY());
        preparedStatement.addBatch();

    }

    public static boolean addStudentTask(StudentTask studentTask) throws NullPointerException, SQLException {
        setDBConnector(dbConnector);
        dbConnector.getConnection().setAutoCommit(false);
        if (studentTask == null) {
            throw new NullPointerException();
        }
        try {
            addGroup(studentTask.getGroup());
            addStudent(studentTask.getName(), studentTask.getSurname(), studentTask.getGroup());
            long idTask = addTaskForStudent(
                    studentTask.getName(),
                    studentTask.getSurname(),
                    studentTask.getGroup(),
                    studentTask.getCreationDate(),
                    studentTask.getCells_x(),
                    studentTask.getCells_y());
            Topology topology = studentTask.getTopology();
            long idWAN = addNetwork(
                    idTask,
                    topology.getWAN(), true);
            List<Pair<Long, Network>> idLAN = new ArrayList<>();
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
                for (Pair<Long, Network> pair : idLAN) {
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
                        networksConnection.getFromNetworkNode().getId(),
                        networksConnection.getToNetworkNode().getId());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        dbConnector.getConnection().commit();
        return true;
    }

    public static List<String> getGroups() throws SQLException {
        setDBConnector(dbConnector);
        List<String> groups = new ArrayList<>();
        query = "SELECT `name` FROM `groups`";
        resultSet = statement.executeQuery(query);
        while (resultSet.next()) {
            groups.add(resultSet.getString(1));
        }
        return groups;
    }

    public static List<HashMap<String, String>> getStudentsTaskList() throws SQLException {
        setDBConnector(dbConnector);
        List<HashMap<String, String>> students = new ArrayList<>();

        query = "SELECT `students`.`name` AS `name`, " +
                "`students`.`surname` AS `surname`, " +
                "`groups`.`name` AS `group`, " +
                "`tasks`.`creation_date` AS `creation_date`, " +
                "`tasks`.`idtask` AS `key` " +
                "FROM `students`, `groups`, `tasks`" +
                "WHERE `students`.`idgroup` = `groups`.`idgroup` " +
                "AND `tasks`.`idstudent` = `students`.`idstudent`";
        resultSet = statement.executeQuery(query);
        while (resultSet.next()) {
            HashMap<String, String> data = new HashMap<>();
            data.put("name", resultSet.getString("name"));
            data.put("surname", resultSet.getString("surname"));
            data.put("group", resultSet.getString("group"));
            data.put("creation_date", resultSet.getString("creation_date"));
            data.put("key", resultSet.getString("key"));
            students.add(data);
        }
        return students;
    }

    public static List<Student> getStudentsByGroup(String group) throws SQLException {
        setDBConnector(dbConnector);
        if (!checkGroupInDB(group)) {
            throw new SQLException("Этой группы не существует в базе");
        } else {
            List<Student> students = new ArrayList<>();
            long groupID = getGroupID(group);
            query = "SELECT `students`.`name` AS `name`, " +
                    "`students`.`surname` AS `surname` " +
                    "FROM `students` " +
                    "WHERE idgroup = " + groupID + ";";
            resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                students.add(new Student(
                        resultSet.getString("name"),
                        resultSet.getString("surname"),
                        group));
            }
            return students;
        }
    }

    public static List<Long> getNetworksID(long taskID) throws SQLException {
        setDBConnector(dbConnector);
        query = "SELECT `idnetwork` FROM networks " +
                "WHERE `idtask` = \'" + taskID + "\';";

        resultSet = statement.executeQuery(query);
        List<Long> id = new ArrayList<>();
        while (resultSet.next()) {
            id.add(resultSet.getLong(1));
        }

        if (id.isEmpty()) {
            return null;
        } else {
            return id;
        }
    }

    private static List<Long> getTasksID(String name, String surname, String group) throws SQLException {
        long ID = getStudentID(name, surname, group);
        query = "SELECT `idtask` " +
                "FROM `tasks` " +
                "WHERE `idstudent` = \'" + ID + "\';";

        resultSet = statement.executeQuery(query);
        List<Long> id = new ArrayList<>();
        while (resultSet.next()) {
            id.add(resultSet.getLong(1));
        }

        if (id.isEmpty()) {
            return null;
        } else {
            return id;
        }
    }

    private static Network getNetworkByID(Long networkID) throws SQLException {
        query = "SELECT `type`, `first`,`second`, `third`, `fourth`, `mask`" +
                "FROM `networks` " +
                "WHERE `idnetwork` = \'" + networkID + "\';";

        resultSet = statement.executeQuery(query);
        if (resultSet.next()) {
            Network network = new Network();
            if (resultSet.getString(1).equals("WAN")) {
                network.setType(NetworkType.WAN);
            } else {
                network.setType(NetworkType.LAN);
            }
            try {
                network.setIp(new IP(
                        resultSet.getInt(2),
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

    private static List<Node> getNodesByNetworkID(long NetworkID) throws SQLException {
        query = "SELECT `nodeindex`,`X`,`Y` " +
                "FROM `nodes` " +
                "WHERE `idnetwork` = \'" + NetworkID + "\';";
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

    private static List<NodeConnection> getConnectionsByNetworksID(List<Long> idNetworks) throws SQLException {
        if (idNetworks.size() == 0) {
            throw new SQLException("Нужен хотя бы один ID сети");
        }

        List<NodeConnection> nodeConnections = new ArrayList<>();

        for (long idNetwork : idNetworks) {
            query = "SELECT `idnode`, `idnetwork`, `id_connected_node`,`id_connected_network` " +
                    "FROM  `connections` " +
                    "WHERE `idnetwork` = \'" + idNetwork + "\' OR `id_connected_node` = \'" + idNetwork + "\';";
            resultSet = statement.executeQuery(query);
            while(resultSet.next()) {
                nodeConnections.add(
                        new NodeConnection(
                                resultSet.getInt(1),
                                resultSet.getLong(2),
                                resultSet.getInt(3),
                                resultSet.getLong(4)));
            }
        }
        if (nodeConnections.isEmpty()) {
            return null;
        } else {
            return nodeConnections;
        }
    }

    private static GregorianCalendar getCreationDate(long taskID) throws SQLException, ParseException {
        query = "SELECT `creation_date`" +
                "FROM `tasks` " +
                "WHERE `idtask` = \'" + taskID + "\';";

        resultSet = statement.executeQuery(query);
        if (resultSet.next()) {
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.fromString(resultSet.getString(1), new SimpleDateFormat("yyyy-MM-dd"));
            return calendar;
        } else {
            return null;
        }
    }

    private static Student getInitialsByTaskID(long taskID) throws SQLException {
        query = "SELECT `students`.`name`, `students`.`surname`, `groups`.`name` " +
                "FROM `tasks`, `students`, `groups` " +
                "WHERE `idtask` = \'" + taskID + "\' AND " +
                "`students`.`idstudent` = `tasks`.`idstudent` AND " +
                "`groups`.`idgroup` = `students`.`idgroup`;";
        resultSet = statement.executeQuery(query);
        if(resultSet.next()) {
            return new Student(resultSet.getString(1),
                    resultSet.getString(2),
                    resultSet.getString(3)
                    );
        } else {
            return null;
        }
    }

    public static List<StudentTask> getStudentTasks(String group, String name, String surname) throws SQLException {
        setDBConnector(dbConnector);
        if (!checkGroupInDB(group)) {
            throw new SQLException("Группа " + group + " не существует в базе");
        }
        if (!checkStudentInDB(name, surname, group)) {
            throw new SQLException("Студент " + surname + " " + name + " не существует в базе");
        }
        final List<Long> tasksID = getTasksID(name, surname, group);
        List<StudentTask> studentTasks = new ArrayList<>();
        if (tasksID == null) {
            throw new SQLException("Для этого студента нет заданий");
        }
        for (Long taskID : tasksID) {
            try {
                studentTasks.add(getTaskByID(taskID));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return studentTasks;
    }

    private static int getCellsXByTaskID(long taskID) throws SQLException {
        query = "SELECT `cells_x` " +
                "FROM `tasks` " +
                "WHERE `idtask` = \'" + taskID + "\';";

        resultSet = statement.executeQuery(query);
        if(resultSet.next()){
            return resultSet.getInt(1);
        } else {
            return 0;
        }
    }

    private static int getCellsYByTaskID(long taskID) throws SQLException {
        query = "SELECT `cells_y` " +
                "FROM `tasks` " +
                "WHERE `idtask` = \'" + taskID + "\';";

        resultSet = statement.executeQuery(query);
        if(resultSet.next()){
            return resultSet.getInt(1);
        } else {
            return 0;
        }
    }

    public static StudentTask getTaskByID(long taskID) throws SQLException {
        setDBConnector(dbConnector);
        final List<Long> networksID = getNetworksID(taskID);
        if (networksID == null) {
            throw new SQLException("Для этого задания нет сетей");
        }

        Topology topology = new Topology();
        List<Network> networks = new ArrayList<>();

        for (Long networkID : networksID) {
            Network network = getNetworkByID(networkID);
            network.setID(networkID);
            network.setNodes(getNodesByNetworkID(networkID));
            networks.add(network);
        }
        final List<NodeConnection> nodeConnections;
        nodeConnections = getConnectionsByNetworksID(networksID);
        if (nodeConnections == null) {
            throw new SQLException("Нет соединений в базе данных");
        }
        topology.setNetworks(networks);

        for (NodeConnection connection : nodeConnections) {
            try {
                if (connection.idConnectedNetwork == connection.idNetwork) {
                    Network network = topology.getNetworkByID(connection.idNetwork);
                    Network c_network = topology.getNetworkByID(connection.idConnectedNetwork);

                    Node nodeFrom = network.getNodeByID(connection.idNode);
                    Node nodeTo = c_network.getNodeByID(connection.idConnectedNode);

                    nodeFrom.connectNode(nodeTo, false);

                } else {
                    Network network = topology.getNetworkByID(connection.idNetwork);
                    Network c_network = topology.getNetworkByID(connection.idConnectedNetwork);

                    Node nodefrom = network.getNodeByID(connection.idNode);
                    Node nodeto = c_network.getNodeByID(connection.idConnectedNode);

                    network.connectNetworks(nodefrom, c_network, nodeto);
                }

            } catch (OneselfConnection | NodeRelationsException e) {
                throw new SQLException("Задание повреждено либо было сгенерировано с ошибкой");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        StudentTask task = new StudentTask();
        task.setTopology(topology);

        Student initials = getInitialsByTaskID(taskID);
        if(initials == null){
            return null;
        }
        task.setStudent(initials);

        try {
            task.setCreationDate(getCreationDate(taskID));
            task.setCells_x(getCellsXByTaskID(taskID));
            task.setCells_y(getCellsYByTaskID(taskID));
        } catch (ParseException e) {
            task.setCreationDate(null);
            e.printStackTrace();
        }
        return task;
    }

    public static void deleteTaskByID(int taskID) throws SQLException {
        setDBConnector(dbConnector);
        query = "DELETE " +
                "FROM `tasks` " +
                "WHERE `idtask` = \'" + taskID + "\';";
        statement.executeUpdate(query);
    }

    public static void deleteStudent(String name, String surname, String group) throws SQLException {
        setDBConnector(dbConnector);
        if(checkStudentInDB(name, surname, group)){
            query = "DELETE " +
                    "FROM `students` " +
                    "WHERE `name` = \'" + name + "\' " +
                    "AND `surname` = \'" + surname + "\' " +
                    "AND `idgroup` = \'" + getGroupID(group) + "\';";
            statement.executeUpdate(query);
        } else {
            throw new SQLException("Студента " + surname + " " + name + " из " + group + " не существует в базе");
        }

    }

    public static List<StudentTask> getAllTasksByYears(int lowYear, int highYear) throws SQLException, Exception  {
        if(lowYear < 2015 || lowYear > 3000 || highYear < 2015 || highYear > 3000){
            throw new Exception("Некорректное значение года");
        }
        if(lowYear > highYear){
            throw new Exception("low должно быть меньше или равно high");
        }
        setDBConnector(dbConnector);
        query = "SELECT `idtask` " +
                "FROM `tasks` " +
                "WHERE YEAR(tasks.creation_date) >= " + lowYear + " AND YEAR(tasks.creation_date) <= " + highYear;

        resultSet = statement.executeQuery(query);
        List<StudentTask> tasks = new ArrayList<>();
        List<Integer> id = new ArrayList<>();
        while(resultSet.next()){
            id.add(resultSet.getInt(1));
        }
        for (Integer i: id){
            tasks.add(getTaskByID(i));
        }
        return tasks;
    }

    public static void deleteGroup(String group) throws SQLException {
        setDBConnector(dbConnector);
        if(checkGroupInDB(group)) {
            query = "DELETE " +
                    "FROM `groups` " +
                    "WHERE `name` = \'" + group + "\';";
            statement.executeUpdate(query);
        } else {
            throw new SQLException("Группы " + group + " не существует в базе");
        }
    }

    public static List<StudentTask> getTasksByGroup(String group) throws SQLException {
        setDBConnector(dbConnector);
        List<StudentTask> tasks = new ArrayList<>();
        final List<Student> students = getStudentsByGroup(group);
        for (Student student: students){
            final List<Long> tasksID = getTasksID(student.getName(), student.getSurname(), group);
            if(tasksID != null) {
                for (Long i : tasksID) {
                    tasks.add(getTaskByID(i));
                }
            }
        }
        return tasks;
    }

    public static void deleteTasksByStudent(Student student) throws SQLException {
        setDBConnector(dbConnector);
        long id = getStudentID(student.getName(), student.getSurname(), student.getGroup());

        query = "DELETE " +
                "FROM `tasks` " +
                "WHERE `idstudent` = " + id;
        statement.executeUpdate(query);
    }

    public static void deleteTasksByGroup(String group) throws SQLException {
        setDBConnector(dbConnector);
        final List<Student> students = getStudentsByGroup(group);

        for (Student student: students){
            deleteTasksByStudent(student);
        }

    }
}
