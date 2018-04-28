package org.donntu.databaseworker;

import org.donntu.generator.Topology;

public class StudentTask {
    private Topology topology;
    private String name;
    private String surname;
    private String group;

    public StudentTask(Topology topology, String name, String surname, String group) {
        this.topology = topology;
        this.name = name;
        this.surname = surname;
        this.group = group;
    }

    public Topology getTopology() {
        return topology;
    }

    public void setTopology(Topology topology) {
        this.topology = topology;
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
