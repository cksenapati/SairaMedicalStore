package com.example.android.sairamedicalstore.ui.offer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.sairamedicalstore.R;
import com.example.android.sairamedicalstore.SairaMedicalStoreApplication;
import com.example.android.sairamedicalstore.models.Offer;
import com.example.android.sairamedicalstore.operations.OfferOperations;
import com.example.android.sairamedicalstore.utils.Constants;
import com.example.android.sairamedicalstore.utils.Utils;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class CreateOrUpdateOfferActivity extends AppCompatActivity {

    String mOfferId,mUserType;
    Offer mCurrentOffer;

    LinearLayout mLinearLayoutOfferDetails;
    EditText mEditTextOfferName,mEditTextOfferDescription,mEditTextOfferValue,mEditTextOfferValidForMinPerches,
            mEditTextMaxDiscountableAmount;
    Spinner mSpinnerOfferValueType,mSpinnerActive;
    TextView mTextViewCreateOrUpdate;

    Firebase mFirebaseAllOffersRef,mFirebaseCurrentOfferRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_or_update_offer);

        Intent intent = getIntent();
        if (intent != null) {
            mOfferId = intent.getStringExtra("offerId");
        }

        mUserType = ((SairaMedicalStoreApplication) this.getApplication()).getUserType();

        initializeScreen();

        setSpinners();

        if(mOfferId == null)
            createNewOffer();
        else
            updateOldOffer();

    }

    private void initializeScreen() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mLinearLayoutOfferDetails = (LinearLayout) findViewById(R.id.linear_layout_offer_details);

        mEditTextOfferName = (EditText) findViewById(R.id.edit_text_offer_name);
        mEditTextOfferDescription = (EditText) findViewById(R.id.edit_text_offer_description);
        mEditTextOfferValue = (EditText) findViewById(R.id.edit_text_offer_value);
        mEditTextOfferValidForMinPerches = (EditText) findViewById(R.id.edit_text_offer_valid_for_min_perches);
        mEditTextMaxDiscountableAmount = (EditText) findViewById(R.id.edit_text_offer_maximum_discountable_amount);

        mSpinnerOfferValueType = (Spinner) findViewById(R.id.spinner_offer_value_type);
        mSpinnerActive = (Spinner) findViewById(R.id.spinner_offer_active);

        mTextViewCreateOrUpdate = (TextView) findViewById(R.id.text_view_create_or_update);

        //Firebase
        mFirebaseAllOffersRef = new Firebase(Constants.FIREBASE_URL_SAIRA_All_OFFERS);
    }

    private void setSpinners()
    {
        ArrayAdapter<CharSequence> booleanOptionsAdapter = ArrayAdapter.createFromResource(this,
                R.array.boolean_options, android.R.layout.simple_spinner_item);
        booleanOptionsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerActive.setAdapter(booleanOptionsAdapter);

        ArrayAdapter<CharSequence> offerValueTypeOptionsAdapter = ArrayAdapter.createFromResource(this,
                R.array.offer_value_type_options, android.R.layout.simple_spinner_item);
        offerValueTypeOptionsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerOfferValueType.setAdapter(offerValueTypeOptionsAdapter);

    }

    private void createNewOffer() {
        setTitle("Create New Offer");
        mTextViewCreateOrUpdate.setText("Create");
    }

    private void updateOldOffer() {
        setTitle("Modify Offer");
        mTextViewCreateOrUpdate.setText("Modify");

        mFirebaseCurrentOfferRef = mFirebaseAllOffersRef.child(mOfferId);
        mFirebaseCurrentOfferRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    Offer offer = dataSnapshot.getValue(Offer.class);
                    if(offer != null)
                    {
                        mCurrentOffer = offer;
                        fillActivityFields();
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    private void fillActivityFields()
    {
        mEditTextOfferName.setText(Utils.toLowerCaseExceptFirstLetter(mCurrentOffer.getOfferName()));
        mEditTextOfferDescription.setText(Utils.toLowerCaseExceptFirstLetter(mCurrentOffer.getOfferDescription()));
        mEditTextOfferValidForMinPerches.setText(String.valueOf(mCurrentOffer.getOfferValidForMinimumPerches()));
        mEditTextMaxDiscountableAmount.setText(String.valueOf(mCurrentOffer.getOfferMaximumDiscountValue()));

        if(mCurrentOffer.getOfferDiscountPercentage() > 0) {
            mEditTextOfferValue.setText(String.valueOf(mCurrentOffer.getOfferDiscountPercentage()));

            String offerValueType = "%";
            ArrayAdapter myAdap = (ArrayAdapter) mSpinnerOfferValueType.getAdapter();
            int spinnerPosition = myAdap.getPosition(offerValueType);
            mSpinnerOfferValueType.setSelection(spinnerPosition);
        }
        else if(mCurrentOffer.getOfferDiscountValue() > 0) {
            mEditTextOfferValue.setText(String.valueOf(mCurrentOffer.getOfferDiscountValue()));

            String offerValueType = "RS";
            ArrayAdapter myAdap = (ArrayAdapter) mSpinnerOfferValueType.getAdapter();
            int spinnerPosition = myAdap.getPosition(offerValueType);
            mSpinnerOfferValueType.setSelection(spinnerPosition);
        }


        String isActive;
        if(mCurrentOffer.isActive())
            isActive = "Yes";
        else
            isActive = "No";
        ArrayAdapter myAdap = (ArrayAdapter) mSpinnerOfferValueType.getAdapter();
        int spinnerPosition = myAdap.getPosition(isActive);
        mSpinnerActive.setSelection(spinnerPosition);


    }

    public void onCreateOrUpdateClick(View view)
    {
      if(checkPageValidation())
      {
        if( Double.parseDouble(mEditTextOfferValue.getText().toString()) >0)
        {
            OfferOperations offerOperations = new OfferOperations(this);

            String[] booleanValues = getResources().getStringArray(R.array.boolean_values);
            int selectedActivePosition = mSpinnerActive.getSelectedItemPosition();
            Boolean isActive = Boolean.valueOf(booleanValues[selectedActivePosition]);

            String selectedOfferValueType = mSpinnerOfferValueType.getSelectedItem().toString();
            double offerDiscountPercentage, offerDiscountValue;
            if(selectedOfferValueType.equals("%"))
            {
                offerDiscountPercentage = Double.parseDouble(mEditTextOfferValue.getText().toString());
                offerDiscountValue = 0;
            }
            else
            {
                offerDiscountValue = Double.parseDouble(mEditTextOfferValue.getText().toString());
                offerDiscountPercentage = 0;
            }

            if(mOfferId != null)
            {
                offerOperations.updateOldOffer(mOfferId,mEditTextOfferName.getText().toString().toUpperCase(),
                        mEditTextOfferDescription.getText().toString().toUpperCase(),offerDiscountPercentage,offerDiscountValue,
                        Double.parseDouble(mEditTextMaxDiscountableAmount.getText().toString()),Double.parseDouble(mEditTextOfferValidForMinPerches.getText().toString()),isActive);
            }
            else
            {
                offerOperations.CreateNewOffer(mEditTextOfferName.getText().toString().toUpperCase(),
                        mEditTextOfferDescription.getText().toString().toUpperCase(),offerDiscountPercentage,offerDiscountValue,
                        Double.parseDouble(mEditTextMaxDiscountableAmount.getText().toString()),Double.parseDouble(mEditTextOfferValidForMinPerches.getText().toString()),isActive);

            }
        }
        else
            Toast.makeText(this,"Offer Value field can't be 0",Toast.LENGTH_LONG).show();
      }
      else
          Toast.makeText(this,"Fields can't left empty.",Toast.LENGTH_LONG).show();
    }

    private boolean checkPageValidation()
    {

        for( int i = 0; i < mLinearLayoutOfferDetails.getChildCount(); i++ ){
            View view = mLinearLayoutOfferDetails.getChildAt(i);
            if (view instanceof EditText) {
                if(((EditText)view).getText().toString().trim().length() < 1)
                    return false;
            }
            else if(view instanceof LinearLayout)
            {
                LinearLayout childLinearLayout = (LinearLayout) view;
                for(int j=0;j<childLinearLayout.getChildCount(); j++)
                {
                    View viewInsideView = childLinearLayout.getChildAt(j);
                    if (viewInsideView instanceof EditText) {
                        if(((EditText)viewInsideView).getText().toString().trim().length() < 1)
                            return false;
                    }
                }
            }
        }

        return true;
    }

}
