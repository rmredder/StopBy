package com.squad.stopby;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrationActivity extends AppCompatActivity {

    private EditText register_username;
    private EditText register_email;
    private EditText register_password;
    private EditText register_userInfo;
    private Button registrationBtn;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseReference;

    private Profile userProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        register_username = (EditText) findViewById(R.id.register_username);
        register_email = (EditText) findViewById(R.id.register_email);
        register_password = (EditText) findViewById(R.id.register_password);
        register_userInfo = (EditText) findViewById(R.id.register_userInfo);
        registrationBtn = (Button) findViewById(R.id.register_registerBtn);

        mAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("user profile");


        registrationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = register_username.getText().toString();
                String email = register_email.getText().toString();
                String password = register_password.getText().toString();
                String userInfo = register_userInfo.getText().toString();
                userProfile = new Profile(username, email, password, userInfo);
                registerUsers(email, password);
            }
        });

    }

    private void registerUsers(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mDatabaseReference.push().setValue(userProfile);
                            //update UI
                            Intent intent = new Intent(RegistrationActivity.this, Menu.class);
                            startActivity(intent);
                            finish();
                            Toast.makeText(RegistrationActivity.this, "Registration is successful.", Toast.LENGTH_SHORT).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(RegistrationActivity.this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
