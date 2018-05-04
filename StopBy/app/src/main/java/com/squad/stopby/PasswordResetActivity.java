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
import com.google.firebase.auth.FirebaseAuth;

public class PasswordResetActivity extends AppCompatActivity {

    private EditText mpasswordResetEmail;
    private Button msubmit_button;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);
        mpasswordResetEmail = (EditText) findViewById(R.id.passwordResetEmail);
        msubmit_button = (Button) findViewById(R.id.submit_button);
        firebaseAuth = firebaseAuth.getInstance();
        mpasswordResetEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String useremail = mpasswordResetEmail.getText().toString().trim();

                if(useremail.equals("")){
                    Toast.makeText(PasswordResetActivity.this, "Please enter your registered email ID", Toast.LENGTH_SHORT).show();
                }else{
                    firebaseAuth.sendPasswordResetEmail(useremail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(PasswordResetActivity.this, "Password reset email sent!", Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(new Intent(PasswordResetActivity.this, StartupActivity.class));
                            }else{
                                Toast.makeText(PasswordResetActivity.this, "Error in sending password reset email", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}
