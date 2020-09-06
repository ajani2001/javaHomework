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
import java.util.HashMap;
import java.util.Map;

public class SwingClient {
    GameClientSocket sockToServer;
    Thread inputListenerThread;
    HashMap<String, Integer> scoreTable;
    String about;
    JFrame gameWindow;
    JPanel myFieldPanel;
    JPanel enemyFieldPanel;
    JPanel nextFigurePanel;
    JPanel[][] myField;
    JPanel[][] enemyField;
    JPanel[][] nextFigureField;
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
                exitGame();
            }
        });
        gameWindow.getContentPane().setLayout(new GridBagLayout());

        myFieldPanel = new JPanel();
        myFieldPanel.setBorder(BorderFactory.createRaisedBevelBorder());
        enemyFieldPanel = new JPanel();
        enemyFieldPanel.setBorder(BorderFactory.createRaisedBevelBorder());
        nextFigurePanel = new JPanel();
        nextFigurePanel.setBorder(BorderFactory.createRaisedBevelBorder());

        scoreField = new JLabel("Connecting..");

        startButton = new JButton("Start");
        startButton.setEnabled(false);
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    sockToServer.sendStartRequest();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                    showPopupWindow("I/O problems..");
                }
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
                exitGame();
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

        try {
            sockToServer = new GameClientSocket(serverAddress, serverPort);
        } catch (IOException e) {
            e.printStackTrace();
            showPopupWindow("Unable to connect to server");
            return;
        }

        inputListenerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!Thread.interrupted()) {
                    Response serverMessage = null;
                    try {
                        serverMessage = sockToServer.receiveResponse();
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    switch (serverMessage.getType()) {
                        case CONFIG_MESSAGE -> applyConfig((ConfigMessage) serverMessage);
                        case GAME_STATE -> updateGameState((GameStateMessage) serverMessage);
                        case INFO_ABOUT -> about = ((InfoAboutResponse) serverMessage).getInfoAbout();
                        case SCORE_TABLE -> scoreTable = ((ScoreTableResponse) serverMessage).getScoreMap();
                        case POPUP_MESSAGE -> showPopupWindow(((PopupMessage) serverMessage).getMessage());
                    }
                }
            }
        });
        inputListenerThread.start();

        myFieldPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ENTER"), "toBottom");
        myFieldPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("LEFT"), "moveLeft");
        myFieldPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("RIGHT"), "moveRight");
        myFieldPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("UP"), "rotate");
        myFieldPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("pressed DOWN"), "speedUpEnable");
        myFieldPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released DOWN"), "speedUpDisable");
        myFieldPanel.getActionMap().put("toBottom", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    sockToServer.sendKeyAction("toBottom");
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                    showPopupWindow("I/O problems..");
                }
            }
        });
        myFieldPanel.getActionMap().put("moveLeft", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    sockToServer.sendKeyAction("moveLeft");
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                    showPopupWindow("I/O problems..");
                }
            }
        });
        myFieldPanel.getActionMap().put("moveRight", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    sockToServer.sendKeyAction("moveRight");
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                    showPopupWindow("I/O problems..");
                }
            }
        });
        myFieldPanel.getActionMap().put("rotate", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    sockToServer.sendKeyAction("rotate");
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                    showPopupWindow("I/O problems..");
                }
            }
        });
        myFieldPanel.getActionMap().put("speedUpEnable", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    sockToServer.sendKeyAction("speedUpEnable");
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                    showPopupWindow("I/O problems..");
                }
            }
        });
        myFieldPanel.getActionMap().put("speedUpDisable", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    sockToServer.sendKeyAction("speedUpDisable");
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                    showPopupWindow("I/O problems..");
                }
            }
        });

        startButton.setEnabled(true);
        highScoreButton.setEnabled(true);
        aboutButton.setEnabled(true);
        exitButton.setEnabled(true);
    }

    public void showScoreTable() {
        JDialog popupWindow = new JDialog(gameWindow, "High scores", true);
        Object [][] tableData;
        HashMap<String, Integer> currentScoreTableReference = scoreTable;
        tableData = new Object[currentScoreTableReference.size()][2];
        {
            int i = 0;
            for (Map.Entry<String, Integer> entry : currentScoreTableReference.entrySet()) {
                tableData[i][0] = entry.getKey();
                tableData[i][1] = entry.getValue();
                ++i;
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

    public void exitGame() {
        inputListenerThread.interrupt();
        try {
            sockToServer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

    public void applyConfig(ConfigMessage config) {
        int myFieldWidth = config.getGameState().getMyField().getWidth();
        int myFieldHeight = config.getGameState().getMyField().getHeight();
        int enemyFieldWidth = config.getGameState().getEnemyField().getWidth();
        int enemyFieldHeight = config.getGameState().getEnemyField().getHeight();
        int nextFigurePanelWidth = config.getGameState().getNextFigureField().getWidth();
        int nextFigurePanelHeight = config.getGameState().getNextFigureField().getHeight();

        myFieldPanel.removeAll();
        myFieldPanel.setLayout(new GridLayout(myFieldHeight, myFieldWidth));
        myField = new JPanel[myFieldWidth][myFieldHeight];
        for(int i = 0; i < myFieldHeight; ++i) {
            for(int j = 0; j < myFieldWidth; ++j) {
                JPanel cell = new JPanel();
                cell.setBorder(BorderFactory.createEtchedBorder());
                myField[j][i] = cell;
                myFieldPanel.add(cell);
            }
        }
        myFieldPanel.validate();

        enemyFieldPanel.removeAll();
        enemyFieldPanel.setLayout(new GridLayout(enemyFieldHeight, enemyFieldWidth));
        enemyField = new JPanel[enemyFieldWidth][enemyFieldHeight];
        for(int i = 0; i < enemyFieldHeight; ++i) {
            for(int j = 0; j < enemyFieldWidth; ++j) {
                JPanel cell = new JPanel();
                cell.setBorder(BorderFactory.createEtchedBorder());
                enemyField[j][i] = cell;
                enemyFieldPanel.add(cell);
            }
        }
        enemyFieldPanel.validate();

        nextFigurePanel.removeAll();
        nextFigurePanel.setLayout(new GridLayout(nextFigurePanelHeight, nextFigurePanelWidth));
        nextFigureField = new JPanel[nextFigurePanelWidth][nextFigurePanelHeight];
        for(int i = 0; i < nextFigurePanelHeight; ++i) {
            for(int j = 0; j < nextFigurePanelWidth; ++j) {
                JPanel cell = new JPanel();
                cell.setBorder(BorderFactory.createEtchedBorder());
                nextFigureField[j][i] = cell;
                nextFigurePanel.add(cell);
            }
        }
        nextFigurePanel.validate();

        updateGameState(config.getGameState());
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

    public void showPopupWindow(String text) {
        JOptionPane.showMessageDialog(gameWindow, text, "Message", JOptionPane.INFORMATION_MESSAGE);
    }
}
