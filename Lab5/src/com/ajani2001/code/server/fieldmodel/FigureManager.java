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
    int currentCode;

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

    public BlockFigure currentFigure() {
        try {
            BlockFigure result = (BlockFigure) availableFigures.get(currentCode / 4).clone();
            for(int i = 0; i < currentCode%4; ++i) {
                result.rotate(true);
            }
            return result;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public BlockFigure nextFigure() {
        currentCode = figuresRandomizer.nextInt(availableFigures.size() * 4);
        figureGrid = new ColorGrid(5, 5);
        BlockFigure currentFigure = currentFigure();
        for(int i = 0; i < currentFigure.points.length; ++i) {
            figureGrid.grid[currentFigure.points[i].x + 2][currentFigure.points[i].y + 2] = currentFigure.color;
        }
        return currentFigure;
    }

    public ColorGrid getCurrentFigureGrid() {
        return figureGrid;
    }
}
