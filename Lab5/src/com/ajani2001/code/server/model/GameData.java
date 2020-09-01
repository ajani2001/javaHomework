package com.ajani2001.code.server.model;

import java.awt.*;
import java.io.*;
import java.util.*;

public class GameData {
    ColorGrid grid;
    String scoreTableFileName;
    SortedSet<Map.Entry<String, Integer>> scoreTable;
    int currentScore;
    String infoAbout;
    BlockFigure[] availableFigures;
    Random figuresRandomizer;
    BlockFigure nextFigure;
    BlockFigure currentFigure;
    Point currentFigureOrigin;
    boolean gameFinished;
    ArrayList<GUI> subscribers;
    Timer gameTimer;
    int currentNormalSpeed;
    boolean highSpeed;

    public GameData(int width, int height, @NotNull String figuresFileName, String scoreTableFileName, String infoFileName) throws MyException {
        grid = new ColorGrid(width, height);
        this.scoreTableFileName = scoreTableFileName;
        if(scoreTableFileName != null) {
            loadScoreTable(scoreTableFileName);
        }
        currentScore = 0;
        if(infoFileName != null) {
            loadInfoAbout(infoFileName);
        }
        loadFigures(figuresFileName);
        figuresRandomizer = new Random(System.currentTimeMillis());
        gameFinished = true;
        subscribers = new ArrayList<>();
    }

    void loadFigures(String figuresFileName) throws FigureConfigFileInvalidException {
        try(FileReader figuresFile = new FileReader(figuresFileName)) {
            StreamTokenizer parser = new StreamTokenizer(figuresFile);
            ArrayList<BlockFigure> figures = new ArrayList<>();
            ArrayList<Point> points = new ArrayList<>();
            for (int nextToken = parser.nextToken(); nextToken != StreamTokenizer.TT_EOF; nextToken = parser.nextToken()) {
                if (nextToken == StreamTokenizer.TT_WORD) {
                    boolean rotatable;
                    if(parser.sval.equalsIgnoreCase("nonrotatable")) {
                        rotatable = false;
                    } else if(parser.sval.equalsIgnoreCase("rotatable")) {
                        rotatable = true;
                    } else {
                        throw new FigureConfigFileInvalidException("Invalid config at line " + parser.lineno());
                    }
                    if(points.size() == 0) {
                        throw new FigureConfigFileInvalidException("Empty figure at line " + parser.lineno());
                    }
                    figures.add(new BlockFigure(points.toArray(new Point[0]), rotatable));
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
            availableFigures = figures.toArray(new BlockFigure[0]);
        }
        catch (IOException e) {
            throw new FigureConfigFileInvalidException(e);
        }
    }

    void loadInfoAbout(String infoFileName) {
        try(BufferedReader infoFile = new BufferedReader(new FileReader(infoFileName))) {
            StringBuilder stringBuilder = new StringBuilder();
            for(String nextLine = infoFile.readLine(); nextLine != null; nextLine = infoFile.readLine()) {
                stringBuilder.append(nextLine);
                stringBuilder.append(System.getProperty("line.separator"));
            }
            infoAbout = stringBuilder.toString();
        }
        catch (IOException e) {
            System.err.println("Info file: " + e.getMessage());
        }
    }

    void loadScoreTable(String scoreTableFileName) {
        scoreTable = new TreeSet<>(new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                if(Integer.compare(o1.getValue(), o2.getValue()) == 0) {
                    return o1.getKey().compareToIgnoreCase(o2.getKey());
                }
                return Integer.compare(o2.getValue(), o1.getValue());
            }
        });
        try(FileReader scoreTableFile = new FileReader(scoreTableFileName)) {
            StreamTokenizer parser = new StreamTokenizer(scoreTableFile);
            parser.resetSyntax();
            parser.wordChars(1, 255);
            parser.whitespaceChars(' ', ' ');
            parser.whitespaceChars('\r', '\r');
            parser.whitespaceChars('\n', '\n');
            parser.eolIsSignificant(true);
            for(int tokenType = parser.nextToken(); tokenType != StreamTokenizer.TT_EOF; tokenType = parser.nextToken()) {
                if(tokenType != StreamTokenizer.TT_WORD) {
                    throw new ScoreTableFileInvalidException("Unexpected token at line " + parser.lineno());
                }
                String playerName = parser.sval;
                if(parser.nextToken() != StreamTokenizer.TT_WORD) {
                    throw new ScoreTableFileInvalidException("Unexpected token at line " + parser.lineno());
                }
                Integer playerScore = Integer.parseInt(parser.sval);
                if(parser.nextToken() == StreamTokenizer.TT_WORD) {
                    throw new ScoreTableFileInvalidException("Unexpected token at line " + parser.lineno());
                }
                scoreTable.add(new AbstractMap.SimpleEntry<String, Integer>(playerName, playerScore));
            }
        }
        catch (IOException | ScoreTableFileInvalidException e) {
            System.err.println("Score Table file: " + e.getMessage());
        }
    }

    public void startNewGame() {
        grid = new ColorGrid(grid.width, grid.height);
        currentScore = 0;
        currentFigure = null;
        currentFigureOrigin = null;
        nextFigure = generateRandomFigure();
        gameFinished = false;
        currentNormalSpeed = 1000;
        if(gameTimer != null) {
            gameTimer.interrupt();
        }
        gameTimer = new Timer(this, currentNormalSpeed);
        gameTimer.start();
        redrawAll();
    }

