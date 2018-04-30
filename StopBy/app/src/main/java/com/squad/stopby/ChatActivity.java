package com.squad.stopby;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private DatabaseReference rootDatabase;

    private FirebaseUser currentUser;

    private Toolbar chat_toolbar;

    private RecyclerView message_list;
    private MessageAdapter msgAdapter;
    private LinearLayoutManager linearLayoutManager;

    private EditText message_view;
    private ImageButton sendBtn;
    private List<Message> mList = new ArrayList<>();

    private String other_user_id;

    private ChildEventListener msgChildEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            Message single_msg = dataSnapshot.getValue(Message.class);

            mList.add(single_msg);
            msgAdapter.notifyDataSetChanged();

            message_list.scrollToPosition(mList.size()-1);
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {}

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

        @Override
        public void onCancelled(DatabaseError databaseError) {}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        other_user_id = getIntent().getStringExtra("other_user_id");

        rootDatabase = FirebaseDatabase.getInstance().getReference();

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        chat_toolbar = (Toolbar) findViewById(R.id.chat_toolbar);
        setSupportActionBar(chat_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //make the username of the person your're talking to as the title of the chat activity
        rootDatabase.child("user profile").child(other_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String toolbarTitle = dataSnapshot.child("name").getValue().toString();

                getSupportActionBar().setTitle(toolbarTitle);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        message_list = (RecyclerView) findViewById(R.id.chat_message_list);
        message_list.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(this);
        message_list.setLayoutManager(linearLayoutManager);
        msgAdapter = new MessageAdapter(this, mList);
        message_list.setAdapter(msgAdapter);


        loadMessage();


        message_view = (EditText) findViewById(R.id.chat_message_view);
        sendBtn = (ImageButton) findViewById(R.id.chat_sendBtn);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(TextUtils.isEmpty(message_view.getText().toString())) {

                    Toast.makeText(ChatActivity.this, "Please enter your message", Toast.LENGTH_SHORT).show();

                } else {

                    HashMap<String, String> notificationMap = new HashMap<>();
                    notificationMap.put("from", currentUser.getUid());
                    notificationMap.put("type", "chat_notification");

                    rootDatabase.child("notifications").child(other_user_id).push().setValue(notificationMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            sendMessage();

                        }
                    });
                }

            }
        });
    }

    private void loadMessage() {

        rootDatabase.child("chat").child(currentUser.getUid()).child(other_user_id).addChildEventListener(msgChildEventListener);

    }

    private void sendMessage() {

        String single_message = message_view.getText().toString();

        final HashMap<String, String> msgMap = new HashMap<>();
        msgMap.put("message", single_message);
        msgMap.put("from", currentUser.getUid());

        rootDatabase.child("chat").child(currentUser.getUid()).child(other_user_id).push().setValue(msgMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                rootDatabase.child("chat").child(other_user_id).child(currentUser.getUid()).push().setValue(msgMap);

            }
        });

        message_view.getText().clear();

    }

    @Override
    protected void onStop() {
        super.onStop();

        rootDatabase.child("chat").child(currentUser.getUid()).child(other_user_id).removeEventListener(msgChildEventListener);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("result", "the method is called");
        if(item.getItemId() == android.R.id.home) {
            Log.d("result for backward", "go through");
//            NavUtils.navigateUpFromSameTask(this);
            finish();
        }

        return true;
    }
}
