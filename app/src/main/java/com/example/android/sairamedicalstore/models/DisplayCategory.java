package com.example.android.sairamedicalstore.models;

import com.example.android.sairamedicalstore.utils.Constants;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashMap;

/**
 * Created by chandan on 19-09-2017.
 */

public class DisplayCategory {
    private String displayCategoryName, displayCategoryCreatedBy;
    private HashMap<String, Object> timestampCreated;

    public DisplayCategory() {
    }

    public DisplayCategory(String categoryName, String categoryCreatedBy, HashMap<String, Object> timestampCreated) {
        this.displayCategoryName = categoryName;
        this.displayCategoryCreatedBy = categoryCreatedBy;
        this.timestampCreated = timestampCreated;
    }

    //====================================Getter=============================
    public String getDisplayCategoryName() {
        return displayCategoryName;
    }

    public String getDisplayCategoryCreatedBy() {
        return displayCategoryCreatedBy;
    }

    public HashMap<String, Object> getTimestampCreated() {
        return timestampCreated;
    }

    @JsonIgnore
    public long getTimestampCreatedLong() {

        return (long) timestampCreated.get(Constants.FIREBASE_PROPERTY_TIMESTAMP);
    }

    //====================================Setter=============================


    public void setDisplayCategoryName(String displayCategoryName) {
        this.displayCategoryName = displayCategoryName;
    }

    public void setDisplayCategoryCreatedBy(String displayCategoryCreatedBy) {
        this.displayCategoryCreatedBy = displayCategoryCreatedBy;
    }

    public void setTimestampCreated(HashMap<String, Object> timestampCreated) {
        this.timestampCreated = timestampCreated;
    }
}
