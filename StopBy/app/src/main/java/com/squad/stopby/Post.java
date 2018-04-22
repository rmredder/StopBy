package com.squad.stopby;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

/**
 * Created by Yuanjian on 4/9/2018.
 */

public class Post {

    private String name;
    private String message;

    public Post(){
        this.name = null;
        this.message = null;
    }

    public Post(String name, String message) {
        this.name = name;
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void pushToDatabase(DatabaseReference databaseReference, String chosenLocation){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference.child("location").child(chosenLocation).child(currentUser.getUid()).setValue(this);
    }
}
