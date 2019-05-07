package com.itshareplus.googlemapdemo;//package com.itshareplus.googlemapdemo;
//
//import android.Manifest;
//import android.app.ProgressDialog;
//import android.content.pm.PackageManager;
//import android.graphics.Color;
//import android.os.Bundle;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.app.FragmentActivity;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.TextView;
//
//import com.google.android.gms.maps.CameraUpdateFactory;
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.OnMapReadyCallback;
//import com.google.android.gms.maps.SupportMapFragment;
//import com.google.android.gms.maps.model.BitmapDescriptorFactory;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.Marker;
//import com.google.android.gms.maps.model.MarkerOptions;
//import com.google.android.gms.maps.model.Polyline;
//import com.google.android.gms.maps.model.PolylineOptions;
//
//import java.io.File;
//import java.io.IOException;
//import java.io.ObjectInputStream;
//import java.io.ObjectOutputStream;
//import java.io.UnsupportedEncodingException;
//import java.net.ServerSocket;
//import java.net.Socket;
//import java.net.UnknownHostException;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Map;
//
//import Chord.FileEntry;
//import Chord.Pair;
//import Modules.DirectionFinder;
//import Modules.DirectionFinderListener;
//import Modules.Route;
//
//public class MapsActivity2 extends FragmentActivity implements OnMapReadyCallback, DirectionFinderListener {
//
//    private GoogleMap mMap;
//    private Button btnFindPath;
//    private EditText etOrigin;
//    private EditText etDestination;
//    private List<Marker> originMarkers = new ArrayList<>();
//    private List<Marker> destinationMarkers = new ArrayList<>();
//    private List<Polyline> polylinePaths = new ArrayList<>();
//    private ProgressDialog progressDialog;
//
//    int flag;
//    static final int SERVER_PORT = 7000;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        setContentView(R.layout.activity_maps);
//        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);
//
//        btnFindPath = (Button) findViewById(R.id.btnFindPath);
//        etDestination = (EditText) findViewById(R.id.etDestination);
//
//
//        Subscriber subscriber = new Subscriber("192.168.1.101", Integer.parseInt(args[1]));
//        Topic topic = new Topic(args[0]);
//
//        // kane preRegister ton subscriber
//        subscriber.doThePreRegister();
//
//        // afou pires oli tin aparaititi pliroforia apo tous brokers kai gia poia kleidia einai upeuthinoi
//        // zita apo sugkekrimeno broker to topic sou gia na sou epistrepsei to value kai na to optikopoihseis
//        Broker broker = subscriber.findMyBrokerForMyTopic(topic);
//
//        // subscriber do the register(broker, topic)
//        subscriber.doTheRegister(broker, topic);
//
////        SubscriberListeningThread slt = new SubscriberListeningThread(subscriber.port); // Port h .getPort()?
////        slt.start();
//
//        subscriber.openServer();
//
//
//        btnFindPath.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                sendRequest();
//            }
//        });
//
//    }
//
//    public void openServer() {
//        ServerSocket providerSocket = null;
//        Socket connection = null;
//
//        try {
//            providerSocket = new ServerSocket(port);
//
//            while (true) {
//                connection = providerSocket.accept();
//
//                ObjectOutputStream out = new ObjectOutputStream(connection.getOutputStream());
//                ObjectInputStream in = new ObjectInputStream(connection.getInputStream());
//
//                int flag;
//
//                flag = in.readInt();
//
//                if(flag == 4) {
//                    Message message = (Message)in.readObject();
//                    System.out.println("Timestamp: " + System.currentTimeMillis() + "A message is coming " + message); // vazoume ena timestamp gia na fainetai oti ginetai parallila to consuming anamesa stous subscribers
//                }
//
//                in.close();
//                out.close();
//                connection.close();
//            }
//        } catch (IOException ioException) {
//            ioException.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                providerSocket.close();
//            } catch (IOException ioException) {
//                ioException.printStackTrace();
//            }
//        }
//    }
//
//    private void doTheRegister(Broker broker, Topic topic) {
//        Socket requestSocket = null;
//        ObjectOutputStream out = null;
//        ObjectInputStream in = null;
//
//        try {
//            requestSocket = new Socket(broker.getIpAddress(), broker.getPort());
//
//            out = new ObjectOutputStream(requestSocket.getOutputStream());
//            in = new ObjectInputStream(requestSocket.getInputStream());
//
//            int flagRegister = 3; // send flag 3 to responsible broker for topic in order to register subscriber and pull
//
//            try {
//
//                out.writeInt(flagRegister);
//                out.flush();
//
//                out.writeObject(this); // send the subscriber himself (this) to be registered
//                out.flush();
//
//                out.writeObject(topic);
//                out.flush();
//
//                // de tha exoume while in.read. Tha kleinei to connection kai tha kanoume expect se ena listening thread as poume
//                // oti tou stelnoume tou broker oti egw eimai se auto to ip, port (afou tou exw steilw to this), kai opote erthoun ta
//                // data ston broker na ta kanei push sto tade port me ena sugkekrimeno flag. egw tha perimenw na mou rthei ena tuple kai tha to kanw system.out.
//
//            } catch(Exception classNot){
//                System.err.println("data received in unknown format");
//                classNot.printStackTrace();
//            }
//        } catch (UnknownHostException unknownHost) {
//            System.err.println("You are trying to connect to an unknown host!");
//        } catch (IOException ioException) {
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
//    }
//
//    private Broker findMyBrokerForMyTopic(Topic topic) {
//        Broker myBroker = null;
//
//        for(Broker broker : brokerInfo.getListOfBrokersResponsibilityLine().keySet()) {
//            HashSet<Topic> mySet = brokerInfo.getListOfBrokersResponsibilityLine().get(broker);
//            if (mySet.contains(topic)) {
//                // an to mySet exei to topic krata to key
//                myBroker = broker;
//                break;
//            }
//        }
//        return myBroker;
//    }
//
//    private void doThePreRegister() {
//        Socket requestSocket = null;
//        ObjectOutputStream out = null;
//        ObjectInputStream in = null;
//
//        try {
//            requestSocket = new Socket("192.168.1.101", 7000);
//
//            out = new ObjectOutputStream(requestSocket.getOutputStream());
//            in = new ObjectInputStream(requestSocket.getInputStream());
//
//            int flagRegister = 2; // send flag 2 to broker 7000 in order to preRegister subscriber and receive all info about brokers and responsibilities
//
//            try {
//
//                out.writeInt(flagRegister);
//                out.flush();
//
//                // perimenw na mathw poioi einai oi upoloipoi brokers kai gia poia kleidia einai upeuthinoi
//                // diladi perimenw ena antikeimeno Info tis morfis {ListOfBrokers, <BrokerId, ResponsibilityLine>}
//
//                brokerInfo = (BrokerInfo)in.readObject();
//                System.out.println("Received from broker brokerinfo upon preregister: " + brokerInfo);
//
//
//            } catch(Exception classNot){
//                System.err.println("data received in unknown format");
//                classNot.printStackTrace();
//            }
//        } catch (UnknownHostException unknownHost) {
//            System.err.println("You are trying to connect to an unknown host!");
//        } catch (IOException ioException) {
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
//    }
//
//    private void sendRequest() {
//
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
//
//    }
//
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        mMap = googleMap;
//
//        LatLng center = new LatLng(38.9, 22.43333);
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, 6));
//
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        mMap.setMyLocationEnabled(true);
//    }
//
//    @Override
//    public void onDirectionFinderStart() {
//        progressDialog = ProgressDialog.show(this, "Please wait.",
//                "Finding direction..!", true);
//
//        if (originMarkers != null) {
//            for (Marker marker : originMarkers) {
//                marker.remove();
//            }
//        }
//
//        if (destinationMarkers != null) {
//            for (Marker marker : destinationMarkers) {
//                marker.remove();
//            }
//        }
//
//        if (polylinePaths != null) {
//            for (Polyline polyline:polylinePaths ) {
//                polyline.remove();
//            }
//        }
//    }
//
//    @Override
//    public void onDirectionFinderSuccess(List<Route> routes) {
//        progressDialog.dismiss();
//        polylinePaths = new ArrayList<>();
//        originMarkers = new ArrayList<>();
//        destinationMarkers = new ArrayList<>();
//
//        for (Route route : routes) {
//
//            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 6));
//            ((TextView) findViewById(R.id.tvDuration)).setText(route.duration.text);
//            ((TextView) findViewById(R.id.tvDistance)).setText(route.distance.text);
//
//            originMarkers.add(mMap.addMarker(new MarkerOptions()
//                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.geo_targeting_512))
//                    .title(route.startAddress)
//                    .position(route.startLocation)));
//            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
//                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.geo_targeting_512))
//                    .title(route.endAddress)
//                    .position(route.endLocation)));
//
//            PolylineOptions polylineOptions = new PolylineOptions().
//                    geodesic(true).
//                    color(Color.BLUE).
//                    width(10);
//
//            for (int i = 0; i < route.points.size(); i++)
//                polylineOptions.add(route.points.get(i));
//
//            polylinePaths.add(mMap.addPolyline(polylineOptions));
//        }
//    }
//}