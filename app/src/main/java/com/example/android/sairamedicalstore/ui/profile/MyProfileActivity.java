package com.example.android.sairamedicalstore.ui.profile;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.util.Util;
import com.example.android.sairamedicalstore.R;
import com.example.android.sairamedicalstore.SairaMedicalStoreApplication;
import com.example.android.sairamedicalstore.models.User;
import com.example.android.sairamedicalstore.ui.mediaFileUpload.ViewOrUploadImageActivity;
import com.example.android.sairamedicalstore.utils.Constants;
import com.example.android.sairamedicalstore.utils.Utils;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class MyProfileActivity extends AppCompatActivity {

    ImageView mImageViewUserProfilePic,mImageViewEditUserPhoneNo;
    LinearLayout mLinearLayoutUserPhoneNo;
    TextView mTextViewUserName,mTextViewUserMailId,mTextViewUserPhoneNo;

    User mCurrentUser;

    Firebase mFirebaseCurrentUserRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        mCurrentUser = ((SairaMedicalStoreApplication) this.getApplication()).getCurrentUser();

        initializeScreen();

        mFirebaseCurrentUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    mCurrentUser = dataSnapshot.getValue(User.class);
                    if(mCurrentUser == null)
                        finish();

                    displayUserProfile();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });




        mImageViewUserProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentToViewOrUploadImage = new Intent(MyProfileActivity.this, ViewOrUploadImageActivity.class);
                intentToViewOrUploadImage.putExtra("imageUriToDisplay",mCurrentUser.getPhotoURL());
                intentToViewOrUploadImage.putExtra("activityTitle", Constants.ACTIVITY_TITLE_PROFILE_PIC);
                startActivity(intentToViewOrUploadImage);
            }
        });

        mImageViewEditUserPhoneNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentToUpdatePhoneNumber = new Intent(MyProfileActivity.this,UpdatePhoneNumberActivity.class);
                startActivity(intentToUpdatePhoneNumber);
            }
        });
    }

    private void initializeScreen() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        /* Common toolbar setup */
        setSupportActionBar(toolbar);
        setTitle("My Profile");

        /* Add back button to the action bar */
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mImageViewUserProfilePic = (ImageView) findViewById(R.id.image_view_user_profile_pic);
        mImageViewEditUserPhoneNo = (ImageView) findViewById(R.id.image_view_edit_user_phone_no);

        mLinearLayoutUserPhoneNo = (LinearLayout) findViewById(R.id.linear_layout_user_phone_no);

        mTextViewUserName = (TextView) findViewById(R.id.text_view_user_name);
        mTextViewUserMailId = (TextView) findViewById(R.id.text_view_user_mail);
        mTextViewUserPhoneNo = (TextView) findViewById(R.id.text_view_user_phone_no);

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

    private void displayUserProfile()
    {
        if(mCurrentUser == null)
            this.finish();
        else
        {
            if(mCurrentUser.getPhotoURL() != null)
                Glide.with(mImageViewUserProfilePic.getContext())
                        .load(mCurrentUser.getPhotoURL())
                        .into(mImageViewUserProfilePic);

            if(mCurrentUser.getEmail() != null)
                mTextViewUserMailId.setText(mCurrentUser.getEmail());

            if(mCurrentUser.getName() != null)
                mTextViewUserName.setText(mCurrentUser.getName());

            if(mCurrentUser.getPhoneNo() != null)
                mTextViewUserPhoneNo.setText(mCurrentUser.getPhoneNo());
            else
                mTextViewUserPhoneNo.setText("Not Available");

        }
    }

}
