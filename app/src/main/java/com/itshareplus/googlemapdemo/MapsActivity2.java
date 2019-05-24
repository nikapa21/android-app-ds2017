//package com.itshareplus.googlemapdemo;
//
//import android.Manifest;
//import android.content.pm.PackageManager;
//import android.os.Bundle;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.app.FragmentActivity;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//
//import com.google.android.gms.maps.CameraUpdateFactory;
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.OnMapReadyCallback;
//import com.google.android.gms.maps.SupportMapFragment;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.Marker;
//import com.google.android.gms.maps.model.MarkerOptions;
//
//import java.util.ArrayList;
//
//public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
//
//    private GoogleMap mMap;
//    private Button btnFindPath;
//    private EditText etDestination;
//    private ArrayList<Marker> markers = new ArrayList<>();
//    private ArrayList<LatLng> latlngs = new ArrayList<>();
//    private MarkerOptions markerOptions = new MarkerOptions();
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_maps);
//        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);
//
//        btnFindPath = (Button) findViewById(R.id.btnFindPath);
//        etDestination = (EditText) findViewById(R.id.etDestination);
//    }
//
//
//    /**
//     * Manipulates the map once available.
//     * This callback is triggered when the map is ready to be used.
//     * This is where we can add markers or lines, add listeners or move the camera. In this case,
//     * we just add a marker near Sydney, Australia.
//     * If Google Play services is not installed on the device, the user will be prompted to install
//     * it inside the SupportMapFragment. This method will only be triggered once the user has
//     * installed Google Play services and returned to the app.
//     */
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        mMap = googleMap;
//        latlngs.add(new LatLng(37.994129, 23.731960));
//        latlngs.add(new LatLng(37.9757, 23.7339));
//
//        int i = 1;
//        for (LatLng point : latlngs) {
//            markerOptions.position(point);
//            markerOptions.title("POI" + i);
//
//            markerOptions.snippet("test");
//            markers.add(mMap.addMarker(markerOptions));
//            i++;
//        }
//        i = 1;
//        for (Marker m : markers) {
//            m.setTag(i);
//            i++;
//        }
//
//
//        LatLng athens = new LatLng(37.994129, 23.731960);
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(athens));
//        mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
//
//        final Button button = findViewById(R.id.button);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                for (Marker m : markers) {
//                    if (m.getTag().equals(1)) {
//                        m.setVisible(!m.isVisible());
//                    }
//                }
//                if (markers.get(0).isVisible()) {
//                    button.setText("Hide");
//                } else {
//                    button.setText("Hidden");
//                }
//            }
//        });
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
//        mMap.setTrafficEnabled(true);
//        mMap.setMyLocationEnabled(true);
//        mMap.getUiSettings().setMyLocationButtonEnabled(true);
//
//        // Add a marker in Sydney and move the camera
//        //LatLng sydney = new LatLng(-34, 151);
//        //Marker m = mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        //m.setRotation((float)5);
//        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
//    }
//}
