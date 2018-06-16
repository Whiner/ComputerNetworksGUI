package org.donntu.databaseworker;

import org.donntu.GregorianCalendar;
import org.donntu.generator.Topology;


public class StudentTask extends Student{
    private int key;
    private Topology topology;
    private GregorianCalendar creationDate;
    private int cells_x;
    private int cells_y;

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public StudentTask(Topology topology, String name, String surname, String group, GregorianCalendar creationDate, int cells_x, int cells_y) {
        this.topology = topology;
        this.name = name;
        this.surname = surname;
        this.group = group;
        this.creationDate = creationDate;
        this.cells_x = cells_x;
        this.cells_y = cells_y;
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

    public void setStudent(Student student){
        this.name = student.name;
        this.surname = student.surname;
        this.group = student.group;
    }

    @Override
    public String toString() {
        return surname + " "
                + name + " "
                + group + " "
                + new GregorianCalendar().toString();
    }

    public int getCells_x() {
        return cells_x;
    }

    public void setCells_x(int cells_x) {
        this.cells_x = cells_x;
    }

    public int getCells_y() {
        return cells_y;
    }

    public void setCells_y(int cells_y) {
        this.cells_y = cells_y;
    }
}
