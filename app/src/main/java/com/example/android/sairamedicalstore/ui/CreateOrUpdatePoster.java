package com.example.android.sairamedicalstore.ui;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.android.sairamedicalstore.R;
import com.example.android.sairamedicalstore.SairaMedicalStoreApplication;
import com.example.android.sairamedicalstore.models.DefaultKeyValuePair;
import com.example.android.sairamedicalstore.models.Poster;
import com.example.android.sairamedicalstore.operations.PosterOperations;
import com.example.android.sairamedicalstore.utils.Constants;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static android.R.attr.key;

public class CreateOrUpdatePoster extends AppCompatActivity {

    ImageView mImageViewPosterImage, mImageViewEditPosterIcon;
    EditText mEditTextPosterName,mEditTextAboutPoster;
    FloatingActionButton mFabAddEditText;
    TextView mTextViewCreateOrUpdatePoster;
    LinearLayout mLinearLayoutAboutPoster;

    Firebase mFirebaseAllPostersRef,mFirebaseCurrentPosterRef;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mFirebaseStorageReference;

    private static final int RC_PHOTO_PICKER =  1;
    String mCurrentPosterId;
    Poster mCurrentPoster;
    public static Uri mCurrentPosterImageURI;
    String mCurrentPosterImageDownloadUrl;

