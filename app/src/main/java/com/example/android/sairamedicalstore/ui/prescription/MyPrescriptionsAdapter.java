package com.example.android.sairamedicalstore.ui.prescription;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.sairamedicalstore.R;
import com.example.android.sairamedicalstore.SairaMedicalStoreApplication;
import com.example.android.sairamedicalstore.models.DefaultKeyValuePair;
import com.example.android.sairamedicalstore.models.Medicine;
import com.example.android.sairamedicalstore.models.Prescription;
import com.example.android.sairamedicalstore.utils.Utils;
import com.firebase.client.Query;
import com.firebase.ui.FirebaseListAdapter;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by chandan on 17-08-2017.
 */

public class MyPrescriptionsAdapter extends FirebaseListAdapter<Prescription> {


    public MyPrescriptionsAdapter(Activity activity, Class<Prescription> modelClass, int modelLayout, Query ref) {
        super(activity,modelClass, modelLayout, ref);
        this.mActivity = activity;
    }

    @Override
    protected void populateView(View view, Prescription prescription) {

        ImageView imageViewPage1 = (ImageView) view.findViewById(R.id.image_view_page1);
        Glide.with(imageViewPage1.getContext())
                .load(prescription.getPrescriptionPages().get("page1"))
                .into(imageViewPage1);

        /*ImageView imageViewPage2 = (ImageView) view.findViewById(R.id.image_view_page2);
        ImageView imageViewPage3 = (ImageView) view.findViewById(R.id.image_view_page3);
        ImageView imageViewPage4 = (ImageView) view.findViewById(R.id.image_view_page4);

        TextView  textViewMorePages = (TextView) view.findViewById(R.id.text_view_more_pages);

        int countPages = prescription.getPrescriptionPages().size();
        switch (countPages)
        {
            case 0:
                return;
            case 1:
                imageViewPage1.setVisibility(View.VISIBLE);
                imageViewPage2.setVisibility(View.GONE);
                imageViewPage3.setVisibility(View.GONE);
                imageViewPage4.setVisibility(View.GONE);
                textViewMorePages.setVisibility(View.GONE);
                break;
            case 2:
                imageViewPage1.setVisibility(View.VISIBLE);
                imageViewPage2.setVisibility(View.VISIBLE);
                imageViewPage3.setVisibility(View.GONE);
                imageViewPage4.setVisibility(View.GONE);
                textViewMorePages.setVisibility(View.GONE);
                break;
            case 3:
                imageViewPage1.setVisibility(View.VISIBLE);
                imageViewPage2.setVisibility(View.VISIBLE);
                imageViewPage3.setVisibility(View.VISIBLE);
                imageViewPage4.setVisibility(View.GONE);
                textViewMorePages.setVisibility(View.GONE);
                break;
            case 4:
                imageViewPage1.setVisibility(View.VISIBLE);
                imageViewPage2.setVisibility(View.VISIBLE);
                imageViewPage3.setVisibility(View.VISIBLE);
                imageViewPage4.setVisibility(View.VISIBLE);
                textViewMorePages.setVisibility(View.GONE);
                break;
            default:
                imageViewPage1.setVisibility(View.VISIBLE);
                imageViewPage2.setVisibility(View.VISIBLE);
                imageViewPage3.setVisibility(View.VISIBLE);
                imageViewPage4.setVisibility(View.VISIBLE);
                textViewMorePages.setVisibility(View.VISIBLE);
                break;
        }

        int count = 1;
        for (Map.Entry<String, String> eachEntry :prescription.getPrescriptionPages().entrySet()) {
            if (count == 1)
            Glide.with(imageViewPage1.getContext())
                    .load(eachEntry.getValue())
                    .into(imageViewPage1);
            else if (count == 2)
                Glide.with(imageViewPage1.getContext())
                        .load(eachEntry.getValue())
                        .into(imageViewPage1);
            else if (count == 3)
                Glide.with(imageViewPage1.getContext())
                        .load(eachEntry.getValue())
                        .into(imageViewPage1);
            else if (count == 4)
                Glide.with(imageViewPage1.getContext())
                        .load(eachEntry.getValue())
                        .into(imageViewPage1);
            else if (count > 4)
            {
               textViewMorePages.setText("+" + (countPages - 3));
                break;
            }

            count++;
        }
        */
    }

}
