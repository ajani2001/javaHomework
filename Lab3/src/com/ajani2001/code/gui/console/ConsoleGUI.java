package com.ajani2001.code.gui.console;

import com.ajani2001.code.ColorGrid;
import com.ajani2001.code.GUI;
import com.ajani2001.code.GameData;

import java.io.*;

public class ConsoleGUI implements GUI {
    GameData model;
    final int consoleHeight = 25;

    @Override
    public void init(GameData dataObject) {
        model = dataObject;
        model.subscribe(this);
        new Thread(new ConsoleListenerThread(model)).start();
    }

    @Override
    public void redraw() {
        ColorGrid gameFieldColorGrid = model.getGameField();
        ColorGrid nextFigureColorGrid = model.getNextFigure();
        StringBuilder builder = new StringBuilder();

        builder.append(getStringForField(gameFieldColorGrid, 0));
        builder.append(" Score: ");
        builder.append(model.getCurrentScore());
        System.out.println(builder.toString());

        for(int i = 1; i <= gameFieldColorGrid.getHeight()+1; ++i) {
            builder = new StringBuilder();
            builder.append(getStringForField(gameFieldColorGrid, i));
            if(i <= nextFigureColorGrid.getHeight()+2) {
                builder.append(' ');
                builder.append(getStringForField(nextFigureColorGrid, i-1));
            }
            System.out.println(builder);
        }
        for(int i = 0; i < consoleHeight - gameFieldColorGrid.getHeight() - 2; ++i) {
            System.out.println();
        }
    }

    String getStringForField(ColorGrid fieldModel, int index) {
        StringBuilder builder = new StringBuilder();
        if(index == 0) {
            builder.append('/');
            builder.append("-".repeat(fieldModel.getWidth()));
            builder.append('\\');
        } else if(index > 0 && index <= fieldModel.getHeight()) {
            builder.append('|');
            for(int i = 0; i < fieldModel.getWidth(); ++i) builder.append( fieldModel.getGrid()[i][index-1] == null ? ' ' : '#' );
            builder.append('|');
        } else if(index == fieldModel.getHeight()+1) {
            builder.append('\\');
            builder.append("-".repeat(fieldModel.getWidth()));
            builder.append('/');
        }
        return builder.toString();
    }
}
