package com.example.android.sairamedicalstore.models;

/**
 * Created by chandan on 16-08-2017.
 */

public class MedicinePic {
    String medicineType, picUrl;

    public MedicinePic() {
    }

    public MedicinePic(String medicineType, String medicinePic) {
        this.medicineType = medicineType;
        this.picUrl = medicinePic;
    }

    public String getMedicineType() {
        return medicineType;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setMedicineType(String medicineType) {
        this.medicineType = medicineType;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }
}
