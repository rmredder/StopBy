package com.squad.stopby;

import android.content.Intent;
import android.location.Location;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    ArrayList<LocationDB> locations = new ArrayList<LocationDB>();
    String userLatitude;
    String userLongitude;
    Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        userLatitude = getIntent().getStringExtra("Latitude");
        userLongitude = getIntent().getStringExtra("Longitude");

        db = new Database();
        db.getDatabase().getReference("Location").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                
                for(DataSnapshot child: children){
                    LocationDB value = child.getValue(LocationDB.class);
                    locations.add(value);
                }

                for(LocationDB loc: locations) {
                    mMap.addMarker(new MarkerOptions().position(new LatLng(loc.getLatitude(), loc.getLongitude()))
                            .title(loc.getUsername())
                            .icon(BitmapDescriptorFactory
                                    .defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Set map to open up to users location
        LatLng buff = new LatLng(Double.parseDouble(userLatitude), Double.parseDouble(userLongitude));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(buff, 14));

        //Put marker for user on the map
        mMap.addMarker(new MarkerOptions().position(buff)
                .title("You are here").icon
                        (BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

        db.getDatabaseReference().keepSynced(true);

        for(LocationDB loc: locations){
            mMap.addMarker(new MarkerOptions().position(new LatLng(loc.getLatitude(), loc.getLongitude()))
                    .title(loc.getUsername())
                    .icon(BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
        }
    }
}
