package com.itshareplus.googlemapdemo;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.io.Serializable;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, Serializable {

    private GoogleMap mMap;
    private Button btnFindBusLine;
    private EditText etTopic;
    private MarkerOptions markerOptions = new MarkerOptions();
    private ArrayList<LatLng> latlngs = new ArrayList<>();
    private ArrayList<Marker> markers = new ArrayList<>();
    Hashtable <String, Marker> vehicleIdMarkerTable = new Hashtable<>();
    Marker marker;
    private MyBroadcastReceiver myBroadcastReceiver;

    int flag;
    int SubscriberPort = 5554;
    static final int SERVER_PORT = 7000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        btnFindBusLine = (Button) findViewById(R.id.btnFindBusLine);
        etTopic = (EditText) findViewById(R.id.etTopic);

        btnFindBusLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequest();
            }
        });
    }

    private void sendRequest() {

        Subscriber subscriber = new Subscriber("192.168.1.4", SubscriberPort);

        /*** Dwse to busline (to topic diladi) gia to opoio endiaferesai ***/

        Topic topic = new Topic(etTopic.getText().toString());
        System.out.println("Sending request for bus line: " + topic);

        /**** kane prwta to PRE REGISTER gia na pareis to brokerInfo me tin pliroforia apo tous brokers gia poia kleidia einai upeuthinoi ****/

        flag = 2; // send flag 2 to broker 7000 in order to preRegister subscriber and receive all info about brokers and responsibilities
        MenuRequestThread mrt = new MenuRequestThread(SERVER_PORT, flag);
        mrt.start();

        /***** afou pires oli tin aparaititi pliroforia zita apo sugkekrimeno broker to topic sou
         * gia na sou epistrepsei to value kai na to optikopoihseis ****/

        BrokerInfo brokerInfo = null;
        try {
            brokerInfo = mrt.call();
            mrt.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Broker myBroker = null;

        for (Broker broker : brokerInfo.getListOfBrokersResponsibilityLine().keySet()) {
            HashSet<Topic> mySet = brokerInfo.getListOfBrokersResponsibilityLine().get(broker);
            if (mySet.contains(topic)) {
                // an to mySet exei to topic krata to key
                myBroker = broker;
                break;
            }
        }

        /***** kane to REGISTER ston upeuthino broker ANOIGONTAS ENA SOCKET KAI PARE TIN PLIROFORIA ****/

        flag = 3; // send with flag 3 to the responsible broker. Write out the topic and the subscriber itself in order to be registered.

        Intent intentMyIntentService = new Intent(this, MyIntentService.class);
        intentMyIntentService.putExtra(MyIntentService.EXTRA_KEY_IN, "");
        intentMyIntentService.putExtra("flag", flag);
        intentMyIntentService.putExtra("broker", myBroker);
        intentMyIntentService.putExtra("topic", topic);
        intentMyIntentService.putExtra("subscriber", subscriber);
        startService(intentMyIntentService);

        myBroadcastReceiver = new MyBroadcastReceiver();

        //register BroadcastReceiver
        IntentFilter intentFilter = new IntentFilter(MyIntentService.ACTION_MyIntentService);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(myBroadcastReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //un-register BroadcastReceiver
        unregisterReceiver(myBroadcastReceiver);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng athens = new LatLng(37.994129, 23.731960);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(athens));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11), 2000, null);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
    }

    public class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int flag = (int) intent.getSerializableExtra("flag");

            if (flag == 0) { // Data received OK
                Data data = (Data) intent.getSerializableExtra("data");
                System.out.println("Received data from publisher :" + data);

                LatLng point = new LatLng(data.getValue().getLatitude(), data.getValue().getLongtitude());
                latlngs.add(point);

                if (!(vehicleIdMarkerTable.containsKey(data.getValue().getVehicleId()))) {

                    System.out.println("Add new marker based on vehicle ID incoming data");
                    markerOptions.position(point);
                    markerOptions.title(data.getTopic() + ", " + data.getValue().getVehicleId());
                    marker = mMap.addMarker(markerOptions);
                    markers.add(marker);
                    marker.showInfoWindow();

                    vehicleIdMarkerTable.put(data.getValue().getVehicleId(), marker);

                } else {

                    System.out.println("update marker");
                    Marker marker = vehicleIdMarkerTable.get(data.getValue().getVehicleId());
                    marker.setPosition(point);
                    marker.showInfoWindow();
                }
            }
            else if (flag == 1) { // Timeout occurred
                String timeoutMessage = (String)intent.getSerializableExtra("Timeout message");

                markerOptions.position(new LatLng(37.994129, 23.731960));
                markerOptions.title(timeoutMessage);
                marker = mMap.addMarker(markerOptions);
                marker.showInfoWindow();
            }
        }
    }
}