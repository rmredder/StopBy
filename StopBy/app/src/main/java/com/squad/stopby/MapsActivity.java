package com.squad.stopby;

import android.*;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ArrayList<LocationDB> locations = new ArrayList<LocationDB>();
    private Database db;

    private LocationManager locationManager;
    private LocationListener locationListener;

    private static final double coordinate_offset = 0.00002f;

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        // If request is cancelled, the result arrays are empty.
        if(requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    //TODO edit min time and min distance for battery efficiency purposes
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                }
            }
        }
        return;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        db = new Database();
        db.getDatabase().getReference("Location").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();

                for(DataSnapshot child: children){
                    LocationDB value = child.getValue(LocationDB.class);
                    locations.add(value);
                    Log.d("CREATION", value.getLatitude());
                    Log.d("CREATION", value.getLongitude());
                }

                for(LocationDB loc: locations) {
                    mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(loc.getLatitude()) - locations.indexOf(loc) * coordinate_offset, Double.parseDouble(loc.getLongitude()) - locations.indexOf(loc) * coordinate_offset))
                            .title(loc.getUsername())
                            .snippet(loc.getPost())
                            .icon(BitmapDescriptorFactory
                                    .defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.e("Location: ", location.toString());
                //Set map to open up to users location
                LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                //mMap.clear(); //this clears map of markers
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 16));

                //Put marker for user on the map
                mMap.addMarker(new MarkerOptions().position(userLocation)
                        .title("You are here").icon
                                (BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        //Check for permission to get users location
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }else{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            Location firstLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            LatLng userLocation = new LatLng(firstLocation.getLatitude(), firstLocation.getLongitude());
            //mMap.clear(); //this clears map of markers
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 16));

            //Put marker for user on the map
            mMap.addMarker(new MarkerOptions().position(userLocation)
                    .title("You are here").icon
                            (BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        }

    }
}
