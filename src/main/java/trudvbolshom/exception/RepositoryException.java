package trudvbolshom.exception;

public class RepositoryException extends RuntimeException {

    public RepositoryException() {
        super();
    }

    public RepositoryException(String message) {
        super(NotFoundTemplateFiles.class.getName() + message);
    }
}
