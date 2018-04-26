package ui.main.generateButton;

import org.donntu.nodegenerator.Topology;
import org.donntu.nodegenerator.TopologyGenerator;
import org.donntu.nodegenerator.drawer.DrawConfigs;
import org.donntu.nodegenerator.drawer.GeneratorDrawer;
import org.donntu.nodegenerator.field.Field;

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

            Field.getInstance().setConfig(
                    GenerateConfig.getInstance().getCellsCount(),
                    GenerateConfig.getInstance().getImageSize(),
                    GenerateConfig.getInstance().getLanQuantity()
                    );
            DrawConfigs.getInstance().setNodeImage(GenerateConfig.getInstance().getNodeImage());
            DrawConfigs.getInstance().calcNodeSize();

            Topology t = TopologyGenerator.generateTopology(
                    GenerateConfig.getInstance().getLanQuantity(),
                    GenerateConfig.getInstance().getWanNodesQuantity(),
                    GenerateConfig.getInstance().getWanRelationsQuantity(),
                    GenerateConfig.getInstance().getLanNodesQuantity(),
                    GenerateConfig.getInstance().getLanRelationsQuantity(),
                    GenerateConfig.getInstance().getNetworksRelationsQuantity());

            drawer = new GeneratorDrawer(
                    Field.getInstance().getFieldSize_px(),
                    Field.getInstance().getFieldSize_px(),
                    true);
            drawer.drawTopology(t);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
