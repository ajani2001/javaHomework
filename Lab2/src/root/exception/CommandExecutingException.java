package root.exception;

public class CommandExecutingException extends MyException {
    public CommandExecutingException() {
    }

    public CommandExecutingException(String message) {
        super(message);
    }

    public CommandExecutingException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommandExecutingException(Throwable cause) {
        super(cause);
    }

    protected CommandExecutingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
