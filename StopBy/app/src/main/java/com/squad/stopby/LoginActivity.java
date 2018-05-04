package com.squad.stopby;

import android.accounts.AccountManager;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private EditText login_email;
    private EditText login_password;
    private Button loginBtn;
    private TextView forgot_password;

    private DatabaseReference userDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        forgot_password = findViewById(R.id.forgot_password);
        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, PasswordResetActivity.class);
                startActivity(intent);
            }
        });


        mAuth = FirebaseAuth.getInstance();

        userDatabase = FirebaseDatabase.getInstance().getReference().child("user profile");

        login_email = (EditText) findViewById(R.id.login_email);
        login_password = (EditText) findViewById(R.id.login_password);
        loginBtn = (Button) findViewById(R.id.loginBtn);
        AccountManager am = AccountManager.get(this);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = login_email.getText().toString();
                String password = login_password.getText().toString();

                //check if user fills in needed information
                if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(email)) {
                    signinUsers(email, password);
                } else {
                    Toast.makeText(LoginActivity.this, "Please fill in the required information", Toast.LENGTH_SHORT).show();
                }
            }
        });



    }

    private void signinUsers(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            String user_device_tokenId = FirebaseInstanceId.getInstance().getToken();

                            userDatabase.child(mAuth.getCurrentUser().getUid()).child("device_tokenId").setValue(user_device_tokenId).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();

                                }
                            });
                        } else {
                            Toast.makeText(LoginActivity.this, "incorrect username or incorrect password", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
