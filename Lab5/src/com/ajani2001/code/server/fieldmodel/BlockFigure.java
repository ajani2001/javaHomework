package com.ajani2001.code.server.fieldmodel;

import java.awt.*;
import java.util.Random;

public class BlockFigure implements Cloneable {
    Point[] points;
    boolean rotatable;
    Color color;
    static Random colorRandomizer;
    static {
        colorRandomizer = new Random(System.currentTimeMillis());
    }
    {
        Color[] availableColors = new Color[]{ Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.CYAN, Color.BLUE, Color.MAGENTA };
        color = availableColors[colorRandomizer.nextInt(availableColors.length)];
    }
    public BlockFigure(Point[] pointArray, boolean rotatable) {
        points = pointArray;
        this.rotatable = rotatable;
    }
    public void rotate(boolean clockwise) {
        if(!rotatable) return;
        for(Point point: points) {
            int tmp = point.y;
            point.y = point.x * (clockwise? -1 : 1);
            point.x = tmp * (clockwise? 1 : -1);
        }
    }

    public Point[] getPoints() {
        return points;
    }

    public Color getColor() {
        return color;
    }

    public boolean isRotatable() {
        return rotatable;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        BlockFigure result = (BlockFigure) super.clone();
        result.points = new Point[points.length];
        for(int i = 0; i < points.length; ++i) {
            result.points[i] = (Point) this.points[i].clone();
        }
        return result;
    }
}
