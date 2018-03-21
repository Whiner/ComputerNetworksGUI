package NodeGenerator;

import java.util.ArrayList;
import java.util.List;

public class Field {

    private int NodeSize;
    private int Cells_Count = -1;
    private int SizeBorderInPx = -1;

    private Section WAN_Section;
    private List<Section> LAN_Sections;
    private int MaxSectionsCount;

    public int getLAN_Field_Count() {
        return LAN_Sections.size();
    }

    public Section getWAN_Section() {
        return WAN_Section;
    }

    public List<Section> getLAN_Sections() {
        return LAN_Sections;
    }

    public int getMaxSectionsCount() {
        return MaxSectionsCount;
    }

    public boolean AddWAN_Section(){
        if(WAN_Section != null)
            return false;
        WAN_Section = new Section("",
                NetworkType.WAN,
                0,
                0,
                Cells_Count,
                Cells_Count / 2);
        return true;
    }

    public boolean CreateLAN_Sections(int count){
        if(LAN_Sections != null)
            return false;

        if(count > MaxSectionsCount && MaxSectionsCount != -1)
            count = MaxSectionsCount;

        LAN_Sections = new ArrayList<>();
        for (int i = 0; i < count; i++){
            LAN_Sections.add(new Section(
                    "" + (LAN_Sections.size() + i + 1),
                    NetworkType.LAN,
                    (Cells_Count / count) * i,
                    Cells_Count / 2 ,
                    Cells_Count / count,
                    Cells_Count / 2
                    ));
        }

        return true;
    }

    public void Delete_LAN_Sections(){ //индикатор успеха надо
        LAN_Sections.clear();
        LAN_Sections = null;
    }
    public void Delete_WAN_Section(){ //индикатор успеха надо
        WAN_Section = null;
    }

    public int getCells_Count() {
        return Cells_Count;
    }

    public void setCells_Count(int cells_Count) throws Exception {
        if(cells_Count < 1)
            throw new Exception("Cells count must be greater 0");
        Cells_Count = cells_Count;
        if(SizeBorderInPx != -1)
            NodeSize();
    }

    public int getSizeBorderInPx() {
        return SizeBorderInPx;
    }

    public void setSizeBorderInPx(int sizeBorderInPx) throws Exception {
        if(sizeBorderInPx < 0)
            throw new Exception("Field size must be positive");

        SizeBorderInPx = sizeBorderInPx;
        if(Cells_Count != -1)
            NodeSize();

    }



    public int getNodeSize() {
        return NodeSize;
    }

    private void NodeSize() {
        try{
            NodeSize = SizeBorderInPx / (Cells_Count * 2);
        }
        catch (Exception e){

        }
    }




    private static Field instance;

    public static Field GetInstance(){
        if (instance == null) {
            instance = new Field();
            instance.MaxSectionsCount = -1;
        }
        return instance;
    }


    private Field() {}


    public void setMaxSectionsCount(int maxSectionsCount) {
        MaxSectionsCount = maxSectionsCount;
    }
}
