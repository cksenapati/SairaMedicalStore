package com.example.android.sairamedicalstore.operations;

import android.app.Activity;
import android.widget.Toast;

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

/*
import static com.example.android.sairamedicalstore.ui.prescription.AddNewPrescriptionActivity.saveDataDialog;
*/
import static com.example.android.sairamedicalstore.ui.prescription.MyPrescriptionsActivity.mActivePrescription;
import static com.example.android.sairamedicalstore.ui.prescription.MyPrescriptionsActivity.mActivePrescriptionIdForNewPrescription;
import static com.example.android.sairamedicalstore.ui.prescription.MyPrescriptionsActivity.mArrayListAllPagesOfActivePrescription;
import static com.example.android.sairamedicalstore.ui.prescription.MyPrescriptionsActivity.mRecyclerViewActivePrescriptionAllPages;
import static com.example.android.sairamedicalstore.ui.prescription.MyPrescriptionsActivity.mTextViewNewOrUpdatePrescription;
import static com.example.android.sairamedicalstore.ui.prescription.MyPrescriptionsActivity.mTextViewSaveOrUpdatePrescription;

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

/*
        final TextView textViewErrorMsg = (TextView) saveDataDialog.findViewById(R.id.text_view_error_message);
*/

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
/*
                            textViewErrorMsg.setText(Constants.ITEM_ALREADY_EXISTS);
*/
                            Toast.makeText(mActivity, Constants.ITEM_ALREADY_EXISTS, Toast.LENGTH_SHORT).show();
                            isExist = true;
                            break;
                        }
                    }
                    if(!isExist) {
                        try {
                            mFirebaseCurrentUserPrescriptionsRef.child(prescriptionId).setValue(newPrescription);
                            Toast.makeText(mActivity, Constants.UPLOAD_SUCCESSFUL, Toast.LENGTH_SHORT).show();
                            mRecyclerViewActivePrescriptionAllPages.setAdapter(null);
                            mArrayListAllPagesOfActivePrescription.clear();
                            mActivePrescriptionIdForNewPrescription = mFirebaseCurrentUserPrescriptionsRef.push().getKey();
                        } catch (Exception ex) {
                        }
                    }
                }
                else
                {
                    try {
                        mFirebaseCurrentUserPrescriptionsRef.child(prescriptionId).setValue(newPrescription);
                        Toast.makeText(mActivity, Constants.UPLOAD_SUCCESSFUL, Toast.LENGTH_SHORT).show();
                        mRecyclerViewActivePrescriptionAllPages.setAdapter(null);
                        mArrayListAllPagesOfActivePrescription.clear();
                        mActivePrescriptionIdForNewPrescription = mFirebaseCurrentUserPrescriptionsRef.push().getKey();
                    } catch (Exception ex) {
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });

    }

    public void updateSavedPrescription(final String prescriptionIdToBeUpdated,final HashMap<String,String> mHashMapUpdatedPrescriptionPages)
    {
        mFirebaseCurrentUserPrescriptionsRef.child(prescriptionIdToBeUpdated).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    Prescription prescriptionToBeUpdated = dataSnapshot.getValue(Prescription.class);
                    if (prescriptionToBeUpdated == null) {
                        Toast.makeText(mActivity, Constants.UPDATE_FAIL, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try {
                        prescriptionToBeUpdated.setPrescriptionPages(mHashMapUpdatedPrescriptionPages);
                        mFirebaseCurrentUserPrescriptionsRef.child(prescriptionIdToBeUpdated).
                                setValue(prescriptionToBeUpdated);

                        mTextViewSaveOrUpdatePrescription.setText("Save");
                        mTextViewNewOrUpdatePrescription.setText("New Prescription");
                        mRecyclerViewActivePrescriptionAllPages.setAdapter(null);
                        mArrayListAllPagesOfActivePrescription.clear();
                        mActivePrescription = null;

                        Toast.makeText(mActivity, Constants.UPLOAD_SUCCESSFUL, Toast.LENGTH_SHORT).show();

                    }
                    catch (Exception ex)
                    {
                        Toast.makeText(mActivity, Constants.UPDATE_FAIL, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Toast.makeText(mActivity, Constants.UPDATE_FAIL, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
