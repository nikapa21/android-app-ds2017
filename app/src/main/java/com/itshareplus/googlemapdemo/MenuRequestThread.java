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

public class MenuRequestThread extends Thread implements Serializable{

    FileEntry fileEntry;
    FileEntry requestedFile;
    private DirectionFinderListener listener;

    int port;
    int flag;

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

    public FileEntry call() throws InterruptedException {

        Thread.sleep(2000);
        return requestedFile;

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

        Socket requestSocket = null;
        ObjectOutputStream out = null;
        ObjectOutputStream out2 = null;
        ObjectInputStream in = null;

        try {
            requestSocket = new Socket("192.168.1.101", 7000);

            out = new ObjectOutputStream(requestSocket.getOutputStream());
            in = new ObjectInputStream(requestSocket.getInputStream());

            int flagRegister = 2; // send flag 2 to broker 7000 in order to preRegister subscriber and receive all info about brokers and responsibilities

            try {

                out.writeInt(flagRegister);
                out.flush();

                String txt = (String) in.readObject();
                System.out.println(txt);

                // perimenw na mathw poioi einai oi upoloipoi brokers kai gia poia kleidia einai upeuthinoi
                // diladi perimenw ena antikeimeno Info tis morfis {ListOfBrokers, <BrokerId, ResponsibilityLine>}

                BrokerInfo brokerInfo = (BrokerInfo) in.readObject();
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
}
