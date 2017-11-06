package com.example.android.sairamedicalstore.utils;

import android.content.Context;
import android.net.ConnectivityManager;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by chandan on 01-08-2017.
 */

public class Utils {

    public static final SimpleDateFormat SIMPLE_DATE_ONLY_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public static String encodeEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }

    public static boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com"); //You can replace it with your name
            return !ipAddr.equals("");

        } catch (Exception e) {
            return false;
        }

    }

    public static String getFirstName(String fullName)
    {
        String arr[] = fullName.split(" ", 2);
        return arr[0];
    }

    public static String toLowerCaseExceptFirstLetter(String stringToBeConverted)
    {
        return stringToBeConverted.substring(0,1).toUpperCase() + stringToBeConverted.substring(1).toLowerCase();
    }

}
