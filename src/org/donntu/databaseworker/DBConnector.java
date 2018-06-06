package org.donntu.databaseworker;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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
    public boolean connectToDB() throws Exception {
        PropertiesReader propertiesReader = new PropertiesReader(new File("properties.txt"));
        try {
            url = "jdbc:" + propertiesReader.getDBMS_Name() +
                    "://" + propertiesReader.getHostname() + ":"
                    + propertiesReader.getPort() + "/"
                    + propertiesReader.getDbname() + "?autoReconnect=true&useSSL=false&characterEncoding=utf-8";
            login = propertiesReader.getLogin();
            password = propertiesReader.getPassword();

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

    private static DBConnector instance;
    private DBConnector(){}
    public static DBConnector getInstance(){
        if(instance == null){
            instance = new DBConnector();
        }
        return instance;
    }
}
