package nodegenerator.ip_addresses;

public class IP {
    private int first;
    private int second;
    private int third;
    private int fourth;

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

    public IP(int first, int second, int third, int fourth) throws Exception {
        if(first < 256
                && second < 256
                && third < 256
                && fourth < 256
                && first >= 0
                && second >= 0
                && third >= 0
                && fourth >= 0) {
            this.first = first;
            this.second = second;
            this.third = third;
            this.fourth = fourth;
        }
        else{
            throw new Exception("Некорректный адрес");
            
        }
    }
}