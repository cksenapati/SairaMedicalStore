package com.example.android.sairamedicalstore.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.sairamedicalstore.R;
import com.example.android.sairamedicalstore.SairaMedicalStoreApplication;
import com.example.android.sairamedicalstore.models.DefaultKeyValuePair;
import com.example.android.sairamedicalstore.models.Medicine;
import com.example.android.sairamedicalstore.models.User;
import com.example.android.sairamedicalstore.utils.Constants;
import com.example.android.sairamedicalstore.utils.Utils;
import com.firebase.client.Firebase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.android.sairamedicalstore.ui.CartActivity.mCurrentCart;
import static com.example.android.sairamedicalstore.ui.CartActivity.mTextViewOrderTotal;
import static com.example.android.sairamedicalstore.ui.CartActivity.mTextViewShippingCharges;
import static com.example.android.sairamedicalstore.ui.CartActivity.mTextViewSubtotal;
import static com.example.android.sairamedicalstore.ui.CartActivity.shippingPrice;
import static com.example.android.sairamedicalstore.ui.CartActivity.subtotalPrice;
import static com.example.android.sairamedicalstore.ui.CartActivity.totalPayablePrice;

/**
 * Created by chandan on 08-03-2017.
 */

public class ProductsInCartAdapter extends ArrayAdapter<Medicine> {

    Activity mActivity;
    User mCurrentUser;
    int noOfItemsSelected;
    double priceForSelectedNoOfItems;
    ArrayList<DefaultKeyValuePair> mArrayListDefaultMedicinePics;

