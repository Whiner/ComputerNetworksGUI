package org.donntu.knt.networks.generator;


import org.donntu.knt.networks.generator.field.Field;
import org.donntu.knt.networks.generator.field.Section;
import org.donntu.knt.networks.generator.generatorException.NodeExistException;
import org.donntu.knt.networks.generator.generatorException.NodeInterseptionException;
import org.donntu.knt.networks.generator.generatorException.NodeRelationsException;
import org.donntu.knt.networks.generator.generatorException.SectionException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


public class TopologyGenerator {

    private TopologyGenerator(){}
    private static Network generateNodes(int nodeCount, int maxNodeRelationsCount, Section section) throws Exception {
        if (nodeCount <= 0) {
            throw new Exception("Количество узлов должно быть больше 0");
        }
        if (maxNodeRelationsCount <= 0) {
            throw new NodeRelationsException("Максимальное количество связей должно быть больше 0");
        }
        if (section == null) {
            throw new NullPointerException();
        }
		//проверку на заполненность секции перенести сюда
        int t_NodeCount = nodeCount;
        if (section.getCells_Count_X() * section.getCells_Count_Y() < nodeCount) {
            t_NodeCount = section.getCells_Count_X() * section.getCells_Count_Y();
        }

        Network network = new Network();

        int i = 0;
        int tries = 0;
        while (i < t_NodeCount && !network.isAllHaveMaxRelations()) {
            tries++;
            int x = ThreadLocalRandom.current().nextInt(
                    section.getBeginCell_X(),
                    section.getBeginCell_X() + section.getCells_Count_X());
            int y = ThreadLocalRandom.current().nextInt(
                    section.getBeginCell_Y(),
                    section.getBeginCell_Y() + section.getCells_Count_Y());


            if (network.getByCoord(x, y) != null) {
                continue;
            }
            List<Integer> connectWith = new ArrayList<>();
            int connectQuantity;
            if (i == 0) {
                connectQuantity = 0;
            } else {
                connectQuantity = ThreadLocalRandom.current().nextInt(1, Math.min(i, maxNodeRelationsCount) + 1);
            }

            for (int j = 0; j < connectQuantity; j++) {
                int randomConnectNode;
                do {
                    randomConnectNode = ThreadLocalRandom.current().nextInt(0, i);
                } while (connectWith.contains(randomConnectNode));
                connectWith.add(randomConnectNode);
            }
            try {
                network.addNode(x, y, connectWith, maxNodeRelationsCount);
            } catch (NodeExistException e) {
                                continue;
            } catch (NodeInterseptionException e) {
                if (tries > 1000) {
                    System.out.println("Защита от вечного цикла сработала");
                    break;
                } else {
                    continue;
                }
            }
            i++;

        }
        section.setFill();
        return network;
    }

    public static Network generateWAN(int nodeCount, int maxNodeRelationsCount) throws Exception {
        if (nodeCount <= 0) {
            throw new Exception("Количество узлов должно быть больше 0");
        }
        if (maxNodeRelationsCount <= 0) {
            throw new NodeRelationsException("Максимальное количество связей должно быть больше 0");
        }
        if (Field.getInstance().getWanSection() == null) {
            throw new NullPointerException("WAN section is null");
        }


        Field.getInstance().getWanSection().setFill();

        Network network = generateNodes(
                nodeCount,
                maxNodeRelationsCount,
                Field.getInstance().getWanSection());
        network.setType(NetworkType.WAN);
        network.doCycleConnection();
        return network;


    }

    public static Network generateLAN(int nodeCount, int maxNodeRelationsCount) throws Exception {
        if (nodeCount <= 0) {
            throw new Exception("Количество узлов должно быть больше 0");
        }
        if (maxNodeRelationsCount <= 0) {
            throw new NodeRelationsException("Максимальное количество связей должно быть больше 0");
        }
        if (Field.getInstance().getLanSections().isEmpty()) {
            throw new NullPointerException("Не созданы LAN секции, в которые можно поместить сеть");
        }

        Section t_Section = null;
        for (Section t : Field.getInstance().getLanSections()) { // перенести в Field
            if (!t.isFill()) {
                t_Section = t;
                break;
            }
        }

        if (t_Section == null) {
            throw new SectionException("Все LAN секции заполнены");
        }

        Network network = generateNodes(
                nodeCount,
                maxNodeRelationsCount,
                t_Section);
        network.setType(NetworkType.LAN);
        network.doCycleConnection();
        return network;
    }

