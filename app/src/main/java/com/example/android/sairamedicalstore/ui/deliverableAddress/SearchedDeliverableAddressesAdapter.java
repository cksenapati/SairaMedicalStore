package com.example.android.sairamedicalstore.ui.deliverableAddress;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.android.sairamedicalstore.R;
import com.example.android.sairamedicalstore.models.DeliverableAddress;
import com.example.android.sairamedicalstore.models.Query;
import com.example.android.sairamedicalstore.utils.Constants;
import com.example.android.sairamedicalstore.utils.Utils;

import java.util.ArrayList;
import java.util.Date;


/**
 * Created by chandan on 08-03-2017.
 */

public class SearchedDeliverableAddressesAdapter extends ArrayAdapter<DeliverableAddress> {

    Activity mActivity;

    public SearchedDeliverableAddressesAdapter(Activity activity, ArrayList<DeliverableAddress> arrayListDeliverableAddresses) {
        super(activity, 0, arrayListDeliverableAddresses);
        mActivity = activity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final DeliverableAddress eachAddress = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_single_deliverable_address, parent, false);
        }

        TextView textViewNameAndPinCode = (TextView) convertView.findViewById(R.id.text_view_name_and_pin);
        TextView textViewDistrictStateCountry = (TextView) convertView.findViewById(R.id.text_view_district_state_country);


        String completeAddress = "", nameAndPin = "";

        if (eachAddress.getName() != null)
            nameAndPin = nameAndPin + Utils.toLowerCaseExceptFirstLetter( eachAddress.getName()) + ",";
        if (eachAddress.getPinCode() != null)
            nameAndPin = nameAndPin + eachAddress.getPinCode();


        if (eachAddress.getDistrict() != null)
            completeAddress = completeAddress + Utils.toLowerCaseExceptFirstLetter(eachAddress.getDistrict()) + ",";
        if (eachAddress.getState() != null)
            completeAddress = completeAddress + Utils.toLowerCaseExceptFirstLetter(eachAddress.getState()) + ",";
        if (eachAddress.getCountry() != null)
            completeAddress = completeAddress +Utils.toLowerCaseExceptFirstLetter( eachAddress.getCountry());

        textViewDistrictStateCountry.setText(completeAddress);
        textViewNameAndPinCode.setText(nameAndPin);

        return convertView;
    }


}

