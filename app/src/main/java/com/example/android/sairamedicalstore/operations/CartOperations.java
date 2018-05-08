package com.example.android.sairamedicalstore.operations;

import android.app.Activity;
import android.widget.Toast;

import com.example.android.sairamedicalstore.SairaMedicalStoreApplication;
import com.example.android.sairamedicalstore.models.Cart;
import com.example.android.sairamedicalstore.models.Prescription;
import com.example.android.sairamedicalstore.models.User;
import com.example.android.sairamedicalstore.utils.Constants;
import com.example.android.sairamedicalstore.utils.Utils;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chandan on 12-10-2017.
 */

public class CartOperations {

    User mCurrentUser;
    Activity mActivity;
    Firebase firebaseCurrentCartRef;

    public CartOperations( Activity mActivity) {
        this.mActivity = mActivity;
        this.mCurrentUser = ((SairaMedicalStoreApplication) mActivity.getApplication()).getCurrentUser();
        firebaseCurrentCartRef = new Firebase(Constants.FIREBASE_URL_SAIRA_All_CARTS).child(Utils.encodeEmail(mCurrentUser.getEmail()));
    }

    public void AddNewProductToCart(final String productId,final int itemCount)
    {
        String encodedEmail = Utils.encodeEmail(mCurrentUser.getEmail());

        firebaseCurrentCartRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    Cart currentCart = dataSnapshot.getValue(Cart.class);
                    if(currentCart != null)
                    {
                        if(currentCart.getProductIdAndItemCount() != null)
                        {
                            boolean isProductAlreadyInCart = false;
                            for (Map.Entry<String, Integer> eachProduct : currentCart.getProductIdAndItemCount().entrySet())
                            {
                                if(eachProduct.getKey().equals(productId)) {
                                    isProductAlreadyInCart = true;
                                    break;
                                }
                            }
                            if(!isProductAlreadyInCart)
                            {
                                currentCart.setNoOfUniqueProductsInCart(currentCart.getNoOfUniqueProductsInCart() + 1);

                                HashMap<String, Integer> currentProductIdAndItemCount = currentCart.getProductIdAndItemCount();
                                currentProductIdAndItemCount.put(productId,itemCount);
                                currentCart.setProductIdAndItemCount(currentProductIdAndItemCount);

                            }
                        }
                        else
                        {
                            currentCart.setNoOfUniqueProductsInCart(currentCart.getNoOfUniqueProductsInCart() + 1);

                            HashMap<String, Integer> currentProductIdAndItemCount = new HashMap<>();
                            currentProductIdAndItemCount.put(productId,itemCount);
                            currentCart.setProductIdAndItemCount(currentProductIdAndItemCount);
                        }

                        try{
                            firebaseCurrentCartRef.setValue(currentCart);
                            Toast.makeText(mActivity, "Product added to your cart" , Toast.LENGTH_SHORT).show();
                        }catch (Exception ex)
                        {
                            Toast.makeText(mActivity, "failed to add product to cart..try again" , Toast.LENGTH_SHORT).show();
                        }

                    }
                }
                else
                {

                    HashMap<String, Integer> currentProductIdAndItemCount = new HashMap<>();
                    currentProductIdAndItemCount.put(productId,itemCount);

                    Cart newCart = new Cart(1,null,currentProductIdAndItemCount);

                    try {
                        firebaseCurrentCartRef.setValue(newCart);
                        Toast.makeText(mActivity, "Product added to your cart" , Toast.LENGTH_SHORT).show();
                    }
                    catch (Exception ex)
                    {
                        Toast.makeText(mActivity, "failed to add product to cart..try again" , Toast.LENGTH_SHORT).show();
                    }

                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Toast.makeText(mActivity, "failed to add product to cart..try again" , Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void updateCart(final Cart cartToBeUpdated)
    {
        firebaseCurrentCartRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try{
                    firebaseCurrentCartRef.setValue(cartToBeUpdated);
                }catch (Exception Ex){}
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

}
