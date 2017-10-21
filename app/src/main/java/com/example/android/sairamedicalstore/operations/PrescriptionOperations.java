package com.example.android.sairamedicalstore.operations;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.util.Util;
import com.example.android.sairamedicalstore.R;
import com.example.android.sairamedicalstore.SairaMedicalStoreApplication;
import com.example.android.sairamedicalstore.models.Prescription;
import com.example.android.sairamedicalstore.models.User;
import com.example.android.sairamedicalstore.utils.Constants;
import com.example.android.sairamedicalstore.utils.Utils;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ServerValue;
import com.firebase.client.ValueEventListener;

import java.util.HashMap;

import static com.example.android.sairamedicalstore.ui.AddNewPrescriptionActivity.saveDataDialog;

/**
 * Created by chandan on 17-10-2017.
 */

public class PrescriptionOperations {

    Firebase mFirebaseCurrentUserPrescriptionsRef;
    User mCurrentUser;
    Activity mActivity;

    public PrescriptionOperations(Activity mActivity) {
        this.mActivity = mActivity;
        this.mCurrentUser = ((SairaMedicalStoreApplication) mActivity.getApplication()).getCurrentUser();
        this.mFirebaseCurrentUserPrescriptionsRef = new Firebase(Constants.FIREBASE_URL_SAIRA_All_PRESCRIPTIONS).child(Utils.encodeEmail(mCurrentUser.getEmail()));

    }

    public void addNewPrescription(final String prescriptionId, HashMap<String,String> hashMapPrescriptionPages,final String prescriptionName) {
        HashMap<String, Object> timestampUploaded = new HashMap<String, Object>();
        timestampUploaded.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);

        final TextView textViewErrorMsg = (TextView) saveDataDialog.findViewById(R.id.text_view_error_message);

        final Prescription newPrescription = new Prescription(prescriptionId,hashMapPrescriptionPages,prescriptionName,timestampUploaded);

        mFirebaseCurrentUserPrescriptionsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    Boolean isExist = false;
                    for (DataSnapshot snapshot:dataSnapshot.getChildren()) {
                        Prescription eachPrescription = snapshot.getValue(Prescription.class);
                        if(eachPrescription != null && eachPrescription.getPrescriptionName().equals(prescriptionName))
                        {
                            textViewErrorMsg.setText(Constants.ITEM_ALREADY_EXISTS);
                            isExist = true;
                            break;
                        }
                    }
                    if(!isExist) {
                        try {
                            mFirebaseCurrentUserPrescriptionsRef.child(prescriptionId).setValue(newPrescription);
                            saveDataDialog.dismiss();
                            Toast.makeText(mActivity, Constants.UPLOAD_SUCCESSFUL, Toast.LENGTH_SHORT).show();
                            mActivity.finish();
                        } catch (Exception ex) {
                        }
                    }
                }
                else
                {
                    try {
                        mFirebaseCurrentUserPrescriptionsRef.child(prescriptionId).setValue(newPrescription);
                        saveDataDialog.dismiss();
                        Toast.makeText(mActivity, Constants.UPLOAD_SUCCESSFUL, Toast.LENGTH_SHORT).show();
                        mActivity.finish();
                    } catch (Exception ex) {
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });

    }


}
