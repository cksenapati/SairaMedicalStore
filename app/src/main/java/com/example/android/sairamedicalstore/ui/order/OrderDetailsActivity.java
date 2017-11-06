package com.example.android.sairamedicalstore.ui.order;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.sairamedicalstore.R;
import com.example.android.sairamedicalstore.SairaMedicalStoreApplication;
import com.example.android.sairamedicalstore.models.Address;
import com.example.android.sairamedicalstore.models.Cart;
import com.example.android.sairamedicalstore.models.Medicine;
import com.example.android.sairamedicalstore.models.Order;
import com.example.android.sairamedicalstore.models.OrderCancellationDetails;
import com.example.android.sairamedicalstore.models.OrderReturnDetails;
import com.example.android.sairamedicalstore.models.Prescription;
import com.example.android.sairamedicalstore.models.User;
import com.example.android.sairamedicalstore.operations.OrderOperations;
import com.example.android.sairamedicalstore.ui.address.DeliveryAddressActivity;
import com.example.android.sairamedicalstore.utils.Constants;
import com.example.android.sairamedicalstore.utils.Utils;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ServerValue;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;



public class OrderDetailsActivity extends AppCompatActivity {

    Firebase mFirebaseCurrentCartRef,mFirebaseAllMedicinesRef,mFirebaseCurrentOrderRef;

    RecyclerView.Adapter recyclerViewAdapter;
    RecyclerView.Adapter recyclerViewAdapterForProducts;


    TextView mTextViewToolbar, mTextViewAction,mTextViewPriceDetailsMessage,mTextViewPaymentMethodMessage,mTextViewPageMessage, mTextViewAddressDetails,mTextViewEditAddress;
    TextView mTextViewSubtotal,mTextViewCodCharges,mTextViewShippingCharges,mTextViewOrderTotal;

    ImageView mImageViewShowOrHideCartProducts, mImageViewShowOrHidePriceDetails, mImageViewShowOrHideDeliveryAddress,mImageViewShowOrHidePrescriptionDetails,
            mImageViewShowOrHidePaymentMethods, mImageViewSelectOrDeselectPrepaid, mImageViewSelectOrDeselectCod;

    LinearLayout mLinearLayoutProductDetailsActonBar,mLinearLayoutPriceDetailsActonBar,mLinearLayoutDeliveryAddressActonBar,
            mLinearLayoutPaymentMethodsActonBar,mLinearLayoutPrescriptionDetailsActonBar,
            mLinearLayoutProductDetails,mLinearLayoutPriceDetails,mLinearLayoutDeliveryAddress,mLinearLayoutPaymentMethods,mLinearLayoutPrescriptionDetails,
            mLinearLayoutCodCharges,mLinearLayoutShippingCharges,
            mLinearLayoutPrepaidPaymentMethod,mLinearLayoutCodPaymentMethod;
    ListView mListViewCartProducts;
    RecyclerView recyclerView;


    double CodCharges;
    int productCounter;
    public static Cart mCurrentCart;
    String mSelectedPaymentMethod;
    public static Order mCurrentOrder;
    Address mCurrentDeliveryAddress;
    User mCurrentUser;
    ArrayList<Medicine> mArrayListCartProducts;
    ArrayList<Prescription> mArrayListSelectedPrescriptions;
    public static HashMap<String,Double> mHashMapIndividualProductPricing;
    public static Dialog reasonOfReturnOrCancelDialog;
    int selectedItemPositionOfSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        Intent intent = getIntent();
        if (intent != null) {
            mCurrentOrder = (Order) intent.getSerializableExtra("currentOrder");
        }

        mCurrentUser = ((SairaMedicalStoreApplication) this.getApplication()).getCurrentUser();

        initialization();
        updateOrderDetails();

        mTextViewAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onActionClick();
            }
        });

    }

    public void initialization()
    {
        mTextViewToolbar = (TextView) findViewById(R.id.text_view_toolbar);
        mTextViewAction = (TextView) findViewById(R.id.text_view_action);
        mTextViewSubtotal = (TextView) findViewById(R.id.text_view_subtotal);
        mTextViewShippingCharges = (TextView) findViewById(R.id.text_view_shipping_charges);
        mTextViewOrderTotal = (TextView) findViewById(R.id.text_view_order_total);
        mTextViewAddressDetails = (TextView) findViewById(R.id.text_view_address_details);
        mTextViewEditAddress = (TextView) findViewById(R.id.text_view_edit_address);
        mImageViewSelectOrDeselectPrepaid = (ImageView) findViewById(R.id.image_view_select_or_deselect_prepaid);
        mImageViewSelectOrDeselectCod = (ImageView) findViewById(R.id.image_view_select_or_deselect_cod);
        mTextViewPaymentMethodMessage = (TextView) findViewById(R.id.text_view_payment_method_message);
        mTextViewPriceDetailsMessage = (TextView) findViewById(R.id.text_view_price_details_message);
        mTextViewPageMessage = (TextView) findViewById(R.id.text_view_page_message);
        mTextViewCodCharges = (TextView) findViewById(R.id.text_view_cod_charges);

        mImageViewShowOrHideCartProducts = (ImageView) findViewById(R.id.image_view_show_or_hide_cart_products);
        mImageViewShowOrHidePriceDetails = (ImageView) findViewById(R.id.image_view_show_or_hide_price_details);
        mImageViewShowOrHideDeliveryAddress = (ImageView) findViewById(R.id.image_view_show_or_hide_delivery_address);
        mImageViewShowOrHidePaymentMethods = (ImageView) findViewById(R.id.image_view_show_or_hide_payment_methods);
        mImageViewShowOrHidePrescriptionDetails = (ImageView) findViewById(R.id.image_view_show_or_hide_prescription_details);

        mLinearLayoutProductDetailsActonBar = (LinearLayout) findViewById(R.id.linear_layout_product_details_action_bar);
        mLinearLayoutPriceDetailsActonBar  = (LinearLayout) findViewById(R.id.linear_layout_price_details_action_bar);
        mLinearLayoutDeliveryAddressActonBar  = (LinearLayout) findViewById(R.id.linear_layout_delivery_address_action_bar);
        mLinearLayoutPaymentMethodsActonBar  = (LinearLayout) findViewById(R.id.linear_layout_payment_methods_action_bar);
        mLinearLayoutPrescriptionDetailsActonBar = (LinearLayout) findViewById(R.id.linear_layout_prescription_details_action_bar);

        mLinearLayoutProductDetails = (LinearLayout) findViewById(R.id.linear_layout_product_details);
        mLinearLayoutPriceDetails  = (LinearLayout) findViewById(R.id.linear_layout_price_details);
        mLinearLayoutDeliveryAddress  = (LinearLayout) findViewById(R.id.linear_layout_delivery_address);
        mLinearLayoutPaymentMethods  = (LinearLayout) findViewById(R.id.linear_layout_payment_methods);
        mLinearLayoutPrescriptionDetails  = (LinearLayout) findViewById(R.id.linear_layout_prescription_details);
        mLinearLayoutCodCharges = (LinearLayout) findViewById(R.id.linear_layout_cod_charges);
        mLinearLayoutShippingCharges = (LinearLayout) findViewById(R.id.linear_layout_shipping_charges);

        mLinearLayoutPrepaidPaymentMethod  = (LinearLayout) findViewById(R.id.linear_layout_prepaid_payment_method);
        mLinearLayoutCodPaymentMethod  = (LinearLayout) findViewById(R.id.linear_layout_cod_payment_method);


        mListViewCartProducts = (ListView) findViewById(R.id.list_view_cart_products);

        mFirebaseCurrentCartRef = new Firebase(Constants.FIREBASE_URL_SAIRA_All_CARTS).child(Utils.encodeEmail(mCurrentUser.getEmail()));
        mFirebaseAllMedicinesRef = new Firebase(Constants.FIREBASE_URL_SAIRA_ALL_MEDICINES);

        mArrayListCartProducts = new ArrayList<>();
        mArrayListSelectedPrescriptions = new ArrayList<>();
        mHashMapIndividualProductPricing = new HashMap<>();

        CodCharges = 0;
    }


    public void setVisibilityOfLinearLayoutAndAdjustArrowMark(View layout, int visibility,View imageView)
    {
        layout.setVisibility(visibility);

        if(visibility == View.GONE)
            ((ImageView) imageView).setImageResource(R.drawable.ic_down_arrow_24);
        else
            ((ImageView) imageView).setImageResource(R.drawable.ic_up_arrow_24);

    }

    public void setActionBarBehaviour()
    {
        mLinearLayoutProductDetailsActonBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mLinearLayoutProductDetails.getVisibility() == View.VISIBLE)
                    setVisibilityOfLinearLayoutAndAdjustArrowMark(mLinearLayoutProductDetails,View.GONE,mImageViewShowOrHideCartProducts);
                else
                {
                    setVisibilityOfLinearLayoutAndAdjustArrowMark(mLinearLayoutProductDetails,View.VISIBLE,mImageViewShowOrHideCartProducts);

                    setVisibilityOfLinearLayoutAndAdjustArrowMark(mLinearLayoutPriceDetails,View.GONE,mImageViewShowOrHidePriceDetails);
                    mTextViewPriceDetailsMessage.setVisibility(View.GONE);

                    setVisibilityOfLinearLayoutAndAdjustArrowMark(mLinearLayoutPrescriptionDetails,View.GONE,mImageViewShowOrHidePrescriptionDetails);
                    setVisibilityOfLinearLayoutAndAdjustArrowMark(mLinearLayoutDeliveryAddress,View.GONE,mImageViewShowOrHideDeliveryAddress);
                    setVisibilityOfLinearLayoutAndAdjustArrowMark(mLinearLayoutPaymentMethods,View.GONE,mImageViewShowOrHidePaymentMethods);
                }
            }
        });

        mLinearLayoutPriceDetailsActonBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mLinearLayoutPriceDetails.getVisibility() == View.VISIBLE)
                    setVisibilityOfLinearLayoutAndAdjustArrowMark(mLinearLayoutPriceDetails,View.GONE,mImageViewShowOrHidePriceDetails);
                else
                {
                    setVisibilityOfLinearLayoutAndAdjustArrowMark(mLinearLayoutProductDetails,View.GONE,mImageViewShowOrHideCartProducts);

                        setVisibilityOfLinearLayoutAndAdjustArrowMark(mLinearLayoutPriceDetails,View.VISIBLE,mImageViewShowOrHidePriceDetails);
                        mTextViewPriceDetailsMessage.setVisibility(View.GONE);


                    setVisibilityOfLinearLayoutAndAdjustArrowMark(mLinearLayoutPrescriptionDetails,View.GONE,mImageViewShowOrHidePrescriptionDetails);
                    setVisibilityOfLinearLayoutAndAdjustArrowMark(mLinearLayoutDeliveryAddress,View.GONE,mImageViewShowOrHideDeliveryAddress);
                    setVisibilityOfLinearLayoutAndAdjustArrowMark(mLinearLayoutPaymentMethods,View.GONE,mImageViewShowOrHidePaymentMethods);

                    if(mCurrentOrder.getOrderId() == null)
                      getPricingDetails();
                    else
                       setPriceDetails();
                }
            }
        });


        mLinearLayoutPrescriptionDetailsActonBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mLinearLayoutPrescriptionDetails.getVisibility() == View.VISIBLE)
                    setVisibilityOfLinearLayoutAndAdjustArrowMark(mLinearLayoutPrescriptionDetails,View.GONE,mImageViewShowOrHidePrescriptionDetails);
                else
                {
                    setVisibilityOfLinearLayoutAndAdjustArrowMark(mLinearLayoutProductDetails,View.GONE,mImageViewShowOrHideCartProducts);
                    setVisibilityOfLinearLayoutAndAdjustArrowMark(mLinearLayoutPriceDetails,View.GONE,mImageViewShowOrHidePriceDetails);
                    setVisibilityOfLinearLayoutAndAdjustArrowMark(mLinearLayoutPrescriptionDetails,View.VISIBLE,mImageViewShowOrHidePrescriptionDetails);
                    setVisibilityOfLinearLayoutAndAdjustArrowMark(mLinearLayoutDeliveryAddress,View.GONE,mImageViewShowOrHideDeliveryAddress);
                    setVisibilityOfLinearLayoutAndAdjustArrowMark(mLinearLayoutPaymentMethods,View.GONE,mImageViewShowOrHidePaymentMethods);

                    mTextViewPriceDetailsMessage.setVisibility(View.GONE);
                }
            }
        });


        mLinearLayoutDeliveryAddressActonBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mLinearLayoutDeliveryAddress.getVisibility() == View.VISIBLE)
                    setVisibilityOfLinearLayoutAndAdjustArrowMark(mLinearLayoutDeliveryAddress,View.GONE,mImageViewShowOrHideDeliveryAddress);
                else
                {
                    setVisibilityOfLinearLayoutAndAdjustArrowMark(mLinearLayoutProductDetails,View.GONE,mImageViewShowOrHideCartProducts);
                    setVisibilityOfLinearLayoutAndAdjustArrowMark(mLinearLayoutPriceDetails,View.GONE,mImageViewShowOrHidePriceDetails);
                    setVisibilityOfLinearLayoutAndAdjustArrowMark(mLinearLayoutPrescriptionDetails,View.GONE,mImageViewShowOrHidePrescriptionDetails);
                    setVisibilityOfLinearLayoutAndAdjustArrowMark(mLinearLayoutDeliveryAddress,View.VISIBLE,mImageViewShowOrHideDeliveryAddress);
                    setVisibilityOfLinearLayoutAndAdjustArrowMark(mLinearLayoutPaymentMethods,View.GONE,mImageViewShowOrHidePaymentMethods);

                    mTextViewPriceDetailsMessage.setVisibility(View.GONE);

                    mCurrentDeliveryAddress = mCurrentOrder.getOrderDeliveryAddress();
                    mTextViewAddressDetails.setText(mCurrentDeliveryAddress.getFullName()+"\n"+mCurrentDeliveryAddress.getAddress()+"\n"+mCurrentDeliveryAddress.getLandmark() +
                            "\n"+mCurrentDeliveryAddress.getCity() + " - " + mCurrentDeliveryAddress.getPinCode() + "\n" + mCurrentDeliveryAddress.getState() + ", India \n" + mCurrentDeliveryAddress.getPhoneNumber() );
                }
            }
        });

        mLinearLayoutPaymentMethodsActonBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mLinearLayoutPaymentMethods.getVisibility() == View.VISIBLE)
                    setVisibilityOfLinearLayoutAndAdjustArrowMark(mLinearLayoutPaymentMethods,View.GONE,mImageViewShowOrHidePaymentMethods);
                else
                {
                    setVisibilityOfLinearLayoutAndAdjustArrowMark(mLinearLayoutProductDetails,View.GONE,mImageViewShowOrHideCartProducts);
                    setVisibilityOfLinearLayoutAndAdjustArrowMark(mLinearLayoutPriceDetails,View.GONE,mImageViewShowOrHidePriceDetails);
                    setVisibilityOfLinearLayoutAndAdjustArrowMark(mLinearLayoutPrescriptionDetails,View.GONE,mImageViewShowOrHidePrescriptionDetails);
                    setVisibilityOfLinearLayoutAndAdjustArrowMark(mLinearLayoutDeliveryAddress,View.GONE,mImageViewShowOrHideDeliveryAddress);
                    setVisibilityOfLinearLayoutAndAdjustArrowMark(mLinearLayoutPaymentMethods,View.VISIBLE,mImageViewShowOrHidePaymentMethods);

                    mTextViewPriceDetailsMessage.setVisibility(View.GONE);
                }
            }
        });

    }

    public void setActionBarBehaviourForYetToBePlacedOrder()
    {
        setActionBarBehaviour();
        mLinearLayoutPrepaidPaymentMethod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mSelectedPaymentMethod == null || mSelectedPaymentMethod.equals("COD"))
                {
                    mSelectedPaymentMethod = "PREPAID";
                    mImageViewSelectOrDeselectPrepaid.setImageResource(R.drawable.ic_round_checked_24);
                    mImageViewSelectOrDeselectCod.setImageResource(R.drawable.ic_round_check_24);
                }
                else
                {
                    mSelectedPaymentMethod = null;
                    mImageViewSelectOrDeselectPrepaid.setImageResource(R.drawable.ic_round_check_24);
                    mImageViewSelectOrDeselectCod.setImageResource(R.drawable.ic_round_check_24);

                }
                CodCharges = 0;
                mTextViewPaymentMethodMessage.setVisibility(View.GONE);
                getPricingDetails();
            }
        });

        mLinearLayoutCodPaymentMethod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mSelectedPaymentMethod == null || mSelectedPaymentMethod.equals("PREPAID"))
                {
                    mSelectedPaymentMethod = "COD";
                    CodCharges = 49.0;
                    mTextViewPaymentMethodMessage.setVisibility(View.VISIBLE);
                    mTextViewPaymentMethodMessage.setText("COD Charge is RS. "+Double.toString(CodCharges)+" for any order value");
                    mImageViewSelectOrDeselectPrepaid.setImageResource(R.drawable.ic_round_check_24);
                    mImageViewSelectOrDeselectCod.setImageResource(R.drawable.ic_round_checked_24);
                }
                else
                {
                    mSelectedPaymentMethod = null;

                    CodCharges = 0;
                    mTextViewPaymentMethodMessage.setVisibility(View.GONE);
                    mImageViewSelectOrDeselectPrepaid.setImageResource(R.drawable.ic_round_check_24);
                    mImageViewSelectOrDeselectCod.setImageResource(R.drawable.ic_round_check_24);

                }
                getPricingDetails();
            }
        });

        mTextViewEditAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentToDeliveryAddress = new Intent(OrderDetailsActivity.this,DeliveryAddressActivity.class);
                intentToDeliveryAddress.putExtra("currentOrder",mCurrentOrder);
                startActivity(intentToDeliveryAddress);
            }
        });
    }

    public void setActionBarBehaviourForAlreadyPlacedOrder()
    {
        setActionBarBehaviour();
        mLinearLayoutPrepaidPaymentMethod.setVisibility(View.GONE);
        mLinearLayoutCodPaymentMethod.setVisibility(View.GONE);
        mTextViewEditAddress.setVisibility(View.GONE);
    }

    public void updateOrderDetails()
    {
        if(mCurrentOrder.getOrderId() == null)
            getYetToBePlacedOrderDetails();
        else
            getAlreadyPlacedOrderDetails();
    }

    public void getYetToBePlacedOrderDetails()
    {
        getProductDetails();
        setActionBarBehaviourForYetToBePlacedOrder();
        getPrescriptionsRecyclerView();
        setPageMessage();
        setActionTextViewText();
    }

    public void getAlreadyPlacedOrderDetails()
    {
        mFirebaseCurrentOrderRef = new Firebase(Constants.FIREBASE_URL_SAIRA_All_ORDERS).child(Utils.encodeEmail(mCurrentUser.getEmail())).child(mCurrentOrder.getOrderId());
        mFirebaseCurrentOrderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    mCurrentOrder = dataSnapshot.getValue(Order.class);
                    if(mCurrentOrder != null)
                    {
                        getProductDetails();
                        setActionBarBehaviourForAlreadyPlacedOrder();
                        getPrescriptionsRecyclerView();
                        getPaymentDetailsForPlacedOrder();
                        setPageMessage();
                        setActionTextViewText();
                    }
                    else
                        Toast.makeText(OrderDetailsActivity.this,"Something went wrong.",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void getProductDetails()
    {
        mLinearLayoutProductDetails.removeAllViews();
        mArrayListCartProducts.clear();
        mHashMapIndividualProductPricing.clear();
        productCounter = 0;

        mCurrentCart = mCurrentOrder.getCart();
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
                                if(eachMedicine != null)
                                    mArrayListCartProducts.add(eachMedicine);

                            }
                            if(mCurrentCart.getNoOfUniqueProductsInCart() == productCounter)
                            {
                               recyclerView = new RecyclerView(OrderDetailsActivity.this);
                                recyclerView.setLayoutParams(new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.MATCH_PARENT));

                                RecyclerView.LayoutManager layoutManager  = new LinearLayoutManager(OrderDetailsActivity.this,LinearLayoutManager.VERTICAL,false);
                                recyclerView.setHasFixedSize(true);
                                recyclerView.setLayoutManager(layoutManager);
                                recyclerViewAdapterForProducts = new ProductDetailsAdapter(mArrayListCartProducts,OrderDetailsActivity.this);

                                recyclerView.setAdapter(recyclerViewAdapterForProducts);
                                mLinearLayoutProductDetails.addView(recyclerView);

                                mLinearLayoutProductDetails.setVisibility(View.VISIBLE);

                                if(mCurrentOrder.getOrderId() == null)
                                    getPricingDetails();
                                else
                                    setPriceDetails();
                            }
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                            mLinearLayoutProductDetails.setVisibility(View.GONE);
                        }
                    });
                }
            }
            else
                mLinearLayoutProductDetails.setVisibility(View.GONE);
        }
        else
            mLinearLayoutProductDetails.setVisibility(View.GONE);

    }

    public void getPricingDetails()
    {

        for (Map.Entry<String, Double> eachEntry : mCurrentOrder.getOrderPricingDetails().entrySet())
        {
            if(eachEntry.getKey().equals("COD Charges")) {
                eachEntry.setValue(CodCharges);
                break;
            }
        }

        setPageMessage();
        setPriceDetails();
    }

    public void setPriceDetails()
    {
        mTextViewSubtotal.setText("RS. "+  Double.toString(mCurrentOrder.getOrderPricingDetails().get("Subtotal")));

        if(mCurrentOrder.getOrderPricingDetails().get("Shipping Charges") > 0) {
            mLinearLayoutShippingCharges.setVisibility(View.VISIBLE);
            mTextViewShippingCharges.setText("RS. " + mCurrentOrder.getOrderPricingDetails().get("Shipping Charges"));
        }
        else
            mLinearLayoutShippingCharges.setVisibility(View.GONE);

        if(mCurrentOrder.getOrderPricingDetails().get("COD Charges") > 0) {
            mLinearLayoutCodCharges.setVisibility(View.VISIBLE);
            mTextViewCodCharges.setText("RS. " + mCurrentOrder.getOrderPricingDetails().get("COD Charges"));
        }
        else
            mLinearLayoutCodCharges.setVisibility(View.GONE);

        double totalPayable = 0.0 ;
        for (Map.Entry<String, Double> eachEntry : mCurrentOrder.getOrderPricingDetails().entrySet())
            totalPayable += eachEntry.getValue();
        totalPayable = (double) Math.round( totalPayable *100) / 100;

        mTextViewOrderTotal.setText("RS. "+  Double.toString(totalPayable));
    }


    public void getPrescriptionsRecyclerView()
    {

        Firebase firebaseAllPrescriptionsRef = new Firebase(Constants.FIREBASE_URL_SAIRA_All_PRESCRIPTIONS);

        Firebase firebaseCurrentUserPrescriptionsRef = firebaseAllPrescriptionsRef.child(Utils.encodeEmail(mCurrentUser.getEmail()));
        firebaseCurrentUserPrescriptionsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    if(mArrayListSelectedPrescriptions != null)
                        mArrayListSelectedPrescriptions.clear();

                    for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                        Prescription eachPrescription = snapshot.getValue(Prescription.class);
                        if(eachPrescription != null && mCurrentOrder.getOrderPrescriptionIds().contains(eachPrescription.getPrescriptionId()))
                            mArrayListSelectedPrescriptions.add(eachPrescription);
                    }

                    if(mArrayListSelectedPrescriptions != null)
                    {
                        recyclerView = new RecyclerView(OrderDetailsActivity.this);
                        recyclerView.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.MATCH_PARENT));

                        RecyclerView.LayoutManager layoutManager  = new LinearLayoutManager(OrderDetailsActivity.this,LinearLayoutManager.HORIZONTAL,false);
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setLayoutManager(layoutManager);
                        recyclerViewAdapter = new PrescriptionDetailsAdapter(mArrayListSelectedPrescriptions,OrderDetailsActivity.this);

                        recyclerView.setAdapter(recyclerViewAdapter);

                        mLinearLayoutPrescriptionDetailsActonBar.setVisibility(View.VISIBLE);
                        mLinearLayoutPrescriptionDetails.addView(recyclerView);
                    }
                    else {
                        mLinearLayoutPrescriptionDetailsActonBar.setVisibility(View.GONE);
                        mLinearLayoutPrescriptionDetails.setVisibility(View.GONE);
                    }

                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void getPaymentDetailsForPlacedOrder()
    {
        int colorCode;
        double totalPayable = 0.0 ;
        for (Map.Entry<String, Double> eachEntry : mCurrentOrder.getOrderPricingDetails().entrySet())
            totalPayable += eachEntry.getValue();
        totalPayable = (double) Math.round( totalPayable *100) / 100;

        if(mCurrentOrder.getTransactionId() == null)
        {
            if(mCurrentOrder.getOrderStatus().equals(Constants.ORDER_STATUS_CANCELED) ||
                    mCurrentOrder.getOrderStatus().equals(Constants.ORDER_STATUS_RETURNED) ||
                    mCurrentOrder.getOrderStatus().equals(Constants.ORDER_STATUS_REQUESTED_FOR_RETURN))
            {
                mTextViewPaymentMethodMessage.setText("It was an COD order.");
                colorCode = OrderDetailsActivity.this.getResources().getColor(R.color.warning_message_text_color);
            }
            else {
                mTextViewPaymentMethodMessage.setText("You need to pay RS. " + totalPayable + " during order delivery.");
                colorCode = OrderDetailsActivity.this.getResources().getColor(R.color.under_process_message_text_color);
            }

            mTextViewPaymentMethodMessage.setTextColor(colorCode);
        }
        else
        {
            mTextViewPaymentMethodMessage.setText("Transaction Id: " +mCurrentOrder.getTransactionId()+ "\n(Prepaid order of RS. "+totalPayable+")");

            if(mCurrentOrder.getOrderStatus().equals(Constants.ORDER_STATUS_RETURNED) && mCurrentOrder.getOrderReturnDetails().getRefundTransactionId() != null)
                mTextViewPaymentMethodMessage.setText(mTextViewPaymentMethodMessage.getText() + "\nRefund transaction Id: "+mCurrentOrder.getOrderReturnDetails().getRefundTransactionId());
            else if (mCurrentOrder.getOrderStatus().equals(Constants.ORDER_STATUS_CANCELED) && mCurrentOrder.getOrderCancellationDetails().getRefundTransactionId() != null)
                mTextViewPaymentMethodMessage.setText(mTextViewPaymentMethodMessage.getText() + "\nRefund transaction Id: "+mCurrentOrder.getOrderCancellationDetails().getRefundTransactionId());
            else if(mCurrentOrder.getOrderStatus().equals(Constants.ORDER_STATUS_RETURNED) ||
                        mCurrentOrder.getOrderStatus().equals(Constants.ORDER_STATUS_CANCELED))
                mTextViewPaymentMethodMessage.setText(mTextViewPaymentMethodMessage.getText() + "\nIt may take 3 to 5 working days for refund.");

            colorCode = OrderDetailsActivity.this.getResources().getColor(R.color.success_message_text_color);
            mTextViewPaymentMethodMessage.setTextColor(colorCode);

        }

        mTextViewPaymentMethodMessage.setVisibility(View.VISIBLE);
    }

    public void onActionClick()
    {
        if(mCurrentOrder.getOrderId() == null)
            placeNewOrder();
        else {
            if (mTextViewAction.getText().equals("Cancel Order"))
                openDialog("Cancel");
            else if (mTextViewAction.getText().equals("Return Order"))
                openDialog("Return");
        }
    }

    public void placeNewOrder()
    {
        if(mSelectedPaymentMethod != null)
        {
            //TODO call payment getway and get the transactionId
            mCurrentOrder.setPaymentMethod(mSelectedPaymentMethod);
            mCurrentOrder.setIndividualProductPricing(mHashMapIndividualProductPricing);

            OrderOperations obj = new OrderOperations(this);
            Order returnOrder = obj.createNewOrder(mCurrentOrder);
            if(returnOrder != null) {
                mCurrentOrder = returnOrder;
                updateOrderDetails();
            }
        }
        else
            Toast.makeText(this,"Please select a payment method",Toast.LENGTH_SHORT).show();

    }

    public void openDialog(String cancelOrReturn)
    {
        if(reasonOfReturnOrCancelDialog != null) {
            reasonOfReturnOrCancelDialog = null;
        }

        reasonOfReturnOrCancelDialog = new Dialog(this);
        reasonOfReturnOrCancelDialog.setContentView(R.layout.dialog_reason_of_return_or_cancel);

        selectedItemPositionOfSpinner = 0;
        TextView textViewDialogName = (TextView) reasonOfReturnOrCancelDialog.findViewById(R.id.text_view_dialog_name);
        final Spinner spinnerReason = (Spinner) reasonOfReturnOrCancelDialog.findViewById(R.id.spinner_reasons);
        final EditText editTextOtherReason = (EditText) reasonOfReturnOrCancelDialog.findViewById(R.id.edit_text_other_reason);
        TextView textViewExit = (TextView) reasonOfReturnOrCancelDialog.findViewById(R.id.text_view_exit);
        TextView textViewCancelOrReturnOrder = (TextView) reasonOfReturnOrCancelDialog.findViewById(R.id.text_view_cancel_order);

        textViewCancelOrReturnOrder.setText(cancelOrReturn + " Order");

        textViewDialogName.setText("Why do you want to "+cancelOrReturn+"?");

        ArrayAdapter<CharSequence> reasonAdapter = ArrayAdapter.createFromResource(this,
                R.array.return_or_cancel_reason_options, android.R.layout.simple_spinner_item);
        reasonAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerReason.setAdapter(reasonAdapter);
        spinnerReason.setSelection(selectedItemPositionOfSpinner);


        reasonOfReturnOrCancelDialog.show();

        spinnerReason.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object selectedObject = parent.getItemAtPosition(position);
                selectedItemPositionOfSpinner = position;

                String selectedReason = (String) selectedObject;
                if(selectedReason.equals("Other"))
                {
                    editTextOtherReason.setText("");
                    editTextOtherReason.setVisibility(View.VISIBLE);
                }
                else
                    editTextOtherReason.setVisibility(View.GONE);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        textViewExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reasonOfReturnOrCancelDialog.dismiss();
            }
        });

        textViewCancelOrReturnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String reason = null;
                if(editTextOtherReason.getVisibility() == View.VISIBLE && editTextOtherReason.getText().toString().trim().length() > 0)
                    reason = editTextOtherReason.getText().toString();
                else if(editTextOtherReason.getVisibility() == View.GONE && selectedItemPositionOfSpinner != 0)
                    reason = spinnerReason.getSelectedItem().toString();

                if (mTextViewAction.getText().equals("Cancel Order"))
                    cancelOrder(reason);
                else if (mTextViewAction.getText().equals("Return Order"))
                    returnOrder(reason);

            }
        });

    }

    public void cancelOrder(String cancellationReason)
    {
        HashMap<String, Object> timestampOrderCancelledOn = new HashMap<>();
        timestampOrderCancelledOn.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);

        OrderCancellationDetails orderCancelDetails = new OrderCancellationDetails(mCurrentOrder.getOrderId(),null,cancellationReason,timestampOrderCancelledOn);
        mCurrentOrder.setOrderCancellationDetails(orderCancelDetails);
        mCurrentOrder.setOrderStatus(Constants.ORDER_STATUS_CANCELED);

        OrderOperations obj = new OrderOperations(OrderDetailsActivity.this);
        obj.updateOldOrder(mCurrentOrder);
    }

    public void returnOrder(String returnReason)
    {
        HashMap<String, Object> timestampOrderReturnRequestedOn = new HashMap<>();
        timestampOrderReturnRequestedOn.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);

        OrderReturnDetails orderReturnDetails = new OrderReturnDetails(mCurrentOrder.getOrderId(),null,returnReason,null,timestampOrderReturnRequestedOn);
        mCurrentOrder.setOrderReturnDetails(orderReturnDetails);
        mCurrentOrder.setOrderStatus(Constants.ORDER_STATUS_REQUESTED_FOR_RETURN);

        OrderOperations obj = new OrderOperations(OrderDetailsActivity.this);
        obj.updateOldOrder(mCurrentOrder);
    }

    public void setPageMessage()
    {
        int textColorCode,backgroundColorCode;
        String messageToBeShown;
        mTextViewPageMessage.setVisibility(View.VISIBLE);

        if(mCurrentOrder.getOrderId() == null)
        {
            double totalPayable = 0.0 ;
            for (Map.Entry<String, Double> eachEntry : mCurrentOrder.getOrderPricingDetails().entrySet())
                totalPayable += eachEntry.getValue();
            totalPayable = (double) Math.round( totalPayable *100) / 100;


            messageToBeShown = "Place Your Order(Rs. "+Double.toString(totalPayable)+")";
            textColorCode = OrderDetailsActivity.this.getResources().getColor(R.color.under_process_message_text_color);
            backgroundColorCode = R.color.under_process_message_background_color;
        }
        else
        {
            if(mCurrentOrder.getOrderStatus().equals(Constants.ORDER_STATUS_PLACED))
            {
                messageToBeShown = "Order Placed Successfully";
                textColorCode = OrderDetailsActivity.this.getResources().getColor(R.color.success_message_text_color);
                backgroundColorCode = R.color.success_message_background_color;
            }
            else if(mCurrentOrder.getOrderStatus().equals(Constants.ORDER_STATUS_DELIVERED))
            {
                messageToBeShown = "Order Delivered Successfully";
                textColorCode = OrderDetailsActivity.this.getResources().getColor(R.color.success_message_text_color);
                backgroundColorCode = R.color.success_message_background_color;
            }
            else if(mCurrentOrder.getOrderStatus().equals(Constants.ORDER_STATUS_CANCELED))
            {
                messageToBeShown = "Order has been Canceled";
                textColorCode = OrderDetailsActivity.this.getResources().getColor(R.color.fail_message_text_color);
                backgroundColorCode = R.color.fail_message_background_color;
            }
            else if(mCurrentOrder.getOrderStatus().equals(Constants.ORDER_STATUS_RETURNED))
            {
                messageToBeShown = "Order Returned Successfully";
                textColorCode = OrderDetailsActivity.this.getResources().getColor(R.color.fail_message_text_color);
                backgroundColorCode = R.color.fail_message_background_color;
            }
            else if(mCurrentOrder.getOrderStatus().equals(Constants.ORDER_STATUS_REQUESTED_FOR_RETURN))
            {
                messageToBeShown = "Return Request Accepted Successfully";
                textColorCode = OrderDetailsActivity.this.getResources().getColor(R.color.fail_message_text_color);
                backgroundColorCode = R.color.fail_message_background_color;
            }
            else if(mCurrentOrder.getOrderStatus().equals(Constants.ORDER_STATUS_DISPATCHED))
            {
                messageToBeShown = "Order Dispatched";
                textColorCode = OrderDetailsActivity.this.getResources().getColor(R.color.under_process_message_text_color);
                backgroundColorCode = R.color.under_process_message_background_color;
            }
            else
            {
                messageToBeShown = "Order Under Process";
                textColorCode = OrderDetailsActivity.this.getResources().getColor(R.color.under_process_message_text_color);
                backgroundColorCode = R.color.under_process_message_background_color;
            }
        }

        mTextViewPageMessage.setText(messageToBeShown);
        mTextViewPageMessage.setBackgroundResource(backgroundColorCode);
        mTextViewPageMessage.setTextColor(textColorCode);

    }

    public void setActionTextViewText()
    {
        int colorCode = OrderDetailsActivity.this.getResources().getColor(R.color.tw__solid_white);
        mTextViewAction.setTextColor(colorCode);
        mTextViewAction.setVisibility(View.VISIBLE);


        if(mCurrentOrder.getOrderId() == null) {
            mTextViewAction.setText("Conform and Proceed");
            mTextViewAction.setBackgroundResource(R.color.primary);
        }
        else
        {
            mTextViewAction.setBackgroundResource(R.color.fail_message_text_color);

            if(mCurrentOrder.getOrderStatus().equals(Constants.ORDER_STATUS_DELIVERED))
                mTextViewAction.setText("Return Order");
            else if(mCurrentOrder.getOrderStatus().equals(Constants.ORDER_STATUS_CANCELED) ||
                    mCurrentOrder.getOrderStatus().equals(Constants.ORDER_STATUS_REQUESTED_FOR_RETURN) ||
                    mCurrentOrder.getOrderStatus().equals(Constants.ORDER_STATUS_RETURNED))
                mTextViewAction.setVisibility(View.GONE);
            else
                mTextViewAction.setText("Cancel Order");
        }

    }


}
