package com.itshareplus.googlemapdemo;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.Serializable;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import Modules.DirectionFinderListener;
import Modules.Route;
import com.itshareplus.googlemapdemo.R;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, DirectionFinderListener, Serializable {

    private GoogleMap mMap;
    private Button btnFindPath;
    private EditText etOrigin;
    private EditText etDestination;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;

    int flag;
    int SubscriberPort = 9000;
    static final int SERVER_PORT = 7000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        btnFindPath = (Button) findViewById(R.id.btnFindPath);
        etDestination = (EditText) findViewById(R.id.etDestination);

        btnFindPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequest();
            }
        });

    }

    private void sendRequest() {

        Subscriber subscriber = new Subscriber("192.168.1.22", SubscriberPort++);


        /*** Dwse to busline (to topic diladi) gia to opoio endiaferesai ****/

        Topic topic = new Topic(etDestination.getText().toString());
        System.out.println(topic);

        /***** kane prwta to PRE REGISTER ****/

        flag = 2; // send flag 2 to broker 7000 in order to preRegister subscriber and receive all info about brokers and responsibilities
        MenuRequestThread mrt = new MenuRequestThread(SERVER_PORT, flag);
        mrt.start();

        /***** afou pires oli tin aparaititi pliroforia apo tous brokers kai gia poia kleidia einai upeuthinoi
          zita apo sugkekrimeno broker to topic sou gia na sou epistrepsei to value kai na to optikopoihseis ****/

        BrokerInfo brokerInfo = null;
        try {
            brokerInfo = mrt.call();
            mrt.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Broker myBroker = null;

        for(Broker broker : brokerInfo.getListOfBrokersResponsibilityLine().keySet()) {
            HashSet<Topic> mySet = brokerInfo.getListOfBrokersResponsibilityLine().get(broker);
            if (mySet.contains(topic)) {
                // an to mySet exei to topic krata to key
                myBroker = broker;
                break;
            }
        }


        /***** kane to REGISTER ****/

        flag = 3; // send with flag 3 to the responsible broker. Write out the topic and the subscriber itself in order to be registered.
        MenuRequestThread mrt2 = new MenuRequestThread(SERVER_PORT, flag, myBroker, topic, subscriber);
        mrt2.start();

    }



//        List<String> destinations = Arrays.asList(etDestination.getText().toString().split(","));
//
//        List<Pair> pairNonExisting = new ArrayList<>();
//        Map<Pair, String> pairCachedDestinations = new HashMap<>();
//
//        for(int i=0;i<destinations.size()-1;i++){
//
//            String origin = destinations.get(i);
//            String destination = destinations.get(i+1);
//            System.out.println("Initiating a separate request for a pair: " + origin + ", " + destination);
//
//            String filename = origin+"_"+destination;
//            File file = new File(filename);
//
//            Pair pair = new Pair(origin, destination);
//
//            System.out.println("Beginning a request for " + filename);
//            FileEntry fileEntry = new FileEntry(file, null, origin, destination);
//
//            flag = 3;
//
//            System.out.println("Beginning Request ");
//            MenuRequestThread mrt = new MenuRequestThread(fileEntry, SERVER_PORT, flag, this);
//            mrt.start();
//
//            FileEntry requestedFile = null;
//            try {
//                requestedFile = mrt.call();
//                mrt.join();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//
//            if (requestedFile.getFileData() != null) {
//                //We have the file, no need for Google API
//                //parse the file
//
//                System.out.println("We found the requested file in our memcached system. Filename: " + requestedFile);
//
//                //this.onDirectionFinderStart();//maybe put that just before the request on the sendRequest
//
//                pairCachedDestinations.put(pair, requestedFile.getFileData());
//                //Util.parseJSon(this, dataFiles);
//
//            } else {
//                pairNonExisting.add(pair);
//            }
//        }
//
////steile mia lista me non existing kai ena map me ta cched wste na ginoune ola mazi
//
//        List<FileEntry> commitFileList = null;// tha paroume mia lista apo potential commit files.
//        try {
//            commitFileList = new DirectionFinder(this, pairNonExisting, pairCachedDestinations).execute();
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
///*
//        System.out.println("Files to commit is of size " + commitFileList.size());
//        System.out.println("And the first file is " + commitFileList.get(0).getFileData());
//*/
//
//        //commit
//        flag = 2;
//        for(FileEntry fileEntryToCommit : commitFileList){
//            MenuRequestThread mrt2 = new MenuRequestThread(fileEntryToCommit, SERVER_PORT, flag, null);
//            mrt2.start();
//        }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng center = new LatLng(38.9, 22.43333);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, 6));

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

    @Override
    public void onDirectionFinderStart() {
        progressDialog = ProgressDialog.show(this, "Please wait.",
                "Finding direction..!", true);

        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }

        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }

        if (polylinePaths != null) {
            for (Polyline polyline:polylinePaths ) {
                polyline.remove();
            }
        }
    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        progressDialog.dismiss();
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();

        for (Route route : routes) {

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 6));
            ((TextView) findViewById(R.id.tvDuration)).setText(route.duration.text);
            ((TextView) findViewById(R.id.tvDistance)).setText(route.distance.text);

            originMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.geo_targeting_512))
                    .title(route.startAddress)
                    .position(route.startLocation)));
            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.geo_targeting_512))
                    .title(route.endAddress)
                    .position(route.endLocation)));

            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.BLUE).
                    width(10);

            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));

            polylinePaths.add(mMap.addPolyline(polylineOptions));
        }
    }


}