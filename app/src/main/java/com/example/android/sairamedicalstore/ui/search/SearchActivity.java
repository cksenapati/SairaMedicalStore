package com.example.android.sairamedicalstore.ui.search;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.sairamedicalstore.R;
import com.example.android.sairamedicalstore.SairaMedicalStoreApplication;
import com.example.android.sairamedicalstore.models.Composition;
import com.example.android.sairamedicalstore.models.DisplayCategory;
import com.example.android.sairamedicalstore.models.Manufacturer;
import com.example.android.sairamedicalstore.models.Medicine;
import com.example.android.sairamedicalstore.models.Poster;
import com.example.android.sairamedicalstore.models.SelectedItem;
import com.example.android.sairamedicalstore.models.User;
import com.example.android.sairamedicalstore.operations.MedicineOperations;
import com.example.android.sairamedicalstore.ui.AddNewMedicine;
import com.example.android.sairamedicalstore.ui.ProductDetailsActivity;
import com.example.android.sairamedicalstore.utils.Constants;
import com.example.android.sairamedicalstore.utils.Utils;
import com.firebase.client.Firebase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SearchActivity extends AppCompatActivity {

    String mUserType, mWhatToSearch;
    ListView mListViewSearchResult;
    EditText mEditTextSearch;
    Button mButtonAddItem, mButtonDone;
    RecyclerView mRecyclerViewSelectedItems;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mRecyclerAdapter;

    String errorMessage;
    Boolean isSuccess;
    User mCurrentUser;
    MedicineOperations operationObject;
    Firebase mFirebaseAllMedicinesRef, mFirebaseAllMedicineManufacturersRef,
            mFirebaseAllMedicineCompositionsRef,mFirebaseAllDisplayCategoriesRef,mFirebaseAllPostersRef;
    private SearchedMedicinesAdapter mSearchedMedicinesAdapter;
    private SearchedManufacturersAdapter mSerarchedManufacturerAdapter;
    private SearchedCompositionsAdapter mSearchedCompositionAdapter;
    private SearchedDisplayCategoriesAdapter mSearchedDisplayCategoryAdapter;
    private SearchedPostersAdapter mSearchedPostersAdapter;

    ArrayList<SelectedItem> mArrayListSelectedItems;


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

        mUserType = ((SairaMedicalStoreApplication) this.getApplication()).getUserType();

        initializeScreen();

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
                    String textEntered = mEditTextSearch.getText().toString().toUpperCase();
                    displayResultAccordingly(textEntered);
                } else
                    mListViewSearchResult.setAdapter(null);
            }
        });


    }

    private void initializeScreen() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        /* Common toolbar setup */
        setSupportActionBar(toolbar);
        setActivityTitle();

        mListViewSearchResult = (ListView) findViewById(R.id.list_view_search_result);
        mEditTextSearch = (EditText) findViewById(R.id.edit_text_search);
        mButtonAddItem = (Button) findViewById(R.id.button_add_item);
        mButtonDone = (Button) findViewById(R.id.button_done);
        mRecyclerViewSelectedItems = (RecyclerView) findViewById(R.id.recycler_view_selected_items);
        mLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);

        mCurrentUser = ((SairaMedicalStoreApplication) this.getApplication()).getCurrentUser();

        mArrayListSelectedItems = new ArrayList<SelectedItem>();

        mFirebaseAllMedicinesRef = new Firebase(Constants.FIREBASE_URL_SAIRA_ALL_MEDICINES);
        mFirebaseAllMedicineManufacturersRef = new Firebase(Constants.FIREBASE_URL_SAIRA_All_MANUFACTURERS);
        mFirebaseAllMedicineCompositionsRef = new Firebase(Constants.FIREBASE_URL_SAIRA_All_COMPOSITIONS);
        mFirebaseAllDisplayCategoriesRef = new Firebase(Constants.FIREBASE_URL_SAIRA_All_DISPLAY_CATEGORIES);
        mFirebaseAllPostersRef = new Firebase(Constants.FIREBASE_URL_SAIRA_All_POSTERS);

        setItemVisibilityAccordingToRequest();

    }

    private void setActivityTitle() {
        switch (mWhatToSearch) {
            case Constants.SEARCH_MEDICINE:
                setTitle("Search Medicines");
                break;
            case Constants.SEARCH_MEDICINE_MANUFACTURER:
                setTitle("Search Manufacturer");
                break;
            case Constants.SEARCH_MEDICINE_COMPOSITION:
                setTitle("Search Compositions");
                break;
            case Constants.SEARCH_DISPLAY_CATEGORY:
                setTitle("Search Display categories");
                break;
            case Constants.SEARCH_POSTER:
                setTitle("Search Posters");
                break;
        }
    }

    private void displayResultAccordingly(String textEntered) {
        switch (mWhatToSearch) {
            case Constants.SEARCH_MEDICINE:
                searchMedicine(textEntered);
                break;
            case Constants.SEARCH_MEDICINE_MANUFACTURER:
                searchMedicineManufacturer(textEntered);
                break;
            case Constants.SEARCH_MEDICINE_COMPOSITION:
                searchMedicineComposition(textEntered);
                break;
            case Constants.SEARCH_DISPLAY_CATEGORY:
                searchDisplayCategories(textEntered);
                break;
            case Constants.SEARCH_POSTER:
                searchPosters(textEntered);
                break;
        }
    }

    private void searchMedicine(String textEntered) {
        if (mSearchedMedicinesAdapter != null)
            mSearchedMedicinesAdapter.cleanup();

        mSearchedMedicinesAdapter = new SearchedMedicinesAdapter(SearchActivity.this, Medicine.class,
                R.layout.item_single_medicine, mFirebaseAllMedicinesRef.orderByChild(Constants.FIREBASE_PROPERTY_MEDICINE_NAME).startAt(textEntered).endAt(textEntered + "~"));

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

        mSerarchedManufacturerAdapter = new SearchedManufacturersAdapter(SearchActivity.this, Manufacturer.class,
                R.layout.item_single_selectable, mFirebaseAllMedicineManufacturersRef.orderByChild(Constants.FIREBASE_PROPERTY_MANUFACTURER_NAME).startAt(textEntered).endAt(textEntered + "~"));

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

        mSearchedCompositionAdapter = new SearchedCompositionsAdapter(SearchActivity.this, Composition.class,
                R.layout.item_single_selectable, mFirebaseAllMedicineCompositionsRef.orderByChild(Constants.FIREBASE_PROPERTY_COMPOSITION_NAME).startAt(textEntered).endAt(textEntered + "~"), mArrayListSelectedItems);

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

        mSearchedDisplayCategoryAdapter = new SearchedDisplayCategoriesAdapter(SearchActivity.this, DisplayCategory.class,
                R.layout.item_single_selectable, mFirebaseAllDisplayCategoriesRef.orderByChild(Constants.FIREBASE_PROPERTY_DISPLAY_CATEGORY_NAME).startAt(textEntered).endAt(textEntered + "~"), mArrayListSelectedItems);

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

        mSearchedPostersAdapter = new SearchedPostersAdapter(SearchActivity.this, Poster.class,
                R.layout.item_single_poster, mFirebaseAllPostersRef.orderByChild(Constants.FIREBASE_PROPERTY_POSTER_NAME).startAt(textEntered).endAt(textEntered + "~"));

        mListViewSearchResult.setAdapter(mSearchedPostersAdapter);

        mListViewSearchResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Poster poster = mSearchedPostersAdapter.getItem(position);
                /*Intent intentProductDetails = new Intent(SearchActivity.this, ProductDetailsActivity.class);
                intentProductDetails.putExtra("medicineId", medicine.getMedicineId());
                startActivity(intentProductDetails);*/
            }
        });
    }




    private void setItemVisibilityAccordingToRequest() {
        switch (mWhatToSearch) {
            case Constants.SEARCH_MEDICINE:
                mButtonAddItem.setVisibility(View.GONE);
                mButtonDone.setVisibility(View.GONE);
                break;
            case Constants.SEARCH_MEDICINE_MANUFACTURER:
                mButtonAddItem.setVisibility(View.VISIBLE);
                mButtonDone.setVisibility(View.VISIBLE);
                break;
            case Constants.SEARCH_MEDICINE_COMPOSITION:
                mButtonAddItem.setVisibility(View.VISIBLE);
                mButtonDone.setVisibility(View.VISIBLE);
                break;
            case Constants.SEARCH_POSTER:
                mButtonAddItem.setVisibility(View.VISIBLE);
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

    private void addNewItem(final String itemName, final Dialog addNewItemDialog, final TextView textViewErrorMsg) {

        if (mWhatToSearch.equals(Constants.SEARCH_MEDICINE_MANUFACTURER))
            operationObject.addNewManufacturer(itemName,addNewItemDialog,textViewErrorMsg);
        else if (mWhatToSearch.equals(Constants.SEARCH_MEDICINE_COMPOSITION))
            operationObject.addNewComposition(itemName,addNewItemDialog,textViewErrorMsg);
        else if (mWhatToSearch.equals(Constants.SEARCH_DISPLAY_CATEGORY))
            operationObject.addNewDisplayCategory(itemName,addNewItemDialog,textViewErrorMsg);
    }


    public void onAddClick(View view) {
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