package com.example.android.sairamedicalstore.operations;

import android.app.Activity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.android.sairamedicalstore.R;
import com.example.android.sairamedicalstore.SairaMedicalStoreApplication;
import com.example.android.sairamedicalstore.models.Offer;
import com.example.android.sairamedicalstore.models.User;
import com.example.android.sairamedicalstore.utils.Constants;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ServerValue;
import com.firebase.client.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.HashMap;

/**
 * Created by chandan on 18-09-2017.
 */

public class OfferOperations {

    Firebase mFirebaseAllOffersRef;
    User mCurrentUser;
    Activity mActivity;

    public OfferOperations(Activity mActivity) {
        this.mFirebaseAllOffersRef = new Firebase(Constants.FIREBASE_URL_SAIRA_All_OFFERS);
        this.mActivity = mActivity;
        this.mCurrentUser = ((SairaMedicalStoreApplication) mActivity.getApplication()).getCurrentUser();

    }

    public void CreateNewOffer(final String offerName, String offerDescription,
                               final double offerDiscountPercentage,final double offerDiscountValue, double offerMaximumDiscountValue,
                               final double offerValidForMinimumPerches, boolean isActive)
    {
        final String offerId,updaterStack,offerValueStack,timestampStack;
        offerId = mFirebaseAllOffersRef.push().getKey();
        updaterStack = mCurrentUser.getEmail();
        if(offerDiscountPercentage>0)
          offerValueStack = String.valueOf(offerDiscountPercentage)+" %";
        else
            offerValueStack = String.valueOf(offerDiscountValue)+" RS";

        HashMap<String, Object> timestampCreated = new HashMap<String, Object>();
        timestampCreated.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);
        HashMap<String, Object> timestampLastUpdate = timestampCreated;
        timestampStack  = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date());

        final Offer currentOffer = new Offer( offerId, offerName,  offerDescription,  updaterStack,  offerValueStack, timestampStack,
         offerDiscountPercentage,  offerDiscountValue,  offerMaximumDiscountValue,
         offerValidForMinimumPerches, timestampCreated, timestampLastUpdate,  isActive);

        mFirebaseAllOffersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Boolean isExist = false;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Offer eachOffer = snapshot.getValue(Offer.class);
                    if (eachOffer.getOfferName().equals(offerName) &&
                            eachOffer.getOfferDiscountValue() == offerDiscountValue &&
                            eachOffer.getOfferDiscountPercentage() == offerDiscountPercentage && eachOffer.getOfferValidForMinimumPerches() == offerValidForMinimumPerches)
                             {
                        Toast.makeText(mActivity, Constants.ITEM_ALREADY_EXISTS, Toast.LENGTH_SHORT).show();
                        isExist = true;
                        break;
                    }
                }
                if(!isExist) {
                    try {
                        mFirebaseAllOffersRef.child(offerId).setValue(currentOffer);
                        resetAllControlsFromCreateOrUpdateOfferPage();
                        Toast.makeText(mActivity, Constants.UPLOAD_SUCCESSFUL, Toast.LENGTH_SHORT).show();

                    } catch (Exception ex) {
                        Toast.makeText(mActivity, Constants.UPLOAD_FAIL, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Toast.makeText(mActivity, Constants.UPLOAD_FAIL, Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void updateOldOffer(String offerId,final String offerName, String offerDescription,
                               final double offerDiscountPercentage,final double offerDiscountValue, double offerMaximumDiscountValue,
                               final double offerValidForMinimumPerches, boolean isActive)
    {
        final String updaterStack,offerValueStack,timestampStack;
        updaterStack = mCurrentUser.getEmail();
        if(offerDiscountPercentage>0)
            offerValueStack = String.valueOf(offerDiscountPercentage)+" %";
        else
            offerValueStack = String.valueOf(offerDiscountValue)+" RS";

        HashMap<String, Object> timestampCreated = new HashMap<String, Object>();
        timestampCreated.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);
        HashMap<String, Object> timestampLastUpdate = timestampCreated;
        timestampStack  = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date());

        final Offer createdOffer = new Offer( offerId, offerName,  offerDescription,  updaterStack,  offerValueStack, timestampStack,
                offerDiscountPercentage,  offerDiscountValue,  offerMaximumDiscountValue,
                offerValidForMinimumPerches, timestampCreated, timestampLastUpdate,  isActive);


        final Firebase currentOfferRef = mFirebaseAllOffersRef.child(offerId);
        currentOfferRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    Offer currentOffer = dataSnapshot.getValue(Offer.class);
                    if(currentOffer != null)
                    {
                        createdOffer.setUpdaterStack(currentOffer.getUpdaterStack() + "," +mCurrentUser.getEmail());
                        if(offerDiscountPercentage>0)
                            createdOffer.setOfferValueStack(currentOffer.getOfferValueStack() + "," +String.valueOf(offerDiscountPercentage)+" %");
                        else
                            createdOffer.setOfferValueStack(currentOffer.getOfferValueStack() + "," +String.valueOf(offerDiscountValue)+" RS");

                        createdOffer.setTimestampStack(currentOffer.getTimestampStack() + "," +timestampStack);
                        createdOffer.setTimestampCreated(currentOffer.getTimestampCreated());

                        try {
                            currentOfferRef.setValue(createdOffer);
                            Toast.makeText(mActivity, "Successfully Updated", Toast.LENGTH_LONG).show();
                        }catch (Exception ex){
                            Toast.makeText(mActivity, "Update failed...try again", Toast.LENGTH_LONG).show();
                        }
                    }
                    else
                        Toast.makeText(mActivity, "Update failed...try again", Toast.LENGTH_LONG).show();


                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Toast.makeText(mActivity, "Update failed...try again", Toast.LENGTH_LONG).show();
            }
        });


    }

    private void resetAllControlsFromCreateOrUpdateOfferPage()
    {
        LinearLayout linearLayout = (LinearLayout) mActivity.findViewById(R.id.linear_layout_offer_details);

        for( int i = 0; i < linearLayout.getChildCount(); i++ ){
            View view = linearLayout.getChildAt(i);
            if (view instanceof EditText) {
                ((EditText)view).setText("");
            }
            else if(view instanceof LinearLayout)
            {
                LinearLayout childLinearLayout = (LinearLayout) view;
                for(int j=0;j<childLinearLayout.getChildCount(); j++)
                {
                    View viewInsideView = childLinearLayout.getChildAt(j);
                    if (viewInsideView instanceof EditText) {
                        ((EditText)viewInsideView).setText("");
                    }
                }
            }
        }
    }
}
