package com.ajani2001.code.factory;

import com.ajani2001.code.Storage;
import com.ajani2001.code.factory.items.Car;
import com.ajani2001.code.factory.items.CarAccessory;
import com.ajani2001.code.factory.items.CarBody;
import com.ajani2001.code.factory.items.CarMotor;

public class WorkerTask implements Runnable {
    Storage<CarAccessory> accessoryStorage;
    Storage<CarMotor> motorStorage;
    Storage<CarBody> bodyStorage;
    Storage<Car> carStorage;
    static long nextId;

    public WorkerTask(Storage<CarAccessory> accessoryStorage, Storage<CarMotor> motorStorage, Storage<CarBody> bodyStorage, Storage<Car> carStorage) {
        this.accessoryStorage = accessoryStorage;
        this.motorStorage = motorStorage;
        this.bodyStorage = bodyStorage;
        this.carStorage = carStorage;
    }

    @Override
    public void run() {
        try {
            CarAccessory accessory = accessoryStorage.get();
            CarBody body = bodyStorage.get();
            CarMotor motor = motorStorage.get();
            long id;
            synchronized (WorkerTask.class) {
                id = ++nextId;
            }
            carStorage.put(new Car(body, motor, accessory, id));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
