package com.squad.stopby;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class Post extends AppCompatActivity {
    private RadioGroup radioGroup;
    private RadioButton hangoutButton;
    private RadioButton studyButton;
    private EditText placeField;
    private Button postButton;
    private EditText timeField;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Location userLocation;

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
                       // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //TODO edit min time and min distance for battery efficiency purposes
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

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.i("Location", location.toString());
                userLocation = location;
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
        final Database database = new Database();

        Button postButton =  findViewById(R.id.postButton);

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToActivePost = new Intent(Post.this, ActivePost.class);
                startActivity(goToActivePost);

                String msg = message.getText().toString();

                //send username and post message to the database
                //TODO need to pass correct username
                LocationDB locationDB = new LocationDB("user1", msg, userLocation.getLatitude(), userLocation.getLongitude());
                locationDB.pushToDatabase(database.getDatabaseReference());

            }
        });
    }

    //To get the value of the clicked radio button /// Not sure if we will be needing this
    public String getValueOfClickedButton() {
        int clickedButtonId = radioGroup.getCheckedRadioButtonId();
        RadioButton clickedButton = findViewById(clickedButtonId);
        return clickedButton.getText().toString();
    }

}
