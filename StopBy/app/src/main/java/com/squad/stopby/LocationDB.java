package com.squad.stopby;

import com.google.firebase.database.DatabaseReference;

/**
 * Created by rredd on 3/20/2018.
 */


public class LocationDB {

    private String username;
    private String post;
    private double latitude;
    private double longitude;
    private String uid;

    public LocationDB(){
        this.username = null;
        this.post = null;
        this.latitude = 0;
        this.longitude = 0;
    }


    public LocationDB(String username, String post, double latitude, double longitude){
        this.username = username;
        this.post = post;
        this.latitude = latitude;
        this.longitude = longitude;
        //ToDO add a timestamp
    }

    public void setUsername(String username){
        this.username = username;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public void setLatitude(long latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(long longitude) {
        this.longitude = longitude;
    }

    public String getUsername() {
        return username;
    }

    public String getPost() {
        return post;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void pushToDatabase(DatabaseReference databaseReference){
        final String location = "Location";
        this.uid = databaseReference.child(location).push().getKey();
        databaseReference.child(location).child(this.uid).setValue(this);
    }
}
