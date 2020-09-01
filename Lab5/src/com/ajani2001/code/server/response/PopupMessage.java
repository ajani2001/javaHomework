package com.ajani2001.code.server.response;

public class PopupMessage extends Response {
    String message;

    public PopupMessage(String message) {
        super(ResponseType.POPUP_MESSAGE);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
