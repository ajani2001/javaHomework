package com.ajani2001.code.server.response;

public class ConfigMessage extends Response {
    GameStateMessage gameState;
    InfoAboutResponse infoAbout;
    ScoreTableResponse scoreTable;

    public ConfigMessage(GameStateMessage gameState, InfoAboutResponse infoAbout, ScoreTableResponse scoreTable) {
        super(ResponseType.CONFIG_MESSAGE);
        this.gameState = gameState;
        this.infoAbout = infoAbout;
        this.scoreTable = scoreTable;
    }

    public GameStateMessage getGameState() {
        return gameState;
    }

    public InfoAboutResponse getInfoAbout() {
        return infoAbout;
    }

    public ScoreTableResponse getScoreTable() {
        return scoreTable;
    }
}
