package com.ajani2001.code;

import java.util.ArrayList;
import java.util.LinkedList;

public class ThreadPool {
    ArrayList<Thread> threads;
    final LinkedList<Runnable> taskList;

    public ThreadPool(int poolSize) {
        threads = new ArrayList<>(poolSize);
        taskList = new LinkedList<>();
        for(int i = 0; i < poolSize; ++i) {
            threads.add(i, new Thread(() -> {
               while(!Thread.interrupted()) {
                   Runnable task;
                   synchronized (taskList) {
                       while(taskList.size() == 0) {
                           try {
                               taskList.wait();
                           } catch (InterruptedException e) {
                               return;
                           }
                       }
                       task = taskList.pop();
                   }
                   task.run();
               }
            }));
        }
    }

    public void putTask(Runnable task) {
        synchronized (taskList) {
            taskList.push(task);
            taskList.notify();
        }
    }

    public int getTaskQueueSize() {
        return taskList.size();
    }

    public void start() {
        for(Thread t: threads) {
            t.start();
        }
    }

    public void finish() {
        for(Thread t: threads) {
            t.interrupt();
        }
        for(Thread t: threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
