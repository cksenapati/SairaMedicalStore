package com.example.android.sairamedicalstore.models;

import com.example.android.sairamedicalstore.utils.Constants;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashMap;

/**
 * Created by chandan on 02-09-2017.
 */

public class Composition {
    private String compositionName,createdBy;
    private HashMap<String, Object> timestampCreated,timestampLastUpdate;

    public Composition() {
    }

    public Composition(String manufacturerName, String createdBy, HashMap<String, Object> timestampCreated, HashMap<String, Object> timestampLastUpdate) {
        this.compositionName = manufacturerName;
        this.createdBy = createdBy;
        this.timestampCreated = timestampCreated;
        this.timestampLastUpdate = timestampLastUpdate;
    }

    public void setCompositionName(String compositionName) {
        this.compositionName = compositionName;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public void setTimestampCreated(HashMap<String, Object> timestampCreated) {
        this.timestampCreated = timestampCreated;
    }

    public void setTimestampLastUpdate(HashMap<String, Object> timestampLastUpdate) {
        this.timestampLastUpdate = timestampLastUpdate;
    }

    //-------------------------Getter-------------------------------------------------------------------------


    public String getCompositionName() {
        return compositionName;
    }

    public String getCreatedBy() {
        return createdBy;
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
