package com.ajani2001.code.server.model;

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
        ColorGrid result;
        try {
            result = (ColorGrid) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
        result.grid = new Color[width][height];
        for(int i = 0; i < width; ++i) {
            for(int j = 0; j < height; ++j) {
                result.grid[i][j] = this.grid[i][j];
            }
        }
        return result;
    }
}