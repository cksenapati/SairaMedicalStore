package com.example.android.sairamedicalstore.ui;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.sairamedicalstore.R;

import java.util.HashMap;

import static com.example.android.sairamedicalstore.ui.AddNewPrescriptionActivity.mImageViewPrescriptionPage;

/**
 * Created by chandan on 12-08-2017.
 */

class AllPrescriptionPagesAdapter extends RecyclerView.Adapter<AllPrescriptionPagesAdapter.ViewHolder> {

    private HashMap<String,String> mHashMapPrescriptionPages;
    private Activity mActivity;

    public AllPrescriptionPagesAdapter(HashMap<String,String> data, Activity activity) {
        this.mHashMapPrescriptionPages = data;
        this.mActivity = activity;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public LinearLayout mLinearLayoutRecycleItem;
        public ImageView mImageViewPrescription;
        public TextView mTextViewPrescriptionName;
        public ViewHolder(View itemView) {
            super(itemView);
            mLinearLayoutRecycleItem = (LinearLayout) itemView.findViewById(R.id.linear_layout_recycle_item);
            mImageViewPrescription = (ImageView) itemView.findViewById(R.id.image_view_prescription);
            mTextViewPrescriptionName = (TextView) itemView.findViewById(R.id.text_view_prescription_name);
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public AllPrescriptionPagesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                     int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycle_item_for_prescription, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(AllPrescriptionPagesAdapter.ViewHolder holder, final int position) {
        Glide.with(holder.mImageViewPrescription.getContext())
                .load(mHashMapPrescriptionPages.get("page"+(position+1)))
                .into(holder.mImageViewPrescription);
        holder.mTextViewPrescriptionName.setText("page"+(position+1));

       holder.mLinearLayoutRecycleItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Glide.with(mImageViewPrescriptionPage.getContext())
                        .load(mHashMapPrescriptionPages.get("page"+(position+1)))
                        .into(mImageViewPrescriptionPage);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mHashMapPrescriptionPages.size();
    }




}
