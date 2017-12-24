package com.example.android.sairamedicalstore.ui.search;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.example.android.sairamedicalstore.R;
import com.example.android.sairamedicalstore.utils.Constants;
import com.example.android.sairamedicalstore.utils.Utils;
import com.firebase.client.Query;
import com.firebase.ui.FirebaseListAdapter;

import java.util.Date;

/**
 * Created by chandan on 17-08-2017.
 */

public class SearchedQueriesAdapter extends FirebaseListAdapter<com.example.android.sairamedicalstore.models.Query> {


    public SearchedQueriesAdapter(Activity activity, Class<com.example.android.sairamedicalstore.models.Query> modelClass, int modelLayout, Query ref) {
        super(activity,modelClass, modelLayout, ref);
        this.mActivity = activity;
    }

    @Override
    protected void populateView(View view, com.example.android.sairamedicalstore.models.Query query) {

        TextView textViewQueryTitle = (TextView) view.findViewById(R.id.text_view_query_title);
        textViewQueryTitle.setText(query.getQueryTitle());

        TextView textViewQueryPostedOn = (TextView) view.findViewById(R.id.text_view_query_posted_on);
        textViewQueryPostedOn.setText(Utils.SIMPLE_DATE_ONLY_FORMAT.format(
                new Date(query.getTimestampCreatedLong())));

        TextView textViewQueryStatus = (TextView) view.findViewById(R.id.text_view_query_status);
        textViewQueryStatus.setText(query.getQueryStatus());
        textViewQueryStatus.setTextColor(setQueryStatusColor(query.getQueryStatus()));
    }

    public int setQueryStatusColor(String status)
    {
        int textColorCode;

        if(status.equals(Constants.QUERY_STATUS_UNDER_PROCESS))
        {
            textColorCode = mActivity.getResources().getColor(R.color.under_process_message_text_color);
        }
        else if(status.equals(Constants.QUERY_STATUS_SOLVED))
        {
            textColorCode = mActivity.getResources().getColor(R.color.success_message_text_color);
        }
        else {
            textColorCode = mActivity.getResources().getColor(R.color.fail_message_text_color);
        }

        return textColorCode;

    }
}
