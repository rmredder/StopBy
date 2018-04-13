package com.squad.stopby;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChatRoomActivity extends AppCompatActivity {

    private DatabaseReference chatroomDatabase;
    private DatabaseReference userDatabase;

    private Toolbar chat_toolbar;

    private RecyclerView chatroom_recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        chatroomDatabase = FirebaseDatabase.getInstance().getReference().child("chat").child(currentUser.getUid());
        userDatabase = FirebaseDatabase.getInstance().getReference().child("user profile");

        chat_toolbar = (Toolbar) findViewById(R.id.chat_posts_toolbar);
        setSupportActionBar(chat_toolbar);
        getSupportActionBar().setTitle("Chat Room");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        chatroom_recyclerView = (RecyclerView) findViewById(R.id.chat_recyclerView);
        chatroom_recyclerView.setHasFixedSize(true);
        chatroom_recyclerView.setLayoutManager(linearLayoutManager);

        //adding lines to separate each item in the recyclerView
        DividerItemDecoration divider = new DividerItemDecoration(chatroom_recyclerView.getContext(), linearLayoutManager.getOrientation());
        chatroom_recyclerView.addItemDecoration(divider);

        FirebaseRecyclerAdapter<Chatter, ChatroomViewHolder> chatroomAdapter = new FirebaseRecyclerAdapter<Chatter, ChatroomViewHolder>(

                Chatter.class,
                R.layout.chatroom_background_layout,
                ChatroomViewHolder.class,
                chatroomDatabase

        ) {
            @Override
            protected void populateViewHolder(final ChatroomViewHolder viewHolder, Chatter model, int position) {

                final String other_user_id = getRef(position).getKey();

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

                        Intent toChat = new Intent(ChatRoomActivity.this, ChatActivity.class);
                        toChat.putExtra("other_user_id", other_user_id);
                        startActivity(toChat);

                    }
                });
            }

        };

        chatroom_recyclerView.setAdapter(chatroomAdapter);

    }
}
