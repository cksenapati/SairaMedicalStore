package com.example.android.sairamedicalstore.models;

import com.example.android.sairamedicalstore.utils.Constants;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashMap;

/**
 * Created by chandan on 17-09-2017.
 */

public class Offer {
    String offerId,offerName,offerDescription,updaterStack,offerValueStack,timestampStack;
    double offerDiscountPercentage,offerDiscountValue,offerMaximumDiscountValue, offerValidForMinimumPerches;
    private HashMap<String, Object> timestampCreated,timestampLastUpdate;
    boolean isActive;

    public Offer() {
    }

    public Offer(String offerId, String offerName, String offerDescription, String updaterStack, String offerValueStack,String timestampStack,
                 double offerDiscountPercentage, double offerDiscountValue, double offerMaximumDiscountValue,
                 double offerValidForMinimumPerches, HashMap<String, Object> timestampCreated,
                 HashMap<String, Object> timestampLastUpdate, boolean isActive) {
        this.offerId = offerId;
        this.offerName = offerName;
        this.offerDescription = offerDescription;
        this.updaterStack = updaterStack;
        this.offerValueStack = offerValueStack;
        this.timestampStack = timestampStack;
        this.offerDiscountPercentage = offerDiscountPercentage;
        this.offerDiscountValue = offerDiscountValue;
        this.offerMaximumDiscountValue = offerMaximumDiscountValue;
        this.offerValidForMinimumPerches = offerValidForMinimumPerches;
        this.timestampCreated = timestampCreated;
        this.timestampLastUpdate = timestampLastUpdate;
        this.isActive = isActive;
    }

    /*
        --------------------------------------------Getter-------------------------------
    */

    public String getOfferId() {
        return offerId;
    }

    public String getOfferName() {
        return offerName;
    }

    public String getOfferDescription() {
        return offerDescription;
    }

    public String getUpdaterStack() {
        return updaterStack;
    }

    public String getOfferValueStack() {
        return offerValueStack;
    }

    public String getTimestampStack() {
        return timestampStack;
    }

    public double getOfferDiscountPercentage() {
        return offerDiscountPercentage;
    }

    public double getOfferDiscountValue() {
        return offerDiscountValue;
    }

    public double getOfferMaximumDiscountValue() {
        return offerMaximumDiscountValue;
    }

    public double getOfferValidForMinimumPerches() {
        return offerValidForMinimumPerches;
    }

    public HashMap<String, Object> getTimestampCreated() {
        return timestampCreated;
    }

    public HashMap<String, Object> getTimestampLastUpdate() {
        return timestampLastUpdate;
    }

    public boolean isActive() {
        return isActive;
    }

    @JsonIgnore
    public long getTimestampLastUpdateLong() {

        return (long) timestampLastUpdate.get(Constants.FIREBASE_PROPERTY_TIMESTAMP);
    }

    @JsonIgnore
    public long getTimestampCreatedLong() {

        return (long) timestampCreated.get(Constants.FIREBASE_PROPERTY_TIMESTAMP);
    }

    /*
    --------------------------------------------Getter-------------------------------
*/

    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }

    public void setOfferName(String offerName) {
        this.offerName = offerName;
    }

    public void setOfferDescription(String offerDescription) {
        this.offerDescription = offerDescription;
    }

    public void setUpdaterStack(String updaterStack) {
        this.updaterStack = updaterStack;
    }

    public void setOfferValueStack(String offerValueStack) {
        this.offerValueStack = offerValueStack;
    }

    public void setTimestampStack(String timestampStack) {
        this.timestampStack = timestampStack;
    }

    public void setOfferDiscountPercentage(double offerDiscountPercentage) {
        this.offerDiscountPercentage = offerDiscountPercentage;
    }

    public void setOfferDiscountValue(double offerDiscountValue) {
        this.offerDiscountValue = offerDiscountValue;
    }

    public void setOfferMaximumDiscountValue(double offerMaximumDiscountValue) {
        this.offerMaximumDiscountValue = offerMaximumDiscountValue;
    }

    public void setOfferValidForMinimumPerches(double offerValidForMinimumPerches) {
        this.offerValidForMinimumPerches = offerValidForMinimumPerches;
    }

    public void setTimestampCreated(HashMap<String, Object> timestampCreated) {
        this.timestampCreated = timestampCreated;
    }

    public void setTimestampLastUpdate(HashMap<String, Object> timestampLastUpdate) {
        this.timestampLastUpdate = timestampLastUpdate;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
