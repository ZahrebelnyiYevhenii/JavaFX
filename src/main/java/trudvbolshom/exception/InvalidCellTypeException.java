package trudvbolshom.exception;

public class InvalidCellTypeException extends Exception{
    public InvalidCellTypeException() {
        super();
    }

    public InvalidCellTypeException(String message) {
        super(ExcelWorkerException.class.getName() + message);
    }
}