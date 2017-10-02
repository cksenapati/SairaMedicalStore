package com.example.android.sairamedicalstore.ui;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.sairamedicalstore.R;

public class CreateOrUpdatePoster extends AppCompatActivity {

    ImageView mImageViewPosterImage, mImageViewEditPosterIcon;
    EditText mEditTextPosterName,mEditTextAboutPoster;
    FloatingActionButton mFabAddEditText;
    TextView mTextViewCreateOrUpdatePoster;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_or_update_poster);
    }

    private void initializeScreen()
    {
        mImageViewEditPosterIcon = (ImageView) findViewById(R.id.image_view_edit_poster_icon);
        mImageViewPosterImage = (ImageView) findViewById(R.id.image_view_poster_image);

        mEditTextPosterName = (EditText) findViewById(R.id.edit_text_poster_name);
        mEditTextAboutPoster = (EditText) findViewById(R.id.edit_text_about);

        mFabAddEditText = (FloatingActionButton) findViewById(R.id.fab_add_edit_text);

        mTextViewCreateOrUpdatePoster = (TextView) findViewById(R.id.text_view_create_or_update);
    }
}
