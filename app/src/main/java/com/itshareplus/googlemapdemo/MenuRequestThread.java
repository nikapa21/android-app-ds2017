package com.itshareplus.googlemapdemo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Enumeration;

public class MenuRequestThread extends Thread {

    int port;
    int flag;
    private BrokerInfo brokerInfo;

    public MenuRequestThread(int serverPort, int flag) {
        this.port = serverPort;
        this.flag = flag;
    }

    public BrokerInfo call() throws InterruptedException {

        Thread.sleep(2000);
        return brokerInfo;

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
    }
}
