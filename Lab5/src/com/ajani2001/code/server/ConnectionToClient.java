package com.ajani2001.code.server;

import com.ajani2001.code.client.Request;
import com.ajani2001.code.server.fieldmodel.ColorGrid;
import com.ajani2001.code.server.response.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;

public class ConnectionToClient extends Socket {
    GameServer server;
    ObjectOutputStream oOStream;
    ObjectInputStream oIStream;
    boolean initialized;

    public ConnectionToClient(GameServer server) {
        super();
        this.server = server;
        initialized = false;
    }

    public void init() throws IOException {
        oOStream = new ObjectOutputStream(getOutputStream());
        oIStream = new ObjectInputStream(getInputStream());
        initialized = true;
    }

    public Request receiveRequest() throws IOException, ClassNotFoundException {
        synchronized (oIStream) {
            return (Request) oIStream.readObject();
        }
    }

    public void sendConfig(ColorGrid myField, int myScore, ColorGrid enemyField, int enemyScore, ColorGrid nextFigureField, HashMap<String, Integer> scoreMap, String infoAbout) throws IOException {
        synchronized (oOStream) { // how to avoid inconsistent data state??
            oOStream.writeObject(new ConfigMessage(new GameStateMessage(myScore, enemyScore, myField, enemyField, nextFigureField), new InfoAboutResponse(infoAbout), new ScoreTableResponse(scoreMap)));
        }
    }

    public void sendGameState(ColorGrid myField, int myScore, ColorGrid enemyField, int enemyScore, ColorGrid nextFigureField) throws IOException {
        synchronized (oOStream) {
            oOStream.writeObject(new GameStateMessage(myScore, enemyScore, myField, enemyField, nextFigureField));
        }
    }

    public void sendScoreTable(HashMap<String, Integer> scoreMap) throws IOException {
        synchronized (oOStream) {
            oOStream.writeObject(new ScoreTableResponse(scoreMap));
        }
    }

    public void sendInfoAbout(String infoAbout) throws IOException {
        synchronized (oOStream) {
            oOStream.writeObject(new InfoAboutResponse(infoAbout));
        }
    }

    public void sendPopupMessage(String message) throws IOException {
        synchronized (oOStream) {
            oOStream.writeObject(new PopupMessage(message));
        }
    }
}
