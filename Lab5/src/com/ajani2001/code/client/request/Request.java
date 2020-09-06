package com.ajani2001.code.client.request;

import java.io.Serializable;

public class Request implements Serializable {
    RequestType type;
    String parameter;

    public Request(RequestType type, String parameter) {
        this.type = type;
        this.parameter = parameter;
    }

    public RequestType getType() {
        return type;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }
}
