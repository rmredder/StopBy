package com.squad.stopby;

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
    ArrayList<LocationDB> locations = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Database db = new Database();
        db.getDatabase().getReference("Location").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                
                for(DataSnapshot child: children){
                    LocationDB value = child.getValue(LocationDB.class);
                    locations.add(value);
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

        //Set map to open up to UB
        LatLng buff = new LatLng(42.9993289, -78.7819876);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(buff, 14));

        //retrieve informaton sent from the Post class
        //Must check to make sure getExtras is not null, if null then app will crash
        if(getIntent().getExtras() != null)
        {
            Bundle extras = getIntent().getExtras();
            mMap.addMarker(new MarkerOptions().position(buff)
                    .title("You are here").snippet(extras.getString("event") + " " + extras.
                            getString("time") + " " + extras.getString("place")).icon
                            (BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        }
        else{
            mMap.addMarker(new MarkerOptions().position(buff)
                    .title("You are here").icon
                            (BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

        }

        mMap.addMarker(new MarkerOptions().position(new LatLng(42.9993917, -78.7912949))
                .title("User1").snippet("Study in Capen").icon
                        (BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
        mMap.addMarker(new MarkerOptions().position(new LatLng(43.000271,-78.784563))
                .title("User2").snippet("Hangout now Student Union").icon
                        (BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
        mMap.addMarker(new MarkerOptions().position(new LatLng(43.002907,-78.788082))
                .title("User3").snippet("Study in Capen").icon
                        (BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));






    }
}
