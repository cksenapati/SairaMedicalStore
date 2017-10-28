package com.example.android.sairamedicalstore.ui.address;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.util.Util;
import com.example.android.sairamedicalstore.R;
import com.example.android.sairamedicalstore.SairaMedicalStoreApplication;
import com.example.android.sairamedicalstore.models.User;
import com.example.android.sairamedicalstore.operations.AddressOperations;
import com.example.android.sairamedicalstore.utils.Constants;
import com.example.android.sairamedicalstore.utils.Utils;
import com.firebase.client.Firebase;

public class AddNewAddressActivity extends AppCompatActivity {

    EditText mEditTextPinCode,mEditTextFullName,mEditTextPhoneNumber,mEditTextAddress,mEditTextLandmark,mEditTextCity;
    Spinner mSpinnerState;
    TextView mTextViewAddAddress;
    LinearLayout mLinearLayoutAddressInformation;

    User mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_address);

        initialization();

        mTextViewAddAddress.setOnClickListener(new View.OnClickListener() {
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

        mTextViewAddAddress = (TextView) findViewById(R.id.text_view_add_address);

    }

    public void onAddAddressClick()
    {
        if(isPageValid())
        {
            if(isDeliveryAvailableToPinCode()) {
                AddressOperations obj = new AddressOperations(this);
                obj.AddNewAddress(mEditTextPinCode.getText().toString(), mEditTextFullName.getText().toString(),
                        mEditTextPhoneNumber.getText().toString(), mEditTextAddress.getText().toString(),
                        mEditTextLandmark.getText().toString(), mEditTextCity.getText().toString(), "Odisha");
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
