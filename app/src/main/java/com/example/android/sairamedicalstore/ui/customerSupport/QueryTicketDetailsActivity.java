package com.example.android.sairamedicalstore.ui.customerSupport;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.util.Util;
import com.example.android.sairamedicalstore.R;
import com.example.android.sairamedicalstore.SairaMedicalStoreApplication;
import com.example.android.sairamedicalstore.models.Query;
import com.example.android.sairamedicalstore.models.User;
import com.example.android.sairamedicalstore.operations.QueryOperations;
import com.example.android.sairamedicalstore.ui.search.SearchActivity;
import com.example.android.sairamedicalstore.utils.Constants;
import com.example.android.sairamedicalstore.utils.Utils;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ServerValue;
import com.firebase.client.ValueEventListener;

import java.util.Date;
import java.util.HashMap;

public class QueryTicketDetailsActivity extends AppCompatActivity {

    TextView mTextViewQueryId,mTextViewQueryTitle,mTextViewQueryText,mTextViewResolverResponse,mTextViewTaggedOrderId,
            mTextViewQueryPostedOn,mTextViewQueryClosedOn,mTextViewQueryPostedBy,mTextViewQueryClosedBy,
            mTextViewUnderProcess,mTextViewAction,mTextViewPageMessage;
    RadioGroup mRadioGroupContactBy;
    RadioButton mRadioButtonPhone,mRadioButtonMail;
    Spinner mSpinnerQueryTitle;
    EditText mEditTextQueryText,mEditTextResolverResponse;

    User mCurrentUser;
    Query mCurrentQuery;
    String mCurrentQueryId;

    Firebase mFirebaseCurrentQueryRef;

