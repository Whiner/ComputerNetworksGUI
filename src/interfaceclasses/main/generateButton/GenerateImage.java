package interfaceclasses.main.generateButton;

import nodegenerator.Topology;
import nodegenerator.TopologyGenerator;
import nodegenerator.drawer.DrawConfigs;
import nodegenerator.drawer.GeneratorDrawer;
import nodegenerator.field.Field;

import java.io.IOException;

public class GenerateImage {

    private static GeneratorDrawer drawer;

    public static void SaveImage(String imageName) throws IOException {
        drawer.saveImage(GenerateConfig.getInstance().getDirectory().getPath() + imageName + ".png");
    }
    public static void generate(){
        try {

            if(Field.getInstance().getLanSections() != null){
                Field.getInstance().getLanSections().clear();
            }

            Field.getInstance().setCellsCount(GenerateConfig.getInstance().getCellsCount());
            Field.getInstance().setFieldSize_px(GenerateConfig.getInstance().getImageSize());
            Field.getInstance().AddWAN_Section();
            Field.getInstance().CreateLAN_Sections(GenerateConfig.getInstance().getLanQuantity());
            DrawConfigs.getInstance().setNodeImage(GenerateConfig.getInstance().getNodeImage());
            DrawConfigs.getInstance().calcNodeSize();

            Topology t = new Topology();

            t.AddNetwork(TopologyGenerator.generateWAN(
                    GenerateConfig.getInstance().getWanNodesQuantity(),
                    GenerateConfig.getInstance().getWanRelationsQuantity()));
            for (int i = 0; i < GenerateConfig.getInstance().getLanQuantity(); i++){
                t.AddNetwork(TopologyGenerator.generateLAN(
                        GenerateConfig.getInstance().getLanNodesQuantity(),
                        GenerateConfig.getInstance().getLanRelationsQuantity()));
                TopologyGenerator.connectNetworks(
                        t.getNetworks().get(0),
                        t.getNetworks().get(i + 1),
                        GenerateConfig.getInstance().getNetworksRelationsQuantity());
            }

            drawer = new GeneratorDrawer(Field.getInstance().getFieldSize_px(), Field.getInstance().getFieldSize_px());
            drawer.drawTopology(t);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
