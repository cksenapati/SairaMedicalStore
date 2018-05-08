package com.example.android.sairamedicalstore.operations;

import android.app.Activity;
import android.net.Uri;
import android.widget.Toast;

import com.example.android.sairamedicalstore.SairaMedicalStoreApplication;
import com.example.android.sairamedicalstore.models.Prescription;
import com.example.android.sairamedicalstore.models.PrescriptionPage;
import com.example.android.sairamedicalstore.models.User;
import com.example.android.sairamedicalstore.utils.Constants;
import com.example.android.sairamedicalstore.utils.Utils;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ServerValue;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/*
import static com.example.android.sairamedicalstore.ui.prescription.AddNewPrescriptionActivity.saveDataDialog;
*/
import static com.example.android.sairamedicalstore.ui.prescription.MyPrescriptionsActivity.mActivePrescription;
import static com.example.android.sairamedicalstore.ui.prescription.MyPrescriptionsActivity.mActivePrescriptionIdForNewPrescription;
import static com.example.android.sairamedicalstore.ui.prescription.MyPrescriptionsActivity.mArrayListAllPagesOfActivePrescription;
import static com.example.android.sairamedicalstore.ui.prescription.MyPrescriptionsActivity.mHashMapNewUploadedPageStorageUris;
import static com.example.android.sairamedicalstore.ui.prescription.MyPrescriptionsActivity.mRecyclerViewActivePrescriptionAllPages;
import static com.example.android.sairamedicalstore.ui.prescription.MyPrescriptionsActivity.mTextViewNewOrUpdatePrescription;
import static com.example.android.sairamedicalstore.ui.prescription.MyPrescriptionsActivity.mTextViewSaveOrUpdatePrescription;

/**
 * Created by chandan on 17-10-2017.
 */

public class PrescriptionOperations {

    Firebase mFirebaseAllPrescriptionsRef;
    User mCurrentUser;
    Activity mActivity;
    int counter;

    public PrescriptionOperations(Activity mActivity) {
        this.mActivity = mActivity;
        this.mCurrentUser = ((SairaMedicalStoreApplication) mActivity.getApplication()).getCurrentUser();
        this.mFirebaseAllPrescriptionsRef = new Firebase(Constants.FIREBASE_URL_SAIRA_All_PRESCRIPTIONS);

    }

