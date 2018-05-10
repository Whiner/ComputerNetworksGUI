package org.donntu.databaseworker;

import javafx.util.Pair;
import org.donntu.generator.*;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


public class DBWorker {
    private static DBConnector dbConnector;
    private static Statement statement;
    private static String query;
    private static ResultSet resultSet;


    public static void setDbConnector(DBConnector dbConnector) throws SQLException {
        if(dbConnector == null){
            throw new NullPointerException();
        }
        DBWorker.dbConnector = dbConnector;
        if(!dbConnector.connectToDB()){
            throw new SQLException("Соединение не установлено");
        }
        statement = dbConnector.getConnection().createStatement();
    }

    private static boolean checkGroupInDB(String group) {
        if(group != null && !group.isEmpty()){
            try {
                query = "SELECT Название FROM группы " +
                        "WHERE (`Название` = \'" + group + "\');";
                resultSet = statement.executeQuery(query);
                return resultSet.next() ;
            } catch(SQLException e){
                return false;
            }
        }

        return false;
    }

    private static Integer getGroupID(String group){
        if(group != null && !group.isEmpty()){
            try {
                query = "SELECT idГруппы,Название FROM группы " +
                        "WHERE (`Название` = \'" + group + "\');";
                resultSet = statement.executeQuery(query);
                if (resultSet.next()){
                    return resultSet.getInt(1);
                }
                else {
                    return null;
                }
            } catch(SQLException e){
                return null;
            }
        }
        return null;
    }

    private static Integer getStudentID(String name, String surname, String group){
        if(name != null && !name.isEmpty() && surname != null && !surname.isEmpty()){
            try {
                query = "SELECT `idстудента` FROM студенты " +
                        "WHERE `Имя` = \'" + name + "\' AND `Фамилия` = \'" + surname + "\' AND `idГруппы` = \'" + getGroupID(group) + "\';";
                resultSet = statement.executeQuery(query);
                if (resultSet.next()){
                    return resultSet.getInt(1);
                }
                else {
                    return null;
                }
            } catch(SQLException e){
                return null;
            }
        }
        return null;
    }

    private static Integer getLastTaskIDbyStudent(String name, String surname, String group) {

        int ID = getStudentID(name, surname, group);
        query = "SELECT idзадания FROM задания " +
                "WHERE `idстудента` = \'" + ID + "\';";
        try {
            resultSet = statement.executeQuery(query);
            if (resultSet.last()) {
                return resultSet.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static boolean checkStudentInDB(String name, String surname, String group)  {
        try{
            query = "SELECT Имя, Фамилия FROM студенты " +
                    "WHERE `Имя` = \'" + name + "\' AND `Фамилия` = \'" + surname + "\' AND `idГруппы` = \'" + getGroupID(group) + "\';";
            resultSet = statement.executeQuery(query);
            return resultSet.next();
        } catch(SQLException e){
            return false;
        }
    }

    private static int addGroup(String group){
        if(!checkGroupInDB(group)){
            query = "INSERT INTO группы(Название) VALUES (\'" + group + "\');";
            try {
                statement.execute(query);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return getGroupID(group);
    }

    private static int addStudent(String name, String surname, String group) throws SQLException {
        if(!checkGroupInDB(group)){
            addGroup(group);
        }
        if(!checkStudentInDB(name, surname, group)) {
            query = "INSERT INTO студенты(Имя, Фамилия, idГруппы) " +
                    "VALUES (\'" + name + "\',\'" + surname + "\',\'" + getGroupID(group) + "\');";
            statement.execute(query);
        }
        return getStudentID(name, surname, group);
    }

    private static Integer addTaskForStudent(String name, String surname, String group) throws SQLException {

        Integer idStudent = getStudentID(name, surname, group);
        if (idStudent == null) {
            idStudent = addStudent(name, surname, group);
        }
        query = "INSERT INTO задания(idстудента, `Дата создания`) " +
                "VALUE (\'" + idStudent + "\',\'" + new Date(System.currentTimeMillis()).toString() + "\');";
        statement.execute(query);
        resultSet = statement.executeQuery("SELECT last_insert_id() AS \'last_id\' FROM задания");
        resultSet.next();
        return resultSet.getInt("last_id");

    }

    private static int addNetworksConnection(int idNetworkFrom, int idNetworkTo, int idNodeFrom, int idNodeTo) throws SQLException {
        query = "INSERT INTO соединения(`idсети`, `idузла`,`idсети соединенного узла`,`idсоединенного узла`)\n" +
                "VALUES ('" + idNetworkFrom  + "','" + idNodeFrom + "','" + idNetworkTo
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
        if(addNodes){
            final List<Node> nodes = network.getNodes();
            for (Node node: nodes){
                addNode(lastNetworkID, node);
            }
            final List<Pair<Node, Node>> connections = network.getUniqueConnections();
            for (Pair<Node, Node> pair: connections){
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

    public static boolean addStudentTask(StudentTask studentTask) throws NullPointerException {
        if(studentTask == null){
            throw new NullPointerException();
        }
        try{
            if(statement == null || statement.isClosed()) {
                statement = dbConnector.getConnection().createStatement();
            }
            addGroup(studentTask.getGroup());
            addStudent(studentTask.getName(), studentTask.getSurname(), studentTask.getGroup());
            int idTask = addTaskForStudent(studentTask.getName(), studentTask.getSurname(), studentTask.getGroup());

            Topology topology = studentTask.getTopology();
            int idWAN = addNetwork(
                    idTask,
                    topology.getWAN(), true);


            List<Pair<Integer, Network>> idLAN = new ArrayList<>();
            final List<Network> laNs = topology.getLANs();
            for (Network lan: laNs){
                idLAN.add(new Pair<>(addNetwork(idTask, lan, true), lan));
            }

            final List<NetworksConnection> networksConnections = topology.getUniqueNetworksConnections();
            for (NetworksConnection networksConnection: networksConnections){
                if(networksConnection.getFromNetwork().getType() != NetworkType.WAN){
                    if(networksConnection.getToNetwork().getType() != NetworkType.WAN){
                        continue;
                    }
                    networksConnection = networksConnection.getInvertedConnection();
                }

                int index = 0;
                for (Pair<Integer, Network> pair: idLAN){
                    if(pair.getValue().equals(networksConnection.getToNetwork())){
                        break;
                    }
                    index++;
                }
                if(index == idLAN.size()){
                    continue;
                }
                addNetworksConnection(idWAN,
                        idLAN.get(index).getKey(),
                        networksConnection.getFromNetworkNode().getID(),
                        networksConnection.getToNetworkNode().getID());
            }
        } catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }


    public static List<String> getGroups() throws SQLException {
        List<String> groups = new ArrayList<>();
        query = "SELECT `Название` FROM `Группы`";
        resultSet = statement.executeQuery(query);
        while(resultSet.next()){
            groups.add(resultSet.getString(1));
        }
        return groups;
    }

}
