package root.exception;

public class FactoryConfigInvalidException extends MyException {
    public FactoryConfigInvalidException() {
    }

    public FactoryConfigInvalidException(String message) {
        super(message);
    }

    public FactoryConfigInvalidException(String message, Throwable cause) {
        super(message, cause);
    }

    public FactoryConfigInvalidException(Throwable cause) {
        super(cause);
    }

    protected FactoryConfigInvalidException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
