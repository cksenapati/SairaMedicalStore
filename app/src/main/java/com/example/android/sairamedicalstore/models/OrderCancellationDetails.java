package com.example.android.sairamedicalstore.models;

import com.example.android.sairamedicalstore.utils.Constants;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by chandan on 07-11-2017.
 */

public class OrderCancellationDetails  implements Serializable {
    String orderId,refundTransactionId,cancellationReason;
    private HashMap<String, Object> timestampOrderCanceledOn;

    public OrderCancellationDetails() {
    }

    public OrderCancellationDetails(String orderId, String refundTransactionId, String cancellationReason,
                                    HashMap<String, Object> timestampOrderCanceledOn) {
        this.orderId = orderId;
        this.refundTransactionId = refundTransactionId;
        this.cancellationReason = cancellationReason;
        this.timestampOrderCanceledOn = timestampOrderCanceledOn;
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

    public String getCancellationReason() {
        return cancellationReason;
    }

    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }

    public HashMap<String, Object> getTimestampOrderCanceledOn() {
        return timestampOrderCanceledOn;
    }

    public void setTimestampOrderCanceledOn(HashMap<String, Object> timestampOrderCanceledOn) {
        this.timestampOrderCanceledOn = timestampOrderCanceledOn;
    }

    @JsonIgnore
    public long getTimestampOrderCancelledOnLong() {

        return (long) timestampOrderCanceledOn.get(Constants.FIREBASE_PROPERTY_TIMESTAMP);
    }
}
