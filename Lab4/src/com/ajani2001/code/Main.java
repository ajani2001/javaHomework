package com.ajani2001.code;

import com.ajani2001.code.factory.Factory;

import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    public static void main(String[] args) throws Exception {
        InputStreamReader factoryConfigReader = new InputStreamReader(Factory.class.getResourceAsStream("FactoryConfig.txt"));
        Factory carFactory = new Factory(factoryConfigReader);
        SwingView window = new SwingView(carFactory);
    }
}
