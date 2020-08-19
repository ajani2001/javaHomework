package com.ajani2001.code.exception;

public class ScoreTableFileInvalidException extends MyException {
    public ScoreTableFileInvalidException() {
    }

    public ScoreTableFileInvalidException(String message) {
        super(message);
    }

    public ScoreTableFileInvalidException(String message, Throwable cause) {
        super(message, cause);
    }

    public ScoreTableFileInvalidException(Throwable cause) {
        super(cause);
    }

    protected ScoreTableFileInvalidException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
