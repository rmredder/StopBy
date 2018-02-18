package com.squad.stopby;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class Menu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
    }

    public void post(View view){
        Intent intent = new Intent(this, Post.class);
        startActivity(intent);
    }

    public void search(View view){
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }
}
