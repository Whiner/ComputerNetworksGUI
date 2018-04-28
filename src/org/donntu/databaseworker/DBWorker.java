package org.donntu.databaseworker;

import org.donntu.generator.Topology;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


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


    }

    public boolean checkGroupInDB(String group) {
        if(group != null && !group.isEmpty()){
            try {
                query = "SELECT Название FROM группы " +
                        "WHERE (Название = \'" + group + "\');";
                resultSet = dbConnector.getConnection().createStatement().executeQuery(query);
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
                        "WHERE (Название = \'" + group + "\');";
                resultSet = dbConnector.getConnection().createStatement().executeQuery(query);
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

    public Integer getStudentID(String name, String surname){
        if(name != null && !name.isEmpty() && surname != null && !surname.isEmpty()){
            try {
                query = "SELECT idСтуденты FROM студенты " +
                        "WHERE Имя = " + name + " AND Фамилия = "+ surname + ";";
                resultSet = dbConnector.getConnection().createStatement().executeQuery(query);
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

    public Integer getLastTaskIDbyStudent(String name, String surname) {
        if (name != null && !name.isEmpty() && surname != null && !surname.isEmpty()) {

            int ID = getStudentID(name, surname);
            query = "SELECT idстудента FROM задания " +
                    "WHERE idстудента = " + ID + ";";
            try {
                resultSet = dbConnector.getConnection().createStatement().executeQuery(query);
                while(resultSet.next()){
                    if(resultSet.last()) {
                        return resultSet.getInt(1);
                    }
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public boolean checkStudentInDB(String name, String surname)  {

        try{
            query = "SELECT Имя, Фамилия FROM студенты " +
                    "WHERE Имя = " + name + " AND Фамилия = "+ surname + ";";
            resultSet = dbConnector.getConnection().createStatement().executeQuery(query);
            return resultSet.next();
        } catch(SQLException e){
            return false;
        }
    }

    private void addGroup(String group){
        if(!checkGroupInDB(group)){
            query = "INSERT INTO группы(Название) VALUES (\'" + group + "\');";
            try {
                statement.execute(query);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void addStudent(String name, String surname, String group) throws SQLException {
        if(!checkGroupInDB(group)){
            addGroup(group);
        }
        if(!checkStudentInDB(name, surname)) {
            query = "INSERT INTO студенты(Имя, Фамилия, idГруппы) " +
                    "VALUES (" + name + "," + surname + "," + getGroupID(group) + ");";
            statement.execute(query);
        }
    }

    private void addTask(Topology topology, int idTask) {


        //перенести все узлы
        //заполнить запись с заданием


    }

    public boolean addStudentTask(StudentTask studentTask) throws NullPointerException {
        if(studentTask == null){
            throw new NullPointerException();
        }

        try{
            statement = dbConnector.getConnection().createStatement();
            if(!checkGroupInDB(studentTask.getGroup())){
                addGroup(studentTask.getGroup());
            }
            if(!checkStudentInDB(studentTask.getName(), studentTask.getSurname())){
                addStudent(studentTask.getName(),  studentTask.getSurname(), studentTask.getGroup());
            }

            query = "INSERT INTO задания(idстудента) " +
                    "VALUE (" + getStudentID(studentTask.getName(), studentTask.getSurname()) + ");";
            statement.execute(query);
            Topology topology = studentTask.getTopology();

            query = "INSERT INTO сети(`Тип сети`, `Первый октет`,`Второй октет`,`Третий октет`,`Четвертый октет`,`Маска`, idЗадания)" +
                    "VALUES (" + topology.getWAN().getType() + "," + topology.getWAN().getIp().getFirst()
                    + "," + topology.getWAN().getIp().getSecond()
                    + "," + topology.getWAN().getIp().getThird()
                    + "," + topology.getWAN().getIp().getFourth()
                    + "," + topology.getWAN().getIp().getMask()
                    + "," + getLastTaskIDbyStudent(studentTask.getName(), studentTask.getSurname()) + ");";

        } catch(Exception e){
            e.printStackTrace();
        }


        return true;
    }
}
