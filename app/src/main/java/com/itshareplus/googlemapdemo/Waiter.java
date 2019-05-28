package com.itshareplus.googlemapdemo;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class Waiter implements Runnable{

    private Message msg;

    private ArrayList<Marker> markers = new ArrayList<>();
    private ArrayList<LatLng> latlngs = new ArrayList<>();
    private MarkerOptions markerOptions = new MarkerOptions();

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

