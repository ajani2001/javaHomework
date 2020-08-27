package com.ajani2001.code.factory;

import com.ajani2001.code.Storage;
import com.ajani2001.code.factory.items.Car;
import com.ajani2001.code.factory.items.CarAccessory;
import com.ajani2001.code.factory.items.CarBody;
import com.ajani2001.code.factory.items.CarMotor;
import com.ajani2001.code.ThreadPool;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Properties;

public class Factory {

    Storage<CarAccessory> accessoryStorage;
    Storage<CarBody> bodyStorage;
    Storage<CarMotor> motorStorage;
    Storage<Car> carStorage;
    ArrayList<Supplier<CarAccessory>> accessorySuppliers;
    Supplier<CarBody> bodySupplier;
    Supplier<CarMotor> motorSupplier;
    ThreadPool workers;
    ArrayList<Dealer> dealers;

    public Factory(Reader configReader) throws IOException {
        Properties config = new Properties();
        config.load(configReader);
        accessoryStorage = new Storage<>(Integer.parseInt(config.getProperty("AccessoryStorageCapacity", "100")));
        bodyStorage = new Storage<>(Integer.parseInt(config.getProperty("BodyStorageCapacity", "100")));
        motorStorage = new Storage<>(Integer.parseInt(config.getProperty("MotorStorageCapacity", "100")));
        carStorage = new Storage<>(Integer.parseInt(config.getProperty("CarStorageCapacity", "100")));
        int accessorySuppliersNumber = Integer.parseInt(config.getProperty("AccessorySuppliersNumber", "5"));
        accessorySuppliers = new ArrayList<>(accessorySuppliersNumber);
        int accessorySuppliersDelay = Integer.parseInt(config.getProperty("AccessorySuppliersDelay", "1000"));
        for(int i = 0; i < accessorySuppliersNumber; ++i) {
            accessorySuppliers.add(i, new Supplier<CarAccessory>("AccessorySupplier"+i, accessorySuppliersDelay, accessoryStorage, CarAccessory.class));
        }
        bodySupplier = new Supplier<CarBody>("BodySupplier", Integer.parseInt(config.getProperty("BodySupplierDelay", "1000")), bodyStorage, CarBody.class);
        motorSupplier = new Supplier<CarMotor>("MotorSupplier", Integer.parseInt(config.getProperty("MotorSupplierDelay", "1000")), motorStorage, CarMotor.class);
        workers = new ThreadPool(Integer.parseInt(config.getProperty("WorkersNumber", "10")));
        int dealersNumber = Integer.parseInt(config.getProperty("DealersNumber", "10"));
        int dealersDelay = Integer.parseInt(config.getProperty("DealersDelay", "1000"));
        dealers = new ArrayList<>(dealersNumber);
        for(int i = 0; i < dealersNumber; ++i) {
            dealers.add(i, new Dealer("Dealer"+i, dealersDelay, this));
        }
    }

    public Car getCar() throws InterruptedException {
        Car result = carStorage.get();
        workers.putTask(new WorkerTask(accessoryStorage, motorStorage, bodyStorage, carStorage));
        return result;
    }

    public void start(){
        for(Supplier<CarAccessory> supplier: accessorySuppliers) {
            supplier.start();
        }
        bodySupplier.start();
        motorSupplier.start();
        workers.start();
        for(Dealer dealer: dealers) {
            dealer.start();
        }
        for(int i = 0; i < dealers.size(); ++i) {
            workers.putTask(new WorkerTask(accessoryStorage, motorStorage, bodyStorage, carStorage));
        }
    }

    public void stop(){
        for(Supplier<CarAccessory> supplier: accessorySuppliers) {
            supplier.interrupt();
        }
        bodySupplier.interrupt();
        motorSupplier.interrupt();
        for(Dealer dealer: dealers) {
            dealer.interrupt();
        }
        workers.finish();

        try {
            for (Supplier<CarAccessory> supplier : accessorySuppliers) {
                supplier.join();
            }
            bodySupplier.join();
            motorSupplier.join();
            for (Dealer dealer : dealers) {
                dealer.join();
            }
        } catch (InterruptedException e) {
            return;
        }
    }

    public void setAccessorySuppliersDelay(long delayMillis) {
        for(Supplier<CarAccessory> supplier: accessorySuppliers) {
            supplier.setDelayMillis(delayMillis);
        }
    }

    public void setBodySupplierDelay(long delayMillis) {
        bodySupplier.setDelayMillis(delayMillis);
    }

    public void setMotorSupplierDelay(long delayMillis) {
        motorSupplier.setDelayMillis(delayMillis);
    }

    public void setDealersDelay(long delayMillis) {
        for(Dealer dealer: dealers) {
            dealer.setDelayMillis(delayMillis);
        }
    }

    public long getAccessorySuppliersDelay() {
        return accessorySuppliers.get(0).getDelayMillis();
    }

    public long getBodySupplierDelay() {
        return bodySupplier.getDelayMillis();
    }

    public long getMotorSupplierDelay() {
        return motorSupplier.getDelayMillis();
    }

    public long getDealersDelay() {
        return dealers.get(0).getDelayMillis();
    }

    public int getAccessoryStorageCapacity() {
        return accessoryStorage.getCapacity();
    }

    public int getAccessoryStorageItemNumber() {
        return accessoryStorage.getItemNumber();
    }

    public int getBodyStorageCapacity() {
        return bodyStorage.getCapacity();
    }

    public int getBodyStorageItemNumber() {
        return bodyStorage.getItemNumber();
    }

    public int getMotorStorageCapacity() {
        return motorStorage.getCapacity();
    }

    public int getMotorStorageItemNumber() {
        return motorStorage.getItemNumber();
    }

    public int getCarStorageCapacity() {
        return carStorage.getCapacity();
    }

    public int getCarStorageItemNumber() {
        return carStorage.getItemNumber();
    }

    public int getAccessorySuppliersNumber() {
        return accessorySuppliers.size();
    }

    public int getDealersNumber() {
        return dealers.size();
    }

    public int getAccessorySuppliedNumber() {
        int result = 0;
        for(Supplier<CarAccessory> supplier: accessorySuppliers) {
            result += supplier.getPartsSupplied();
        }
        return result;
    }

    public int getCarBodiesSuppliedNumber() {
        return bodySupplier.getPartsSupplied();
    }

    public int getCarMotorsSuppliedNumber() {
        return motorSupplier.getPartsSupplied();
    }

    public int getCarsSuppliedNumber() {
        int result = 0;
        for(Dealer dealer: dealers) {
            result += dealer.carNumber;
        }
        return result;
    }

}
