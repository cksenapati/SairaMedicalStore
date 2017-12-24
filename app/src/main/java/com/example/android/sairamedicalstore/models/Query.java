package com.example.android.sairamedicalstore.models;

import com.example.android.sairamedicalstore.utils.Constants;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by chandan on 24-11-2017.
 */

public class Query implements Serializable {
    String queryId,queryTitle,queryPostedBy,queryText,contactToCustomerBy,queryStatus,queryResolvedBy,queryResolverReply,taggedOrderId;
    private HashMap<String, Object> timestampCreated,timestampQueryClosed;

    public Query() {
    }

    public Query(String queryId, String queryTitle, String queryPostedBy, String queryText, String contactToCustomerBy,String taggedOrderId,
                 String queryStatus, String queryResolvedBy,String queryResolverReply,
                 HashMap<String, Object> timestampCreated, HashMap<String, Object> timestampQueryClosed) {
        this.queryId = queryId;
        this.queryTitle = queryTitle;
        this.queryPostedBy = queryPostedBy;
        this.queryText = queryText;
        this.contactToCustomerBy = contactToCustomerBy;
        this.taggedOrderId = taggedOrderId;
        this.queryStatus = queryStatus;
        this.queryResolvedBy = queryResolvedBy;
        this.queryResolverReply = queryResolverReply;
        this.timestampCreated = timestampCreated;
        this.timestampQueryClosed = timestampQueryClosed;
    }

    public String getQueryId() {
        return queryId;
    }

    public void setQueryId(String queryId) {
        this.queryId = queryId;
    }

    public String getQueryTitle() {
        return queryTitle;
    }

    public void setQueryTitle(String queryTitle) {
        this.queryTitle = queryTitle;
    }

    public String getQueryPostedBy() {
        return queryPostedBy;
    }

    public void setQueryPostedBy(String queryPostedBy) {
        this.queryPostedBy = queryPostedBy;
    }

    public String getTaggedOrderId() {
        return taggedOrderId;
    }

    public void setTaggedOrderId(String taggedOrderId) {
        this.taggedOrderId = taggedOrderId;
    }

    public String getQueryText() {
        return queryText;
    }

    public void setQueryText(String queryText) {
        this.queryText = queryText;
    }

    public String getContactToCustomerBy() {
        return contactToCustomerBy;
    }

    public void setContactToCustomerBy(String contactToCustomerBy) {
        this.contactToCustomerBy = contactToCustomerBy;
    }

    public String getQueryStatus() {
        return queryStatus;
    }

    public void setQueryStatus(String queryStatus) {
        this.queryStatus = queryStatus;
    }

    public String getQueryResolvedBy() {
        return queryResolvedBy;
    }

    public void setQueryResolvedBy(String queryResolvedBy) {
        this.queryResolvedBy = queryResolvedBy;
    }

    public String getQueryResolverReply() {
        return queryResolverReply;
    }

    public void setQueryResolverReply(String queryResolverReply) {
        this.queryResolverReply = queryResolverReply;
    }

    public HashMap<String, Object> getTimestampCreated() {
        return timestampCreated;
    }

    public void setTimestampCreated(HashMap<String, Object> timestampCreated) {
        this.timestampCreated = timestampCreated;
    }

    @JsonIgnore
    public long getTimestampCreatedLong() {

        return (long) timestampCreated.get(Constants.FIREBASE_PROPERTY_TIMESTAMP);
    }

    public HashMap<String, Object> getTimestampQueryClosed() {
        return timestampQueryClosed;
    }

    public void setTimestampQueryClosed(HashMap<String, Object> timestampQueryClosed) {
        this.timestampQueryClosed = timestampQueryClosed;
    }

    @JsonIgnore
    public long getTimestampQueryClosedLong() {

        return (long) timestampQueryClosed.get(Constants.FIREBASE_PROPERTY_TIMESTAMP);
    }


}
