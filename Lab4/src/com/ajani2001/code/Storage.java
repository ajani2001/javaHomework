package com.ajani2001.code;

import java.util.ArrayList;

public class Storage <T> {
    int capacity;
    ArrayList<T> itemList;

    public Storage(int capacity) {
        this.capacity = capacity;
        itemList = new ArrayList<>(capacity);
    }

    public synchronized void put(T item) throws InterruptedException {
        while (itemList.size() == capacity) {
            this.wait();
        }
        itemList.add(itemList.size(), item);
        this.notify();
    }

    public synchronized T get() throws InterruptedException {
        while (itemList.size() == 0) {
            this.wait();
        }
        T result = itemList.get(itemList.size() - 1);
        itemList.remove(itemList.size() - 1);
        this.notify();
        return result;
    }

    public int getCapacity() {
        return capacity;
    }

    public synchronized int getItemNumber() {
        return itemList.size();
    }
}
