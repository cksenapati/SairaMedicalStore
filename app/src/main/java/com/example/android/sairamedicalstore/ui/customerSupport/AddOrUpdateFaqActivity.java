package com.example.android.sairamedicalstore.ui.customerSupport;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.sairamedicalstore.R;
import com.example.android.sairamedicalstore.models.Faq;
import com.example.android.sairamedicalstore.operations.FaqOperations;
import com.example.android.sairamedicalstore.utils.Constants;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;


public class AddOrUpdateFaqActivity extends AppCompatActivity {

    EditText mEditTextQuestion,mEditTextAnswer;
    FloatingActionButton mFabAddAnswer;
    TextView mTextViewAction,mTextViewDelete;
    LinearLayout mLinearLayoutAllAnswers;

    Firebase mFirebaseCurrentFaqRef;

    String mCurrentFaqId;
    Faq mCurrentFaq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_or_update_faq);

        Intent intent = getIntent();
        if (intent != null) {
            mCurrentFaqId = intent.getStringExtra("FaqId");
        }

        initializeScreen();

        if(mCurrentFaqId == null)
            setupForNewFaq();
        else
            setupForFaqUpdate();

        mFabAddAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAllEditTextBoxesFilled())
                    addNewEditText(null);
                else
                    Toast.makeText(AddOrUpdateFaqActivity.this,"Fields can't left empty.",Toast.LENGTH_SHORT).show();
            }
        });

        mTextViewAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isAllEditTextBoxesFilled())
                    addOrUpdateFaq();
                else
                    Toast.makeText(AddOrUpdateFaqActivity.this,"Fields can't left empty.",Toast.LENGTH_SHORT).show();

            }
        });

        mTextViewDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCurrentFaq.getFaqId() != null)
                {
                    FaqOperations object = new FaqOperations(AddOrUpdateFaqActivity.this);
                    object.DeleteFaq(mCurrentFaq.getFaqId());
                }
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

        /* Add back button to the action bar */
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mLinearLayoutAllAnswers = (LinearLayout) findViewById(R.id.linear_layout_all_answers);

        mEditTextQuestion = (EditText) findViewById(R.id.edit_text_question);
        mEditTextAnswer = (EditText) findViewById(R.id.edit_text_answer);

        mFabAddAnswer = (FloatingActionButton) findViewById(R.id.fab_add_more_answer);

        mTextViewAction = (TextView) findViewById(R.id.text_view_action);
        mTextViewDelete = (TextView) findViewById(R.id.text_view_delete);
    }

    public void setupForNewFaq()
    {
        setTitle("New FAQ");
        mTextViewAction.setText("Create");

    }

    public void setupForFaqUpdate()
    {
        setTitle("Update FAQ");
        mTextViewAction.setText("Update");
        mTextViewDelete.setVisibility(View.VISIBLE);

        mFirebaseCurrentFaqRef = new Firebase(Constants.FIREBASE_URL_SAIRA_All_FAQS).child(mCurrentFaqId);

        mFirebaseCurrentFaqRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    mCurrentFaq = dataSnapshot.getValue(Faq.class);
                    if (mCurrentFaq == null)
                        finish();

                    displayFaqDetails();
                }
                else
                    finish();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void displayFaqDetails()
    {
        mEditTextQuestion.setText(mCurrentFaq.getFaqQuestion());

        clearAllDynamicallyAddedEditTextBoxes();

        ArrayList<String> sortedKeys =
                new ArrayList<String>(mCurrentFaq.getFaqAnswers().keySet());

        Collections.sort(sortedKeys);

        // Display the TreeMap which is naturally sorted
        int count = 0;

        for (String x : sortedKeys)
        {
            if(count == 0)
                mEditTextAnswer.setText(mCurrentFaq.getFaqAnswers().get(x).toString());
            else
                addNewEditText(mCurrentFaq.getFaqAnswers().get(x).toString());

            count++;
        }

        /*for (Map.Entry<String, Object> entry : mCurrentFaq.getFaqAnswers().entrySet()) {

            if(count == 0)
                mEditTextAnswer.setText(entry.getValue().toString());
            else
                addNewEditText(entry.getValue().toString());

            count++;
        }*/
    }

    public void clearAllDynamicallyAddedEditTextBoxes()
    {
        int count = 1;
        for( int i = 0; i < mLinearLayoutAllAnswers.getChildCount(); i++ ){
            View view = mLinearLayoutAllAnswers.getChildAt(i);
            if (view instanceof EditText) {
                try {
                    mLinearLayoutAllAnswers.removeViewAt(count);
                }catch (Exception ex){}

                count++;
            }
        }
    }

    public boolean isAllEditTextBoxesFilled()
    {
        if (mEditTextQuestion.getText().toString().trim().length() < 1)
            return false;

        for( int i = 0; i < mLinearLayoutAllAnswers.getChildCount(); i++ ){
            View view = mLinearLayoutAllAnswers.getChildAt(i);
            if (view instanceof EditText) {
                if(((EditText)view).getText().toString().trim().length() < 1)
                    return false;
            }
        }

        return true;
    }

    private void addNewEditText(String textToBeFilled)
    {
        EditText myEditText = new EditText(AddOrUpdateFaqActivity.this);
        myEditText.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT)); // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
        myEditText.setHint("Answer");


        int padding_in_dp = 7;
        final float scale = getResources().getDisplayMetrics().density;
        int padding_in_px = (int) (padding_in_dp * scale + 0.5f);

        myEditText.setPadding(padding_in_px,padding_in_px,padding_in_px,padding_in_px);
        myEditText.setBackgroundResource(R.color.tw__solid_white);

        TableLayout.LayoutParams params = new TableLayout.LayoutParams();
        int margin = (int) getResources().getDimension(R.dimen.extra_small_margin);
        params.setMargins(margin, margin, margin, margin);
        myEditText.setLayoutParams(params);

        myEditText.requestFocus();

        if (textToBeFilled != null)
            myEditText.setText(textToBeFilled);


        mLinearLayoutAllAnswers.addView(myEditText);
    }

    public void addOrUpdateFaq()
    {
        FaqOperations object = new FaqOperations(this);
        if (mCurrentFaqId == null) {
            object.AddNewFaq(mEditTextQuestion.getText().toString().toUpperCase(), 100, collectAllAnswers());
        }
        else {
            mCurrentFaq.setFaqQuestion(mEditTextQuestion.getText().toString().toUpperCase());
            mCurrentFaq.setFaqAnswers(collectAllAnswers());
            object.UpdateOldPoster(mCurrentFaq);
        }
    }

    private HashMap<String, Object> collectAllAnswers()
    {
        HashMap<String, Object> AllAnswers = new HashMap<>();

        int count = 1;
        for( int i = 0; i < mLinearLayoutAllAnswers.getChildCount(); i++ ){
            View view = mLinearLayoutAllAnswers.getChildAt(i);
            if (view instanceof EditText) {
                if(((EditText)view).getText().toString().trim().length() >= 1) {
                    AllAnswers.put("answer" + count, ((EditText) view).getText().toString());
                    count++;
                }

            }
        }
        return AllAnswers;
    }

}
