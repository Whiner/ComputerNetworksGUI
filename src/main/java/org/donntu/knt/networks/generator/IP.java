package org.donntu.knt.networks.generator;

public class IP {
    private int first;
    private int second;
    private int third;
    private int fourth;
    private int mask;

    public IP() {
    }

    public int getMask() {
        return mask;
    }

    public void setMask(int mask) throws Exception {
        if(mask > 32 || mask < 0) {
            throw new Exception("Маска введена не корректно");
        }
        this.mask = mask;
    }


    @Override
    public String toString() {
        return  first +
                "." + second +
                "." + third +
                "." + fourth +
                "/" + mask;
    }

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

    public IP getCopy() throws Exception {
        return new IP(first, second, third, fourth, mask);
    }

    public boolean isEmpty(){
        return first == 0 && second == 0 && third == 0 && fourth == 0 && mask == 0;
    }

    public boolean isLike(IP ip) {
        return (mask == ip.mask && fourth == ip.fourth);
    }

    public boolean isEnter(IP ip){
        int thisRange = (int) Math.pow(2, 32 - this.getMask());
        int checkRange = (int) Math.pow(2, 32 - ip.getMask());
        long longIP = ip2long(ip);
        long longThis = ip2long(this);
        return longIP >= longThis && longIP < longThis + thisRange
                || longIP + checkRange >= longThis && longIP + checkRange < longThis + thisRange;
    }

    private static Long ip2long(IP ip) {
        int[] octets = new int[4];
        octets[0] = ip.getFirst();
        octets[1] = ip.getSecond();
        octets[2] = ip.getThird();
        octets[3] = ip.getFourth();
        long num = 0;
        for (int i = 0; i < octets.length; i++) {
            int power = 3 - i;
            num += (octets[i] % 256) * Math.pow(256, power);
        }
        return num;
    }
}
