package com.ajani2001.code.server.response;

import com.ajani2001.code.server.fieldmodel.ColorGrid;

public class GameStateMessage extends Response {
    int myScore;
    int enemyScore;
    ColorGrid myField;
    ColorGrid enemyField;
    ColorGrid nextFigureField;

    public GameStateMessage(int myScore, int enemyScore, ColorGrid myField, ColorGrid enemyField, ColorGrid nextFigureField) {
        super(ResponseType.GAME_STATE);
        this.myScore = myScore;
        this.enemyScore = enemyScore;
        this.myField = (ColorGrid) myField.clone();
        this.enemyField = (ColorGrid) enemyField.clone();
        this.nextFigureField = (ColorGrid) nextFigureField.clone();
    }

    public int getMyScore() {
        return myScore;
    }

    public int getEnemyScore() {
        return enemyScore;
    }

    public ColorGrid getMyField() {
        return myField;
    }

    public ColorGrid getEnemyField() {
        return enemyField;
    }

    public ColorGrid getNextFigureField() {
        return nextFigureField;
    }
}
