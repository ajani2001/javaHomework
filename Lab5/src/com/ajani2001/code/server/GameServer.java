package com.ajani2001.code.server;

import com.ajani2001.code.server.exception.FigureConfigFileInvalidException;
import com.ajani2001.code.server.fieldmodel.ColorGrid;
import com.ajani2001.code.server.fieldmodel.FigureManager;
import com.ajani2001.code.server.fieldmodel.BlockFigure;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class GameServer extends ServerSocket implements Runnable {
    MyTimer gameTimer;
    ClientModel[] players;
    FigureManager figureGenerator;
    HashMap<String, Integer> scoreMap;
    String infoAbout;
    String scoreTableFileName = "scoretable.txt";
    String infoFileName = "about.txt";
    String figureConfigFileName = "figures.txt";

    public GameServer(int port) throws FigureConfigFileInvalidException, IOException {
        super(port);
        Properties scoreTable = new Properties();
        scoreMap = new HashMap<>();
        try(FileReader figureConfigReader = new FileReader(figureConfigFileName)) {
            figureGenerator = new FigureManager(figureConfigReader);
        } catch (IOException e) {
            throw new FigureConfigFileInvalidException(e);
        }
        try(FileReader propertiesReader = new FileReader(scoreTableFileName)) {
            scoreTable.load(propertiesReader);
            for(Map.Entry<Object, Object> property: scoreTable.entrySet()) {
                scoreMap.put( (String) property.getKey(), Integer.parseInt( (String) property.getValue()));
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Score file: " + e.getMessage());
            scoreMap = new HashMap<>();
        }
        try(BufferedReader infoAboutReader = new BufferedReader(new FileReader(infoFileName))) {
            StringBuilder stringBuilder = new StringBuilder();
            for(String nextLine = infoAboutReader.readLine(); nextLine != null; nextLine = infoAboutReader.readLine()) {
                stringBuilder.append(nextLine);
                stringBuilder.append(System.lineSeparator());
            }
            infoAbout = stringBuilder.toString();
        } catch (IOException e) {
            System.err.println("Info file: " + e.getMessage());
            infoAbout = "";
        }
        players = new ClientModel[2];
    }

    public void run() {
        while(!Thread.interrupted()) {
            int checkTimeoutMillis = 1000;
            if(players[0] == null) {
                players[0] = new ClientModel(10, 20, this, accept());
                try {
                    players[0].getConnection().sendConfig(players[0].getField().getGameField(), players[0].getField().getCurrentScore(), players[1]==null?new ColorGrid(10, 20):players[1].getField().getGameField(), players[1]==null?0:players[1].getField().getCurrentScore(), figureGenerator.getCurrentFigureGrid(), scoreMap, infoAbout);
                } catch (IOException e) {
                    e.printStackTrace();
                    try {
                        players[0].getConnection().close();
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                    players[0] = null;
                    continue;
                }
            }
            if (!players[0].getConnection().isConnected()) {
                try {
                    players[0].getConnection().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                players[0] = new ClientModel(10, 20, this, accept());
                try {
                    players[0].getConnection().sendConfig(players[0].getField().getGameField(), players[0].getField().getCurrentScore(), players[1]==null?new ColorGrid(10, 20):players[1].getField().getGameField(), players[1]==null?0:players[1].getField().getCurrentScore(), figureGenerator.getCurrentFigureGrid(), scoreMap, infoAbout);
                } catch (IOException e) {
                    e.printStackTrace();
                    try {
                        players[0].getConnection().close();
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                    players[0] = null;
                }
            }
            if(players[1] == null) {
                players[1] = new ClientModel(10, 20, this, accept());
                try {
                    players[1].getConnection().sendConfig(players[1].getField().getGameField(), players[1].getField().getCurrentScore(), players[0]==null?new ColorGrid(10, 20):players[0].getField().getGameField(), players[0]==null?0:players[0].getField().getCurrentScore(), figureGenerator.getCurrentFigureGrid(), scoreMap, infoAbout);
                } catch (IOException e) {
                    e.printStackTrace();
                    try {
                        players[1].getConnection().close();
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                    players[1] = null;
                    continue;
                }
            }
            if (!players[1].getConnection().isConnected()) {
                try {
                    players[1].getConnection().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                players[1] = new ClientModel(10, 20, this, accept());
                try {
                    players[1].getConnection().sendConfig(players[1].getField().getGameField(), players[1].getField().getCurrentScore(), players[0]==null?new ColorGrid(10, 20):players[0].getField().getGameField(), players[0]==null?0:players[0].getField().getCurrentScore(), figureGenerator.getCurrentFigureGrid(), scoreMap, infoAbout);
                } catch (IOException e) {
                    e.printStackTrace();
                    try {
                        players[1].getConnection().close();
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                    players[1] = null;
                }
            }
            try {
                Thread.sleep(checkTimeoutMillis);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    public ConnectionToClient accept() {
        while(true) {
            try {
                ConnectionToClient result = new ConnectionToClient();
                implAccept(result);
                result.init();
                return result;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void notifyReady() {
        if(players[0] == null || players[1] == null) return;
        if(players[0].isReady() && players[1].isReady() && players[0].getField().isGameFinished() && players[1].getField().isGameFinished()) {
            players[0].getField().startNewGame();
            players[1].getField().startNewGame();
            gameTimer = new MyTimer(new Runnable() {
                int tickNumber = 0;
                @Override
                public void run() {
                    if(players[0].getField().getCurrentFigure() == null && players[1].getField().getCurrentFigure() == null) {
                        if(tickNumber%2 != 0) return;
                        try {
                            players[0].getField().spawnFigure((BlockFigure) figureGenerator.getCurrentFigure().clone());
                        } catch (CloneNotSupportedException impossible) {}
                        try {
                            players[1].getField().spawnFigure((BlockFigure) figureGenerator.getCurrentFigure().clone());
                        } catch (CloneNotSupportedException impossible) {}
                        figureGenerator.nextFigure();
                        if(players[0].getField().isGameFinished() && players[1].getField().isGameFinished()) {
                            gameTimer.interrupt();
                        }
                    } else {
                        players[0].notifyTimerTick(tickNumber);
                        players[1].notifyTimerTick(tickNumber);
                    }
                    int currentMinScore = Integer.min(players[0].getField().getCurrentScore(), players[1].getField().getCurrentScore());
                    gameTimer.setDelayMillis((int) (500 * Math.pow(0.8, currentMinScore/1000)));
                    notifyStateChanged();
                    ++tickNumber;
                }
            }, 500);
            gameTimer.start();
        }
    }

    public void notifyStateChanged() {
        if(players[0] == null || players[1] == null) return;
        try {
            players[0].getConnection().sendGameState(players[0].getField().getGameField(), players[0].getField().getCurrentScore(), players[1].getField().getGameField(), players[1].getField().getCurrentScore(), figureGenerator.getCurrentFigureGrid());
            players[1].getConnection().sendGameState(players[1].getField().getGameField(), players[1].getField().getCurrentScore(), players[0].getField().getGameField(), players[0].getField().getCurrentScore(), figureGenerator.getCurrentFigureGrid());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveScore(ClientModel model, String playerName) {
        try(FileWriter scoreFileWriter = new FileWriter(scoreTableFileName)) {
            if (!model.getField().isGameFinished()) {
                model.getConnection().sendPopupMessage("Game is not finished yet!");
                scoreFileWriter.close();
                return;
            }
            if (model.getField().getCurrentScore() == 0) {
                model.getConnection().sendPopupMessage("You have no score!");
                scoreFileWriter.close();
                return;
            }
            scoreMap.put(playerName, model.getField().getCurrentScore());
            Properties scoreMapSaver = new Properties();
            for (Map.Entry<String, Integer> scoreRecord : scoreMap.entrySet()) {
                scoreMapSaver.setProperty(scoreRecord.getKey(), scoreRecord.getValue().toString());
            }
            scoreMapSaver.store(scoreFileWriter, null);
            players[0].getConnection().sendScoreTable(scoreMap);
            players[1].getConnection().sendScoreTable(scoreMap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
