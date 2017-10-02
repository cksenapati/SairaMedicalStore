package com.example.android.sairamedicalstore.ui.search;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.sairamedicalstore.R;
import com.example.android.sairamedicalstore.models.Manufacturer;
import com.example.android.sairamedicalstore.models.SelectedItem;
import com.example.android.sairamedicalstore.utils.Utils;
import com.firebase.client.Query;
import com.firebase.client.collection.ArraySortedMap;
import com.firebase.ui.FirebaseListAdapter;

import java.util.ArrayList;

/**
 * Created by chandan on 17-08-2017.
 */

public class SearchedManufacturersAdapter extends FirebaseListAdapter<Manufacturer> {


    public SearchedManufacturersAdapter(Activity activity, Class<Manufacturer> modelClass, int modelLayout, Query ref) {
        super(activity, modelClass, modelLayout, ref);
        this.mActivity = activity;
    }

    @Override
    protected void populateView(View view, Manufacturer manufacturer) {

        String displayableName = Utils.toLowerCaseExceptFirstLetter(manufacturer.getManufacturerName());
        TextView textViewManufacturerName = (TextView) view.findViewById(R.id.text_view_item_name);
        textViewManufacturerName.setText(displayableName);

        ImageView imageView = (ImageView) view.findViewById(R.id.image_view_add);
        imageView.setVisibility(View.GONE);

    }

}
