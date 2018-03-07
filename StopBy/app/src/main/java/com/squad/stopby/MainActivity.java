package com.squad.stopby;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText username = (EditText) findViewById(R.id.username);
        EditText password = (EditText) findViewById(R.id.password);
        Button login = (Button) findViewById(R.id.login);


        // get instance of firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReferenceFromUrl("https://stopby-196918.firebaseio.com/");

        myRef.setValue("Hello, World!");

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //myRef.setValue("Hello World");
            }
        });
    }

    //Called when user logs in
    public void login(View view){
        Intent intent = new Intent(this, Menu.class);
        startActivity(intent);
    }
}
