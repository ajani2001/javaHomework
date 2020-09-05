package com.ajani2001.code.client;

import com.ajani2001.code.server.response.Response;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class GameClientSocket extends Socket {
    final ObjectInputStream oIStream;
    final ObjectOutputStream oOStream;

    public GameClientSocket(String host, int port) throws UnknownHostException, IOException {
        super(host, port);
        oIStream = new ObjectInputStream(getInputStream());
        oOStream = new ObjectOutputStream(getOutputStream());
    }

    public Response receiveResponse() throws IOException, ClassNotFoundException {
        synchronized (oIStream) {
            return (Response) oIStream.readObject();
        }
    }

    public void sendStartRequest() throws IOException {
        synchronized (oOStream) {
            oOStream.writeObject(Request.START_GAME);
        }
    }

    public void sendKeyAction(String actionName) throws IOException {
        synchronized (Request.KEY_ACTION) {
            Request.KEY_ACTION.setParameter(actionName);
            synchronized (oOStream) {
                oOStream.writeObject(Request.KEY_ACTION);
            }
        }
    }

    public void saveScore(String name) throws IOException {
        Request saveRequest = Request.SAVE_SCORE;
        saveRequest.setParameter(name);
        synchronized (oOStream) {
            oOStream.writeObject(saveRequest);
        }
    }
}