    public static void connectNetworks(Network firstNetwork, Network secondNetwork, int connectionsQuantity) throws Exception {
        if (firstNetwork == null || secondNetwork == null) {
            throw new NullPointerException();
        }
        if (connectionsQuantity < 1) {
            throw new Exception("Некорректное количество связей");
        }

        class Connected {
            private Node first;
            private Node second;

            private Connected() {
            }

            private Connected(Node f, Node s) {
                first = f;
                second = s;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) {
                    return true;
                }
                if (o == null || getClass() != o.getClass()) {
                    return false;
                }
                Connected connected = (Connected) o;
                return first.equals(connected.first) &&
                        second.equals(connected.second);
            }

        }
        List<Connected> connectedNodes = new ArrayList<>();

        for (int i = 0; i < connectionsQuantity; i++) { // сделать проверку на всем поле по переcечениям
            Connected lastConnected = new Connected();
            int lastLength = 1000;
            for (Node f : firstNetwork.getNodes()) {
                for (Node s : secondNetwork.getNodes()) {
                    int length = (int) Math.sqrt(Math.pow(f.getCellNumberX() - s.getCellNumberX(), 2)
                            + Math.pow(f.getCellNumberY() - s.getCellNumberY(), 2)); // расстояние между точками
                    if (length <= lastLength && !connectedNodes.contains(new Connected(f, s))) {
                        lastConnected.first = f;
                        lastConnected.second = s;
                        lastLength = length;
                    }
                }
            }

            connectedNodes.add(lastConnected);
            firstNetwork.connectNetworks(lastConnected.first, secondNetwork, lastConnected.second);
        }

    }

    public static Topology generateTopology(
            int lan_quantity,
            int wan_node_quantity,
            int wan_relations_quantity,
            int lan_node_quantity,
            int lan_relations_quantity,
            int networks_relations) {

        try { // очищать все секции перед генерацией
            Topology t = new Topology();
            t.AddNetwork(TopologyGenerator.generateWAN(
                    wan_node_quantity,
                    wan_relations_quantity));
            for (int i = 0; i < lan_quantity; i++) {
                t.AddNetwork(generateLAN(
                        lan_node_quantity,
                        lan_relations_quantity));
                connectNetworks(
                        t.getNetworks().get(0),
                        t.getNetworks().get(i + 1),
                        networks_relations);
            }
            for (Network network : t.getNetworks()) {
                boolean enter;
                do {
                    enter = false;
                    generateIPForNetwork(network);
                    for (Network network1 : t.getNetworks()) {
                        if(network == network1 || network1.getIp().isEmpty()){
                            continue;
                        }
                        if (network1.getIp() != null && network.getIp().isEnter(network1.getIp())){
                            enter = true;
                            break;
                        }
                    }
                } while(enter);

            }
            return t;
        } catch (Exception e) { //ловить заполненные секции
            e.printStackTrace();
        }
        return null;
    }

    public static void generateIPForNetwork(Network network) throws Exception {
        if (network.getNodes().size() == 0) {
            throw new NodeExistException("Пустая сеть");
        }
        IP ip = network.getIp();

        if (network.getType() == NetworkType.LAN) {
            int firstOctet = 0;
            int lowerSecondOctet = 0, upperSecondOctet = 0;
            switch (ThreadLocalRandom.current().nextInt(1, 3)) {
                case 1:
                    firstOctet = 10;
                    lowerSecondOctet = 0;
                    upperSecondOctet = 256;
                    break;
                case 2:
                    firstOctet = 172;
                    lowerSecondOctet = 17;
                    upperSecondOctet = 32;
                    break;
                case 3:
                    firstOctet = 192;
                    lowerSecondOctet = 167;
                    upperSecondOctet = 167;
                    break;
            }
            ip.setFirst(firstOctet);
            ip.setSecond(ThreadLocalRandom.current().nextInt(lowerSecondOctet, upperSecondOctet));
        } else {
            ip.setFirst(ThreadLocalRandom.current().nextInt(0, 256));
            ip.setSecond(ThreadLocalRandom.current().nextInt(0, 256));
        }

        int relationsCount = network.getCountRelations();
        int needAddresses = relationsCount * 4 + network.getNetworkConnections().size() * 4 + 2;

        for (int i = 32; i > 0; i--) {
            if (Math.pow(2, 32 - i) > needAddresses) {
                ip.setMask(ThreadLocalRandom.current().nextInt(i - 3, i + 1));
                break;
            }
        }

        int total = 32;
        if(ip.getMask() <= total - 8){
           total -= 8;
        }

        int notChangeableBites = total - ip.getMask();

        int changeableBites = 8 - notChangeableBites;
        int possibleOptionsQuantity = (int) Math.pow(2, changeableBites);

        int step = 256 / possibleOptionsQuantity;

        int octet = ThreadLocalRandom.current().nextInt(0, possibleOptionsQuantity) * step;

        if(total == 32){
            ip.setThird(ThreadLocalRandom.current().nextInt(0, 256));
            ip.setFourth(octet);
        } else {
            ip.setThird(octet);
            ip.setFourth(0);
        }
    }


}
