package com.example.android.sairamedicalstore.operations;

import android.app.Activity;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.android.sairamedicalstore.R;
import com.example.android.sairamedicalstore.SairaMedicalStoreApplication;
import com.example.android.sairamedicalstore.models.Medicine;
import com.example.android.sairamedicalstore.models.Poster;
import com.example.android.sairamedicalstore.models.User;
import com.example.android.sairamedicalstore.ui.CreateOrUpdatePoster;
import com.example.android.sairamedicalstore.utils.Constants;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ServerValue;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.HashMap;

/**
 * Created by chandan on 05-10-2017.
 */

public class PosterOperations {
    Firebase mFirebaseAllPosters;
    User mCurrentUser;
    Activity mActivity;

    public PosterOperations( Activity mActivity) {
        this.mActivity = mActivity;
        this.mCurrentUser = ((SairaMedicalStoreApplication) mActivity.getApplication()).getCurrentUser();
        this.mFirebaseAllPosters = new Firebase(Constants.FIREBASE_URL_SAIRA_All_POSTERS);
    }

    public void CreateNewPoster(final Uri posterImageURI,final String posterName, String displayCategory, HashMap<String, Object> aboutPoster,
                                final String posterImageDownloadUrl)
    {
        String posterCreatedBy = mCurrentUser.getEmail();
        HashMap<String, Object> timestampCreated = new HashMap<String, Object>();
        timestampCreated.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);
        HashMap<String, Object> timestampLastUpdate = timestampCreated;
        final String posterId = mFirebaseAllPosters.push().getKey();

        final Poster currentPoster = new Poster(posterId,posterName,displayCategory,aboutPoster,posterImageDownloadUrl,posterCreatedBy,timestampCreated,timestampLastUpdate);

        mFirebaseAllPosters.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Boolean isExist = false;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Poster eachPoster = snapshot.getValue(Poster.class);
                    if (eachPoster.getPosterName().equals(posterName)) {
                        Toast.makeText(mActivity, Constants.ITEM_ALREADY_EXISTS, Toast.LENGTH_SHORT).show();
                        isExist = true;
                        break;
                    }
                }
                if(!isExist) {
                    try {
                        mFirebaseAllPosters.child(posterId).setValue(currentPoster);
                        uploadImageInStorage(posterImageURI,posterId,posterImageDownloadUrl);
                        resetAllControlsFromCreateOrUpdatePage();
                        Toast.makeText(mActivity, Constants.UPLOAD_SUCCESSFUL, Toast.LENGTH_SHORT).show();

                    } catch (Exception ex) {
                        Toast.makeText(mActivity, Constants.UPLOAD_FAIL, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void UpdateOldPoster(final String posterId,final Uri posterImageURI,final String posterName, String displayCategory, HashMap<String, Object> aboutPoster,
                                final String posterImageDownloadUrl)
    {
        HashMap<String, Object> timestampLastUpdate = new HashMap<String, Object>();
        timestampLastUpdate.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);

        final Poster updatedPoster = new Poster(posterId,posterName,displayCategory,aboutPoster,posterImageDownloadUrl,null,null,timestampLastUpdate);

        mFirebaseAllPosters.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Poster eachPoster = snapshot.getValue(Poster.class);
                    if (eachPoster.getPosterId().equals(posterId)) {

                        updatedPoster.setPosterCreatedBy(eachPoster.getPosterCreatedBy());
                        updatedPoster.setTimestampCreated(eachPoster.getTimestampCreated());

                        try {
                            mFirebaseAllPosters.child(posterId).setValue(updatedPoster);
                            if(posterImageURI != null)
                              uploadImageInStorage(posterImageURI,posterId,posterImageDownloadUrl);
                            resetAllControlsFromCreateOrUpdatePage();
                            Toast.makeText(mActivity, Constants.UPDATE_SUCCESSFUL, Toast.LENGTH_SHORT).show();

                        } catch (Exception ex) {
                            Toast.makeText(mActivity, Constants.UPDATE_FAIL, Toast.LENGTH_SHORT).show();
                        }

                        break;
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void uploadImageInStorage(Uri posterImageURI,final String posterId,final String posterImageDownloadUrl)
    {
        FirebaseStorage  mFirebaseStorage = FirebaseStorage.getInstance();
        StorageReference mFirebaseAllPostersStorageReference = mFirebaseStorage.getReference().child("allPosters");


        StorageReference selectedPhotoRef = mFirebaseAllPostersStorageReference.child(posterImageURI.getLastPathSegment());
        UploadTask uploadTask = selectedPhotoRef.putFile(posterImageURI);
        uploadTask.addOnSuccessListener(mActivity, new OnSuccessListener<UploadTask.TaskSnapshot>() {
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                try{
                    mFirebaseAllPosters.child(posterId).child("posterImageURI").setValue(downloadUrl.toString());
                    DeleteOldDownloadPath(posterImageDownloadUrl);
                }catch (Exception ex){}
            }
        });
    }

    private void DeleteOldDownloadPath(String posterImageDownloadUrl)
    {
        FirebaseStorage  mFirebaseStorage = FirebaseStorage.getInstance();
        StorageReference photoRef = mFirebaseStorage.getReferenceFromUrl(posterImageDownloadUrl);

        try{
            photoRef.delete();
        }catch (Exception ex){}
    }

    private void resetAllControlsFromCreateOrUpdatePage()
    {
        ImageView imageViewPosterImage = (ImageView) mActivity.findViewById(R.id.image_view_poster_image);
        imageViewPosterImage.setImageResource(R.drawable.upload_image);
        CreateOrUpdatePoster.mCurrentPosterImageURI = null;

        EditText editTextPosterName = (EditText) mActivity.findViewById(R.id.edit_text_poster_name);
        editTextPosterName.setText("");


        LinearLayout linearLayoutAboutPoster = (LinearLayout) mActivity.findViewById(R.id.linear_layout_about_poster);
        for( int i = 0; i < linearLayoutAboutPoster.getChildCount(); i++ ){
            View view = linearLayoutAboutPoster.getChildAt(i);
            if (view instanceof EditText) {
                ((EditText)view).setText("");
                view.setVisibility(View.GONE);
            }
        }

        EditText editTextAboutPoster = (EditText) mActivity.findViewById(R.id.edit_text_about);
        editTextAboutPoster.setVisibility(View.VISIBLE);

    }
}
