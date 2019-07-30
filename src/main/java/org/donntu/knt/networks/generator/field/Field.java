package org.donntu.knt.networks.generator.field;

import org.donntu.knt.networks.databaseworker.StudentTask;
import org.donntu.knt.networks.drawer.DrawConfig;

import java.util.ArrayList;
import java.util.List;

public class Field {

    private int cellsCountX = 8;
    private int cellsCountY = 8;

    private Section wanSection;
    private List<Section> lanSections;


    public int getLAN_Field_Count() {
        return lanSections.size();
    }

    public Section getWanSection() {
        return wanSection;
    }

    public List<Section> getLanSections() {
        return lanSections;
    }

    public void setConfig(int cellsCountX, int cellsCountY, int lan_quantity) throws Exception {
        setCellsCountX(cellsCountX);
        setCellsCountY(cellsCountY);
        Field.getInstance().addWANSection();
        createLANSections(lan_quantity);
    }

    public boolean addWANSection() {
        if (wanSection != null)
            return false;
        wanSection = new Section("WAN",
                0,
                0,
                cellsCountX,
                cellsCountY / 2);
        return true;
    }

    public void createLANSections(int count) throws Exception {
        if (count < 1) {
            throw new Exception("Отрицательное количество LAN секций");
        }

        lanSections = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            int beginCell_X = (cellsCountX / count) * i;
            int beginCell_Y = cellsCountY / 2;
            int cells_Count_X = cellsCountX / count;
            int cells_Count_Y = cellsCountY - cellsCountY / 2;
            if (i == count - 1) {
                cells_Count_X = cellsCountX - cells_Count_X * (count - 1);
            }
            lanSections.add(new Section(
                    "LAN" + (i + 1),
                    beginCell_X,
                    beginCell_Y,
                    cells_Count_X,
                    cells_Count_Y
            ));
        }

    }

    public void deleteLANSections() {
        lanSections.clear();
        lanSections = null;
    }

    public void deleteWANSection() { //индикатор успеха надо
        wanSection = null;
    }


    public void autoFilling(StudentTask task) throws Exception {
        cellsCountX = task.getCells_x();
        cellsCountY = task.getCells_y();
        createLANSections(task.getTopology().get_LAN_Quantity());
        if(task.getTopology().getWAN() != null) {
            addWANSection();
        }
        DrawConfig.getInstance().calcNodeSize();
    }


    private static Field instance;

    public static Field getInstance() {
        if (instance == null) {
            instance = new Field();
        }
        return instance;
    }


    private Field() {
    }


    public int getCellsCountX() {
        return cellsCountX;
    }

    public void setCellsCountX(int cellsCountX) {
        this.cellsCountX = cellsCountX;
    }

    public int getCellsCountY() {
        return cellsCountY;
    }

    public void setCellsCountY(int cellsCountY) {
        this.cellsCountY = cellsCountY;
    }

}
