package com.example.android.sairamedicalstore.operations;

import android.app.Activity;
import android.net.Uri;

import com.example.android.sairamedicalstore.SairaMedicalStoreApplication;
import com.example.android.sairamedicalstore.models.Order;
import com.example.android.sairamedicalstore.models.Prescription;
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
import java.util.Map;

import static com.example.android.sairamedicalstore.ui.order.OrderDetailsActivity.reasonOfReturnOrCancelDialog;

/**
 * Created by chandan on 30-10-2017.
 */

public class OrderOperations {
    User mCurrentUser;
    Activity mActivity;
    Firebase firebaseCurrentUserAllOrdersRef;

    public OrderOperations( Activity mActivity) {
        this.mActivity = mActivity;
        this.mCurrentUser = ((SairaMedicalStoreApplication) mActivity.getApplication()).getCurrentUser();
        this.firebaseCurrentUserAllOrdersRef = new Firebase(Constants.FIREBASE_URL_SAIRA_All_ORDERS).child(Utils.encodeEmail(mCurrentUser.getEmail()));
    }

    public Order createNewOrder(Order orderToBePlaced)
    {
        orderToBePlaced.setOrderId(firebaseCurrentUserAllOrdersRef.push().getKey());

        HashMap<String, Object> timestampOrderCreated = new HashMap<>();
        timestampOrderCreated.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);
        orderToBePlaced.setTimestampCreated(timestampOrderCreated);

        if(orderToBePlaced.getPaymentMethod().equals("PREPAID"))
            orderToBePlaced.setTransactionId(orderToBePlaced.getOrderId()); //Temporary transaction id

        orderToBePlaced.setOrderStatus(Constants.ORDER_STATUS_PLACED);

        try {
            firebaseCurrentUserAllOrdersRef.child(orderToBePlaced.getOrderId()).setValue(orderToBePlaced);
            /*for (Map.Entry<String,Prescription> eachPrescription :orderToBePlaced.getOrderPrescriptions().entrySet()) {
                for (Map.Entry<String,String> eachPage :eachPrescription.getValue().getPrescriptionPages().entrySet()) {
                    uploadImageInStorage(null,orderToBePlaced.getOrderId(),eachPrescription.getKey(),eachPage.getKey());
                }
            }*/
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
        final Firebase firebaseCurrentOrderRef = firebaseCurrentUserAllOrdersRef.child(orderToBeOptated.getOrderId());
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

    private void uploadImageInStorage(Uri posterImageURI, final String orderId,final String prescriptionCount,final String pageCount)
    {
        FirebaseStorage mFirebaseStorage = FirebaseStorage.getInstance();
        StorageReference mFirebaseAfterOrderPrescriptionStorageReference = mFirebaseStorage.getReference().child("allOrders").child(orderId).child(prescriptionCount);


        StorageReference selectedPhotoRef = mFirebaseAfterOrderPrescriptionStorageReference.child(posterImageURI.getLastPathSegment());
        UploadTask uploadTask = selectedPhotoRef.putFile(posterImageURI);
        uploadTask.addOnSuccessListener(mActivity, new OnSuccessListener<UploadTask.TaskSnapshot>() {
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                try{
                    firebaseCurrentUserAllOrdersRef.child(orderId).child("orderPrescriptions").child(prescriptionCount).child("prescriptionPages")
                            .child(pageCount).setValue(downloadUrl.toString());
                }catch (Exception ex){}
            }
        });
    }

}
