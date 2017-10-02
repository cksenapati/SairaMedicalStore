package com.example.android.sairamedicalstore.ui.search;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.sairamedicalstore.R;
import com.example.android.sairamedicalstore.SairaMedicalStoreApplication;
import com.example.android.sairamedicalstore.models.Medicine;
import com.example.android.sairamedicalstore.models.MedicinePic;
import com.example.android.sairamedicalstore.utils.Utils;
import com.firebase.client.Firebase;
import com.firebase.client.Query;
import com.firebase.ui.FirebaseListAdapter;

import java.util.ArrayList;

import static android.R.attr.x;

/**
 * Created by chandan on 17-08-2017.
 */

public class SearchedMedicinesAdapter extends FirebaseListAdapter<Medicine> {

    ArrayList<MedicinePic> mArrayListDefaultMedicinePics;

    public SearchedMedicinesAdapter(Activity activity,Class<Medicine> modelClass, int modelLayout, Query ref) {
        super(activity,modelClass, modelLayout, ref);
        this.mActivity = activity;
        mArrayListDefaultMedicinePics = ((SairaMedicalStoreApplication) mActivity.getApplication()).getArrayListDefaultMedicinePics();
    }

    @Override
    protected void populateView(View view, Medicine medicine) {

        String displayableMedicineName = Utils.toLowerCaseExceptFirstLetter(medicine.getMedicineName());
        String displayableManufacturerName = Utils.toLowerCaseExceptFirstLetter(medicine.getMedicineManufacturerName());

        ImageView imageViewSingleMedicine = (ImageView) view.findViewById(R.id.image_view_single_medicine);
        Glide.with(imageViewSingleMedicine.getContext())
                .load(getImageUrlToDisplay(medicine))
                .into(imageViewSingleMedicine);

        TextView textViewSingleMedicineName = (TextView) view.findViewById(R.id.text_view_single_medicine_name);
        textViewSingleMedicineName.setText(displayableMedicineName);

        TextView textViewSingleMedicineManufacturer = (TextView) view.findViewById(R.id.text_view_single_medicine_manufacturer);
        textViewSingleMedicineManufacturer.setText(displayableManufacturerName);

        TextView textViewSingleMedicinePricePerPack = (TextView) view.findViewById(R.id.text_view_single_medicine_price_per_pack);
        textViewSingleMedicinePricePerPack.setText("Rs " + String.valueOf(medicine.getPricePerPack()) + " per pack");

        TextView textViewSingleMedicineType = (TextView) view.findViewById(R.id.text_view_single_medicine_type);
        textViewSingleMedicineType.setText(medicine.getMedicineType()+" (" + medicine.getNoOfItemsInOnePack() + " items in a pack)");

    }

    public String getImageUrlToDisplay(Medicine medicine)
    {
        String imageUrl = medicine.getMedicineImageUrl();
        String medicineType = medicine.getMedicineType();
        if(imageUrl.equals("default"))
        {
            for (int i = 0; i < mArrayListDefaultMedicinePics.size(); i++) {
                if(mArrayListDefaultMedicinePics.get(i).getMedicineType().equals(medicineType))
                    return mArrayListDefaultMedicinePics.get(i).getPicUrl();
            }
            return mArrayListDefaultMedicinePics.get(0).getPicUrl();
        }
        else
            return imageUrl;
    }
}