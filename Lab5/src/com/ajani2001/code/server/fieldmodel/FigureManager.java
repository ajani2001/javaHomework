package com.ajani2001.code.server.fieldmodel;

import com.ajani2001.code.server.exception.FigureConfigFileInvalidException;

import java.awt.*;
import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.Random;

public class FigureManager {
    ColorGrid figureGrid;
    ArrayList<BlockFigure> availableFigures;
    Random figuresRandomizer;
    BlockFigure currentFigure;

    public FigureManager(Reader configReader) throws FigureConfigFileInvalidException {
        figuresRandomizer = new Random(System.currentTimeMillis());
        StreamTokenizer parser = new StreamTokenizer(configReader);
        availableFigures = new ArrayList<>();
        ArrayList<Point> points = new ArrayList<>();
        try {
            for (int nextToken = parser.nextToken(); nextToken != StreamTokenizer.TT_EOF; nextToken = parser.nextToken()) {
                if (nextToken == StreamTokenizer.TT_WORD) {
                    boolean rotatable;
                    if (parser.sval.equalsIgnoreCase("nonrotatable")) {
                        rotatable = false;
                    } else if (parser.sval.equalsIgnoreCase("rotatable")) {
                        rotatable = true;
                    } else {
                        throw new FigureConfigFileInvalidException("Invalid config at line " + parser.lineno());
                    }
                    if (points.size() == 0) {
                        throw new FigureConfigFileInvalidException("Empty figure at line " + parser.lineno());
                    }
                    availableFigures.add(new BlockFigure(points.toArray(new Point[0]), rotatable));
                    points = new ArrayList<>();
                    continue;
                }
                int x, y;
                if (nextToken != StreamTokenizer.TT_NUMBER) {
                    throw new FigureConfigFileInvalidException("Invalid config at line " + parser.lineno());
                }
                x = (int) parser.nval;
                if (parser.nextToken() != StreamTokenizer.TT_NUMBER) {
                    throw new FigureConfigFileInvalidException("Invalid config at line " + parser.lineno());
                }
                y = (int) parser.nval;
                points.add(new Point(x, y));
            }
        } catch(IOException e) {
            throw new FigureConfigFileInvalidException("Invalid config at line " + parser.lineno());
        }
        nextFigure();
    }

    public BlockFigure getCurrentFigure() {
        return currentFigure;
    }

    public BlockFigure nextFigure() {
        int currentCode = figuresRandomizer.nextInt(availableFigures.size() * 4);
        int currentIndex = currentCode / 4;
        int currentRotation = currentCode % 4;
        Point[] clonedPointsArray = new Point[availableFigures.get(currentIndex).getPoints().length];
        for(int i = 0; i < clonedPointsArray.length; ++i) {
            clonedPointsArray[i] = (Point) availableFigures.get(currentIndex).getPoints()[i].clone();
        }
        currentFigure = new BlockFigure(clonedPointsArray, availableFigures.get(currentIndex).isRotatable());
        for(int i = 0; i < currentRotation; ++i) {
            currentFigure.rotate(true);
        }
        figureGrid = new ColorGrid(5, 5);
        for(int i = 0; i < currentFigure.getPoints().length; ++i) {
            figureGrid.grid[currentFigure.getPoints()[i].x + 2][currentFigure.getPoints()[i].y + 2] = currentFigure.getColor();
        }
        return currentFigure;
    }

    public ColorGrid getCurrentFigureGrid() {
        return figureGrid;
    }
}
