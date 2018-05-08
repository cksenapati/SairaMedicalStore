package com.example.android.sairamedicalstore.ui.prescription;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.android.sairamedicalstore.R;
import com.example.android.sairamedicalstore.SairaMedicalStoreApplication;
import com.example.android.sairamedicalstore.models.Cart;
import com.example.android.sairamedicalstore.models.Medicine;
import com.example.android.sairamedicalstore.models.Prescription;
import com.example.android.sairamedicalstore.models.PrescriptionPage;
import com.example.android.sairamedicalstore.models.User;
import com.example.android.sairamedicalstore.operations.CartOperations;
import com.example.android.sairamedicalstore.operations.PrescriptionOperations;
import com.example.android.sairamedicalstore.ui.search.SearchActivity;
import com.example.android.sairamedicalstore.utils.Constants;
import com.example.android.sairamedicalstore.utils.Utils;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EvaluatePrescriptionActivity extends AppCompatActivity {

    Firebase mFirebaseCurrentUserCart,mFirebaseAllMedicinesRef;

    private GestureDetector mGestureDetector;
    ImageView mImageViewGoBack,mImageViewPrescriptionPage,mImageViewExpandImage;
    TextView mTextViewActivityTitle,mTextViewPreviousImage,mTextViewNextImage, mTextViewAction,mTextViewMedicineNames;
    FloatingActionButton mFabAddMedicine;
    ProgressBar mProgressBarDataLoading;
    LinearLayout mLinearLayoutPageData;

    Prescription mCurrentPrescription;
    PrescriptionPage mActivePrescriptionPage;
    ArrayList<String> mArrayListPrescriptionPageIds;
    ArrayList<Medicine> mArrayListAllMedicinesInActivePrescription,mArrayListAllMedicinesInActivePage;

    User mCurrentUser;
    Cart mCurrentUserCart;
    Integer totalCountOfPages;

    private static final int RC_MEDICINE_PICKER = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluate_prescription);

        Intent intent = getIntent();
        if (intent != null) {
            mCurrentPrescription = (Prescription) intent.getSerializableExtra("currentPrescription");
        }

        mCurrentUser = ((SairaMedicalStoreApplication) this.getApplication()).getCurrentUser();

        initialization();

        mTextViewActivityTitle.setText(mCurrentPrescription.getPrescriptionName());
        mTextViewPreviousImage.setText("<");
        mTextViewNextImage.setText(">");

        for (Map.Entry eachPage :mCurrentPrescription.getPrescriptionPages().entrySet()) {
            mArrayListPrescriptionPageIds.add(eachPage.getKey().toString());
        }
        totalCountOfPages = mArrayListPrescriptionPageIds.size();


        mActivePrescriptionPage = mCurrentPrescription.getPrescriptionPages().get(mArrayListPrescriptionPageIds.get(0));
        displayDataAboutPrescriptionPage();


        CustomGestureDetector customGestureDetector = new CustomGestureDetector();
        mGestureDetector = new GestureDetector(this, customGestureDetector);
        mImageViewGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mTextViewPreviousImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentPageIndex = mArrayListPrescriptionPageIds.indexOf(mActivePrescriptionPage.getPrescriptionPageId());
                if (currentPageIndex == 0)
                    mActivePrescriptionPage = mCurrentPrescription.getPrescriptionPages().get(mArrayListPrescriptionPageIds.get(totalCountOfPages - 1));
                else
                    mActivePrescriptionPage = mCurrentPrescription.getPrescriptionPages().get(mArrayListPrescriptionPageIds.get(currentPageIndex - 1));

                displayDataAboutPrescriptionPage();

            }
        });

        mTextViewNextImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentPageIndex = mArrayListPrescriptionPageIds.indexOf(mActivePrescriptionPage.getPrescriptionPageId());
                if (currentPageIndex == totalCountOfPages - 1)
                    mActivePrescriptionPage = mCurrentPrescription.getPrescriptionPages().get(mArrayListPrescriptionPageIds.get(0));
                else
                    mActivePrescriptionPage = mCurrentPrescription.getPrescriptionPages().get(mArrayListPrescriptionPageIds.get(currentPageIndex + 1));

                displayDataAboutPrescriptionPage();
            }
        });


        mImageViewExpandImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                if (mActivePrescriptionPage != null && mActivePrescriptionPage.getImageUri() != null)
                {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(mActivePrescriptionPage.getImageUri()), "image/*");
                    startActivity(intent);
                }
                }catch (Exception ex){}
            }
        });

        switch (mCurrentUser.getUserType())
        {
            case Constants.USER_TYPE_END_USER :
                setupPageForEndUser();
                break;

            case Constants.USER_TYPE_OWNER :
                setupPageForOwner();
                break;

            default :
                finish();
        }

    }

    class CustomGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            // Swipe left (next)
            if (e1.getX() > e2.getX()) {
                int currentPageIndex = mArrayListPrescriptionPageIds.indexOf(mActivePrescriptionPage.getPrescriptionPageId());
                if (currentPageIndex == totalCountOfPages - 1)
                    mActivePrescriptionPage = mCurrentPrescription.getPrescriptionPages().get(mArrayListPrescriptionPageIds.get(0));
                else
                    mActivePrescriptionPage = mCurrentPrescription.getPrescriptionPages().get(mArrayListPrescriptionPageIds.get(currentPageIndex + 1));

                displayDataAboutPrescriptionPage();
            }

            // Swipe right (previous)
            if (e1.getX() < e2.getX()) {
                int currentPageIndex = mArrayListPrescriptionPageIds.indexOf(mActivePrescriptionPage.getPrescriptionPageId());
                if (currentPageIndex == 0)
                    mActivePrescriptionPage = mCurrentPrescription.getPrescriptionPages().get(mArrayListPrescriptionPageIds.get(totalCountOfPages - 1));
                else
                    mActivePrescriptionPage = mCurrentPrescription.getPrescriptionPages().get(mArrayListPrescriptionPageIds.get(currentPageIndex - 1));

                displayDataAboutPrescriptionPage();

            }

            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_MEDICINE_PICKER) {
            if (resultCode == RESULT_OK) {
                try {
                    PrescriptionPage tempActivePrescriptionPage = mActivePrescriptionPage;
                    HashMap<String,PrescriptionPage> tempPrescriptionPages = mCurrentPrescription.getPrescriptionPages();

                    tempActivePrescriptionPage.setMedicineIdAndNameInPage((HashMap<String, String>) data.getSerializableExtra("selectedMedicineIdsAndNames"));
                    tempActivePrescriptionPage.setMedicineIdAndItemCountInPage((HashMap<String, Integer>) data.getSerializableExtra("selectedMedicineIdsAndItemCounts"));

                    tempPrescriptionPages.put(tempActivePrescriptionPage.getPrescriptionPageId(),tempActivePrescriptionPage);

                    mCurrentPrescription.setPrescriptionPages(tempPrescriptionPages);

                    mActivePrescriptionPage = tempActivePrescriptionPage;

                    displayDataAboutPrescriptionPage();
                }catch (Exception ex){
                    Toast.makeText(this, "Something went wrong.", Toast.LENGTH_SHORT).show();
                }

            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Something went wrong.", Toast.LENGTH_SHORT).show();
            }
        }

    }


    private void initialization()
    {
        mImageViewGoBack = (ImageView) findViewById(R.id.image_view_go_back);
        mImageViewPrescriptionPage = (ImageView) findViewById(R.id.image_view_prescription_page);
        mImageViewExpandImage = (ImageView) findViewById(R.id.image_view_expand_image);

        mTextViewActivityTitle = (TextView) findViewById(R.id.text_view_activity_title);
        mTextViewPreviousImage = (TextView) findViewById(R.id.text_view_previous_image);
        mTextViewNextImage = (TextView) findViewById(R.id.text_view_next_image);
        mTextViewAction = (TextView) findViewById(R.id.text_view_action);
        mTextViewMedicineNames = (TextView) findViewById(R.id.text_view_medicine_names);

        mFabAddMedicine = (FloatingActionButton) findViewById(R.id.fab_add_medicine_name);

        mProgressBarDataLoading = (ProgressBar) findViewById(R.id.progress_bar_data_loding);

        mLinearLayoutPageData = (LinearLayout) findViewById(R.id.linear_layout_page_data);

        mArrayListPrescriptionPageIds = new ArrayList<>();
        mArrayListAllMedicinesInActivePrescription = new ArrayList<>();
        mArrayListAllMedicinesInActivePage = new ArrayList<>();

    }

    private void displayDataAboutPrescriptionPage()
    {
        //make all views disabled until the image get loaded
        mLinearLayoutPageData.setEnabled(false);

        if (mActivePrescriptionPage.getMedicineIdAndNameInPage() != null &&
                mActivePrescriptionPage.getMedicineIdAndNameInPage().size() > 0)
        {
            mTextViewMedicineNames.setVisibility(View.VISIBLE);
            String medicineNames = "";
            for (Map.Entry eachEntry:mActivePrescriptionPage.getMedicineIdAndNameInPage().entrySet()) {
                medicineNames += ", "+ eachEntry.getValue();
            }

            mTextViewMedicineNames.setText(medicineNames.replaceFirst(",",""));
        }
        else
        {
            mTextViewMedicineNames.setVisibility(View.INVISIBLE);
        }

        Glide.with(mImageViewPrescriptionPage.getContext())
                .load(mActivePrescriptionPage.getImageUri())
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        mProgressBarDataLoading.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        mProgressBarDataLoading.setVisibility(View.GONE);
                        mLinearLayoutPageData.setEnabled(true);
                        return false;
                    }
                })
                .into(mImageViewPrescriptionPage);

    }

    private void setupPageForEndUser()
    {
        mFabAddMedicine.setVisibility(View.GONE);

        if(mActivePrescriptionPage.getMedicineIdAndItemCountInPage() != null &&
                mActivePrescriptionPage.getMedicineIdAndItemCountInPage().size() > 0)
        {
            mTextViewAction.setVisibility(View.VISIBLE);
            mTextViewAction.setText("Add to cart");
        }
        else
            mTextViewAction.setVisibility(View.GONE);


        mFirebaseAllMedicinesRef = new Firebase(Constants.FIREBASE_URL_SAIRA_ALL_MEDICINES);

        mFirebaseCurrentUserCart = new Firebase(Constants.FIREBASE_URL_SAIRA_All_CARTS).child(Utils.encodeEmail(mCurrentUser.getEmail()));
        mFirebaseCurrentUserCart.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                    mCurrentUserCart = dataSnapshot.getValue(Cart.class);
                else
                    mCurrentUserCart = null;
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                finish();
            }
        });

        mTextViewAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CartOperations obj = new CartOperations(EvaluatePrescriptionActivity.this);
                HashMap<String, Integer> currentProductIdAndItemCount;
                HashMap<String,Prescription> tempCartPrescriptions;

                if (mCurrentUserCart == null)
                {
                    currentProductIdAndItemCount = new HashMap<String, Integer>();
                    for (Map.Entry eachPrescriptionPage :mCurrentPrescription.getPrescriptionPages().entrySet()) {
                        for (Map.Entry eachIdAndItemCountPair : ((PrescriptionPage)eachPrescriptionPage.getValue()).getMedicineIdAndItemCountInPage().entrySet()) {
                            currentProductIdAndItemCount.put((String) eachIdAndItemCountPair.getKey(), (Integer) eachIdAndItemCountPair.getValue());
                        }
                    }

                    tempCartPrescriptions = new HashMap<String, Prescription>();
                    tempCartPrescriptions.put(mCurrentPrescription.getPrescriptionName(),mCurrentPrescription);

                    mCurrentUserCart = new Cart(currentProductIdAndItemCount.size(),tempCartPrescriptions,currentProductIdAndItemCount);

                }
                else if (mCurrentUserCart.getProductIdAndItemCount() != null)
                {
                    currentProductIdAndItemCount = mCurrentUserCart.getProductIdAndItemCount();

                    for (Map.Entry eachPrescriptionPage :mCurrentPrescription.getPrescriptionPages().entrySet()) {
                        for (Map.Entry eachIdAndItemCountPair:((PrescriptionPage)eachPrescriptionPage.getValue()).getMedicineIdAndItemCountInPage().entrySet()) {
                            if (!mCurrentUserCart.getProductIdAndItemCount().containsKey(eachIdAndItemCountPair.getKey()))
                                mCurrentUserCart.setNoOfUniqueProductsInCart(mCurrentUserCart.getNoOfUniqueProductsInCart() + 1);
                            currentProductIdAndItemCount.put((String) eachIdAndItemCountPair.getKey(), (Integer) eachIdAndItemCountPair.getValue());
                        }
                    }


                    if (mCurrentUserCart.getCartPrescriptions() != null)
                        tempCartPrescriptions = mCurrentUserCart.getCartPrescriptions();
                    else
                        tempCartPrescriptions = new HashMap<String, Prescription>();

                    tempCartPrescriptions.put(mCurrentPrescription.getPrescriptionName(),mCurrentPrescription);

                    mCurrentUserCart.setCartPrescriptions(tempCartPrescriptions);
                }

                obj.updateCart(mCurrentUserCart);

                Toast.makeText(EvaluatePrescriptionActivity.this,"Successfully added to cart",Toast.LENGTH_SHORT).show();
            }
        });
    }


  /*  public void updateRecyclerView()
    {
        mArrayListAllMedicinesInActivePrescription.clear();
        mArrayListAllMedicinesInActivePage.clear();

        for (Map.Entry eachPage : mCurrentPrescription.getPrescriptionPages().entrySet() ) {
            PrescriptionPage page = (PrescriptionPage) eachPage.getValue();
            for (Map.Entry eachItemAndItemCountPair : page.getMedicineIdAndItemCountInPage().entrySet() ) {
                getMedicineFromId(eachItemAndItemCountPair.getKey().toString());
            }
        }
    }

    public void getMedicineFromId(String medicineId)
    {
        Firebase firebaseAllMedicinesRef = new Firebase(Constants.FIREBASE_URL_SAIRA_ALL_MEDICINES);
        firebaseAllMedicinesRef.child(medicineId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    Medicine medicine = dataSnapshot.getValue(Medicine.class);
                    if (medicine != null)
                    {
                        if (mArrayListAllMedicinesInActivePrescription != null && mArrayListAllMedicinesInActivePrescription.size() == 0)
                          mArrayListAllMedicinesInActivePrescription.add(medicine);
                        else if (mArrayListAllMedicinesInActivePrescription != null && mArrayListAllMedicinesInActivePrescription.size() > 0)
                        {
                            for (Medicine eachMedicineInArrayList:mArrayListAllMedicinesInActivePrescription) {
                                if (eachMedicineInArrayList.getMedicineId().equals(medicine.getMedicineId()))
                                    return;
                            }
                            //To avoid duplicate data in list
                            mArrayListAllMedicinesInActivePrescription.add(medicine);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }*/

    private void setupPageForOwner()
    {
        mTextViewAction.setVisibility(View.VISIBLE);
        mFabAddMedicine.setVisibility(View.VISIBLE);

        mFabAddMedicine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String selectedMedicineIds = "";
                if (mActivePrescriptionPage.getMedicineIdAndNameInPage() != null) {
                    for (Map.Entry eachNameIdPair : mActivePrescriptionPage.getMedicineIdAndItemCountInPage().entrySet()) {
                        selectedMedicineIds = selectedMedicineIds + "," + eachNameIdPair.getKey() + "," + eachNameIdPair.getValue();
                    }
                    selectedMedicineIds = selectedMedicineIds.replaceFirst(",", "");
                }

                Intent searchActivity = new Intent(EvaluatePrescriptionActivity.this, SearchActivity.class);
                searchActivity.putExtra("whatToSearch", Constants.SEARCH_AND_CHOOSE_MEDICINE);
                searchActivity.putExtra("oldSelectedData",selectedMedicineIds);
                startActivityForResult(Intent.createChooser(searchActivity, "Complete action using"), RC_MEDICINE_PICKER);
            }
        });

        mTextViewAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (Map.Entry eachPrescriptionPage :mCurrentPrescription.getPrescriptionPages().entrySet()) {
                    if( ((PrescriptionPage)eachPrescriptionPage.getValue()).getMedicineIdAndNameInPage() == null ||
                            ((PrescriptionPage)eachPrescriptionPage.getValue()).getMedicineIdAndNameInPage().size() < 1 )
                    {
                        Toast.makeText(EvaluatePrescriptionActivity.this,"Some pages are not yet evaluated.",Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                PrescriptionOperations obj = new PrescriptionOperations(EvaluatePrescriptionActivity.this);
                obj.saveEvaluatedPrescription(mCurrentPrescription);
            }
        });

    }

}