    public void addNewPrescription(final Prescription newPrescription) {

        mFirebaseAllPrescriptionsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    Boolean isExist = false;
                    for (DataSnapshot snapshot:dataSnapshot.getChildren()) {
                        Prescription eachPrescription = snapshot.getValue(Prescription.class);
                        if(eachPrescription != null && eachPrescription.getPrescriptionName().equals(newPrescription.getPrescriptionName()))
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
                            HashMap<String, Object> timestampUploaded = new HashMap<String, Object>();
                            timestampUploaded.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);
                            newPrescription.setTimestampUploaded(timestampUploaded);

                            mFirebaseAllPrescriptionsRef.child(newPrescription.getPrescriptionId()).setValue(newPrescription);
                            uploadImageInAllPrescriptionsStoragePath(newPrescription);

                            Toast.makeText(mActivity, Constants.UPLOAD_SUCCESSFUL, Toast.LENGTH_SHORT).show();
                            mRecyclerViewActivePrescriptionAllPages.setAdapter(null);
                            mArrayListAllPagesOfActivePrescription.clear();
                            mActivePrescriptionIdForNewPrescription = mFirebaseAllPrescriptionsRef.push().getKey();
                        } catch (Exception ex) {
                        }
                    }
                }
                else
                {
                    try {
                        HashMap<String, Object> timestampUploaded = new HashMap<String, Object>();
                        timestampUploaded.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);
                        newPrescription.setTimestampUploaded(timestampUploaded);

                        mFirebaseAllPrescriptionsRef.child(newPrescription.getPrescriptionId()).setValue(newPrescription);
                        uploadImageInAllPrescriptionsStoragePath(newPrescription);

                        Toast.makeText(mActivity, Constants.UPLOAD_SUCCESSFUL, Toast.LENGTH_SHORT).show();
                        mRecyclerViewActivePrescriptionAllPages.setAdapter(null);
                        mArrayListAllPagesOfActivePrescription.clear();
                        mActivePrescriptionIdForNewPrescription = mFirebaseAllPrescriptionsRef.push().getKey();
                    } catch (Exception ex) {
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });

    }

    public void updateSavedPrescription(final String prescriptionIdToBeUpdated,final HashMap<String,PrescriptionPage> mHashMapUpdatedPrescriptionPages)
    {
        mFirebaseAllPrescriptionsRef.child(prescriptionIdToBeUpdated).addListenerForSingleValueEvent(new ValueEventListener() {
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
                        mFirebaseAllPrescriptionsRef.child(prescriptionIdToBeUpdated).
                                setValue(prescriptionToBeUpdated);
                        uploadImageInAllPrescriptionsStoragePath(prescriptionToBeUpdated);

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

    private void uploadImageInAllPrescriptionsStoragePath(final Prescription currentPrescription)
    {
        counter = 0;
        final ArrayList<String> arrayListTemporaryDownloadPath = new ArrayList<>();

        for (final Map.Entry<String,Uri> eachStorageUri :mHashMapNewUploadedPageStorageUris.entrySet() ) {

            if (currentPrescription.getPrescriptionPages().containsKey(eachStorageUri.getKey()))
            {
                FirebaseStorage mFirebaseStorage = FirebaseStorage.getInstance();
                StorageReference selectedPhotoRef = mFirebaseStorage.getReference().child(Constants.FIREBASE_LOCATION_All_PRESCRIPTIONS).
                        child(currentPrescription.getPrescriptionId()).child(eachStorageUri.getValue().getLastPathSegment());

                UploadTask uploadTask = selectedPhotoRef.putFile(eachStorageUri.getValue());
                uploadTask.addOnSuccessListener(mActivity, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        try{
                            PrescriptionPage newPage = currentPrescription.getPrescriptionPages().get(eachStorageUri.getKey());
                            arrayListTemporaryDownloadPath.add(newPage.getImageUri());
                            newPage.setImageUri(downloadUrl.toString());

                            currentPrescription.getPrescriptionPages().remove(eachStorageUri.getKey());
                            currentPrescription.getPrescriptionPages().put(eachStorageUri.getKey(),newPage);
                            counter++;

                            if (counter >= mHashMapNewUploadedPageStorageUris.size()) {
                                try {
                                    mFirebaseAllPrescriptionsRef.child(currentPrescription.getPrescriptionId()).setValue(currentPrescription);
                                    DeleteOldDownloadPath(arrayListTemporaryDownloadPath);
                                    mHashMapNewUploadedPageStorageUris.clear();
                                }catch (Exception ex){
                                    mHashMapNewUploadedPageStorageUris.clear();
                                }

                            }
                        }catch (Exception ex){
                        }
                    }
                });
            }
            else
              counter++;

        }


    }

    private void DeleteOldDownloadPath(ArrayList<String>  arrayListTemporaryDownloadPath)
    {
        for (String downloadUrl:arrayListTemporaryDownloadPath) {
            FirebaseStorage  mFirebaseStorage = FirebaseStorage.getInstance();
            StorageReference photoRef = mFirebaseStorage.getReferenceFromUrl(downloadUrl);

            try{
                photoRef.delete();
            }catch (Exception ex){}
        }

    }

    public void SendPrescriptionForEvaluation(final String prescriptionIdToBeSentForEvaluation)
    {

        mFirebaseAllPrescriptionsRef.child(prescriptionIdToBeSentForEvaluation).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    Prescription prescription = dataSnapshot.getValue(Prescription.class);
                    if (prescription != null && prescription.getPrescriptionOwnerEmail().equals(mCurrentUser.getEmail())) {
                        prescription.setSentForEvaluation(true);
                        mFirebaseAllPrescriptionsRef.child(prescriptionIdToBeSentForEvaluation).setValue(prescription);
                        Toast.makeText(mActivity,"Sent For Evaluation",Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Toast.makeText(mActivity,"Unable to send for evaluation",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void saveEvaluatedPrescription(final Prescription evaluatedPrescription)
    {
        evaluatedPrescription.setSentForEvaluation(false);
        mFirebaseAllPrescriptionsRef.child(evaluatedPrescription.getPrescriptionId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    Prescription prescription = dataSnapshot.getValue(Prescription.class);
                    if (prescription != null) {
                        mFirebaseAllPrescriptionsRef.child(evaluatedPrescription.getPrescriptionId()).setValue(evaluatedPrescription);
                        Toast.makeText(mActivity,Constants.UPDATE_SUCCESSFUL,Toast.LENGTH_SHORT).show();
                        mActivity.finish();
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Toast.makeText(mActivity,Constants.UPDATE_FAIL,Toast.LENGTH_SHORT).show();
            }
        });
    }

}