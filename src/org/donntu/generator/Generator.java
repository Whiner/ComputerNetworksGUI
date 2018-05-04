package org.donntu.generator;

import org.donntu.databaseworker.StudentTask;
import org.donntu.generator.configs.GenerateConfig;
import org.donntu.generator.drawer.DrawConfigs;
import org.donntu.generator.drawer.GeneratorDrawer;
import org.donntu.generator.field.Field;

public class Generator { //оболочка для всего

    public static StudentTask generateIndividualTask(
            String name, String surname, String group, GenerateConfig generateConfig) throws Exception {
        try {

            if(Field.getInstance().getLanSections() != null){
                Field.getInstance().getLanSections().clear();
            }

            Field.getInstance().setConfig(
                    generateConfig.getCellsCountX(),
                    generateConfig.getCellsCountY(),
                    generateConfig.getImageHeight(),
                    generateConfig.getImageWidth(),
                    generateConfig.getLanQuantity()
            );
            DrawConfigs.getInstance().setNodeImage(generateConfig.getNodeImage());
            DrawConfigs.getInstance().calcNodeSize();

            Topology t = TopologyGenerator.generateTopology(
                    generateConfig.getLanQuantity(),
                    generateConfig.getWanNodesQuantity(),
                    generateConfig.getWanRelationsQuantity(),
                    generateConfig.getLanNodesQuantity(),
                    generateConfig.getLanRelationsQuantity(),
                    generateConfig.getNetworksRelationsQuantity());
            return new StudentTask(t, name, surname, group);
        } catch (Exception e) {
            throw new Exception("Генерация прервана с ошибкой: \n" + e.getMessage());
        }
    }
}
