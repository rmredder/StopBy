package com.squad.stopby;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileChangeActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private EditText username_change_field;
    private EditText interest_change_field;
    private Button submitBtn;

    private DatabaseReference profileDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_change);

        toolbar = (Toolbar) findViewById(R.id.profileChange_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Edit Profile");

        username_change_field = (EditText) findViewById(R.id.change_username);
        interest_change_field = (EditText) findViewById(R.id.change_interest);
        submitBtn = (Button) findViewById(R.id.change_submitBtn);

        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        profileDatabase = FirebaseDatabase.getInstance().getReference().child("user profile").child(currentUser.getUid());
        //retrieve username and interest from the database and display them
        profileDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String username = dataSnapshot.child("name").getValue().toString();
                String interest = dataSnapshot.child("interest").getValue().toString();
                username_change_field.setText(username);
                interest_change_field.setText(interest);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        //update username and interest
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String new_username = username_change_field.getText().toString();
                String new_interest = interest_change_field.getText().toString();

                // ---------- need to revise ----------------

                if(!TextUtils.isEmpty(new_username) && !TextUtils.isEmpty(new_interest)) {

                    profileDatabase.child("name").setValue(new_username);
                    profileDatabase.child("interest").setValue(new_interest);
                    username_change_field.getText().clear();
                    interest_change_field.getText().clear();
                    Toast.makeText(ProfileChangeActivity.this, "update successful", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(ProfileChangeActivity.this, ":(", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
