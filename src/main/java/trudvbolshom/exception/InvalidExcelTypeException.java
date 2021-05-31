package trudvbolshom.exception;

public class InvalidExcelTypeException extends Exception {
    public InvalidExcelTypeException() {
        super();
    }

    public InvalidExcelTypeException(String message) {
        super(InvalidExcelTypeException.class.getName() + message);
    }
}