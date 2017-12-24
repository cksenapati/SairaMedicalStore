package com.example.android.sairamedicalstore.operations;

import android.app.Activity;
import android.net.Uri;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.android.sairamedicalstore.R;
import com.example.android.sairamedicalstore.SairaMedicalStoreApplication;
import com.example.android.sairamedicalstore.models.Faq;
import com.example.android.sairamedicalstore.models.Poster;
import com.example.android.sairamedicalstore.models.User;
import com.example.android.sairamedicalstore.ui.poster.CreateOrUpdatePoster;
import com.example.android.sairamedicalstore.utils.Constants;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ServerValue;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;



/**
 * Created by chandan on 05-10-2017.
 */

public class FaqOperations {
    Firebase mFirebaseAllFaqs;
    User mCurrentUser;
    Activity mActivity;

    public FaqOperations(Activity mActivity) {
        this.mActivity = mActivity;
        this.mCurrentUser = ((SairaMedicalStoreApplication) mActivity.getApplication()).getCurrentUser();
        this.mFirebaseAllFaqs = new Firebase(Constants.FIREBASE_URL_SAIRA_All_FAQS);
    }

    public void AddNewFaq(final String faqQuestion, int faqPriority, HashMap<String, Object> faqAnswers)
    {

        final String faqId = mFirebaseAllFaqs.push().getKey();

        final Faq newFaq = new Faq(faqId,faqQuestion,faqPriority,faqAnswers);

        mFirebaseAllFaqs.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Boolean isExist = false;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Faq eachFaq = snapshot.getValue(Faq.class);
                    if (eachFaq.getFaqQuestion().equals(faqQuestion)) {
                        Toast.makeText(mActivity, "Same question already exists.", Toast.LENGTH_SHORT).show();
                        isExist = true;
                        break;
                    }
                }
                if(!isExist) {
                    try {
                        mFirebaseAllFaqs.child(faqId).setValue(newFaq);
                        Toast.makeText(mActivity, Constants.UPLOAD_SUCCESSFUL, Toast.LENGTH_SHORT).show();
                         mActivity.finish();
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

    public void UpdateOldPoster(final Faq faqToBeUpdated)
    {

        final Firebase firebaseCurrentFaqRef = mFirebaseAllFaqs.child(faqToBeUpdated.getFaqId());

        mFirebaseAllFaqs.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Faq eachFaq = snapshot.getValue(Faq.class);
                    if (eachFaq.getFaqQuestion().equals(faqToBeUpdated.getFaqQuestion()) &&
                            !eachFaq.getFaqId().equals(faqToBeUpdated.getFaqId())) {

                        Toast.makeText(mActivity, "Same question already exists", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                try {
                    firebaseCurrentFaqRef.setValue(faqToBeUpdated);
                    Toast.makeText(mActivity, Constants.UPDATE_SUCCESSFUL, Toast.LENGTH_SHORT).show();
                    mActivity.finish();

                } catch (Exception ex) {
                    Toast.makeText(mActivity, Constants.UPDATE_FAIL, Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Toast.makeText(mActivity, Constants.UPDATE_FAIL, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void DeleteFaq(final String faqId)
    {
        final Firebase firebaseCurrentFaqRef = mFirebaseAllFaqs.child(faqId);

        mFirebaseAllFaqs.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists())
                {
                    try {
                        firebaseCurrentFaqRef.setValue(null);
                        Toast.makeText(mActivity, "Successfully Deleted", Toast.LENGTH_SHORT).show();
                        mActivity.finish();

                    } catch (Exception ex) {
                        Toast.makeText(mActivity, Constants.UPDATE_FAIL, Toast.LENGTH_SHORT).show();
                    }
                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Toast.makeText(mActivity, Constants.UPDATE_FAIL, Toast.LENGTH_SHORT).show();
            }
        });

    }


}
