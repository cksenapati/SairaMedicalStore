package com.example.android.sairamedicalstore.models;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by chandan on 24-11-2017.
 */

public class PrescriptionPage implements Serializable {
    String prescriptionPageId,imageUri,prescriptionId;
    boolean isActive;
    private HashMap<String, String> medicineIdAndNameInPage;
    private HashMap<String, Integer> medicineIdAndItemCountInPage;

    public PrescriptionPage() {
    }

    public PrescriptionPage(String prescriptionPageId, String imageUri, String prescriptionId, boolean isActive,
                            HashMap<String, String> medicineIdAndNameInPage, HashMap<String, Integer> medicineIdAndItemCountInPage) {
        this.prescriptionPageId = prescriptionPageId;
        this.imageUri = imageUri;
        this.prescriptionId = prescriptionId;
        this.isActive = isActive;
        this.medicineIdAndNameInPage = medicineIdAndNameInPage;
        this.medicineIdAndItemCountInPage = medicineIdAndItemCountInPage;
    }

    public String getPrescriptionPageId() {
        return prescriptionPageId;
    }

    public void setPrescriptionPageId(String prescriptionPageId) {
        this.prescriptionPageId = prescriptionPageId;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getPrescriptionId() {
        return prescriptionId;
    }

    public void setPrescriptionId(String prescriptionId) {
        this.prescriptionId = prescriptionId;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public HashMap<String, String> getMedicineIdAndNameInPage() {
        return medicineIdAndNameInPage;
    }

    public void setMedicineIdAndNameInPage(HashMap<String, String> medicineIdAndNameInPage) {
        this.medicineIdAndNameInPage = medicineIdAndNameInPage;
    }

    public HashMap<String, Integer> getMedicineIdAndItemCountInPage() {
        return medicineIdAndItemCountInPage;
    }

    public void setMedicineIdAndItemCountInPage(HashMap<String, Integer> medicineIdAndItemCountInPage) {
        this.medicineIdAndItemCountInPage = medicineIdAndItemCountInPage;
    }
}
