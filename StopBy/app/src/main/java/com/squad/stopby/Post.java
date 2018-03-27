package com.squad.stopby;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;


public class Post extends AppCompatActivity {

    private Database db;
    private DatabaseReference profileDatabaseReference;

    private EditText post_messageField;
    private Button postBtn;

    private String userLatitude;
    private String userLongitude;

    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        post_messageField = (EditText) findViewById(R.id.post_messageField);
        postBtn = (Button) findViewById(R.id.postbtn);

        //current user's location coordinate
        userLatitude = getIntent().getStringExtra("Latitude");
        userLongitude = getIntent().getStringExtra("Longitude");

        //Instance of Firebase
        db = new Database();
        profileDatabaseReference = db.getDatabaseReference().child("user profile");

        username = "";

        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent goToActivePost = new Intent(Post.this, ActivePost.class);
//                startActivity(goToActivePost);

                final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                profileDatabaseReference.child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        username = dataSnapshot.getValue(Profile.class).getUsername();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                Log.d("id value", username);
                String message = post_messageField.getText().toString();

                //send username and post message to the database
                //TODO need to pass correct username
                LocationDB locationDB = new LocationDB(username, message,
                        userLatitude, userLongitude);
                locationDB.pushToDatabase(db.getDatabaseReference());

                Toast.makeText(Post.this, "You have successfully posted!", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
