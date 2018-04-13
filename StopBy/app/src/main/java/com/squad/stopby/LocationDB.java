package com.squad.stopby;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

/**
 * Created by rredd on 3/20/2018.
 */


public class LocationDB {

    private String username;
    private String post;
    private String latitude;
    private String longitude;

    public LocationDB(){
        this.username = null;
        this.post = null;
        this.latitude = "";
        this.longitude = "";
    }


    public LocationDB(String username, String post, String latitude, String longitude){
        this.username = username;
        this.post = post;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getUsername() {
        return username;
    }

    public String getPost() {
        return post;
    }

    public String getLatitude() {
        return (String) latitude;
    }

    public String getLongitude() {
        return (String) longitude;
    }

    public void pushToDatabase(DatabaseReference databaseReference){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference.child("location").child("currentlocation").child(currentUser.getUid()).setValue(this);
    }
}
