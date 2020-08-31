package com.ajani2001.code.server.response;

public class InfoAboutResponse extends Response {
    String infoAbout;

    public InfoAboutResponse(String infoAbout) {
        super(ResponseType.INFO_ABOUT);
        this.infoAbout = infoAbout;
    }

    public String getInfoAbout() {
        return infoAbout;
    }
}
