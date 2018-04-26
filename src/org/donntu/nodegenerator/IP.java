package org.donntu.nodegenerator;

public class IP {
    private int first;
    private int second;
    private int third;
    private int fourth;

    public int getMask() {
        return mask;
    }

    public void setMask(int mask) throws Exception {
        if(mask > 32 || mask < 0) {
            throw new Exception("Маска введена не корректно");
        }
        this.mask = mask;
    }

    private int mask;

    public int getFirst() {
        return first;
    }

    public void setFirst(int first) throws Exception {
        if(first > 255 || first < 0){
            throw new Exception("Некорректное число для адреса");
        }
        this.first = first;
    }

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) throws Exception {
        if(second > 255 || second < 0){
            throw new Exception("Некорректное число для адреса");
        }
        this.second = second;
    }

    public int getThird() {
        return third;
    }

    public void setThird(int third) throws Exception {
        if(third > 255 || third < 0){
            throw new Exception("Некорректное число для адреса");
        }
        this.third = third;
    }

    public int getFourth() {
        return fourth;
    }

    public void setFourth(int fourth) throws Exception {
        if(fourth > 255 || fourth < 0){
            throw new Exception("Некорректное число для адреса");
        }
        this.fourth = fourth;
    }

    public IP(int first, int second, int third, int fourth, int mask) throws Exception {
        try {
            setFirst(first);
            setSecond(second);
            setThird(third);
            setFourth(fourth);
            setMask(mask);
        } catch (Exception e){
            throw new Exception("Некорректный адрес");
        }

    }
}
