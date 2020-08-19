package com.ajani2001.code;

import com.ajani2001.code.exception.MyException;
import com.ajani2001.code.gui.console.ConsoleGUI;
import com.ajani2001.code.gui.swing.SwingGUI;

public class Main {
    public static void main(String[] args) throws MyException {
        String resourcePath = "src\\com\\ajani2001\\code\\resources\\";
        GameData data = new GameData(10, 20,
                resourcePath + "Tetrominoes.txt",
                resourcePath + "ScoreTable.txt",
                resourcePath + "About.txt");
        ConsoleGUI consoleGUI = new ConsoleGUI();
        SwingGUI swingGUI = new SwingGUI();
        consoleGUI.init(data);
        swingGUI.init(data);
    }
}
