package com.ajani2001.code.server.response;

import java.util.HashMap;

public class ScoreTableResponse extends Response {
    HashMap<String, Integer> scoreMap;

    public ScoreTableResponse(HashMap<String, Integer> scoreMap) {
        super(ResponseType.SCORE_TABLE);
        this.scoreMap = scoreMap;
    }

    public HashMap<String, Integer> getScoreMap() {
        return scoreMap;
    }
}
