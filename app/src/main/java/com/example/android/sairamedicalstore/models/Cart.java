package com.example.android.sairamedicalstore.models;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by chandan on 12-10-2017.
 */

public class Cart implements Serializable {
    int noOfUniqueProductsInCart;
    private HashMap<String,Prescription> cartPrescriptions;
    private HashMap<String, Integer> productIdAndItemCount;

    public Cart() {
    }

    public Cart(int noOfUniqueProductsInCart,HashMap<String, Prescription> cartPrescriptions, HashMap<String, Integer> productIdAndItemCount) {
        this.noOfUniqueProductsInCart = noOfUniqueProductsInCart;
        this.productIdAndItemCount = productIdAndItemCount;
        this.cartPrescriptions = cartPrescriptions;
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

    public HashMap<String, Prescription> getCartPrescriptions() {
        return cartPrescriptions;
    }

    public void setCartPrescriptions(HashMap<String, Prescription> cartPrescriptions) {
        this.cartPrescriptions = cartPrescriptions;
    }
}
