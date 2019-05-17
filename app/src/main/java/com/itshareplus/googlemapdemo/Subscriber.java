package com.itshareplus.googlemapdemo;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class Subscriber implements Serializable {

    private static final long serialVersionUID = 1L;

    String addr;
    int port;
    BrokerInfo brokerInfo;


    public Subscriber(String addr, int port) {
        this.addr = addr;
        this.port = port;
    }

    public Subscriber() {

    }

    public Subscriber(String subscriberIP) {
        this.addr = subscriberIP;
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


    public List<Broker> getBrokers() {
        return null;
    }

    public void register(Broker broker, Topic topic) {

    }

    public void disconnect(Broker broker, Topic topic) {

    }

    public void visualiseData(Topic topic, Value value) {

    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public BrokerInfo getBrokerInfo() {
        return brokerInfo;
    }

    public void setBrokerInfo(BrokerInfo brokerInfo) {
        this.brokerInfo = brokerInfo;
    }

    @Override
    public String toString() {
        return "Subscriber{" +
                "addr='" + addr + '\'' +
                ", port=" + port +
                '}';
    }
}
