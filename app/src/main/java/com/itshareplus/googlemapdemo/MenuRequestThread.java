package com.itshareplus.googlemapdemo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Enumeration;

import Chord.FileEntry;
import Modules.DirectionFinderListener;

public class MenuRequestThread extends Thread {

    private Message msg;
    FileEntry fileEntry;
    Data data;
    private DirectionFinderListener listener;

    int port;
    int flag;
    private Broker broker;
    private Topic topic;
    private BrokerInfo brokerInfo;
    Subscriber subscriber;

    public MenuRequestThread(FileEntry fileEntry, int port, int flag, DirectionFinderListener listener) {

        this.fileEntry = fileEntry;
        this.port = port;
        this.flag = flag;
        this.listener = listener;

    }

    public MenuRequestThread(int serverPort, int flag) {
        this.port = serverPort;
        this.flag = flag;
    }

    public MenuRequestThread(int serverPort) {
        this.port = serverPort;
    }

    public MenuRequestThread(Broker broker, Topic topic) {
    }

    public MenuRequestThread(int serverPort, int flag, Broker myBroker, Topic topic) {
        this.port = serverPort;
        this.flag = flag;
        this.broker = myBroker;
        this.topic = topic;
    }

    public MenuRequestThread(int flag, Broker myBroker, Topic topic, Subscriber subscriber) {
        this.flag = flag;
        this.broker = myBroker;
        this.topic = topic;
        this.subscriber = subscriber;
    }

    public MenuRequestThread(int serverPort, int flag, Broker myBroker) {
        this.port = serverPort;
        this.flag = flag;
        this.broker = myBroker;
    }

    public MenuRequestThread(int flag, Broker myBroker, Subscriber subscriber) {
        this.flag = flag;
        this.broker = myBroker;
        this.subscriber = subscriber;
    }

    public MenuRequestThread(int flag, Broker myBroker, Topic topic, Subscriber subscriber, Message msg) {
        this.flag = flag;
        this.broker = myBroker;
        this.topic = topic;
        this.subscriber = subscriber;
        this.msg = msg;
    }

    public BrokerInfo call() throws InterruptedException {

        Thread.sleep(2000);
        return brokerInfo;

    }

    public Data callData() throws InterruptedException {

        Thread.sleep(2000);
        return data;

    }

    public InetAddress getLocalIpAddress(){
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return InetAddress.getByName(inetAddress.getHostAddress()); } } } }
        catch (Exception ex) {
            System.out.println("IP Address "+ ex.toString());
        }
        return null;
    }

    public void run() {

        if (flag == 2) { /***** DO THE PREREGISTER *****/

            Socket requestSocket = null;
            ObjectOutputStream out = null;
            ObjectInputStream in = null;

            try {
                requestSocket = new Socket("192.168.1.4", 7000);

                out = new ObjectOutputStream(requestSocket.getOutputStream());
                in = new ObjectInputStream(requestSocket.getInputStream());

                try {

                    out.writeInt(flag);
                    out.flush();

                    String greetingMessage = (String) in.readObject();
                    System.out.println(greetingMessage);

                    // perimenw na mathw poioi einai oi upoloipoi brokers kai gia poia kleidia einai upeuthinoi
                    // diladi perimenw ena antikeimeno Info tis morfis {ListOfBrokers, <BrokerId, ResponsibilityLine>}

                    brokerInfo = (BrokerInfo) in.readObject();
                    System.out.println("Received from broker brokerinfo upon preregister: " + brokerInfo);

                } catch(Exception classNot){
                    System.err.println("data received in unknown format");
                    classNot.printStackTrace();
                }
            } catch (UnknownHostException unknownHost) {
                System.err.println("You are trying to connect to an unknown host!");
            } catch (IOException ioException) {
                ioException.printStackTrace();
            } finally {
                try {
                    in.close();
                    out.close();
                    requestSocket.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        }

        else if (flag == 3) { /***** DO THE REGISTER AND WAIT FOR DATA *****/

            Socket requestSocket = null;
            ObjectOutputStream out = null;
            ObjectInputStream in = null;

            try {
                requestSocket = new Socket("192.168.1.4", broker.getPort());

                String myIp = "192.168.1.4";
                System.out.println("app has an IP " + myIp);

                out = new ObjectOutputStream(requestSocket.getOutputStream());
                in = new ObjectInputStream(requestSocket.getInputStream());

                // send flag 3 to responsible broker for topic in order to register subscriber and pull

                try {

                    out.writeInt(flag);
                    out.flush();

                    out.writeObject(subscriber); // send the subscriber himself (this) to be registered
                    out.flush();

                    out.writeObject(topic);
                    out.flush();

                    System.out.println("Subscriber just registered!");


                    while(true){

                        Data data = (Data) in.readObject();
                        System.out.println("Data: " + data);

                        Notifier notifier = new Notifier(msg);
                        msg.setData(data);
                        msg.setMsg("Notification sent ");
                        new Thread(notifier).start();
                    }


                } catch(Exception classNot){
                    System.err.println("data received in unknown format");
                    classNot.printStackTrace();
                }
            } catch (UnknownHostException unknownHost) {
                System.err.println("You are trying to connect to an unknown host!");
            } catch (IOException ioException) {
                ioException.printStackTrace();
            } finally {
                try {
                    in.close();
                    out.close();
                    requestSocket.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }

        }
    }
}
