package com.example.android.sairamedicalstore.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.android.sairamedicalstore.R;
import com.example.android.sairamedicalstore.models.Poster;
import com.example.android.sairamedicalstore.utils.Constants;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.Map;

import static android.R.attr.textSize;

public class PosterDetailsActivity extends AppCompatActivity {

    ImageView mImageViewPoster;
    LinearLayout mLinearLayoutAboutPoster;

    Firebase mFirebaseAllPostersRef,mFirebaseCurrentPosterRef;

    String mPosterId;
    Poster mCurrentPoster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poster_details);

        Intent intent = getIntent();
        if (intent != null) {
            mPosterId = intent.getStringExtra("posterId");
        }

        initializeScreen();

        getCurrentPoster();

    }

    private void initializeScreen() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        /* Common toolbar setup */
        setSupportActionBar(toolbar);

        mImageViewPoster = (ImageView) findViewById(R.id.image_view_poster_image);
        mLinearLayoutAboutPoster = (LinearLayout) findViewById(R.id.linear_layout_about_poster);

        mFirebaseAllPostersRef = new Firebase(Constants.FIREBASE_URL_SAIRA_All_POSTERS);
    }

    public void getCurrentPoster()
    {
        if(mPosterId != null)
        {
             mFirebaseCurrentPosterRef = mFirebaseAllPostersRef.child(mPosterId);
            mFirebaseCurrentPosterRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists())
                    {
                        mCurrentPoster = dataSnapshot.getValue(Poster.class);
                        if(mCurrentPoster != null)
                            pageLoad();
                        else{
                            Toast.makeText(PosterDetailsActivity.this,"Poster not found.",Toast.LENGTH_LONG).show();
                            finish();
                        }
                    }
                    else{
                        Toast.makeText(PosterDetailsActivity.this,"Poster not found.",Toast.LENGTH_LONG).show();
                        finish();
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    Toast.makeText(PosterDetailsActivity.this,"Poster not found.",Toast.LENGTH_LONG).show();
                    finish();
                }
            });
        }else{
            Toast.makeText(this,"Poster not found.",Toast.LENGTH_LONG).show();
            finish();
        }
    }

    public void pageLoad()
    {
        Glide.with(mImageViewPoster.getContext())
                .load(mCurrentPoster.getPosterImageURI())
                .into(mImageViewPoster);

        setTitle(mCurrentPoster.getPosterName());

        for (Map.Entry<String, Object> entry : mCurrentPoster.getAboutPoster().entrySet()) {

            TextView textView = new TextView(this);
            textView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT)); // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.

            int padding = getResources().getDimensionPixelOffset(R.dimen.extra_small_margin);
            float textSize = getResources().getDimension(R.dimen.double_extra_small_text_size);

            textView.setPadding(padding,padding,padding,padding);

            textView.setText(entry.getValue().toString());
            textView.setTextSize(textSize);

            mLinearLayoutAboutPoster.addView(textView);

        }
    }
}
