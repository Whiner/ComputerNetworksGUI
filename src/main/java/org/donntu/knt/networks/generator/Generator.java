package org.donntu.knt.networks.generator;

import org.donntu.knt.networks.generator.configs.GenerateConfig;
import org.donntu.knt.networks.generator.field.Field;

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
                    generateConfig.getLanNetworksQuantity()
            );

            return TopologyGenerator.generateTopology(
                        generateConfig.getLanNetworksQuantity(),
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
