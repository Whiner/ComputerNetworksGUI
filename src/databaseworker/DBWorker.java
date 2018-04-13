package databaseworker;

import nodegenerator.Topology;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static databaseworker.DBConstants.*;


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
                return resultSet.next();
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

    public boolean checkStudentInDB(String group, String name, String surname)  {
        if(!checkGroupInDB(group)){
            return false;
        }
        try{
            query = "SELECT Имя, Фамилия FROM студенты " +
                    "WHERE idГруппы = " + getGroupID(group) + ";";
            resultSet = dbConnector.getConnection().createStatement().executeQuery(query);
            while(resultSet.next()){
                if(resultSet.getNString(1).equals(name) && resultSet.getNString(2).equals(surname)){
                    return true;
                }
            }
            return false;
        } catch(SQLException e){
            return false;
        }
    }

    private void addGroup(String group){
        if(!checkGroupInDB(group)){
            query = "INSERT INTO группы(Название) VALUES (\'" + group + "\');";
            try {
                statement.execute(query);
            } catch (SQLException e) {}
        }
    }

    private void addStudent(String name, String surname, String group, int taskID) throws SQLException {
        addGroup(group);
        query = "INSERT INTO студенты(Имя, Фамилия, idГруппы, idЗадания) " +
                "VALUES (" + name + "," + surname + "," + getGroupID(group) + "," + taskID + ");";
        statement.execute(query);
    }

    private int addTask(Topology topology) {
        int taskID = 0;
        //перенести все узлы
        //заполнить запись с заданием

        return taskID;
    }

    public boolean toDB(StudentTask studentTask, boolean rewrite) throws NullPointerException {
        if(studentTask == null){
            throw new NullPointerException();
        }

        try{
            statement = dbConnector.getConnection().createStatement();
            if(!checkGroupInDB(studentTask.getGroup())){
                query = "INSERT INTO группы(Название) VALUES (\'" + studentTask.getGroup() + "\');";
                statement.execute(query);
            }
            if(checkStudentInDB(studentTask.getGroup(), studentTask.getName(), studentTask.getSurname())){
                if(rewrite){
                    query = "SELECT idзадания FROM студенты " +
                            "WHERE (Имя = " + studentTask.getName() + ") AND (Фамилия = " + studentTask.getSurname() + ");";
                    resultSet = statement.executeQuery(query);
                    int id = resultSet.getInt(1);

                    query = "DELETE * FROM задания " +
                            "WHERE idзадания = " + id + ";";
                    statement.execute(query);

                    //записать новое
                } else {
                    return false;
                }
            } else {

            }

        } catch(Exception e){
            e.printStackTrace();
        }


        return true;
    }
}
