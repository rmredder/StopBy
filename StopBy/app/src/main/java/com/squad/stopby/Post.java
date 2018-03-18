package com.squad.stopby;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Post extends AppCompatActivity {
    private RadioGroup radioGroup;
    private RadioButton hangoutButton;
    private RadioButton studyButton;
    private EditText placeField;
    private Button postButton;
    private EditText timeField;

    LocationManager locationManager;
    LocationListener locationListener;

    //private FusedLocationProviderClient mFusedLocationClient;

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
                       // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                    }
                }
                return;
            }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        final EditText message = findViewById(R.id.timeField);

        //TODO this section will get users location, need to convert this to something storable to put in database
        /*
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object

                        }
                    }
                });
*/
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.i("location", location.toString());
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        //Check for permission to get users location
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }else{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }

        //Instance of Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = database.getReferenceFromUrl("https://stopby-196918.firebaseio.com/");

        Button postButton =  findViewById(R.id.postButton);

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToActivePost = new Intent(Post.this, ActivePost.class);
                startActivity(goToActivePost);

                String msg = message.getText().toString();
                //TODO pass Username and location data
                //send username and post message to the database
                databaseReference.child("Location").child("User3").child("Post").setValue(msg);
                databaseReference.child("Location").child("User3").child("Coordinates").setValue(12345);

            }
        });
    }

    /*
    public void post2Map(String event, String time, String place) {
        Intent intent= new Intent(this, MapsActivity.class);
        Bundle extras = new Bundle();
        extras.putString("event", event);
        extras.putString("time", time);
        extras.putString("place", place);
        intent.putExtras(extras);
        startActivity(intent);
    }*/

    //To get the value of the clicked radio button
    public String getValueOfClickedButton() {
        int clickedButtonId = radioGroup.getCheckedRadioButtonId();
        RadioButton clickedButton = findViewById(clickedButtonId);
        return clickedButton.getText().toString();
    }


    public void pushLocation(Double location, String username, String post, DatabaseReference ref)
    {

        ref.child("Location").child(username).setValue(post);
    }
}
