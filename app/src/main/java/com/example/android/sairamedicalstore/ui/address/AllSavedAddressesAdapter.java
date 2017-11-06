package com.example.android.sairamedicalstore.ui.address;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.sairamedicalstore.R;
import com.example.android.sairamedicalstore.SairaMedicalStoreApplication;
import com.example.android.sairamedicalstore.models.Address;
import com.example.android.sairamedicalstore.models.Manufacturer;
import com.example.android.sairamedicalstore.models.User;
import com.example.android.sairamedicalstore.ui.order.OrderDetailsActivity;
import com.example.android.sairamedicalstore.utils.Constants;
import com.example.android.sairamedicalstore.utils.Utils;
import com.firebase.client.Firebase;
import com.firebase.client.Query;
import com.firebase.ui.FirebaseListAdapter;

import org.w3c.dom.Text;

import static com.example.android.sairamedicalstore.ui.address.DeliveryAddressActivity.mCurrentTemporaryOrder;

/**
 * Created by chandan on 17-08-2017.
 */

public class AllSavedAddressesAdapter extends FirebaseListAdapter<Address> {

    Firebase firebaseCurrentUserAllAddressesRef;
    User mCurrentUser;

    public AllSavedAddressesAdapter(Activity activity, Class<Address> modelClass, int modelLayout, Query ref) {
        super(activity, modelClass, modelLayout, ref);
        this.mActivity = activity;
        this.mCurrentUser = ((SairaMedicalStoreApplication) mActivity.getApplication()).getCurrentUser();
        this.firebaseCurrentUserAllAddressesRef = new Firebase(Constants.FIREBASE_URL_SAIRA_All_ADDRESSES).child(Utils.encodeEmail(mCurrentUser.getEmail()));
    }

    @Override
    protected void populateView(View view, final Address address) {

        TextView textViewAddressDetails = (TextView) view.findViewById(R.id.text_view_address_details);
        textViewAddressDetails.setText(address.getFullName()+"\n"+address.getAddress()+"\n"+address.getLandmark() +
                "\n"+address.getCity() + " - " + address.getPinCode() + "\n" + address.getState() + ", India \n" + address.getPhoneNumber() );

        LinearLayout linearLayoutSelectableItem = (LinearLayout) view.findViewById(R.id.linear_layout_selectable_item);
        linearLayoutSelectableItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentTemporaryOrder.setOrderDeliveryAddress(address);
                Intent intentToOrderDetails = new Intent(mActivity,OrderDetailsActivity.class);
                intentToOrderDetails.putExtra("currentOrder",mCurrentTemporaryOrder);
                mActivity.startActivity(intentToOrderDetails);
            }
        });

        TextView textViewEdit = (TextView) view.findViewById(R.id.text_view_edit);
        textViewEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentToAddOrUpdateAddress = new Intent(mActivity,AddOrUpdateAddressActivity.class);
                intentToAddOrUpdateAddress.putExtra("addressToBeUpdated",address);
                mActivity.startActivity(intentToAddOrUpdateAddress);
            }
        });

        TextView textViewRemove = (TextView) view.findViewById(R.id.text_view_remove);
        textViewRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseCurrentUserAllAddressesRef.child(address.getAddressId()).setValue(null);
                Toast.makeText(mActivity,"Address removed successfully",Toast.LENGTH_SHORT).show();
            }
        });

    }

}
