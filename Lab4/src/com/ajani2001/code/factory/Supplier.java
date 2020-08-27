package com.ajani2001.code.factory;

import com.ajani2001.code.Storage;
import com.ajani2001.code.factory.items.Part;

import java.util.HashMap;

public class Supplier <T extends Part> extends Thread {
    String supplierName;
    long delayMillis;
    Storage<T> partStorage;
    Class<T> partClass;
    final static HashMap<Class<?>, Long> nextIds;
    int partsSupplied;

    static {
        nextIds = new HashMap<>(); //?
    }

    public Supplier(String supplierName, long delayMillis, Storage<T> partStorage, Class<T> partClass) {
        this.supplierName = supplierName;
        this.delayMillis = delayMillis;
        this.partStorage =  partStorage;
        this.partClass = partClass;
        synchronized (nextIds) {
            if(!nextIds.containsKey(partClass)) {
                nextIds.put(partClass, 0L);
            }
        }
    }

    @Override
    public void run() {
        while(!interrupted()) {
            try {
                long newId;
                synchronized (nextIds) {
                    newId = nextIds.get(partClass);
                    nextIds.put(partClass, ++newId);
                }
                partStorage.put(partClass.getConstructor(String.class, long.class).newInstance(supplierName, newId));
                ++partsSupplied;
                Thread.sleep(delayMillis);
            } catch (InterruptedException e) {
                break;
            } catch (ReflectiveOperationException e) {
                e.printStackTrace();
            }
        }
    }

    public void setDelayMillis(long delayMillis) {
        this.delayMillis = delayMillis;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public long getDelayMillis() {
        return delayMillis;
    }

    public int getPartsSupplied() {
        return partsSupplied;
    }
}
