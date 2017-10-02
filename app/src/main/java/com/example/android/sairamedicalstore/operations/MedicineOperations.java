package com.example.android.sairamedicalstore.operations;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
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
import com.example.android.sairamedicalstore.models.MedicinePic;
import com.example.android.sairamedicalstore.models.SelectedItem;
import com.example.android.sairamedicalstore.models.User;
import com.example.android.sairamedicalstore.utils.Constants;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ServerValue;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import static android.R.attr.category;

/**
 * Created by chandan on 15-08-2017.
 */

public class MedicineOperations {

    Firebase mFirebaseAllMedicinesReference, mFirebaseAllManufacturerRef, mFirebaseAllCompositionsRef;
    User mCurrentUser;
    ArrayList<MedicinePic> mArrayListDefaultMedicinePics;
    Activity mActivity;

    public MedicineOperations(User currentUser,Activity callingActivity) {
        this.mActivity = callingActivity;
        this.mFirebaseAllMedicinesReference = new Firebase(Constants.FIREBASE_URL_SAIRA_ALL_MEDICINES);
        this.mFirebaseAllManufacturerRef = new Firebase(Constants.FIREBASE_URL_SAIRA_All_MANUFACTURERS);
        this.mFirebaseAllCompositionsRef = new Firebase(Constants.FIREBASE_URL_SAIRA_All_COMPOSITIONS);
        mCurrentUser = currentUser;
        mArrayListDefaultMedicinePics = new ArrayList<MedicinePic>();
    }

    public void AddNewMedicine(final String medicineName, String medicineManufacturerName, String medicineComposition, String medicineCategory,
                               final String displayCategoryNames, final String medicineType, final String medicineImageUrl, int noOfItemsInOnePack,
                               final double pricePerPack, boolean medicineAvailability, boolean isLooseAvailable) {
        final String medicineId = mFirebaseAllMedicinesReference.push().getKey();
        double priceStack = pricePerPack;
        String updaterStack = mCurrentUser.getEmail();
        HashMap<String, Object> timestampCreated = new HashMap<String, Object>();
        timestampCreated.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);
        HashMap<String, Object> timestampLastUpdate = timestampCreated;

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
                                AddNewProductToDisplay(stringOfDisplaycategoryNames[i].toUpperCase(),medicineId,medicineName,medicineImageUrl,medicineType,pricePerPack);
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

    private void AddNewProductToDisplay(String displayCategoryName,final String productId, String productName,
                                        String productImageUrl, String productType, double pricePerPack)
    {
        final DisplayProduct currentDisplayProduct = new DisplayProduct(productId,productName,productImageUrl,productType,pricePerPack);

        Firebase firebaseAllDisplayRef = new Firebase(Constants.FIREBASE_URL_SAIRA_All_DISPLAY);
        final Firebase firebaseCurrentDisplayRef = firebaseAllDisplayRef.child(displayCategoryName);
        firebaseCurrentDisplayRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                    Boolean isProductExist = false;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        DisplayProduct eachDisplayProduct = snapshot.getValue(DisplayProduct.class);
                        if (eachDisplayProduct.getProductId().equals(productId)) {
                            isProductExist = true;
                            break;
                        }
                    }
                    if(!isProductExist) {
                        try {
                            firebaseCurrentDisplayRef.child(productId).setValue(currentDisplayProduct);
                        } catch (Exception ex) {
                        }
                    }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Toast.makeText(mActivity, Constants.UPLOAD_FAIL, Toast.LENGTH_SHORT).show();
            }

        });

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
                        MedicinePic pic = snapshot.getValue(MedicinePic.class);
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
