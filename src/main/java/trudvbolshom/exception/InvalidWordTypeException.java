package trudvbolshom.exception;

public class InvalidWordTypeException extends Exception{
    public InvalidWordTypeException() {
        super();
    }

    public InvalidWordTypeException(String message) {
        super(ExcelWorkerException.class.getName() + message);
    }
}