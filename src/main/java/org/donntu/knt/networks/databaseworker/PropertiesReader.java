package org.donntu.knt.networks.databaseworker;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PropertiesReader {
    private File file;
    private String[] fileContent;
    private String DBMS_Name;
    private String hostname;
    private String port;
    private String login;
    private String password;
    private String dbname;

    PropertiesReader(File file) throws Exception {
        setFilename(file);
    }
    private String getByRegular(String regular, String text){
        Pattern p = Pattern.compile(regular);
        Matcher m = p.matcher(text);
        m.find();
        return m.group(1);
    }

    public void setFilename(File file) throws Exception {
        this.file = file;
        readFile();
        parse();
    }

    private void readFile() throws FileNotFoundException {
        Scanner scanner = new Scanner(file);
        List<String> content = new ArrayList<>();
        while(scanner.hasNext()){
            content.add(scanner.nextLine());
        }
        fileContent = new String [content.size()];
        int i = 0;
        for (String line: content){
            fileContent[i++] = line;
        }
    }

    private void parse() throws Exception {
        try {
            DBMS_Name = getByRegular("DBMS_Name:\\s*(.+)", fileContent[0]);
            hostname = getByRegular("hostname:\\s*(.+)", fileContent[1]);
            port = getByRegular("port:\\s*(.+)", fileContent[2]);
            login = getByRegular("login:\\s*(.+)", fileContent[3]);
            password = getByRegular("password:\\s*(.+)", fileContent[4]);
            dbname = getByRegular("dbname:\\s*(.+)", fileContent[5]);
        } catch (IllegalStateException e){
            throw new Exception("Структура файла повреждена");
        }
    }

    public File getFile() {
        return file;
    }

    public String[] getFileContent() {
        return fileContent;
    }

    public String getDBMS_Name() {
        return DBMS_Name;
    }

    public String getHostname() {
        return hostname;
    }

    public String getPort() {
        return port;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getDbname() {
        return dbname;
    }
}
