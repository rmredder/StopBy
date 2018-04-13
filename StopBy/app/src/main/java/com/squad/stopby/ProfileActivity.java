package com.squad.stopby;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private CircleImageView profile_img;
    private TextView profile_name;
    private TextView profile_interst;
    private Button chatBtn;

    private DatabaseReference userDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profile_img = (CircleImageView) findViewById(R.id.chatroom_profileImg);
        profile_name = (TextView) findViewById(R.id.profile_name);
        profile_interst = (TextView) findViewById(R.id.profile_interest);
        chatBtn = (Button) findViewById(R.id.chatBtn);

        final String other_user_id = getIntent().getStringExtra("other_user_id");
        userDatabase = FirebaseDatabase.getInstance().getReference().child("user profile").child(other_user_id);
        userDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String imgUri = dataSnapshot.child("image").getValue().toString();
                String name = dataSnapshot.child("name").getValue().toString();
                String interest = dataSnapshot.child("interest").getValue().toString();

                //load user' profile pic
                if(imgUri.equals("default")) {

                    Picasso.with(ProfileActivity.this).load(R.drawable.default1).into(profile_img);

                } else {

                    Picasso.with(ProfileActivity.this).load(imgUri).into(profile_img);

                }

                //display user's name
                profile_name.setText(name);

                //display user's interest
                profile_interst.setText(interest);

                //if user opens their own profile, they would not see the chatBtn
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if(currentUser.getUid().equals(other_user_id)) {
                    chatBtn.setVisibility(View.INVISIBLE);
                } else {
                    chatBtn.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toChat = new Intent(ProfileActivity.this, ChatActivity.class);
                toChat.putExtra("other_user_id", other_user_id);
                startActivity(toChat);
            }
        });

    }
}
