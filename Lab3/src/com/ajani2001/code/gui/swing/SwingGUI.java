package com.ajani2001.code.gui.swing;

import com.ajani2001.code.ColorGrid;
import com.ajani2001.code.GUI;
import com.ajani2001.code.GameData;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SwingGUI implements GUI {
    GameData model;
    JPanel[][] gameJPanelGrid;
    JPanel[][] nextFigureJPanelGrid;
    JTextArea scoreDisplayJTextArea;

    @Override
    public void init(GameData model) {
        this.model = model;
        model.subscribe(this);

        JFrame mainFrame = new JFrame("Tetris");
        mainFrame.setSize(400, 600);
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainFrame.getContentPane().setLayout(new GridBagLayout());

        ColorGrid fieldColorGrid = model.getGameField();
        JPanel gameFieldArea = new JPanel(new GridLayout(fieldColorGrid.getHeight(), fieldColorGrid.getWidth()));
        gameFieldArea.setBorder(BorderFactory.createLoweredBevelBorder());
        gameJPanelGrid = new JPanel[fieldColorGrid.getWidth()][fieldColorGrid.getHeight()];
        for (int i = 0; i < fieldColorGrid.getHeight(); ++i) {
            for (int j = 0; j < fieldColorGrid.getWidth(); ++j) {
                JPanel cell = new JPanel();
                cell.setBorder(BorderFactory.createEtchedBorder(Color.GRAY, Color.WHITE));
                gameFieldArea.add(cell);
                gameJPanelGrid[j][i] = cell;
            }
        }

        ColorGrid nextFigureColorGrid = model.getNextFigure();
        JPanel nextBlockArea = new JPanel(new GridLayout(nextFigureColorGrid.getHeight(), nextFigureColorGrid.getWidth()));
        nextBlockArea.setBorder(BorderFactory.createRaisedBevelBorder());
        nextFigureJPanelGrid = new JPanel[nextFigureColorGrid.getWidth()][nextFigureColorGrid.getHeight()];
        for (int i = 0; i < nextFigureColorGrid.getHeight(); ++i) {
            for (int j = 0; j < nextFigureColorGrid.getWidth(); ++j) {
                JPanel cell = new JPanel();
                cell.setBorder(BorderFactory.createEtchedBorder());
                nextBlockArea.add(cell);
                nextFigureJPanelGrid[j][i] = cell;
            }
        }


        scoreDisplayJTextArea = new JTextArea("Score: 0");
        scoreDisplayJTextArea.setEnabled(false);
        JButton startButton = new JButton("New game");
        JButton scoreTableButton = new JButton("Scores");
        JButton saveScoreButton = new JButton("Save score");
        JButton aboutButton = new JButton("About");
        JButton exitButton = new JButton("Exit");

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 7;
        constraints.weightx = 0.8;
        constraints.weighty = 1;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.insets = new Insets(2, 2, 2, 2);
        mainFrame.getContentPane().add(gameFieldArea, constraints);

        constraints.gridx = 1;
        constraints.gridwidth = constraints.gridheight = 1;
        constraints.weightx = 0.2;
        constraints.weighty = 0.0;
        constraints.fill = GridBagConstraints.NONE;
        mainFrame.getContentPane().add(scoreDisplayJTextArea, constraints);

        constraints.gridy = 1;
        constraints.weighty = 0.05;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.insets = new Insets(30, 20, 30, 5);
        mainFrame.getContentPane().add(nextBlockArea, constraints);

        constraints.weighty = 0.1;
        constraints.gridy = 2;
        constraints.insets = new Insets(2, 2, 2, 2);
        mainFrame.getContentPane().add(startButton, constraints);

        constraints.gridy = 3;
        mainFrame.getContentPane().add(scoreTableButton, constraints);

        constraints.gridy = 4;
        mainFrame.getContentPane().add(saveScoreButton, constraints);

        constraints.gridy = 5;
        mainFrame.getContentPane().add(aboutButton, constraints);

        constraints.gridy = 6;
        mainFrame.getContentPane().add(exitButton, constraints);

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.startNewGame();
            }
        });

        scoreTableButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(mainFrame, model.getHighScore(5), "High scores", JOptionPane.PLAIN_MESSAGE);
            }
        });

        saveScoreButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String playerName = (String) JOptionPane.showInputDialog(mainFrame, "Enter you name", "Save score", JOptionPane.QUESTION_MESSAGE, null, null, "Player");
                if(!playerName.isBlank()) model.saveScore(playerName);
            }
        });

        aboutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(mainFrame, model.getInfoAbout(), "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        gameFieldArea.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ENTER"), "toBottom");
        gameFieldArea.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("LEFT"), "moveLeft");
        gameFieldArea.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("RIGHT"), "moveRight");
        gameFieldArea.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("UP"), "rotate");
        gameFieldArea.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("pressed DOWN"), "speedUpEnable");
        gameFieldArea.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released DOWN"), "speedUpDisable");
        gameFieldArea.getActionMap().put("toBottom", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.pushFigureToBottom();
            }
        });
        gameFieldArea.getActionMap().put("moveLeft", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.moveFigureLeft();
            }
        });
        gameFieldArea.getActionMap().put("moveRight", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.moveFigureRight();
            }
        });
        gameFieldArea.getActionMap().put("rotate", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.rotateFigure();
            }
        });
        gameFieldArea.getActionMap().put("speedUpEnable", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.speedUp(true);
            }
        });
        gameFieldArea.getActionMap().put("speedUpDisable", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.speedUp(false);
            }
        });
        mainFrame.setVisible(true);
    }

    @Override
    public void redraw() {
        ColorGrid currentGameFieldColorGrid = model.getGameField();
        ColorGrid currentNextFigureColorGrid = model.getNextFigure();
        for(int y = 0; y < currentGameFieldColorGrid.getHeight(); ++y) {
            for(int x = 0; x < currentGameFieldColorGrid.getWidth(); ++x) {
                if(currentGameFieldColorGrid.getGrid()[x][y] == null) {
                    gameJPanelGrid[x][y].setBackground(Color.WHITE);
                } else {
                    gameJPanelGrid[x][y].setBackground(currentGameFieldColorGrid.getGrid()[x][y]);
                }
            }
        }
        for(int y = 0; y < currentNextFigureColorGrid.getHeight(); ++y) {
            for(int x = 0; x < currentNextFigureColorGrid.getWidth(); ++x) {
                if(currentNextFigureColorGrid.getGrid()[x][y] == null) {
                    nextFigureJPanelGrid[x][y].setBackground(Color.WHITE);
                } else {
                    nextFigureJPanelGrid[x][y].setBackground(currentNextFigureColorGrid.getGrid()[x][y]);
                }
            }
        }
        scoreDisplayJTextArea.setText("Score: " + model.getCurrentScore());
    }
}
