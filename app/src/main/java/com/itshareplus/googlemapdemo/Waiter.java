package com.itshareplus.googlemapdemo;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class Waiter implements Runnable{

    private Message msg;

    public Waiter(Message m){
        this.msg = m;
    }

    @Override
    public void run() {
        while(true) {
            String name = Thread.currentThread().getName();
            synchronized (msg) {
                try {
                    System.out.println(name+" waiting to get notified at time:"+System.currentTimeMillis());
                    msg.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(name+" waiter thread got notified at time:"+System.currentTimeMillis());
                //process the data now
                System.out.println(name+" processed: "+msg.getMsg());
                System.out.println("TRIED TO PRINT ON THE DISPLAY");
            }
        }
    }

}

