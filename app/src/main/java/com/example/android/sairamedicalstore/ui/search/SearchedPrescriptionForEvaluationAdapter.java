package com.example.android.sairamedicalstore.ui.search;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.example.android.sairamedicalstore.R;
import com.example.android.sairamedicalstore.models.Prescription;
import com.example.android.sairamedicalstore.models.User;
import com.example.android.sairamedicalstore.utils.Utils;
import com.firebase.client.Query;
import com.firebase.ui.FirebaseListAdapter;

/**
 * Created by chandan on 17-08-2017.
 */

public class SearchedPrescriptionForEvaluationAdapter extends FirebaseListAdapter<Prescription> {


    public SearchedPrescriptionForEvaluationAdapter(Activity activity, Class<Prescription> modelClass, int modelLayout, Query ref) {
        super(activity,modelClass, modelLayout, ref);
        this.mActivity = activity;
    }

    @Override
    protected void populateView(View view, Prescription prescription) {

        TextView textViewPrescriptionName = (TextView) view.findViewById(R.id.text_view_prescription_name);
        textViewPrescriptionName.setText(Utils.toLowerCaseExceptFirstLetter(prescription.getPrescriptionName()));

        TextView textViewSenderEmail = (TextView) view.findViewById(R.id.text_view_sender_email);
        textViewSenderEmail.setText(prescription.getPrescriptionOwnerEmail());

    }


}
