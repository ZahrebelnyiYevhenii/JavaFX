package trudvbolshom.exception;

public class WordWorkerException extends Exception{

    public WordWorkerException() {
        super();
    }

    public WordWorkerException(String message) {
        super(WordWorkerException.class.getName() + message);
    }
}
