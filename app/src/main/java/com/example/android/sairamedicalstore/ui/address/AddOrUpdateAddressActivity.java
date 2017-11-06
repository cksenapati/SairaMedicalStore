package com.example.android.sairamedicalstore.ui.address;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.sairamedicalstore.R;
import com.example.android.sairamedicalstore.models.Address;
import com.example.android.sairamedicalstore.operations.AddressOperations;

public class AddOrUpdateAddressActivity extends AppCompatActivity {

    EditText mEditTextPinCode,mEditTextFullName,mEditTextPhoneNumber,mEditTextAddress,mEditTextLandmark,mEditTextCity;
    Spinner mSpinnerState;
    TextView mTextViewAddOrUpdateAddress,mTextViewToolbar;
    LinearLayout mLinearLayoutAddressInformation;

    Address mAddressToBeUpdated;


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

        mSpinnerState = (Spinner) findViewById(R.id.spinner_state);

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
        mEditTextPhoneNumber.setText(mAddressToBeUpdated.getPhoneNumber());
    }

    public void onAddAddressClick()
    {
        if(isPageValid())
        {
            if(isDeliveryAvailableToPinCode()) {
                AddressOperations obj = new AddressOperations(this);
                if(mAddressToBeUpdated == null)
                    obj.AddNewAddress(mEditTextPinCode.getText().toString(), mEditTextFullName.getText().toString(),
                            mEditTextPhoneNumber.getText().toString(), mEditTextAddress.getText().toString(),
                            mEditTextLandmark.getText().toString(), mEditTextCity.getText().toString(), "Odisha");
                else
                {
                    mAddressToBeUpdated.setPinCode(mEditTextPinCode.getText().toString());
                    mAddressToBeUpdated.setFullName(mEditTextFullName.getText().toString());
                    mAddressToBeUpdated.setAddress(mEditTextAddress.getText().toString());
                    mAddressToBeUpdated.setLandmark(mEditTextLandmark.getText().toString());
                    mAddressToBeUpdated.setCity(mEditTextCity.getText().toString());
                    mAddressToBeUpdated.setPhoneNumber(mEditTextPhoneNumber.getText().toString());
                    obj.UpdateSavedAddress(mAddressToBeUpdated);
                }
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
                view.setBackgroundResource(R.color.tw__composer_red);
            }
            else
                view.setBackgroundResource(R.color.tw__transparent);

        }

        return returnValue;
    }

    public boolean isDeliveryAvailableToPinCode()
    {
        if(mEditTextPinCode.getText().toString().trim().length() <6) {
            Toast.makeText(this, "Invalid PinCode", Toast.LENGTH_SHORT).show();
            mEditTextPinCode.setBackgroundResource(R.color.tw__composer_red);
            return false;
        }
        else {
            //TODO Provide code that checks availability of service in given area
            return true;
        }

    }
}
