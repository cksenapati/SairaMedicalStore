package com.example.android.sairamedicalstore.ui.customerSupport;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.example.android.sairamedicalstore.BuildConfig;
import com.example.android.sairamedicalstore.R;
import com.example.android.sairamedicalstore.SairaMedicalStoreApplication;
import com.example.android.sairamedicalstore.models.User;
import com.example.android.sairamedicalstore.ui.MainActivity;
import com.example.android.sairamedicalstore.ui.search.SearchActivity;
import com.example.android.sairamedicalstore.utils.Constants;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.HashMap;
import java.util.Map;

public class CustomerSupportActivity extends AppCompatActivity {

    LinearLayout mLinearLayoutAllQueryTickets,mLinearLayoutFrequentlyAskedQuestions,mLinearLayoutReplyToQueryTickets,mLinearLayoutCallUs;
    TextView mTextViewCustomerSupportNo;

    User mCurrentUser;

    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    public static final String CALL_US_ON = "call_us_on";
    public static final String DEFAULT_CONTACT_NUMBER = "+91 8763717165";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_support);

        mCurrentUser = ((SairaMedicalStoreApplication) this.getApplication()).getCurrentUser();

        initializeScreen();

        displayAccordingToUserType();

        mLinearLayoutAllQueryTickets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentToSearchActivity = new Intent(CustomerSupportActivity.this, SearchActivity.class);
                intentToSearchActivity.putExtra("whatToSearch",Constants.SEARCH_QUERY);
                startActivity(intentToSearchActivity);
            }
        });

        mLinearLayoutFrequentlyAskedQuestions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentToSearchActivity = new Intent(CustomerSupportActivity.this, SearchActivity.class);
                intentToSearchActivity.putExtra("whatToSearch",Constants.SEARCH_FAQ);
                startActivity(intentToSearchActivity);
            }
        });

        mLinearLayoutReplyToQueryTickets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentToSearchActivity = new Intent(CustomerSupportActivity.this, SearchActivity.class);
                intentToSearchActivity.putExtra("whatToSearch",Constants.SEARCH_ALL_QUERIES_FROM_ALL_USERS);
                startActivity(intentToSearchActivity);
            }
        });

        //Remote config setup
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        // Create Remote Config Setting to enable developer mode.
        // Fetching configs from the server is normally limited to 5 requests per hour.
        // Enabling developer mode allows many more requests to be made per hour, so developers
        // can test different config values during development.
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        mFirebaseRemoteConfig.setConfigSettings(configSettings);

        // Define default config values. Defaults are used when fetched config values are not
        // available. Eg: if an error occurred fetching values from the server.
        Map<String, Object> defaultConfigMap = new HashMap<>();
        defaultConfigMap.put(CALL_US_ON, DEFAULT_CONTACT_NUMBER);
        mFirebaseRemoteConfig.setDefaults(defaultConfigMap);
        fetchConfig();

    }

    // Fetch the config to determine the allowed length of messages.
    public void fetchConfig() {
        long cacheExpiration = 3600; // 1 hour in seconds
        // If developer mode is enabled reduce cacheExpiration to 0 so that each fetch goes to the
        // server. This should not be used in release builds.
        if (mFirebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }
        mFirebaseRemoteConfig.fetch(cacheExpiration)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Make the fetched config available
                        // via FirebaseRemoteConfig get<type> calls, e.g., getLong, getString.
                        mFirebaseRemoteConfig.activateFetched();

                        mTextViewCustomerSupportNo.setText(mTextViewCustomerSupportNo.getText() + mFirebaseRemoteConfig.getString(CALL_US_ON));

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                this.finish();
                return true;

            default: return super.onOptionsItemSelected(item);
        }

    }

    private void initializeScreen()
    {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        /* Common toolbar setup */
        setSupportActionBar(toolbar);
        setTitle("Customer Support");

        /* Add back button to the action bar */
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mLinearLayoutAllQueryTickets = (LinearLayout) findViewById(R.id.linear_layout_all_query_tickets_action_bar);
        mLinearLayoutFrequentlyAskedQuestions = (LinearLayout) findViewById(R.id.linear_layout_frequently_asked_questions_action_bar);
        mLinearLayoutReplyToQueryTickets = (LinearLayout) findViewById(R.id.linear_layout_reply_to_query_tickets_action_bar);
        mLinearLayoutCallUs = (LinearLayout) findViewById(R.id.linear_layout_call_us);

        mTextViewCustomerSupportNo = (TextView) findViewById(R.id.text_view_customer_support_no);


    }

    private void displayAccordingToUserType()
    {
        switch (mCurrentUser.getUserType())
        {
            case Constants.USER_TYPE_END_USER :
                mLinearLayoutAllQueryTickets.setVisibility(View.VISIBLE);
                mLinearLayoutCallUs.setVisibility(View.VISIBLE);
                mLinearLayoutFrequentlyAskedQuestions.setVisibility(View.VISIBLE);
                mLinearLayoutReplyToQueryTickets.setVisibility(View.GONE);
                break;
            default:
                mLinearLayoutAllQueryTickets.setVisibility(View.GONE);
                mLinearLayoutCallUs.setVisibility(View.GONE);
                mLinearLayoutFrequentlyAskedQuestions.setVisibility(View.VISIBLE);
                mLinearLayoutReplyToQueryTickets.setVisibility(View.VISIBLE);
                break;
        }

    }

}
