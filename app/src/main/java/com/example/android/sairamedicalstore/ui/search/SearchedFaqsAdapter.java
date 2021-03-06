package com.example.android.sairamedicalstore.ui.search;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.sairamedicalstore.R;
import com.example.android.sairamedicalstore.models.Faq;
import com.example.android.sairamedicalstore.models.Poster;
import com.example.android.sairamedicalstore.utils.Utils;
import com.firebase.client.Query;
import com.firebase.ui.FirebaseListAdapter;

/**
 * Created by chandan on 17-08-2017.
 */

public class SearchedFaqsAdapter extends FirebaseListAdapter<Faq> {


    public SearchedFaqsAdapter(Activity activity, Class<Faq> modelClass, int modelLayout, Query ref) {
        super(activity,modelClass, modelLayout, ref);
        this.mActivity = activity;
    }

    @Override
    protected void populateView(View view, Faq faq) {

        TextView textViewFaqQuestion = (TextView) view.findViewById(R.id.text_view_faq_question);
        textViewFaqQuestion.setText(faq.getFaqQuestion());

    }

}
