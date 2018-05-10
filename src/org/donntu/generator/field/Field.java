package org.donntu.generator.field;

import org.donntu.generator.NetworkType;

import java.util.ArrayList;
import java.util.List;

public class Field {

    private int cellsCountX = 16;
    private int cellsCountY = 8;
    //private int fieldSize_px = 2000;
    private int height = 2000;
    private int width = 2000;
    private Section wanSection;
    private List<Section> lanSections;

    public void setHeight(int height) {
        this.height = height;
    }

    public int getLAN_Field_Count() {
        return lanSections.size();
    }

    public Section getWanSection() {
        return wanSection;
    }

    public List<Section> getLanSections() {
        return lanSections;
    }

    public void setConfig(int cellsCountX, int cellsCountY, int fieldHeight, int fieldWidth, int lan_quantity) throws Exception {
        setCellsCountX(cellsCountX);
        setCellsCountY(cellsCountY);
        setWidth(fieldWidth);
        setHeight(fieldHeight);
        Field.getInstance().AddWAN_Section();
        CreateLAN_Sections(lan_quantity);
    }

    public boolean AddWAN_Section(){
        if(wanSection != null)
            return false;
        wanSection = new Section("WAN",
                NetworkType.WAN,
                0,
                0,
                cellsCountX,
                cellsCountY / 2);
        return true;
    }

    public void CreateLAN_Sections(int count) throws Exception {
        if(count < 1){
            throw new Exception("Отрицательное количество LAN секций");
        }
        if(lanSections != null && !lanSections.isEmpty()) {
            throw new Exception("Пустой указатель или секции уже существуют");
        }

        lanSections = new ArrayList<>();

        for (int i = 0; i < count; i++){
            int beginCell_X = (cellsCountX / count) * i;
            int beginCell_Y = cellsCountY / 2;
            int cells_Count_X = cellsCountX / count;
            int cells_Count_Y = cellsCountY - cellsCountY / 2;
            if(i == count - 1){
                cells_Count_X = cellsCountX - cells_Count_X * (count - 1);
            }
            lanSections.add(new Section(
                    "LAN" + (i + 1),
                    NetworkType.LAN,
                    beginCell_X,
                    beginCell_Y,
                    cells_Count_X,
                    cells_Count_Y
                    ));
        }

    }

    public void Delete_LAN_Sections(){ //индикатор успеха надо
        lanSections.clear();
        lanSections = null;
    }
    public void Delete_WAN_Section(){ //индикатор успеха надо
        wanSection = null;
    }


   /* public int getFieldSize_px() {
        return fieldSize_px;
    }

    public void setFieldSize_px(int fieldSize_px) throws Exception {
        if(fieldSize_px < 0) {
            throw new Exception("Field size must be positive");
        }
        this.fieldSize_px = fieldSize_px;
    }*/



    private static Field instance;

    public static Field getInstance(){
        if (instance == null) {
            instance = new Field();
        }
        return instance;
    }


    private Field() {}


    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
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

    public void setConfig(int cellsCountX, int cellsCountY, int lanQuantity) throws Exception {
        setCellsCountX(cellsCountX);
        setCellsCountY(cellsCountY);

        Field.getInstance().AddWAN_Section();
        CreateLAN_Sections(lanQuantity);
    }
}
