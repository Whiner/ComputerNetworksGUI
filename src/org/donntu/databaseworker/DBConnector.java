package org.donntu.databaseworker;

import java.sql.*;

public class DBConnector {
    private Connection connection;
    private String url;
    private String login;
    private String password;


    public Connection getConnection() {
        return connection;
    }

    public boolean isOpen() throws SQLException {
        if(connection != null){
            return !connection.isClosed();
        } else {
            return false;
        }

    }
    public boolean connectToDB()  {
        try {
            url = "jdbc:mysql://localhost:3306/networks?autoReconnect=true&useSSL=false";
            login = "Admin";
            password = "admin";

            connection = DriverManager.getConnection(url, login, password);

        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    public void closeConnection() throws SQLException {
        if(connection != null && !connection.isClosed()){
            connection.close();
        }
    }
}
