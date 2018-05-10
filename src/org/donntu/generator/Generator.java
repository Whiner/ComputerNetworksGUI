package org.donntu.generator;

import org.donntu.databaseworker.StudentTask;
import org.donntu.generator.configs.GenerateConfig;
import org.donntu.drawer.DrawConfigs;
import org.donntu.generator.field.Field;

import java.io.File;
import java.util.Date;

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
                    generateConfig.getLanQuantity()
            );
            DrawConfigs.getInstance().setNodeImage(new File("images-fx/2.png"));
            DrawConfigs.getInstance().calcNodeSize();

            Topology t = TopologyGenerator.generateTopology(
                    generateConfig.getLanQuantity(),
                    generateConfig.getWanNodesQuantity(),
                    generateConfig.getWanRelationsQuantity(),
                    generateConfig.getLanNodesQuantity(),
                    generateConfig.getLanRelationsQuantity(),
                    generateConfig.getNetworksRelationsQuantity());
            return new StudentTask(t, name, surname, group, new Date());
        } catch (Exception e) {
            throw new Exception("Генерация прервана с ошибкой: \n" + e.getMessage());
        }
    }
}
