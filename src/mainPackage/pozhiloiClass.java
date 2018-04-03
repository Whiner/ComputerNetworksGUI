package mainPackage;

import nodeGenerator.drawer.DrawConfigs;
import nodeGenerator.drawer.GeneratorDrawer;
import nodeGenerator.field.Field;
import nodeGenerator.Network;
import nodeGenerator.Topology;
import nodeGenerator.TopologyGenerator;
import javafx.scene.image.Image;

import java.io.File;
import java.util.List;

class pozhiloiClass {
    static Image pozhiloiMetod() throws Exception {
        if(Field.getInstance().getLanSections() != null){
            Field.getInstance().getLanSections().clear();
        }
        Field.getInstance().setCellsCount(8); // если изменяю до 5 и кол-во узлов - 10, то залупится
        Field.getInstance().setFieldSize_px(2000);
        Field.getInstance().AddWAN_Section();
        Field.getInstance().CreateLAN_Sections(2);
        DrawConfigs.getInstance().setNodeImage(new File("src/images-fx/2.png"));
        DrawConfigs.getInstance().calcNodeSize();

        Topology t = new Topology();

        try {
            t.AddNetwork(TopologyGenerator.generateWAN(8,3));
            t.AddNetwork(TopologyGenerator.generateLAN(8, 3));
            t.AddNetwork(TopologyGenerator.generateLAN(8, 3));
            TopologyGenerator.connectNetworks(t.getNetworks().get(0), t.getNetworks().get(1), 2);
            TopologyGenerator.connectNetworks(t.getNetworks().get(0), t.getNetworks().get(2), 2);

        } catch (Exception e) {
           e.printStackTrace();
        }

        GeneratorDrawer drawer = new GeneratorDrawer(Field.getInstance().getFieldSize_px(), Field.getInstance().getFieldSize_px());
        List<Network> networks = t.getNetworks();
        drawer.drawTopology(t);
        drawer.saveImage("1.png");
        return drawer.getImage();
    }
}
