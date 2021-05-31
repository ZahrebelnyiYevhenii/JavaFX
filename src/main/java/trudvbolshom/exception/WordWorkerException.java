package trudvbolshom.exception;

public class WordWorkerException extends RuntimeException {

    public WordWorkerException() {
        super();
    }

    public WordWorkerException(String message) {
        super(WordWorkerException.class.getName() + message);
    }
}
