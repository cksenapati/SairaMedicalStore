package com.example.android.sairamedicalstore.ui;

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
    RecyclerView.Adapter recyclerViewAdapter;
    String mCurrentDisplayingImageURI;

    RelativeLayout mRelativeLayoutViewPrescription;
    LinearLayout mLinearLayoutForPrescriptionsRecyclerView;
    ImageView mImageViewPrescriptionSelectionOption,mImageViewUploadNewPrescription;
    public static ImageView mImageViewPrescription;
    TextView mTextViewNoPrescriptions;
    public static TextView mTextViewPrescriptionName;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prescriptions);

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

    }


    private void setPrescriptionDetails(Prescription prescription)
    {
        mTextViewNoPrescriptions.setVisibility(View.GONE);
        mTextViewPrescriptionName.setVisibility(View.VISIBLE);
        mImageViewPrescriptionSelectionOption.setVisibility(View.VISIBLE);
        mImageViewPrescription.setVisibility(View.VISIBLE);

        mCurrentDisplayingImageURI = prescription.getPrescriptionPages().get("page1");
        setImageViewPrescription();
        mTextViewPrescriptionName.setText(Utils.toLowerCaseExceptFirstLetter(prescription.getPrescriptionName()));

    }

    private void setImageViewPrescription()
    {
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
        mTextViewNoPrescriptions = (TextView) findViewById(R.id.text_view_no_prescriptions);
        mTextViewPrescriptionName = (TextView) findViewById(R.id.text_view_prescription_name);


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

                        setPrescriptionDetails(arrayListPrescriptions.get(0));
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
