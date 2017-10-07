package com.example.android.sairamedicalstore.ui;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.sairamedicalstore.R;
import com.example.android.sairamedicalstore.SairaMedicalStoreApplication;
import com.example.android.sairamedicalstore.models.DisplayProduct;
import com.example.android.sairamedicalstore.models.DefaultKeyValuePair;
import com.example.android.sairamedicalstore.models.Medicine;
import com.example.android.sairamedicalstore.ui.search.SearchActivity;

import java.util.ArrayList;

/**
 * Created by chandan on 12-08-2017.
 */

class DisplayProductsAdapter extends RecyclerView.Adapter<DisplayProductsAdapter.ViewHolder> {

    private ArrayList<DisplayProduct> mArrayListDisplayProducts;
    private ArrayList<DefaultKeyValuePair> mArrayListDefaultMedicinePics;
    private Activity mActivity;

    public DisplayProductsAdapter(ArrayList<DisplayProduct> data, Activity activity) {
        this.mArrayListDisplayProducts = data;
        this.mActivity = activity;
        this.mArrayListDefaultMedicinePics = ((SairaMedicalStoreApplication) mActivity.getApplication()).getArrayListDefaultMedicinePics();
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView mImageViewItem;
        public TextView mTextViewName;
        public TextView mTextViewPrice;
        public ViewHolder(View itemView) {
            super(itemView);
            mImageViewItem = (ImageView) itemView.findViewById(R.id.image_view_item_pic);
            mTextViewName = (TextView) itemView.findViewById(R.id.text_view_item_name);
            mTextViewPrice = (TextView) itemView.findViewById(R.id.text_view_item_price);
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public DisplayProductsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycle_items, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(DisplayProductsAdapter.ViewHolder holder, final int position) {
        Glide.with(holder.mImageViewItem.getContext())
                .load(getImageUrlToDisplay(position))
                .into(holder.mImageViewItem);
        holder.mTextViewName.setText(mArrayListDisplayProducts.get(position).getProductName());
        holder.mTextViewPrice.setText(String.valueOf(mArrayListDisplayProducts.get(position).getPricePerPack()));
        holder.mImageViewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentProductDetails = new Intent(mActivity, ProductDetailsActivity.class);
                intentProductDetails.putExtra("medicineId", mArrayListDisplayProducts.get(position).getProductId());
                mActivity.startActivity(intentProductDetails);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mArrayListDisplayProducts.size();
    }

    public String getImageUrlToDisplay(int position)
    {
        String imageUrl = mArrayListDisplayProducts.get(position).getProductImageUrl();
        String medicineType = mArrayListDisplayProducts.get(position).getProductType();
        if(imageUrl.equals("default"))
        {
            for (int i = 0; i < mArrayListDefaultMedicinePics.size(); i++) {
                if(mArrayListDefaultMedicinePics.get(i).getKey().equals(medicineType))
                    return mArrayListDefaultMedicinePics.get(i).getValue();
            }
            return mArrayListDefaultMedicinePics.get(0).getValue();
        }
        else
            return imageUrl;
    }


}
