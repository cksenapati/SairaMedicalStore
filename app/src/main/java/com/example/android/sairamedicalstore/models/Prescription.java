package com.example.android.sairamedicalstore.models;

import com.example.android.sairamedicalstore.utils.Constants;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by chandan on 17-10-2017.
 */

public class Prescription implements Serializable {
    String prescriptionId,prescriptionName,prescriptionOwnerEmail;
    boolean isSentForEvaluation;
    private HashMap<String, Object> timestampUploaded;
    private HashMap<String, PrescriptionPage> prescriptionPages;

    public Prescription() {
    }

    public Prescription(String prescriptionId, HashMap<String, PrescriptionPage> prescriptionPages, String prescriptionName,
                        String prescriptionOwnerEmail,HashMap<String, Object> timestampUploaded) {
        this.prescriptionId = prescriptionId;
        this.prescriptionPages = prescriptionPages;
        this.prescriptionName = prescriptionName;
        isSentForEvaluation = false;
        this.prescriptionOwnerEmail = prescriptionOwnerEmail;
        this.timestampUploaded = timestampUploaded;
    }

    public String getPrescriptionId() {
        return prescriptionId;
    }

    public void setPrescriptionId(String prescriptionId) {
        this.prescriptionId = prescriptionId;
    }

    public HashMap<String, PrescriptionPage> getPrescriptionPages() {
        return prescriptionPages;
    }

    public void setPrescriptionPages(HashMap<String, PrescriptionPage> prescriptionPages) {
        this.prescriptionPages = prescriptionPages;
    }

    public String getPrescriptionName() {
        return prescriptionName;
    }

    public void setPrescriptionName(String prescriptionName) {
        this.prescriptionName = prescriptionName;
    }

    public String getPrescriptionOwnerEmail() {
        return prescriptionOwnerEmail;
    }

    public void setPrescriptionOwnerEmail(String prescriptionOwnerEmail) {
        this.prescriptionOwnerEmail = prescriptionOwnerEmail;
    }

    public HashMap<String, Object> getTimestampUploaded() {
        return timestampUploaded;
    }

    public boolean isSentForEvaluation() {
        return isSentForEvaluation;
    }

    public void setSentForEvaluation(boolean sentForEvaluation) {
        isSentForEvaluation = sentForEvaluation;
    }

    @JsonIgnore
    public long getTimestampUploadedLong() {

        return (long) timestampUploaded.get(Constants.FIREBASE_PROPERTY_TIMESTAMP);
    }

    public void setTimestampUploaded(HashMap<String, Object> timestampUploaded) {
        this.timestampUploaded = timestampUploaded;
    }
}
