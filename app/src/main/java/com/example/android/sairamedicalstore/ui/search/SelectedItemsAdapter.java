package com.example.android.sairamedicalstore.ui.search;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.sairamedicalstore.R;
import com.example.android.sairamedicalstore.models.SelectedItem;

import java.util.ArrayList;

/**
 * Created by chandan on 12-08-2017.
 */

class SelectedItemsAdapter extends RecyclerView.Adapter<SelectedItemsAdapter.ViewHolder> {

    private ArrayList<SelectedItem> mArrayListSelectedItems;
    private static Activity mActivity;

    public SelectedItemsAdapter(ArrayList<SelectedItem> data, Activity activity) {
        this.mArrayListSelectedItems = data;
        this.mActivity = activity;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView mImageViewRemoveItem;
        public TextView mTextViewItemName;
        public ViewHolder(final View itemView) {
            super(itemView);
            mImageViewRemoveItem = (ImageView) itemView.findViewById(R.id.image_view_remove_item);
            mTextViewItemName = (TextView) itemView.findViewById(R.id.text_view_item_name);

            mImageViewRemoveItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemView.setVisibility(View.GONE);
                }
            });
        }


    }

    // Create new views (invoked by the layout manager)
    @Override
    public SelectedItemsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                              int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_single_selected_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(SelectedItemsAdapter.ViewHolder holder, int position) {
        holder.mTextViewItemName.setText(mArrayListSelectedItems.get(position).getItemName());
    }

    @Override
    public int getItemCount() {
        return mArrayListSelectedItems.size();
    }


}
