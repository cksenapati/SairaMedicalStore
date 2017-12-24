package com.example.android.sairamedicalstore.operations;

import android.app.Activity;
import android.widget.Toast;

import com.example.android.sairamedicalstore.SairaMedicalStoreApplication;
import com.example.android.sairamedicalstore.models.Query;
import com.example.android.sairamedicalstore.models.User;
import com.example.android.sairamedicalstore.utils.Constants;
import com.example.android.sairamedicalstore.utils.Utils;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ServerValue;
import com.firebase.client.ValueEventListener;

import java.util.HashMap;

/**
 * Created by chandan on 28-10-2017.
 */

public class QueryOperations {
    User mCurrentUser;
    Activity mActivity;
    Firebase firebaseAllQueriesRef;

    public QueryOperations(Activity mActivity) {
        this.mActivity = mActivity;
        this.mCurrentUser = ((SairaMedicalStoreApplication) mActivity.getApplication()).getCurrentUser();
        this.firebaseAllQueriesRef = new Firebase(Constants.FIREBASE_URL_SAIRA_All_QUERIES)/*.child(Utils.encodeEmail(mCurrentUser.getEmail()))*/;
    }

    public String PostNewQuery( String queryTitle, String queryText,  String contactToCustomerBy , String taggedOrderId)
    {
        String queryId = firebaseAllQueriesRef.push().getKey();
        String queryPostedBy = mCurrentUser.getEmail();
        String queryResolvedBy = null,queryResolverReply = null;
        String queryStatus = Constants.QUERY_STATUS_POSTED;

        HashMap<String, Object> timestampCreated = new HashMap<>();
        timestampCreated.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);
        HashMap<String, Object> timestampQueryClosed = null;

        Query newQuery = new Query( queryId,  queryTitle,  queryPostedBy,  queryText,  contactToCustomerBy, taggedOrderId, queryStatus,  queryResolvedBy,queryResolverReply,
                timestampCreated,  timestampQueryClosed);

        try {
            firebaseAllQueriesRef.child(queryId).setValue(newQuery);
            Toast.makeText(mActivity,Constants.UPLOAD_SUCCESSFUL,Toast.LENGTH_SHORT).show();
        }
        catch (Exception ex)
        {
            Toast.makeText(mActivity,Constants.UPLOAD_FAIL,Toast.LENGTH_SHORT).show();
            queryId = null;
        }

        return queryId;
    }

    public  void updateQuery(final Query queryToBeUpdated)
    {
        final HashMap<String, Object> timestampQueryClosedOn = new HashMap<>();
        timestampQueryClosedOn.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);

        final Firebase firebaseCurrentQuery = new Firebase(Constants.FIREBASE_URL_SAIRA_All_QUERIES)/*.child(Utils.encodeEmail(mCurrentUser.getEmail()))*/.
                child(queryToBeUpdated.getQueryId());

        firebaseCurrentQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    try {
                        firebaseCurrentQuery.setValue(queryToBeUpdated);
                        Toast.makeText(mActivity,Constants.UPDATE_SUCCESSFUL,Toast.LENGTH_SHORT).show();
                    }
                    catch (Exception ex){
                        Toast.makeText(mActivity,Constants.UPDATE_FAIL,Toast.LENGTH_SHORT).show();
                    }

                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Toast.makeText(mActivity,Constants.UPDATE_FAIL,Toast.LENGTH_SHORT).show();
            }
        });
    }

   /* public  void reOpenClosedQuery(String queryId)
    {

        final Firebase firebaseCurrentQuery = new Firebase(Constants.FIREBASE_URL_SAIRA_All_QUERIES).child(Utils.encodeEmail(mCurrentUser.getEmail())).
                child(queryId);

        firebaseCurrentQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    Query queryToBeReOpened = dataSnapshot.getValue(Query.class);
                    if (queryToBeReOpened != null) {
                        queryToBeReOpened.setQueryStatus(Constants.QUERY_STATUS_REOPENED);
                        try {
                            firebaseCurrentQuery.setValue(queryToBeReOpened);
                            Toast.makeText(mActivity,"Query has been re-opened",Toast.LENGTH_SHORT).show();
                        }
                        catch (Exception ex){
                            Toast.makeText(mActivity,Constants.UPDATE_FAIL,Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Toast.makeText(mActivity,Constants.UPDATE_FAIL,Toast.LENGTH_SHORT).show();
            }
        });
    }*/

}
