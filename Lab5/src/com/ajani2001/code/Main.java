package com.ajani2001.code;

import com.ajani2001.code.client.SwingClient;
import com.ajani2001.code.server.GameServer;
import com.ajani2001.code.server.exception.FigureConfigFileInvalidException;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        if(args.length == 1) {
            try {
                GameServer server = new GameServer(Integer.parseInt(args[0]));
                server.run();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (FigureConfigFileInvalidException e) {
                e.printStackTrace();
            }
        } else if(args.length == 2) {
            SwingClient client = new SwingClient(args[0], Integer.parseInt(args[1]));
        }
    }
}
