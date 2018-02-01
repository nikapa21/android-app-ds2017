package com.itshareplus.googlemapdemo;

import Chord.FileEntry;
import Modules.DirectionFinderListener;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.UnknownHostException;
import java.net.InetAddress;
import java.util.Enumeration;

public class MenuRequestThread extends Thread {

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

            //Create a socket to the MasterNode ip and port (7777):
            requestSocket = new Socket("192.168.1.12", port);
            //System.out.println("menu is opening a socket to the master node's port " + port);//debug

            InetAddress myIp = getLocalIpAddress();
            System.out.println("app has an IP " + myIp);

            // Get input and output streams
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            in = new ObjectInputStream(requestSocket.getInputStream());

            //First step is to send the flag to the Master - Which action we will take.
            out.writeInt(flag);
            out.flush();

            //TODO commit

            if(flag==2){ //commit-save file

                out.writeObject(fileEntry);
                out.flush();
                out.writeObject(myIp);
                out.flush();

                System.out.println("Waiting for the master to commit file ");
            }
            else if(flag==3) { // search file

                out.writeObject(fileEntry);
                out.flush();
                out.writeObject(myIp);
                out.flush();

                //read the requested file from server
                requestedFile = (FileEntry) in.readObject();

                System.out.println("Waiting for the master's response for search action ");
            }

        } catch (UnknownHostException unknownHost) {
            System.err.println("You are trying to connect to an unknown host!");
        } catch (Exception ioException) {
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