    public synchronized void moveFigureLeft() {
        if(gameFinished || currentFigure == null) return;
        --currentFigureOrigin.x;
        if(!isCurrentFigureValid()) {
            ++currentFigureOrigin.x;
        }
        redrawAll();
    }

    public void saveScore(String playerName) {
        if(currentScore == 0 || !gameFinished) return;
        try(FileWriter scoreTableFile = new FileWriter(scoreTableFileName, true)) {
            scoreTableFile.append(System.lineSeparator()).append(playerName).append(" ").append(String.valueOf(currentScore));
            currentScore = 0;
        }
        catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public synchronized void moveFigureRight() {
        if(gameFinished || currentFigure == null) return;
        ++currentFigureOrigin.x;
        if(!isCurrentFigureValid()) {
            --currentFigureOrigin.x;
        }
        redrawAll();
    }

    public synchronized void moveFigureDown() {
        if(gameFinished) return;
        if(currentFigure == null) {
            try {
                spawnNewFigure();
            }
            catch (FigureSpawnImpossibleException e) {
                gameFinished = true;
            }
        } else {
            ++currentFigureOrigin.y;
            if(!isCurrentFigureValid()) {
                --currentFigureOrigin.y;
                overlayFigure(grid, currentFigure, currentFigureOrigin);
                currentFigure = null;
                currentFigureOrigin = null;
                clearRows();
            }
        }
        redrawAll();
    }

    public synchronized void rotateFigure() {
        if(gameFinished || currentFigure == null) return;
        currentFigure.rotate(true);
        if(!isCurrentFigureValid()) {
            currentFigure.rotate(false);
        }
        redrawAll();
    }

    public synchronized void pushFigureToBottom() {
        if(gameFinished || currentFigure == null) return;
        while (isCurrentFigureValid()) {
            ++currentFigureOrigin.y;
        }
        --currentFigureOrigin.y;
        overlayFigure(grid, currentFigure, currentFigureOrigin);
        currentFigure = null;
        currentFigureOrigin = null;
        clearRows();
        redrawAll();
    }

    public String getInfoAbout() {
        return infoAbout;
    }

    public String getHighScore(int entryNumber) {
        loadScoreTable(scoreTableFileName);
        StringBuilder builder = new StringBuilder();
        Iterator<Map.Entry<String, Integer>> entryIterator = scoreTable.iterator();
        for(int i = 0; i < entryNumber; ++i) {
            if(!entryIterator.hasNext()) break;
            Map.Entry<String, Integer> nextEntry = (Map.Entry<String, Integer>) entryIterator.next();
            builder.append(nextEntry.getKey()).append(": ").append(nextEntry.getValue()).append(System.lineSeparator());
        }
        return builder.toString();
    }

    public void speedUp(boolean set) {
        if(gameFinished) return;
        highSpeed = set;
        if(set) {
            gameTimer.setDelayMillis(currentNormalSpeed / 4);
        } else {
            gameTimer.setDelayMillis(currentNormalSpeed);
        }
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
        currentNormalSpeed = (int) (1000 * Math.pow(0.8, currentScore/1000));
        gameTimer.setDelayMillis((highSpeed? currentNormalSpeed/4 : currentNormalSpeed));
    }

    boolean isRowCompleted(int rowIndex) {
        for(int x = 0; x < grid.width; ++x) {
            if(grid.getGrid()[x][rowIndex] == null) {
                return false;
            }
        }
        return true;
    }

    private boolean isCurrentFigureValid() {
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

    private void overlayFigure (ColorGrid target, BlockFigure figure, Point origin) {
        for(Point p: figure.getPoints()) {
            try {
                target.getGrid()[origin.x + p.x][origin.y + p.y] = figure.getColor();
            }
            catch (IndexOutOfBoundsException ignore){}
        }
    }

    private void spawnNewFigure() throws FigureSpawnImpossibleException {
        currentFigure = nextFigure;
        currentFigureOrigin = new Point(grid.width/2, -1);
        nextFigure = generateRandomFigure();
        if(!isCurrentFigureValid()) {
            throw new FigureSpawnImpossibleException();
        }
    }
    BlockFigure generateRandomFigure() {
        int randomInt = figuresRandomizer.nextInt(availableFigures.length * 4);
        BlockFigure randomFigure = (BlockFigure) availableFigures[randomInt % availableFigures.length].clone();
        for(int i = 0; i < randomInt / availableFigures.length; ++i) {
            randomFigure.rotate(true);
        }
        return randomFigure;
    }
    public void subscribe(GUI subscriber) {
        subscribers.add(subscriber);
    }
    void redrawAll() {
        for(GUI subscriber: subscribers) {
            subscriber.redraw();
        }
    }
    public synchronized ColorGrid getGameField() {
        ColorGrid currentView = (ColorGrid) grid.clone();
        if(gameFinished || currentFigure == null) return currentView;
        overlayFigure(currentView, currentFigure, currentFigureOrigin);
        return  currentView;
    }
    public synchronized ColorGrid getNextFigure() {
        ColorGrid nextFigureGrid = new ColorGrid(5, 5);
        if(gameFinished) return nextFigureGrid;
        overlayFigure(nextFigureGrid, nextFigure, new Point(2, 2));
        return nextFigureGrid;
    }
    public int getCurrentScore() {
        return currentScore;
    }
}

class BlockFigure implements Cloneable {
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

    @Override
    public Object clone() {
        ArrayList<Point> newPointList = new ArrayList<>();
        for(Point p: points) {
            newPointList.add((Point) p.clone());
        }
        return new BlockFigure(newPointList.toArray(new Point[0]), rotatable);
    }
}