    private static final int RC_ORDER_ID_PICKER = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_ticket_details);

        Intent intent = getIntent();
        if (intent != null) {
            mCurrentQueryId = intent.getStringExtra("currentQueryId");
        }

        mCurrentUser = ((SairaMedicalStoreApplication) this.getApplication()).getCurrentUser();

        initializeScreen();

        if (mCurrentQueryId == null)
            setupForNewQuery();
        else {
            getCurrentQuery();
        }

        mTextViewAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTextViewAction.getText().equals("Post"))
                    postNewQuery();
                else if(mTextViewAction.getText().equals("Close"))
                {
                    if (mCurrentQuery.getQueryPostedBy().equals(mCurrentUser.getEmail()))
                        closeOpenedQuery("Self Closed");
                    else if (mEditTextResolverResponse.getText().toString().trim().length() > 0)
                        closeOpenedQuery(mEditTextResolverResponse.getText().toString());
                    else
                        Toast.makeText(QueryTicketDetailsActivity.this,"Give some response.",Toast.LENGTH_SHORT).show();

                }
                else if(mTextViewAction.getText().equals("Re Open"))
                    reOpenClosedQuery();
            }
        });

        mTextViewTaggedOrderId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent searchActivity = new Intent(QueryTicketDetailsActivity.this, SearchActivity.class);
                searchActivity.putExtra("whatToSearch",Constants.SEARCH_AND_CHOOSE_ORDER);
                startActivityForResult(Intent.createChooser(searchActivity, "Complete action using"), RC_ORDER_ID_PICKER);
            }
        });

        mTextViewUnderProcess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                queryUnderProcess();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_ORDER_ID_PICKER) {
            if (resultCode == RESULT_OK) {
                String orderId  = data.getStringExtra("returnText");
                if(orderId != null && orderId.trim().length()>0)
                    mTextViewTaggedOrderId.setText(orderId);
                else
                    mTextViewTaggedOrderId.setText("Click to tag your order");

            } else if (resultCode == RESULT_CANCELED) {
                // Unable to pick this file
                // progressBar.setVisibility(View.GONE);
                Toast.makeText(this, "Something went wrong.", Toast.LENGTH_SHORT).show();
                mTextViewTaggedOrderId.setText("Click to tag your order");
            }
        }

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
        /* Add back button to the action bar */
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mTextViewQueryId = (TextView) findViewById(R.id.text_view_query_id);
        mTextViewQueryPostedOn = (TextView) findViewById(R.id.text_view_query_posted_on);
        mTextViewQueryClosedOn = (TextView) findViewById(R.id.text_view_query_closed_on);

        mTextViewQueryPostedBy = (TextView) findViewById(R.id.text_view_query_posted_by);
        mTextViewQueryClosedBy = (TextView) findViewById(R.id.text_view_query_closed_by);

        mTextViewUnderProcess = (TextView) findViewById(R.id.text_view_under_process);
        mTextViewAction = (TextView) findViewById(R.id.text_view_action);
        mTextViewPageMessage = (TextView) findViewById(R.id.text_view_page_message);

        mRadioGroupContactBy = (RadioGroup) findViewById(R.id.radio_group_contact_by);
        mRadioButtonPhone = (RadioButton) findViewById(R.id.radio_button_phone);
        mRadioButtonMail = (RadioButton) findViewById(R.id.radio_button_mail);

        mSpinnerQueryTitle = (Spinner) findViewById(R.id.spinner_query_title);

        mEditTextQueryText = (EditText) findViewById(R.id.edit_text_query_text);
        mEditTextResolverResponse = (EditText) findViewById(R.id.edit_text_resolver_response);
        mTextViewQueryTitle = (TextView) findViewById(R.id.text_view_query_title);
        mTextViewResolverResponse = (TextView) findViewById(R.id.text_view_resolver_response);
        mTextViewQueryText = (TextView) findViewById(R.id.text_view_query_text);
        mTextViewTaggedOrderId = (TextView) findViewById(R.id.text_view_tagged_order_id);
    }

    public void getCurrentQuery()
    {
        mFirebaseCurrentQueryRef = new Firebase(Constants.FIREBASE_URL_SAIRA_All_QUERIES)/*.child(Utils.encodeEmail(mCurrentUser.getEmail()))*/
                .child(mCurrentQueryId);

        mFirebaseCurrentQueryRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    mCurrentQuery = dataSnapshot.getValue(Query.class);
                    if(mCurrentQuery == null)
                        finish();

                    setupForOldPostedQuery();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void setupForNewQuery()
    {
        mRadioButtonMail.setClickable(true);
        mRadioButtonPhone.setClickable(true);

        mEditTextQueryText.setEnabled(true);
        mEditTextQueryText.setText(null);

        mTextViewQueryTitle.setVisibility(View.GONE);

        mTextViewTaggedOrderId.setEnabled(true);

        mTextViewQueryId.setVisibility(View.GONE);
        mTextViewPageMessage.setVisibility(View.GONE);
        mTextViewQueryClosedOn.setVisibility(View.GONE);
        mTextViewQueryPostedOn.setVisibility(View.GONE);
        mTextViewQueryPostedBy.setVisibility(View.GONE);
        mTextViewQueryClosedBy.setVisibility(View.GONE);

        mTextViewAction.setText("Post");

        mSpinnerQueryTitle.setVisibility(View.VISIBLE);
        ArrayAdapter<CharSequence> queryTitleAdapter;
        queryTitleAdapter = ArrayAdapter.createFromResource(this,
                    R.array.query_title_options, android.R.layout.simple_spinner_item);
        queryTitleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerQueryTitle.setAdapter(queryTitleAdapter);
        mSpinnerQueryTitle.setSelection(0);

        if(mCurrentUser.getPhoneNo() == null)
            mRadioButtonPhone.setEnabled(false);
    }

    public void setupForOldPostedQuery()
    {
        mRadioButtonMail.setClickable(false);
        mRadioButtonPhone.setClickable(false);

        mEditTextQueryText.setVisibility(View.GONE);

        mTextViewQueryClosedBy.setVisibility(View.GONE);

        mTextViewQueryText.setVisibility(View.VISIBLE);
        mTextViewQueryText.setText(mTextViewQueryText.getText() + mCurrentQuery.getQueryText());

        mTextViewQueryTitle.setVisibility(View.VISIBLE);
        mTextViewQueryTitle.setText(mTextViewQueryTitle.getText() + mCurrentQuery.getQueryTitle());

        mSpinnerQueryTitle.setVisibility(View.GONE);

        mTextViewTaggedOrderId.setEnabled(false);
        if (mCurrentQuery.getTaggedOrderId() == null)
            mTextViewTaggedOrderId.setVisibility(View.GONE);
        else {
            mTextViewTaggedOrderId.setVisibility(View.VISIBLE);
            mTextViewTaggedOrderId.setText("Tagged OrderId : " + mCurrentQuery.getTaggedOrderId());
        }


        mTextViewQueryId.setVisibility(View.VISIBLE);
        mTextViewQueryId.setText(mCurrentQuery.getQueryId());

        mTextViewPageMessage.setVisibility(View.VISIBLE);
        mTextViewPageMessage.setText(mCurrentQuery.getQueryStatus());
        setPageMessage();


        if (mCurrentQuery.getTimestampCreated() != null) {
            mTextViewQueryPostedOn.setVisibility(View.VISIBLE);
            mTextViewQueryPostedOn.setText("Posted on : "+Utils.SIMPLE_DATE_ONLY_FORMAT.format(
                    new Date(mCurrentQuery.getTimestampCreatedLong())));
        }

        if (mCurrentQuery.getQueryStatus().equals(Constants.QUERY_STATUS_SOLVED)) {

            if (mCurrentQuery.getTimestampQueryClosed() != null){
                mTextViewQueryClosedOn.setVisibility(View.VISIBLE);
                mTextViewQueryClosedOn.setText("Closed on : "+Utils.SIMPLE_DATE_ONLY_FORMAT.format(
                        new Date(mCurrentQuery.getTimestampQueryClosedLong())));
            }
            else
                mTextViewQueryClosedOn.setVisibility(View.GONE);

            if (mCurrentQuery.getQueryResolverReply() != null)
            {
                mTextViewResolverResponse.setVisibility(View.VISIBLE);
                mTextViewResolverResponse.setText(mTextViewResolverResponse.getText() +  mCurrentQuery.getQueryResolverReply());
            }
            else
                mTextViewResolverResponse.setVisibility(View.GONE);

        }
        else
        {
            mTextViewQueryClosedOn.setVisibility(View.GONE);
            mTextViewResolverResponse.setVisibility(View.GONE);
        }


        //For Action button
        if (mCurrentUser.getUserType().equals(Constants.USER_TYPE_END_USER))
        {
            if (mCurrentQuery.getQueryStatus().equals(Constants.QUERY_STATUS_SOLVED))
            {
                mTextViewAction.setText("Re Open");
                mTextViewAction.setBackgroundResource(R.color.tw__composer_red);
            }
            else
            {
                mTextViewAction.setText("Close");
                mTextViewAction.setBackgroundResource(R.color.primary);
            }

            mTextViewQueryPostedBy.setVisibility(View.GONE);
        }
        else
        {
            if (mCurrentQuery.getQueryStatus().equals(Constants.QUERY_STATUS_SOLVED))
                mTextViewAction.setVisibility(View.GONE);
            else
            {
                mTextViewAction.setText("Close");
                mTextViewAction.setBackgroundResource(R.color.primary);
            }

            mTextViewQueryPostedBy.setVisibility(View.VISIBLE);
            mTextViewQueryPostedBy.setText(mTextViewQueryPostedBy.getText() + mCurrentQuery.getQueryPostedBy());

        }

        //For under process button
        if (!mCurrentUser.getUserType().equals(Constants.USER_TYPE_END_USER) &&
                !mCurrentQuery.getQueryStatus().equals(Constants.QUERY_STATUS_SOLVED)) {
            mEditTextResolverResponse.setVisibility(View.VISIBLE);
            mTextViewUnderProcess.setVisibility(View.VISIBLE);
        }
        else {
            mEditTextResolverResponse.setVisibility(View.GONE);
            mTextViewUnderProcess.setVisibility(View.GONE);
        }

        //Special visibility for owner(s)
        //It should always be called at the end of the current method
        if (mCurrentUser.getUserType().equals(Constants.USER_TYPE_OWNER))
            extraVisibilityForOwner();
    }

    private void postNewQuery()
    {
        if (mSpinnerQueryTitle.getSelectedItemPosition() == 0){
            Toast.makeText(this, "Please select your reason.", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (mEditTextQueryText.getText().toString().trim().length() < 1) {
            Toast.makeText(this, "Query field can't be empty.", Toast.LENGTH_SHORT).show();
            return;
        }

        String contactBy = mRadioButtonMail.getText().toString();
        if (mRadioGroupContactBy.getCheckedRadioButtonId() == R.id.radio_button_phone)
            contactBy = mRadioButtonPhone.getText().toString();

        String taggedOrderId = null;
        if (!mTextViewTaggedOrderId.getText().equals("Click to tag your order"))
            taggedOrderId = mTextViewTaggedOrderId.getText().toString();

        QueryOperations operationObject = new QueryOperations(this);
        mCurrentQueryId = operationObject.PostNewQuery(mSpinnerQueryTitle.getSelectedItem().toString(),mEditTextQueryText.getText().toString(),contactBy,taggedOrderId);

        if (mCurrentQueryId != null)
          getCurrentQuery();


    }

    private void closeOpenedQuery(String resolverReply)
    {
        HashMap<String, Object> timestampClosedAt = new HashMap<>();
        timestampClosedAt.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);

        QueryOperations operationObject = new QueryOperations(this);
        mCurrentQuery.setTimestampQueryClosed(timestampClosedAt);
        mCurrentQuery.setQueryStatus(Constants.QUERY_STATUS_SOLVED);
        mCurrentQuery.setQueryResolvedBy(mCurrentUser.getEmail());
        mCurrentQuery.setQueryResolverReply(resolverReply);

        operationObject.updateQuery(mCurrentQuery);
        getCurrentQuery();
    }

    private void reOpenClosedQuery()
    {
        QueryOperations operationObject = new QueryOperations(this);

        mCurrentQuery.setQueryStatus(Constants.QUERY_STATUS_REOPENED);
        operationObject.updateQuery(mCurrentQuery);
        getCurrentQuery();
    }

    private void queryUnderProcess()
    {
        QueryOperations operationObject = new QueryOperations(this);

        mCurrentQuery.setQueryStatus(Constants.QUERY_STATUS_UNDER_PROCESS);
        operationObject.updateQuery(mCurrentQuery);
        getCurrentQuery();
    }

    public void setPageMessage()
    {
        int textColorCode,backgroundColorCode;

        if(mCurrentQuery.getQueryStatus().equals(Constants.QUERY_STATUS_UNDER_PROCESS))
        {
            textColorCode = QueryTicketDetailsActivity.this.getResources().getColor(R.color.under_process_message_text_color);
            backgroundColorCode = R.color.under_process_message_background_color;
        }
        else if(mCurrentQuery.getQueryStatus().equals(Constants.QUERY_STATUS_SOLVED))
        {
            textColorCode = QueryTicketDetailsActivity.this.getResources().getColor(R.color.success_message_text_color);
            backgroundColorCode = R.color.success_message_background_color;
        }
        else {
            textColorCode = QueryTicketDetailsActivity.this.getResources().getColor(R.color.fail_message_text_color);
            backgroundColorCode = R.color.fail_message_background_color;
        }

        mTextViewPageMessage.setBackgroundResource(backgroundColorCode);
        mTextViewPageMessage.setTextColor(textColorCode);

    }

    public void extraVisibilityForOwner()
    {
        if (mCurrentQuery.getTimestampQueryClosed() != null){
            mTextViewQueryClosedOn.setVisibility(View.VISIBLE);
            mTextViewQueryClosedOn.setText(mTextViewQueryClosedOn.getText() + Utils.SIMPLE_DATE_ONLY_FORMAT.format(
                    new Date(mCurrentQuery.getTimestampQueryClosedLong())));

            mTextViewQueryClosedBy.setVisibility(View.VISIBLE);
            mTextViewQueryClosedBy.setText(mTextViewQueryClosedBy.getText() + mCurrentQuery.getQueryResolvedBy() );

            mTextViewResolverResponse.setVisibility(View.VISIBLE);
            mTextViewResolverResponse.setText(mTextViewResolverResponse.getText() +  mCurrentQuery.getQueryResolverReply());

        }

    }


}
