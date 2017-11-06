package com.example.android.sairamedicalstore.operations;

import android.app.Activity;
import android.widget.Toast;

import com.example.android.sairamedicalstore.SairaMedicalStoreApplication;
import com.example.android.sairamedicalstore.models.Address;
import com.example.android.sairamedicalstore.models.User;
import com.example.android.sairamedicalstore.utils.Constants;
import com.example.android.sairamedicalstore.utils.Utils;
import com.firebase.client.Firebase;
import com.firebase.client.ServerValue;

import java.util.HashMap;

/**
 * Created by chandan on 28-10-2017.
 */

public class AddressOperations {
    User mCurrentUser;
    Activity mActivity;
    Firebase firebaseCurrentUserAllAddressesRef;

    public AddressOperations( Activity mActivity) {
        this.mActivity = mActivity;
        this.mCurrentUser = ((SairaMedicalStoreApplication) mActivity.getApplication()).getCurrentUser();
        this.firebaseCurrentUserAllAddressesRef = new Firebase(Constants.FIREBASE_URL_SAIRA_All_ADDRESSES).child(Utils.encodeEmail(mCurrentUser.getEmail()));

    }

    public void AddNewAddress(String pinCode, String fullName, String phoneNumber, String address, String landmark, String city,
                              String state)
    {

        String addressId = firebaseCurrentUserAllAddressesRef.push().getKey();
        HashMap<String, Object> timestampCreated = new HashMap<>();
        timestampCreated.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);
        HashMap<String, Object> timestampLastUpdate = timestampCreated;

        Address newAddress = new Address(addressId,pinCode,fullName,phoneNumber,address,landmark,city,state,timestampCreated,timestampLastUpdate);

        try {
            firebaseCurrentUserAllAddressesRef.child(addressId).setValue(newAddress);
            mActivity.finish();
        }
        catch (Exception ex)
        {
            Toast.makeText(mActivity,Constants.UPLOAD_FAIL,Toast.LENGTH_SHORT).show();
        }
    }

    public void UpdateSavedAddress(Address addressNeedToBeUpdated)
    {

        HashMap<String, Object> timestampLastUpdate = new HashMap<>();
        timestampLastUpdate.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);

        addressNeedToBeUpdated.setTimestampLastUpdate(timestampLastUpdate);

        try {
            firebaseCurrentUserAllAddressesRef.child(addressNeedToBeUpdated.getAddressId()).setValue(addressNeedToBeUpdated);
            Toast.makeText(mActivity,Constants.UPDATE_SUCCESSFUL,Toast.LENGTH_SHORT).show();
            mActivity.finish();
        }
        catch (Exception ex)
        {
            Toast.makeText(mActivity,Constants.UPDATE_FAIL,Toast.LENGTH_SHORT).show();
        }
    }
}
