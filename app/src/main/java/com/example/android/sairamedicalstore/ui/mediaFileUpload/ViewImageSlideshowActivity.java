package com.example.android.sairamedicalstore.ui.mediaFileUpload;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.example.android.sairamedicalstore.R;
import com.example.android.sairamedicalstore.models.PrescriptionPage;

import java.util.ArrayList;

public class ViewImageSlideshowActivity extends AppCompatActivity {

    private ViewFlipper mViewFlipper;
    private GestureDetector mGestureDetector;
    ImageView mImageViewGoBack;
    TextView mTextViewActivityTitle,mTextViewPreviousImage,mTextViewNextImage;

    ArrayList<String> mArrayListImageUrlsForSlideShow;
    String mActivityTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image_slideshow);

        Intent intent = getIntent();
        if (intent != null) {
            mArrayListImageUrlsForSlideShow =  intent.getStringArrayListExtra("arrayListImageUrlsForSlideShow");
            mActivityTitle = intent.getStringExtra("slideshowName");
        }

        initialization();

        mTextViewActivityTitle.setText(mActivityTitle);
        mTextViewPreviousImage.setText("<");
        mTextViewNextImage.setText(">");

        setSlideShow();

        // Set in/out flipping animations
        mViewFlipper.setInAnimation(this, android.R.anim.fade_in);
        mViewFlipper.setOutAnimation(this, android.R.anim.fade_out);

        CustomGestureDetector customGestureDetector = new CustomGestureDetector();
        mGestureDetector = new GestureDetector(this, customGestureDetector);

        mImageViewGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mTextViewPreviousImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewFlipper.showPrevious();
            }
        });

        mTextViewNextImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewFlipper.showNext();
            }
        });

        Toast.makeText(this,"Swipe left/right to view images",Toast.LENGTH_LONG).show();

    }

    class CustomGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            // Swipe left (next)
            if (e1.getX() > e2.getX()) {
                mViewFlipper.showNext();
            }

            // Swipe right (previous)
            if (e1.getX() < e2.getX()) {
                mViewFlipper.showPrevious();
            }

            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    private void initialization()
    {
        mViewFlipper = (ViewFlipper) findViewById(R.id.view_flipper_prescription_pages);
        mImageViewGoBack = (ImageView) findViewById(R.id.image_view_go_back);
        mTextViewActivityTitle = (TextView) findViewById(R.id.text_view_activity_title);

        mTextViewPreviousImage = (TextView) findViewById(R.id.text_view_previous_image);
        mTextViewNextImage = (TextView) findViewById(R.id.text_view_next_image);
    }

    public void setSlideShow()
    {
        for (String eachImageUrl :mArrayListImageUrlsForSlideShow) {

            ImageView imageView = new ImageView(this);
            Glide.with(imageView.getContext())
                    .load(eachImageUrl)
                    .into(imageView);

            mViewFlipper.addView(imageView);
        }


    }

}
