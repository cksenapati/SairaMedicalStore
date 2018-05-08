package com.example.android.sairamedicalstore.ui.address;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.sairamedicalstore.R;
import com.example.android.sairamedicalstore.models.Address;
import com.example.android.sairamedicalstore.models.DeliverableAddress;
import com.example.android.sairamedicalstore.operations.AddressOperations;
import com.example.android.sairamedicalstore.ui.deliverableAddress.SearchedDeliverableAddressesAdapter;
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

public class AddOrUpdateAddressActivity extends AppCompatActivity {

    EditText mEditTextPinCode,mEditTextFullName,mEditTextPhoneNumber,mEditTextAddress,mEditTextLandmark,mEditTextCity,mEditTextState;
    //Spinner mSpinnerState;
    TextView mTextViewAddOrUpdateAddress,mTextViewToolbar;
    LinearLayout mLinearLayoutAddressInformation;

    Address mAddressToBeUpdated;

    String retrievedState,retrievedCity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_or_update_address);

        Intent intent = getIntent();
        if (intent != null) {
            mAddressToBeUpdated = (Address) intent.getSerializableExtra("addressToBeUpdated");
        }

        initialization();

        if(mAddressToBeUpdated != null) {
            displayOldAddressDetails();
            mTextViewToolbar.setText("Update Address");
            mTextViewAddOrUpdateAddress.setText("Update Address");
        }

        mTextViewAddOrUpdateAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAddAddressClick();
            }
        });

        mEditTextPinCode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus)
                {
                    mEditTextState.setText("");
                    retrievedState = "";
                    retrievedCity = "";

                    if (isPinValid()) {
                        String openPostalPinCodeUrl = Constants.POSTAL_DETAILS_BY_PIN_NUMBER_BASE_URL + mEditTextPinCode.getText();
                        getPinCodeDetailsAsyncTask task = new getPinCodeDetailsAsyncTask();
                        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, openPostalPinCodeUrl);
                    }
                }
            }
        });
    }

    public void initialization()
    {
        mLinearLayoutAddressInformation = (LinearLayout) findViewById(R.id.linear_layout_address_information);

        mEditTextPinCode = (EditText) findViewById(R.id.edit_text_pincode);
        mEditTextFullName = (EditText) findViewById(R.id.edit_text_full_name);
        mEditTextPhoneNumber = (EditText) findViewById(R.id.edit_text_phone_number);
        mEditTextAddress = (EditText) findViewById(R.id.edit_text_address);
        mEditTextLandmark = (EditText) findViewById(R.id.edit_text_landmark);
        mEditTextCity = (EditText) findViewById(R.id.edit_text_city);
        mEditTextState = (EditText) findViewById(R.id.edit_text_state);

        //mSpinnerState = (Spinner) findViewById(R.id.spinner_state);

        mTextViewAddOrUpdateAddress = (TextView) findViewById(R.id.text_view_add_or_update_address);
        mTextViewToolbar = (TextView) findViewById(R.id.text_view_toolbar);

    }

    public void displayOldAddressDetails()
    {
        mEditTextPinCode.setText(mAddressToBeUpdated.getPinCode());
        mEditTextFullName.setText(mAddressToBeUpdated.getFullName());
        mEditTextAddress.setText(mAddressToBeUpdated.getAddress());
        mEditTextLandmark.setText(mAddressToBeUpdated.getLandmark());
        mEditTextCity.setText(mAddressToBeUpdated.getCity());
        mEditTextState.setText(mAddressToBeUpdated.getState());
        mEditTextPhoneNumber.setText(mAddressToBeUpdated.getPhoneNumber());
    }

    public void onAddAddressClick()
    {
        if(isPageValid())
        {
                AddressOperations obj = new AddressOperations(this);
                if(mAddressToBeUpdated == null)
                    obj.AddNewAddress(mEditTextPinCode.getText().toString(), mEditTextFullName.getText().toString(),
                            mEditTextPhoneNumber.getText().toString(), mEditTextAddress.getText().toString(),
                            mEditTextLandmark.getText().toString(), mEditTextCity.getText().toString(), mEditTextState.getText().toString());
                else
                {
                    mAddressToBeUpdated.setPinCode(mEditTextPinCode.getText().toString());
                    mAddressToBeUpdated.setFullName(mEditTextFullName.getText().toString());
                    mAddressToBeUpdated.setAddress(mEditTextAddress.getText().toString());
                    mAddressToBeUpdated.setLandmark(mEditTextLandmark.getText().toString());
                    mAddressToBeUpdated.setCity(mEditTextCity.getText().toString());
                    mAddressToBeUpdated.setState(mEditTextState.getText().toString());

                    mAddressToBeUpdated.setPhoneNumber(mEditTextPhoneNumber.getText().toString());
                    obj.UpdateSavedAddress(mAddressToBeUpdated);
                }

        }
        else
            Toast.makeText(this,"Fields Can't left blank",Toast.LENGTH_SHORT).show();
    }

    public boolean isPageValid()
    {
        boolean returnValue = true;

        for( int i = 0; i < mLinearLayoutAddressInformation.getChildCount(); i++ ){
            View view = mLinearLayoutAddressInformation.getChildAt(i);
            if (view.getVisibility()==View.VISIBLE && view instanceof EditText && ((EditText)view).getText().toString().trim().length() < 1) {
                returnValue = false;
                view.setBackgroundResource(R.color.fail_message_background_color);
            }
            else
                view.setBackgroundResource(R.color.tw__transparent);

        }

        return returnValue;
    }

    public boolean isPinValid()
    {
        if(mEditTextPinCode.getText().toString().trim().length() != 6) {
            Toast.makeText(this, "Invalid PinCode", Toast.LENGTH_SHORT).show();
            mEditTextPinCode.setBackgroundResource(R.color.tw__composer_red);
            return false;
        }
        else {
            return true;
        }

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
                updateStateAndCity();
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
            String status = rootJsonObject.getString("Status");

            if (status.equals("Success")) {
                JSONArray arrayOfPostOffices = rootJsonObject.getJSONArray("PostOffice");
                JSONObject singlePostOfficeJsonObject = arrayOfPostOffices.getJSONObject(0);

                try {
                    retrievedCity = singlePostOfficeJsonObject.getString("District");
                    retrievedState = singlePostOfficeJsonObject.getString("State");
                }catch (Exception ex){}
            }

        }
        catch (Exception e)
        {
        }
    }

    private void updateStateAndCity()
    {
        if (retrievedCity != "")
            mEditTextCity.setText(retrievedCity);

        if (retrievedState != "")
            mEditTextState.setText(retrievedState);
        else
            Toast.makeText(this,"Invalid Pin",Toast.LENGTH_SHORT).show();

    }

}
