package com.itshareplus.googlemapdemo;

import java.io.Serializable;

public class Broker implements Serializable {

    private static final long serialVersionUID = 1L;

    private String ipAddress;
    private int port;

    public Broker(String ipAddress, int port) {
        this.ipAddress = ipAddress;
        this.port = port;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public int getPort() {
        return port;
    }

    public boolean equals(Object o) {
        if (o == null){
            return false;
        }
        Broker other = (Broker)o;

        return other.getIpAddress().equals(this.getIpAddress()) && other.getPort() == this.getPort();
    }

    public String toString() {
        return "Broker{" +
                "ipAddress='" + ipAddress + '\'' +
                ", port=" + port +
                '}';
    }
}

