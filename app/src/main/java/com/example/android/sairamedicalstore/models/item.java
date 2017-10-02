package com.example.android.sairamedicalstore.models;

/**
 * Created by chandan on 12-08-2017.
 */

public class item {
    private String mImageUrl;
    private String mItemName;
    private String mItemPrice;

    public item( String mItemName, String mImageUrl,String mItemPrice) {
        this.mImageUrl = mImageUrl;
        this.mItemName = mItemName;
        this.mItemPrice = mItemPrice;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public String getItemName() {
        return mItemName;
    }

    public String getItemPrice() {
        return mItemPrice;
    }
}
