package com.ajani2001.code.server;

class MyTimer extends Thread {
    int delayMillis;

    public MyTimer(Runnable target, int delayMillis) {
        super(target);
        this.delayMillis = delayMillis;
    }

    public int getDelayMillis() {
        return delayMillis;
    }

    public void setDelayMillis(int delayMillis) {
        this.delayMillis = delayMillis;
    }

    public void run() {
        while(isInterrupted()) {
            try {
                super.run();
                sleep(delayMillis);
            }
            catch (InterruptedException e) {
                break;
            }
        }
    }
}
