package org.donntu.databaseworker;

import javafx.util.Pair;
import org.donntu.generator.*;
import sun.nio.ch.Net;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;


public class DBWorker {
    private DBConnector dbConnector;
    private Statement statement;
    private String query;
    private ResultSet resultSet;

    public DBWorker(DBConnector dbConnector) throws SQLException {
        if(dbConnector == null){
            throw new NullPointerException();
        }
        this.dbConnector = dbConnector;
        if(!dbConnector.connectToDB()){
            throw new SQLException("Соединение не установлено");
        }
        statement = dbConnector.getConnection().createStatement();


    }

    public boolean checkGroupInDB(String group) {
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

    public Integer getGroupID(String group){
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

    public Integer getStudentID(String name, String surname, String group){
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

    public Integer getLastTaskIDbyStudent(String name, String surname, String group) {

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

    public boolean checkStudentInDB(String name, String surname, String group)  {

        try{
            query = "SELECT Имя, Фамилия FROM студенты " +
                    "WHERE `Имя` = \'" + name + "\' AND `Фамилия` = \'" + surname + "\' AND `idГруппы` = \'" + getGroupID(group) + "\';";
            resultSet = statement.executeQuery(query);
            return resultSet.next();
        } catch(SQLException e){
            return false;
        }
    }

    private int addGroup(String group){
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

    private int addStudent(String name, String surname, String group) throws SQLException {
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

    private Integer addTaskForStudent(String name, String surname, String group) throws SQLException {

        Integer idStudent = getStudentID(name, surname, group);
        if (idStudent == null) {
            idStudent = addStudent(name, surname, group);
        }
        query = "INSERT INTO задания(idстудента) " +
                "VALUE (\'" + idStudent + "\');";
        statement.execute(query);
        resultSet = statement.executeQuery("SELECT last_insert_id() AS \'last_id\' FROM задания");
        resultSet.next();
        return resultSet.getInt("last_id");

    }

    private int addNetwork(int idTask, Network network, boolean addNodes) throws SQLException {
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
        final int last_id = resultSet.getInt("last_id");
        if(addNodes){
            final List<Pair<Node, Node>> connections = network.getUniqueConnections();
            for (Pair<Node, Node> pair: connections){
                addNodeConnection(last_id, pair.getKey(), pair.getValue());
            }
        }
        return last_id;
    }

    private int addNodeConnection(int idNetwork, Node from, Node to) throws SQLException {
        query = "INSERT INTO `узлы`(`idСети`,`НомерУзла`,`Соединен с`,`X`,`Y`)" +
                "VALUES (\'" + idNetwork + "\', " +
                "\'" + from.getID() + "\', " +
                "\'" + to.getID() + " \', " +
                "\'" + from.getCellNumber_X() + " \'," +
                "\'" + from.getCellNumber_Y() + "\');";
        statement.execute(query);
        resultSet = statement.executeQuery("SELECT last_insert_id() AS last_id FROM `узлы`");
        resultSet.next();
        return resultSet.getInt("last_id");
    }

    public boolean addStudentTask(StudentTask studentTask) throws NullPointerException {
        if(studentTask == null){
            throw new NullPointerException();
        }
        try{
            if(statement == null || statement.isClosed()) {
                statement = dbConnector.getConnection().createStatement();
            }
            int idGroup = addGroup(studentTask.getGroup());
            int idStudent = addStudent(studentTask.getName(), studentTask.getSurname(), studentTask.getGroup());
            int idTask = addTaskForStudent(studentTask.getName(), studentTask.getSurname(), studentTask.getGroup());

            Topology topology = studentTask.getTopology();
            int idWAN = addNetwork(
                    idTask,
                    topology.getWAN(), true);
            //for (Network network:)

        } catch(Exception e){
            e.printStackTrace();
        }


        return true;
    }
}
