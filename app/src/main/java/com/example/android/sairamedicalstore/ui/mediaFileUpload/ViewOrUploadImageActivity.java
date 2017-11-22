package com.example.android.sairamedicalstore.ui.mediaFileUpload;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.android.sairamedicalstore.R;
import com.example.android.sairamedicalstore.SairaMedicalStoreApplication;
import com.example.android.sairamedicalstore.models.User;
import com.example.android.sairamedicalstore.ui.profile.UpdatePhoneNumberActivity;
import com.example.android.sairamedicalstore.utils.Constants;
import com.example.android.sairamedicalstore.utils.Utils;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ViewOrUploadImageActivity extends AppCompatActivity {

    ImageView mImageViewImageToDisplay;
    ProgressBar mProgressBarImageLoading;
    FloatingActionButton mFabEditImage;

    User mCurrentUser;
    String mImageUriToDisplay;
    String mActivityTitle;
    private static final int RC_PROFILE_PIC_PICKER =  1;

    private FirebaseStorage mFirebaseStorage;
    private StorageReference mFirebaseCurrentUserProfilePicStorageRef;
    Firebase mFirebaseCurrentUserRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_or_upload_image);

        Intent intent = getIntent();
        if (intent != null) {
            mImageUriToDisplay = intent.getStringExtra("imageUriToDisplay");
            mActivityTitle = intent.getStringExtra("activityTitle");
        }

        mCurrentUser = ((SairaMedicalStoreApplication) this.getApplication()).getCurrentUser();

        initialization();

        displayImage();

        activityIsFor();

        mFabEditImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent imagePickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
                imagePickerIntent.setType("image/*");
                imagePickerIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(imagePickerIntent, "Complete action using"), RC_PROFILE_PIC_PICKER);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_PROFILE_PIC_PICKER)
        {
            if(resultCode == RESULT_OK){

                StorageReference selectedPhotoRef = mFirebaseCurrentUserProfilePicStorageRef.child(data.getData().getLastPathSegment());
                UploadTask uploadTask = selectedPhotoRef.putFile(data.getData());
                uploadTask.addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // When the image has successfully uploaded, we get its download URL
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        try {
                            removeOldProfilePic(mCurrentUser.getPhotoURL());
                            mCurrentUser.setPhotoURL(downloadUrl.toString());
                            mFirebaseCurrentUserRef.setValue(mCurrentUser);
                        }catch (Exception ex){}
                    }
                });
            }
            else if (resultCode == RESULT_CANCELED)
            {
                // Unable to pick this file
                Toast.makeText(this, "Unable to pick this image file", Toast.LENGTH_SHORT).show();
                //finish();
            }
        }

    }


    private void initialization()
    {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        /* Common toolbar setup */
        setSupportActionBar(toolbar);
        setTitle(mActivityTitle);

        /* Add back button to the action bar */
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mImageViewImageToDisplay = (ImageView) findViewById(R.id.image_view_image_to_display);

        mProgressBarImageLoading = (ProgressBar) findViewById(R.id.progress_bar_image_loading);

        mFabEditImage = (FloatingActionButton) findViewById(R.id.fab_edit_image);
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

    public void displayImage()
    {
        if(mImageUriToDisplay != null)
            Glide.with(mImageViewImageToDisplay.getContext())
                    .load(mImageUriToDisplay)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            mProgressBarImageLoading.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            mProgressBarImageLoading.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(mImageViewImageToDisplay);
        else
            this.finish();
    }

    public void activityIsFor()
    {
        switch (mActivityTitle)
        {
            case Constants.ACTIVITY_TITLE_PROFILE_PIC : profilePicSetUp();

        }
    }

    public void profilePicSetUp()
    {
        mFirebaseStorage = FirebaseStorage.getInstance();
        mFirebaseCurrentUserRef = new Firebase(Constants.FIREBASE_URL_SAIRA_ALL_USERS).child(Utils.encodeEmail(mCurrentUser.getEmail()));
        mFirebaseCurrentUserProfilePicStorageRef = mFirebaseStorage.getReference().child(Constants.FIREBASE_LOCATION_All_PROFILE_PICS).child(Utils.encodeEmail(mCurrentUser.getEmail()));

        mFabEditImage.setVisibility(View.VISIBLE);

        mFirebaseCurrentUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    mCurrentUser = dataSnapshot.getValue(User.class);
                    if(mCurrentUser == null)
                        finish();

                    mImageUriToDisplay = mCurrentUser.getPhotoURL();
                    ((SairaMedicalStoreApplication) ViewOrUploadImageActivity.this.getApplication()).setCurrentUser(mCurrentUser);
                    displayImage();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    public void removeOldProfilePic(String imageUrlToBeRemoved)
    {

        FirebaseStorage  mFirebaseStorage = FirebaseStorage.getInstance();
        StorageReference photoRef = mFirebaseStorage.getReferenceFromUrl(imageUrlToBeRemoved);

        try{
            photoRef.delete();
        }catch (Exception ex){}

    }
}
