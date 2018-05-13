package org.donntu.generator;

import javafx.util.Pair;
import org.donntu.GregorianCalendar;
import org.donntu.drawer.DrawConfig;
import org.donntu.generator.configs.GenerateConfig;
import org.donntu.generator.field.Field;

import java.io.File;
import java.util.*;

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

            Topology t = TopologyGenerator.generateTopology(
                    generateConfig.getLanQuantity(),
                    generateConfig.getWanNodesQuantity(),
                    generateConfig.getWanRelationsQuantity(),
                    generateConfig.getLanNodesQuantity(),
                    generateConfig.getLanRelationsQuantity(),
                    generateConfig.getNetworksRelationsQuantity());

            return new StudentTask(t, name, surname, group, new GregorianCalendar()); //решить вопрос со временем
        } catch (Exception e) {
            throw new Exception("Генерация прервана с ошибкой: \n" + e.getMessage());
        }
    }

    public static List<StudentTask> generateTasksForGroup(
            List<Pair<String, String>> namesAndSurnames,
            String group,
            GenerateConfig generateConfig) {
        List<StudentTask> tasks = new ArrayList<>();

        for (Pair<String, String> nameAndSurname: namesAndSurnames){
            try {
                tasks.add(
                        generateIndividualTask(
                                nameAndSurname.getKey(),
                                nameAndSurname.getValue(),
                                group,
                                generateConfig)
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return tasks;
    }
}
