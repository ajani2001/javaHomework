package com.ajani2001.code.server.fieldmodel;

import java.awt.*;

public class FieldModel {
    ColorGrid grid;
    int currentScore;
    BlockFigure currentFigure;
    Point currentFigureOrigin;
    boolean gameFinished;

    public FieldModel(int width, int height) {
        grid = new ColorGrid(width, height);
        currentScore = 0;
        gameFinished = true;
    }

    public void startNewGame() {
        grid = new ColorGrid(grid.width, grid.height);
        currentScore = 0;
        currentFigure = null;
        currentFigureOrigin = null;
        gameFinished = false;
    }

    public void moveFigureLeft() {
        if(gameFinished || currentFigure == null) return;
        --currentFigureOrigin.x;
        if(!isCurrentFigureValid()) {
            ++currentFigureOrigin.x;
        }
    }

    public void moveFigureRight() {
        if(gameFinished || currentFigure == null) return;
        ++currentFigureOrigin.x;
        if(!isCurrentFigureValid()) {
            --currentFigureOrigin.x;
        }
    }

    public void moveFigureDown() {
        if(gameFinished || currentFigure == null) return;
        ++currentFigureOrigin.y;
        if(!isCurrentFigureValid()) {
            --currentFigureOrigin.y;
            overlayFigure(grid, currentFigure, currentFigureOrigin);
            currentFigure = null;
            currentFigureOrigin = null;
            clearRows();
        }
    }

    public void rotateFigure() {
        if(gameFinished || currentFigure == null) return;
        currentFigure.rotate(true);
        if(!isCurrentFigureValid()) {
            currentFigure.rotate(false);
        }
    }

    public void pushFigureToBottom() {
        if(gameFinished || currentFigure == null) return;
        while (isCurrentFigureValid()) {
            ++currentFigureOrigin.y;
        }
        --currentFigureOrigin.y;
    }

    void clearRows() {
        Color[][] rawGrid = grid.getGrid();
        int rowsCleared = 0;
        for(int y = grid.height-1; y >= 0; --y) {
            while (isRowCompleted(y)) {
                for(int y1 = y; y1 >= 0; --y1) {
                    for(int x = 0; x < grid.width; ++x) {
                        rawGrid[x][y1] = (y1 > 0? rawGrid[x][y1-1] : null);
                    }
                }
                ++rowsCleared;
            }
        }
        currentScore += rowsCleared * rowsCleared * 100;
    }

    boolean isRowCompleted(int rowIndex) {
        for(int x = 0; x < grid.width; ++x) {
            if(grid.getGrid()[x][rowIndex] == null) {
                return false;
            }
        }
        return true;
    }

    boolean isCurrentFigureValid() {
        for(Point p: currentFigure.getPoints()) {
            int globalX = currentFigureOrigin.x + p.x;
            int globalY = currentFigureOrigin.y + p.y;
            if(     globalX < 0 ||
                    globalX >= grid.getWidth() ||
                    globalY >= grid.getHeight() ||
                    (globalY >= 0 && grid.getGrid()[globalX][globalY] != null ) ) {
                return false;
            }
        }
        return true;
    }

    void overlayFigure (ColorGrid target, BlockFigure figure, Point origin) {
        for(Point p: figure.getPoints()) {
            try {
                target.getGrid()[origin.x + p.x][origin.y + p.y] = figure.getColor();
            }
            catch (IndexOutOfBoundsException ignore){}
        }
    }

    public ColorGrid getGameField() {
        ColorGrid currentView = (ColorGrid) grid.clone();
        if(gameFinished || currentFigure == null) return currentView;
        overlayFigure(currentView, currentFigure, currentFigureOrigin);
        return  currentView;
    }

    public int getCurrentScore() {
        return currentScore;
    }

    public BlockFigure getCurrentFigure() {
        return currentFigure;
    }

    public boolean isGameFinished() {
        return gameFinished;
    }

    public void spawnFigure(BlockFigure figure) {
        if(gameFinished) return;
        currentFigure = figure;
        currentFigureOrigin = new Point(grid.width/2, -1);
        if(!isCurrentFigureValid()) {
            currentFigure = null;
            currentFigureOrigin = null;
            gameFinished = true;
        }
    }
}

