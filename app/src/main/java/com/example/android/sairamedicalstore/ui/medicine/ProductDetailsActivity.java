package com.example.android.sairamedicalstore.ui.medicine;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.sairamedicalstore.R;
import com.example.android.sairamedicalstore.SairaMedicalStoreApplication;
import com.example.android.sairamedicalstore.models.Cart;
import com.example.android.sairamedicalstore.models.Medicine;
import com.example.android.sairamedicalstore.models.DefaultKeyValuePair;
import com.example.android.sairamedicalstore.models.User;
import com.example.android.sairamedicalstore.operations.CartOperations;
import com.example.android.sairamedicalstore.ui.MainActivity;
import com.example.android.sairamedicalstore.ui.cart.CartActivity;
import com.example.android.sairamedicalstore.ui.medicine.AddOrUpdateMedicineActivity;
import com.example.android.sairamedicalstore.ui.search.SearchActivity;
import com.example.android.sairamedicalstore.utils.Constants;
import com.example.android.sairamedicalstore.utils.Utils;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

import static android.media.CamcorderProfile.get;

public class ProductDetailsActivity extends AppCompatActivity {

    Firebase mFirebaseAllProductRef, mFirebaseCurrentMedicineRef;

    User mCurrentUser;
    Cart mCurrentCart;
    Medicine mCurrentMedicine;
    String mMedicineId;
    ArrayList<DefaultKeyValuePair> mArrayListDefaultMedicinePics;
    ArrayList<Integer> mArrayListQuantity;
    int noOfItemsSelected;
    double priceForSelectedNoOfItems;


    ImageView mImageViewCart,mImageViewCloseOffers,mImageViewProduct,mImageViewPrescriptionRequired,mImageViewGoBack;
    LinearLayout mLinearLayoutOffers,mLinearLayoutPricing,mLinearLayoutProductDetails,mLinearLayoutProductInfo;
    TextView mTextViewOffers,mTextViewProductName,mTextViewProductManufacturer,mTextViewProductComposition,mTextViewProductPricePerUnit,
             mTextViewAvailability,mTextViewPriceForSelectedQuantity,mTextViewSubstitute;
    public static TextView mTextViewAction;
    Spinner mSpinnerQuantity;
    ProgressBar mProgressBarFetchingData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        Intent intent = getIntent();
        if (intent != null) {
            mMedicineId = intent.getStringExtra("medicineId");
        }
        mCurrentUser = ((SairaMedicalStoreApplication) this.getApplication()).getCurrentUser();

        initializeScreen();
        mArrayListDefaultMedicinePics = ((SairaMedicalStoreApplication) this.getApplication()).getArrayListDefaultMedicinePics();

        getProduct();

