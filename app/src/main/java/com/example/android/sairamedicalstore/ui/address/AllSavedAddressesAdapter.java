package com.example.android.sairamedicalstore.ui.address;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.sairamedicalstore.R;
import com.example.android.sairamedicalstore.SairaMedicalStoreApplication;
import com.example.android.sairamedicalstore.models.Address;
import com.example.android.sairamedicalstore.models.DeliverableAddress;
import com.example.android.sairamedicalstore.models.Manufacturer;
import com.example.android.sairamedicalstore.models.User;
import com.example.android.sairamedicalstore.ui.order.OrderDetailsActivity;
import com.example.android.sairamedicalstore.utils.Constants;
import com.example.android.sairamedicalstore.utils.Utils;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.firebase.ui.FirebaseListAdapter;

import org.w3c.dom.Text;

import java.util.HashMap;

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
                onAddressItemClick(address);
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

    public void onAddressItemClick(final Address address)
    {

            Firebase firebaseAllDeliverableAddressesRef = new Firebase(Constants.FIREBASE_URL_SAIRA_All_DELIVERABLE_ADDRESS);
            firebaseAllDeliverableAddressesRef.child(address.getPinCode()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists())
                    {
                        DeliverableAddress DeliverableAddress = dataSnapshot.getValue(DeliverableAddress.class);
                        if (DeliverableAddress != null)
                        {
                            if ( DeliverableAddress.getDeliveryCharge() == 0.0)
                                Toast.makeText(mActivity,"Ships for FREE",Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(mActivity,"Ships at Rs " + DeliverableAddress.getDeliveryCharge(),Toast.LENGTH_SHORT).show();

                            HashMap<String,Double> mHashMapTempOrderPricingDetails = new HashMap<String, Double>();
                            mHashMapTempOrderPricingDetails = mCurrentTemporaryOrder.getOrderPricingDetails();
                            mHashMapTempOrderPricingDetails.put("Shipping Charges",DeliverableAddress.getDeliveryCharge());

                            mCurrentTemporaryOrder.setIndividualProductPricing(mHashMapTempOrderPricingDetails);

                            mCurrentTemporaryOrder.setOrderDeliveryAddress(address);
                            Intent intentToOrderDetails = new Intent(mActivity,OrderDetailsActivity.class);
                            intentToOrderDetails.putExtra("currentOrder",mCurrentTemporaryOrder);
                            mActivity.startActivity(intentToOrderDetails);
                        }
                    }
                    else
                        Toast.makeText(mActivity,"Not available for delivery at " + address.getPinCode(),Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    Toast.makeText(mActivity,"Something went wrong",Toast.LENGTH_SHORT).show();
                }
            });

    }

}
