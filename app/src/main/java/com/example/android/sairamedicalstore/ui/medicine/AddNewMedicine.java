package com.example.android.sairamedicalstore.ui.medicine;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.android.sairamedicalstore.R;
import com.example.android.sairamedicalstore.SairaMedicalStoreApplication;
import com.example.android.sairamedicalstore.models.DefaultKeyValuePair;
import com.example.android.sairamedicalstore.models.User;
import com.example.android.sairamedicalstore.operations.MedicineOperations;
import com.example.android.sairamedicalstore.ui.search.SearchActivity;
import com.example.android.sairamedicalstore.utils.Constants;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

public class AddNewMedicine extends AppCompatActivity {

    Spinner mSpinnerMedicineCategory,mSpinnerMedicineType,mSpinnerMedicineAvailability,mSpinnerLooseMedicineAvailability;
    ImageView mImageViewMedicineImage;
    EditText mEditTextMedicineName,mEditTextMedicineManufacturerName,mEditTextComposition,mEditTextItemsInOnePack,
            mEditTextPricePerPack,mEditTextDisplayCategory;
    LinearLayout mLinearLayoutMainContent;

    User mCurrentUser;
    MedicineOperations operationObject;
    String mMedicineImageUrl;
    ArrayList<String> mArrayListMedicineCategories, mArrayListMedicineTypes;
    ArrayList<DefaultKeyValuePair>  mArrayListDefaultMedicinePics;

