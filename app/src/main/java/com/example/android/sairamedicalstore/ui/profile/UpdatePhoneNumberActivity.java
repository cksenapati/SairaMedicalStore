package com.example.android.sairamedicalstore.ui.profile;

import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.sairamedicalstore.R;
import com.example.android.sairamedicalstore.SairaMedicalStoreApplication;
import com.example.android.sairamedicalstore.models.User;
import com.example.android.sairamedicalstore.utils.Constants;
import com.example.android.sairamedicalstore.utils.Utils;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.msg91.sendotp.library.SendOtpVerification;
import com.msg91.sendotp.library.Verification;
import com.msg91.sendotp.library.VerificationListener;

import android.telephony.gsm.SmsManager;

import static android.R.attr.data;
import static android.R.attr.phoneNumber;
import static android.R.id.message;

public class UpdatePhoneNumberActivity extends AppCompatActivity implements VerificationListener {

    Firebase mFirebaseCurrentUserRef;

    LinearLayout mLinearLayoutNewPhoneNumber,mLinearLayoutOtp;
    TextView mTextViewOldPhoneNumber,mTextViewOtpVerify,mTextViewNext,mTextViewCancel;
    EditText mEditTextNewPhoneNumber,mEditTextOtp;
    Spinner mSpinnerCountryCodes;

    User mCurrentUser;
    int time;
    Verification mVerification;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_phone_number);

        mCurrentUser = ((SairaMedicalStoreApplication) this.getApplication()).getCurrentUser();

        initializeScreen();

        mTextViewNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mEditTextNewPhoneNumber.getText().toString().trim().length() == 10)
                    checkAndSendOtp();
                else
                    Toast.makeText(UpdatePhoneNumberActivity.this,"Incorrect Phone Number",Toast.LENGTH_SHORT).show();
            }
        });

        mTextViewCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditTextNewPhoneNumber.setText(null);
                mEditTextNewPhoneNumber.setEnabled(true);
                mEditTextOtp.setText(null);
                mLinearLayoutOtp.setVisibility(View.GONE);
                mTextViewNext.setText("Send Otp");


            }
        });

        mFirebaseCurrentUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    mCurrentUser = dataSnapshot.getValue(User.class);
                    if(mCurrentUser == null)
                        finish();

                    ((SairaMedicalStoreApplication) UpdatePhoneNumberActivity.this.getApplication()).setCurrentUser(mCurrentUser);

                    showCurrentPhoneNumber();
                    loadSpinner();

                    mEditTextOtp.setText(null);
                    mEditTextNewPhoneNumber.setText(null);
                    mLinearLayoutOtp.setVisibility(View.GONE);
                    mTextViewNext.setText("Send Otp");
                    mEditTextNewPhoneNumber.setEnabled(true);

                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        mTextViewOtpVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mVerification.verify(mEditTextOtp.getText().toString());
            }
        });


    }

    private void initializeScreen() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        /* Common toolbar setup */
        setSupportActionBar(toolbar);
        setTitle("Update Phone Number");

        /* Add back button to the action bar */
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mLinearLayoutNewPhoneNumber = (LinearLayout) findViewById(R.id.linear_layout_new_phone_number);
        mLinearLayoutOtp = (LinearLayout) findViewById(R.id.linear_layout_otp);

        mTextViewOldPhoneNumber = (TextView) findViewById(R.id.text_view_old_phone_number);
        mTextViewOtpVerify = (TextView) findViewById(R.id.text_view_otp_verify);
        mTextViewNext = (TextView) findViewById(R.id.text_view_next);
        mTextViewCancel = (TextView) findViewById(R.id.text_view_cancel);

        mEditTextNewPhoneNumber = (EditText) findViewById(R.id.edit_text_phone_number);
        mEditTextOtp = (EditText) findViewById(R.id.edit_text_otp);

        mSpinnerCountryCodes = (Spinner) findViewById(R.id.spinner_country_codes);

        mFirebaseCurrentUserRef = new Firebase(Constants.FIREBASE_URL_SAIRA_ALL_USERS).child(Utils.encodeEmail(mCurrentUser.getEmail()));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                this.finish();
                return true;

            default: return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onInitiated(String response) {
        Toast.makeText(this,"OTP sent successfully",Toast.LENGTH_SHORT).show();

        mEditTextNewPhoneNumber.setEnabled(false);
        mLinearLayoutOtp.setVisibility(View.VISIBLE);
        mTextViewNext.setText("Resend Otp");
    }

    @Override
    public void onInitiationFailed(Exception paramException) {
        Toast.makeText(this,"OTP sending failed. Try again.",Toast.LENGTH_SHORT).show();
        mTextViewNext.setText("Resend Otp");
    }

    @Override
    public void onVerified(String response) {
        Toast.makeText(this,"Phone number updated Successfully.",Toast.LENGTH_SHORT).show();

        mCurrentUser.setPhoneNo(mEditTextNewPhoneNumber.getText().toString().trim());
        mFirebaseCurrentUserRef.setValue(mCurrentUser);
    }

    @Override
    public void onVerificationFailed(Exception paramException) {
        Toast.makeText(this,"Verification failed.",Toast.LENGTH_SHORT).show();
    }

    public void showCurrentPhoneNumber()
    {
        if (mCurrentUser.getPhoneNo() != null) {
            mTextViewOldPhoneNumber.setText("Old No : " + mCurrentUser.getPhoneNo());
            mTextViewOldPhoneNumber.setVisibility(View.VISIBLE);
        }
        else
            mTextViewOldPhoneNumber.setVisibility(View.GONE);
    }

    public void loadSpinner()
    {
        ArrayAdapter<CharSequence> reasonAdapter;
        reasonAdapter = ArrayAdapter.createFromResource(this,
                    R.array.country_code_options, android.R.layout.simple_spinner_item);

        reasonAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerCountryCodes.setAdapter(reasonAdapter);
        mSpinnerCountryCodes.setSelection(0);
    }

    private void checkAndSendOtp()
    {
        Firebase firebaseAllUsersRef = new Firebase(Constants.FIREBASE_URL_SAIRA_ALL_USERS);
        firebaseAllUsersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists())
                    return;

                for (DataSnapshot snapshot:dataSnapshot.getChildren()) {
                    User eachUser = snapshot.getValue(User.class);
                    if (eachUser == null)
                        continue;

                    if (mEditTextNewPhoneNumber.getText().toString().trim().equals(eachUser.getPhoneNo())) {
                        if (eachUser.getEmail().equals(mCurrentUser.getEmail()))
                            Toast.makeText(UpdatePhoneNumberActivity.this, "Number already verified", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(UpdatePhoneNumberActivity.this, "Number being used by other person.", Toast.LENGTH_SHORT).show();

                        return;
                    }
                }

                if (mTextViewNext.getText().equals("Send Otp"))
                {
                    mVerification = SendOtpVerification.createSmsVerification
                            (SendOtpVerification
                                    .config(mSpinnerCountryCodes.getSelectedItem().toString() + mEditTextNewPhoneNumber.getText().toString().trim())
                                    .context(UpdatePhoneNumberActivity.this)
                                    .autoVerification(true)
                                    .build(), UpdatePhoneNumberActivity.this);

                    mVerification.initiate();
                }
                else if(mTextViewNext.getText().equals("Resend Otp"))
                    mVerification.resend("text");

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }


}
