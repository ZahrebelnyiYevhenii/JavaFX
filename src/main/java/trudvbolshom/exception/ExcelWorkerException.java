package trudvbolshom.exception;

public class ExcelWorkerException extends Exception{
    public ExcelWorkerException() {
        super();
    }

    public ExcelWorkerException(String message) {
        super(ExcelWorkerException.class.getName() + message);
    }
}
