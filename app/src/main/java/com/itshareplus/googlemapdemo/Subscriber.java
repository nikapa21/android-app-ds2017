package com.itshareplus.googlemapdemo;

import java.io.Serializable;
import java.util.Objects;

public class Subscriber implements Serializable {

    private static final long serialVersionUID = 1L;

    String addr;
    int port;


    public Subscriber(String addr, int port) {
        this.addr = addr;
        this.port = port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Subscriber that = (Subscriber) o;
        return port == that.port &&
                addr.equals(that.addr);
    }

    @Override
    public int hashCode() {
        return Objects.hash(addr, port);
    }

    @Override
    public String toString() {
        return "Subscriber{" +
                "addr='" + addr + '\'' +
                ", port=" + port +
                '}';
    }
}
