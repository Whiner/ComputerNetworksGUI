package nodeGenerator.field;

import nodeGenerator.NetworkType;

import java.util.ArrayList;
import java.util.List;

public class Field {

    private int cellsCount = -1;
    private int fieldSize_px = -1;

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



    public boolean AddWAN_Section(){
        if(wanSection != null)
            return false;
        wanSection = new Section("WAN",
                NetworkType.WAN,
                0,
                0,
                cellsCount,
                cellsCount / 2);
        return true;
    }

    public boolean CreateLAN_Sections(int count){
        if(lanSections != null) {
            return false;
        }

        lanSections = new ArrayList<>();

        for (int i = 0; i < count; i++){
            lanSections.add(new Section(
                    "LAN" + (i + 1),
                    NetworkType.LAN,
                    (cellsCount / count) * i,
                    cellsCount / 2 ,
                    cellsCount / count,
                    cellsCount / 2
                    ));
        }

        return true;
    }

    public void Delete_LAN_Sections(){ //индикатор успеха надо
        lanSections.clear();
        lanSections = null;
    }
    public void Delete_WAN_Section(){ //индикатор успеха надо
        wanSection = null;
    }

    public int getCellsCount() {
        return cellsCount;
    }

    public void setCellsCount(int cellsCount) throws Exception {
        if(cellsCount < 1)
            throw new Exception("Cells count must be greater 0");
        this.cellsCount = cellsCount;
    }

    public int getFieldSize_px() {
        return fieldSize_px;
    }

    public void setFieldSize_px(int fieldSize_px) throws Exception {
        if(fieldSize_px < 0)
            throw new Exception("Field size must be positive");
        this.fieldSize_px = fieldSize_px;
    }



    private static Field instance;

    public static Field getInstance(){
        if (instance == null) {
            instance = new Field();
        }
        return instance;
    }


    private Field() {}



}
