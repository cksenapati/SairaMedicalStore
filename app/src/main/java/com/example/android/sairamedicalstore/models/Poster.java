package com.example.android.sairamedicalstore.models;

import com.example.android.sairamedicalstore.utils.Constants;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashMap;

/**
 * Created by chandan on 02-10-2017.
 */

public class Poster {
    String posterId,posterName,displayCategory,posterImageURI,posterCreatedBy;
    private HashMap<String, Object> timestampCreated,timestampLastUpdate,aboutPoster;

    public Poster() {
    }

    public Poster(String posterId, String posterName, String displayCategory, HashMap<String, Object> aboutPoster, String posterImageURI, String posterCreatedBy, HashMap<String, Object> timestampCreated, HashMap<String, Object> timestampLastUpdate) {
        this.posterId = posterId;
        this.posterName = posterName;
        this.displayCategory = displayCategory;
        this.aboutPoster = aboutPoster;
        this.posterImageURI = posterImageURI;
        this.posterCreatedBy = posterCreatedBy;
        this.timestampCreated = timestampCreated;
        this.timestampLastUpdate = timestampLastUpdate;
    }

    public String getPosterId() {
        return posterId;
    }

    public void setPosterId(String posterId) {
        this.posterId = posterId;
    }

    public String getPosterName() {
        return posterName;
    }

    public void setPosterName(String posterName) {
        this.posterName = posterName;
    }

    public String getDisplayCategory() {
        return displayCategory;
    }

    public void setDisplayCategory(String displayCategory) {
        this.displayCategory = displayCategory;
    }

    public HashMap<String, Object> getAboutPoster() {
        return aboutPoster;
    }

    public void setAboutPoster(HashMap<String, Object> aboutPoster) {
        this.aboutPoster = aboutPoster;
    }

    public String getPosterImageURI() {
        return posterImageURI;
    }

    public void setPosterImageURI(String posterImageURI) {
        this.posterImageURI = posterImageURI;
    }

    public String getPosterCreatedBy() {
        return posterCreatedBy;
    }

    public void setPosterCreatedBy(String posterCreatedBy) {
        this.posterCreatedBy = posterCreatedBy;
    }



    public HashMap<String, Object> getTimestampCreated() {
        return timestampCreated;
    }

    @JsonIgnore
    public long getTimestampLastUpdateLong() {

        return (long) timestampLastUpdate.get(Constants.FIREBASE_PROPERTY_TIMESTAMP);
    }

    public void setTimestampCreated(HashMap<String, Object> timestampCreated) {
        this.timestampCreated = timestampCreated;
    }




    public HashMap<String, Object> getTimestampLastUpdate() {
        return timestampLastUpdate;
    }

    @JsonIgnore
    public long getTimestampCreatedLong() {

        return (long) timestampCreated.get(Constants.FIREBASE_PROPERTY_TIMESTAMP);
    }

    public void setTimestampLastUpdate(HashMap<String, Object> timestampLastUpdate) {
        this.timestampLastUpdate = timestampLastUpdate;
    }

}
