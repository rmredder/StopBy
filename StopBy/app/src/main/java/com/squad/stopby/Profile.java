package com.squad.stopby;

/**
<<<<<<< HEAD
 * Created by Yuanjian on 3/15/2018.
=======
 * Created by Yuanjian on 3/25/2018.
>>>>>>> 1dbe265afb7e0ca77ead138fcdb35406c3e85b80
 */

public class Profile {
    private String username;
    private String email;
    private String password;
    private String userInfo;

    public Profile(){}

    public Profile(String username, String email, String password, String userInfo) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.userInfo = userInfo;

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public String getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(String userInfo) {
        this.userInfo = userInfo;
    }
}
