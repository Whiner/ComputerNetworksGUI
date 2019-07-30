package org.donntu.knt.networks.ui.main;

public class TableTaskStruct {
    private String name;
    private String surname;
    private String group;
    //private Date cre



    public TableTaskStruct(String name, String surname, String group) {
        this.name = name;
        this.surname = surname;
        this.group = group;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }
}
