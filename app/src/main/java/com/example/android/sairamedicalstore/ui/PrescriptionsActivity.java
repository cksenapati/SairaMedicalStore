package com.example.android.sairamedicalstore.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.android.sairamedicalstore.R;
import com.example.android.sairamedicalstore.SairaMedicalStoreApplication;
import com.example.android.sairamedicalstore.models.Prescription;
import com.example.android.sairamedicalstore.models.User;
import com.example.android.sairamedicalstore.operations.PrescriptionOperations;
import com.example.android.sairamedicalstore.ui.search.SearchActivity;
import com.example.android.sairamedicalstore.utils.Constants;
import com.example.android.sairamedicalstore.utils.Utils;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;



public class PrescriptionsActivity extends AppCompatActivity {

    User mCurrentUser;
    ArrayList<Prescription> arrayListPrescriptions;
    public static ArrayList<String> arrayListSelectedPrescriptionIds;

    RecyclerView.Adapter recyclerViewAdapter;
    String mCurrentDisplayingImageURI;
    public static int mCurrentPageNumber;
    public static Prescription mCurrentPrescription;

    RelativeLayout mRelativeLayoutViewPrescription;
    LinearLayout mLinearLayoutForPrescriptionsRecyclerView;
    public static ImageView mImageViewPrescriptionSelectionOption;
    ImageView mImageViewUploadNewPrescription,mImageViewLeftArrow,mImageViewRightArrow;
    public static ImageView mImageViewPrescription;
    TextView mTextViewNoPrescriptions,mTextViewDone;
    public static TextView mTextViewPrescriptionName;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prescriptions);

        arrayListSelectedPrescriptionIds = new ArrayList<>();

        Intent intent = getIntent();
        if (intent != null && intent.getStringArrayListExtra("arrayListSelectedPrescriptionIds")!= null) {
            arrayListSelectedPrescriptionIds = intent.getStringArrayListExtra("arrayListSelectedPrescriptionIds");
        }

        mCurrentUser = ((SairaMedicalStoreApplication) this.getApplication()).getCurrentUser();


        initialization();
        setPrescriptionsRecyclerView();

        mImageViewUploadNewPrescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentAddNewPrescription = new Intent(PrescriptionsActivity.this,AddNewPrescriptionActivity.class);
                startActivity(intentAddNewPrescription);
            }
        });

        mImageViewRightArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCurrentPrescription.getPrescriptionPages().size() <= mCurrentPageNumber)
                    mCurrentPageNumber = 1;
                else
                    mCurrentPageNumber++;
                setImageViewPrescription();
            }
        });

        mImageViewLeftArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCurrentPageNumber <= 1)
                    mCurrentPageNumber = mCurrentPrescription.getPrescriptionPages().size();
                else
                    mCurrentPageNumber--;
                setImageViewPrescription();
            }
        });

        mImageViewPrescriptionSelectionOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index=-1;
                for (String eachPrescriptionId: arrayListSelectedPrescriptionIds) {
                    index++;
                    if (eachPrescriptionId.equals(mCurrentPrescription.getPrescriptionId()))
                    {
                        arrayListSelectedPrescriptionIds.remove(index);
                        mImageViewPrescriptionSelectionOption.setImageResource(R.drawable.ic_check_48);
                        return;
                    }
                }
                arrayListSelectedPrescriptionIds.add(mCurrentPrescription.getPrescriptionId());
                mImageViewPrescriptionSelectionOption.setImageResource(R.drawable.ic_checked_48);

            }
        });

        mTextViewDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(arrayListSelectedPrescriptionIds.size()>0)
                {
                    Intent intentToCartActivity = new Intent(PrescriptionsActivity.this, CartActivity.class);
                    intentToCartActivity.putExtra("arrayListSelectedPrescriptionIds", arrayListSelectedPrescriptionIds);
                    setResult(Activity.RESULT_OK, intentToCartActivity);
                    finish();
                }
                else
                    Toast.makeText(PrescriptionsActivity.this,"Please select prescription(s).",Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void setPrescriptionDetails()
    {
        mTextViewNoPrescriptions.setVisibility(View.GONE);
        mTextViewPrescriptionName.setVisibility(View.VISIBLE);
        mImageViewPrescriptionSelectionOption.setVisibility(View.VISIBLE);
        checkWhetherPrescriptionIsSelected();
        mImageViewPrescription.setVisibility(View.VISIBLE);

        mCurrentPageNumber = 1;
        setImageViewPrescription();
        mTextViewPrescriptionName.setText(Utils.toLowerCaseExceptFirstLetter(mCurrentPrescription.getPrescriptionName()));

    }

    public void checkWhetherPrescriptionIsSelected()
    {
        for (String eachPrescriptionId:arrayListSelectedPrescriptionIds) {
            if(eachPrescriptionId.equals(mCurrentPrescription.getPrescriptionId()))
            {
                mImageViewPrescriptionSelectionOption.setImageResource(R.drawable.ic_checked_48);
                return;
            }
        }

        mImageViewPrescriptionSelectionOption.setImageResource(R.drawable.ic_check_48);

    }

    private void setImageViewPrescription()
    {
        mCurrentDisplayingImageURI = mCurrentPrescription.getPrescriptionPages().get("page"+mCurrentPageNumber);
        Glide.with(mImageViewPrescription.getContext())
                .load(mCurrentDisplayingImageURI)
                .into(mImageViewPrescription);
    }

    public void initialization()
    {
        mLinearLayoutForPrescriptionsRecyclerView = (LinearLayout) findViewById(R.id.linear_layout_for_prescriptions_recycler_view);
        mImageViewPrescription = (ImageView) findViewById(R.id.image_view_prescription);
        mImageViewPrescriptionSelectionOption = (ImageView) findViewById(R.id.image_view_prescription_selection_option);
        mImageViewUploadNewPrescription = (ImageView) findViewById(R.id.image_view_upload_new_prescription);
        mImageViewLeftArrow = (ImageView) findViewById(R.id.image_view_left_arrow);
        mImageViewRightArrow = (ImageView) findViewById(R.id.image_view_right_arrow);

        mTextViewNoPrescriptions = (TextView) findViewById(R.id.text_view_no_prescriptions);
        mTextViewPrescriptionName = (TextView) findViewById(R.id.text_view_prescription_name);
        mTextViewDone = (TextView) findViewById(R.id.text_view_done);

        arrayListPrescriptions = new ArrayList<>();
    }

    public void setPrescriptionsRecyclerView()
    {
        Firebase firebaseAllPrescriptionsRef = new Firebase(Constants.FIREBASE_URL_SAIRA_All_PRESCRIPTIONS);

        Firebase firebaseCurrentUserPrescriptionsRef = firebaseAllPrescriptionsRef.child(Utils.encodeEmail(mCurrentUser.getEmail()));
        firebaseCurrentUserPrescriptionsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    arrayListPrescriptions.clear();
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                        Prescription eachPrescription = snapshot.getValue(Prescription.class);
                        if(eachPrescription != null)
                         arrayListPrescriptions.add(eachPrescription);
                    }

                    if(arrayListPrescriptions != null)
                    {
                        recyclerView = new RecyclerView(PrescriptionsActivity.this);
                        recyclerView.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.MATCH_PARENT));

                        RecyclerView.LayoutManager layoutManager  = new LinearLayoutManager(PrescriptionsActivity.this,LinearLayoutManager.HORIZONTAL,false);
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setLayoutManager(layoutManager);
                        recyclerViewAdapter = new AllAvailablePrescriptionsAdapter(arrayListPrescriptions,PrescriptionsActivity.this);
                        recyclerView.setAdapter(recyclerViewAdapter);

                        mLinearLayoutForPrescriptionsRecyclerView.addView(recyclerView);

                        mCurrentPrescription = arrayListPrescriptions.get(0);
                        setPrescriptionDetails();
                    }

                }
                else
                {
                    mTextViewNoPrescriptions.setVisibility(View.VISIBLE);
                    mImageViewPrescription.setVisibility(View.GONE);
                    mImageViewPrescriptionSelectionOption.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }


}
