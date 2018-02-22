package com.squad.stopby;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Set map to open up to UB
        LatLng buff = new LatLng(42.9993289, -78.7819876);
        mMap.animateCamera(CameraUpdateFactory.zoomBy((float).5));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(buff));


        mMap.addMarker(new MarkerOptions().position(new LatLng(43.000710, -78.793274))
                .title("User1"));
        mMap.addMarker(new MarkerOptions().position(new LatLng(43.000271,-78.784563))
                .title("User2"));
        mMap.addMarker(new MarkerOptions().position(new LatLng(43.002907,-78.788082))
                .title("User3"));






    }
}
