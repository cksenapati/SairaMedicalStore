package com.example.android.sairamedicalstore.ui.search;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.sairamedicalstore.R;
import com.example.android.sairamedicalstore.models.DisplayCategory;
import com.example.android.sairamedicalstore.models.SelectedItem;
import com.example.android.sairamedicalstore.utils.Utils;
import com.firebase.client.Query;
import com.firebase.ui.FirebaseListAdapter;

import java.util.ArrayList;

/**
 * Created by chandan on 17-08-2017.
 */

public class SearchedDisplayCategoriesAdapter extends FirebaseListAdapter<DisplayCategory> {

    ArrayList<SelectedItem> mArrayListSelectedItems;


    public SearchedDisplayCategoriesAdapter(Activity activity, Class<DisplayCategory> modelClass, int modelLayout, Query ref, ArrayList<SelectedItem> arrayListSelectedItems) {
        super(activity, modelClass, modelLayout, ref);
        this.mActivity = activity;
        this.mArrayListSelectedItems = arrayListSelectedItems;
    }

    @Override
    protected void populateView(View view, DisplayCategory displayCategory) {

        String displayableName = Utils.toLowerCaseExceptFirstLetter(displayCategory.getDisplayCategoryName());
        TextView textViewDisplayCategoryName = (TextView) view.findViewById(R.id.text_view_item_name);
        textViewDisplayCategoryName.setText(displayableName);

        ImageView imageView = (ImageView) view.findViewById(R.id.image_view_add);
        imageView.setVisibility(View.GONE);

        if (mArrayListSelectedItems.size() > 0) {
            for (int i = 0;i<mArrayListSelectedItems.size();i++)
            {
                if (mArrayListSelectedItems.get(i).getItemName().toUpperCase().equals(displayCategory.getDisplayCategoryName())) {
                    imageView.setVisibility(View.VISIBLE);
                    break;
                }
            }
        }

    }

}
