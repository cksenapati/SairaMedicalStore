package com.example.android.sairamedicalstore.models;

import java.util.HashMap;

/**
 * Created by chandan on 12-10-2017.
 */

public class Cart {
    int noOfUniqueProductsInCart;
    private HashMap<String, Integer> productIdAndItemCount;

    public Cart() {
    }

    public Cart(int noOfUniqueProductsInCart, HashMap<String, Integer> productIdAndItemCount) {
        this.noOfUniqueProductsInCart = noOfUniqueProductsInCart;
        this.productIdAndItemCount = productIdAndItemCount;
    }

    public int getNoOfUniqueProductsInCart() {
        return noOfUniqueProductsInCart;
    }

    public void setNoOfUniqueProductsInCart(int noOfUniqueProductsInCart) {
        this.noOfUniqueProductsInCart = noOfUniqueProductsInCart;
    }

    public HashMap<String, Integer> getProductIdAndItemCount() {
        return productIdAndItemCount;
    }

    public void setProductIdAndItemCount(HashMap<String, Integer> productIdAndItemCount) {
        this.productIdAndItemCount = productIdAndItemCount;
    }
}
