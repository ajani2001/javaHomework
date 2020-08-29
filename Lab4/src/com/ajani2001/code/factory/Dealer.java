package com.ajani2001.code.factory;

import com.ajani2001.code.factory.items.Car;

import java.io.IOException;
import java.io.Writer;
import java.sql.Time;
import java.time.LocalTime;
import java.util.GregorianCalendar;

public class Dealer extends Thread {
    String dealerName;
    long delayMillis;
    Factory carFactory;
    int carNumber;
    Writer logWriter;

    public Dealer(String dealerName, long delayMillis, Factory carFactory, Writer logWriter) {
        this.dealerName = dealerName;
        this.delayMillis = delayMillis;
        this.carFactory = carFactory;
        this.logWriter = logWriter;
    }

    @Override
    public void run() {
        while(!interrupted()) {
            try {
                Car car = carFactory.getCar();
                ++carNumber;
                if(logWriter != null) {
                    synchronized (logWriter) {
                        try {
                            logWriter.write(LocalTime.now().toString() + ": Dealer \"" + dealerName + "\": Auto #" + car.getId() + ": Motor #" + car.getMotor().getId() + ", Body #" + car.getBody().getId() + ", Accessory #" + car.getAccessory().getId()+System.lineSeparator());
                        } catch (IOException ignore) {
                        }
                    }
                }
                sleep(delayMillis);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    public void setDelayMillis(long delayMillis) {
        this.delayMillis = delayMillis;
    }

    public String getDealerName() {
        return dealerName;
    }

    public long getDelayMillis() {
        return delayMillis;
    }

    public int getCarNumber() {
        return carNumber;
    }
}
