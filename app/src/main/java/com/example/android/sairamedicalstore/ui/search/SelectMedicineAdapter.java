package com.example.android.sairamedicalstore.ui.search;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.sairamedicalstore.R;
import com.example.android.sairamedicalstore.SairaMedicalStoreApplication;
import com.example.android.sairamedicalstore.models.DefaultKeyValuePair;
import com.example.android.sairamedicalstore.models.Medicine;
import com.example.android.sairamedicalstore.models.SelectedItem;
import com.example.android.sairamedicalstore.utils.Utils;
import com.firebase.client.Query;
import com.firebase.ui.FirebaseListAdapter;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by chandan on 17-08-2017.
 */

public class SelectMedicineAdapter extends FirebaseListAdapter<Medicine> {

    ArrayList<DefaultKeyValuePair> mArrayListDefaultMedicinePics;
    HashMap<String,String> mHashMapSelectedMedicineIdsAndNames;
    HashMap<String,Integer> mHashMapSelectedMedicineIdsAndItemCounts;


    public SelectMedicineAdapter(Activity activity, Class<Medicine> modelClass, int modelLayout, Query ref,
                                 HashMap<String,String> hashMapSelectedMedicineIdsAndNames,HashMap<String,Integer> hashMapSelectedMedicineIdsAndItemCounts) {
        super(activity,modelClass, modelLayout, ref);
        this.mActivity = activity;
        mArrayListDefaultMedicinePics = ((SairaMedicalStoreApplication) mActivity.getApplication()).getArrayListDefaultMedicinePics();
        this.mHashMapSelectedMedicineIdsAndNames = hashMapSelectedMedicineIdsAndNames;
        this.mHashMapSelectedMedicineIdsAndItemCounts = hashMapSelectedMedicineIdsAndItemCounts;
    }

    @Override
    protected void populateView(View view, Medicine medicine) {

        String displayableMedicineName = Utils.toLowerCaseExceptFirstLetter(medicine.getMedicineName());
        String displayableManufacturerName = Utils.toLowerCaseExceptFirstLetter(medicine.getMedicineManufacturerName());
        String displayableMedicineType = Utils.toLowerCaseExceptFirstLetter(medicine.getMedicineType());

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
        textViewSingleMedicineType.setText(displayableMedicineType+" (" + medicine.getNoOfItemsInOnePack() + " items in a pack)");

        TextView textViewSingleMedicineItemCount = (TextView) view.findViewById(R.id.text_view_single_medicine_item_count);
        textViewSingleMedicineItemCount.setVisibility(View.GONE);
        if (mHashMapSelectedMedicineIdsAndItemCounts != null && mHashMapSelectedMedicineIdsAndItemCounts.size() > 0) {
            if (mHashMapSelectedMedicineIdsAndItemCounts.get(medicine.getMedicineId()) != null) {
                textViewSingleMedicineItemCount.setVisibility(View.VISIBLE);
                textViewSingleMedicineItemCount.setText(mHashMapSelectedMedicineIdsAndItemCounts.get(medicine.getMedicineId()).toString());
            }
        }

        ImageView imageViewSelected = (ImageView) view.findViewById(R.id.image_view_add);
        imageViewSelected.setVisibility(View.GONE);
        if (mHashMapSelectedMedicineIdsAndNames != null && mHashMapSelectedMedicineIdsAndNames.size() > 0) {
            if (mHashMapSelectedMedicineIdsAndNames.get(medicine.getMedicineId()) != null)
                imageViewSelected.setVisibility(View.VISIBLE);
        }


    }

    public String getImageUrlToDisplay(Medicine medicine)
    {
        String imageUrl = medicine.getMedicineImageUrl();
        String medicineType = medicine.getMedicineType();
        if(imageUrl.equals("default"))
        {
            for (int i = 0; i < mArrayListDefaultMedicinePics.size(); i++) {
                if(mArrayListDefaultMedicinePics.get(i).getKey().equals(medicineType))
                    return mArrayListDefaultMedicinePics.get(i).getValue();
            }
            return mArrayListDefaultMedicinePics.get(0).getValue();
        }
        else
            return imageUrl;
    }
}
