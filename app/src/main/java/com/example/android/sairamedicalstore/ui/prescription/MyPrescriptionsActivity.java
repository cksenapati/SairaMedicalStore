package com.example.android.sairamedicalstore.ui.prescription;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.sairamedicalstore.R;
import com.example.android.sairamedicalstore.SairaMedicalStoreApplication;
import com.example.android.sairamedicalstore.models.Prescription;
import com.example.android.sairamedicalstore.models.PrescriptionPage;
import com.example.android.sairamedicalstore.models.User;
import com.example.android.sairamedicalstore.operations.PrescriptionOperations;
import com.example.android.sairamedicalstore.ui.cart.CartActivity;
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
import java.util.HashMap;
import java.util.Map;


public class MyPrescriptionsActivity extends AppCompatActivity {

    public static TextView mTextViewNewOrUpdatePrescription,mTextViewSaveOrUpdatePrescription;
    TextView mTextViewAddNewPage,mTextViewShowMorePrescriptions,mTextViewDone,mTextViewSelectedPrescriptions,mTextViewActivityName;
    LinearLayout /*mLinearLayoutActivePrescriptionAllPages,*/mLinearLayoutForRecyclerView;
    public static RecyclerView mRecyclerViewActivePrescriptionAllPages;
    ScrollView mScrollView;
    EditText mEditTextSearchPrescription;
    ImageView mImageViewGoBack,mImageViewSearchPrescription;

    private FirebaseStorage mFirebaseStorage;
    private StorageReference mFirebaseActivePrescriptionStorageRef;
    Firebase mFirebaseAllPrescriptionsRef;


    User mCurrentUser;
    public static Prescription mActivePrescription;
    public static String mActivePrescriptionIdForNewPrescription;
    String mActivityVisitPurpose,mSearchedPrescriptionName;
    int mMaxNumberOfPrescriptions;

    public static ArrayList<PrescriptionPage> mArrayListAllPagesOfActivePrescription;
    ArrayList<Prescription> mArrayListSelectedPrescriptions;
    public static HashMap<String,Uri> mHashMapNewUploadedPageStorageUris;

