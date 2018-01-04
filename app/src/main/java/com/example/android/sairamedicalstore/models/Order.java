package com.example.android.sairamedicalstore.models;

import com.example.android.sairamedicalstore.utils.Constants;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by chandan on 28-10-2017.
 */

public class Order implements Serializable {
    String orderId,orderStatus,transactionId,paymentMethod,orderPlaceBy;
    Cart cart;
    Address orderDeliveryAddress;
    HashMap<String,Prescription> orderPrescriptions;
    ArrayList<String> orderComplaintIds;
    private HashMap<String, Object> timestampCreated,timestampDeliveryScheduledOn;
    private HashMap<String,Double> orderPricingDetails;
    private HashMap<String,Double> individualProductPricing;
    private OrderReturnDetails orderReturnDetails;
    private OrderCancellationDetails orderCancellationDetails;


    public Order() {
    }

    public Order(String orderId, String orderStatus,String transactionId,String paymentMethod, String orderPlaceBy ,Cart cart, Address orderDeliveryAddress,
                 HashMap<String,Prescription> orderPrescriptions, ArrayList<String> orderComplaintIds, HashMap<String, Object> timestampOrderPlaced,HashMap<String, Object> timestampDeliveryScheduledOn,
                 HashMap<String, Double> orderPricingDetails, HashMap<String, Double> individualProductPricing,
                 OrderReturnDetails orderReturnDetails,OrderCancellationDetails orderCancelDetails) {
        this.orderId = orderId;
        this.orderStatus = orderStatus;
        this.transactionId = transactionId;
        this.paymentMethod = paymentMethod;
        this.orderPlaceBy = orderPlaceBy;
        this.cart = cart;
        this.orderDeliveryAddress = orderDeliveryAddress;
        this.orderPrescriptions = orderPrescriptions;
        this.orderComplaintIds = orderComplaintIds;
        this.timestampCreated = timestampOrderPlaced;
        this.timestampDeliveryScheduledOn = timestampDeliveryScheduledOn;
        this.orderPricingDetails = orderPricingDetails;
        this.individualProductPricing = individualProductPricing;
        this.orderReturnDetails = orderReturnDetails;
        this.orderCancellationDetails = orderCancelDetails;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getOrderPlaceBy() {
        return orderPlaceBy;
    }

    public void setOrderPlaceBy(String orderPlaceBy) {
        this.orderPlaceBy = orderPlaceBy;
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public Address getOrderDeliveryAddress() {
        return orderDeliveryAddress;
    }

    public void setOrderDeliveryAddress(Address orderDeliveryAddress) {
        this.orderDeliveryAddress = orderDeliveryAddress;
    }



    public HashMap<String,Prescription> getOrderPrescriptions() {
        return orderPrescriptions;
    }

    public void setOrderPrescriptions(HashMap<String,Prescription> orderPrescriptions) {
        this.orderPrescriptions = orderPrescriptions;
    }

    public ArrayList<String> getOrderComplaintIds() {
        return orderComplaintIds;
    }

    public void setOrderComplaintIds(ArrayList<String> orderComplaintIds) {
        this.orderComplaintIds = orderComplaintIds;
    }

    public HashMap<String, Object> getTimestampCreated() {
        return timestampCreated;
    }

    public void setTimestampCreated(HashMap<String, Object> timestampCreated) {
        this.timestampCreated = timestampCreated;
    }

    @JsonIgnore
    public long getTimestampOrderPlacedLong() {

        return (long) timestampCreated.get(Constants.FIREBASE_PROPERTY_TIMESTAMP);
    }


    public HashMap<String, Object> getTimestampDeliveryScheduledOn() {
        return timestampDeliveryScheduledOn;
    }

    public void setTimestampDeliveryScheduledOn(HashMap<String, Object> timestampDeliveryScheduledOn) {
        this.timestampDeliveryScheduledOn = timestampDeliveryScheduledOn;
    }

    @JsonIgnore
    public long getTimestampDeliveryScheduledOnLong() {

        return (long) timestampDeliveryScheduledOn.get(Constants.FIREBASE_PROPERTY_TIMESTAMP);
    }

    public HashMap<String, Double> getOrderPricingDetails() {
        return orderPricingDetails;
    }

    public void setOrderPricingDetails(HashMap<String, Double> orderPricingDetails) {
        this.orderPricingDetails = orderPricingDetails;
    }

    public HashMap<String, Double> getIndividualProductPricing() {
        return individualProductPricing;
    }

    public void setIndividualProductPricing(HashMap<String, Double> individualProductPricing) {
        this.individualProductPricing = individualProductPricing;
    }

    public OrderReturnDetails getOrderReturnDetails() {
        return orderReturnDetails;
    }

    public void setOrderReturnDetails(OrderReturnDetails orderReturnDetails) {
        this.orderReturnDetails = orderReturnDetails;
    }

    public OrderCancellationDetails getOrderCancellationDetails() {
        return orderCancellationDetails;
    }

    public void setOrderCancellationDetails(OrderCancellationDetails orderCancellationDetails) {
        this.orderCancellationDetails = orderCancellationDetails;
    }
}
