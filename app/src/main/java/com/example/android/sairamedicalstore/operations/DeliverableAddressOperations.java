package com.example.android.sairamedicalstore.operations;

import android.app.Activity;
import android.widget.Toast;

import com.example.android.sairamedicalstore.SairaMedicalStoreApplication;
import com.example.android.sairamedicalstore.models.Address;
import com.example.android.sairamedicalstore.models.DeliverableAddress;
import com.example.android.sairamedicalstore.models.User;
import com.example.android.sairamedicalstore.utils.Constants;
import com.example.android.sairamedicalstore.utils.Utils;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ServerValue;
import com.firebase.client.ValueEventListener;

import java.util.HashMap;

/**
 * Created by chandan on 28-10-2017.
 */

public class DeliverableAddressOperations {
    User mCurrentUser;
    Activity mActivity;
    Firebase firebaseAllDeliverableAddressesRef;

    public DeliverableAddressOperations(Activity mActivity) {
        this.mActivity = mActivity;
        this.mCurrentUser = ((SairaMedicalStoreApplication) mActivity.getApplication()).getCurrentUser();
        this.firebaseAllDeliverableAddressesRef = new Firebase(Constants.FIREBASE_URL_SAIRA_All_DELIVERABLE_ADDRESS);
    }

    public void AddOrUpdateDeliverableAddress(final DeliverableAddress address)
    {
        firebaseAllDeliverableAddressesRef.child(address.getPinCode()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    firebaseAllDeliverableAddressesRef.child(address.getPinCode()).setValue(address);
                    Toast.makeText(mActivity,Constants.UPDATE_SUCCESSFUL,Toast.LENGTH_SHORT).show();
                    return;
                }

                firebaseAllDeliverableAddressesRef.child(address.getPinCode()).setValue(address);
                Toast.makeText(mActivity,Constants.UPLOAD_SUCCESSFUL,Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Toast.makeText(mActivity,Constants.UPLOAD_FAIL,Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void RemoveDeliverableAddress(final DeliverableAddress address)
    {
        firebaseAllDeliverableAddressesRef.child(address.getPinCode()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getValue(DeliverableAddress.class) != null)
                {
                    firebaseAllDeliverableAddressesRef.child(address.getPinCode()).setValue(null);
                    Toast.makeText(mActivity,"Successfully Removed",Toast.LENGTH_SHORT).show();
                    return;
                }
                else
                 Toast.makeText(mActivity,"Doesn't exist",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Toast.makeText(mActivity,"Something went wrong",Toast.LENGTH_SHORT).show();
            }
        });
    }



}
