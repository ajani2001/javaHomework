package com.ajani2001.code.server;

import com.ajani2001.code.client.Request;
import com.ajani2001.code.server.fieldmodel.FieldModel;

import java.io.IOException;

public class ClientModel {
    ConnectionToClient sockToClient;
    FieldModel field;
    GameServer server;
    Thread connectionListenerThread;
    boolean speedUpMode;
    boolean isReady;

    public ClientModel(int width, int height, GameServer serverParam, ConnectionToClient connectionSocket) {
        sockToClient = connectionSocket;
        field = new FieldModel(width, height);
        server = serverParam;
        connectionListenerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(!Thread.interrupted()) {
                    try {
                        Request incomingRequest = sockToClient.receiveRequest();
                        switch (incomingRequest) {
                            case START_GAME -> {
                                isReady = true;
                                server.notifyReady();
                            }
                            case SAVE_SCORE -> server.saveScore(ClientModel.this, incomingRequest.getParameter());
                            case KEY_ACTION -> {
                                if(incomingRequest.getParameter().equals("toBottom")) {
                                    field.pushFigureToBottom();
                                    server.notifyStateChanged();
                                } else if(incomingRequest.getParameter().equals("moveLeft")) {
                                    field.moveFigureLeft();
                                    server.notifyStateChanged();
                                } else if(incomingRequest.getParameter().equals("moveRight")) {
                                    field.moveFigureRight();
                                    server.notifyStateChanged();
                                } else if(incomingRequest.getParameter().equals("rotate")) {
                                    field.rotateFigure();
                                    server.notifyStateChanged();
                                } else if(incomingRequest.getParameter().equals("speedUpEnable")) {
                                    speedUpMode = true;
                                } else if(incomingRequest.getParameter().equals("speedUpDisable")) {
                                    speedUpMode = false;
                                } else {
                                    System.err.println("Unknown command: "+incomingRequest.getParameter());
                                }
                            }
                        }
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                        break;
                    }
                }
            }
        });
        connectionListenerThread.start();
        speedUpMode = false;
        isReady = false;
    }

    public ConnectionToClient getConnection() {
        return sockToClient;
    }

    public FieldModel getField() {
        return field;
    }

    public boolean isReady() {
        return isReady;
    }

    public void notifyTimerTick(int tickNumber) {
        if(!speedUpMode && tickNumber%2 != 0) return;
        field.moveFigureDown();
    }
}
