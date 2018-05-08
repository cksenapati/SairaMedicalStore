package com.example.android.sairamedicalstore.ui.search;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.android.sairamedicalstore.R;
import com.example.android.sairamedicalstore.SairaMedicalStoreApplication;
import com.example.android.sairamedicalstore.models.Composition;
import com.example.android.sairamedicalstore.models.DefaultKeyValuePair;
import com.example.android.sairamedicalstore.models.DeliverableAddress;
import com.example.android.sairamedicalstore.models.DisplayCategory;
import com.example.android.sairamedicalstore.models.Faq;
import com.example.android.sairamedicalstore.models.Manufacturer;
import com.example.android.sairamedicalstore.models.Medicine;
import com.example.android.sairamedicalstore.models.Order;
import com.example.android.sairamedicalstore.models.Poster;
import com.example.android.sairamedicalstore.models.Prescription;
import com.example.android.sairamedicalstore.models.Query;
import com.example.android.sairamedicalstore.models.SelectedItem;
import com.example.android.sairamedicalstore.models.User;
import com.example.android.sairamedicalstore.operations.MedicineOperations;
import com.example.android.sairamedicalstore.ui.customerSupport.AddOrUpdateFaqActivity;
import com.example.android.sairamedicalstore.ui.customerSupport.QueryTicketDetailsActivity;
import com.example.android.sairamedicalstore.ui.deliverableAddress.DeliverableAddressActivity;
import com.example.android.sairamedicalstore.ui.medicine.AddOrUpdateMedicineActivity;
import com.example.android.sairamedicalstore.ui.poster.CreateOrUpdatePoster;
import com.example.android.sairamedicalstore.ui.medicine.ProductDetailsActivity;
import com.example.android.sairamedicalstore.ui.order.OrderDetailsActivity;
import com.example.android.sairamedicalstore.ui.prescription.EvaluatePrescriptionActivity;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static android.R.attr.key;


public class SearchActivity extends AppCompatActivity {

    private static final int RC_IMAGE_PICKER =  1;
    String tempMedicineType;

    String  mWhatToSearch,mSpecialSearch;
    ListView mListViewSearchResult,mListViewPinCodeDetailsSearchResult;
    EditText mEditTextSearch;
    Button mButtonDone;
    FloatingActionButton mFabAddItem;
    ImageView mImageViewSort,mImageViewBackArrow,mImageViewDataNotFound;
    TextView mTextViewActivityTitle,mTextViewSelectedItems;

    String errorMessage,searchOrderBy;
    Boolean isSuccess;
    User mCurrentUser;
    int maximumNoOfQueriesToDisplay;
    MedicineOperations operationObject;

    Firebase mFirebaseAllMedicinesRef, mFirebaseAllMedicineManufacturersRef,
            mFirebaseAllMedicineCompositionsRef,mFirebaseAllDisplayCategoriesRef,mFirebaseAllPostersRef, mFirebaseAllOrdersRef,
            mFirebaseAllQueriesRef,mFirebaseAllFaqsRef,mFirebaseAllUsersRef,mFirebaseAllPrescriptionsRef,mFirebaseAllDeliverableAddressesRef,
            mFirebaseAllDefaultMedicinePics;

    private FirebaseStorage mFirebaseStorage;
    private StorageReference mFirebaseTemporaryStorageRef,mFirebaseAllMedicinesStorageRef;

    private SearchedMedicinesAdapter mSearchedMedicinesAdapter;
    private SearchedManufacturersAdapter mSerarchedManufacturerAdapter;
    private SearchedCompositionsAdapter mSearchedCompositionAdapter;
    private SearchedDisplayCategoriesAdapter mSearchedDisplayCategoryAdapter;
    private SearchedPostersAdapter mSearchedPostersAdapter;
    private SearchedOrdersAdapter mSearchedOrdersAdapter;
    private SearchedQueriesAdapter mSearchedQueriesAdapter;
    private SearchedFaqsAdapter mSearchedFaqsAdapter;
    private SearchedUsersAdapter mSearchedUsersAdapter;
    private SelectMedicineAdapter mSelectMedicineAdapter;
    private SearchedDeliverableAddressesAdapter mSearchedDeliverableAddressesAdapter;
    private SearchedPrescriptionForEvaluationAdapter mSearchedPrescriptionForEvaluationAdapter;
    private SearchedDefaultMedicinePicsAdapter mSearchedDefaultMedicinePicsAdapter;

    ArrayList<SelectedItem> mArrayListSelectedItems;
    ArrayList<Query> mArrayListQueriesFromAllUsers;
    ArrayList<String> mArrayListPostOffices;

    LinkedHashMap<String,String> mHashMapSortOptions,mHashMapSortOptions2;
    HashMap<String,String> mHashMapSelectedMedicineIdsAndNames;
    HashMap<String,Integer> mHashMapSelectedMedicineIdsAndItemCounts;

    int globalCount = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        String oldSelectedData ="";
        Intent intent = getIntent();
        if (intent != null) {
            mWhatToSearch = intent.getStringExtra("whatToSearch");
            oldSelectedData = intent.getStringExtra("oldSelectedData");
            mSpecialSearch = intent.getStringExtra("specialSearch");
            searchOrderBy = intent.getStringExtra("searchOrderBy");
        }


        initializeScreen();

        setActivityTitle();


        if (mWhatToSearch.equals(Constants.SEARCH_AND_CHOOSE_MEDICINE))
            getOldSelectedMedicineIds(oldSelectedData);
        else
            getOldSelectedData(oldSelectedData);


        operationObject = new MedicineOperations(mCurrentUser,this);

        mEditTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mEditTextSearch.getText().toString().trim().length() > 0) {
                    String textEntered = mEditTextSearch.getText().toString();
                    displayResultAccordingly(textEntered);
                }
                else
                    displayAllData();
            }
        });

        displayAllData();

        mFabAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAddClick();
            }
        });

        mImageViewBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchActivity.this.finish();
            }
        });

        mImageViewSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortListViewAccordingToSearch();
            }
        });

        if(mSpecialSearch != null)
            mEditTextSearch.setText(mSpecialSearch);


        mListViewSearchResult.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (mListViewSearchResult.getCount() > 0)
                    mImageViewDataNotFound.setVisibility(View.GONE);
                else
                    mImageViewDataNotFound.setVisibility(View.VISIBLE);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_IMAGE_PICKER)
        {
            if(resultCode == RESULT_OK){

                StorageReference selectedPhotoRef = mFirebaseTemporaryStorageRef.child(data.getData().getLastPathSegment());
                UploadTask uploadTask = selectedPhotoRef.putFile(data.getData());
                uploadTask.addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // When the image has successfully uploaded, we get its download URL
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        try {
                            DefaultKeyValuePair tempPair = new DefaultKeyValuePair(tempMedicineType,downloadUrl.toString());
                            openDialogForDefaultMedicinePic(tempPair,data.getData());
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


    private void initializeScreen() {


        mImageViewSort = (ImageView) findViewById(R.id.image_view_filter);
        mTextViewActivityTitle = (TextView) findViewById(R.id.text_view_activity_title);
        mTextViewSelectedItems = (TextView) findViewById(R.id.text_view_selected_items);
        mImageViewBackArrow  = (ImageView) findViewById(R.id.image_view_back_arrow);
        mImageViewDataNotFound = (ImageView) findViewById(R.id.image_view_data_not_found);

        mListViewSearchResult = (ListView) findViewById(R.id.list_view_search_result);
        mEditTextSearch = (EditText) findViewById(R.id.edit_text_search);
        mButtonDone = (Button) findViewById(R.id.button_done);

        mCurrentUser = ((SairaMedicalStoreApplication) this.getApplication()).getCurrentUser();

        mArrayListSelectedItems = new ArrayList<SelectedItem>();
        mArrayListQueriesFromAllUsers = new ArrayList<>();
        mArrayListPostOffices = new ArrayList<>();
        mHashMapSortOptions = new LinkedHashMap<>();
        mHashMapSortOptions2 = new LinkedHashMap<>();
        mHashMapSelectedMedicineIdsAndNames = new HashMap<>();
        mHashMapSelectedMedicineIdsAndItemCounts = new HashMap<>();

        mFabAddItem = (FloatingActionButton) findViewById(R.id.fab_add_item);

        mFirebaseAllMedicinesRef = new Firebase(Constants.FIREBASE_URL_SAIRA_ALL_MEDICINES);
        mFirebaseAllMedicineManufacturersRef = new Firebase(Constants.FIREBASE_URL_SAIRA_All_MANUFACTURERS);
        mFirebaseAllMedicineCompositionsRef = new Firebase(Constants.FIREBASE_URL_SAIRA_All_COMPOSITIONS);
        mFirebaseAllDisplayCategoriesRef = new Firebase(Constants.FIREBASE_URL_SAIRA_All_DISPLAY_CATEGORIES);
        mFirebaseAllPostersRef = new Firebase(Constants.FIREBASE_URL_SAIRA_All_POSTERS);
        mFirebaseAllOrdersRef = new Firebase(Constants.FIREBASE_URL_SAIRA_All_ORDERS);
        mFirebaseAllQueriesRef = new Firebase(Constants.FIREBASE_URL_SAIRA_All_QUERIES);
        mFirebaseAllFaqsRef = new Firebase(Constants.FIREBASE_URL_SAIRA_All_FAQS);
        mFirebaseAllUsersRef = new Firebase(Constants.FIREBASE_URL_SAIRA_ALL_USERS);
        mFirebaseAllPrescriptionsRef = new Firebase(Constants.FIREBASE_URL_SAIRA_All_PRESCRIPTIONS);
        mFirebaseAllDeliverableAddressesRef = new Firebase(Constants.FIREBASE_URL_SAIRA_All_DELIVERABLE_ADDRESS);
        mFirebaseAllDefaultMedicinePics = new Firebase(Constants.FIREBASE_URL_SAIRA_All_DEFAULT_VALUES).child(Constants.FIREBASE_PROPERTY_DEFAULT_MEDICINE_PICS);

        mFirebaseStorage = FirebaseStorage.getInstance();
        mFirebaseTemporaryStorageRef = mFirebaseStorage.getReference().child(Constants.FIREBASE_LOCATION_TEMPORARY_STORAGE);
        mFirebaseAllMedicinesStorageRef = mFirebaseStorage.getReference().child(Constants.FIREBASE_LOCATION_All_MEDICINES);

        setItemVisibilityAccordingToRequest();

        maximumNoOfQueriesToDisplay = 10;

    }

    private void setActivityTitle() {
        switch (mWhatToSearch) {
            case Constants.SEARCH_MEDICINE:
                mTextViewActivityTitle.setText("Search Medicines");
                break;
            case Constants.SEARCH_MEDICINE_MANUFACTURER:
                mTextViewActivityTitle.setText("Search Manufacturer");
                break;
            case Constants.SEARCH_MEDICINE_COMPOSITION:
                mTextViewActivityTitle.setText("Search Compositions");
                break;
            case Constants.SEARCH_DISPLAY_CATEGORY:
                mTextViewActivityTitle.setText("Search Display categories");
                break;
            case Constants.SEARCH_POSTER:
                mTextViewActivityTitle.setText("Search Posters");
                break;
            case Constants.SEARCH_ORDER:
                mTextViewActivityTitle.setText("My Orders");
                break;
            case Constants.SEARCH_ALL_ORDERS:
                mTextViewActivityTitle.setText("Search Orders");
                break;
            case Constants.SEARCH_QUERY:
                mTextViewActivityTitle.setText("Search Queries");
                break;
            case Constants.SEARCH_FAQ:
                mTextViewActivityTitle.setText("FAQs");
                break;
            case Constants.SEARCH_ALL_QUERIES_FROM_ALL_USERS:
                mTextViewActivityTitle.setText("All Queries");
                break;
            case Constants.SEARCH_AND_CHOOSE_ORDER:
                mTextViewActivityTitle.setText("Choose Order");
                break;
            case Constants.SEARCH_AND_CHOOSE_MEDICINE:
                mTextViewActivityTitle.setText("Choose Medicines");
                break;
            case Constants.SEARCH_USERS:
                mTextViewActivityTitle.setText("Search Users");
                break;
            case Constants.SEARCH_PRESCRIPTION_FOR_EVALUATION:
                mTextViewActivityTitle.setText("Evaluate Prescriptions");
                break;
            case Constants.SEARCH_DELIVERABLE_ADDRESS:
                mTextViewActivityTitle.setText("Deliverable Places");
                break;
            case Constants.SEARCH_DEFAULT_MEDICINE_PICS:
                mTextViewActivityTitle.setText("Medicine Pics");
                break;
        }
    }

    private void displayResultAccordingly(String textEntered) {
        switch (mWhatToSearch) {
            case Constants.SEARCH_MEDICINE:
                searchMedicine(textEntered.toUpperCase());
                break;
            case Constants.SEARCH_MEDICINE_MANUFACTURER:
                searchMedicineManufacturer(textEntered.toUpperCase());
                break;
            case Constants.SEARCH_MEDICINE_COMPOSITION:
                searchMedicineComposition(textEntered.toUpperCase());
                break;
            case Constants.SEARCH_DISPLAY_CATEGORY:
                searchDisplayCategories(textEntered.toUpperCase());
                break;
            case Constants.SEARCH_POSTER:
                searchPosters(textEntered.toUpperCase());
                break;
            case Constants.SEARCH_FAQ:
                searchFaqs(textEntered.toUpperCase());
                break;
            case Constants.SEARCH_ALL_ORDERS:
                searchAllOrders(textEntered);
                break;
            case Constants.SEARCH_ALL_QUERIES_FROM_ALL_USERS:
                searchQueriesFromAllUsers(textEntered.toUpperCase());
                break;
            case Constants.SEARCH_USERS:
                searchUsers(textEntered.toUpperCase());
                break;
            case Constants.SEARCH_AND_CHOOSE_MEDICINE:
                searchAndChooseMedicines(textEntered.toUpperCase());
                break;
            case Constants.SEARCH_PRESCRIPTION_FOR_EVALUATION:
                searchPrescriptionForEvaluation(textEntered.toUpperCase());
                break;
            case Constants.SEARCH_DELIVERABLE_ADDRESS:
                searchDeliverablePlaces(textEntered.toUpperCase());
                break;
            case Constants.SEARCH_DEFAULT_MEDICINE_PICS:
                searchDefaultMedicinePics(textEntered.toUpperCase());
                break;
        }

        setSearchEditHint();

    }

    private void displayAllData() {

        switch (mWhatToSearch) {
            case Constants.SEARCH_MEDICINE:
                searchMedicine(null);
                break;
            case Constants.SEARCH_MEDICINE_MANUFACTURER:
                searchMedicineManufacturer(null);
                break;
            case Constants.SEARCH_MEDICINE_COMPOSITION:
                searchMedicineComposition(null);
                break;
            case Constants.SEARCH_DISPLAY_CATEGORY:
                searchDisplayCategories(null);
                break;
            case Constants.SEARCH_POSTER:
                searchPosters(null);
                break;
            case Constants.SEARCH_ORDER:
                searchOrders();
                break;
            case Constants.SEARCH_ALL_ORDERS:
                searchAllOrders(null);
                break;
            case Constants.SEARCH_QUERY:
                searchQueries();
                break;
            case Constants.SEARCH_FAQ:
                searchFaqs(null);
                break;
            case Constants.SEARCH_ALL_QUERIES_FROM_ALL_USERS:
                searchQueriesFromAllUsers(null);
                break;
            case Constants.SEARCH_AND_CHOOSE_ORDER:
                searchAndChooseOrder();
                break;
            case Constants.SEARCH_AND_CHOOSE_MEDICINE:
                searchAndChooseMedicines(null);
                break;
            case Constants.SEARCH_USERS:
                searchUsers(null);
                break;
            case Constants.SEARCH_PRESCRIPTION_FOR_EVALUATION:
                searchPrescriptionForEvaluation(null);
                break;
            case Constants.SEARCH_DELIVERABLE_ADDRESS:
                searchDeliverablePlaces(null);
                break;
            case Constants.SEARCH_DEFAULT_MEDICINE_PICS:
                searchDefaultMedicinePics(null);
                break;
        }

        setSearchEditHint();
    }


//--------------------------------------------------------------------------------------------------
    private void searchMedicine(String textEntered) {
        if (mSearchedMedicinesAdapter != null)
            mSearchedMedicinesAdapter.cleanup();

        if (searchOrderBy == null || searchOrderBy.length() < 1)
            searchOrderBy = Constants.FIREBASE_PROPERTY_MEDICINE_NAME;

        if (textEntered != null && textEntered.trim().length() >1)
            mSearchedMedicinesAdapter = new SearchedMedicinesAdapter(SearchActivity.this, Medicine.class,
                    R.layout.item_single_medicine, mFirebaseAllMedicinesRef.
                    orderByChild(searchOrderBy).startAt(textEntered).endAt(textEntered + "~"));
        else
            mSearchedMedicinesAdapter = new SearchedMedicinesAdapter(SearchActivity.this, Medicine.class,
                    R.layout.item_single_medicine, mFirebaseAllMedicinesRef.
                    orderByChild(searchOrderBy));

        mListViewSearchResult.setAdapter(mSearchedMedicinesAdapter);


        mListViewSearchResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Medicine medicine = mSearchedMedicinesAdapter.getItem(position);
                Intent intentProductDetails = new Intent(SearchActivity.this, ProductDetailsActivity.class);
                intentProductDetails.putExtra("medicineId", medicine.getMedicineId());
                startActivity(intentProductDetails);
            }
        });
    }

    private void searchAndChooseMedicines(String textEntered) {
        if (mSelectMedicineAdapter != null)
            mSelectMedicineAdapter.cleanup();

        if (searchOrderBy == null || searchOrderBy.length() < 1)
            searchOrderBy = Constants.FIREBASE_PROPERTY_MEDICINE_NAME;

        if (textEntered != null && textEntered.trim().length() >1)
            mSelectMedicineAdapter = new SelectMedicineAdapter(SearchActivity.this, Medicine.class,
                    R.layout.item_select_single_medicine, mFirebaseAllMedicinesRef.
                    orderByChild(searchOrderBy).startAt(textEntered).endAt(textEntered + "~"),mHashMapSelectedMedicineIdsAndNames,mHashMapSelectedMedicineIdsAndItemCounts);
        else
            mSelectMedicineAdapter = new SelectMedicineAdapter(SearchActivity.this, Medicine.class,
                    R.layout.item_select_single_medicine, mFirebaseAllMedicinesRef.
                    orderByChild(searchOrderBy),mHashMapSelectedMedicineIdsAndNames,mHashMapSelectedMedicineIdsAndItemCounts);

        mListViewSearchResult.setAdapter(mSelectMedicineAdapter);


        mListViewSearchResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Medicine medicine = mSelectMedicineAdapter.getItem(position);
                onMedicineListViewClicked(medicine,view);
            }
        });
    }


    private void searchMedicineManufacturer(String textEntered) {
        if (mSerarchedManufacturerAdapter != null)
            mSerarchedManufacturerAdapter.cleanup();

        if (searchOrderBy == null || searchOrderBy.length() < 1)
            searchOrderBy = Constants.FIREBASE_PROPERTY_MANUFACTURER_NAME;

        if (textEntered != null && textEntered.trim().length() >1)
            mSerarchedManufacturerAdapter = new SearchedManufacturersAdapter(SearchActivity.this, Manufacturer.class,
                R.layout.item_single_selectable, mFirebaseAllMedicineManufacturersRef
                .orderByChild(searchOrderBy).startAt(textEntered).endAt(textEntered + "~"));
        else
            mSerarchedManufacturerAdapter = new SearchedManufacturersAdapter(SearchActivity.this, Manufacturer.class,
                    R.layout.item_single_selectable, mFirebaseAllMedicineManufacturersRef
                    .orderByChild(searchOrderBy));

        mListViewSearchResult.setAdapter(mSerarchedManufacturerAdapter);


        mListViewSearchResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Manufacturer manufacturer = mSerarchedManufacturerAdapter.getItem(position);
                Intent intentToAddMedicine = new Intent(SearchActivity.this, AddOrUpdateMedicineActivity.class);
                intentToAddMedicine.putExtra("returnText", Utils.toLowerCaseExceptFirstLetter(manufacturer.getManufacturerName()));
                setResult(Activity.RESULT_OK, intentToAddMedicine);
                finish();
            }
        });
    }

    private void searchMedicineComposition(String textEntered) {
        if (mSearchedCompositionAdapter != null)
            mSearchedCompositionAdapter.cleanup();

        if (searchOrderBy == null || searchOrderBy.length() < 1)
            searchOrderBy = Constants.FIREBASE_PROPERTY_COMPOSITION_NAME;

        if (textEntered != null && textEntered.trim().length() >1)
            mSearchedCompositionAdapter = new SearchedCompositionsAdapter(SearchActivity.this, Composition.class,
                    R.layout.item_single_selectable, mFirebaseAllMedicineCompositionsRef.
                    orderByChild(searchOrderBy).startAt(textEntered).endAt(textEntered + "~"), mArrayListSelectedItems);
        else
            mSearchedCompositionAdapter = new SearchedCompositionsAdapter(SearchActivity.this, Composition.class,
                    R.layout.item_single_selectable, mFirebaseAllMedicineCompositionsRef.
                    orderByChild(searchOrderBy), mArrayListSelectedItems);

        mListViewSearchResult.setAdapter(mSearchedCompositionAdapter);


        mListViewSearchResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Composition composition = mSearchedCompositionAdapter.getItem(position);
                onListViewItemClick(composition.getCompositionName(),view);
            }
        });
    }

    private void searchDisplayCategories(String textEntered)
    {
        if (mSearchedDisplayCategoryAdapter != null)
            mSearchedDisplayCategoryAdapter.cleanup();

        if (searchOrderBy == null || searchOrderBy.length() < 1)
            searchOrderBy = Constants.FIREBASE_PROPERTY_DISPLAY_CATEGORY_NAME;

        if (textEntered != null && textEntered.trim().length() >1)
            mSearchedDisplayCategoryAdapter = new SearchedDisplayCategoriesAdapter(SearchActivity.this, DisplayCategory.class,
                    R.layout.item_single_selectable, mFirebaseAllDisplayCategoriesRef.
                    orderByChild(searchOrderBy).startAt(textEntered).endAt(textEntered + "~"), mArrayListSelectedItems);
        else
            mSearchedDisplayCategoryAdapter = new SearchedDisplayCategoriesAdapter(SearchActivity.this, DisplayCategory.class,
                    R.layout.item_single_selectable, mFirebaseAllDisplayCategoriesRef.
                    orderByChild(searchOrderBy), mArrayListSelectedItems);

        mListViewSearchResult.setAdapter(mSearchedDisplayCategoryAdapter);


        mListViewSearchResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DisplayCategory displayCategory = mSearchedDisplayCategoryAdapter.getItem(position);
                onListViewItemClick(displayCategory.getDisplayCategoryName(),view);
            }
        });
    }

    private void searchPosters(String textEntered)
    {
        if (mSearchedPostersAdapter != null)
            mSearchedPostersAdapter.cleanup();

        if (searchOrderBy == null || searchOrderBy.length() < 1)
            searchOrderBy = Constants.FIREBASE_PROPERTY_POSTER_NAME;

        if (textEntered != null && textEntered.trim().length() >1)
            mSearchedPostersAdapter = new SearchedPostersAdapter(SearchActivity.this, Poster.class,
                    R.layout.item_single_poster, mFirebaseAllPostersRef.
                    orderByChild(searchOrderBy).startAt(textEntered).endAt(textEntered + "~"));
        else
            mSearchedPostersAdapter = new SearchedPostersAdapter(SearchActivity.this, Poster.class,
                    R.layout.item_single_poster, mFirebaseAllPostersRef.
                    orderByChild(searchOrderBy));

        mListViewSearchResult.setAdapter(mSearchedPostersAdapter);


        mListViewSearchResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Poster poster = mSearchedPostersAdapter.getItem(position);
                Intent intentCreateOrUpdatePoster = new Intent(SearchActivity.this, CreateOrUpdatePoster.class);
                intentCreateOrUpdatePoster.putExtra("posterId", poster.getPosterId());
                startActivity(intentCreateOrUpdatePoster);
            }
        });
    }

    private void searchOrders() {
        if (mSearchedOrdersAdapter != null)
            mSearchedOrdersAdapter.cleanup();

        searchOrderBy = Constants.FIREBASE_PROPERTY_ORDER_PLACED_BY;

        mSearchedOrdersAdapter = new SearchedOrdersAdapter(SearchActivity.this, Order.class,
                R.layout.item_single_order, mFirebaseAllOrdersRef.
                orderByChild(searchOrderBy).equalTo(mCurrentUser.getEmail()));

        mListViewSearchResult.setAdapter(mSearchedOrdersAdapter);


        mListViewSearchResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Order order = mSearchedOrdersAdapter.getItem(position);
                Intent intentToOrderDetails = new Intent(SearchActivity.this, OrderDetailsActivity.class);
                intentToOrderDetails.putExtra("currentOrder", order);
                startActivity(intentToOrderDetails);
            }
        });
    }

    private void searchAllOrders(String textEntered) {
        if (mSearchedOrdersAdapter != null)
            mSearchedOrdersAdapter.cleanup();

        if (searchOrderBy == null || searchOrderBy.length() < 1)
            searchOrderBy = Constants.FIREBASE_PROPERTY_TIMESTAMP_CREATED + "/" + Constants.FIREBASE_PROPERTY_TIMESTAMP;
        else if (searchOrderBy != Constants.FIREBASE_PROPERTY_ORDER_ID && textEntered != null)
            textEntered = textEntered.toUpperCase();

        if (textEntered != null && textEntered.trim().length() >1)
            mSearchedOrdersAdapter = new SearchedOrdersAdapter(SearchActivity.this, Order.class,
                    R.layout.item_single_order, mFirebaseAllOrdersRef.
                    orderByChild(searchOrderBy).startAt(textEntered).endAt(textEntered + "~"));
        else
            mSearchedOrdersAdapter = new SearchedOrdersAdapter(SearchActivity.this, Order.class,
                    R.layout.item_single_order, mFirebaseAllOrdersRef.
                    orderByChild(searchOrderBy));

        mListViewSearchResult.setAdapter(mSearchedOrdersAdapter);


        mListViewSearchResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }

    private void searchQueries() {
        if (mSearchedQueriesAdapter != null)
            mSearchedQueriesAdapter.cleanup();

        searchOrderBy = "queryPostedBy";

        mSearchedQueriesAdapter = new SearchedQueriesAdapter(SearchActivity.this, Query.class,
                R.layout.item_single_old_query_ticket, mFirebaseAllQueriesRef.
                orderByChild(searchOrderBy).equalTo(mCurrentUser.getEmail()));

        mListViewSearchResult.setAdapter(mSearchedQueriesAdapter);


        mListViewSearchResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Query selectedQuery = mSearchedQueriesAdapter.getItem(position);
                Intent intentToQueryDetails = new Intent(SearchActivity.this, QueryTicketDetailsActivity.class);
                intentToQueryDetails.putExtra("currentQueryId", selectedQuery.getQueryId());
                startActivity(intentToQueryDetails);
            }
        });
    }

    private void searchFaqs(String textEntered) {
        if (mSearchedFaqsAdapter != null)
            mSearchedFaqsAdapter.cleanup();

        if (searchOrderBy == null || searchOrderBy.length() < 1)
            searchOrderBy = Constants.FIREBASE_PROPERTY_FAQ_PRIORITY;

        if (textEntered != null && textEntered.trim().length() >1)
            mSearchedFaqsAdapter = new SearchedFaqsAdapter(SearchActivity.this, Faq.class,
                    R.layout.item_single_faq, mFirebaseAllFaqsRef.orderByChild(searchOrderBy).startAt(textEntered).endAt(textEntered + "~"));
        else
            mSearchedFaqsAdapter = new SearchedFaqsAdapter(SearchActivity.this, Faq.class,
                    R.layout.item_single_faq, mFirebaseAllFaqsRef.orderByChild(searchOrderBy));

        mListViewSearchResult.setAdapter(mSearchedFaqsAdapter);


        mListViewSearchResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Faq selectedFaq = mSearchedFaqsAdapter.getItem(position);
                if (mCurrentUser.getUserType().equals(Constants.USER_TYPE_END_USER))
                    openDialogForFaqAnswer(selectedFaq);
                else
                {
                    Intent intentToAddOrUpdateFaqActivity = new Intent(SearchActivity.this, AddOrUpdateFaqActivity.class);
                    intentToAddOrUpdateFaqActivity.putExtra("FaqId", selectedFaq.getFaqId());
                    startActivity(intentToAddOrUpdateFaqActivity);
                }
            }
        });

    }

    private void searchQueriesFromAllUsers(String textEntered)
    {
        if (mSearchedQueriesAdapter != null)
            mSearchedQueriesAdapter.cleanup();

        if (searchOrderBy == null || searchOrderBy.length() < 1)
            searchOrderBy = Constants.FIREBASE_PROPERTY_TIMESTAMP_CREATED + "/" + Constants.FIREBASE_PROPERTY_TIMESTAMP;

        if (textEntered != null && textEntered.trim().length() >1)
            mSearchedQueriesAdapter = new SearchedQueriesAdapter(SearchActivity.this, Query.class,
                    R.layout.item_single_old_query_ticket, mFirebaseAllQueriesRef.
                    orderByChild(searchOrderBy).startAt(textEntered).endAt(textEntered + "~").limitToFirst(maximumNoOfQueriesToDisplay));
        else
            mSearchedQueriesAdapter = new SearchedQueriesAdapter(SearchActivity.this, Query.class,
                    R.layout.item_single_old_query_ticket, mFirebaseAllQueriesRef.
                    orderByChild(searchOrderBy).limitToFirst(maximumNoOfQueriesToDisplay));

        mListViewSearchResult.setAdapter(mSearchedQueriesAdapter);


        mListViewSearchResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Query selectedQuery = mSearchedQueriesAdapter.getItem(position);
                Intent intentToQueryDetails = new Intent(SearchActivity.this, QueryTicketDetailsActivity.class);
                intentToQueryDetails.putExtra("currentQueryId", selectedQuery.getQueryId());
                startActivity(intentToQueryDetails);
            }
        });
    }

    private void searchAndChooseOrder() {
        if (mSearchedOrdersAdapter != null)
            mSearchedOrdersAdapter.cleanup();

        searchOrderBy = "orderPlacedBy";

        mSearchedOrdersAdapter = new SearchedOrdersAdapter(SearchActivity.this, Order.class,
                R.layout.item_single_order, mFirebaseAllOrdersRef.
                orderByChild(searchOrderBy).equalTo(mCurrentUser.getEmail()));


        mListViewSearchResult.setAdapter(mSearchedOrdersAdapter);


        mListViewSearchResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Order order = mSearchedOrdersAdapter.getItem(position);
                Intent intentToQueryTicketDetails = new Intent(SearchActivity.this, QueryTicketDetailsActivity.class);
                intentToQueryTicketDetails.putExtra("returnText", order.getOrderId());
                setResult(Activity.RESULT_OK, intentToQueryTicketDetails);
                finish();
            }
        });
    }

    private void searchUsers(String textEntered) {
        if (mSearchedUsersAdapter != null)
            mSearchedUsersAdapter.cleanup();

        if (searchOrderBy == null || searchOrderBy.length() < 1)
            searchOrderBy = Constants.FIREBASE_PROPERTY_TIMESTAMP_JOINED + "/" + Constants.FIREBASE_PROPERTY_TIMESTAMP;

        if (textEntered != null && textEntered.trim().length() >1)
            mSearchedUsersAdapter = new SearchedUsersAdapter(SearchActivity.this, User.class,
                    R.layout.item_single_user, mFirebaseAllUsersRef.
                    orderByChild(searchOrderBy).startAt(textEntered).endAt(textEntered + "~"));
        else
            mSearchedUsersAdapter = new SearchedUsersAdapter(SearchActivity.this, User.class,
                    R.layout.item_single_user, mFirebaseAllUsersRef.
                    orderByChild(searchOrderBy));

        mListViewSearchResult.setAdapter(mSearchedUsersAdapter);



        mListViewSearchResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User userToBeViewed = mSearchedUsersAdapter.getItem(position);
                openViewUserDialog(userToBeViewed);
            }
        });
    }

    private void searchPrescriptionForEvaluation(String textEntered) {
        if (mSearchedPrescriptionForEvaluationAdapter != null)
            mSearchedPrescriptionForEvaluationAdapter.cleanup();

        searchOrderBy = Constants.FIREBASE_PROPERTY_IS_PRESCRIPTION_SENT_FOR_EVALUATION;

        mSearchedPrescriptionForEvaluationAdapter = new SearchedPrescriptionForEvaluationAdapter(SearchActivity.this, Prescription.class,
                    R.layout.item_single_prescription_for_evaluation, mFirebaseAllPrescriptionsRef.
                    orderByChild(searchOrderBy).equalTo(true));

        mListViewSearchResult.setAdapter(mSearchedPrescriptionForEvaluationAdapter);


        mListViewSearchResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Prescription prescription = mSearchedPrescriptionForEvaluationAdapter.getItem(position);
                Intent intentToEvaluatePrescriptionActivity = new Intent(SearchActivity.this, EvaluatePrescriptionActivity.class);
                intentToEvaluatePrescriptionActivity.putExtra("currentPrescription",prescription);
                startActivity(intentToEvaluatePrescriptionActivity);
            }
        });
    }

    private void searchDeliverablePlaces(String textEntered)
    {
        if (mSearchedDeliverableAddressesAdapter != null)
            mSearchedDeliverableAddressesAdapter.cleanup();

        if (searchOrderBy == null || searchOrderBy.length() < 1)
            searchOrderBy = Constants.FIREBASE_PROPERTY_DISTRICT;

        if (textEntered != null && textEntered.trim().length() >1)
            mSearchedDeliverableAddressesAdapter = new SearchedDeliverableAddressesAdapter(SearchActivity.this, DeliverableAddress.class,
                    R.layout.item_single_deliverable_address, mFirebaseAllDeliverableAddressesRef.
                    orderByChild(searchOrderBy).startAt(textEntered).endAt(textEntered + "~"));
        else
            mSearchedDeliverableAddressesAdapter = new SearchedDeliverableAddressesAdapter(SearchActivity.this, DeliverableAddress.class,
                    R.layout.item_single_deliverable_address, mFirebaseAllDeliverableAddressesRef.
                    orderByChild(searchOrderBy));

        mListViewSearchResult.setAdapter(mSearchedDeliverableAddressesAdapter);


        mListViewSearchResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DeliverableAddress deliverableAddress = mSearchedDeliverableAddressesAdapter.getItem(position);
                Intent intentToDeliverableAddressActivity = new Intent(SearchActivity.this, DeliverableAddressActivity.class);
                intentToDeliverableAddressActivity.putExtra("activePinCode",deliverableAddress.getPinCode());
                startActivity(intentToDeliverableAddressActivity);
            }
        });
    }

    private void searchDefaultMedicinePics(String textEntered)
    {
        if (mSearchedDefaultMedicinePicsAdapter != null)
            mSearchedDefaultMedicinePicsAdapter.cleanup();

        if (searchOrderBy == null || searchOrderBy.length() < 1)
            searchOrderBy = "key";

        if (textEntered != null && textEntered.trim().length() >1)
            mSearchedDefaultMedicinePicsAdapter = new SearchedDefaultMedicinePicsAdapter(SearchActivity.this, DefaultKeyValuePair.class,
                    R.layout.item_single_poster, mFirebaseAllDefaultMedicinePics.
                    orderByChild(searchOrderBy).startAt(textEntered).endAt(textEntered + "~"));
        else
            mSearchedDefaultMedicinePicsAdapter = new SearchedDefaultMedicinePicsAdapter(SearchActivity.this, DefaultKeyValuePair.class,
                    R.layout.item_single_poster, mFirebaseAllDefaultMedicinePics.
                    orderByChild(searchOrderBy));

        mListViewSearchResult.setAdapter(mSearchedDefaultMedicinePicsAdapter);


        mListViewSearchResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DefaultKeyValuePair keyValuePair = mSearchedDefaultMedicinePicsAdapter.getItem(position);
                openDialogForDefaultMedicinePic(keyValuePair,null);
            }
        });
    }

