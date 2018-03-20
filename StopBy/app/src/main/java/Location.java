import com.google.firebase.database.DatabaseReference;

/**
 * Created by rredd on 3/20/2018.
 */

public class Location {
    private String username;
    private String post;
    private long latitude;
    private long longitude;


    public Location(String username, String post, long latitude, long longitude){
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

    public long getLatitude() {
        return latitude;
    }

    public long getLongitude() {
        return longitude;
    }

    public void pushToDatabase(DatabaseReference databaseReference){
        databaseReference.child("Location")
                .setValue(this);

    }
}
