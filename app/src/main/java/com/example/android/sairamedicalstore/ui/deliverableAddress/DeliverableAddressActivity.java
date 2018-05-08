package com.example.android.sairamedicalstore.ui.deliverableAddress;

import android.app.Dialog;
import android.content.Intent;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.android.sairamedicalstore.R;
import com.example.android.sairamedicalstore.models.DeliverableAddress;
import com.example.android.sairamedicalstore.operations.DeliverableAddressOperations;
import com.example.android.sairamedicalstore.ui.search.SearchActivity;
import com.example.android.sairamedicalstore.utils.Constants;
import com.example.android.sairamedicalstore.utils.Utils;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import static android.R.attr.dialogTitle;

public class DeliverableAddressActivity extends AppCompatActivity {

    Spinner  mSpinnerSearchBy;
    EditText mEditTextSearch;
    TextView mTextViewSearch;
    ListView mListViewSearchResults;
    ImageView mImageViewGoBack;

    String mActivePinCode;
    ArrayList<DeliverableAddress> mArrayListDeliverableAddresses;
    SearchedDeliverableAddressesAdapter mSearchedDeliverableAddressesAdapter;
    String searchBy;//search by "pinCode" or "address"

    Firebase mFirebaseAllDeliverableAddressesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deliverable_address);

        Intent intent = getIntent();
        if (intent != null) {
            mActivePinCode = intent.getStringExtra("activePinCode");
        }

        initialization();

        ArrayAdapter<CharSequence> searchByAdapter;
        searchByAdapter = ArrayAdapter.createFromResource(this,
                R.array.pin_code_search_by_options, android.R.layout.simple_spinner_item);
        searchByAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerSearchBy.setAdapter(searchByAdapter);
        mSpinnerSearchBy.setSelection(0);

        if (mActivePinCode != null)
        {
            mEditTextSearch.setText(mActivePinCode);
            String openPostalPinCodeUrl = Constants.POSTAL_DETAILS_BY_PIN_NUMBER_BASE_URL + mActivePinCode;
            getPinCodeDetailsAsyncTask task = new getPinCodeDetailsAsyncTask();
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,openPostalPinCodeUrl);
        }


        mTextViewSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mArrayListDeliverableAddresses.clear();
                mListViewSearchResults.setAdapter(null);

                if (mSpinnerSearchBy.getSelectedItemPosition() == 0)
                {
                    if (mEditTextSearch.getText().toString().length() == 6 && !mEditTextSearch.getText().toString().contains(" "))
                    {
                        mActivePinCode = mEditTextSearch.getText().toString();
                        String openPostalPinCodeUrl = Constants.POSTAL_DETAILS_BY_PIN_NUMBER_BASE_URL + mActivePinCode;
                        getPinCodeDetailsAsyncTask task = new getPinCodeDetailsAsyncTask();
                        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,openPostalPinCodeUrl);
                    }
                    else {
                        mActivePinCode = null;
                        Toast.makeText(DeliverableAddressActivity.this, "Invalid Pin", Toast.LENGTH_SHORT).show();
                    }
                }
                else if (mEditTextSearch.getText().toString().trim().length() != 0)
                {
                    mActivePinCode = null;
                    String searchItem =  mEditTextSearch.getText().toString().replaceAll(" ","%20");
                    String openPostalPinCodeUrl = Constants.POSTAL_DETAILS_BY_PLACE_NAME_BASE_URL + searchItem;
                    getPinCodeDetailsAsyncTask task = new getPinCodeDetailsAsyncTask();
                    task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,openPostalPinCodeUrl);
                }
            }
        });

        mSpinnerSearchBy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mSpinnerSearchBy.getSelectedItemPosition() == 0)
                    mEditTextSearch.setInputType(InputType.TYPE_CLASS_NUMBER);
                else
                    mEditTextSearch.setInputType(InputType.TYPE_CLASS_TEXT);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mImageViewGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mListViewSearchResults.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                DeliverableAddress deliverableAddress = mSearchedDeliverableAddressesAdapter.getItem(position);
                AddOrRemoveDeliverableDialog(deliverableAddress);
                return false;
            }
        });
    }

    private void initialization()
    {
        mSpinnerSearchBy = (Spinner) findViewById(R.id.spinner_search_by);
        mEditTextSearch = (EditText) findViewById(R.id.edit_text_search);

        mTextViewSearch = (TextView) findViewById(R.id.text_view_search);
        mListViewSearchResults = (ListView)  findViewById(R.id.list_view_search_result);

        mImageViewGoBack = (ImageView)  findViewById(R.id.image_view_go_back);

        mArrayListDeliverableAddresses = new ArrayList<>();

        mFirebaseAllDeliverableAddressesRef = new Firebase(Constants.FIREBASE_URL_SAIRA_All_DELIVERABLE_ADDRESS);
    }

    private class getPinCodeDetailsAsyncTask extends AsyncTask<String,Void,String>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... urls) {
            if(urls.length <1 || urls[0] == null)
                return null ;

            try {
                getJsonResponse(urls[0]);
                return "success";
            }
            catch (Exception ex)
            {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String string)
        {
            if(string == null)
                return;
            else
                displayAllPostOffices();
        }
    }

    public void getJsonResponse(String openTDbBaseUrl)
    {
        URL url = null;
        try {
            url = new URL(openTDbBaseUrl);
        } catch (MalformedURLException e) {

        }

        String jsonResponse = null;
        try {
            jsonResponse = Utils.makeHttpRequest(url);
        }
        catch (IOException e) {
        }

        try {
            extractFeatureFromJson(jsonResponse);
        }
        catch (Exception ex)
        {
        }
    }

    private void extractFeatureFromJson(String jsonObjectURL)
    {
        if (TextUtils.isEmpty(jsonObjectURL)) {
            return ;
        }

        try
        {
            JSONObject rootJsonObject = new JSONObject(jsonObjectURL);
            String message = rootJsonObject.getString("Message");
            String status = rootJsonObject.getString("Status");

            if (status.equals("Success")) {
                JSONArray arrayOfPostOffices = rootJsonObject.getJSONArray("PostOffice");
                for (int counter = 0; counter < arrayOfPostOffices.length(); counter++) {
                    JSONObject singlePostOfficeJsonObject = arrayOfPostOffices.getJSONObject(counter);
                    String name = singlePostOfficeJsonObject.getString("Name");
                    String district = singlePostOfficeJsonObject.getString("District");
                    String state = singlePostOfficeJsonObject.getString("State");
                    String country = singlePostOfficeJsonObject.getString("Country");
                    String pinCode = null;
                    try {
                        pinCode =  singlePostOfficeJsonObject.getString("PINCode");
                    }catch (Exception ex){
                        pinCode = mActivePinCode;
                    }

                    DeliverableAddress address = new DeliverableAddress(pinCode,name.toUpperCase(),district.toUpperCase(),state.toUpperCase(),country.toUpperCase(),0.0);
                    mArrayListDeliverableAddresses.add(address);
                }
            }

        }
        catch (Exception e)
        {
        }
    }

    private void displayAllPostOffices()
    {
        if (mArrayListDeliverableAddresses.size() > 0)
        {
            mSearchedDeliverableAddressesAdapter = new SearchedDeliverableAddressesAdapter(this,mArrayListDeliverableAddresses);
            mListViewSearchResults.setAdapter(mSearchedDeliverableAddressesAdapter);

            mListViewSearchResults.setVisibility(View.VISIBLE);
        }
        else {
            Toast.makeText(this,"No Record Found",Toast.LENGTH_SHORT).show();
        }


    }


    //Dialog

    public void AddOrRemoveDeliverableDialog(final DeliverableAddress deliverableAddress) {

        final Dialog addOrRemoveDeliverable = new Dialog(this,android.R.style.Theme_Light_NoTitleBar_Fullscreen);
        addOrRemoveDeliverable.setContentView(R.layout.dialog_add_or_remove_deliverable);

        final EditText editTextDeliveryCharge = (EditText) addOrRemoveDeliverable.findViewById(R.id.edit_text_delivery_charge);
        final TextView textViewAddOrUpdateDeliverable = (TextView) addOrRemoveDeliverable.findViewById(R.id.text_view_add_or_update_deliverable);
        final TextView textViewAddress = (TextView) addOrRemoveDeliverable.findViewById(R.id.text_view_address);
        final TextView textViewRemoveDeliverable = (TextView) addOrRemoveDeliverable.findViewById(R.id.text_view_remove_from_deliverable);


            String addressString = "";

            if (deliverableAddress.getPinCode() != null)
                addressString = addressString + deliverableAddress.getPinCode() + ",";
            if (deliverableAddress.getName() != null)
                addressString = addressString + deliverableAddress.getName() + ",";
            if (deliverableAddress.getDistrict() != null)
                addressString = addressString + deliverableAddress.getDistrict() + ",";
            if (deliverableAddress.getState() != null)
                addressString = addressString + deliverableAddress.getState() + ",";
            if (deliverableAddress.getCountry() != null)
                addressString = addressString + deliverableAddress.getCountry();

            textViewAddress.setText(addressString);

        mFirebaseAllDeliverableAddressesRef.child(deliverableAddress.getPinCode()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    DeliverableAddress address = dataSnapshot.getValue(DeliverableAddress.class);
                    if (address != null)
                    {
                        editTextDeliveryCharge.setText(Double.toString(address.getDeliveryCharge()));

                        textViewAddOrUpdateDeliverable.setText("Update Deliverable");

                        textViewRemoveDeliverable.setVisibility(View.VISIBLE);
                    }else {

                        textViewAddOrUpdateDeliverable.setText("Add Deliverable");
                        textViewRemoveDeliverable.setVisibility(View.VISIBLE);

                        textViewRemoveDeliverable.setVisibility(View.GONE);
                    }
                }else {
                    textViewAddOrUpdateDeliverable.setText("Add Deliverable");
                    textViewRemoveDeliverable.setVisibility(View.VISIBLE);

                    textViewRemoveDeliverable.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });


        addOrRemoveDeliverable.show();

        final DeliverableAddressOperations obj = new DeliverableAddressOperations(this);

        textViewAddOrUpdateDeliverable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextDeliveryCharge.getText() != null && editTextDeliveryCharge.getText().length() > 0)
                    deliverableAddress.setDeliveryCharge(Double.parseDouble(editTextDeliveryCharge.getText().toString()));

                obj.AddOrUpdateDeliverableAddress(deliverableAddress);
                addOrRemoveDeliverable.dismiss();
            }
        });

        textViewRemoveDeliverable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                obj.RemoveDeliverableAddress(deliverableAddress);
                addOrRemoveDeliverable.dismiss();
            }
        });


    }


}
