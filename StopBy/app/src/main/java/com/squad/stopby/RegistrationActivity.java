package com.squad.stopby;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.iid.FirebaseInstanceId;

import java.security.MessageDigest;
import java.util.HashMap;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class RegistrationActivity extends AppCompatActivity {

    private EditText register_username;
    private EditText register_email;
    private EditText register_password;
    private Button registrationBtn;

    private FirebaseAuth mAuth;
    private Database db;
    private DatabaseReference userDatabase;

    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mEditor = mPreferences.edit();

        register_username = (EditText) findViewById(R.id.register_username);
        register_email = (EditText) findViewById(R.id.register_email);
        register_password = (EditText) findViewById(R.id.register_password);
        registrationBtn = (Button) findViewById(R.id.register_registerBtn);

        mAuth = FirebaseAuth.getInstance();
        db = new Database();
        userDatabase = db.getDatabaseReference().child("user profile");

        registrationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    String username = register_username.getText().toString();

                    //shared preference
                    mEditor.putString(getString(R.string.username), username);
                    mEditor.commit();

                    String email = register_email.getText().toString();
                    String rawPassword = register_password.getText().toString();

                    //check if the user fills in all the information
                    if(!TextUtils.isEmpty(username) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(rawPassword)) {
                        registerUsers(username, email, rawPassword);
                    } else {
                        Toast.makeText(RegistrationActivity.this, "Please fill in all the required information", Toast.LENGTH_SHORT).show();
                    }
                }
        });
    }

    private void registerUsers(final String username, final String email, final String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            try {
                                //retrieve user tokenId
                                String user_device_tokenId = FirebaseInstanceId.getInstance().getToken();

                                HashMap<String, String> userMap = new HashMap<>();
                                userMap.put("name", username);
                                userMap.put("email", email);
                                userMap.put("password", encrypt(password));
                                userMap.put("image", "default");
                                userMap.put("interest", "This user is too lazy to write down anything.");
                                userMap.put("device_tokenId", user_device_tokenId);

                                FirebaseUser currentUser = mAuth.getCurrentUser();
                                userDatabase.child(currentUser.getUid()).setValue(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        //update UI
                                        Intent intent = new Intent(RegistrationActivity.this, MenuActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                        Toast.makeText(RegistrationActivity.this, "Registration is successful.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } catch(Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(RegistrationActivity.this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    //Encrypt the password
    public String encrypt(String password) throws Exception {
        SecretKeySpec key = generateKey(password);
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] bytesArray = cipher.doFinal(password.getBytes());
        String encryptedVal = Base64.encodeToString(bytesArray, Base64.DEFAULT);
        return encryptedVal;
    }

    private SecretKeySpec generateKey(String password) throws Exception {
        final MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = password.getBytes("UTF-8");
        digest.update(bytes, 0, bytes.length);
        byte[] key = digest.digest();
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        return secretKeySpec;
    }
}
