package com.example.android.sairamedicalstore.ui.address;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.sairamedicalstore.R;
import com.example.android.sairamedicalstore.SairaMedicalStoreApplication;
import com.example.android.sairamedicalstore.models.Address;
import com.example.android.sairamedicalstore.models.Medicine;
import com.example.android.sairamedicalstore.models.User;
import com.example.android.sairamedicalstore.ui.ProductDetailsActivity;
import com.example.android.sairamedicalstore.ui.search.SearchActivity;
import com.example.android.sairamedicalstore.ui.search.SearchedMedicinesAdapter;
import com.example.android.sairamedicalstore.utils.Constants;
import com.example.android.sairamedicalstore.utils.Utils;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class DeliveryAddressActivity extends AppCompatActivity {

    Firebase mFirebaseCurrentUserAllSavedAddressRef;

    TextView mTextViewAddNewAddress,mTextViewNoSavedAddress;
    ListView mListViewSavedDeliveryAddresses;

    User mCurrentUser;
    private AllSavedAddressesAdapter mAllSavedAddressesAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_address);

        mCurrentUser = ((SairaMedicalStoreApplication) this.getApplication()).getCurrentUser();

        initialization();

        mFirebaseCurrentUserAllSavedAddressRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    mListViewSavedDeliveryAddresses.setVisibility(View.VISIBLE);
                    mTextViewNoSavedAddress.setVisibility(View.GONE);

                    getAllAddresses();
                }
                else
                {
                    mListViewSavedDeliveryAddresses.setVisibility(View.GONE);
                    mTextViewNoSavedAddress.setVisibility(View.VISIBLE);

                    if (mAllSavedAddressesAdapter != null)
                        mAllSavedAddressesAdapter.cleanup();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        mTextViewAddNewAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentToAddNewAddress = new Intent(DeliveryAddressActivity.this,AddNewAddressActivity.class);
                startActivity(intentToAddNewAddress);
            }
        });

    }

    public void initialization()
    {
        mTextViewAddNewAddress = (TextView) findViewById(R.id.text_view_add_new_address);
        mTextViewNoSavedAddress = (TextView) findViewById(R.id.text_view_no_saved_address);

        mListViewSavedDeliveryAddresses = (ListView) findViewById(R.id.list_view_saved_delivery_addresses);

        mFirebaseCurrentUserAllSavedAddressRef = new Firebase(Constants.FIREBASE_URL_SAIRA_All_ADDRESSES).child(Utils.encodeEmail(mCurrentUser.getEmail()));
    }

    public void getAllAddresses()
    {
        if (mAllSavedAddressesAdapter != null)
            mAllSavedAddressesAdapter.cleanup();

        mAllSavedAddressesAdapter = new AllSavedAddressesAdapter(DeliveryAddressActivity.this, Address.class,
                R.layout.item_single_address, mFirebaseCurrentUserAllSavedAddressRef.orderByChild(Constants.FIREBASE_PROPERTY_TIMESTAMP_LAST_UPDATE + "/" + Constants.FIREBASE_PROPERTY_TIMESTAMP));

        mListViewSavedDeliveryAddresses.setAdapter(mAllSavedAddressesAdapter);

        /*mListViewSavedDeliveryAddresses.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Medicine medicine = mAllSavedAddressesAdapter.getItem(position);
                Intent intentProductDetails = new Intent(SearchActivity.this, ProductDetailsActivity.class);
                intentProductDetails.putExtra("medicineId", medicine.getMedicineId());
                startActivity(intentProductDetails);
            }
        });*/
    }
}
