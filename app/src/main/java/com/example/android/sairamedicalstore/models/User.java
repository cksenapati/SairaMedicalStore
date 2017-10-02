package com.example.android.sairamedicalstore.models;

import com.example.android.sairamedicalstore.utils.Constants;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashMap;

import static android.R.attr.y;

/**
 * Created by chandan on 03-03-2017.
 */
@SuppressWarnings("serial")
public class User {

    private String name,email,phoneNo,photoURL;
    private HashMap<String, Object> timestampJoined;

    public User() {
    }

    public User(String name, String email, String phoneNo,String photoURL, HashMap<String, Object> timestampJoined) {
        this.name = name;
        this.email = email;
        this.phoneNo = phoneNo;
        this.timestampJoined = timestampJoined;
        this.photoURL = photoURL;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public HashMap<String, Object> getTimestampJoined() {
        return timestampJoined;
    }

    @JsonIgnore
    public long getTimestampJoinedLong() {

        return (long) timestampJoined.get(Constants.FIREBASE_PROPERTY_TIMESTAMP);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

    public void setTimestampJoined(HashMap<String, Object> timestampJoined) {
        this.timestampJoined = timestampJoined;
    }
}
