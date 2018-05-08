package com.example.android.sairamedicalstore.models;

import com.example.android.sairamedicalstore.utils.Constants;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by chandan on 28-10-2017.
 */

public class DeliverableAddress implements Serializable {
    String pinCode,name,district,state,country;
    double deliveryCharge;

    public DeliverableAddress() {
    }

    public DeliverableAddress(String pinCode,String name, String district, String state, String country, double deliveryCharge) {
        this.pinCode = pinCode;
        this.name = name;
        this.district = district;
        this.state = state;
        this.country = country;
        this.deliveryCharge = deliveryCharge;
    }

    public String getPinCode() {
        return pinCode;
    }

    public void setPinCode(String pinCode) {
        this.pinCode = pinCode;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public double getDeliveryCharge() {
        return deliveryCharge;
    }

    public void setDeliveryCharge(double deliveryCharge) {
        this.deliveryCharge = deliveryCharge;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
