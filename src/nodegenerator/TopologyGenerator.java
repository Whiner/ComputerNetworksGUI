package nodegenerator;


import nodegenerator.field.Field;
import nodegenerator.field.Section;
import nodegenerator.generatorException.NodeExistException;
import nodegenerator.generatorException.NodeInterseptionException;
import nodegenerator.generatorException.NodeRelationsException;
import nodegenerator.generatorException.SectionException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;


public class TopologyGenerator {

    private static Network generateNodes(int nodeCount, int maxNodeRelationsCount, Section section) throws Exception {
        if(nodeCount <= 0)
            throw new Exception("Node count must be greater 0");
        if(maxNodeRelationsCount <= 0 || maxNodeRelationsCount > 5)
            throw new Exception("Max node relations count must be greater 0 and less 5");
        if(section == null)
            throw new Exception("Null section");
        int t_NodeCount = nodeCount;
        if(section.getCells_Count_X() * section.getCells_Count_Y() < nodeCount)
            t_NodeCount = section.getCells_Count_X()*section.getCells_Count_Y();

        Network network = new Network();

        int i = 0;
        int tries = 0;
        while(i < t_NodeCount && !network.isAllHaveMaxRelations()){
            tries++;
            int x = ThreadLocalRandom.current().nextInt(
                    section.getBeginCell_X(),
                    section.getBeginCell_X() + section.getCells_Count_X());
            int y = ThreadLocalRandom.current().nextInt(
                    section.getBeginCell_Y(),
                    section.getBeginCell_Y() + section.getCells_Count_Y());


            if(network.getByCoord(x, y) != null){
                continue;
            }
            List<Integer> connectWith = new ArrayList<>();
            int connectQuantity;
            if(i == 0){
                connectQuantity = 0;
            }
            else{
                connectQuantity = ThreadLocalRandom.current().nextInt(1, Math.min(i, maxNodeRelationsCount) + 1);
            }

            for(int j = 0; j < connectQuantity; j++){
                int randomConnectNode;
                do {
                    randomConnectNode = ThreadLocalRandom.current().nextInt(0, i);
                } while (connectWith.contains(randomConnectNode));
                connectWith.add(randomConnectNode);
            }
            try{
                network.addNode(x, y, connectWith, maxNodeRelationsCount);
            } catch (NodeExistException e){
                continue;
            } catch (NodeInterseptionException e){
                if(tries > 1000){
                    break;
                } else {
                    continue;
                }
            }
            i++;
        }
        section.setFill();
        System.out.println("Node tries " + tries + "/" + nodeCount);
        return network;
    }

    public static Network generateWAN(int nodeCount, int maxNodeRelationsCount) throws Exception {
        if(nodeCount <= 0) {
            throw new Exception("Node count must be greater 0");
        }
        if(maxNodeRelationsCount <= 0 || maxNodeRelationsCount > 5) {
            throw new NodeRelationsException("Max node relations count must be greater 0 and less 5");
        }
        if(Field.getInstance().getWanSection() == null) {
            throw new NullPointerException("WAN Section is null");
        }


        Field.getInstance().getWanSection().setFill();

        Network network = generateNodes(
                nodeCount,
                maxNodeRelationsCount,
                Field.getInstance().getWanSection());
            network.setType(NetworkType.WAN);
        return network;


    }

    public static Network generateLAN(int nodeCount, int maxNodeRelationsCount) throws Exception {
        if(nodeCount <= 0){
            throw new Exception("Node count must be greater 0");
        }
        if(maxNodeRelationsCount <= 0 || maxNodeRelationsCount > 5) {
            throw new NodeRelationsException("Max node relations count must be greater 0 and less 5");
        }
        if(Field.getInstance().getLanSections().isEmpty()) {
            throw new NullPointerException("LAN sections is null");
        }

        Section t_Section = null;
        for(Section t: Field.getInstance().getLanSections()){
            if(!t.isFill())
            {
                t_Section = t;
                break;
            }
        }
        if(t_Section == null) {
            throw new SectionException("All LAN sections is full");
        }

        Network network = generateNodes(
                nodeCount,
                maxNodeRelationsCount,
                t_Section);
        network.setType(NetworkType.LAN);
        return network;
    }

    public static void connectNetworks(Network firstNetwork, Network secondNetwork, int connectionsQuantity) throws Exception {
        if(firstNetwork == null || secondNetwork == null){
            throw new NullPointerException();
        }
        if(connectionsQuantity < 1){
            throw new Exception("Некорректное количество связей");
        }



        class Connected {
            private Node first;
            private Node second;
            Connected(){}
            private Connected(Node f, Node s) {
                first = f;
                second = s;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                Connected connected = (Connected) o;
                return Objects.equals(first, connected.first) &&
                        Objects.equals(second, connected.second);
            }

        }
        List<Connected> connectedNodes = new ArrayList<>();




        for (int i = 0; i < connectionsQuantity; i++) { // сделать проверку на всем поле по переечениям
            Connected lastConnected = new Connected();
            int lastLength = 1000;
            for (Node f : firstNetwork.getNodes()) {
                for (Node s : secondNetwork.getNodes()) {
                    int length = (int) Math.sqrt(Math.pow(f.getCellNumber_X() - s.getCellNumber_X(), 2)
                            + Math.pow(f.getCellNumber_Y() - s.getCellNumber_Y(), 2)); // расстояние между точками
                    if (length <= lastLength && !connectedNodes.contains(new Connected(f, s) )) {
                        lastConnected.first = f;
                        lastConnected.second = s;
                        lastLength = length;
                    }
                }
            }

            lastConnected.first.connectNode(lastConnected.second, true);
            connectedNodes.add(lastConnected);

        }

    }

}
