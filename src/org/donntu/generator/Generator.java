package org.donntu.generator;

import org.donntu.generator.configs.GenerateConfig;
import org.donntu.generator.field.Field;

public class Generator {

    public static Topology generateTask(
            GenerateConfig generateConfig) throws Exception {
        try {
            if(Field.getInstance().getLanSections() != null){
                Field.getInstance().getLanSections().clear();
            }

            Field.getInstance().setConfig(
                    generateConfig.getCellsCountX(),
                    generateConfig.getCellsCountY(),
                    generateConfig.getLanQuantity()
            );

            return TopologyGenerator.generateTopology(
                        generateConfig.getLanQuantity(),
                        generateConfig.getWanNodesQuantity(),
                        generateConfig.getWanPortsQuantity(),
                        generateConfig.getLanNodesQuantity(),
                        generateConfig.getLanPortsQuantity(),
                        generateConfig.getNetworksPortsQuantity());
        } catch (Exception e) {
            throw new Exception("Генерация прервана с ошибкой: \n" + e.getMessage());
        }
    }

}
