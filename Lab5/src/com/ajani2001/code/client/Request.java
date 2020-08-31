package com.ajani2001.code.client;

public enum Request {
    GET_CONFIG, GET_HIGHSCORE, GET_INFOABOUT, SAVE_SCORE, START_GAME, KEY_ACTION;
    String parameter;

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }
}
