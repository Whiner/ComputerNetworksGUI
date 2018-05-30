package org.donntu.generator.field;


public class Section {

    private String name;
    private boolean fill;

    private int BeginCell_X, BeginCell_Y;
    private int Cells_Count_X;
    private int Cells_Count_Y;

    public int getBeginCell_X() {
        return BeginCell_X;
    }

    public void setBeginCell_X(int beginCell_X) {
        BeginCell_X = beginCell_X;
    }

    public int getBeginCell_Y() {
        return BeginCell_Y;
    }

    public void setBeginCell_Y(int beginCell_Y) {
        BeginCell_Y = beginCell_Y;
    }

    public int getCells_Count_X() {
        return Cells_Count_X;
    }

    public void setCells_Count_X(int cells_Count_X) {
        Cells_Count_X = cells_Count_X;
    }

    public int getCells_Count_Y() {
        return Cells_Count_Y;
    }

    public void setCells_Count_Y(int cells_Count_Y) {
        Cells_Count_Y = cells_Count_Y;
    }

    public Section(String name,  int beginCell_X, int beginCell_Y, int cells_Count_X, int cells_Count_Y) {
        this.name = name;

        BeginCell_X = beginCell_X;
        BeginCell_Y = beginCell_Y;
        Cells_Count_X = cells_Count_X;
        Cells_Count_Y = cells_Count_Y;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public boolean isFill() {
        return fill;
    }

    public void setFill() {
        this.fill = true;
    }
}
