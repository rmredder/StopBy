package com.squad.stopby;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Menu extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Button search = findViewById(R.id.search);
        Button post = findViewById(R.id.post);

        // get instance of firebase
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = database.getReferenceFromUrl("https://stopby-196918.firebaseio.com/");

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //call find users and go to map page
                database.getReference("Location").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String value = dataSnapshot.getValue(String.class);
                        //txtValue.setText(value);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                post(view);
            }
        });
    }

    public void post(View view){
        Intent goToPost = new Intent(this, Post.class);
        startActivity(goToPost);
    }

    public void findUsers(DatabaseReference ref)
    {
        // ref.addListenerForSingleValueEvent(ValueEventListener v);
    }

}
