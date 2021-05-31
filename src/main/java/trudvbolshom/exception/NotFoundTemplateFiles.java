package trudvbolshom.exception;

public class NotFoundTemplateFiles extends RuntimeException {

    public NotFoundTemplateFiles() {
        super();
    }

    public NotFoundTemplateFiles(String message) {
        super(NotFoundTemplateFiles.class.getName() + message);
    }
}
