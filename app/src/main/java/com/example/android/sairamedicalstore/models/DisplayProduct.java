package com.example.android.sairamedicalstore.models;

/**
 * Created by chandan on 15-08-2017.
 */

public class DisplayProduct {
    private String productId,productName,productImageUrl,productType;
    private double pricePerPack;

    public DisplayProduct() {
    }

    public DisplayProduct(String productId, String productName, String productImageUrl, String productType, double pricePerPack) {
        this.productId = productId;
        this.productName = productName;
        this.productImageUrl = productImageUrl;
        this.productType = productType;
        this.pricePerPack = pricePerPack;
    }

    //Setter

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setProductImageUrl(String productImageUrl) {
        this.productImageUrl = productImageUrl;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public void setPricePerPack(double pricePerPack) {
        this.pricePerPack = pricePerPack;
    }


    //getter


    public String getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductImageUrl() {
        return productImageUrl;
    }

    public String getProductType() {
        return productType;
    }

    public double getPricePerPack() {
        return pricePerPack;
    }
}
