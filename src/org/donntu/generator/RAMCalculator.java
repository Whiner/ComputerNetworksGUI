package org.donntu.generator;

public class RAMCalculator {
    private static int nodeRAM = 128;

    public static int getRAM(int nodeQuantity){
        if(nodeQuantity > 0) {
            return nodeQuantity * nodeRAM;
        } else {
            return 0;
        }
    }

    public static int getNodeRAM() {
        return nodeRAM;
    }

    public static void setNodeRAM(int nodeRAM) {
        RAMCalculator.nodeRAM = nodeRAM;
    }

    public static int getNodeQuantity(int memory){
        if(memory > 0){
            return memory / nodeRAM;
        } else {
            return 0;
        }
    }

    public static int getNodeQuantityInLAN(int memory, int wanRAM, int lanQuantity){
        return (memory - wanRAM) / lanQuantity / nodeRAM;
    }

    public static int getNodeQuantityInWAN(int memory){
        return (memory / nodeRAM / 2);
    }
}