    ArrayList<DefaultKeyValuePair> mArrayListCommonDefaultValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_or_update_poster);

        Intent intent = getIntent();
        if (intent != null) {
            mCurrentPosterId = intent.getStringExtra("posterId");
        }

        initializeScreen();

        getPosterFromPosterId();

        mFabAddEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText myEditText = new EditText(CreateOrUpdatePoster.this);
                myEditText.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT)); // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                myEditText.setHint("About Poster");

                int padding_in_dp = 7;
                final float scale = getResources().getDisplayMetrics().density;
                int padding_in_px = (int) (padding_in_dp * scale + 0.5f);

                myEditText.setPadding(padding_in_px,padding_in_px,padding_in_px,padding_in_px);
                myEditText.setBackgroundResource(R.color.tw__solid_white);

                TableLayout.LayoutParams params = new TableLayout.LayoutParams();
                int margin = (int) getResources().getDimension(R.dimen.extra_small_margin);
                params.setMargins(margin, margin, margin, margin);
                myEditText.setLayoutParams(params);

                myEditText.requestFocus();

                mLinearLayoutAboutPoster.addView(myEditText);
            }
        });

        mTextViewCreateOrUpdatePoster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createOrUpdatePosterDetails();
            }
        });

        mImageViewEditPosterIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent imagePickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
                imagePickerIntent.setType("image/*");
                imagePickerIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(imagePickerIntent, "Complete action using"), RC_PHOTO_PICKER);

            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_PHOTO_PICKER)
        {
            if(resultCode == RESULT_OK){

                 mCurrentPosterImageURI = data.getData();

                StorageReference selectedPhotoRef = mFirebaseStorageReference.child(mCurrentPosterImageURI.getLastPathSegment());
                UploadTask uploadTask = selectedPhotoRef.putFile(mCurrentPosterImageURI);
                uploadTask.addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // When the image has successfully uploaded, we get its download URL
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        try {
                            mCurrentPosterImageDownloadUrl = downloadUrl.toString();
                            updatePosterPic();
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

    private void updatePosterPic()
    {
        Glide.with(mImageViewPosterImage.getContext())
                .load(mCurrentPosterImageDownloadUrl)
                .into(mImageViewPosterImage);
    }


    private void initializeScreen()
    {
        mImageViewEditPosterIcon = (ImageView) findViewById(R.id.image_view_edit_poster_icon);
        mImageViewPosterImage = (ImageView) findViewById(R.id.image_view_poster_image);

        mEditTextPosterName = (EditText) findViewById(R.id.edit_text_poster_name);
        mEditTextAboutPoster = (EditText) findViewById(R.id.edit_text_about);

        mFabAddEditText = (FloatingActionButton) findViewById(R.id.fab_add_edit_text);

        mTextViewCreateOrUpdatePoster = (TextView) findViewById(R.id.text_view_create_or_update);

        mLinearLayoutAboutPoster = (LinearLayout) findViewById(R.id.linear_layout_about_poster);

        mFirebaseAllPostersRef = new Firebase(Constants.FIREBASE_URL_SAIRA_All_POSTERS);

        mFirebaseStorage = FirebaseStorage.getInstance();
        mFirebaseStorageReference = mFirebaseStorage.getReference().child("TemporaryPics");

    }

    private void getPosterFromPosterId()
    {
        if(mCurrentPosterId != null)
        {
            mTextViewCreateOrUpdatePoster.setText("Update");
            mFirebaseCurrentPosterRef = mFirebaseAllPostersRef.child(mCurrentPosterId);
            mFirebaseCurrentPosterRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists())
                    {
                        mCurrentPoster = dataSnapshot.getValue(Poster.class);
                        if(mCurrentPoster != null)
                        {
                            setPosterDetails();
                        }
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
        }
    }

    private void setPosterDetails()
    {
        mCurrentPosterImageDownloadUrl = mCurrentPoster.getPosterImageURI();
        Glide.with(mImageViewPosterImage.getContext())
                .load(mCurrentPoster.getPosterImageURI())
                .into(mImageViewPosterImage);

        mEditTextPosterName.setText(mCurrentPoster.getPosterName());

        int count = 0;
        for (Map.Entry<String, Object> entry : mCurrentPoster.getAboutPoster().entrySet()) {
            if(count == 0)
            {
                mEditTextAboutPoster.setText(entry.getValue().toString());
            }
            else{
                EditText myEditText = new EditText(this);
                myEditText.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT)); // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.

                int padding_in_dp = 7;
                final float scale = getResources().getDisplayMetrics().density;
                int padding_in_px = (int) (padding_in_dp * scale + 0.5f);

                myEditText.setPadding(padding_in_px,padding_in_px,padding_in_px,padding_in_px);

                myEditText.setBackgroundResource(R.color.tw__solid_white);
                myEditText.setText(entry.getValue().toString());

                TableLayout.LayoutParams params = new TableLayout.LayoutParams();
                int margin = (int) getResources().getDimension(R.dimen.extra_small_margin);
                params.setMargins(margin, margin, margin, margin);
                myEditText.setLayoutParams(params);

                mLinearLayoutAboutPoster.addView(myEditText);
            }
            count++;
        }
    }

    private void createOrUpdatePosterDetails()
    {
        if(validateAllEditTextFields())
        {
            PosterOperations operation = new PosterOperations(this);
            if(mCurrentPosterId == null)
            operation.CreateNewPoster(mCurrentPosterImageURI,mEditTextPosterName.getText().toString().toUpperCase(),
                    null,getAllAboutPoster(),mCurrentPosterImageDownloadUrl);
            else {
                operation.UpdateOldPoster(mCurrentPosterId, mCurrentPosterImageURI, mEditTextPosterName.getText().toString().toUpperCase(),
                        null, getAllAboutPoster(), mCurrentPosterImageDownloadUrl);
                finish();
            }
        }
    }

    private HashMap<String, Object> getAllAboutPoster()
    {
        HashMap<String, Object> allAbouts = new HashMap<>();

        int count = 1;
        for( int i = 0; i < mLinearLayoutAboutPoster.getChildCount(); i++ ){
            View view = mLinearLayoutAboutPoster.getChildAt(i);
            if (view instanceof EditText) {
                if(((EditText)view).getText().toString().trim().length() >= 1) {
                    allAbouts.put("about" + count, ((EditText) view).getText().toString());
                    count++;
                }

            }
        }
        return allAbouts;
    }

    private boolean validateAllEditTextFields()
    {
        if(mEditTextPosterName.getText().toString().trim().length() < 1) {
            Toast.makeText(this,"Fields can't be empty",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(mCurrentPosterImageDownloadUrl == null) {
            Toast.makeText(this,"Upload a photo",Toast.LENGTH_SHORT).show();
            return false;
        }

        for( int i = 0; i < mLinearLayoutAboutPoster.getChildCount(); i++ ){
            View view = mLinearLayoutAboutPoster.getChildAt(i);
            if (view instanceof EditText) {
                if(((EditText)view).getText().toString().trim().length() >= 1)
                    return true;
            }
        }

        Toast.makeText(this,"Fields can't be empty",Toast.LENGTH_SHORT).show();
        return false;
    }
}
