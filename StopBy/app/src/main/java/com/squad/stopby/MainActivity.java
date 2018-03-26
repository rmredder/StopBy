package com.squad.stopby;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.*;
import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private Button main_postBtn;
    private Button main_searchBtn;
    private Button main_profileBtn;

    private android.support.v7.widget.Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Authentication
        mAuth = FirebaseAuth.getInstance();

        main_postBtn = (Button) findViewById(R.id.main_postBtn);
        main_searchBtn = (Button) findViewById(R.id.main_searchBtn);
        main_profileBtn = (Button) findViewById(R.id.main_profileBtn);

        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.main_menu);
        setSupportActionBar(toolbar);

//        main_postBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mDatabaseReference.push().setValue("hello world");
//            }
//        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser ==  null) {
            Intent intent = new Intent(this, StartupActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Welcome back brodie", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

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

    public void findUsers(DatabaseReference ref)
    {
        // ref.addListenerForSingleValueEvent(ValueEventListener v);
    }



}
