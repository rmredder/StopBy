package com.squad.stopby;

import android.*;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private ArrayList<LocationDB> locations = new ArrayList<LocationDB>();
    private Database db;

    private LocationManager locationManager;
    private LocationListener locationListener;

    private DatabaseReference profileDatabaseReference;
    private String username = "";

    private static final double coordinate_offset = 0.00002f;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        GetUserName();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getUsersLocation();
        mMap.setMyLocationEnabled(true);
        addMarkers();
        SetMarkerListener();
        NearbyUsers();
    }

    public void queryUser(String user){
        DatabaseReference dbRef = new Database().getDatabaseReference();

        Query query = dbRef.child("user profile").orderByChild("name").equalTo(user);

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
                            TextView userName = mView.findViewById(R.id.user_name);
                            TextView userInfo = mView.findViewById(R.id.user_info);
                            ImageView userImage = mView.findViewById(R.id.user_image);
                            TextView close = mView.findViewById(R.id.txtclose);
                            String quote = "\"";
                            userName.setText(usersProfile.getName());
                            userInfo.setText(quote + " " + usersProfile.getInterest() + " " + quote);
                            String imgUrl = usersProfile.getImage();
                            if(imgUrl.equals("default")) {

                                Picasso.with(MapsActivity.this).load(R.drawable.default1).into(userImage);

                            } else {

                                Picasso.with(MapsActivity.this).load(imgUrl).into(userImage);

                            }

                            mBuilder.setView(mView);
                            final AlertDialog dialog = mBuilder.create();
                            dialog.show();

                            close.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

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

    public void SetMarkerListener(){
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                switch(marker.getTitle()) {

                    case "Capen":
                        Intent toCapen = new Intent(MapsActivity.this, AvailablePostsCapenActivity.class);
                        startActivity(toCapen);
                        break;

                    case "Davis":
                        Intent toDavis = new Intent(MapsActivity.this, AvailablePostsDavisActivity.class);
                        startActivity(toDavis);
                        break;

                    case "Lockwood":
                        Intent toLockwood= new Intent(MapsActivity.this, AvailablePostsLockwoodActivity.class);
                        startActivity(toLockwood);
                        break;
                    case "Student Union":
                        Intent toStudentUnion = new Intent(MapsActivity.this, AvailablePostsSuActivity.class);
                        startActivity(toStudentUnion);
                        break;
                    default: queryUser(marker.getTitle());
                }

                return false;

            }
        });
    }

    private void GetUserName(){
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
    }

    private void getUsersLocation(){
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        //cannot access map without granting permission
        @SuppressLint("MissingPermission") Task location = mFusedLocationProviderClient.getLastLocation();
        location.addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful()){
                    //if location returns null default to UB
                    Location currentLocation = (Location) task.getResult();
                    if(currentLocation != null){
                        mMap.moveCamera(CameraUpdateFactory.
                                newLatLngZoom(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), 15));
                    }else{
                        mMap.moveCamera(CameraUpdateFactory.
                                newLatLngZoom(new LatLng(43.000870, -78.789746), 15));
                    }
                }else{
                    mMap.moveCamera(CameraUpdateFactory.
                            newLatLngZoom(new LatLng(43.000870, -78.789746), 15));
                }
            }
        });
    }

    public void NearbyUsers(){
        db.getDatabase().getReference("location").child("currentlocation").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();

                for(DataSnapshot child: children){
                    LocationDB value = child.getValue(LocationDB.class);
                    locations.add(value);
                }
                mMap.clear();
                addMarkers();
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
}
