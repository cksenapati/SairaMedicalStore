package com.example.android.sairamedicalstore.models;

import com.example.android.sairamedicalstore.utils.Constants;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by chandan on 13-08-2017.
 */

public class Medicine implements Serializable {
    //medicineCategory -> otc or prescription
    //medicineType -> tablet,syrup or cream
    private String medicineId,medicineName, medicineManufacturerName, medicineComposition,medicineCategory,medicineType,medicineImageUrl,updaterStack,displayCategory;
    private int noOfItemsInOnePack;
    private double pricePerPack,priceStack;
    private boolean medicineAvailability,isLooseAvailable;
    private HashMap<String, Object> timestampCreated,timestampLastUpdate;


    public Medicine() {
    }

        public Medicine(String medicineId,String medicineName, String medicineManufacturerName, String medicineComposition, String medicineCategory,
                        String medicineType, String medicineImageUrl, String updaterStack,String displayCategory ,int noOfItemsInOnePack,
                        double pricePerPack, double priceStack, boolean medicineAvailability, boolean isLooseAvailable,
                        HashMap<String, Object> timestampCreated, HashMap<String, Object> timestampLastUpdate) {
            this.medicineId = medicineId;
        this.medicineName = medicineName;
        this.medicineManufacturerName = medicineManufacturerName;
        this.medicineComposition = medicineComposition;
        this.medicineCategory = medicineCategory;
        this.medicineType = medicineType;
        this.medicineImageUrl = medicineImageUrl;
        this.updaterStack = updaterStack;
        this.displayCategory = displayCategory;
        this.noOfItemsInOnePack = noOfItemsInOnePack;
        this.pricePerPack = pricePerPack;
        this.priceStack = priceStack;
        this.medicineAvailability = medicineAvailability;
        this.isLooseAvailable = isLooseAvailable;
        this.timestampCreated = timestampCreated;
        this.timestampLastUpdate = timestampLastUpdate;
    }

    //-------------------------Setter-------------------------------------------------------------------------


    public void setMedicineId(String medicineId) {
        this.medicineId = medicineId;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public void setMedicineManufacturerName(String medicineManufacturerName) {
        this.medicineManufacturerName = medicineManufacturerName;
    }

    public void setMedicineComposition(String medicineComposition) {
        this.medicineComposition = medicineComposition;
    }

    public void setMedicineCategory(String medicineCategory) {
        this.medicineCategory = medicineCategory;
    }

    public void setMedicineType(String medicineType) {
        this.medicineType = medicineType;
    }

    public void setMedicineImageUrl(String medicineImageUrl) {
        this.medicineImageUrl = medicineImageUrl;
    }

    public void setUpdaterStack(String updaterStack) {
        this.updaterStack = updaterStack;
    }

    public void setDisplayCategory(String displayCategory) {
        this.displayCategory = displayCategory;
    }

    public void setNoOfItemsInOnePack(int noOfItemsInOnePack) {
        this.noOfItemsInOnePack = noOfItemsInOnePack;
    }

    public void setPricePerPack(double pricePerPack) {
        this.pricePerPack = pricePerPack;
    }

    public void setPriceStack(double priceStack) {
        this.priceStack = priceStack;
    }

    public void setMedicineAvailability(boolean medicineAvailability) {
        this.medicineAvailability = medicineAvailability;
    }

    public void setLooseAvailable(boolean looseAvailable) {
        isLooseAvailable = looseAvailable;
    }

    public void setTimestampCreated(HashMap<String, Object> timestampCreated) {
        this.timestampCreated = timestampCreated;
    }

    public void setTimestampLastUpdate(HashMap<String, Object> timestampLastUpdate) {
        this.timestampLastUpdate = timestampLastUpdate;
    }

    //-------------------------Getter-------------------------------------------------------------------------


    public String getMedicineId() {
        return medicineId;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public String getMedicineManufacturerName() {
        return medicineManufacturerName;
    }

    public String getMedicineComposition() {
        return medicineComposition;
    }

    public String getMedicineCategory() {
        return medicineCategory;
    }

    public String getMedicineType() {
        return medicineType;
    }

    public String getMedicineImageUrl() {
        return medicineImageUrl;
    }

    public String getUpdaterStack() {
        return updaterStack;
    }

    public String getDisplayCategory() {
        return displayCategory;
    }

    public int getNoOfItemsInOnePack() {
        return noOfItemsInOnePack;
    }

    public double getPricePerPack() {
        return pricePerPack;
    }

    public double getPriceStack() {
        return priceStack;
    }

    public boolean isMedicineAvailability() {
        return medicineAvailability;
    }

    public boolean isLooseAvailable() {
        return isLooseAvailable;
    }

    public HashMap<String, Object> getTimestampCreated() {
        return timestampCreated;
    }

    public HashMap<String, Object> getTimestampLastUpdate() {
        return timestampLastUpdate;
    }

    @JsonIgnore
    public long getTimestampLastUpdateLong() {

        return (long) timestampLastUpdate.get(Constants.FIREBASE_PROPERTY_TIMESTAMP);
    }

    @JsonIgnore
    public long getTimestampCreatedLong() {

        return (long) timestampCreated.get(Constants.FIREBASE_PROPERTY_TIMESTAMP);
    }

}
