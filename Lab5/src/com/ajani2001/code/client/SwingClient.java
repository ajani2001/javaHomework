package com.ajani2001.code.client;

import com.ajani2001.code.server.fieldmodel.ColorGrid;
import com.ajani2001.code.server.response.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class SwingClient {
    Socket sockToServer;
    ObjectOutputStream sockOutput;
    Thread inputListenerThread;
    JFrame gameWindow;
    JPanel[][] myField;
    JPanel[][] enemyField;
    JPanel[][] nextFigureField;
    HashMap<String, Integer> scoreTable;
    String about;
    JLabel scoreField;
    JButton startButton;
    JButton highScoreButton;
    JButton aboutButton;
    JButton exitButton;

    public SwingClient(String serverAddress, int serverPort) {
        gameWindow = new JFrame("Tetris online");
        gameWindow.setSize(800, 600);
        gameWindow.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        gameWindow.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exit();
            }
        });
        gameWindow.getContentPane().setLayout(new GridBagLayout());

        JPanel myFieldPanel = new JPanel();
        JPanel enemyFieldPanel = new JPanel();
        JPanel nextFigurePanel = new JPanel();

        scoreField = new JLabel("Connecting..");

        startButton = new JButton("Start");
        startButton.setEnabled(false);
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendStartRequest();
            }
        });

        highScoreButton = new JButton("High scores");
        highScoreButton.setEnabled(false);
        highScoreButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showScoreTable();
            }
        });

        aboutButton = new JButton("About");
        aboutButton.setEnabled(false);
        aboutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showInfoAbout();
            }
        });

        exitButton = new JButton("Exit");
        exitButton.setEnabled(false);
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exit();
            }
        });

        GridBagConstraints constraints = new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0);
        constraints.gridheight = 8;
        gameWindow.getContentPane().add(myFieldPanel, constraints);
        constraints.gridx = 1;
        gameWindow.getContentPane().add(enemyFieldPanel, constraints);
        constraints.gridx = 2;
        constraints.gridheight = 1;
        gameWindow.getContentPane().add(scoreField, constraints);
        constraints.gridy = 1;
        gameWindow.getContentPane().add(nextFigurePanel, constraints);
        constraints.gridy = 2;
        gameWindow.getContentPane().add(startButton, constraints);
        constraints.gridy = 3;
        gameWindow.getContentPane().add(highScoreButton, constraints);
        constraints.gridy = 4;
        gameWindow.getContentPane().add(aboutButton, constraints);
        constraints.gridy = 5;
        gameWindow.getContentPane().add(exitButton, constraints);
        gameWindow.setVisible(true);

        sockToServer = new Socket(serverAddress, serverPort);
        sockOutput = new ObjectOutputStream(sockToServer.getOutputStream());

        inputListenerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                ObjectInputStream sockInput = new ObjectInputStream(sockToServer.getInputStream());
                while (!Thread.interrupted()) {
                    Response serverMessage = (Response) sockInput.readObject();
                    switch (serverMessage.getType()) {
                        case CONFIG_MESSAGE -> applyConfig((ConfigMessage) serverMessage);
                        case GAME_STATE -> updateGameState((GameStateMessage) serverMessage);
                        case INFO_ABOUT -> about = ((InfoAboutResponse) serverMessage).getInfoAbout();
                        case SCORE_TABLE -> scoreTable = ((ScoreTableResponse) serverMessage).getScoreMap();
                        case POPUP_MESSAGE -> showPopupWindow((PopupMessage) serverMessage);
                    }
                }
            }
        });
        inputListenerThread.start();

        startButton.setEnabled(true);
        highScoreButton.setEnabled(true);
        aboutButton.setEnabled(true);
        exitButton.setEnabled(true);


    }

    public void sendStartRequest() {
        synchronized (sockOutput) {
            sockOutput.writeObject(Request.START_GAME);
        }
    }

    public void showScoreTable() {
        JDialog popupWindow = new JDialog(gameWindow, "High scores", true);
        Object [][] tableData;
        synchronized (scoreTable) {
            tableData = new Object[scoreTable.size()][2];
            {
                int i = 0;
                for (Map.Entry<String, Integer> entry : scoreTable.entrySet()) {
                    tableData[i][0] = entry.getKey();
                    tableData[i][1] = entry.getValue();
                    ++i;
                }
            }
        }
        DefaultTableModel tableModel = new DefaultTableModel(tableData, new String[]{"Name", "Score"});
        TableRowSorter<DefaultTableModel> rowSorter = new TableRowSorter<>(tableModel);
        JTable table = new JTable(tableModel);
        table.setEnabled(false);
        table.setRowSorter(rowSorter);
        rowSorter.toggleSortOrder(1);
        rowSorter.toggleSortOrder(1);
        popupWindow.getContentPane().add(table);
        popupWindow.setVisible(true);
    }

    public void showInfoAbout() {
        JOptionPane.showMessageDialog(gameWindow, about, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    public void saveScore(String name) {
        Request saveRequest = Request.SAVE_SCORE;
        saveRequest.setParameter(name);
        synchronized (sockOutput) {
            sockOutput.writeObject(saveRequest);
        }
    }

    public void exit() {
        sockToServer.close();
        System.exit(0);
    }

    public void applyConfig(ConfigMessage config) {
        int myFieldWidth = config.getGameState().getMyField().getWidth();
        int myFieldHeight = config.getGameState().getMyField().getHeight();
        int enemyFieldWidth = config.getGameState().getEnemyField().getWidth();
        int enemyFieldHeight = config.getGameState().getEnemyField().getHeight();
        int nextFigurePanelWidth = config.getGameState().getNextFigureField().getWidth();
        int nextFigurePanelHeight = config.getGameState().getNextFigureField().getHeight();
        myField = new JPanel[myFieldWidth][myFieldHeight];
        for(int i = 0; i < myFieldWidth; ++i) {
            for(int j = 0; j < myFieldHeight; ++j) {
                myField[i][j] = new JPanel();
            }
        }
        enemyField = new JPanel[enemyFieldWidth][enemyFieldHeight];
        for(int i = 0; i < enemyFieldWidth; ++i) {
            for(int j = 0; j < enemyFieldHeight; ++j) {
                enemyField[i][j] = new JPanel();
            }
        }
        nextFigureField = new JPanel[nextFigurePanelWidth][nextFigurePanelHeight];
        for(int i = 0; i < nextFigurePanelWidth; ++i) {
            for(int j = 0; j < nextFigurePanelHeight; ++j) {
                myField[i][j] = new JPanel();
            }
        }
        scoreTable = config.getScoreTable().getScoreMap();
        about = config.getInfoAbout().getInfoAbout();
    }

    public void updateGameState(GameStateMessage newGameState) {
        updateFieldState(myField, newGameState.getMyField());
        updateFieldState(enemyField, newGameState.getEnemyField());
        updateFieldState(nextFigureField, newGameState.getNextFigureField());
        scoreField.setText("My score: "+newGameState.getMyScore()+System.lineSeparator()+"Enemy score: "+newGameState.getEnemyScore());
    }

    public void updateFieldState(JPanel[][] field, ColorGrid newState) {
        for(int i = 0; i < newState.getWidth(); ++i) {
            for(int j = 0; j < newState.getHeight(); ++j) {
                field[i][j].setBackground(newState.getGrid()[i][j]);
            }
        }
    }

    public void showPopupWindow(PopupMessage message) {
        JOptionPane.showMessageDialog(gameWindow, message.getMessage(), "Message", JOptionPane.INFORMATION_MESSAGE);
    }
}