    public ProductsInCartAdapter(Activity activity, ArrayList<Medicine> arrayListAllProductsInCart) {
        super(activity, 0, arrayListAllProductsInCart);
        mActivity = activity;
        mCurrentUser = ((SairaMedicalStoreApplication) mActivity.getApplication()).getCurrentUser();
        mArrayListDefaultMedicinePics = ((SairaMedicalStoreApplication) mActivity.getApplication()).getArrayListDefaultMedicinePics();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final Medicine eachMedicine = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_single_cart_item, parent, false);
        }


        ImageView imageViewProductImage = (ImageView) convertView.findViewById(R.id.image_view_single_product_image);
        Glide.with(imageViewProductImage.getContext())
                .load(getImageUrlToDisplay(eachMedicine))
                .into(imageViewProductImage);

        TextView textViewProductName = (TextView) convertView.findViewById(R.id.text_view_single_product_name);
        textViewProductName.setText(Utils.toLowerCaseExceptFirstLetter(eachMedicine.getMedicineName()));

        ImageView imageViewPrescriptionRequired = (ImageView) convertView.findViewById(R.id.image_view_prescription_required);
        if(eachMedicine.getMedicineCategory().equals(Constants.MEDICINE_CATEGORY_PRESCRIPTION))
            imageViewPrescriptionRequired.setVisibility(View.VISIBLE);
        else
            imageViewPrescriptionRequired.setVisibility(View.GONE);

        TextView textViewProductOutOfStuck = (TextView) convertView.findViewById(R.id.text_view_single_product_message);
        if(!eachMedicine.isMedicineAvailability()) {
            textViewProductOutOfStuck.setText("This product is out of stuck");
            int colorCode = mActivity.getResources().getColor(R.color.tw__composer_red);
            textViewProductOutOfStuck.setTextColor(colorCode);

            textViewProductOutOfStuck.setVisibility(View.VISIBLE);
        }
        else
            textViewProductOutOfStuck.setVisibility(View.GONE);

        TextView textViewProductRemove = (TextView) convertView.findViewById(R.id.text_view_single_product_remove);
        textViewProductRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeProductFromCart(eachMedicine.getMedicineId());
            }
        });

        Spinner spinnerQuantity = (Spinner) convertView.findViewById(R.id.spinner_quantity);
        TextView textViewPriceForSelectedQuantity = (TextView) convertView.findViewById(R.id.text_view_total_product_price);

        setPricing(eachMedicine,spinnerQuantity,textViewPriceForSelectedQuantity);


        return convertView;
    }

    private void setPricing(final Medicine currentMedicine,Spinner spinnerQuantity,final TextView textViewPriceForSelectedQuantity)
    {
        ArrayList<Integer> mArrayListQuantity =  new ArrayList<>();

        int itemsInOnePack = currentMedicine.getNoOfItemsInOnePack();
        final double pricePerSingleItem = currentMedicine.getPricePerPack() / itemsInOnePack ;

        if(currentMedicine.isLooseAvailable())
        {
            for(int i=1;i<=5*itemsInOnePack;i++)
                mArrayListQuantity.add(i);
        }
        else{
            for(int i=1;i<=5;i++)
                mArrayListQuantity.add(itemsInOnePack*i);
        }


        ArrayAdapter<Integer> quantityAdapter = new ArrayAdapter<Integer>(mActivity,android.R.layout.simple_spinner_item, mArrayListQuantity);
        quantityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerQuantity.setAdapter(quantityAdapter);

        noOfItemsSelected = setSelectedQuantity(currentMedicine.getMedicineId());
        if(noOfItemsSelected == 0)
            noOfItemsSelected = itemsInOnePack;

        int defaultPosition = quantityAdapter.getPosition(noOfItemsSelected);
        spinnerQuantity.setSelection(defaultPosition);

        priceForSelectedNoOfItems = (double) Math.round( (double)noOfItemsSelected * pricePerSingleItem *100) / 100;
        textViewPriceForSelectedQuantity.setText("RS. "+  Double.toString(priceForSelectedNoOfItems));

        subtotalPrice = subtotalPrice + priceForSelectedNoOfItems;
        subtotalPrice = (double) Math.round(subtotalPrice *100) / 100;
        totalPayablePrice = (double) Math.round((subtotalPrice+shippingPrice) *100) / 100;
        mTextViewSubtotal.setText("RS. "+  Double.toString(subtotalPrice));
        mTextViewShippingCharges.setText("RS. "+  Double.toString(shippingPrice));
        mTextViewOrderTotal.setText("RS. "+  Double.toString(totalPayablePrice));


        spinnerQuantity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object selectedObject = parent.getItemAtPosition(position);

                noOfItemsSelected = (Integer) selectedObject;
                updateProductQuantity(currentMedicine.getMedicineId(),noOfItemsSelected);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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

    public void removeProductFromCart(String productIdToBeRemoved)
    {
        if(mCurrentCart != null && mCurrentCart.getProductIdAndItemCount() != null)
        {
            Firebase firebaseCurrentCartRef = new Firebase(Constants.FIREBASE_URL_SAIRA_All_CARTS).child(Utils.encodeEmail(mCurrentUser.getEmail()));
            HashMap<String, Integer> newHashSet = mCurrentCart.getProductIdAndItemCount();
            for (Map.Entry<String, Integer> eachEntry : mCurrentCart.getProductIdAndItemCount().entrySet())
            {
                if(eachEntry.getKey().equals(productIdToBeRemoved))
                {
                    newHashSet.remove(eachEntry.getKey());
                    mCurrentCart.setProductIdAndItemCount(newHashSet);
                    mCurrentCart.setNoOfUniqueProductsInCart(mCurrentCart.getNoOfUniqueProductsInCart() - 1);
                    firebaseCurrentCartRef.setValue(mCurrentCart);
                    break;
                }
            }
        }
    }

    public int setSelectedQuantity(String productId)
    {
        if(mCurrentCart != null && mCurrentCart.getProductIdAndItemCount() != null)
        {
            for (Map.Entry<String, Integer> eachEntry : mCurrentCart.getProductIdAndItemCount().entrySet())
            {
                if(eachEntry.getKey().equals(productId))
                {
                    return eachEntry.getValue();
                }
            }
        }

        return 0;
    }

    public void updateProductQuantity(String productId,int newQuantity)
    {
        if(mCurrentCart != null && mCurrentCart.getProductIdAndItemCount() != null)
        {
            Firebase firebaseCurrentCartRef = new Firebase(Constants.FIREBASE_URL_SAIRA_All_CARTS).child(Utils.encodeEmail(mCurrentUser.getEmail()));
            HashMap<String, Integer> newHashSet = new HashMap<>();
            for (Map.Entry<String, Integer> eachEntry : mCurrentCart.getProductIdAndItemCount().entrySet())
            {
                if(eachEntry.getKey().equals(productId))
                    newHashSet.put(eachEntry.getKey(),newQuantity);
                else
                    newHashSet.put(eachEntry.getKey(),eachEntry.getValue());
            }

            mCurrentCart.setProductIdAndItemCount(newHashSet);
            firebaseCurrentCartRef.setValue(mCurrentCart);
        }
    }
}

