package com.squad.stopby;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class AvailablePostsLockwoodActivity extends AppCompatActivity {

    private android.support.v7.widget.Toolbar lockwood_toolbar;

    private RecyclerView lockwood_recyclerView;
    private LinearLayoutManager linearLayoutManager;

    private DatabaseReference lockwoodDatabase;

    private DatabaseReference userDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_available_posts_lockwood);

        lockwood_toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.su_posts_toolbar);
        setSupportActionBar(lockwood_toolbar);
        getSupportActionBar().setTitle("Available Posts in Lockwood");

        lockwoodDatabase = FirebaseDatabase.getInstance().getReference().child("location").child("Lockwood");
        userDatabase = FirebaseDatabase.getInstance().getReference().child("user profile");

        linearLayoutManager = new LinearLayoutManager(this);
        lockwood_recyclerView = (RecyclerView) findViewById(R.id.su_recyclerView);
        lockwood_recyclerView.setHasFixedSize(true);
        lockwood_recyclerView.setLayoutManager(linearLayoutManager);

        //adding lines to separate each item in the recyclerView
        DividerItemDecoration divider = new DividerItemDecoration(lockwood_recyclerView.getContext(), linearLayoutManager.getOrientation());
        lockwood_recyclerView.addItemDecoration(divider);

        FirebaseRecyclerAdapter<Post, CapenPostsViewHolder> capen_post_adapter = new FirebaseRecyclerAdapter<Post, CapenPostsViewHolder>(
                Post.class,
                R.layout.available_posts_location_layout,
                CapenPostsViewHolder.class,
                lockwoodDatabase
        ) {
            @Override
            protected void populateViewHolder(final CapenPostsViewHolder viewHolder, Post single_post, int position) {

                final String other_user_id = getRef(position).getKey();

                //display user's profle image, name, and message in the availablePosts page
                viewHolder.displayMessage(single_post.getMessage());

                userDatabase.child(other_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        //display user profile picture
                        String imgUrl = dataSnapshot.child("image").getValue().toString();
                        viewHolder.displayProfilePic(imgUrl);

                        //display user's name
                        String username = dataSnapshot.child("name").getValue().toString();
                        viewHolder.setName(username);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });

                viewHolder.getView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent toProfile = new Intent(AvailablePostsLockwoodActivity.this, ProfileActivity.class);
                        toProfile.putExtra("other_user_id", other_user_id);
                        startActivity(toProfile);
                    }
                });

            }
        };

        lockwood_recyclerView.setAdapter(capen_post_adapter);

    }
}
