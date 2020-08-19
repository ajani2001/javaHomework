package com.ajani2001.code;

import java.awt.*;

public class ColorGrid implements Cloneable {
    Color[][] grid;
    int width;
    int height;

    private ColorGrid(){}

    public ColorGrid(int width, int height) {
        this.width = width;
        this.height = height;
        grid = new Color[width][height];
    }

    public Color[][] getGrid() {
        return grid;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Object clone() {
        ColorGrid toReturn = new ColorGrid(width, height);
        toReturn.grid = new Color[width][height];
        for(int i = 0; i < width; ++i) {
            for(int j = 0; j < height; ++j) {
                toReturn.grid[i][j] = grid[i][j];
            }
        }

        return toReturn;
    }
}
