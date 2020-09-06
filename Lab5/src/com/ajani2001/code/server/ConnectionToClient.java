package com.ajani2001.code.server;

import com.ajani2001.code.client.request.Request;
import com.ajani2001.code.server.fieldmodel.ColorGrid;
import com.ajani2001.code.server.response.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;

public class ConnectionToClient extends Socket {
    ObjectOutputStream oOStream;
    ObjectInputStream oIStream;
    boolean initialized;

    public ConnectionToClient() {
        super();
        initialized = false;
    }

    public void init() throws IOException {
        oOStream = new ObjectOutputStream(getOutputStream());
        oIStream = new ObjectInputStream(getInputStream());
        initialized = true;
    }

    public Request receiveRequest() throws IOException, ClassNotFoundException {
        if(!initialized) throw new IOException("ConnectionToClient object is not initialized");
        synchronized (oIStream) {
            return (Request) oIStream.readObject();
        }
    }

    public void sendConfig(ColorGrid myField, int myScore, ColorGrid enemyField, int enemyScore, ColorGrid nextFigureField, HashMap<String, Integer> scoreMap, String infoAbout) throws IOException {
        if(!initialized) throw new IOException("ConnectionToClient object is not initialized");
        synchronized (oOStream) { // how to avoid inconsistent data state??
            oOStream.writeObject(new ConfigMessage(new GameStateMessage(myScore, enemyScore, myField, enemyField, nextFigureField), new InfoAboutResponse(infoAbout), new ScoreTableResponse(scoreMap)));
        }
    }

    public void sendGameState(ColorGrid myField, int myScore, ColorGrid enemyField, int enemyScore, ColorGrid nextFigureField) throws IOException {
        if(!initialized) throw new IOException("ConnectionToClient object is not initialized");
        synchronized (oOStream) {
            oOStream.writeObject(new GameStateMessage(myScore, enemyScore, myField, enemyField, nextFigureField));
        }
    }

    public void sendScoreTable(HashMap<String, Integer> scoreMap) throws IOException {
        if(!initialized) throw new IOException("ConnectionToClient object is not initialized");
        synchronized (oOStream) {
            oOStream.writeObject(new ScoreTableResponse(scoreMap));
        }
    }

    public void sendInfoAbout(String infoAbout) throws IOException {
        if(!initialized) throw new IOException("ConnectionToClient object is not initialized");
        synchronized (oOStream) {
            oOStream.writeObject(new InfoAboutResponse(infoAbout));
        }
    }

    public void sendPopupMessage(String message) throws IOException {
        if(!initialized) throw new IOException("ConnectionToClient object is not initialized");
        synchronized (oOStream) {
            oOStream.writeObject(new PopupMessage(message));
        }
    }
}