    private static final int RC_PRESCRIPTION_PAGE_PICKER =  1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_prescriptions);


        Intent intent = getIntent();
        if (intent != null) {
            mActivityVisitPurpose = intent.getStringExtra("activityVisitPurpose");
            mArrayListSelectedPrescriptions = (ArrayList<Prescription>) intent.getSerializableExtra("arrayListSelectedPrescriptions");
            mActivePrescription = (Prescription) intent.getSerializableExtra("activePrescription");
        }

        if (mArrayListSelectedPrescriptions == null)
            mArrayListSelectedPrescriptions = new ArrayList<>();

        mCurrentUser = ((SairaMedicalStoreApplication) this.getApplication()).getCurrentUser();

        initialization();

        displaySavedPrescriptions();

        mTextViewAddNewPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent imagePickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
                imagePickerIntent.setType("image/*");
                imagePickerIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(imagePickerIntent, "Complete action using"), RC_PRESCRIPTION_PAGE_PICKER);
            }
        });

        mTextViewSaveOrUpdatePrescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mArrayListAllPagesOfActivePrescription.size() <= 0)
                {
                    Toast.makeText(MyPrescriptionsActivity.this,"Add Pics to Update prescription",Toast.LENGTH_SHORT).show();
                    return;
                }

                if (mTextViewSaveOrUpdatePrescription.getText().equals("Save"))
                    openSaveDataDialog();
                else
                    updateOldPrescription();
            }
        });

        mTextViewShowMorePrescriptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMaxNumberOfPrescriptions += 5;
                displaySavedPrescriptions();
            }
        });

        if (mActivityVisitPurpose != null && mActivityVisitPurpose.equals(Constants.ACTIVITY_VISIT_PURPOSE_SELECT))
            mTextViewDone.setVisibility(View.VISIBLE);
        else
            mTextViewDone.setVisibility(View.GONE);

        mTextViewDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mArrayListSelectedPrescriptions == null || mArrayListSelectedPrescriptions.size()<1) {
                    Toast.makeText(MyPrescriptionsActivity.this, "Please select prescription(s).", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intentToCartActivity = new Intent(MyPrescriptionsActivity.this, CartActivity.class);
                intentToCartActivity.putExtra("arrayListSelectedPrescriptions", mArrayListSelectedPrescriptions);
                setResult(Activity.RESULT_OK, intentToCartActivity);
                finish();
            }
        });

        mImageViewGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEditTextSearchPrescription.getVisibility() == View.VISIBLE) {
                    mTextViewActivityName.setVisibility(View.VISIBLE);
                    mEditTextSearchPrescription.setVisibility(View.GONE);
                    mImageViewSearchPrescription.setVisibility(View.VISIBLE);
                    mSearchedPrescriptionName = "";
                    displaySavedPrescriptions();
                }
                else
                {
                    MyPrescriptionsActivity.this.finish();
                }
            }
        });

        mImageViewSearchPrescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mImageViewSearchPrescription.setVisibility(View.GONE);
                mEditTextSearchPrescription.setVisibility(View.VISIBLE);
                mTextViewActivityName.setVisibility(View.GONE);
            }
        });

        mEditTextSearchPrescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mEditTextSearchPrescription.getText().toString().trim().length() > 0)
                    mSearchedPrescriptionName = mEditTextSearchPrescription.getText().toString().toUpperCase();
                else
                    mSearchedPrescriptionName = "";

                displaySavedPrescriptions();
            }
        });

        if (mActivityVisitPurpose != null && mActivityVisitPurpose.equals(Constants.ACTIVITY_VISIT_PURPOSE_SELECT))
            mTextViewActivityName.setText("Select Prescription");
        else
            mTextViewActivityName.setText("My Prescriptions");

        if (mActivePrescription != null)
            setupForPrescriptionUpdateProcess();

        showOrHideSelectedPrescriptions();


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_PRESCRIPTION_PAGE_PICKER)
        {
            if(resultCode == RESULT_OK){

                StorageReference selectedPhotoRef = mFirebaseStorage.getReference().child(Constants.FIREBASE_LOCATION_TEMPORARY_STORAGE)
                        .child(data.getData().getLastPathSegment());

                UploadTask uploadTask = selectedPhotoRef.putFile(data.getData());
                uploadTask.addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // When the image has successfully uploaded, we get its download URL
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        try {
                            addNewPageToActivePrescription(downloadUrl.toString(),data.getData());
                        }catch (Exception ex){}
                    }
                });
            }
            else if (resultCode == RESULT_CANCELED)
            {
                // Unable to pick this file
                Toast.makeText(this, "Unable to pick this image file", Toast.LENGTH_SHORT).show();
                //finish();
            }
        }

    }

    public void addNewPageToActivePrescription(String imageDownloadURI, Uri storageUri)
    {

        String PrescriptionPageId = mFirebaseAllPrescriptionsRef.child(mActivePrescriptionIdForNewPrescription).push().getKey();
        PrescriptionPage newPage = new PrescriptionPage(PrescriptionPageId,imageDownloadURI,mActivePrescriptionIdForNewPrescription,true,null,null);
        mArrayListAllPagesOfActivePrescription.add(newPage);

        mHashMapNewUploadedPageStorageUris.put(PrescriptionPageId,storageUri);

        displayActivePrescriptionPages();
    }

    public void displayActivePrescriptionPages()
    {
        mRecyclerViewActivePrescriptionAllPages.setAdapter(null);

        RecyclerView.LayoutManager layoutManager  = new LinearLayoutManager(MyPrescriptionsActivity.this,LinearLayoutManager.HORIZONTAL,false);
        mRecyclerViewActivePrescriptionAllPages.setHasFixedSize(true);
        mRecyclerViewActivePrescriptionAllPages.setLayoutManager(layoutManager);
        RecyclerView.Adapter adapter = new DisplayPrescriptionPagesAdapter(mArrayListAllPagesOfActivePrescription,true,MyPrescriptionsActivity.this);
        mRecyclerViewActivePrescriptionAllPages.setAdapter(adapter);

    }

    public void initialization()
    {
        mScrollView = (ScrollView) findViewById(R.id.scrollView);

        mRecyclerViewActivePrescriptionAllPages = (RecyclerView) findViewById(R.id.recycler_view_active_prescription_all_pages);
        mLinearLayoutForRecyclerView = (LinearLayout) findViewById(R.id.linear_layout_for_recycler_view);

        mTextViewActivityName = (TextView) findViewById(R.id.text_view_activity_name);
        mTextViewNewOrUpdatePrescription = (TextView) findViewById(R.id.text_view_new_or_update_prescription);
        mTextViewSaveOrUpdatePrescription = (TextView) findViewById(R.id.text_view_save_or_update_prescription);
        mTextViewAddNewPage = (TextView) findViewById(R.id.text_view_add_new_page);
        mTextViewShowMorePrescriptions = (TextView) findViewById(R.id.text_view_show_more_prescriptions);
        mTextViewDone = (TextView) findViewById(R.id.text_view_done);
        mTextViewSelectedPrescriptions = (TextView) findViewById(R.id.text_view_selected_prescription);

        mArrayListAllPagesOfActivePrescription = new ArrayList<>();
        mHashMapNewUploadedPageStorageUris = new HashMap<>();


        mFirebaseAllPrescriptionsRef = new Firebase(Constants.FIREBASE_URL_SAIRA_All_PRESCRIPTIONS);

        mFirebaseStorage = FirebaseStorage.getInstance();
        mActivePrescriptionIdForNewPrescription = mFirebaseAllPrescriptionsRef.push().getKey();

        mMaxNumberOfPrescriptions = 5;

        mEditTextSearchPrescription = (EditText) findViewById(R.id.edit_text_search_prescription);

        mImageViewGoBack = (ImageView) findViewById(R.id.image_view_go_back);
        mImageViewSearchPrescription = (ImageView) findViewById(R.id.image_view_search_prescription);
    }

    public void displaySavedPrescriptions()
    {
        if((mLinearLayoutForRecyclerView).getChildCount() > 0)
            mLinearLayoutForRecyclerView.removeAllViews();
        mTextViewShowMorePrescriptions.setVisibility(View.GONE);

        if (mSearchedPrescriptionName != null && mSearchedPrescriptionName.length() > 0)
            mFirebaseAllPrescriptionsRef.orderByChild("prescriptionName").startAt(mSearchedPrescriptionName).
                    endAt(mSearchedPrescriptionName + "~").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists())
                    {
                        if((mLinearLayoutForRecyclerView).getChildCount() > 0)
                            mLinearLayoutForRecyclerView.removeAllViews();

                        if(dataSnapshot.getChildrenCount() > mMaxNumberOfPrescriptions)
                            mTextViewShowMorePrescriptions.setVisibility(View.VISIBLE);
                        else
                            mTextViewShowMorePrescriptions.setVisibility(View.GONE);

                        int count = 0;
                        for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                            final Prescription currentPrescription = snapshot.getValue(Prescription.class);

                            if (!currentPrescription.getPrescriptionOwnerEmail().equals(mCurrentUser.getEmail()))
                                continue;

                            createRecyclerView(currentPrescription);

                            RecyclerView recyclerView = createRecyclerView(currentPrescription);
                            final LinearLayout linearLayoutName = createLinearLayoutForRecyclerView(currentPrescription);

                            LinearLayout linearLayout = new LinearLayout(MyPrescriptionsActivity.this);
                            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.MATCH_PARENT));
                            linearLayout.setOrientation(LinearLayout.VERTICAL);

                            linearLayout.setBackgroundResource(R.drawable.rectangle_border);
                            linearLayout.setBackgroundColor(getResources().getColor(R.color.tw__composer_white));

                            ViewGroup.MarginLayoutParams marginLayoutParams =
                                    (ViewGroup.MarginLayoutParams) linearLayout.getLayoutParams();
                            marginLayoutParams.setMargins(10, 10, 10, 20);
                            linearLayout.setLayoutParams(marginLayoutParams);

                            linearLayout.addView(linearLayoutName);
                            linearLayout.addView(recyclerView);

                            linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
                                @Override
                                public boolean onLongClick(View v) {
                                    openActionDataDialog(currentPrescription,linearLayoutName);
                                    return false;
                                }
                            });

                            mLinearLayoutForRecyclerView.addView(linearLayout);

                            count++;
                            if (count>=mMaxNumberOfPrescriptions)
                                break;
                        }
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
        else
            mFirebaseAllPrescriptionsRef.orderByChild(Constants.FIREBASE_PROPERTY_PRESCRIPTION_OWNER_EMAIL).
                    equalTo(mCurrentUser.getEmail()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists())
                    {
                        if((mLinearLayoutForRecyclerView).getChildCount() > 0)
                            mLinearLayoutForRecyclerView.removeAllViews();

                        if(dataSnapshot.getChildrenCount() > mMaxNumberOfPrescriptions)
                            mTextViewShowMorePrescriptions.setVisibility(View.VISIBLE);
                        else
                            mTextViewShowMorePrescriptions.setVisibility(View.GONE);

                        int count = 0;
                        for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                            final Prescription currentPrescription = snapshot.getValue(Prescription.class);

                            createRecyclerView(currentPrescription);

                            RecyclerView recyclerView = createRecyclerView(currentPrescription);
                            final LinearLayout linearLayoutName = createLinearLayoutForRecyclerView(currentPrescription);

                            LinearLayout linearLayout = new LinearLayout(MyPrescriptionsActivity.this);
                            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.MATCH_PARENT));
                            linearLayout.setOrientation(LinearLayout.VERTICAL);

                            linearLayout.setBackgroundResource(R.drawable.rectangle_border);
                            linearLayout.setBackgroundColor(getResources().getColor(R.color.tw__composer_white));

                            ViewGroup.MarginLayoutParams marginLayoutParams =
                                    (ViewGroup.MarginLayoutParams) linearLayout.getLayoutParams();
                            marginLayoutParams.setMargins(10, 10, 10, 20);
                            linearLayout.setLayoutParams(marginLayoutParams);

                            linearLayout.addView(linearLayoutName);
                            linearLayout.addView(recyclerView);

                            linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
                                @Override
                                public boolean onLongClick(View v) {
                                    openActionDataDialog(currentPrescription,linearLayoutName);
                                    return false;
                                }
                            });

                            mLinearLayoutForRecyclerView.addView(linearLayout);

                            count++;
                            if (count>=mMaxNumberOfPrescriptions)
                                break;
                        }
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
    }

    public RecyclerView createRecyclerView(Prescription currentPrescription)
    {
        ArrayList<PrescriptionPage> arrayListPrescriptionPages = new ArrayList<>();

        for (Map.Entry<String,PrescriptionPage> eachPage:currentPrescription.getPrescriptionPages().entrySet()) {
            if (eachPage.getValue().isActive())
                arrayListPrescriptionPages.add(eachPage.getValue());
        }

        RecyclerView recyclerView = new RecyclerView(MyPrescriptionsActivity.this);
        recyclerView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));

        ViewGroup.MarginLayoutParams marginLayoutParams =
                (ViewGroup.MarginLayoutParams) recyclerView.getLayoutParams();
        marginLayoutParams.setMargins(0, 5, 0, 5);
        recyclerView.setLayoutParams(marginLayoutParams);

        RecyclerView.LayoutManager layoutManager  = new LinearLayoutManager(MyPrescriptionsActivity.this,LinearLayoutManager.HORIZONTAL,false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        RecyclerView.Adapter adapter = new DisplayPrescriptionPagesAdapter(arrayListPrescriptionPages,false,MyPrescriptionsActivity.this);
        recyclerView.setAdapter(adapter);

        return recyclerView;

    }

    public LinearLayout createLinearLayoutForRecyclerView(final Prescription currentPrescription)
    {
        //---------------Linear layout------------------------------
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));

        ViewGroup.MarginLayoutParams marginLayoutParamsForLinearLayout =
                (ViewGroup.MarginLayoutParams) linearLayout.getLayoutParams();
        marginLayoutParamsForLinearLayout.setMargins(5, 5, 5, 5);
        linearLayout.setLayoutParams(marginLayoutParamsForLinearLayout);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);


        //------------Text box for prescription name-----------------------------
        TextView mTextViewPrescriptionName = new TextView(this);
        mTextViewPrescriptionName.setLayoutParams(new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.MATCH_PARENT,1f));

        ViewGroup.MarginLayoutParams marginLayoutParams =
                (ViewGroup.MarginLayoutParams) mTextViewPrescriptionName.getLayoutParams();
        marginLayoutParams.setMargins(5, 5, 5, 5);
        mTextViewPrescriptionName.setLayoutParams(marginLayoutParams);

        mTextViewPrescriptionName.setText(Utils.toLowerCaseExceptFirstLetter(currentPrescription.getPrescriptionName()));

        linearLayout.addView(mTextViewPrescriptionName);


        //----------------Image View for selection of prescription-----------------------
        if (mActivityVisitPurpose != null && mActivityVisitPurpose.equals(Constants.ACTIVITY_VISIT_PURPOSE_SELECT)) {
            final ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));

            imageView.setImageResource(R.drawable.ic_check_24);
            if (mArrayListSelectedPrescriptions != null && mArrayListSelectedPrescriptions.size() > 0)
            {
                for (Prescription eachPrescription :mArrayListSelectedPrescriptions) {
                    if(eachPrescription.getPrescriptionId().equals(currentPrescription.getPrescriptionId()))
                    {
                        imageView.setImageResource(R.drawable.ic_checked_24);
                        break;
                    }
                }
            }



            ViewGroup.MarginLayoutParams marginLayoutParamsForImageView =
                    (ViewGroup.MarginLayoutParams) imageView.getLayoutParams();
            marginLayoutParamsForImageView.setMargins(5, 5, 5, 5);
            imageView.setLayoutParams(marginLayoutParamsForImageView);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mArrayListSelectedPrescriptions != null && mArrayListSelectedPrescriptions.size() > 0)
                    {
                        int index = 0;
                        for (Prescription eachPrescription :mArrayListSelectedPrescriptions) {
                            if(eachPrescription.getPrescriptionId().equals(currentPrescription.getPrescriptionId()))
                            {
                                imageView.setImageResource(R.drawable.ic_check_24);
                                mArrayListSelectedPrescriptions.remove(index);
                                showOrHideSelectedPrescriptions();
                                return;
                            }
                            index++;
                        }
                        imageView.setImageResource(R.drawable.ic_checked_24);
                        mArrayListSelectedPrescriptions.add(currentPrescription);
                        showOrHideSelectedPrescriptions();
                    }
                    else
                    {
                        imageView.setImageResource(R.drawable.ic_checked_24);
                        mArrayListSelectedPrescriptions.add(currentPrescription);
                        showOrHideSelectedPrescriptions();
                    }
                }
            });


            linearLayout.addView(imageView);
        }

        return linearLayout;

    }

    public void addNewPrescription(String prescriptionName)
    {
        HashMap<String,PrescriptionPage> tempHashMapPrescriptionPages = new HashMap<>();

        for (PrescriptionPage eachPage: mArrayListAllPagesOfActivePrescription) {
            tempHashMapPrescriptionPages.put(eachPage.getPrescriptionPageId() , eachPage);
        }

        Prescription prescription = new Prescription(mActivePrescriptionIdForNewPrescription,tempHashMapPrescriptionPages,prescriptionName,mCurrentUser.getEmail(),null);


        PrescriptionOperations obj = new PrescriptionOperations(this);
        obj.addNewPrescription(prescription);
    }

    public void updateOldPrescription()
    {
        HashMap<String,PrescriptionPage> tempHashMapPrescriptionPages = new HashMap<>();

        for (PrescriptionPage eachPage: mArrayListAllPagesOfActivePrescription) {
            tempHashMapPrescriptionPages.put(eachPage.getPrescriptionPageId() , eachPage);
        }

        /*for (Map.Entry<String,PrescriptionPage> eachOldPage :mActivePrescription.getPrescriptionPages().entrySet()) {
            if (!tempHashMapPrescriptionPages.containsKey(eachOldPage.getKey())) {
                eachOldPage.getValue().setActive(false);
                tempHashMapPrescriptionPages.put(eachOldPage.getKey(), eachOldPage.getValue());
            }
        }*/


        PrescriptionOperations obj = new PrescriptionOperations(this);
        obj.updateSavedPrescription(mActivePrescription.getPrescriptionId(),tempHashMapPrescriptionPages);
    }

    public void openSaveDataDialog()
    {
        final Dialog getPrescriptionNameDialog = new Dialog(this);
        getPrescriptionNameDialog.setContentView(R.layout.dialog_add_new_item);

        final EditText editTextName = (EditText) getPrescriptionNameDialog.findViewById(R.id.edit_text_name);
        TextView textViewDone = (TextView) getPrescriptionNameDialog.findViewById(R.id.text_view_done);
        TextView textViewDiscard = (TextView) getPrescriptionNameDialog.findViewById(R.id.text_view_discard);
        final TextView textViewErrorMsg = (TextView) getPrescriptionNameDialog.findViewById(R.id.text_view_error_message);

        getPrescriptionNameDialog.show();

        textViewDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextName.getText().toString().trim().length() > 0) {
                    String prescriptionName = editTextName.getText().toString().toUpperCase();
                    getPrescriptionNameDialog.dismiss();
                    addNewPrescription(prescriptionName);
                } else {
                    textViewErrorMsg.setText("Provide Prescription Name");
                }
            }
        });

        textViewDiscard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPrescriptionNameDialog.dismiss();
            }
        });
    }

    public void openActionDataDialog(final Prescription clickedPrescription,final LinearLayout linearLayoutWithNameAndCheckedImage)
    {
        final Dialog showActionOptionsDialog = new Dialog(this);
        showActionOptionsDialog.setContentView(R.layout.dialog_show_action_options);

        LinearLayout linearLayoutActionOptions = (LinearLayout) showActionOptionsDialog.findViewById(R.id.linear_layout_action_options);
        TextView textViewErrorMessage = (TextView) showActionOptionsDialog.findViewById(R.id.text_view_error_message);
        TextView textViewEditPrescription = (TextView) showActionOptionsDialog.findViewById(R.id.text_view_action_option_1);
        TextView textViewSelectPrescription = (TextView) showActionOptionsDialog.findViewById(R.id.text_view_action_option_2);
        TextView textViewDeletePrescription = (TextView) showActionOptionsDialog.findViewById(R.id.text_view_action_option_3);
        TextView textViewSendForEvaluation = (TextView) showActionOptionsDialog.findViewById(R.id.text_view_action_option_4);
        TextView textViewViewPrescription = (TextView) showActionOptionsDialog.findViewById(R.id.text_view_action_option_5);
        TextView textViewDialogTitle= (TextView) showActionOptionsDialog.findViewById(R.id.text_view_dialog_title);

        textViewDialogTitle.setText(Utils.toLowerCaseExceptFirstLetter(clickedPrescription.getPrescriptionName()));
        textViewEditPrescription.setText("Edit this prescription");
        textViewDeletePrescription.setText("Delete this prescription");
        textViewSendForEvaluation.setText("Send for Evaluation");
        textViewViewPrescription.setText("View Prescription");

        if (mActivityVisitPurpose != null && mActivityVisitPurpose.equals(Constants.ACTIVITY_VISIT_PURPOSE_SELECT)) {
            textViewSelectPrescription.setVisibility(View.VISIBLE);
            textViewSelectPrescription.setText("Select this prescription");
        }
        else
            textViewSelectPrescription.setVisibility(View.GONE);

        showActionOptionsDialog.show();

        textViewEditPrescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivePrescription = clickedPrescription;
                setupForPrescriptionUpdateProcess();
                showActionOptionsDialog.dismiss();
                mScrollView.fullScroll(View.FOCUS_UP);
            }
        });

        textViewSelectPrescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for( int i = 0; i < linearLayoutWithNameAndCheckedImage.getChildCount(); i++ ){
                    View view = linearLayoutWithNameAndCheckedImage.getChildAt(i);
                    if (view instanceof ImageView)
                    {
                        ((ImageView)view).setImageResource(R.drawable.ic_checked_24);
                        mArrayListSelectedPrescriptions.add(clickedPrescription);
                        showOrHideSelectedPrescriptions();
                    }
                }
                showActionOptionsDialog.dismiss();
            }
        });

        textViewDeletePrescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mFirebaseAllPrescriptionsRef.child(clickedPrescription.getPrescriptionId()).setValue(null);
                    Toast.makeText(MyPrescriptionsActivity.this,"Prescription deleted successfully. ",Toast.LENGTH_SHORT).show();

                    if (mActivePrescription != null && mActivePrescription.getPrescriptionId().equals(clickedPrescription.getPrescriptionId()))
                    {
                        mTextViewSaveOrUpdatePrescription.setText("Save");
                        mTextViewNewOrUpdatePrescription.setText("New Prescription");
                        mRecyclerViewActivePrescriptionAllPages.setAdapter(null);
                        mArrayListAllPagesOfActivePrescription.clear();
                        mActivePrescription = null;
                    }

                    displaySavedPrescriptions();

                }catch (Exception ex){
                    Toast.makeText(MyPrescriptionsActivity.this,"Unable to delete. Try again",Toast.LENGTH_SHORT).show();
                }
                finally {
                    showActionOptionsDialog.dismiss();
                }
            }
        });

        textViewSendForEvaluation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrescriptionOperations obj = new PrescriptionOperations(MyPrescriptionsActivity.this);
                obj.SendPrescriptionForEvaluation(clickedPrescription.getPrescriptionId());
            }
        });

        textViewViewPrescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentToEvaluatePrescriptionActivity = new Intent(MyPrescriptionsActivity.this, EvaluatePrescriptionActivity.class);
                intentToEvaluatePrescriptionActivity.putExtra("currentPrescription",clickedPrescription);
                startActivity(intentToEvaluatePrescriptionActivity);

                showActionOptionsDialog.dismiss();
            }
        });

    }

    public void setupForPrescriptionUpdateProcess()
    {
        if (mActivePrescription == null)
            return;

        mArrayListAllPagesOfActivePrescription.clear();

        for (Map.Entry<String,PrescriptionPage> eachPage :mActivePrescription.getPrescriptionPages().entrySet()) {
            if (eachPage.getValue().isActive())
              mArrayListAllPagesOfActivePrescription.add(eachPage.getValue());
        }
        mTextViewNewOrUpdatePrescription.setText("Update " + Utils.toLowerCaseExceptFirstLetter(mActivePrescription.getPrescriptionName()));
        mTextViewSaveOrUpdatePrescription.setText("Update");
        displayActivePrescriptionPages();
    }

    public void showOrHideSelectedPrescriptions()
    {
        if (mArrayListSelectedPrescriptions != null && mArrayListSelectedPrescriptions.size() > 0)
        {
            mTextViewSelectedPrescriptions.setVisibility(View.VISIBLE);
            String prescriptionName = "";
            for (Prescription eachPrescription:mArrayListSelectedPrescriptions) {
                prescriptionName = prescriptionName + "   #" +   Utils.toLowerCaseExceptFirstLetter(eachPrescription.getPrescriptionName());
            }
            mTextViewSelectedPrescriptions.setText(prescriptionName);
        }
        else
            mTextViewSelectedPrescriptions.setVisibility(View.GONE);
    }



}
