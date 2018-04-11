package com.squad.stopby;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccSettingActivity extends AppCompatActivity {

    private CircleImageView img;
    private TextView name;
    private TextView interest;
    private Button changeImgBtn;
    private Button changeTextBtn;

    private DatabaseReference userDatabase;
    private StorageReference filePath;

    private static final int galleryPicker = 14;

    private static final int storagePermission = 22;

    private boolean isPermissionGranted = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accsetting);

        img = (CircleImageView) findViewById(R.id.chat_profileImg);
        name = (TextView) findViewById(R.id.accSetting_name);
        interest = (TextView) findViewById(R.id.accSetting_interest);
        changeImgBtn = (Button) findViewById(R.id.accSetting_changeImgBtn);
        changeTextBtn = (Button) findViewById(R.id.accSetting_changeTextBtn);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = currentUser.getUid();
        userDatabase = FirebaseDatabase.getInstance().getReference().child("user profile").child(uid);
        filePath = FirebaseStorage.getInstance().getReference().child("profile images").child(uid + ".jpg");

        userDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String username = dataSnapshot.child("name").getValue().toString();
                String user_interest = dataSnapshot.child("interest").getValue().toString();
                String profileUri = dataSnapshot.child("image").getValue().toString();

                name.setText(username);
                interest.setText(user_interest);
                if(profileUri.equals("default")) {

                    Picasso.with(AccSettingActivity.this).load(R.drawable.default1).into(img);

                } else {

                    Picasso.with(AccSettingActivity.this).load(profileUri).into(img);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        //request permission
        requestPermission();

        //change user's profile info like username and interest
        changeTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toEditProfile = new Intent(AccSettingActivity.this, ProfileChangeActivity.class);
                startActivity(toEditProfile);
            }
        });

        changeImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check if permission if granted
                if(isPermissionGranted) {
                    //open gallery
                    Intent gallery = new Intent();
                    //shows only images
                    gallery.setType("image/*");
                    gallery.setAction(Intent.ACTION_GET_CONTENT);
                    //always show the chooser
                    startActivityForResult(Intent.createChooser(gallery, "SELECT IMAGE"), galleryPicker);

                } else {
                    requestPermission();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && requestCode == galleryPicker) {

            Uri imgUri = data.getData();

            CropImage.activity(imgUri)
                    .setAspectRatio(1,1)
                    .start(this);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                final Uri resultUri = result.getUri();

                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()) {

                            String download_imgUri = task.getResult().getDownloadUrl().toString();
                            userDatabase.child("image").setValue(download_imgUri);

                        }
                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();

            }
        }

    }

    // --------- Permission -------------

    private void requestPermission() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            isPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, storagePermission);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == storagePermission) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                isPermissionGranted = true;
            }
        }
    }


}
