package org.donntu.generator;

import org.donntu.GregorianCalendar;
import org.donntu.databaseworker.Student;
import org.donntu.databaseworker.StudentTask;
import org.donntu.generator.configs.GenerateConfig;
import org.donntu.generator.field.Field;

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
                        generateConfig.getWanPortsQuantity(),
                        generateConfig.getLanNodesQuantity(),
                        generateConfig.getLanPortsQuantity(),
                        generateConfig.getNetworksPortsQuantity());

            return new StudentTask(t, name, surname, group, new GregorianCalendar());
        } catch (Exception e) {
            throw new Exception("Генерация прервана с ошибкой: \n" + e.getMessage());
        }
    }

    public static List<StudentTask> generateTasksForGroup(
            List<Student> namesAndSurnames,
            String group,
            GenerateConfig generateConfig) {
        List<StudentTask> tasks = new ArrayList<>();

        for (Student nameAndSurname: namesAndSurnames){
            try {
                tasks.add(
                        generateIndividualTask(
                                nameAndSurname.getName(),
                                nameAndSurname.getSurname(),
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
