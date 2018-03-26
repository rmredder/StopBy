package com.squad.stopby;

/**
 * Created by Yuanjian on 3/25/2018.
 */

public class Profile {
    private String username;
    private String email;
    private String password;
    private String userInfo;

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