        if (mCurrentUser.getUserType().equals(Constants.USER_TYPE_END_USER))
        {
            mImageViewCart.setVisibility(View.VISIBLE);
            mImageViewCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent cartIntent = new Intent(ProductDetailsActivity.this,CartActivity.class);
                    startActivity(cartIntent);
                }
            });
        }
        else
            mImageViewCart.setVisibility(View.GONE);

        mImageViewGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });





    }

    private void initializeScreen()
    {
        mImageViewGoBack = (ImageView) findViewById(R.id.image_view_go_back);
        mImageViewCart = (ImageView) findViewById(R.id.image_view_cart);
        mImageViewPrescriptionRequired = (ImageView) findViewById(R.id.image_view_prescription_required);

        mLinearLayoutOffers = (LinearLayout) findViewById(R.id.linear_layout_offers);
        mTextViewOffers = (TextView) findViewById(R.id.text_view_offers);
        mImageViewCloseOffers = (ImageView) findViewById(R.id.image_view_close_offers);

        mLinearLayoutProductDetails = (LinearLayout) findViewById(R.id.linear_layout_product_details);
        mLinearLayoutProductInfo = (LinearLayout) findViewById(R.id.linear_layout_product_info);
        mImageViewProduct = (ImageView) findViewById(R.id.image_view_product);
        mTextViewProductName = (TextView) findViewById(R.id.text_view_product_name);
        mTextViewProductManufacturer = (TextView) findViewById(R.id.text_view_product_manufacturer);
        mTextViewProductComposition = (TextView) findViewById(R.id.text_view_product_composition);
        mTextViewProductPricePerUnit = (TextView) findViewById(R.id.text_view_product_price_per_unit);

        mTextViewAvailability = (TextView) findViewById(R.id.text_view_availability);

        mLinearLayoutPricing = (LinearLayout) findViewById(R.id.linear_layout_pricing);
        mSpinnerQuantity = (Spinner) findViewById(R.id.spinner_quantity);
        mTextViewPriceForSelectedQuantity = (TextView) findViewById(R.id.text_view_price_for_selected_quantity);

        mTextViewSubstitute = (TextView) findViewById(R.id.text_view_substitutes);
        mTextViewAction = (TextView) findViewById(R.id.text_view_add_to_cart);

        mProgressBarFetchingData = (ProgressBar) findViewById(R.id.progress_bar_fetching_data);


        //Firebase
        mFirebaseAllProductRef = new Firebase(Constants.FIREBASE_URL_SAIRA_ALL_MEDICINES);
        mFirebaseCurrentMedicineRef = mFirebaseAllProductRef.child(mMedicineId);

        mArrayListQuantity = new ArrayList<Integer>();
    }

    private void getProduct()
    {
        mProgressBarFetchingData.setVisibility(View.VISIBLE);

        mFirebaseCurrentMedicineRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    try {
                        mCurrentMedicine = dataSnapshot.getValue(Medicine.class);
                        displayDataForCustomer();
                    }catch (Exception ex){}
                }
                mProgressBarFetchingData.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                mProgressBarFetchingData.setVisibility(View.GONE);
            }
        });
    }

    public void setActionTextViewText()
    {
        String encodedEmail = Utils.encodeEmail(mCurrentUser.getEmail());

        final Firebase firebaseCurrentCartRef = new Firebase(Constants.FIREBASE_URL_SAIRA_All_CARTS).child(encodedEmail);
        firebaseCurrentCartRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    mCurrentCart = dataSnapshot.getValue(Cart.class);
                    mTextViewAction.setText("Add to cart");

                    if(mCurrentCart != null && mCurrentCart.getProductIdAndItemCount() != null)
                    {
                        for (Map.Entry<String, Integer> eachProduct : mCurrentCart.getProductIdAndItemCount().entrySet())
                        {
                            if(eachProduct.getKey().equals(mCurrentMedicine.getMedicineId())) {
                                mTextViewAction.setText("Go to cart");
                                break;
                            }
                        }
                    }
                }
                else
                {
                    mTextViewAction.setText("Add to cart");
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                mTextViewAction.setText("Add to cart");
            }
        });
    }

    private void displayDataForCustomer()
    {

        displayProductPic();
        mTextViewProductName.setText(mCurrentMedicine.getMedicineName());
        mTextViewProductManufacturer.setText("Manufacturer: "+Utils.toLowerCaseExceptFirstLetter(mCurrentMedicine.getMedicineManufacturerName()));
        mTextViewProductComposition.setText("Compositions: " + Utils.toLowerCaseExceptFirstLetter(mCurrentMedicine.getMedicineComposition()));
        mTextViewProductPricePerUnit.setText("RS "+ Double.toString(mCurrentMedicine.getPricePerPack()) + " /"+
                Integer.toString(mCurrentMedicine.getNoOfItemsInOnePack()) +"\n"+
                Utils.toLowerCaseExceptFirstLetter(mCurrentMedicine.getMedicineType()) +"(s)");

        if (mCurrentMedicine.getMedicineCategory().equals(Constants.MEDICINE_CATEGORY_PRESCRIPTION))
            mImageViewPrescriptionRequired.setVisibility(View.VISIBLE);
        else
            mImageViewPrescriptionRequired.setVisibility(View.GONE);

        setAvailability();

        if (mCurrentUser.getUserType().equals(Constants.USER_TYPE_END_USER))
        setActionTextViewText();
        else {
            mTextViewAction.setVisibility(View.VISIBLE);
            mTextViewAction.setText("Modify");
            mTextViewAction.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }

        mTextViewSubstitute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent searchActivity = new Intent(ProductDetailsActivity.this, SearchActivity.class);
                searchActivity.putExtra("whatToSearch",Constants.SEARCH_MEDICINE);
                searchActivity.putExtra("searchOrderBy",Constants.FIREBASE_PROPERTY_MEDICINE_COMPOSITION);
                searchActivity.putExtra("specialSearch",mCurrentMedicine.getMedicineComposition());
                startActivity(searchActivity);
            }
        });
    }


    private void setAvailability()
    {
        if(mCurrentMedicine.isMedicineAvailability())
        {
            mTextViewAvailability.setVisibility(View.GONE);
            mLinearLayoutPricing.setVisibility(View.VISIBLE);
            mTextViewAction.setEnabled(true);
            setPricing();
        }
        else
        {
            mTextViewAvailability.setVisibility(View.VISIBLE);
            mLinearLayoutPricing.setVisibility(View.GONE);
            mTextViewAction.setEnabled(false);
        }
    }

    private void setPricing()
    {
        mArrayListQuantity.clear();
        int itemsInOnePack = mCurrentMedicine.getNoOfItemsInOnePack();
        final double pricePerSingleItem = mCurrentMedicine.getPricePerPack() / itemsInOnePack ;

        if(mCurrentMedicine.isLooseAvailable())
        {
            for(int i=1;i<=5*itemsInOnePack;i++)
                mArrayListQuantity.add(i);
        }
        else{
            for(int i=1;i<=5;i++)
                mArrayListQuantity.add(itemsInOnePack*i);
        }


        ArrayAdapter<Integer> quantityAdapter = new ArrayAdapter<Integer>(this,android.R.layout.simple_spinner_item, mArrayListQuantity);
        quantityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerQuantity.setAdapter(quantityAdapter);

        int defaultPosition = quantityAdapter.getPosition(itemsInOnePack);
        mSpinnerQuantity.setSelection(defaultPosition);
        noOfItemsSelected = itemsInOnePack;
        priceForSelectedNoOfItems = (double) Math.round( (double)noOfItemsSelected * pricePerSingleItem *100) / 100;


        mSpinnerQuantity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object selectedObject = parent.getItemAtPosition(position);
                noOfItemsSelected = (Integer) selectedObject;
                priceForSelectedNoOfItems = (double) Math.round( (double)noOfItemsSelected * pricePerSingleItem *100) / 100;
                mTextViewPriceForSelectedQuantity.setText("RS "+  Double.toString(priceForSelectedNoOfItems));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }


    public void displayProductPic()
    {
        String imageUrl = mCurrentMedicine.getMedicineImageUrl();
        String medicineType = mCurrentMedicine.getMedicineType();

        if(imageUrl.equals("default"))
        {
            for (int i = 0; i < mArrayListDefaultMedicinePics.size(); i++) {
                if(mArrayListDefaultMedicinePics.get(i).getKey().equals(medicineType)) {
                    imageUrl = mArrayListDefaultMedicinePics.get(i).getValue();
                    break;
                }
            }
        }


        Glide.with(mImageViewProduct.getContext())
                .load(imageUrl)
                .into(mImageViewProduct);
    }


    public void onAddToCartClick(View view)
    {
        if (mCurrentUser.getUserType().equals(Constants.USER_TYPE_END_USER)){
            if(mTextViewAction.getText().equals("Add to cart"))
            {
                CartOperations obj = new CartOperations(this);
                obj.AddNewProductToCart(mCurrentMedicine.getMedicineId(),noOfItemsSelected);
            }
            else
            {
                Intent cartIntent = new Intent(this,CartActivity.class);
                startActivity(cartIntent);
            }
        }
        else
        {
            Intent intentToAddOrUpdateMedicine = new Intent(this,AddOrUpdateMedicineActivity.class);
            intentToAddOrUpdateMedicine.putExtra("currentMedicine",mCurrentMedicine);
            startActivity(intentToAddOrUpdateMedicine);
        }

    }

}
