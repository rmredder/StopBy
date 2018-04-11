package com.squad.stopby;

import android.*;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ArrayList<LocationDB> locations = new ArrayList<LocationDB>();
    private Database db;

    private LocationManager locationManager;
    private LocationListener locationListener;

    private DatabaseReference profileDatabaseReference;
    private String username = "";

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
        profileDatabaseReference = db.getDatabaseReference().child("user profile");

        //retrieve the username and stored it in the location part of the database alongside with lat and long
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        profileDatabaseReference.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Profile value = dataSnapshot.getValue(Profile.class);
                if(value.getName() != null){
                    username = value.getName();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        db.getDatabase().getReference("Location").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();

                for(DataSnapshot child: children){
                    LocationDB value = child.getValue(LocationDB.class);
                    locations.add(value);
                }
                mMap.clear();
                for(LocationDB loc: locations) {
                    if(username != "" && !(loc.getUsername().equals(username))){
                        mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(loc.getLatitude()) - locations.indexOf(loc) * coordinate_offset, Double.parseDouble(loc.getLongitude()) - locations.indexOf(loc) * coordinate_offset))
                                .title(loc.getUsername())
                                .snippet(loc.getPost())
                                .icon(BitmapDescriptorFactory
                                        .defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
                    }
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
                mMap.clear();
                //Put marker for user on the map
                addMarkers();
                mMap.addMarker(new MarkerOptions().position(userLocation)
                        .title("You are here").icon
                                (BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                for(LocationDB loc: locations) {
                    if(username != "" && !(loc.getUsername().equals(username))){
                        mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(loc.getLatitude()) - locations.indexOf(loc) * coordinate_offset, Double.parseDouble(loc.getLongitude()) - locations.indexOf(loc) * coordinate_offset))
                                .title(loc.getUsername())
                                .snippet(loc.getPost())
                                .icon(BitmapDescriptorFactory
                                        .defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
                    }
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            @Override
            public void onProviderEnabled(String provider) {}

            @Override
            public void onProviderDisabled(String provider) {}
        };

        //Check for permission to get users location
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }else{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            Location firstLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(firstLocation != null)
            {
                LatLng userLocation = new LatLng(firstLocation.getLatitude(), firstLocation.getLongitude());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 16));
                //Put marker for user on the map
                Log.e("first location: ", "first");
                mMap.addMarker(new MarkerOptions().position(userLocation)
                        .title("You are here").icon
                                (BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            }
            else{ Log.e("Location = ", " null"); }

        }

//        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
//            @Override
//            public void onInfoWindowClick(Marker marker) {
//                if(marker != null){
//                    CharSequence findThisUser = marker.getTitle();
//                    String thisUser = marker.getTitle();
//                    //Toast.makeText(getBaseContext(), findThisUser, Toast.LENGTH_SHORT).show();
//                    queryUser(thisUser);
//
//                }
//            }
//        });
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                switch(marker.getTitle()) {

                    case "Capen":
                        Intent toCapen = new Intent(MapsActivity.this, AvailablePostsCapenActivity.class);
                        startActivity(toCapen);
                        break;

                    case "Davis":
                        Toast.makeText(MapsActivity.this, "Davis", Toast.LENGTH_SHORT).show();
                        break;

                    case "Lockwood":
                        Toast.makeText(MapsActivity.this, "Lockwood", Toast.LENGTH_SHORT).show();
                        break;

                    case "Student Union":
                        Toast.makeText(MapsActivity.this, "Student Union", Toast.LENGTH_SHORT).show();
                        break;

                }

                return false;

            }
        });

    }

    public void queryUser(String user){
        DatabaseReference dbRef = new Database().getDatabaseReference();

        Query query = dbRef.child("user profile").orderByChild("username").equalTo(user);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot singleSnapShot: dataSnapshot.getChildren()){
                        if(singleSnapShot.exists()){

                            //This is profile object that should be displayed in popup info window
                            Profile usersProfile = singleSnapShot.getValue(Profile.class);
                            final AlertDialog.Builder mBuilder = new AlertDialog.Builder(MapsActivity.this);
                            final View mView = getLayoutInflater().inflate(R.layout.popup_window, null);
                            TextView userName = mView.findViewById(R.id.textView);
                            TextView userInfo = mView.findViewById(R.id.textView4);
                            Button close = mView.findViewById(R.id.button4);

//                            userName.setText(usersProfile.getName());
//                            userInfo.setText(usersProfile.getUserInfo());

                            mBuilder.setView(mView);
                            final AlertDialog dialog = mBuilder.create();
                            dialog.show();

                            close.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });


//                            Log.e("User Found: ", singleSnapShot.getValue(Profile.class).getUsername());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        //return usersProfile;

    }

    //adds hardcoded markers for Capen, Lockwood, SU, and Davis
    public void addMarkers(){
        //capen
        mMap.addMarker(new MarkerOptions().position(new LatLng(43.000911, -78.789725))
                .title("Capen").icon
                        (BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        //davis
        mMap.addMarker(new MarkerOptions().position(new LatLng(43.002685, -78.787148))
                .title("Davis").icon
                        (BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        //lockwood
        mMap.addMarker(new MarkerOptions().position(new LatLng(43.000236, -78.786011))
                .title("Lockwood").icon
                        (BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        //SU
        mMap.addMarker(new MarkerOptions().position(new LatLng(43.001089, -78.786095))
                .title("Student Union").icon
                        (BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
    }
}
