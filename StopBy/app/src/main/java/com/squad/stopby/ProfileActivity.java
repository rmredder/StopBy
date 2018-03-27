package com.squad.stopby;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class ProfileActivity extends AppCompatActivity {

    private LinearLayout profile_username;
    private LinearLayout profile_email;
    private LinearLayout profile_userInfo;

    private TextView profile_usernameField;
    private TextView profile_emialField;
    private TextView profile_userInfoField;

    private String username;
    private String email;
    private String userInfo;

    private Database db;
    private DatabaseReference profileDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profile_username = (LinearLayout) findViewById(R.id.profile_username);
        profile_email = (LinearLayout)  findViewById(R.id.profile_email);
        profile_userInfo = (LinearLayout) findViewById(R.id.profile_userInfo);

        profile_usernameField = (TextView) profile_username.getChildAt(1);
        profile_emialField = (TextView) profile_email.getChildAt(1);
        profile_userInfoField = (TextView) profile_userInfo.getChildAt(1);


        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        db = new Database();
        profileDatabase = db.getDatabaseReference().child("user profile");
        profileDatabase.child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Profile value = dataSnapshot.getValue(Profile.class);
                username = value.getUsername();
                email = value.getEmail();
                userInfo = value.getUserInfo();
                profile_usernameField.setText(username);
                profile_emialField.setText(email);
                profile_userInfoField.setText(userInfo);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }
}
