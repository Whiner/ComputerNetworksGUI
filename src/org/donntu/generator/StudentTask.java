package org.donntu.generator;

import org.donntu.GregorianCalendar;
import org.donntu.generator.Topology;


public class StudentTask {
    private Topology topology;
    private GregorianCalendar creationDate;
    private String name;
    private String surname;
    private String group;

    public StudentTask(Topology topology, String name, String surname, String group, GregorianCalendar creationDate) {
        this.topology = topology;
        this.name = name;
        this.surname = surname;
        this.group = group;
        this.creationDate = creationDate;
    }
    public StudentTask(){}

    public StudentTask(GregorianCalendar creationDate, String name, String surname, String group) {
        this.creationDate = creationDate;
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

    public GregorianCalendar getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(GregorianCalendar creationDate) {
        this.creationDate = creationDate;
    }
}
