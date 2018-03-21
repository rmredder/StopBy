package com.squad.stopby;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by rredd on 3/20/2018.
 */

public class Database {

    private FirebaseDatabase database;
    final private DatabaseReference databaseReference;
    private String userName; //Should we use a different class to store username?

    public Database(){
        this.database = FirebaseDatabase.getInstance();
        this.databaseReference = database.getReferenceFromUrl("https://stopby-196918.firebaseio.com/");
    }

    public DatabaseReference getDatabaseReference(){
        return this.databaseReference;
    }

    public void setUserName(String userName){
        this.userName = userName;
    }
}