    private static final int RC_COMPOSITION_PICKER = 1;
    private static final int RC_MANUFACTURER_PICKER = 2;
    private static final int RC_DISPLAY_CATEGORY_PICKER = 3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_medicine);

        mCurrentUser = ((SairaMedicalStoreApplication) this.getApplication()).getCurrentUser();
        operationObject = new MedicineOperations(mCurrentUser,this);

        initializeScreen();

        getDefaultMedicinePics();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.availability_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerMedicineAvailability.setAdapter(adapter);
        mSpinnerLooseMedicineAvailability.setAdapter(adapter);


        setMedicineCategories();
        setMedicineTypes();

        mSpinnerMedicineType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Glide.with(mImageViewMedicineImage.getContext())
                        .load(mArrayListDefaultMedicinePics.get(getIndexFromArrayList(mSpinnerMedicineType.getSelectedItem().toString())).getValue())
                        .into(mImageViewMedicineImage);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mEditTextMedicineManufacturerName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldSelectedManufacturerName = mEditTextMedicineManufacturerName.getText().toString();
                Intent searchActivity = new Intent(AddNewMedicine.this, SearchActivity.class);
                searchActivity.putExtra("whatToSearch",Constants.SEARCH_MEDICINE_MANUFACTURER);
                startActivityForResult(Intent.createChooser(searchActivity, "Complete action using"), RC_MANUFACTURER_PICKER);
            }
        });

        mEditTextComposition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldSelectedCompositionName = mEditTextComposition.getText().toString();
                Intent searchActivity = new Intent(AddNewMedicine.this, SearchActivity.class);
                searchActivity.putExtra("whatToSearch",Constants.SEARCH_MEDICINE_COMPOSITION);
                searchActivity.putExtra("oldSelectedData",oldSelectedCompositionName);
                startActivityForResult(Intent.createChooser(searchActivity, "Complete action using"), RC_COMPOSITION_PICKER);
            }
        });

        mEditTextDisplayCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldSelectedDisplayCategories = mEditTextDisplayCategory.getText().toString();
                Intent searchActivity = new Intent(AddNewMedicine.this, SearchActivity.class);
                searchActivity.putExtra("whatToSearch",Constants.SEARCH_DISPLAY_CATEGORY);
                searchActivity.putExtra("oldSelectedData",oldSelectedDisplayCategories);
                startActivityForResult(Intent.createChooser(searchActivity, "Complete action using"), RC_DISPLAY_CATEGORY_PICKER);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_COMPOSITION_PICKER) {
            if (resultCode == RESULT_OK) {
               String composition  = data.getStringExtra("returnText");
               if(composition != null && composition.trim().length()>0)
                    mEditTextComposition.setText(composition);
               else
                   mEditTextComposition.setText("");

            } else if (resultCode == RESULT_CANCELED) {
                // Unable to pick this file
                // progressBar.setVisibility(View.GONE);
                Toast.makeText(this, "Something went wrong.", Toast.LENGTH_SHORT).show();
            }
        }
        else if (requestCode == RC_MANUFACTURER_PICKER) {
            if (resultCode == RESULT_OK) {
                String manufacturer  = data.getStringExtra("returnText");
                if(manufacturer != null && manufacturer.trim().length()>0)
                    mEditTextMedicineManufacturerName.setText(manufacturer);
                else
                    mEditTextMedicineManufacturerName.setText("");

            } else if (resultCode == RESULT_CANCELED) {
                // Unable to pick this file
                // progressBar.setVisibility(View.GONE);
                Toast.makeText(this, "Something went wrong.", Toast.LENGTH_SHORT).show();
            }
        }
        else if (requestCode == RC_DISPLAY_CATEGORY_PICKER) {
            if (resultCode == RESULT_OK) {
                String displayCategory  = data.getStringExtra("returnText");
                if(displayCategory != null && displayCategory.trim().length()>0)
                    mEditTextDisplayCategory.setText(displayCategory);
                else
                    mEditTextDisplayCategory.setText("");

            } else if (resultCode == RESULT_CANCELED) {
                // Unable to pick this file
                // progressBar.setVisibility(View.GONE);
                Toast.makeText(this, "Something went wrong.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initializeScreen()
    {
        mLinearLayoutMainContent = (LinearLayout) findViewById(R.id.linear_layout_main_content);

        mSpinnerMedicineCategory = (Spinner) findViewById(R.id.spinner_medicine_category);
        mSpinnerMedicineType = (Spinner) findViewById(R.id.spinner_medicine_type);
        mSpinnerMedicineAvailability = (Spinner) findViewById(R.id.spinner_medicine_availability);
        mSpinnerLooseMedicineAvailability = (Spinner) findViewById(R.id.spinner_loose_medicine_availability);

        mImageViewMedicineImage = (ImageView) findViewById(R.id.image_view_medicine_image);

        mEditTextMedicineName = (EditText) findViewById(R.id.edit_text_medicine_name);
        mEditTextMedicineManufacturerName = (EditText) findViewById(R.id.edit_text_medicine_manufacturer_name);
        mEditTextComposition = (EditText) findViewById(R.id.edit_text_medicine_composition);
        mEditTextItemsInOnePack = (EditText) findViewById(R.id.edit_text_no_of_items_in_one_pack);
        mEditTextPricePerPack = (EditText) findViewById(R.id.edit_text_price_per_pack);
        mEditTextDisplayCategory = (EditText) findViewById(R.id.edit_text_display_category);

        mArrayListMedicineCategories = new ArrayList<String>();
        mArrayListMedicineTypes = new ArrayList<String>();
        mArrayListDefaultMedicinePics = new ArrayList<DefaultKeyValuePair>();

        mMedicineImageUrl = "default";
    }

    public void onSaveClick(View v)
    {
        String[] spinnerValues = getResources().getStringArray(R.array.availability_values);
        int selectedAvailabilityPosition = mSpinnerMedicineAvailability.getSelectedItemPosition();
        int selectedLooseAvailabilityPosition = mSpinnerLooseMedicineAvailability.getSelectedItemPosition();

        String selectedCategoryValue = mSpinnerMedicineCategory.getSelectedItem().toString();
        String selectedTypeValue = mSpinnerMedicineType.getSelectedItem().toString();
        String selectedDisplayCategories = mEditTextDisplayCategory.getText().toString();
        if(selectedDisplayCategories.trim().length() <1)
            selectedDisplayCategories = null;
        else
            selectedDisplayCategories = selectedDisplayCategories.toUpperCase();
        Boolean selectedAvailabilityValue = Boolean.valueOf(spinnerValues[selectedAvailabilityPosition]);
        Boolean selectedLooseAvailabilityValue = Boolean.valueOf(spinnerValues[selectedLooseAvailabilityPosition]);

        boolean isPageValid = checkPageValidation();
        if(isPageValid) {

            operationObject.AddNewMedicine(mEditTextMedicineName.getText().toString().toUpperCase(),
                    mEditTextMedicineManufacturerName.getText().toString().toUpperCase(),
                    mEditTextComposition.getText().toString().toUpperCase(), selectedCategoryValue,selectedDisplayCategories ,selectedTypeValue, mMedicineImageUrl,
                    Integer.valueOf(mEditTextItemsInOnePack.getText().toString()),
                    Double.valueOf(mEditTextPricePerPack.getText().toString()),
                    selectedAvailabilityValue, selectedLooseAvailabilityValue);

            Glide.with(mImageViewMedicineImage.getContext())
                    .load(mArrayListDefaultMedicinePics.get(getIndexFromArrayList(mSpinnerMedicineType.getSelectedItem().toString())).getValue())
                    .into(mImageViewMedicineImage);
        }
        else
            Toast.makeText(this, "Fill all the fields.", Toast.LENGTH_LONG).show();
    }

    private void getDefaultMedicinePics()
    {
        mArrayListDefaultMedicinePics = ((SairaMedicalStoreApplication) this.getApplication()).getArrayListDefaultMedicinePics();
    }

    private void setMedicineCategories()
    {
        Firebase medicineCategoriesRef = new Firebase(Constants.FIREBASE_URL_SAIRA_All_MEDICINE_CATEGORIES);
        medicineCategoriesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    mArrayListMedicineCategories.clear();
                    for (DataSnapshot areaSnapshot: dataSnapshot.getChildren()) {
                        String category = areaSnapshot.child(Constants.FIREBASE_PROPERTY_CATEGORY_NAME).getValue(String.class);
                        mArrayListMedicineCategories.add(category);
                    }

                    ArrayAdapter<String> categoriesAdapter = new ArrayAdapter<String>(AddNewMedicine.this, android.R.layout.simple_spinner_item, mArrayListMedicineCategories);
                    categoriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mSpinnerMedicineCategory.setAdapter(categoriesAdapter);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void setMedicineTypes()
    {
        Firebase medicineTypesRef = new Firebase(Constants.FIREBASE_URL_SAIRA_All_MEDICINE_TYPES);
        medicineTypesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    mArrayListMedicineTypes.clear();
                    for (DataSnapshot areaSnapshot: dataSnapshot.getChildren()) {
                        String type = areaSnapshot.child(Constants.FIREBASE_PROPERTY_TYPE_NAME).getValue(String.class);
                        mArrayListMedicineTypes.add(type);
                    }

                    ArrayAdapter<String> typesAdapter = new ArrayAdapter<String>(AddNewMedicine.this,
                            android.R.layout.simple_spinner_item, mArrayListMedicineTypes);
                    typesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mSpinnerMedicineType.setAdapter(typesAdapter);

                    //set default pic as capsule as spineer's default value is capsule
                    Glide.with(mImageViewMedicineImage.getContext())
                            .load(mArrayListDefaultMedicinePics.get(getIndexFromArrayList(mSpinnerMedicineType.getSelectedItem().toString())).getValue())
                            .into(mImageViewMedicineImage);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }


    private boolean checkPageValidation()
    {
        for( int i = 0; i < mLinearLayoutMainContent.getChildCount(); i++ ){
            View view = mLinearLayoutMainContent.getChildAt(i);
            if (view instanceof EditText) {
                if(((EditText)view).getText().toString().trim().length() < 1 && view.getId() != R.id.edit_text_display_category)
                    return false;
            }
        }

        return true;
    }

    private int getIndexFromArrayList(String key)
    {
        for (int i = 0; i < mArrayListDefaultMedicinePics.size(); i++) {
            if(mArrayListDefaultMedicinePics.get(i).getKey().equals(key))
                return i;
        }

        return 0;
    }


}
