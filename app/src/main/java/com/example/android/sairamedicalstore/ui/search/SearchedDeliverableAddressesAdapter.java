package com.example.android.sairamedicalstore.ui.search;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.example.android.sairamedicalstore.R;
import com.example.android.sairamedicalstore.models.DeliverableAddress;
import com.example.android.sairamedicalstore.models.Faq;
import com.example.android.sairamedicalstore.utils.Utils;
import com.firebase.client.Query;
import com.firebase.ui.FirebaseListAdapter;

/**
 * Created by chandan on 17-08-2017.
 */

public class SearchedDeliverableAddressesAdapter extends FirebaseListAdapter<DeliverableAddress> {


    public SearchedDeliverableAddressesAdapter(Activity activity, Class<DeliverableAddress> modelClass, int modelLayout, Query ref) {
        super(activity,modelClass, modelLayout, ref);
        this.mActivity = activity;
    }

    @Override
    protected void populateView(View view, DeliverableAddress deliverableAddress) {

        TextView textViewNameAndPinCode = (TextView) view.findViewById(R.id.text_view_name_and_pin);
        TextView textViewDistrictStateCountry = (TextView) view.findViewById(R.id.text_view_district_state_country);

        String completeAddress = "", nameAndPin = "";

        if (deliverableAddress.getName() != null)
            nameAndPin = nameAndPin + Utils.toLowerCaseExceptFirstLetter( deliverableAddress.getName()) + ",";
        if (deliverableAddress.getPinCode() != null)
            nameAndPin = nameAndPin + deliverableAddress.getPinCode();


        if (deliverableAddress.getDistrict() != null)
            completeAddress = completeAddress + Utils.toLowerCaseExceptFirstLetter( deliverableAddress.getDistrict()) + ",";
        if (deliverableAddress.getState() != null)
            completeAddress = completeAddress + Utils.toLowerCaseExceptFirstLetter( deliverableAddress.getState()) + ",";
        if (deliverableAddress.getCountry() != null)
            completeAddress = completeAddress + Utils.toLowerCaseExceptFirstLetter( deliverableAddress.getCountry());

        textViewDistrictStateCountry.setText(completeAddress);
        textViewNameAndPinCode.setText(nameAndPin);

    }

}
