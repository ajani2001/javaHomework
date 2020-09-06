package com.ajani2001.code.client;

import com.ajani2001.code.client.request.Request;
import com.ajani2001.code.client.request.RequestType;
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
            oOStream.writeObject(new Request(RequestType.START_GAME, null));
        }
    }

    public void sendKeyAction(String actionName) throws IOException {
        synchronized (oOStream) {
            oOStream.writeObject(new Request(RequestType.KEY_ACTION, actionName));
        }
    }

    public void saveScore(String name) throws IOException {
        synchronized (oOStream) {
            oOStream.writeObject(new Request(RequestType.SAVE_SCORE, name));
        }
    }
}
