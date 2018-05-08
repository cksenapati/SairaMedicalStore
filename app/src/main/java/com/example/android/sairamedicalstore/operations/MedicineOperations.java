package com.example.android.sairamedicalstore.operations;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.net.Uri;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.sairamedicalstore.R;
import com.example.android.sairamedicalstore.SairaMedicalStoreApplication;
import com.example.android.sairamedicalstore.models.Composition;
import com.example.android.sairamedicalstore.models.DisplayCategory;
import com.example.android.sairamedicalstore.models.DisplayProduct;
import com.example.android.sairamedicalstore.models.Manufacturer;
import com.example.android.sairamedicalstore.models.Medicine;
import com.example.android.sairamedicalstore.models.DefaultKeyValuePair;
import com.example.android.sairamedicalstore.models.User;
import com.example.android.sairamedicalstore.utils.Constants;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ServerValue;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by chandan on 15-08-2017.
 */

public class MedicineOperations {

    Firebase mFirebaseAllMedicinesReference, mFirebaseAllManufacturerRef, mFirebaseAllCompositionsRef;
    User mCurrentUser;
    ArrayList<DefaultKeyValuePair> mArrayListDefaultMedicinePics;
    Activity mActivity;

    public MedicineOperations(User currentUser,Activity callingActivity) {
        this.mActivity = callingActivity;
        this.mFirebaseAllMedicinesReference = new Firebase(Constants.FIREBASE_URL_SAIRA_ALL_MEDICINES);
        this.mFirebaseAllManufacturerRef = new Firebase(Constants.FIREBASE_URL_SAIRA_All_MANUFACTURERS);
        this.mFirebaseAllCompositionsRef = new Firebase(Constants.FIREBASE_URL_SAIRA_All_COMPOSITIONS);
        mCurrentUser = currentUser;
        mArrayListDefaultMedicinePics = new ArrayList<DefaultKeyValuePair>();
    }

    public void AddNewMedicine(final String medicineName, String medicineManufacturerName, String medicineComposition, String medicineCategory,
                               final String displayCategoryNames, final String medicineType, final String medicineImageUrl, int noOfItemsInOnePack,
                               final double pricePerPack, boolean medicineAvailability, boolean isLooseAvailable) {
        final String medicineId = mFirebaseAllMedicinesReference.push().getKey();
        //double priceStack = pricePerPack;
        //String updaterStack = mCurrentUser.getEmail();
        Object timestamp = ServerValue.TIMESTAMP;

        HashMap<String, Object> timestampCreated = new HashMap<String, Object>();
        timestampCreated.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, timestamp);
        HashMap<String, Object> timestampLastUpdate = timestampCreated;


        String timeStampInStringFormat = new SimpleDateFormat("MMM dd yyyy HH:mm:ss").format(new Date());
        HashMap<String, String>  updaterStack = new HashMap<>();
        updaterStack.put(timeStampInStringFormat, mCurrentUser.getEmail());

        HashMap<String, String>  priceStack = new HashMap<>();
        priceStack.put(timeStampInStringFormat, String.valueOf(pricePerPack));


        final Medicine newMedicine = new Medicine(medicineId,medicineName, medicineManufacturerName, medicineComposition, medicineCategory, medicineType, medicineImageUrl,
                updaterStack,displayCategoryNames, noOfItemsInOnePack, pricePerPack, priceStack, medicineAvailability, isLooseAvailable, timestampCreated, timestampLastUpdate);

        mFirebaseAllMedicinesReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Boolean isExist = false;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Medicine eachMedicine = snapshot.getValue(Medicine.class);
                    if (eachMedicine.getMedicineName().equals(medicineName) && eachMedicine.getMedicineType().equals(medicineType)) {
                        Toast.makeText(mActivity, Constants.ITEM_ALREADY_EXISTS, Toast.LENGTH_SHORT).show();
                        isExist = true;
                        break;
                    }
                }
                if(!isExist) {
                    try {
                        mFirebaseAllMedicinesReference.child(medicineId).setValue(newMedicine);
                        resetAllControlsFromAddNewMedicinesPage();
                        if(displayCategoryNames != null && displayCategoryNames.trim().length() > 0)
                        {
                            String[] stringOfDisplaycategoryNames = displayCategoryNames.split(",");
                            for(int i =0 ; i<stringOfDisplaycategoryNames.length;i++)
                            {
                                //AddNewProductToDisplay(stringOfDisplaycategoryNames[i].toUpperCase(),medicineId,medicineName,medicineImageUrl,medicineType,pricePerPack);
                                AddMedicinesToDisplay(newMedicine,stringOfDisplaycategoryNames[i].toUpperCase());
                            }
                        }
                        Toast.makeText(mActivity, Constants.UPLOAD_SUCCESSFUL, Toast.LENGTH_SHORT).show();

                    } catch (Exception ex) {
                        Toast.makeText(mActivity, Constants.UPLOAD_FAIL, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Toast.makeText(mActivity, Constants.UPLOAD_FAIL, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void UpdateMedicine(final Medicine medicineToBeUpdated,final Uri currentMedicineImageURI)
    {
        mFirebaseAllMedicinesReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Boolean isExist = false;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Medicine eachMedicine = snapshot.getValue(Medicine.class);
                    if (eachMedicine.getMedicineName().equals(medicineToBeUpdated.getMedicineName()) && eachMedicine.getMedicineType().equals(medicineToBeUpdated.getMedicineType())
                            && !eachMedicine.getMedicineId().equals(medicineToBeUpdated.getMedicineId()) ) {
                        Toast.makeText(mActivity, Constants.ITEM_ALREADY_EXISTS, Toast.LENGTH_SHORT).show();
                        isExist = true;
                        break;
                    }
                }
                if(!isExist) {
                    try {
                        mFirebaseAllMedicinesReference.child(medicineToBeUpdated.getMedicineId()).setValue(medicineToBeUpdated);

                        if (currentMedicineImageURI == null ){
                            if(medicineToBeUpdated.getDisplayCategory() != null && medicineToBeUpdated.getDisplayCategory().trim().length() > 0)
                                RemoveAndAddMedicinesFromDisplay(medicineToBeUpdated);
                            else
                            {
                                Toast.makeText(mActivity, Constants.UPDATE_SUCCESSFUL, Toast.LENGTH_SHORT).show();
                                mActivity.finish();
                            }
                        }
                        else
                            uploadImageInStorage(currentMedicineImageURI,medicineToBeUpdated);

                    } catch (Exception ex) {
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

    private void uploadImageInStorage(Uri currentMedicineImageURI,final Medicine medicineToBeUpdated)
    {
        FirebaseStorage mFirebaseStorage = FirebaseStorage.getInstance();
        StorageReference mFirebaseAllMedicinesStorageReference = mFirebaseStorage.getReference().child(Constants.FIREBASE_LOCATION_All_MEDICINES);


        StorageReference selectedPhotoRef = mFirebaseAllMedicinesStorageReference.child(medicineToBeUpdated.getMedicineId()).child(currentMedicineImageURI.getLastPathSegment());
        UploadTask uploadTask = selectedPhotoRef.putFile(currentMedicineImageURI);
        uploadTask.addOnSuccessListener(mActivity, new OnSuccessListener<UploadTask.TaskSnapshot>() {
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                try{
                    mFirebaseAllMedicinesReference.child(medicineToBeUpdated.getMedicineId()).child("medicineImageUrl").setValue(downloadUrl.toString());
                    RemoveOldDownloadPath(medicineToBeUpdated.getMedicineImageUrl());

                    medicineToBeUpdated.setMedicineImageUrl(downloadUrl.toString());
                    if(medicineToBeUpdated.getDisplayCategory() != null && medicineToBeUpdated.getDisplayCategory().trim().length() > 0)
                        RemoveAndAddMedicinesFromDisplay(medicineToBeUpdated);
                    else
                    {
                        Toast.makeText(mActivity, Constants.UPDATE_SUCCESSFUL, Toast.LENGTH_SHORT).show();
                        mActivity.finish();
                    }

                }catch (Exception ex){}
            }
        });

    }

    private void RemoveOldDownloadPath(String medicineImageDownloadUrl)
    {
        FirebaseStorage  mFirebaseStorage = FirebaseStorage.getInstance();
        StorageReference photoRef = mFirebaseStorage.getReferenceFromUrl(medicineImageDownloadUrl);

        try{
            photoRef.delete();
        }catch (Exception ex){}
    }

    public void RemoveAndAddMedicinesFromDisplay(final Medicine medicineToBeRemoved)
    {
        final Firebase firebaseAllDisplayRef = new Firebase(Constants.FIREBASE_URL_SAIRA_All_DISPLAY);
        Firebase firebaseAllDisplayCategoriesRef = new Firebase(Constants.FIREBASE_URL_SAIRA_All_DISPLAY_CATEGORIES);

        firebaseAllDisplayCategoriesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    for (DataSnapshot snapshot:dataSnapshot.getChildren()) {
                        DisplayCategory displayCategory = snapshot.getValue(DisplayCategory.class);
                        if (displayCategory != null)
                        {
                            try {
                                firebaseAllDisplayRef.child(displayCategory.getDisplayCategoryName()).child(medicineToBeRemoved.getMedicineId()).setValue(null);
                                String[] stringOfDisplaycategoryNames = medicineToBeRemoved.getDisplayCategory().split(",");
                                for(int i =0 ; i<stringOfDisplaycategoryNames.length;i++)
                                    AddMedicinesToDisplay(medicineToBeRemoved,stringOfDisplaycategoryNames[i].toUpperCase());

                                mActivity.finish();

                            }
                            catch (Exception ex){}
                        }
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void AddMedicinesToDisplay(final Medicine medicineToBeAdded,String displayCategoryName)
    {
        DisplayProduct currentDisplayProduct = new DisplayProduct(medicineToBeAdded.getMedicineId(),
                medicineToBeAdded.getMedicineName(),medicineToBeAdded.getMedicineImageUrl(),medicineToBeAdded.getMedicineType()
                ,medicineToBeAdded.getPricePerPack());

        Firebase firebaseCurrentDisplayRef = new Firebase(Constants.FIREBASE_URL_SAIRA_All_DISPLAY).child(displayCategoryName);

        try{
            firebaseCurrentDisplayRef.child(medicineToBeAdded.getMedicineId()).setValue(currentDisplayProduct);
        }catch (Exception ex){}

    }



    public void addNewManufacturer(final String manufacturerName,final Dialog addNewItemDialog, final TextView textViewErrorMsg) {
        HashMap<String, Object> timestampCreated = new HashMap<String, Object>();
        timestampCreated.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);
        HashMap<String, Object> timestampLastUpdate = timestampCreated;

        String createdBy = mCurrentUser.getEmail();

        final Manufacturer manufacturer = new Manufacturer(manufacturerName, createdBy, timestampCreated, timestampLastUpdate);

        mFirebaseAllManufacturerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Boolean isExist = false;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Manufacturer eachManufacturer = snapshot.getValue(Manufacturer.class);
                    if (eachManufacturer.getManufacturerName().equals(manufacturerName)) {
                        textViewErrorMsg.setText(Constants.ITEM_ALREADY_EXISTS);
                        isExist = true;
                        break;
                    }
                }
                if(!isExist) {
                    try {
                        mFirebaseAllManufacturerRef.push().setValue(manufacturer);
                        addNewItemDialog.dismiss();
                        Toast.makeText(mActivity, Constants.UPLOAD_SUCCESSFUL, Toast.LENGTH_SHORT).show();
                    } catch (Exception ex) {
                        Toast.makeText(mActivity, Constants.UPLOAD_FAIL, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Toast.makeText(mActivity, Constants.UPLOAD_FAIL, Toast.LENGTH_SHORT).show();
            }

        });
    }

    public void addNewComposition(final String compositionName,final Dialog addNewItemDialog, final TextView textViewErrorMsg) {
        HashMap<String, Object> timestampCreated = new HashMap<String, Object>();
        timestampCreated.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);
        HashMap<String, Object> timestampLastUpdate = timestampCreated;

        String createdBy = mCurrentUser.getEmail();

        final Composition composition = new Composition(compositionName, createdBy, timestampCreated, timestampLastUpdate);

        mFirebaseAllCompositionsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Boolean isExist = false;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Composition eachComposition = snapshot.getValue(Composition.class);
                    if (eachComposition.getCompositionName().equals(compositionName)) {
                        textViewErrorMsg.setText(Constants.ITEM_ALREADY_EXISTS);
                        isExist = true;
                        break;
                    }
                }
                if(!isExist) {
                    try {
                        mFirebaseAllCompositionsRef.push().setValue(composition);
                        addNewItemDialog.dismiss();
                        Toast.makeText(mActivity, Constants.UPLOAD_SUCCESSFUL, Toast.LENGTH_SHORT).show();
                    } catch (Exception ex) {
                        Toast.makeText(mActivity, Constants.UPLOAD_FAIL, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Toast.makeText(mActivity, Constants.UPLOAD_FAIL, Toast.LENGTH_SHORT).show();
            }

        });
    }

    public void addNewDisplayCategory(final String categoryName, final Dialog addNewItemDialog, final TextView textViewErrorMsg) {
        HashMap<String, Object> timestampCreated = new HashMap<String, Object>();
        timestampCreated.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);

        String categoryCreatedBy = mCurrentUser.getEmail();

        final DisplayCategory category = new DisplayCategory(categoryName, categoryCreatedBy, timestampCreated);

        final Firebase firebaseAllDisplayCategoriesRef = new Firebase(Constants.FIREBASE_URL_SAIRA_All_DISPLAY_CATEGORIES);
        firebaseAllDisplayCategoriesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Boolean isExist = false;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    DisplayCategory eachDisplayCategory = snapshot.getValue(DisplayCategory.class);
                    if (eachDisplayCategory.getDisplayCategoryName().equals(categoryName)) {
                        textViewErrorMsg.setText(Constants.ITEM_ALREADY_EXISTS);
                        isExist = true;
                        break;
                    }
                }
                if(!isExist) {
                    try {
                        firebaseAllDisplayCategoriesRef.child(categoryName).setValue(category);
                        addNewItemDialog.dismiss();
                        Toast.makeText(mActivity, Constants.UPLOAD_SUCCESSFUL, Toast.LENGTH_SHORT).show();
                    } catch (Exception ex) {
                        Toast.makeText(mActivity, Constants.UPLOAD_FAIL, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Toast.makeText(mActivity, Constants.UPLOAD_FAIL, Toast.LENGTH_SHORT).show();
            }

        });
    }


    public void getAllDefaultValues() {

        final Application sairaMedicalStoreApplication = mActivity.getApplication();

        Firebase allDefaultValuesRef = new Firebase(Constants.FIREBASE_URL_SAIRA_All_DEFAULT_VALUES);

        Firebase defaultMedicinePics = allDefaultValuesRef.child(Constants.FIREBASE_PROPERTY_DEFAULT_MEDICINE_PICS);
        defaultMedicinePics.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    mArrayListDefaultMedicinePics.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        DefaultKeyValuePair pic = snapshot.getValue(DefaultKeyValuePair.class);
                        mArrayListDefaultMedicinePics.add(pic);
                    }

                    ((SairaMedicalStoreApplication) sairaMedicalStoreApplication).
                            setArrayListDefaultMedicinePics(mArrayListDefaultMedicinePics);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    private void resetAllControlsFromAddNewMedicinesPage()
    {
        LinearLayout linearLayout = (LinearLayout) mActivity.findViewById(R.id.linear_layout_main_content);
       ImageView imageView = (ImageView) mActivity.findViewById(R.id.image_view_medicine_image);

        for( int i = 0; i < linearLayout.getChildCount(); i++ ) {
            View view = linearLayout.getChildAt(i);
            if (view instanceof EditText) {
                ((EditText)view).setText("");
            }
            else if(view instanceof LinearLayout)
            {
                View view2 = ((LinearLayout) view).getChildAt(1);
                if(view2 instanceof Spinner)
                    ((Spinner)view2).setSelection(0);
            }
        }
    }
}
