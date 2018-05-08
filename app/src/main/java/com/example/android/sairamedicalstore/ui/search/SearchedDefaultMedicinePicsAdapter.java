package com.example.android.sairamedicalstore.ui.search;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.sairamedicalstore.R;
import com.example.android.sairamedicalstore.models.DefaultKeyValuePair;
import com.example.android.sairamedicalstore.models.Poster;
import com.example.android.sairamedicalstore.utils.Utils;
import com.firebase.client.Query;
import com.firebase.ui.FirebaseListAdapter;

/**
 * Created by chandan on 17-08-2017.
 */

public class SearchedDefaultMedicinePicsAdapter extends FirebaseListAdapter<DefaultKeyValuePair> {


    public SearchedDefaultMedicinePicsAdapter(Activity activity, Class<DefaultKeyValuePair> modelClass, int modelLayout, Query ref) {
        super(activity,modelClass, modelLayout, ref);
        this.mActivity = activity;
    }

    @Override
    protected void populateView(View view, DefaultKeyValuePair keyValuePair) {


        ImageView imageViewSinglePoster = (ImageView) view.findViewById(R.id.image_view_single_poster);
        Glide.with(imageViewSinglePoster.getContext())
                .load(keyValuePair.getValue())
                .into(imageViewSinglePoster);

        String displayablePosterName = Utils.toLowerCaseExceptFirstLetter(keyValuePair.getKey());
        TextView textViewSinglePosterName = (TextView) view.findViewById(R.id.text_view_single_poster_name);
        textViewSinglePosterName.setText(displayablePosterName);

    }

}
