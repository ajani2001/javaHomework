package com.ajani2001.code.factory;

import com.ajani2001.code.factory.items.Car;

public class Dealer extends Thread {
    String dealerName;
    long delayMillis;
    Factory carFactory;
    int carNumber;

    public Dealer(String dealerName, long delayMillis, Factory carFactory) {
        this.dealerName = dealerName;
        this.delayMillis = delayMillis;
        this.carFactory = carFactory;
    }

    @Override
    public void run() {
        while(!interrupted()) {
            try {
                Car car = carFactory.getCar();
                ++carNumber;
                System.out.println("Dealer \"" + dealerName + "\": Auto #" + car.getId() + ": Motor #" + car.getMotor().getId() + ", Body #" + car.getBody().getId() + ", Accessory #" + car.getAccessory().getId());
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
