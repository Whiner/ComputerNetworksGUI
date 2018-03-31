package nodeGenerator.generatorException;

public class OneselfConnection extends Exception {
    public OneselfConnection(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
