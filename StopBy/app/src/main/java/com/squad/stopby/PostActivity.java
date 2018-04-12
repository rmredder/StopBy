package com.squad.stopby;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;


public class PostActivity extends AppCompatActivity {

    private Database db;
    private DatabaseReference profileDatabaseReference;

    private EditText post_messageField;
    private Button postBtn;

    LocationManager locationManager;
    LocationListener locationListener;

    private String userLatitude;
    private String userLongitude;
    private String chooseLocation = null;

    private String username;

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
        setContentView(R.layout.activity_post);

        Spinner locationSpinner = findViewById(R.id.spinner);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(PostActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.choose_location_spinner));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationSpinner.setAdapter(adapter);

        locationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch(position){
                    case 0:
                        chooseLocation = null;
                        break;
                    case 1:
                        chooseLocation = getString(R.string.capen);
                        break;
                    case 2:
                        chooseLocation = getString(R.string.davis);
                        break;
                    case 3:
                        chooseLocation = getString(R.string.lockwood);
                        break;
                    case 4:
                        chooseLocation = getString(R.string.su);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        post_messageField = (EditText) findViewById(R.id.post_messageField);
        postBtn = (Button) findViewById(R.id.postbtn);

        //current user's location coordinate
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.e("Location: ", location.toString());
                userLatitude = Double.toString(location.getLatitude());
                userLongitude = Double.toString(location.getLongitude());
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
            userLatitude = Double.toString(firstLocation.getLatitude());
            userLongitude = Double.toString(firstLocation.getLongitude());

        }

        //Instance of Firebase
        db = new Database();
        profileDatabaseReference = db.getDatabaseReference().child("user profile");

        //retrieve the username and stored it in the location part of the database alongside with lat and long
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        profileDatabaseReference.child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Profile value = dataSnapshot.getValue(Profile.class);
                username = value.getName();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String message = post_messageField.getText().toString();

                if(chooseLocation == null){
                    //send username and post message to the database
                    LocationDB locationDB = new LocationDB(username, message, userLatitude, userLongitude);
                    locationDB.pushToDatabase(db.getDatabaseReference());


                }else{
                    //todo push to db
                }

                //clear the message textview
                post_messageField.setText(null);
                Toast.makeText(PostActivity.this, "You have successfully posted!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(PostActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });
    }
}
