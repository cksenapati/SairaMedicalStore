package com.example.android.sairamedicalstore.models;

import com.example.android.sairamedicalstore.utils.Constants;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by chandan on 17-10-2017.
 */

public class Prescription implements Serializable {
    String prescriptionId,prescriptionName;
    private HashMap<String, Object> timestampUploaded;
    private HashMap<String, String> prescriptionPages;

    public Prescription() {
    }

    public Prescription(String prescriptionId, HashMap<String, String> prescriptionPages, String prescriptionName, HashMap<String, Object> timestampUploaded) {
        this.prescriptionId = prescriptionId;
        this.prescriptionPages = prescriptionPages;
        this.prescriptionName = prescriptionName;
        this.timestampUploaded = timestampUploaded;
    }

    public String getPrescriptionId() {
        return prescriptionId;
    }

    public void setPrescriptionId(String prescriptionId) {
        this.prescriptionId = prescriptionId;
    }

    public HashMap<String, String> getPrescriptionPages() {
        return prescriptionPages;
    }

    public void setPrescriptionPages(HashMap<String, String> prescriptionPages) {
        this.prescriptionPages = prescriptionPages;
    }

    public String getPrescriptionName() {
        return prescriptionName;
    }

    public void setPrescriptionName(String prescriptionName) {
        this.prescriptionName = prescriptionName;
    }

    public HashMap<String, Object> getTimestampUploaded() {
        return timestampUploaded;
    }

    @JsonIgnore
    public long getTimestampUploadedLong() {

        return (long) timestampUploaded.get(Constants.FIREBASE_PROPERTY_TIMESTAMP);
    }

    public void setTimestampUploaded(HashMap<String, Object> timestampUploaded) {
        this.timestampUploaded = timestampUploaded;
    }
}