//--------------------------------------------------------------------------------------------------


    private void setItemVisibilityAccordingToRequest() {
        switch (mWhatToSearch) {
            case Constants.SEARCH_MEDICINE:

                if (mCurrentUser.getUserType().equals(Constants.USER_TYPE_END_USER))
                    mFabAddItem.setVisibility(View.GONE);
                else
                    mFabAddItem.setVisibility(View.VISIBLE);

                mButtonDone.setVisibility(View.GONE);
                break;
            case Constants.SEARCH_AND_CHOOSE_MEDICINE:

                if (mCurrentUser.getUserType().equals(Constants.USER_TYPE_END_USER))
                    this.finish();

                mFabAddItem.setVisibility(View.VISIBLE);
                mButtonDone.setVisibility(View.VISIBLE);
                mEditTextSearch.setVisibility(View.VISIBLE);
                mImageViewSort.setVisibility(View.VISIBLE);
                break;
            case Constants.SEARCH_MEDICINE_MANUFACTURER:
                mFabAddItem.setVisibility(View.VISIBLE);
                mButtonDone.setVisibility(View.VISIBLE);
                break;
            case Constants.SEARCH_MEDICINE_COMPOSITION:
                mFabAddItem.setVisibility(View.VISIBLE);
                mButtonDone.setVisibility(View.VISIBLE);
                break;
            case Constants.SEARCH_POSTER:
                mFabAddItem.setVisibility(View.VISIBLE);
                mButtonDone.setVisibility(View.GONE);
                break;
            case Constants.SEARCH_ORDER:
                mFabAddItem.setVisibility(View.GONE);
                mButtonDone.setVisibility(View.GONE);
                mEditTextSearch.setVisibility(View.GONE);
                mImageViewSort.setVisibility(View.GONE);
                break;
            case Constants.SEARCH_ALL_ORDERS:
                mFabAddItem.setVisibility(View.GONE);
                mButtonDone.setVisibility(View.GONE);
                mEditTextSearch.setVisibility(View.VISIBLE);
                break;
            case Constants.SEARCH_QUERY:
                mFabAddItem.setVisibility(View.VISIBLE);
                mButtonDone.setVisibility(View.GONE);
                mImageViewSort.setVisibility(View.GONE);
                mEditTextSearch.setVisibility(View.GONE);
                break;
            case Constants.SEARCH_FAQ:

                if (mCurrentUser.getUserType().equals(Constants.USER_TYPE_END_USER))
                    mFabAddItem.setVisibility(View.GONE);
                else
                    mFabAddItem.setVisibility(View.VISIBLE);

                mButtonDone.setVisibility(View.GONE);
                break;
            case Constants.SEARCH_ALL_QUERIES_FROM_ALL_USERS:
                mFabAddItem.setVisibility(View.GONE);
                mButtonDone.setVisibility(View.GONE);
                break;
            case Constants.SEARCH_AND_CHOOSE_ORDER:
                mFabAddItem.setVisibility(View.GONE);
                mButtonDone.setVisibility(View.GONE);
                mEditTextSearch.setVisibility(View.GONE);
                mImageViewSort.setVisibility(View.GONE);
                break;
            case Constants.SEARCH_USERS:
                mFabAddItem.setVisibility(View.GONE);
                mButtonDone.setVisibility(View.GONE);
                break;
            case Constants.SEARCH_PRESCRIPTION_FOR_EVALUATION:
                mFabAddItem.setVisibility(View.GONE);
                mButtonDone.setVisibility(View.GONE);
                mEditTextSearch.setVisibility(View.GONE);
                mImageViewSort.setVisibility(View.GONE);
                break;
            case Constants.SEARCH_DELIVERABLE_ADDRESS:
                mFabAddItem.setVisibility(View.VISIBLE);
                mButtonDone.setVisibility(View.GONE);
                mEditTextSearch.setVisibility(View.VISIBLE);
                mImageViewSort.setVisibility(View.VISIBLE);
                break;
            case Constants.SEARCH_DEFAULT_MEDICINE_PICS:
                mFabAddItem.setVisibility(View.VISIBLE);
                mButtonDone.setVisibility(View.GONE);
                mEditTextSearch.setVisibility(View.VISIBLE);
                mImageViewSort.setVisibility(View.GONE);
                break;
        }
    }

    public void openDialog(String dialogTitle) {

        final Dialog addNewItemDialog = new Dialog(this);
        addNewItemDialog.setTitle(dialogTitle);
        addNewItemDialog.setContentView(R.layout.dialog_add_new_item);

        final EditText editTextName = (EditText) addNewItemDialog.findViewById(R.id.edit_text_name);
        TextView textViewDone = (TextView) addNewItemDialog.findViewById(R.id.text_view_done);
        TextView textViewDiscard = (TextView) addNewItemDialog.findViewById(R.id.text_view_discard);
        final TextView textViewErrorMsg = (TextView) addNewItemDialog.findViewById(R.id.text_view_error_message);

        addNewItemDialog.show();

        textViewDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextName.getText().toString().trim().length() > 0) {
                    String enteredName = editTextName.getText().toString().toUpperCase();
                    addNewItem(enteredName, addNewItemDialog, textViewErrorMsg);
                } else {
                    isSuccess = false;
                    errorMessage = "Give some values.";
                    textViewErrorMsg.setText(errorMessage);
                }
            }
        });

        textViewDiscard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewItemDialog.dismiss();
            }
        });


    }

    public void openDialogForFaqAnswer(Faq currentFaq)
    {
        final Dialog showFaqAnswerDialog = new Dialog(this,android.R.style.Theme_Light_NoTitleBar_Fullscreen);
        showFaqAnswerDialog.setContentView(R.layout.dialog_faq_answer);

        TextView textViewFaqQuestion = (TextView) showFaqAnswerDialog.findViewById(R.id.text_view_faq_question);
        TextView textViewExit = (TextView) showFaqAnswerDialog.findViewById(R.id.text_view_exit);
        LinearLayout mLinearLayoutFaqAnswers = (LinearLayout)  showFaqAnswerDialog.findViewById(R.id.linear_layout_faq_answers);

        textViewFaqQuestion.setText(currentFaq.getFaqQuestion());

        ArrayList<String> sortedKeys =
                new ArrayList<String>(currentFaq.getFaqAnswers().keySet());
        Collections.sort(sortedKeys);

        for (String x : sortedKeys)
        {
            TextView textView = new TextView(this);
            textView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT)); // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.

            int padding = getResources().getDimensionPixelOffset(R.dimen.extra_small_margin);
            float textSize = getResources().getDimension(R.dimen.triple_extra_small_text_size);

            textView.setPadding(padding,padding,padding,padding);

            textView.setText("√ " + currentFaq.getFaqAnswers().get(x).toString());
            textView.setTextSize(textSize);

            mLinearLayoutFaqAnswers.addView(textView);
        }

        textViewExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFaqAnswerDialog.dismiss();
            }
        });

        showFaqAnswerDialog.show();
    }

    public void openViewUserDialog(final User userToBeViewed) {

        final Dialog viewUserDialog = new Dialog(this,android.R.style.Theme_Light_NoTitleBar_Fullscreen);
        viewUserDialog.setContentView(R.layout.dialog_view_user);

        ImageView imageViewUserProfilePic = (ImageView) viewUserDialog.findViewById(R.id.image_view_user_profile_pic);
        ImageView imageViewCloseDialog = (ImageView) viewUserDialog.findViewById(R.id.image_view_close_dialog);
        TextView textViewUserName = (TextView) viewUserDialog.findViewById(R.id.text_view_user_name);
        TextView textViewUserEmail = (TextView) viewUserDialog.findViewById(R.id.text_view_user_email);
        TextView textViewUserPhoneNo = (TextView) viewUserDialog.findViewById(R.id.text_view_user_phone_no);
        TextView textViewUpdate = (TextView) viewUserDialog.findViewById(R.id.text_view_update);
        final Spinner spinnerUserType = (Spinner) viewUserDialog.findViewById(R.id.spinner_user_type);
        final Spinner spinnerUserActive = (Spinner) viewUserDialog.findViewById(R.id.spinner_user_active);


        textViewUserName.setText(userToBeViewed.getName());
        textViewUserEmail.setText(userToBeViewed.getEmail());

        if (userToBeViewed.getPhoneNo() != null){
            textViewUserPhoneNo.setText(userToBeViewed.getPhoneNo());
            textViewUserPhoneNo.setVisibility(View.VISIBLE);
        }
        else
            textViewUserPhoneNo.setVisibility(View.GONE);

        ArrayAdapter<CharSequence> userTypesAdapter;
        userTypesAdapter = ArrayAdapter.createFromResource(this,
                R.array.user_type_options, android.R.layout.simple_spinner_item);
        userTypesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUserType.setAdapter(userTypesAdapter);

        ArrayAdapter<CharSequence> ActiveOptionsAdapter;
        ActiveOptionsAdapter = ArrayAdapter.createFromResource(this,
                R.array.active_options, android.R.layout.simple_spinner_item);
        ActiveOptionsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUserActive.setAdapter(ActiveOptionsAdapter);

        try {
            int defaultPositionForUserType = userTypesAdapter.getPosition(userToBeViewed.getUserType());
            spinnerUserType.setSelection(defaultPositionForUserType);

            int defaultPositionForUserAccountActive = 1;
            if (userToBeViewed.getAccountStatus() != null)
                 defaultPositionForUserAccountActive = ActiveOptionsAdapter.getPosition(userToBeViewed.getAccountStatus());

            spinnerUserActive.setSelection(defaultPositionForUserAccountActive);

        }catch (Exception Ex)
        {
            spinnerUserType.setVisibility(View.GONE);
            spinnerUserActive.setVisibility(View.GONE);

        }

        Glide.with(imageViewUserProfilePic.getContext())
                .load(userToBeViewed.getPhotoURL())
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        viewUserDialog.show();
                        return false;
                    }
                })
                .into(imageViewUserProfilePic);


        textViewUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (spinnerUserType.getSelectedItem().toString().equals(userToBeViewed.getUserType()) &&
                        spinnerUserActive.getSelectedItem().toString().equals(userToBeViewed.getAccountStatus())) {
                    viewUserDialog.dismiss();
                    return;
                }
                else {
                    userToBeViewed.setUserType(spinnerUserType.getSelectedItem().toString());
                    userToBeViewed.setAccountStatus(spinnerUserActive.getSelectedItem().toString());

                    mFirebaseAllUsersRef.child(Utils.encodeEmail(userToBeViewed.getEmail())).
                            setValue(userToBeViewed);
                    viewUserDialog.dismiss();
                    Toast.makeText(SearchActivity.this,Constants.UPDATE_SUCCESSFUL,Toast.LENGTH_SHORT).show();
                }
            }
        });

        imageViewCloseDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewUserDialog.dismiss();
            }
        });

    }

    public void sortListViewAccordingToSearch()
    {
        mHashMapSortOptions.clear();

        switch (mWhatToSearch) {
            case Constants.SEARCH_MEDICINE:
                mHashMapSortOptions.put("Medicine Name",Constants.FIREBASE_PROPERTY_MEDICINE_NAME);
                mHashMapSortOptions.put("Manufacturer Name",Constants.FIREBASE_PROPERTY_MEDICINE_MANUFACTURER_NAME);
                mHashMapSortOptions.put("Medicine Type",Constants.FIREBASE_PROPERTY_MEDICINE_TYPE);
                mHashMapSortOptions.put("Medicine Category",Constants.FIREBASE_PROPERTY_MEDICINE_CATEGORY);
                openDialogForSort();
                break;
            case Constants.SEARCH_MEDICINE_MANUFACTURER:
                mHashMapSortOptions.put("Name",Constants.FIREBASE_PROPERTY_MANUFACTURER_NAME);
                mHashMapSortOptions.put("Most Recent",Constants.FIREBASE_PROPERTY_TIMESTAMP_CREATED + "/" + Constants.FIREBASE_PROPERTY_TIMESTAMP);
                openDialogForSort();
                break;
            case Constants.SEARCH_MEDICINE_COMPOSITION:
                mHashMapSortOptions.put("Name",Constants.FIREBASE_PROPERTY_COMPOSITION_NAME);
                mHashMapSortOptions.put("Most Recent",Constants.FIREBASE_PROPERTY_TIMESTAMP_CREATED + "/" + Constants.FIREBASE_PROPERTY_TIMESTAMP);
                openDialogForSort();
                break;
            case Constants.SEARCH_DISPLAY_CATEGORY:
                mHashMapSortOptions.put("Name",Constants.FIREBASE_PROPERTY_CATEGORY_NAME);
                mHashMapSortOptions.put("Most Recent",Constants.FIREBASE_PROPERTY_TIMESTAMP_CREATED + "/" + Constants.FIREBASE_PROPERTY_TIMESTAMP);
                openDialogForSort();
                break;
            case Constants.SEARCH_POSTER:
                mHashMapSortOptions.put("Name",Constants.FIREBASE_PROPERTY_POSTER_NAME);
                mHashMapSortOptions.put("Most Recent",Constants.FIREBASE_PROPERTY_TIMESTAMP_CREATED + "/" + Constants.FIREBASE_PROPERTY_TIMESTAMP);
                openDialogForSort();
                break;
            case Constants.SEARCH_ALL_ORDERS:
                mHashMapSortOptions.put("Order Status",Constants.FIREBASE_PROPERTY_ORDER_STATUS);
                mHashMapSortOptions.put("Order ID",Constants.FIREBASE_PROPERTY_ORDER_ID);
                mHashMapSortOptions.put("Payment Method",Constants.FIREBASE_PROPERTY_ORDER_PAYMENT_METHOD);
                mHashMapSortOptions.put("Order Placed By",Constants.FIREBASE_PROPERTY_ORDER_PLACED_BY);
                mHashMapSortOptions.put("Most Recent",Constants.FIREBASE_PROPERTY_TIMESTAMP_CREATED + "/" + Constants.FIREBASE_PROPERTY_TIMESTAMP);
                openDialogForSort();
                break;
            case Constants.SEARCH_ALL_QUERIES_FROM_ALL_USERS:
                mHashMapSortOptions.put("Query Status",Constants.FIREBASE_PROPERTY_QUERY_STATUS);
                mHashMapSortOptions.put("Query Title",Constants.FIREBASE_PROPERTY_QUERY_TITLE);
                mHashMapSortOptions.put("Most Recent",Constants.FIREBASE_PROPERTY_TIMESTAMP_CREATED + "/" + Constants.FIREBASE_PROPERTY_TIMESTAMP);
                mHashMapSortOptions.put("Query Posted By",Constants.FIREBASE_PROPERTY_QUERY_POSTED_BY);
                openDialogForSort();
                break;
            case Constants.SEARCH_FAQ:
                mHashMapSortOptions.put("Most Searched",Constants.FIREBASE_PROPERTY_FAQ_PRIORITY);
                mHashMapSortOptions.put("Question",Constants.FIREBASE_PROPERTY_FAQ_QUESTION);
                openDialogForSort();
                break;
            case Constants.SEARCH_USERS:
                mHashMapSortOptions.put("User Name",Constants.FIREBASE_PROPERTY_USER_NAME);
                mHashMapSortOptions.put("User Email",Constants.FIREBASE_PROPERTY_USER_EMAIL);
                mHashMapSortOptions.put("User Phone No",Constants.FIREBASE_PROPERTY_USER_PHONE_NO);
                mHashMapSortOptions.put("User Type",Constants.FIREBASE_PROPERTY_USER_TYPE);
                openDialogForSort();
                break;
            case Constants.SEARCH_DELIVERABLE_ADDRESS:
                mHashMapSortOptions.put("Pin Code",Constants.FIREBASE_PROPERTY_PIN_CODE);
                mHashMapSortOptions.put("District",Constants.FIREBASE_PROPERTY_DISTRICT);
                mHashMapSortOptions.put("State",Constants.FIREBASE_PROPERTY_STATE);
                openDialogForSort();
                break;

        }
    }

    public void sortListViewAccordingToSearch2(String selectedSortOption1)
    {
        mHashMapSortOptions2.clear();

        switch (mWhatToSearch) {
            case Constants.SEARCH_MEDICINE:
                if (selectedSortOption1.equals(Constants.FIREBASE_PROPERTY_MEDICINE_TYPE))
                {
                    ArrayList<DefaultKeyValuePair> arrayListDefaultMedicinePics = ((SairaMedicalStoreApplication) this.getApplication()).getArrayListDefaultMedicinePics();

                    for (DefaultKeyValuePair keyValuePair :arrayListDefaultMedicinePics ) {
                        String medicineType = Utils.toLowerCaseExceptFirstLetter(keyValuePair.getKey());
                        mHashMapSortOptions2.put(medicineType,medicineType);
                    }
                    /*mHashMapSortOptions2.put("Capsule",Constants.MEDICINE_TYPE_CAPSULE);
                    mHashMapSortOptions2.put("Tablet",Constants.MEDICINE_TYPE_TABLET );
                    mHashMapSortOptions2.put("Syrup",Constants.MEDICINE_TYPE_SYRUP);
                    mHashMapSortOptions2.put("Injection",Constants.MEDICINE_TYPE_INJECTION);*/
                    mHashMapSortOptions2.put("Other","Other");
                }
                else if (selectedSortOption1.equals(Constants.FIREBASE_PROPERTY_MEDICINE_CATEGORY))
                {
                    mHashMapSortOptions2.put("Prescription",Constants.MEDICINE_CATEGORY_PRESCRIPTION);
                    mHashMapSortOptions2.put("Otc)",Constants.MEDICINE_CATEGORY_OTC);
                }
                break;

            case Constants.SEARCH_ALL_ORDERS:
                if (selectedSortOption1.equals(Constants.FIREBASE_PROPERTY_ORDER_STATUS))
                {
                    mHashMapSortOptions2.put("Placed",Constants.ORDER_STATUS_PLACED);
                    mHashMapSortOptions2.put("Under Process",Constants.ORDER_STATUS_UNDER_PROCESS );
                    mHashMapSortOptions2.put("Dispatched",Constants.ORDER_STATUS_DISPATCHED);
                    mHashMapSortOptions2.put("Delivered",Constants.ORDER_STATUS_DELIVERED);
                    mHashMapSortOptions2.put("Request for return",Constants.ORDER_STATUS_REQUESTED_FOR_RETURN );
                    mHashMapSortOptions2.put("Returned",Constants.ORDER_STATUS_RETURNED);
                    mHashMapSortOptions2.put("Canceled",Constants.ORDER_STATUS_CANCELED);
                    mHashMapSortOptions2.put("Other","Other");
                }
                else if (selectedSortOption1.equals(Constants.FIREBASE_PROPERTY_ORDER_PAYMENT_METHOD))
                {
                    mHashMapSortOptions2.put("Prepaid",Constants.PAYMENT_METHOD_PREPAID);
                    mHashMapSortOptions2.put("Cod",Constants.PAYMENT_METHOD_COD);
                    mHashMapSortOptions2.put("Other","Other");
                }
                break;

            case Constants.SEARCH_ALL_QUERIES_FROM_ALL_USERS:
                if (selectedSortOption1.equals(Constants.FIREBASE_PROPERTY_QUERY_STATUS))
                {
                    mHashMapSortOptions2.put("Posted",Constants.QUERY_STATUS_POSTED);
                    mHashMapSortOptions2.put("Under Process",Constants.QUERY_STATUS_RUNNING );
                    mHashMapSortOptions2.put("Solved",Constants.QUERY_STATUS_SOLVED);
                    mHashMapSortOptions2.put("Re-opened",Constants.QUERY_STATUS_REOPENED);
                    mHashMapSortOptions2.put("Other","Other");
                }
                break;

            case Constants.SEARCH_USERS:
                if (selectedSortOption1.equals(Constants.FIREBASE_PROPERTY_USER_TYPE))
                {
                    mHashMapSortOptions2.put("End user",Constants.USER_TYPE_END_USER);
                    mHashMapSortOptions2.put("Employee",Constants.USER_TYPE_EMPLOYEE);
                    mHashMapSortOptions2.put("Developer",Constants.USER_TYPE_DEVELOPER);
                    mHashMapSortOptions2.put("Owner",Constants.USER_TYPE_OWNER);
                    mHashMapSortOptions2.put("Other","Other");
                }
                break;
        }
    }

    public void openDialogForSort()
    {
        final Dialog filterDialog = new Dialog(this,android.R.style.Theme_Light_NoTitleBar_Fullscreen);
        filterDialog.setContentView(R.layout.dialog_sort_search);

        TextView textViewFilter = (TextView) filterDialog.findViewById(R.id.text_view_sort);
        TextView textViewExit = (TextView) filterDialog.findViewById(R.id.text_view_exit);
        final RadioGroup radioGroupFilterOptions = (RadioGroup) filterDialog.findViewById(R.id.radio_group_sort_by_options);
        final RadioGroup radioGroupFilterOptions2 = (RadioGroup) filterDialog.findViewById(R.id.radio_group_sort_by_options2);

        if (mHashMapSortOptions.size() < 1)
            return;

        int count = 0;
        for (Map.Entry<String, String> eachOption : mHashMapSortOptions.entrySet()) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setId(count);
            radioButton.setText(eachOption.getKey());
            if (searchOrderBy.equals(eachOption.getValue()))
                radioButton.setChecked(true);

            radioGroupFilterOptions.addView(radioButton);

            count++;
        }

        globalCount = 100;
        sortListViewAccordingToSearch2(searchOrderBy);
        if (mHashMapSortOptions2.size() > 0) {
            radioGroupFilterOptions2.setVisibility(View.VISIBLE);
            for (Map.Entry<String, String> eachOption : mHashMapSortOptions2.entrySet()) {
                RadioButton radioButton = new RadioButton(SearchActivity.this);
                radioButton.setId(globalCount);
                radioButton.setText(eachOption.getKey());
                if (mSpecialSearch != null && mSpecialSearch.equals(eachOption.getValue()))
                    radioButton.setChecked(true);

                radioGroupFilterOptions2.addView(radioButton);

                globalCount++;
            }
        }
        globalCount = 0;

        filterDialog.show();

        radioGroupFilterOptions.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                RadioButton checkedRadioButton = (RadioButton)group.findViewById(checkedId);
                boolean isChecked = checkedRadioButton.isChecked();
                radioGroupFilterOptions2.removeAllViews();
                radioGroupFilterOptions2.setVisibility(View.GONE);
                mHashMapSortOptions2.clear();
                mSpecialSearch = null;
                globalCount = 100;
                if (isChecked)
                {
                    sortListViewAccordingToSearch2(mHashMapSortOptions.get(checkedRadioButton.getText().toString()));
                    if (mHashMapSortOptions.size() > 0)
                    {
                        for (Map.Entry<String, String> eachOption : mHashMapSortOptions2.entrySet()) {
                            RadioButton radioButton = new RadioButton(SearchActivity.this);
                            radioButton.setId(globalCount);
                            radioButton.setText(eachOption.getKey());

                            radioGroupFilterOptions2.addView(radioButton);

                            globalCount++;
                        }
                        radioGroupFilterOptions2.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        globalCount = 0;

        textViewFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (radioGroupFilterOptions.getCheckedRadioButtonId() != -1) {
                    String selectedKey = ((RadioButton) filterDialog.findViewById(radioGroupFilterOptions.getCheckedRadioButtonId())).
                            getText().toString();
                    searchOrderBy = mHashMapSortOptions.get(selectedKey);

                    if (mHashMapSortOptions2.size() > 0 && radioGroupFilterOptions2.getCheckedRadioButtonId() != -1) {
                        String selectedKey2 = ((RadioButton) filterDialog.findViewById(radioGroupFilterOptions2.getCheckedRadioButtonId())).
                                getText().toString();
                        mSpecialSearch = mHashMapSortOptions2.get(selectedKey2);
                    }

                    if (mSpecialSearch != null && mSpecialSearch.trim().length() > 0 && !mSpecialSearch.equals("Other")) {
                        mEditTextSearch.setText(mSpecialSearch);
                        displayResultAccordingly(mSpecialSearch);

                    }
                    else {
                        mEditTextSearch.setText("");
                        displayAllData();
                    }

                }
                filterDialog.dismiss();
            }
        });

        textViewExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterDialog.dismiss();
            }
        });
    }

    private void addNewItem(final String itemName, final Dialog addNewItemDialog, final TextView textViewErrorMsg) {

        if (mWhatToSearch.equals(Constants.SEARCH_MEDICINE_MANUFACTURER))
            operationObject.addNewManufacturer(itemName,addNewItemDialog,textViewErrorMsg);
        else if (mWhatToSearch.equals(Constants.SEARCH_MEDICINE_COMPOSITION))
            operationObject.addNewComposition(itemName,addNewItemDialog,textViewErrorMsg);
        else if (mWhatToSearch.equals(Constants.SEARCH_DISPLAY_CATEGORY))
            operationObject.addNewDisplayCategory(itemName,addNewItemDialog,textViewErrorMsg);
    }



    public void onAddClick() {
        switch (mWhatToSearch) {
            case Constants.SEARCH_MEDICINE_MANUFACTURER:
                openDialog("Add New Manufacturer");
                break;
            case Constants.SEARCH_MEDICINE_COMPOSITION:
                openDialog("Add New Composition");
                break;
            case Constants.SEARCH_DISPLAY_CATEGORY:
                openDialog("Add New Category");
                break;
            case Constants.SEARCH_QUERY:
                Intent intentToQueryTicketDetailsActivity = new Intent(SearchActivity.this, QueryTicketDetailsActivity.class);
                startActivity(intentToQueryTicketDetailsActivity);
                break;
            case Constants.SEARCH_FAQ:
                Intent intentToAddOrUpdateFaqActivity = new Intent(SearchActivity.this, AddOrUpdateFaqActivity.class);
                startActivity(intentToAddOrUpdateFaqActivity);
                break;
            case Constants.SEARCH_MEDICINE:
            case Constants.SEARCH_AND_CHOOSE_MEDICINE:
                Intent intentToAddNewMedicineActivity = new Intent(SearchActivity.this, AddOrUpdateMedicineActivity.class);
                startActivity(intentToAddNewMedicineActivity);
                break;
            case Constants.SEARCH_POSTER:
                Intent intentToCreateNewPoster = new Intent(SearchActivity.this, CreateOrUpdatePoster.class);
                startActivity(intentToCreateNewPoster);
                break;
            case Constants.SEARCH_DELIVERABLE_ADDRESS:
                Intent intentToDeliverableAddressActivity = new Intent(SearchActivity.this, DeliverableAddressActivity.class);
                startActivity(intentToDeliverableAddressActivity);
                break;
            case Constants.SEARCH_DEFAULT_MEDICINE_PICS:
                openDialogForDefaultMedicinePic(null,null);
                break;
        }

    }

    public void onDoneClick(View view) {
        String returnText = "";
        if(mArrayListSelectedItems.size() > 0)
        {
            returnText = mArrayListSelectedItems.get(0).getItemName();
            for(int i=1;i<mArrayListSelectedItems.size();i++)
            {
                returnText = returnText +","+ mArrayListSelectedItems.get(i).getItemName();
            }
        }

        Intent intentToReturnToCallerActivity;
        switch (mWhatToSearch)
        {
            case Constants.SEARCH_AND_CHOOSE_MEDICINE:
                intentToReturnToCallerActivity = new Intent(SearchActivity.this, EvaluatePrescriptionActivity.class);
                intentToReturnToCallerActivity.putExtra("selectedMedicineIdsAndNames", mHashMapSelectedMedicineIdsAndNames);
                intentToReturnToCallerActivity.putExtra("selectedMedicineIdsAndItemCounts",mHashMapSelectedMedicineIdsAndItemCounts);
                break;
            default: intentToReturnToCallerActivity = new Intent(SearchActivity.this, AddOrUpdateMedicineActivity.class);
                intentToReturnToCallerActivity.putExtra("returnText", returnText);


        }
        setResult(Activity.RESULT_OK, intentToReturnToCallerActivity);
        finish();
    }

    private void onListViewItemClick(String selectedItemName,View view)
    {
        boolean isRequestToAdd = true;
        ImageView imageView = (ImageView) view.findViewById(R.id.image_view_add);

        for(int i = 0 ; i<mArrayListSelectedItems.size() ; i++)
        {
            if(mArrayListSelectedItems.get(i).getItemName().toUpperCase().equals(selectedItemName)) {
                mArrayListSelectedItems.remove(i);
                isRequestToAdd = false;
                break;
            }
        }

        if(isRequestToAdd)
        {
            SelectedItem item = new SelectedItem(Utils.toLowerCaseExceptFirstLetter(selectedItemName));
            mArrayListSelectedItems.add(item);
            imageView.setVisibility(View.VISIBLE);
        }
        else
            imageView.setVisibility(View.GONE);

        //setRecyclerView();
        setSelectedItemTextView();
    }

    private void getOldSelectedData(String oldSelectedData)
    {
        if(oldSelectedData != null && oldSelectedData.trim().length() > 0)
        {
            String[] stringOfItems = oldSelectedData.split(",");
            for(int i =0 ; i<stringOfItems.length;i++)
            {
                SelectedItem item = new SelectedItem(stringOfItems[i]);
                mArrayListSelectedItems.add(item);
            }
            //setRecyclerView();
            setSelectedItemTextView();
        }
    }

    private void setSelectedItemTextView()
    {
        if (mArrayListSelectedItems != null && mArrayListSelectedItems.size() > 0)
        {
            Collections.sort(mArrayListSelectedItems, new Comparator<SelectedItem>() {
                @Override
                public int compare(SelectedItem o1, SelectedItem o2) {
                    return o1.getItemName().compareTo(o2.getItemName());
                }
            });

            String textForSelectedItemsTextView ="";
            for (SelectedItem eachSelectedItem:mArrayListSelectedItems) {
                textForSelectedItemsTextView += "  #" + Utils.toLowerCaseExceptFirstLetter(eachSelectedItem.getItemName());
            }

            if (textForSelectedItemsTextView.trim().length() > 0) {
                mTextViewSelectedItems.setVisibility(View.VISIBLE);
                mTextViewSelectedItems.setText(textForSelectedItemsTextView);
            }
            else
                mTextViewSelectedItems.setVisibility(View.INVISIBLE);
        }
        else
            mTextViewSelectedItems.setVisibility(View.INVISIBLE);

    }


    //---------------------------methods used for Search and select medicines for prescription evaluation
    private void getOldSelectedMedicineIds(final String oldSelectedData)
    {
        mHashMapSelectedMedicineIdsAndNames.clear();
        mHashMapSelectedMedicineIdsAndItemCounts.clear();

        if(oldSelectedData != null && oldSelectedData.trim().length() > 0) {
            final String[] stringOfItems = oldSelectedData.split(",");
            for (int i = 0; i < stringOfItems.length; i=i+2) {

                mHashMapSelectedMedicineIdsAndItemCounts.put(stringOfItems[i], Integer.valueOf(stringOfItems[i+1]));

                mFirebaseAllMedicinesRef.child(stringOfItems[i]).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override

                    public void onDataChange(DataSnapshot dataSnapshot) {
                        globalCount++;
                        if (dataSnapshot.exists())
                        {
                            Medicine med = dataSnapshot.getValue(Medicine.class);
                            if (med != null) {
                                mHashMapSelectedMedicineIdsAndNames.put(med.getMedicineId(), med.getMedicineName());
                            }

                        }

                        if (globalCount >= stringOfItems.length)
                            setSelectedItemTextViewForSelectedMedicines();
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });

            }

        }


    }

    private void setSelectedItemTextViewForSelectedMedicines()
    {
        if (mHashMapSelectedMedicineIdsAndNames != null && mHashMapSelectedMedicineIdsAndNames.size() > 0)
        {
            if (mHashMapSelectedMedicineIdsAndItemCounts != null && mHashMapSelectedMedicineIdsAndItemCounts.size() > 0) {
                for (Map.Entry eachSelectedIdAndItemCountPair : mHashMapSelectedMedicineIdsAndItemCounts.entrySet()) {
                    if (!mHashMapSelectedMedicineIdsAndNames.containsKey(eachSelectedIdAndItemCountPair.getKey()))
                        mHashMapSelectedMedicineIdsAndItemCounts.remove(eachSelectedIdAndItemCountPair.getKey());
                }
            }

            String textForSelectedItemsTextView ="";
            for (Map.Entry eachSelectedItem : mHashMapSelectedMedicineIdsAndNames.entrySet()) {
                textForSelectedItemsTextView += "  #" + Utils.toLowerCaseExceptFirstLetter(eachSelectedItem.getValue().toString());
            }

            if (textForSelectedItemsTextView.trim().length() > 0) {
                mTextViewSelectedItems.setVisibility(View.VISIBLE);
                mTextViewSelectedItems.setText(textForSelectedItemsTextView);
            }
            else
                mTextViewSelectedItems.setVisibility(View.INVISIBLE);
        }
        else
            mTextViewSelectedItems.setVisibility(View.INVISIBLE);
    }



    private void onMedicineListViewClicked(Medicine clickedMedicine,View view)
    {
        ImageView imageView = (ImageView) view.findViewById(R.id.image_view_add);
        TextView textViewItemCount = (TextView) view.findViewById(R.id.text_view_single_medicine_item_count);

        if (mWhatToSearch.equals(Constants.SEARCH_AND_CHOOSE_MEDICINE))
        {
            if (mHashMapSelectedMedicineIdsAndNames.get(clickedMedicine.getMedicineId()) != null)
            {
                mHashMapSelectedMedicineIdsAndNames.remove(clickedMedicine.getMedicineId());
                mHashMapSelectedMedicineIdsAndItemCounts.remove(clickedMedicine.getMedicineId());

                textViewItemCount.setVisibility(View.GONE);
                imageView.setVisibility(View.GONE);
            }
            else
            {
                openDialogToSelectMedicineItemCount(clickedMedicine,imageView,textViewItemCount);
            }
        }

        setSelectedItemTextViewForSelectedMedicines();
    }

    public void setSearchEditHint()
    {
        if (mSpecialSearch != null && !mSpecialSearch.equals("Other"))
        {
            mEditTextSearch.setVisibility(View.GONE);
            return;
        }

        mEditTextSearch.setVisibility(View.VISIBLE);

        switch (searchOrderBy)
        {
            case Constants.FIREBASE_PROPERTY_MEDICINE_NAME :
                mEditTextSearch.setHint("Medicine Name");
                break;
            case Constants.FIREBASE_PROPERTY_MEDICINE_MANUFACTURER_NAME :
                mEditTextSearch.setHint("Manufacturer Name");
                break;
            case Constants.FIREBASE_PROPERTY_MEDICINE_COMPOSITION :
                mEditTextSearch.setVisibility(View.GONE);
                mImageViewSort.setVisibility(View.GONE);
                break;
            case Constants.FIREBASE_PROPERTY_MEDICINE_CATEGORY :
                mEditTextSearch.setHint("Medicine Category");
                break;
            case Constants.FIREBASE_PROPERTY_MEDICINE_TYPE :
                mEditTextSearch.setHint("Medicine Type");
                break;
            case Constants.FIREBASE_PROPERTY_COMPOSITION_NAME :
                mEditTextSearch.setHint("Composition Name");
                break;
            case Constants.FIREBASE_PROPERTY_DISPLAY_CATEGORY_NAME :
                mEditTextSearch.setHint("Category Name");
                break;
            case Constants.FIREBASE_PROPERTY_POSTER_NAME :
                mEditTextSearch.setHint("Poster Name");
                break;

            case Constants.FIREBASE_PROPERTY_ORDER_STATUS :
                mEditTextSearch.setHint("Order Status");
                break;
            case Constants.FIREBASE_PROPERTY_ORDER_PAYMENT_METHOD :
                mEditTextSearch.setHint("Payment Method");
                break;
            case Constants.FIREBASE_PROPERTY_ORDER_ID :
                mEditTextSearch.setHint("Order Id");
                break;
            case Constants.FIREBASE_PROPERTY_ORDER_PLACED_BY:
                mEditTextSearch.setHint("abc@xyz.com");
                break;

            case Constants.FIREBASE_PROPERTY_QUERY_STATUS :
                mEditTextSearch.setHint("Query Status");
                break;
            case Constants.FIREBASE_PROPERTY_QUERY_TITLE :
                mEditTextSearch.setHint("Query Title");
                break;
            case Constants.FIREBASE_PROPERTY_QUERY_POSTED_BY :
                mEditTextSearch.setHint("abc@xyz.com");
                break;
            case Constants.FIREBASE_PROPERTY_FAQ_QUESTION :
                mEditTextSearch.setHint("Question");
                break;
            case Constants.FIREBASE_PROPERTY_USER_NAME :
                mEditTextSearch.setHint("User Name");
                break;
            case Constants.FIREBASE_PROPERTY_USER_EMAIL :
                mEditTextSearch.setHint("abc@xyz.com");
                break;
            case Constants.FIREBASE_PROPERTY_USER_PHONE_NO :
                mEditTextSearch.setHint("User Phone No");
                break;
            case Constants.FIREBASE_PROPERTY_USER_TYPE :
                mEditTextSearch.setHint("User Type");
                break;


            case Constants.FIREBASE_PROPERTY_PIN_CODE :
                mEditTextSearch.setHint("Pin Code");
                break;
            case Constants.FIREBASE_PROPERTY_DISTRICT :
                mEditTextSearch.setHint("District");
                break;
            case Constants.FIREBASE_PROPERTY_STATE :
                mEditTextSearch.setHint("State");
                break;

            case "key" :
                mEditTextSearch.setHint("Medicine Type (Ex Capsule)");
                break;

            case Constants.FIREBASE_PROPERTY_FAQ_PRIORITY :
            case Constants.FIREBASE_PROPERTY_TIMESTAMP_CREATED + "/" + Constants.FIREBASE_PROPERTY_TIMESTAMP :
            default:
                mEditTextSearch.setVisibility(View.GONE);
                break;
        }
    }

    //------------------------------------ Dialogs -----------------------------------

    public void openDialogToSelectMedicineItemCount(final Medicine medicine,final ImageView imageView,final TextView textViewItemCount) {

        final Dialog selectMedicineItemCountDialog = new Dialog(this);
        selectMedicineItemCountDialog.setTitle("Select medicine globalCount");
        selectMedicineItemCountDialog.setContentView(R.layout.dialog_select_medicine_item_count);

        final Spinner spinnerItemCount = (Spinner) selectMedicineItemCountDialog.findViewById(R.id.spinner_item_counts);
        TextView textViewDone = (TextView) selectMedicineItemCountDialog.findViewById(R.id.text_view_done);
        TextView textViewDiscard = (TextView) selectMedicineItemCountDialog.findViewById(R.id.text_view_discard);
        final TextView textViewErrorMsg = (TextView) selectMedicineItemCountDialog.findViewById(R.id.text_view_error_message);

        selectMedicineItemCountDialog.show();

        ArrayList<Integer> itemCountArrayList = new ArrayList<Integer>();
        itemCountArrayList.add(0);
        int itemsInOnePack = medicine.getNoOfItemsInOnePack();

        if(medicine.isLooseAvailable())
        {
            for(int i=1;i<=5*itemsInOnePack;i++)
                itemCountArrayList.add(i);
        }
        else{
            for(int i=1;i<=5;i++)
                itemCountArrayList.add(itemsInOnePack*i);
        }


        ArrayAdapter<Integer> itemCountAdapter = new ArrayAdapter<>(this,
                 android.R.layout.simple_spinner_item,itemCountArrayList);
        itemCountAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerItemCount.setAdapter(itemCountAdapter);

        textViewDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (! ((Integer) spinnerItemCount.getSelectedItem()).equals(0)) {

                    mHashMapSelectedMedicineIdsAndNames.put(medicine.getMedicineId(),medicine.getMedicineName());
                    mHashMapSelectedMedicineIdsAndItemCounts.put(medicine.getMedicineId(), (Integer) spinnerItemCount.getSelectedItem());

                    imageView.setVisibility(View.VISIBLE);
                    textViewItemCount.setVisibility(View.VISIBLE);
                    textViewItemCount.setText(spinnerItemCount.getSelectedItem().toString());

                    selectMedicineItemCountDialog.dismiss();
                    return;
                }
                else
                    textViewErrorMsg.setText("Invalid Item Count");
            }
        });

        textViewDiscard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectMedicineItemCountDialog.dismiss();
            }
        });


    }

    public void openDialogForDefaultMedicinePic(final DefaultKeyValuePair keyValuePair, final Uri data)
    {

        final Dialog viewDefaultMedicinePicDialog = new Dialog(this,android.R.style.Theme_Light_NoTitleBar_Fullscreen);
        viewDefaultMedicinePicDialog.setContentView(R.layout.dialog_default_medicine_pic);

        ImageView imageViewDefaultMedicinePic = (ImageView) viewDefaultMedicinePicDialog.findViewById(R.id.image_view_default_medicine_pic);
        ImageView imageViewCloseDialog = (ImageView) viewDefaultMedicinePicDialog.findViewById(R.id.image_view_close_dialog);
        final EditText editTextMedicineType = (EditText) viewDefaultMedicinePicDialog.findViewById(R.id.edit_text_medicine_type);
        TextView textViewAction = (TextView)  viewDefaultMedicinePicDialog.findViewById(R.id.text_view_action);
        FloatingActionButton fabFetchImage = (FloatingActionButton) viewDefaultMedicinePicDialog.findViewById(R.id.fab_fetch_image);


        imageViewCloseDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewDefaultMedicinePicDialog.dismiss();
            }
        });

        if (keyValuePair != null)
        {
            if (keyValuePair.getKey() != null && !keyValuePair.getKey().equals("*NA*"))
                editTextMedicineType.setText(Utils.toLowerCaseExceptFirstLetter(keyValuePair.getKey()));
            else
                editTextMedicineType.setText("");

            Glide.with(imageViewDefaultMedicinePic.getContext())
                    .load(keyValuePair.getValue())
                    .into(imageViewDefaultMedicinePic);

        }

        viewDefaultMedicinePicDialog.show();


        fabFetchImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextMedicineType.getText().toString().length() > 0)
                    tempMedicineType = editTextMedicineType.getText().toString();
                else
                    tempMedicineType = "*NA*";

                Intent imagePickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
                imagePickerIntent.setType("image/*");
                imagePickerIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(imagePickerIntent, "Complete action using"), RC_IMAGE_PICKER);

                viewDefaultMedicinePicDialog.dismiss();
            }
        });

        textViewAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextMedicineType.getText().toString().trim().length() > 0 && keyValuePair != null &&
                        keyValuePair.getValue() != null && keyValuePair.getValue().toString().length() >= 0) {

                    if (data != null) {
                        savePicInExactLocation(data,keyValuePair,editTextMedicineType.getText().toString());
                    }
                    else
                    {
                        keyValuePair.setKey(editTextMedicineType.getText().toString().toUpperCase());
                        mFirebaseAllDefaultMedicinePics.child(editTextMedicineType.getText().toString().trim().toLowerCase()).setValue(keyValuePair);
                        Toast.makeText(SearchActivity.this,Constants.UPDATE_SUCCESSFUL,Toast.LENGTH_SHORT).show();
                    }

                    viewDefaultMedicinePicDialog.dismiss();
                    MedicineOperations obj = new MedicineOperations(mCurrentUser,SearchActivity.this);
                    obj.getAllDefaultValues();

                }
                else
                    Toast.makeText(SearchActivity.this,"Enter Data",Toast.LENGTH_SHORT).show();
            }
        });

    }

    private  void savePicInExactLocation(Uri data,final DefaultKeyValuePair keyValuePair,final String key)
    {
        StorageReference selectedPhotoRef = mFirebaseAllMedicinesStorageRef.child(data.getLastPathSegment());
        UploadTask uploadTask = selectedPhotoRef.putFile(data);
        uploadTask.addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                try {

                    String oldImageRef = keyValuePair.getValue();
                    keyValuePair.setKey(key.toUpperCase());
                    keyValuePair.setValue(downloadUrl.toString());

                    mFirebaseAllDefaultMedicinePics.child(key.trim().toLowerCase()).setValue(keyValuePair);
                    Toast.makeText(SearchActivity.this,Constants.UPDATE_SUCCESSFUL,Toast.LENGTH_SHORT).show();

                    FirebaseStorage  mFirebaseStorage = FirebaseStorage.getInstance();
                    StorageReference photoRef = mFirebaseStorage.getReferenceFromUrl(oldImageRef);
                    photoRef.delete();


                } catch (Exception ex) {
                    Toast.makeText(SearchActivity.this,Constants.UPDATE_FAIL,Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}