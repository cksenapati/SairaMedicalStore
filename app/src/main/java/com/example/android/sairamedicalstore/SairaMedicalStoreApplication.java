package com.example.android.sairamedicalstore;

import android.app.Application;

import com.example.android.sairamedicalstore.models.MedicinePic;
import com.example.android.sairamedicalstore.models.User;

import java.util.ArrayList;

/**
 * Created by chandan on 01-08-2017.
 */

public class SairaMedicalStoreApplication extends Application {

    private User mCurrentUser;
    private String mUserType;
    private ArrayList<MedicinePic> mArrayListDefaultMedicinePics;

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

    public ArrayList<MedicinePic> getArrayListDefaultMedicinePics() {
        return mArrayListDefaultMedicinePics;
    }

    public void setArrayListDefaultMedicinePics(ArrayList<MedicinePic> mArrayListDefaultMedicinePics) {
        this.mArrayListDefaultMedicinePics = mArrayListDefaultMedicinePics;
    }
}
