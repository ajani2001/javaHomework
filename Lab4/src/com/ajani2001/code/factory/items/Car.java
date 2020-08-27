package com.ajani2001.code.factory.items;

public class Car {
    final CarBody body;
    final CarMotor motor;
    final CarAccessory accessory;
    final long id;

    public Car(CarBody body, CarMotor motor, CarAccessory accessory, long id) {
        this.body = body;
        this.motor = motor;
        this.accessory = accessory;
        this.id = id;
    }

    public CarBody getBody() {
        return body;
    }

    public CarMotor getMotor() {
        return motor;
    }

    public CarAccessory getAccessory() {
        return accessory;
    }

    public long getId() {
        return id;
    }
}
