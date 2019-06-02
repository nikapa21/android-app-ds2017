package com.itshareplus.googlemapdemo;
import android.app.IntentService;
import android.content.Intent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class MyIntentService extends IntentService {

    public static final String ACTION_MyIntentService = "com.example.androidintentservice.RESPONSE";
    public static final String ACTION_MyUpdate = "com.example.androidintentservice.UPDATE";
    public static final String EXTRA_KEY_IN = "EXTRA_IN";
    public static final String EXTRA_KEY_OUT = "EXTRA_OUT";
    public static final String EXTRA_KEY_UPDATE = "EXTRA_UPDATE";

    String msgFromActivity;
    int flag;
    Broker myBroker;
    Topic topic;
    Subscriber subscriber;
    private int flag2;

    public MyIntentService() {
        super("com.example.androidintentservice.MyIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        //get input
        msgFromActivity = intent.getStringExtra(EXTRA_KEY_IN);
        myBroker = (Broker) intent.getSerializableExtra("broker");
        flag = (int) intent.getSerializableExtra("flag");
        topic = (Topic) intent.getSerializableExtra("topic");
        subscriber = (Subscriber) intent.getSerializableExtra("subscriber");

        if (flag == 3) { /***** DO THE REGISTER AND WAIT FOR DATA *****/

            Socket requestSocket = null;
            ObjectOutputStream out = null;
            ObjectInputStream in = null;

            try {
                requestSocket = new Socket(myBroker.getIpAddress(), myBroker.getPort());

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

                    System.out.println("Subscriber just registered! Waiting for data to visualize ");

                    while(true){

                        try{
                            requestSocket.setSoTimeout(15000); // wait for 15 seconds, if data are not received, print timeout

                            Data data = (Data) in.readObject();

                            flag2 = 0;
                            //send update
                            Intent intentUpdate = new Intent();
                            intentUpdate.setAction(ACTION_MyIntentService);
                            intentUpdate.addCategory(Intent.CATEGORY_DEFAULT);
                            intentUpdate.putExtra("flag", flag2);
                            intentUpdate.putExtra("data", data);
                            sendBroadcast(intentUpdate);
                        } catch (SocketTimeoutException sex) {
                            String timeoutMessage = "Timeout!";
                            System.out.println(timeoutMessage);

                            requestSocket.close();

                            flag2 = 1;
                            Intent intentTimeout = new Intent();
                            intentTimeout.setAction(ACTION_MyIntentService);
                            intentTimeout.addCategory(Intent.CATEGORY_DEFAULT);
                            intentTimeout.putExtra("flag", flag2);
                            intentTimeout.putExtra("Timeout message", timeoutMessage);
                            sendBroadcast(intentTimeout);
                            break;
                        }
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
