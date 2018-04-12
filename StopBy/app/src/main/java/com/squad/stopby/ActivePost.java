package com.squad.stopby;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class ActivePost extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_post);

        // Use to access user's message from Post page
        String userPost = getIntent().getStringExtra("USER_POST");

        // Log to console to ensure correct message was sent
        Log.d("USER_MESSAGE", userPost);
    }
}
