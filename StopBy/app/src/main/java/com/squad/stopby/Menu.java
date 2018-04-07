package com.squad.stopby;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Menu extends AppCompatActivity {
    private int MAP_PERMISSION = 1;
    private boolean PERMISSION_GRANTED = false;

    private FirebaseAuth mAuth;

    private Button menu_postBtn;
    private Button menu_searchBtn;
    private Button menu_profileBtn;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        mAuth = FirebaseAuth.getInstance();

        menu_postBtn = (Button) findViewById(R.id.menu_postBtn);
        menu_searchBtn = (Button) findViewById(R.id.menu_searchBtn);
        menu_profileBtn = (Button) findViewById(R.id.menu_profileBtn);

        toolbar = (Toolbar) findViewById(R.id.menu_toolbar);
        setSupportActionBar(toolbar);

        menu_postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(PERMISSION_GRANTED){
                    toPost();
                }else{
                    //Toast.makeText(Menu.this, "No location permission granted", Toast.LENGTH_SHORT).show();
                    requestMapPermission();
                }

            }
        });

        menu_searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(PERMISSION_GRANTED){
                    toSearch();
                }else{
                    requestMapPermission();
                }

            }
        });

        menu_profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Menu.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        //
        if (ContextCompat.checkSelfPermission(Menu.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            PERMISSION_GRANTED = true;
        }else{
            requestMapPermission();
            Log.e("ContextCompat: ", "here");
        }
    }

    //authenticate users
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser ==  null) {
            Intent intent = new Intent(this, StartupActivity.class);
            startActivity(intent);
            finish();
        }
    }

    //create the menu on top
    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    //users sign out
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.main_signout) {
            FirebaseAuth.getInstance().signOut();
            //update UI
            Intent intent = new Intent(this, StartupActivity.class);
            startActivity(intent);
        }
        return true;
    }

    public void toPost(){
        Intent goToPost = new Intent(this, Post.class);
        startActivity(goToPost);
    }

    public void toSearch(){
        Intent goToMap = new Intent(this, MapsActivity.class);
        startActivity(goToMap);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == MAP_PERMISSION){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                PERMISSION_GRANTED = true;
            }else{
                Toast.makeText(this, "Permission Not Granted", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void requestMapPermission(){

        if(ActivityCompat.shouldShowRequestPermissionRationale(Menu.this, android.Manifest.permission.ACCESS_FINE_LOCATION)){
            new AlertDialog.Builder(this).setTitle("Permission Needed")
                    .setMessage("Location permission needed")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(Menu.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, MAP_PERMISSION);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();

        }else{
            ActivityCompat.requestPermissions(Menu.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            Log.e("RequestMapPermission: ", "here");
        }
    }
}