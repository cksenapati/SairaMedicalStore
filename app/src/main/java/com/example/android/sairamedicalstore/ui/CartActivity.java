package com.example.android.sairamedicalstore.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.sairamedicalstore.R;
import com.example.android.sairamedicalstore.SairaMedicalStoreApplication;
import com.example.android.sairamedicalstore.models.Cart;
import com.example.android.sairamedicalstore.models.Medicine;
import com.example.android.sairamedicalstore.models.User;
import com.example.android.sairamedicalstore.utils.Constants;
import com.example.android.sairamedicalstore.utils.Utils;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class CartActivity extends AppCompatActivity {

    Firebase mFirebaseCurrentCartRef,mFirebaseAllMedicinesRef;
    ProductsInCartAdapter productsInCartAdapter;


    ArrayList<Medicine> mArrayListCartProducts;
    int productCounter;
    User mCurrentUser;
    public static Cart mCurrentCart;
    static double subtotalPrice,shippingPrice;


    LinearLayout mLinearLayoutOffer,mLinearLayoutUploadPrescription;
    LinearLayout mLinearLayoutPriceDetails;

    EditText mEditTextPincode;
    TextView mTextViewCheckPincode,mTextViewPincodeMessage,mTextViewOffer,mTextViewPriceDetailsMessage,
            mTextViewProceedToDeliveryAddress,mTextViewEmptyCart,mTextViewGoNext,mTextViewPrescriptionUploaded;
    public static TextView mTextViewSubtotal,mTextViewShippingCharges,mTextViewOrderTotal ;

    ImageView mImageViewCloseOffer;
    ListView mListViewCartProducts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        mCurrentUser = ((SairaMedicalStoreApplication) this.getApplication()).getCurrentUser();

        initializeScreen();
        getCartObject();

        mLinearLayoutUploadPrescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent prescriptionActivityIntent = new Intent(CartActivity.this,PrescriptionsActivity.class);
                startActivity(prescriptionActivityIntent);
            }
        });
    }

    private void initializeScreen()
    {
        mListViewCartProducts = (ListView) findViewById(R.id.list_view_cart_products);

        View header = getLayoutInflater().inflate(R.layout.cart_header, null);
        View footer = getLayoutInflater().inflate(R.layout.cart_footer, null);
        mListViewCartProducts.addHeaderView(header);
        mListViewCartProducts.addFooterView(footer);


        mLinearLayoutOffer = (LinearLayout) header.findViewById(R.id.linear_layout_offer);
        mLinearLayoutPriceDetails = (LinearLayout) footer.findViewById(R.id.linear_layout_price_details);
        mLinearLayoutUploadPrescription = (LinearLayout) footer.findViewById(R.id.linear_layout_upload_prescription);

        mEditTextPincode = (EditText) header.findViewById(R.id.edit_text_pincode);

        mTextViewCheckPincode = (TextView) header.findViewById(R.id.text_view_check_pincode);
        mTextViewPincodeMessage = (TextView) header.findViewById(R.id.text_view_pincode_message);
        mTextViewOffer = (TextView) header.findViewById(R.id.text_view_offer);
        mTextViewSubtotal = (TextView)  footer.findViewById(R.id.text_view_subtotal);
        mTextViewShippingCharges = (TextView) footer.findViewById(R.id.text_view_shipping_charges);
        mTextViewOrderTotal = (TextView) footer.findViewById(R.id.text_view_order_total);
        mTextViewProceedToDeliveryAddress = (TextView) findViewById(R.id.text_view_proceed_to_delivery_address);
        mTextViewEmptyCart = (TextView) findViewById(R.id.text_view_empty_cart);
        mTextViewPriceDetailsMessage = (TextView) footer.findViewById(R.id.text_view_price_details_message);
        mTextViewGoNext = (TextView) footer.findViewById(R.id.text_view_go_next);
        mTextViewPrescriptionUploaded = (TextView) footer.findViewById(R.id.text_view_prescription_uploaded);

        mImageViewCloseOffer = (ImageView) findViewById(R.id.image_view_close_offer);

        mFirebaseCurrentCartRef = new Firebase(Constants.FIREBASE_URL_SAIRA_All_CARTS).child(Utils.encodeEmail(mCurrentUser.getEmail()));
        mFirebaseAllMedicinesRef = new Firebase(Constants.FIREBASE_URL_SAIRA_ALL_MEDICINES);

        mArrayListCartProducts = new ArrayList<>();
        subtotalPrice = 0;
        shippingPrice= 49.0 ;
    }

    public void getCartObject()
    {

        mFirebaseCurrentCartRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    if (productsInCartAdapter != null) {
                        productsInCartAdapter.clear();
                    }
                    mArrayListCartProducts.clear();
                    mLinearLayoutUploadPrescription.setVisibility(View.GONE);
                    subtotalPrice = 0;
                    productCounter = 0;

                    mCurrentCart = dataSnapshot.getValue(Cart.class);
                    if(mCurrentCart != null)
                    {
                        if(mCurrentCart.getProductIdAndItemCount() != null)
                        {
                            for (final Map.Entry<String, Integer> eachEntry : mCurrentCart.getProductIdAndItemCount().entrySet())
                            {
                                Firebase firebaseEachMedicineRef = mFirebaseAllMedicinesRef.child(eachEntry.getKey());
                                firebaseEachMedicineRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        productCounter++;
                                        if(dataSnapshot.exists())
                                        {
                                            Medicine eachMedicine = dataSnapshot.getValue(Medicine.class);
                                            if(eachMedicine != null) {
                                                mArrayListCartProducts.add(eachMedicine);
                                                if(!eachMedicine.isMedicineAvailability())
                                                {
                                                    int colorCode = CartActivity.this.getResources().getColor(R.color.tw__composer_red);
                                                    mLinearLayoutPriceDetails.setVisibility(View.GONE);
                                                    mTextViewPriceDetailsMessage.setVisibility(View.VISIBLE);
                                                    mTextViewPriceDetailsMessage.setTextColor(colorCode);
                                                }
                                                if(mLinearLayoutUploadPrescription.getVisibility() == View.GONE && eachMedicine.isMedicineAvailability() && eachMedicine.getMedicineCategory().equals(Constants.MEDICINE_CATEGORY_PRESCRIPTION))
                                                {
                                                    mLinearLayoutUploadPrescription.setVisibility(View.VISIBLE);
                                                }
                                            }
                                        }

                                        if(mCurrentCart.getNoOfUniqueProductsInCart() == productCounter)
                                        {
                                            productsInCartAdapter = new ProductsInCartAdapter(CartActivity.this, mArrayListCartProducts);
                                            mListViewCartProducts.setAdapter(productsInCartAdapter);

                                            mListViewCartProducts.setVisibility(View.VISIBLE);
                                            mTextViewEmptyCart.setVisibility(View.GONE);

                                        }
                                    }

                                    @Override
                                    public void onCancelled(FirebaseError firebaseError) {
                                        mListViewCartProducts.setVisibility(View.GONE);
                                        mTextViewEmptyCart.setVisibility(View.VISIBLE);
                                    }
                                });
                            }


                        }
                        else
                        {
                            mListViewCartProducts.setVisibility(View.GONE);
                            mTextViewEmptyCart.setVisibility(View.VISIBLE);
                        }

                    }
                    else
                    {
                        mListViewCartProducts.setVisibility(View.GONE);
                        mTextViewEmptyCart.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                mListViewCartProducts.setVisibility(View.GONE);
                mTextViewEmptyCart.setVisibility(View.VISIBLE);
            }
        });
    }



}
