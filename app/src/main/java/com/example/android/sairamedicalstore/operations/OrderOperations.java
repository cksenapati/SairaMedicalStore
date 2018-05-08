package com.example.android.sairamedicalstore.operations;

import android.app.Activity;
import android.net.Uri;

import com.example.android.sairamedicalstore.SairaMedicalStoreApplication;
import com.example.android.sairamedicalstore.models.Order;
import com.example.android.sairamedicalstore.models.User;
import com.example.android.sairamedicalstore.utils.Constants;
import com.example.android.sairamedicalstore.utils.Utils;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ServerValue;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import static com.example.android.sairamedicalstore.ui.order.OrderDetailsActivity.reasonOfReturnOrCancelDialog;

/**
 * Created by chandan on 30-10-2017.
 */

public class OrderOperations {
    User mCurrentUser;
    Activity mActivity;
    Firebase firebaseAllOrdersRef;

    public OrderOperations( Activity mActivity) {
        this.mActivity = mActivity;
        this.mCurrentUser = ((SairaMedicalStoreApplication) mActivity.getApplication()).getCurrentUser();
        this.firebaseAllOrdersRef = new Firebase(Constants.FIREBASE_URL_SAIRA_All_ORDERS);
    }

    public Order createNewOrder(Order orderToBePlaced)
    {
        orderToBePlaced.setOrderId(firebaseAllOrdersRef.push().getKey());

        HashMap<String, Object> timestampOrderCreated = new HashMap<>();
        timestampOrderCreated.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);
        orderToBePlaced.setTimestampCreated(timestampOrderCreated);

        if(orderToBePlaced.getPaymentMethod().equals("PREPAID"))
            orderToBePlaced.setTransactionId(orderToBePlaced.getOrderId()); //Temporary transaction id

        orderToBePlaced.setOrderStatus(Constants.ORDER_STATUS_PLACED);

        try {
            firebaseAllOrdersRef.child(orderToBePlaced.getOrderId()).setValue(orderToBePlaced);
            clearCartAfterOrderPlaced();
            return orderToBePlaced;
        }
        catch (Exception Ex)
        {
            return null;
        }
    }

    public void updateOldOrder(final Order orderToBeOptated)
    {
        final Firebase firebaseCurrentOrderRef = firebaseAllOrdersRef.child(orderToBeOptated.getOrderId());
        firebaseCurrentOrderRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    try{
                        firebaseCurrentOrderRef.setValue(orderToBeOptated);
                        reasonOfReturnOrCancelDialog.dismiss();
                    }catch (Exception ex){}
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void clearCartAfterOrderPlaced()
    {
        Firebase firebaseCurrentUserCartRef = new Firebase(Constants.FIREBASE_URL_SAIRA_All_CARTS).child(Utils.encodeEmail(mCurrentUser.getEmail()));
        firebaseCurrentUserCartRef.setValue(null);
    }


}
