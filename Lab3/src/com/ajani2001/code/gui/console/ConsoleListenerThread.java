package com.ajani2001.code.gui.console;

import com.ajani2001.code.GameData;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;

public class ConsoleListenerThread implements Runnable {
    GameData model;

    private ConsoleListenerThread() {}

    public ConsoleListenerThread(GameData model) {
        this.model = model;
    }

    @Override
    public void run() {
        System.out.println("Welcome to the console version of my tetris!");
        System.out.println("Available commands:");
        System.out.println("New Game");
        System.out.println("High scores");
        System.out.println("About");
        System.out.println("Exit");
        StreamTokenizer consoleTokenizer = new StreamTokenizer(new InputStreamReader(System.in));
        consoleTokenizer.resetSyntax();
        consoleTokenizer.wordChars(1, 255);
        consoleTokenizer.whitespaceChars('\r', '\r');
        consoleTokenizer.whitespaceChars('\n', '\n');
        for (int token = 0; ; ) {
            try {
                token = consoleTokenizer.nextToken();
            } catch (IOException e) {
                System.err.println(e.getMessage());
                System.exit(0);
            }
            if (token == StreamTokenizer.TT_EOF) {
                System.exit(0);
            } else if (consoleTokenizer.sval.equalsIgnoreCase("New Game")) {
                model.startNewGame();
            } else if (consoleTokenizer.sval.equalsIgnoreCase("High scores")) {
                System.out.println(model.getHighScore(5));
            } else if (consoleTokenizer.sval.equalsIgnoreCase("About")) {
                System.out.println(model.getInfoAbout());
            } else if (consoleTokenizer.sval.equalsIgnoreCase("Exit")) {
                System.exit(0);
            } else if (consoleTokenizer.sval.equalsIgnoreCase("A")) {
                model.moveFigureLeft();
            } else if (consoleTokenizer.sval.equalsIgnoreCase("W")) {
                model.rotateFigure();
            } else if (consoleTokenizer.sval.equalsIgnoreCase("S")) {
                model.pushFigureToBottom();
            } else if (consoleTokenizer.sval.equalsIgnoreCase("D")) {
                model.moveFigureRight();
            } else if (consoleTokenizer.sval.substring(0, 11).equalsIgnoreCase("Save score ")) {
                if(consoleTokenizer.sval.substring(11).isBlank()) {
                    System.out.println("Empty player name");
                } else {
                    model.saveScore(consoleTokenizer.sval.substring(11));
                }
            } else {
                System.out.println("Unknown command: " + consoleTokenizer.sval);
            }
        }
    }
}
