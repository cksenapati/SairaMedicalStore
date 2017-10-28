package com.example.android.sairamedicalstore.ui.address;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.sairamedicalstore.R;
import com.example.android.sairamedicalstore.models.Address;
import com.example.android.sairamedicalstore.models.Manufacturer;
import com.example.android.sairamedicalstore.utils.Utils;
import com.firebase.client.Query;
import com.firebase.ui.FirebaseListAdapter;

import org.w3c.dom.Text;

/**
 * Created by chandan on 17-08-2017.
 */

public class AllSavedAddressesAdapter extends FirebaseListAdapter<Address> {


    public AllSavedAddressesAdapter(Activity activity, Class<Address> modelClass, int modelLayout, Query ref) {
        super(activity, modelClass, modelLayout, ref);
        this.mActivity = activity;
    }

    @Override
    protected void populateView(View view, Address address) {

        TextView textViewAddressDetails = (TextView) view.findViewById(R.id.text_view_address_details);
        textViewAddressDetails.setText(address.getFullName()+"\n"+address.getAddress()+"\n"+address.getLandmark() +
                "\n"+address.getCity() + " - " + address.getPinCode() + "\n" + address.getState() + ", India \n" + address.getPhoneNumber() );

        LinearLayout linearLayoutSelectableItem = (LinearLayout) view.findViewById(R.id.linear_layout_selectable_item);
        TextView textViewEdit = (TextView) view.findViewById(R.id.text_view_edit);
        TextView textViewRemove = (TextView) view.findViewById(R.id.text_view_remove);

    }

}
