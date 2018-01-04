package com.example.android.sairamedicalstore.ui.order;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.sairamedicalstore.R;
import com.example.android.sairamedicalstore.SairaMedicalStoreApplication;
import com.example.android.sairamedicalstore.models.DefaultKeyValuePair;
import com.example.android.sairamedicalstore.models.Medicine;
import com.example.android.sairamedicalstore.models.User;
import com.example.android.sairamedicalstore.ui.medicine.ProductDetailsActivity;

import java.util.ArrayList;
import java.util.Map;

import static com.example.android.sairamedicalstore.ui.order.OrderDetailsActivity.mCurrentCart;
import static com.example.android.sairamedicalstore.ui.order.OrderDetailsActivity.mCurrentOrder;
import static com.example.android.sairamedicalstore.ui.order.OrderDetailsActivity.mHashMapIndividualProductPricing;

/**
 * Created by chandan on 12-08-2017.
 */

class ProductDetailsAdapter extends RecyclerView.Adapter<ProductDetailsAdapter.ViewHolder> {

    Activity mActivity;
    User mCurrentUser;
    ArrayList<Medicine> mArrayListAllProductsInCart;
    ArrayList<DefaultKeyValuePair> mArrayListDefaultMedicinePics;

    public ProductDetailsAdapter(ArrayList<Medicine> arrayListAllProductsInCart, Activity activity) {
        this.mArrayListAllProductsInCart = arrayListAllProductsInCart;
        this.mActivity = activity;
        mCurrentUser = ((SairaMedicalStoreApplication) mActivity.getApplication()).getCurrentUser();
        mArrayListDefaultMedicinePics = ((SairaMedicalStoreApplication) mActivity.getApplication()).getArrayListDefaultMedicinePics();

    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public LinearLayout mLinearLayoutOrderedProduct;
        public ImageView mImageViewProductImage;
        public TextView mTextViewProductName;
        public TextView mTextViewSelectedQuantity;
        public TextView mTextViewPriceForSelectedQuantity;

        public ViewHolder(View itemView) {
            super(itemView);
            mLinearLayoutOrderedProduct = (LinearLayout) itemView.findViewById(R.id.linear_layout_ordered_product);
            mImageViewProductImage = (ImageView) itemView.findViewById(R.id.image_view_product_image);
            mTextViewProductName = (TextView) itemView.findViewById(R.id.text_view_product_name);
            mTextViewSelectedQuantity = (TextView) itemView.findViewById(R.id.text_view_product_selected_quantity);
            mTextViewPriceForSelectedQuantity = (TextView) itemView.findViewById(R.id.text_view_product_price_for_selected_quantity);

        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ProductDetailsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                               int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycle_item_for_ordered_product, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ProductDetailsAdapter.ViewHolder holder, final int position) {

        Glide.with(holder.mImageViewProductImage.getContext())
                .load(getImageUrlToDisplay(mArrayListAllProductsInCart.get(position)))
                .into(holder.mImageViewProductImage);
        holder.mTextViewProductName.setText(mArrayListAllProductsInCart.get(position).getMedicineName());
        holder.mTextViewSelectedQuantity.setText("Quantity: "+setSelectedQuantity(mArrayListAllProductsInCart.get(position).getMedicineId()));
        holder.mTextViewPriceForSelectedQuantity.setText(getPricing(mArrayListAllProductsInCart.get(position)));
        holder.mLinearLayoutOrderedProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentToProductDetails = new Intent(mActivity, ProductDetailsActivity.class);
                intentToProductDetails.putExtra("medicineId", mArrayListAllProductsInCart.get(position).getMedicineId());
                mActivity.startActivity(intentToProductDetails);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mArrayListAllProductsInCart.size();
    }


    private String getPricing(final Medicine currentMedicine)
    {
        int itemsInOnePack = currentMedicine.getNoOfItemsInOnePack();
        final double pricePerSingleItem = currentMedicine.getPricePerPack() / itemsInOnePack ;

        int noOfItemsSelected = setSelectedQuantity(currentMedicine.getMedicineId());
        if(noOfItemsSelected == 0)
            noOfItemsSelected = itemsInOnePack;

        if(mCurrentOrder.getOrderId() != null)
        {
            for (Map.Entry<String, Double> eachEntry : mCurrentOrder.getIndividualProductPricing().entrySet())
            {
                if(eachEntry.getKey().equals(currentMedicine.getMedicineId()))
                    return "RS. "+  Double.toString(eachEntry.getValue());
            }
        }
        double priceForSelectedNoOfItems = (double) Math.round( (double)noOfItemsSelected * pricePerSingleItem *100) / 100;

        mHashMapIndividualProductPricing.put(currentMedicine.getMedicineId(),priceForSelectedNoOfItems);
        return "RS. "+  Double.toString(priceForSelectedNoOfItems);

    }

    public String getImageUrlToDisplay(Medicine medicine)
    {
        String imageUrl = medicine.getMedicineImageUrl();
        String medicineType = medicine.getMedicineType();
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


    public int setSelectedQuantity(String productId)
    {
        if(mCurrentCart != null && mCurrentCart.getProductIdAndItemCount() != null)
            for (Map.Entry<String, Integer> eachEntry : mCurrentCart.getProductIdAndItemCount().entrySet())
                if(eachEntry.getKey().equals(productId))
                    return eachEntry.getValue();

        return 0;
    }


}
