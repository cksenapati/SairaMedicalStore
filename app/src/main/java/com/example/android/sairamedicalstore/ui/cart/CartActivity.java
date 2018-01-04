package com.example.android.sairamedicalstore.ui.cart;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.sairamedicalstore.R;
import com.example.android.sairamedicalstore.SairaMedicalStoreApplication;
import com.example.android.sairamedicalstore.models.Cart;
import com.example.android.sairamedicalstore.models.Medicine;
import com.example.android.sairamedicalstore.models.Order;
import com.example.android.sairamedicalstore.models.Prescription;
import com.example.android.sairamedicalstore.models.User;
import com.example.android.sairamedicalstore.ui.address.DeliveryAddressActivity;
import com.example.android.sairamedicalstore.ui.prescription.MyPrescriptionsActivity;
import com.example.android.sairamedicalstore.ui.prescription.PrescriptionsActivity;
import com.example.android.sairamedicalstore.utils.Constants;
import com.example.android.sairamedicalstore.utils.Utils;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CartActivity extends AppCompatActivity {

    Firebase mFirebaseCurrentCartRef,mFirebaseAllMedicinesRef;
    ProductsInCartAdapter productsInCartAdapter;


    ArrayList<Medicine> mArrayListCartProducts;
    ArrayList<Prescription> mArrayListSelectedPrescriptions;
    HashMap<String,Double> mHashMapOrderPricingDetails;

    int productCounter;
    User mCurrentUser;
    public static Cart mCurrentCart;
    static double subtotalPrice,shippingPrice,totalPayablePrice;
    private static final int RC_PRISCRIPTIONS_PICKER = 1;
    boolean isPrescriptionsAdded,isAllProductsInCartAvailable;
    Order mCurrentOrder;

    LinearLayout mLinearLayoutOffer,mLinearLayoutUploadPrescription;
    LinearLayout mLinearLayoutPriceDetails;

    EditText mEditTextPincode;
    TextView mTextViewCheckPincode,mTextViewPincodeMessage,mTextViewOffer,mTextViewPriceDetailsMessage,
            mTextViewProceedToDeliveryAddress,mTextViewEmptyCart,mTextViewGoNext,mTextViewPrescriptionUploaded;
    public static TextView mTextViewSubtotal,mTextViewShippingCharges,mTextViewOrderTotal ;

    ImageView mImageViewCloseOffer,mListViewGoBack;
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
                /*Intent prescriptionActivityIntent = new Intent(CartActivity.this,PrescriptionsActivity.class);
                prescriptionActivityIntent.putExtra("arrayListSelectedPrescriptionIds",mArrayListSelectedPrescriptionIds);
                startActivityForResult(Intent.createChooser(prescriptionActivityIntent, "Complete action using"), RC_PRISCRIPTIONS_PICKER);*/

                Intent myPrescriptionsActivityIntent = new Intent(CartActivity.this,MyPrescriptionsActivity.class);
                myPrescriptionsActivityIntent.putExtra("arrayListSelectedPrescriptions",mArrayListSelectedPrescriptions);
                myPrescriptionsActivityIntent.putExtra("activityVisitPurpose",Constants.ACTIVITY_VISIT_PURPOSE_SELECT);
                startActivityForResult(Intent.createChooser(myPrescriptionsActivityIntent, "Complete action using"), RC_PRISCRIPTIONS_PICKER);

            }
        });

        mTextViewProceedToDeliveryAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isAllProductsInCartAvailable)
                {
                    if(isPrescriptionsAdded)
                    {
                        mHashMapOrderPricingDetails.put("Subtotal",subtotalPrice);
                        mHashMapOrderPricingDetails.put("Shipping Charges",shippingPrice);
                        mHashMapOrderPricingDetails.put("COD Charges",0.0);

                        HashMap<String,Prescription> hashMapSelectedPrescriptions = new HashMap<String, Prescription>();
                        for (int count = 0; count<mArrayListSelectedPrescriptions.size(); count++)
                            hashMapSelectedPrescriptions.put("prescription"+(count+1),mArrayListSelectedPrescriptions.get(count));

                        mCurrentOrder = new Order(null,null,null,null,mCurrentUser.getEmail(),mCurrentCart,null,hashMapSelectedPrescriptions,null,null,null,mHashMapOrderPricingDetails,null,null,null);
                        Intent intentToDeliveryAddress = new Intent(CartActivity.this, DeliveryAddressActivity.class);
                        intentToDeliveryAddress.putExtra("currentOrder",mCurrentOrder);
                        startActivity(intentToDeliveryAddress);
                    }
                    else
                        Toast.makeText(CartActivity.this,"Please upload required prescriptions.",Toast.LENGTH_LONG).show();

                }
                else
                    Toast.makeText(CartActivity.this,"Remove the Out-Of-Stuck product(s) from cart",Toast.LENGTH_LONG).show();

            }
        });

        mListViewGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_PRISCRIPTIONS_PICKER) {
            if (resultCode == RESULT_OK) {
                mArrayListSelectedPrescriptions = (ArrayList<Prescription>) data.getSerializableExtra("arrayListSelectedPrescriptions");
                if (mArrayListSelectedPrescriptions != null && mArrayListSelectedPrescriptions.size() > 0) {
                    isPrescriptionsAdded = true;
                    mTextViewGoNext.setVisibility(View.GONE);
                    mTextViewPrescriptionUploaded.setVisibility(View.VISIBLE);
                }
                else {
                    isPrescriptionsAdded = false;
                    mTextViewGoNext.setVisibility(View.VISIBLE);
                    mTextViewPrescriptionUploaded.setVisibility(View.GONE);
                }

            } else if (resultCode == RESULT_CANCELED) {
                // Unable to pick this file
                // progressBar.setVisibility(View.GONE);
                Toast.makeText(this, "Data Unchanged.", Toast.LENGTH_SHORT).show();
            }
        }
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
        mListViewGoBack = (ImageView) findViewById(R.id.image_view_go_back);

        mFirebaseCurrentCartRef = new Firebase(Constants.FIREBASE_URL_SAIRA_All_CARTS).child(Utils.encodeEmail(mCurrentUser.getEmail()));
        mFirebaseAllMedicinesRef = new Firebase(Constants.FIREBASE_URL_SAIRA_ALL_MEDICINES);

        mArrayListCartProducts = new ArrayList<>();
        mHashMapOrderPricingDetails = new HashMap<>();

        subtotalPrice = 0;
        totalPayablePrice = 0;
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
                    isPrescriptionsAdded = true;
                    isAllProductsInCartAvailable = true;
                    subtotalPrice = 0;
                    productCounter = 0;
                    totalPayablePrice = 0;

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
                                                    isAllProductsInCartAvailable = false;
                                                }
                                                if(mLinearLayoutUploadPrescription.getVisibility() == View.GONE && eachMedicine.isMedicineAvailability() && eachMedicine.getMedicineCategory().equals(Constants.MEDICINE_CATEGORY_PRESCRIPTION))
                                                {
                                                    mLinearLayoutUploadPrescription.setVisibility(View.VISIBLE);
                                                    isPrescriptionsAdded = false;
                                                }
                                            }
                                        }

                                        if(mCurrentCart.getNoOfUniqueProductsInCart() == productCounter)
                                        {
                                            productsInCartAdapter = new ProductsInCartAdapter(CartActivity.this, mArrayListCartProducts);
                                            mListViewCartProducts.setAdapter(productsInCartAdapter);

                                            mListViewCartProducts.setVisibility(View.VISIBLE);
                                            mTextViewEmptyCart.setVisibility(View.GONE);
                                            mTextViewProceedToDeliveryAddress.setVisibility(View.VISIBLE);

                                        }
                                    }

                                    @Override
                                    public void onCancelled(FirebaseError firebaseError) {
                                        mListViewCartProducts.setVisibility(View.GONE);
                                        mTextViewEmptyCart.setVisibility(View.VISIBLE);
                                        mTextViewProceedToDeliveryAddress.setVisibility(View.GONE);
                                    }
                                });
                            }


                        }
                        else
                        {
                            mListViewCartProducts.setVisibility(View.GONE);
                            mTextViewEmptyCart.setVisibility(View.VISIBLE);
                            mTextViewProceedToDeliveryAddress.setVisibility(View.GONE);
                        }

                    }
                    else
                    {
                        mListViewCartProducts.setVisibility(View.GONE);
                        mTextViewEmptyCart.setVisibility(View.VISIBLE);
                        mTextViewProceedToDeliveryAddress.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                mListViewCartProducts.setVisibility(View.GONE);
                mTextViewEmptyCart.setVisibility(View.VISIBLE);
                mTextViewProceedToDeliveryAddress.setVisibility(View.GONE);
            }
        });
    }



}
