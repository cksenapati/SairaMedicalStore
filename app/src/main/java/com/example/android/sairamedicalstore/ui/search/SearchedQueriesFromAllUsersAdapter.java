package com.example.android.sairamedicalstore.ui.search;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.android.sairamedicalstore.R;
import com.example.android.sairamedicalstore.models.Query;
import com.example.android.sairamedicalstore.utils.Constants;
import com.example.android.sairamedicalstore.utils.Utils;

import java.util.ArrayList;
import java.util.Date;


/**
 * Created by chandan on 08-03-2017.
 */

public class SearchedQueriesFromAllUsersAdapter extends ArrayAdapter<Query> {

    Activity mActivity;

    public SearchedQueriesFromAllUsersAdapter(Activity activity, ArrayList<Query> arrayListQueriesFromAllUsers) {
        super(activity, 0, arrayListQueriesFromAllUsers);
        mActivity = activity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final Query eachQuery = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_single_old_query_ticket, parent, false);
        }

        TextView textViewQueryTitle = (TextView) convertView.findViewById(R.id.text_view_query_title);
        textViewQueryTitle.setText(eachQuery.getQueryTitle());

        TextView textViewQueryPostedOn = (TextView) convertView.findViewById(R.id.text_view_query_posted_on);
        textViewQueryPostedOn.setText(Utils.SIMPLE_DATE_ONLY_FORMAT.format(
                new Date(eachQuery.getTimestampCreatedLong())));

        TextView textViewQueryStatus = (TextView) convertView.findViewById(R.id.text_view_query_status);
        textViewQueryStatus.setText(eachQuery.getQueryStatus());
        textViewQueryStatus.setTextColor(setQueryStatusColor(eachQuery.getQueryStatus()));


        return convertView;
    }

    public int setQueryStatusColor(String status)
    {
        int textColorCode;

        if(status.equals(Constants.QUERY_STATUS_RUNNING))
            textColorCode = mActivity.getResources().getColor(R.color.under_process_message_text_color);
        else if(status.equals(Constants.QUERY_STATUS_SOLVED))
            textColorCode = mActivity.getResources().getColor(R.color.success_message_text_color);
        else
            textColorCode = mActivity.getResources().getColor(R.color.fail_message_text_color);

        return textColorCode;

    }

}

