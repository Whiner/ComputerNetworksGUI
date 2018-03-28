package NodeGenerator;


import NodeGenerator.GeneratorException.NodeRelationsCountException;
import NodeGenerator.GeneratorException.OutOfFieldException;
import NodeGenerator.GeneratorException.SectionException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class TopologyGenerator {
    /** бутлупы бывают. генерирует слишком много**/
//скобки и имена с мал
    private static Network GenerateNodes(int nodeCount, int maxNodeRelationsCount, Section section) throws Exception {
        if(nodeCount <= 0)
            throw new Exception("Node count must be greater 0");
        if(maxNodeRelationsCount <= 0 || maxNodeRelationsCount > 5)
            throw new Exception("Max node relations count must be greater 0 and less 5");
        if(section == null)
            throw new Exception("Null section");
        int t_NodeCount = nodeCount;
        if(section.getCells_Count_X() * section.getCells_Count_Y() < nodeCount)
            t_NodeCount = section.getCells_Count_X()*section.getCells_Count_Y();

        Random r = new Random();
        Network network = new Network();

        try {
            network.CreateParentNode( // создает родительский элемент
                    section.getBeginCell_X() + r.nextInt(section.getCells_Count_X()),
                    section.getBeginCell_Y() + r.nextInt(section.getCells_Count_Y()));
            network.GetLastNode().setMaxRelationsCount(maxNodeRelationsCount);
        } catch (Exception e){
            throw e;
        }
        int i = 0;

        while(i < t_NodeCount - 1 && !network.isAllHaveMaxRelations()){
            // генерация остальных
            int additionally_Connect_Count;
            if(i < maxNodeRelationsCount) { //до того как будет достаточно узлов для максимального количества связей будет i
                additionally_Connect_Count = r.nextInt(i + 1);
            }
            else {
                additionally_Connect_Count = r.nextInt(maxNodeRelationsCount);
            }

            Direction t_direction = Direction.RandomDirection();  // направление
            int parentID = r.nextInt(i + 1); //рандомный родительский узел
            Node current_parent_node = network.GetNodeByID(parentID);

            if(current_parent_node != null) {
                if(current_parent_node.getMaxRelationsCount() <= current_parent_node.getRelationsCount()) {
                    continue;
                }
                int X = Direction.Check_X_by_Direction(current_parent_node, t_direction);
                int Y = Direction.Check_Y_by_Direction(current_parent_node, t_direction);



                if(X >= section.getBeginCell_X() + section.getCells_Count_X() ||
                        X < section.getBeginCell_X() ||
                        Y >= section.getBeginCell_Y() + section.getCells_Count_Y() ||
                        Y < section.getBeginCell_Y()) {
                    continue;
                }
                Node node_by_coord = network.GetByCoord(X, Y);
                if(node_by_coord != null) {
                    continue;
                }
            }
            else {
                continue;
            }

            List<Integer> additionally_Connect_ID = new ArrayList<>(); //с какими соединить еще
            int randomConnectNode;
            if(i != 0 && additionally_Connect_Count != 0)
            {
                for (int j = 0; j < additionally_Connect_Count; j++) {
                    do {
                        randomConnectNode = r.nextInt(i + 1);

                    } while (randomConnectNode == parentID
                            || additionally_Connect_ID.contains(randomConnectNode));
                    additionally_Connect_ID.add(randomConnectNode);
                }
            }
            boolean newNode;
            try {
                newNode = network.AddNode(t_direction, parentID, additionally_Connect_ID);
                if(!newNode) {
                    continue;
                }
                network.GetLastNode().setMaxRelationsCount(maxNodeRelationsCount); // ловить
            } catch (Exception e) {
                if(e.getClass() != NodeRelationsCountException.class
                        || e.getClass() != OutOfFieldException.class) {
                    System.out.println(e.getMessage());
                    break;
                }

            }
            i++;
        }
        section.setFill();
        return network;
    }

    public static Network GenerateWAN(int nodeCount, int maxNodeRelationsCount) throws Exception {
        if(nodeCount <= 0) {
            throw new Exception("Node count must be greater 0");
        }
        if(maxNodeRelationsCount <= 0 || maxNodeRelationsCount > 5) {
            throw new NodeRelationsCountException("Max node relations count must be greater 0 and less 5");
        }
        if(Field.GetInstance().getWAN_Section() == null) {
            throw new NullPointerException("WAN Section is null");
        }


        Field.GetInstance().getWAN_Section().setFill();

        Network network = GenerateNodes(
                nodeCount,
                maxNodeRelationsCount,
                Field.GetInstance().getWAN_Section());
            network.setType(NetworkType.WAN);
        return network;


    }

    public static Network GenerateLAN(int nodeCount, int maxNodeRelationsCount) throws Exception {
        if(nodeCount <= 0){
            throw new Exception("Node count must be greater 0");
        }
        if(maxNodeRelationsCount <= 0 || maxNodeRelationsCount > 5) {
            throw new NodeRelationsCountException("Max node relations count must be greater 0 and less 5");
        }
        if(Field.GetInstance().getLAN_Sections().isEmpty()) {
            throw new NullPointerException("LAN sections is null");
        }

        Section t_Section = null;
        for(Section t: Field.GetInstance().getLAN_Sections()){
            if(!t.isFill())
            {
                t_Section = t;
                break;
            }
        }
        if(t_Section == null) {
            throw new SectionException("All LAN sections is full");
        }

        Network network = GenerateNodes(
                nodeCount,
                maxNodeRelationsCount,
                t_Section);
        network.setType(NetworkType.LAN);
        return network;
    }
}
