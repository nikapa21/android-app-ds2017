package com.itshareplus.googlemapdemo;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
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
import java.util.ArrayList;
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

        /***** kanei prwta to PRE REGISTER ****/

        MenuRequestThread mrt = new MenuRequestThread(SERVER_PORT);
        mrt.start();

//        Socket requestSocket = null;
//        ObjectOutputStream out = null;
//        ObjectInputStream in = null;
//
//        try {
//
//            //Create a socket to the MasterNode ip and port (7000):
//            requestSocket = new Socket("192.168.1.101", SERVER_PORT);
//            //System.out.println("menu is opening a socket to the master node's port " + port);//debug
//
//            String myIp = "192.168.1.101";
//            System.out.println("app has an IP " + myIp);
//
//            // Get input and output streams
//            out = new ObjectOutputStream(requestSocket.getOutputStream());
//            in = new ObjectInputStream(requestSocket.getInputStream());
//
//            //First step is to send the flag to the Master - Which action we will take.
//            out.writeInt(flag);
//            out.flush();
//
//            if (flag == 10) { // speak with Broker 7000 and do the Pre Register
//
//                String txt = (String) in.readObject();
//                brokerInfo = (BrokerInfo)in.readObject();
//                System.out.println("Received from broker brokerinfo upon preregister: " + brokerInfo);
//
//                System.out.println(txt);
//            }
//
//        } catch (UnknownHostException unknownHost) {
//            System.err.println("You are trying to connect to an unknown host!");
//        } catch (Exception ioException) {
//            ioException.printStackTrace();
//        } finally {
//            try {
//                in.close();
//                out.close();
//                requestSocket.close();
//            } catch (IOException ioException) {
//                ioException.printStackTrace();
//            }
//        }



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