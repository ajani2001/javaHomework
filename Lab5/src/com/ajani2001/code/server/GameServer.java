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
        while(!Thread.interrupted() && isBound() && !isClosed()) { // am I right?
            int checkTimeoutMillis = 1000;
            if (players[0] == null) {
                try {
                    ClientModel newClient = new ClientModel(10, 20, this, accept());
                    synchronized (this) {
                        players[0] = newClient;
                        players[0].getConnection().sendConfig(players[0].getField().getGameField(), players[0].getField().getCurrentScore(), players[1] == null ? new ColorGrid(10, 20) : players[1].getField().getGameField(), players[1] == null ? 0 : players[1].getField().getCurrentScore(), figureGenerator.getCurrentFigureGrid(), scoreMap, infoAbout);
                    }
                } catch (IOException e) {
                    removeClient(players[0]);
                }
            }
            if (players[1] == null) {
                try {
                    ClientModel newClient = new ClientModel(10, 20, this, accept());
                    synchronized (this) {
                        players[1] = newClient;
                        players[1].getConnection().sendConfig(players[1].getField().getGameField(), players[1].getField().getCurrentScore(), players[0] == null ? new ColorGrid(10, 20) : players[0].getField().getGameField(), players[0] == null ? 0 : players[0].getField().getCurrentScore(), figureGenerator.getCurrentFigureGrid(), scoreMap, infoAbout);
                    }
                } catch (IOException e) {
                    removeClient(players[1]);
                }
            }
            try {
                Thread.sleep(checkTimeoutMillis);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    public ConnectionToClient accept() throws IOException {
        ConnectionToClient result = new ConnectionToClient();
        implAccept(result);
        result.init();
        return result;
    }

    public synchronized void notifyReady() {
        if(players[0] == null || players[1] == null) return;
        if(players[0].isReady() && players[1].isReady() && players[0].getField().isGameFinished() && players[1].getField().isGameFinished()) {
            players[0].getField().startNewGame();
            players[1].getField().startNewGame();
            players[0].resetReady();
            players[1].resetReady();
            gameTimer = new MyTimer(new Runnable() {
                int tickNumber = 0;
                @Override
                public void run() {
                    synchronized (GameServer.this) {
                        if (players[0] == null || players[1] == null) {
                            gameTimer.interrupt();
                            return;
                        }
                        if (players[0].getField().getCurrentFigure() == null && players[1].getField().getCurrentFigure() == null) {
                            if (tickNumber % 4 == 0) {
                                try {
                                    players[0].getField().spawnFigure((BlockFigure) figureGenerator.getCurrentFigure().clone());
                                    players[1].getField().spawnFigure((BlockFigure) figureGenerator.getCurrentFigure().clone());
                                } catch (CloneNotSupportedException impossible) {
                                }
                                if (players[0].getField().isGameFinished() && players[1].getField().isGameFinished()) {
                                    gameTimer.interrupt();
                                    return;
                                }
                                figureGenerator.nextFigure();
                                int currentMinScore = Integer.min(players[0].getField().getCurrentScore(), players[1].getField().getCurrentScore());
                                gameTimer.setDelayMillis((int) (250 * Math.pow(0.8, currentMinScore / 1000)));
                                notifyStateChanged();
                            }
                        } else {
                            players[0].notifyTimerTick(tickNumber);
                            players[1].notifyTimerTick(tickNumber);
                            notifyStateChanged();
                        }
                        ++tickNumber;
                    }
                }
            }, 250);
            gameTimer.start();
        }
    }

    public synchronized void notifyStateChanged() {
        if(players[0] == null || players[1] == null) return;
        int modelIndex = 0;
        try {
            players[0].getConnection().sendGameState(players[0].getField().getGameField(), players[0].getField().getCurrentScore(), players[1].getField().getGameField(), players[1].getField().getCurrentScore(), figureGenerator.getCurrentFigureGrid());
            modelIndex = 1;
            players[1].getConnection().sendGameState(players[1].getField().getGameField(), players[1].getField().getCurrentScore(), players[0].getField().getGameField(), players[0].getField().getCurrentScore(), figureGenerator.getCurrentFigureGrid());
        } catch (IOException e) {
            switch (modelIndex) {
                case 0 -> removeClient(players[0]);
                case 1 -> removeClient(players[1]);
            }
        }
    }

    public synchronized void saveScore(ClientModel model, String playerName) {
        int modelIndex = -1;
        try {
            if (!model.getField().isGameFinished()) {
                model.getConnection().sendPopupMessage("Game is not finished yet!");
                return;
            }
            if (model.getField().getCurrentScore() == 0) {
                model.getConnection().sendPopupMessage("You have no score!");
                return;
            }

            try (FileWriter scoreFileWriter = new FileWriter(scoreTableFileName)) {
                scoreMap.put(playerName, model.getField().getCurrentScore());
                Properties scoreMapSaver = new Properties();
                for (Map.Entry<String, Integer> scoreRecord : scoreMap.entrySet()) {
                    scoreMapSaver.setProperty(scoreRecord.getKey(), scoreRecord.getValue().toString());
                }
                scoreMapSaver.store(scoreFileWriter, null);
            } catch (IOException e) {
                e.printStackTrace();
                model.getConnection().sendPopupMessage("Unable to save :(");
            }

            if(players[0] != null) {
                modelIndex = 0;
                players[0].getConnection().sendScoreTable(scoreMap);
            }
            if(players[1] != null) {
                modelIndex = 1;
                players[1].getConnection().sendScoreTable(scoreMap);
            }
            modelIndex = -1;
            model.getConnection().sendPopupMessage("Saved!");
        } catch (IOException e) {
            switch (modelIndex) {
                case -1 -> removeClient(model);
                case  0 -> removeClient(players[0]);
                case  1 -> removeClient(players[1]);
            }
        }
    }

    public synchronized void removeClient(ClientModel client) { //?synchronized
        if(client==players[0]) {
            players[0] = null;
            gameTimer.interrupt();
        } else if(client==players[1]) {
            players[1] = null;
            gameTimer.interrupt();
        } else {
            return;
        }
        try {
            client.getConnection().close();
        } catch (IOException ignored) {}

        ClientModel anotherPlayer;
        if(players[0] != null) {
            anotherPlayer = players[0];
        } else if(players[1] != null) {
            anotherPlayer = players[1];
        } else {
            return;
        }
        try {
            anotherPlayer.getConnection().sendPopupMessage("Your opponent has disconnected!");
        } catch (IOException e) {
            removeClient(anotherPlayer);
        }
    }
}
