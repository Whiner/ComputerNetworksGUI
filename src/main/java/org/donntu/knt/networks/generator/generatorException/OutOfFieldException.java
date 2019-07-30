package org.donntu.knt.networks.generator.generatorException;

public class OutOfFieldException extends Exception {
    private int x, y;

    public OutOfFieldException(String message) {
        super(message);
    }

    public OutOfFieldException(String message, int x, int y) {
        super(message);
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
