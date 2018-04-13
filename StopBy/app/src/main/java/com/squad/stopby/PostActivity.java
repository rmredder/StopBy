package com.squad.stopby;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class PostActivity extends AppCompatActivity {

    private Database db;
    private DatabaseReference profileDatabaseReference;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    private TextView current_post;
    private EditText post_messageField;
    private Button postBtn;
    private Button deactivateBtn;

    private String userLatitude;
    private String userLongitude;
    private String chooseLocation = null;

    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        getUsersLocation();
        GetUserName();

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
        current_post = (TextView) findViewById(R.id.current_post);
        postBtn = (Button) findViewById(R.id.postbtn);
        deactivateBtn = (Button) findViewById(R.id.deactivate_post);

        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String message = post_messageField.getText().toString();
                boolean posted = false;

                if(chooseLocation == null && username != null){
                    //send username and post message to the database
                    LocationDB locationDB = new LocationDB(username, message, userLatitude, userLongitude);
                    locationDB.pushToDatabase(db.getDatabaseReference());
                    posted = true;
                }else if(chooseLocation != null && username != null){
                        Post post = new Post(username, message);
                        post.pushToDatabase(db.getDatabaseReference(), chooseLocation);
                        posted = true;
                }

                //clear the message textview
                if(posted){
                    post_messageField.setText(null);
                    Toast.makeText(PostActivity.this, "You have successfully posted!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(PostActivity.this, MapsActivity.class);
                    postBtn.setVisibility(View.INVISIBLE);
                    deactivateBtn.setVisibility(View.VISIBLE);
                    startActivity(intent);
                }else{
                    post_messageField.setText(null);
                    Toast.makeText(PostActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }

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
                    if(currentLocation != null) {
                        userLatitude = Double.toString(currentLocation.getLatitude());
                        userLongitude = Double.toString(currentLocation.getLongitude());
                    }else{
                        userLatitude = "43.000411";
                        userLongitude = "-78.787045";
                    }
                }
            }
        });
    }
}
