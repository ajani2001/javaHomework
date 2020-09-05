package com.ajani2001.code.server.exception;

public class FigureConfigFileInvalidException extends Exception {
    public FigureConfigFileInvalidException() {
    }

    public FigureConfigFileInvalidException(String message) {
        super(message);
    }

    public FigureConfigFileInvalidException(String message, Throwable cause) {
        super(message, cause);
    }

    public FigureConfigFileInvalidException(Throwable cause) {
        super(cause);
    }

    protected FigureConfigFileInvalidException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
