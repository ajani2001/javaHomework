package com.ajani2001.code;

import com.ajani2001.code.factory.Factory;

import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    public static void main(String[] args) throws Exception {
        InputStreamReader factoryConfigReader = new InputStreamReader(Factory.class.getResourceAsStream("FactoryConfig.txt"));
        Factory carFactory = new Factory(factoryConfigReader);
        /*
        carFactory.start();
        for(int i = 0; i < 15; ++i) {
            System.out.println("Accessory suppliers: "+carFactory.getAccessorySuppliersNumber()+", delay: "+carFactory.getAccessorySuppliersDelay());
            System.out.println("Body supplier delay: "+carFactory.getBodySupplierDelay());
            System.out.println("Motor supplier delay: "+carFactory.getMotorSupplierDelay());
            System.out.println("Dealers: "+carFactory.getDealersNumber()+", delay: "+carFactory.getDealersDelay());
            System.out.println("Accessory storage: "+carFactory.getAccessoryStorageItemNumber()+"/"+carFactory.getAccessoryStorageCapacity());
            System.out.println("Body storage: "+carFactory.getBodyStorageItemNumber()+"/"+carFactory.getBodyStorageCapacity());
            System.out.println("Motor storage: "+carFactory.getMotorStorageItemNumber()+"/"+carFactory.getMotorStorageCapacity());
            System.out.println("Car storage: "+carFactory.getCarStorageItemNumber()+"/"+carFactory.getCarStorageCapacity());
            Thread.sleep(1500);
        }
        carFactory.stop();
        */
        SwingView window = new SwingView(carFactory);
    }
}
