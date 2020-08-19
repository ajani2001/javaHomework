package com.ajani2001.code.exception;

public class FigureSpawnImpossibleException extends MyException {
    public FigureSpawnImpossibleException() {
    }

    public FigureSpawnImpossibleException(String message) {
        super(message);
    }

    public FigureSpawnImpossibleException(String message, Throwable cause) {
        super(message, cause);
    }

    public FigureSpawnImpossibleException(Throwable cause) {
        super(cause);
    }

    protected FigureSpawnImpossibleException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
