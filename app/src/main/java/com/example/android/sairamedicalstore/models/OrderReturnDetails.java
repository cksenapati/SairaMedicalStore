package com.example.android.sairamedicalstore.models;

import com.example.android.sairamedicalstore.utils.Constants;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by chandan on 07-11-2017.
 */

public class OrderReturnDetails  implements Serializable {
    String orderId,refundTransactionId,returnReason;
    private HashMap<String, Object> timestampOrderReturnScheduledOn,timestampOrderReturnedOn,timestampOrderReturnRequestedOn;

    public OrderReturnDetails() {
    }

    public OrderReturnDetails(String orderId, String refundTransactionId, String returnReason,HashMap<String, Object>  timestampOrderReturnScheduledOn,
                              HashMap<String, Object> timestampOrderReturnedOn, HashMap<String, Object> timestampOrderReturnRequestedOn) {
        this.orderId = orderId;
        this.refundTransactionId = refundTransactionId;
        this.returnReason = returnReason;
        this.timestampOrderReturnScheduledOn = timestampOrderReturnScheduledOn;
        this.timestampOrderReturnedOn = timestampOrderReturnedOn;
        this.timestampOrderReturnRequestedOn = timestampOrderReturnRequestedOn;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getRefundTransactionId() {
        return refundTransactionId;
    }

    public void setRefundTransactionId(String refundTransactionId) {
        this.refundTransactionId = refundTransactionId;
    }

    public String getReturnReason() {
        return returnReason;
    }

    public void setReturnReason(String returnReason) {
        this.returnReason = returnReason;
    }

    public HashMap<String, Object> getTimestampOrderReturnScheduledOn() {
        return timestampOrderReturnScheduledOn;
    }

    public void setTimestampOrderReturnScheduledOn(HashMap<String, Object> timestampOrderReturnScheduledOn) {
        this.timestampOrderReturnScheduledOn = timestampOrderReturnScheduledOn;
    }

    @JsonIgnore
    public long getTimestampOrderReturnScheduledOnLong() {

        return (long) timestampOrderReturnScheduledOn.get(Constants.FIREBASE_PROPERTY_TIMESTAMP);
    }

    public HashMap<String, Object> getTimestampOrderReturnedOn() {
        return timestampOrderReturnedOn;
    }

    public void setTimestampOrderReturnedOn(HashMap<String, Object> timestampOrderReturnedOn) {
        this.timestampOrderReturnedOn = timestampOrderReturnedOn;
    }

    @JsonIgnore
    public long getTimestampOrderReturnedOnLong() {

        return (long) timestampOrderReturnedOn.get(Constants.FIREBASE_PROPERTY_TIMESTAMP);
    }

    public HashMap<String, Object> getTimestampOrderReturnRequestedOn() {
        return timestampOrderReturnRequestedOn;
    }

    public void setTimestampOrderReturnRequestedOn(HashMap<String, Object> timestampOrderReturnRequestedOn) {
        this.timestampOrderReturnRequestedOn = timestampOrderReturnRequestedOn;
    }

    @JsonIgnore
    public long getTimestampOrderReturnRequestedOnLong() {

        return (long) timestampOrderReturnRequestedOn.get(Constants.FIREBASE_PROPERTY_TIMESTAMP);
    }
}
