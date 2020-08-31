package com.ajani2001.code.server.response;

import java.io.Serializable;

public abstract class Response implements Serializable {
    ResponseType type;

    public Response(ResponseType type) {
        this.type = type;
    }

    public ResponseType getType() {
        return type;
    }
}