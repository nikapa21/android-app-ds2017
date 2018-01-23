package com.itshareplus.googlemapdemo;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

import org.json.JSONException;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Chord.FileEntry;
import Modules.DirectionFinder;
import Modules.DirectionFinderListener;
import Modules.Route;
import Modules.Util;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, DirectionFinderListener {

    private GoogleMap mMap;
    private Button btnFindPath;
    private EditText etOrigin;
    private EditText etDestination;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;

    int flag;
    static final int SERVER_PORT = 7777;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        btnFindPath = (Button) findViewById(R.id.btnFindPath);
        etOrigin = (EditText) findViewById(R.id.etOrigin);
        etDestination = (EditText) findViewById(R.id.etDestination);

        btnFindPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMultipleHardcodedRequests();
            }
        });

    }

    private void sendMultipleHardcodedRequests() {

        String filename = "athens_lamia";
        File file = new File(filename);

        System.out.println("Beginning a request for athens_lamia ");
        FileEntry fileEntry = new FileEntry(file, null, "athens", "lamia");

        flag = 3;

        System.out.println("Beginning Request ");
        MenuRequestThread mrt = new MenuRequestThread(fileEntry, SERVER_PORT, flag, this);
        mrt.start();

        FileEntry requestedFile = null;
        try {
            requestedFile = mrt.call();
            mrt.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String filename2 = "lamia_ioannina";
        File file2 = new File(filename2);

        System.out.println("Beginning a request for lamia_ioannina ");
        FileEntry fileEntry2 = new FileEntry(file2, null, "lamia", "ioannina");

        flag = 3;

        System.out.println("Beginning Request ");
        MenuRequestThread mrt2 = new MenuRequestThread(fileEntry2, SERVER_PORT, flag, this);
        mrt2.start();

        FileEntry requestedFile2 = null;
        try {
            requestedFile2 = mrt2.call();
            mrt2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //We have the file, no need for Google API
            //parse the file

        System.out.println("We found the requested file in our memcached system. Filename: " + requestedFile2);

        this.onDirectionFinderStart();//maybe put that just before the request on the sendRequest

        try {

            List<String> dataFiles = new ArrayList<>();
            dataFiles.add(requestedFile.getFileData());
            dataFiles.add(requestedFile2.getFileData());
            Util.parseJSon(this, dataFiles);

        } catch (JSONException e) {
            e.printStackTrace();
        }



    }

    private void sendRequest() {
        String origin = etOrigin.getText().toString();
        String destination = etDestination.getText().toString();

        List<String> destinations = Arrays.asList(destination.split(","));

        /*for(int i=0;i<destinations.size()-1;i++){
            System.out.println("Initiating a separate request for a pair: " + destinations.get(i) + ", " + destinations.get(i+1));
            // tha prepei na kanw n-1 diaforetika requestthread h edw h eksw apo ti loop
        }*/

        if (origin.isEmpty()) {
            Toast.makeText(this, "Please enter origin address!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (destination.isEmpty()) {
            Toast.makeText(this, "Please enter destination address!", Toast.LENGTH_SHORT).show();
            return;
        }

        String filename = origin+"_"+destination;
        File file = new File(filename);

        System.out.println("Beginning a request for athens_lamia ");
        FileEntry fileEntry = new FileEntry(file, null, origin, destination);

        flag = 3;

        System.out.println("Beginning Request ");
        MenuRequestThread mrt = new MenuRequestThread(fileEntry, SERVER_PORT, flag, this);
        mrt.start();

        FileEntry requestedFile = null;
        try {
            requestedFile = mrt.call();
            mrt.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (requestedFile.getFileData() == null) {

            System.out.println("The file " + requestedFile.getFile() + " doesn't exist. We will ask Google for help...");
            //TODO find the file from the Google API
            //MapsActivity.sendReplyFromChord(fileExists);

            FileEntry commitFile = null;

            try {
                commitFile = new DirectionFinder(this, requestedFile.getOrigin(), requestedFile.getDestination()).execute();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            System.out.println("File to commit is " + commitFile);

            //commit
            flag = 2;
            MenuRequestThread mrt2 = new MenuRequestThread(commitFile, SERVER_PORT, flag, null);
            mrt2.start();


        } else {//We have the file, no need for Google API
            //parse the file

            System.out.println("We found the requested file in our memcached system. Filename: " + requestedFile);

            this.onDirectionFinderStart();//maybe put that just before the request on the sendRequest

            try {

                List<String> dataFiles = new ArrayList<>();
                dataFiles.add(requestedFile.getFileData());
                Util.parseJSon(this, dataFiles);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng athens = new LatLng(37.984338, 23.730271);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(athens, 12));
        originMarkers.add(mMap.addMarker(new MarkerOptions()
                .title("Athens city centre")
                .position(athens)));

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
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 16));
            ((TextView) findViewById(R.id.tvDuration)).setText(route.duration.text);
            ((TextView) findViewById(R.id.tvDistance)).setText(route.distance.text);

            originMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue))
                    .title(route.startAddress)
                    .position(route.startLocation)));
            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green))
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