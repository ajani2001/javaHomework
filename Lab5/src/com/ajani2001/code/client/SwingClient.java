package com.ajani2001.code.client;

import com.ajani2001.code.server.response.ConfigMessage;

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
    ObjectInputStream sockInput;
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
                try {
                    sockToServer.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                System.exit(0);
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
                synchronized (sockOutput) {
                    sockOutput.writeObject(Request.START_GAME);
                }
            }
        });

        highScoreButton = new JButton("High scores");
        highScoreButton.setEnabled(false);
        highScoreButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        aboutButton = new JButton("About");
        aboutButton.setEnabled(false);
        aboutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        exitButton = new JButton("Exit");
        exitButton.setEnabled(false);
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

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
        ConfigMessage config = (ConfigMessage) new ObjectInputStream(sockToServer.getInputStream()).readObject();
        int myFieldWidth = config.getGameState().getMyField().getWidth();
        int myFieldHeight = config.getGameState().getMyField().getHeight();
        int enemyFieldWidth = config.getGameState().getEnemyField().getWidth();
        int enemyFieldHeight = config.getGameState().getEnemyField().getHeight();
        int nextFigurePanelWidth = config.getGameState().getNextFigureField().getWidth();
        int nextFigurePanelHeight = config.getGameState().getNextFigureField().getHeight();
        myField = new JPanel[myFieldWidth][myFieldHeight];
        enemyField = new JPanel[enemyFieldWidth][enemyFieldHeight];
        nextFigureField = new JPanel[nextFigurePanelWidth][nextFigurePanelHeight];
        scoreTable = config.getScoreTable().getScoreMap();
        about = config.getInfoAbout().getInfoAbout();
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
        table.setRowSorter(rowSorter);
        rowSorter.toggleSortOrder(1);
        rowSorter.toggleSortOrder(1);
        popupWindow.getContentPane().add(table);
        popupWindow.setVisible(true);
    }

    public void showInfoAbout() {
        
    }

    public void redraw
}
