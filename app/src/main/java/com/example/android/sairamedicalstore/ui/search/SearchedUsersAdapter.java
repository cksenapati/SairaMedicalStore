package com.example.android.sairamedicalstore.ui.search;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.example.android.sairamedicalstore.R;
import com.example.android.sairamedicalstore.models.User;
import com.example.android.sairamedicalstore.utils.Constants;
import com.example.android.sairamedicalstore.utils.Utils;
import com.firebase.client.Query;
import com.firebase.ui.FirebaseListAdapter;

import java.util.Date;

/**
 * Created by chandan on 17-08-2017.
 */

public class SearchedUsersAdapter extends FirebaseListAdapter<User> {


    public SearchedUsersAdapter(Activity activity, Class<User> modelClass, int modelLayout, Query ref) {
        super(activity,modelClass, modelLayout, ref);
        this.mActivity = activity;
    }

    @Override
    protected void populateView(View view, User user) {

        TextView textViewUserName = (TextView) view.findViewById(R.id.text_view_user_name);
        textViewUserName.setText(Utils.toLowerCaseExceptFirstLetter(user.getName()));

        TextView textViewUserEmail = (TextView) view.findViewById(R.id.text_view_user_email);
        textViewUserEmail.setText(user.getEmail());

    }


}
