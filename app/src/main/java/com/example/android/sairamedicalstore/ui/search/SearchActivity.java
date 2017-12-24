package com.example.android.sairamedicalstore.ui.search;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.android.sairamedicalstore.R;
import com.example.android.sairamedicalstore.SairaMedicalStoreApplication;
import com.example.android.sairamedicalstore.models.Composition;
import com.example.android.sairamedicalstore.models.DisplayCategory;
import com.example.android.sairamedicalstore.models.Faq;
import com.example.android.sairamedicalstore.models.Manufacturer;
import com.example.android.sairamedicalstore.models.Medicine;
import com.example.android.sairamedicalstore.models.Order;
import com.example.android.sairamedicalstore.models.Poster;
import com.example.android.sairamedicalstore.models.Query;
import com.example.android.sairamedicalstore.models.SelectedItem;
import com.example.android.sairamedicalstore.models.User;
import com.example.android.sairamedicalstore.operations.MedicineOperations;
import com.example.android.sairamedicalstore.ui.customerSupport.AddOrUpdateFaqActivity;
import com.example.android.sairamedicalstore.ui.customerSupport.QueryTicketDetailsActivity;
import com.example.android.sairamedicalstore.ui.medicine.AddNewMedicine;
import com.example.android.sairamedicalstore.ui.poster.CreateOrUpdatePoster;
import com.example.android.sairamedicalstore.ui.ProductDetailsActivity;
import com.example.android.sairamedicalstore.ui.order.OrderDetailsActivity;
import com.example.android.sairamedicalstore.utils.Constants;
import com.example.android.sairamedicalstore.utils.Utils;
import com.firebase.client.Firebase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class SearchActivity extends AppCompatActivity {

    String  mWhatToSearch;
    ListView mListViewSearchResult;
    EditText mEditTextSearch;
    Button /*mButtonAddItem,*/ mButtonDone;
    RecyclerView mRecyclerViewSelectedItems;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mRecyclerAdapter;
    FloatingActionButton mFabAddItem;
    ImageView mImageViewSort,mImageViewBackArrow;
    TextView mTextViewActivityTitle;

    String errorMessage,searchOrderBy;
    Boolean isSuccess;
    User mCurrentUser;
    int maximumNoOfQueriesToDisplay;
    MedicineOperations operationObject;
    Firebase mFirebaseAllMedicinesRef, mFirebaseAllMedicineManufacturersRef,
            mFirebaseAllMedicineCompositionsRef,mFirebaseAllDisplayCategoriesRef,mFirebaseAllPostersRef,mFirebaseCurrentUserAllOrdersRef,
            mFirebaseAllQueriesRef,mFirebaseAllFaqsRef;
    private SearchedMedicinesAdapter mSearchedMedicinesAdapter;
    private SearchedManufacturersAdapter mSerarchedManufacturerAdapter;
    private SearchedCompositionsAdapter mSearchedCompositionAdapter;
    private SearchedDisplayCategoriesAdapter mSearchedDisplayCategoryAdapter;
    private SearchedPostersAdapter mSearchedPostersAdapter;
    private SearchedOrdersAdapter mSearchedOrdersAdapter;
    private SearchedQueriesAdapter mSearchedQueriesAdapter;
    private SearchedFaqsAdapter mSearchedFaqsAdapter;


    ArrayList<SelectedItem> mArrayListSelectedItems;
    ArrayList<Query> mArrayListQueriesFromAllUsers;
    HashMap<String,String> mHashMapSortOptions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        String oldSelectedData ="";
        Intent intent = getIntent();
        if (intent != null) {
            mWhatToSearch = intent.getStringExtra("whatToSearch");
            oldSelectedData = intent.getStringExtra("oldSelectedData");
        }


        initializeScreen();

        setActivityTitle();

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

    }

    private void initializeScreen() {


        mImageViewSort = (ImageView) findViewById(R.id.image_view_filter);
        mTextViewActivityTitle = (TextView) findViewById(R.id.text_view_activity_title);
        mImageViewBackArrow  = (ImageView) findViewById(R.id.image_view_back_arrow);


        mListViewSearchResult = (ListView) findViewById(R.id.list_view_search_result);
        mEditTextSearch = (EditText) findViewById(R.id.edit_text_search);
        //mButtonAddItem = (Button) findViewById(R.id.button_add_item);
        mButtonDone = (Button) findViewById(R.id.button_done);
        mRecyclerViewSelectedItems = (RecyclerView) findViewById(R.id.recycler_view_selected_items);
        mLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);

        mCurrentUser = ((SairaMedicalStoreApplication) this.getApplication()).getCurrentUser();

        mArrayListSelectedItems = new ArrayList<SelectedItem>();
        mArrayListQueriesFromAllUsers = new ArrayList<>();
        mHashMapSortOptions = new HashMap<>();

        mFabAddItem = (FloatingActionButton) findViewById(R.id.fab_add_item);

        mFirebaseAllMedicinesRef = new Firebase(Constants.FIREBASE_URL_SAIRA_ALL_MEDICINES);
        mFirebaseAllMedicineManufacturersRef = new Firebase(Constants.FIREBASE_URL_SAIRA_All_MANUFACTURERS);
        mFirebaseAllMedicineCompositionsRef = new Firebase(Constants.FIREBASE_URL_SAIRA_All_COMPOSITIONS);
        mFirebaseAllDisplayCategoriesRef = new Firebase(Constants.FIREBASE_URL_SAIRA_All_DISPLAY_CATEGORIES);
        mFirebaseAllPostersRef = new Firebase(Constants.FIREBASE_URL_SAIRA_All_POSTERS);
        mFirebaseCurrentUserAllOrdersRef = new Firebase(Constants.FIREBASE_URL_SAIRA_All_ORDERS).child(Utils.encodeEmail(mCurrentUser.getEmail()));
        mFirebaseAllQueriesRef = new Firebase(Constants.FIREBASE_URL_SAIRA_All_QUERIES);
        mFirebaseAllFaqsRef = new Firebase(Constants.FIREBASE_URL_SAIRA_All_FAQS);
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
            case Constants.SEARCH_ORDER:
                searchOrders(textEntered);
                break;
            case Constants.SEARCH_QUERY:
                searchQueries();
                break;
            case Constants.SEARCH_FAQ:
                searchFaqs(textEntered.toUpperCase());
                break;
            case Constants.SEARCH_ALL_QUERIES_FROM_ALL_USERS:
                searchQueriesFromAllUsers(textEntered.toUpperCase());
                break;
            case Constants.SEARCH_AND_CHOOSE_ORDER:
                searchAndChooseOrder(textEntered);
                break;
        }
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
                searchOrders(null);
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
                searchAndChooseOrder(null);
                break;
        }
    }


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
                Intent intentToAddMedicine = new Intent(SearchActivity.this, AddNewMedicine.class);
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
                onListViewItemClick(Utils.toLowerCaseExceptFirstLetter(composition.getCompositionName()),view);
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
                onListViewItemClick(Utils.toLowerCaseExceptFirstLetter(displayCategory.getDisplayCategoryName()),view);
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

    private void searchOrders(String textEntered) {
        if (mSearchedOrdersAdapter != null)
            mSearchedOrdersAdapter.cleanup();

        if (searchOrderBy == null || searchOrderBy.length() < 1)
            searchOrderBy = Constants.FIREBASE_PROPERTY_ORDER_STATUS;

        if (textEntered != null && textEntered.trim().length() >1)
            mSearchedOrdersAdapter = new SearchedOrdersAdapter(SearchActivity.this, Order.class,
                    R.layout.item_single_order, mFirebaseCurrentUserAllOrdersRef.
                    orderByChild(searchOrderBy).startAt(textEntered).endAt(textEntered + "~"));
        else
            mSearchedOrdersAdapter = new SearchedOrdersAdapter(SearchActivity.this, Order.class,
                    R.layout.item_single_order, mFirebaseCurrentUserAllOrdersRef.
                    orderByChild(searchOrderBy));

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

    private void searchQueries() {
        if (mSearchedQueriesAdapter != null)
            mSearchedQueriesAdapter.cleanup();

        mSearchedQueriesAdapter = new SearchedQueriesAdapter(SearchActivity.this, Query.class,
                R.layout.item_single_old_query_ticket, mFirebaseAllQueriesRef.
                orderByChild("queryPostedBy").equalTo(mCurrentUser.getEmail()));

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

    private void searchAndChooseOrder(String textEntered) {
        if (mSearchedOrdersAdapter != null)
            mSearchedOrdersAdapter.cleanup();

        if (searchOrderBy == null || searchOrderBy.length() < 1)
            searchOrderBy = Constants.FIREBASE_PROPERTY_ORDER_STATUS;

        if (textEntered != null && textEntered.trim().length() >1)
            mSearchedOrdersAdapter = new SearchedOrdersAdapter(SearchActivity.this, Order.class,
                R.layout.item_single_order, mFirebaseCurrentUserAllOrdersRef.
                orderByChild(searchOrderBy).startAt(textEntered).endAt(textEntered + "~"));
        else
            mSearchedOrdersAdapter = new SearchedOrdersAdapter(SearchActivity.this, Order.class,
                    R.layout.item_single_order, mFirebaseCurrentUserAllOrdersRef.
                    orderByChild(searchOrderBy));

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




    private void setItemVisibilityAccordingToRequest() {
        switch (mWhatToSearch) {
            case Constants.SEARCH_MEDICINE:
                mFabAddItem.setVisibility(View.GONE);
                mButtonDone.setVisibility(View.GONE);
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
                break;
            case Constants.SEARCH_QUERY:
                mFabAddItem.setVisibility(View.VISIBLE);
                mButtonDone.setVisibility(View.GONE);
                mImageViewSort.setVisibility(View.GONE);
                break;
            case Constants.SEARCH_FAQ:
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
        /*for (Map.Entry<String, Object> eachAnswer : mCurrentFaq.getFaqAnswers().entrySet()) {

            TextView textView = new TextView(this);
            textView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT)); // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.

            int padding = getResources().getDimensionPixelOffset(R.dimen.extra_small_margin);
            float textSize = getResources().getDimension(R.dimen.triple_extra_small_text_size);

            textView.setPadding(padding,padding,padding,padding);

            textView.setText("." + eachAnswer.getValue().toString());
            textView.setTextSize(textSize);

            mLinearLayoutFaqAnswers.addView(textView);
        }*/

        textViewExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFaqAnswerDialog.dismiss();
            }
        });

        showFaqAnswerDialog.show();
    }

    public void sortListViewAccordingToSearch()
    {
        mHashMapSortOptions.clear();

        switch (mWhatToSearch) {
            case Constants.SEARCH_MEDICINE:
                mHashMapSortOptions.put("Name",Constants.FIREBASE_PROPERTY_MEDICINE_NAME);
                mHashMapSortOptions.put("Availability",Constants.FIREBASE_PROPERTY_MEDICINE_AVAILABILITY);
                mHashMapSortOptions.put("Category (Ex: OTC)",Constants.FIREBASE_PROPERTY_MEDICINE_CATEGORY);
                mHashMapSortOptions.put("Manufacturer",Constants.FIREBASE_PROPERTY_MEDICINE_MANUFACTURER_NAME);
                mHashMapSortOptions.put("Type (Ex: Tablet)",Constants.FIREBASE_PROPERTY_MEDICINE_TYPE);
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
            case Constants.SEARCH_ORDER:
            case Constants.SEARCH_AND_CHOOSE_ORDER:
                mHashMapSortOptions.put("Order Status",Constants.FIREBASE_PROPERTY_ORDER_STATUS);
                mHashMapSortOptions.put("Payment Method",Constants.FIREBASE_PROPERTY_ORDER_PAYMENT_METHOD);
                mHashMapSortOptions.put("Most Recent",Constants.FIREBASE_PROPERTY_TIMESTAMP_CREATED + "/" + Constants.FIREBASE_PROPERTY_TIMESTAMP);
                openDialogForSort();
                break;
            case Constants.SEARCH_ALL_QUERIES_FROM_ALL_USERS:
                mHashMapSortOptions.put("Query Status",Constants.FIREBASE_PROPERTY_QUERY_STATUS);
                mHashMapSortOptions.put("Query Title",Constants.FIREBASE_PROPERTY_QUERY_TITLE);
                mHashMapSortOptions.put("Most Recent",Constants.FIREBASE_PROPERTY_TIMESTAMP_CREATED + "/" + Constants.FIREBASE_PROPERTY_TIMESTAMP);
                openDialogForSort();
                break;
            case Constants.SEARCH_FAQ:
                mHashMapSortOptions.put("Most Searched",Constants.FIREBASE_PROPERTY_FAQ_PRIORITY);
                mHashMapSortOptions.put("Question",Constants.FIREBASE_PROPERTY_FAQ_QUESTION);
                openDialogForSort();
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

        if (mHashMapSortOptions.size() < 1)
            return;

        for (Map.Entry<String, String> eachOption : mHashMapSortOptions.entrySet()) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setText(eachOption.getKey());
            /*
            if (searchOrderBy.equals(eachEntry))
                radioButton.setChecked(true);*/

            radioGroupFilterOptions.addView(radioButton);
        }

        filterDialog.show();

        textViewFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (radioGroupFilterOptions.getCheckedRadioButtonId() != -1) {
                    String selectedKey = ((RadioButton) filterDialog.findViewById(radioGroupFilterOptions.getCheckedRadioButtonId())).
                            getText().toString();

                    searchOrderBy = mHashMapSortOptions.get(selectedKey);

                    mEditTextSearch.setText(null);
                    displayAllData();
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

        Intent intentToAddMedicine = new Intent(SearchActivity.this, AddNewMedicine.class);
        intentToAddMedicine.putExtra("returnText", returnText);
        setResult(Activity.RESULT_OK, intentToAddMedicine);
        finish();
    }

    private void onListViewItemClick(String selectedItemName,View view)
    {
        boolean isRequestToAdd = true;
        ImageView imageView = (ImageView) view.findViewById(R.id.image_view_add);

        for(int i = 0 ; i<mArrayListSelectedItems.size() ; i++)
        {
            if(mArrayListSelectedItems.get(i).getItemName().equals(selectedItemName)) {
                mArrayListSelectedItems.remove(i);
                isRequestToAdd = false;
                break;
            }
        }

        if(isRequestToAdd)
        {
            SelectedItem item = new SelectedItem(selectedItemName);
            mArrayListSelectedItems.add(item);
            imageView.setVisibility(View.VISIBLE);
        }
        else
            imageView.setVisibility(View.GONE);

        setRecyclerView();

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
            setRecyclerView();
        }
    }

    private void setRecyclerView()
    {
        mRecyclerViewSelectedItems.setHasFixedSize(true);
        mRecyclerViewSelectedItems.setLayoutManager(mLayoutManager);
        if (mRecyclerAdapter != null)
            mRecyclerViewSelectedItems.setAdapter(null);
        if(mArrayListSelectedItems.size() > 0) {

            Collections.sort(mArrayListSelectedItems, new Comparator<SelectedItem>() {
                @Override
                public int compare(SelectedItem o1, SelectedItem o2) {
                    return o1.getItemName().compareTo(o2.getItemName());
                }
            });

            mRecyclerAdapter = new SelectedItemsAdapter(mArrayListSelectedItems, this);
            mRecyclerViewSelectedItems.setAdapter(mRecyclerAdapter);
        }
    }


}