package com.example.android.sairamedicalstore;

import android.app.Application;

import com.example.android.sairamedicalstore.models.DefaultKeyValuePair;
import com.example.android.sairamedicalstore.models.User;

import java.util.ArrayList;

/**
 * Created by chandan on 01-08-2017.
 */

public class SairaMedicalStoreApplication extends Application {

    private User mCurrentUser;
    private String mUserType;
    private ArrayList<DefaultKeyValuePair> mArrayListDefaultMedicinePics;
    private ArrayList<DefaultKeyValuePair> mArrayListCommonDefaultValues;

    public User getCurrentUser() {
        return mCurrentUser;
    }

    public void setCurrentUser(User mCurrentUser) {
        this.mCurrentUser = mCurrentUser;
    }

    public String getUserType() {
        return mUserType;
    }

    public void setUserType(String mUserType) {
        this.mUserType = mUserType;
    }

    public ArrayList<DefaultKeyValuePair> getArrayListDefaultMedicinePics() {
        return mArrayListDefaultMedicinePics;
    }

    public void setArrayListDefaultMedicinePics(ArrayList<DefaultKeyValuePair> mArrayListDefaultMedicinePics) {
        this.mArrayListDefaultMedicinePics = mArrayListDefaultMedicinePics;
    }

    public ArrayList<DefaultKeyValuePair> getArrayListCommonDefaultValues() {
        return mArrayListCommonDefaultValues;
    }

    public void setArrayListCommonDefaultValues(ArrayList<DefaultKeyValuePair> mArrayListCommonDefaultValues) {
        this.mArrayListCommonDefaultValues = mArrayListCommonDefaultValues;
    }
}
