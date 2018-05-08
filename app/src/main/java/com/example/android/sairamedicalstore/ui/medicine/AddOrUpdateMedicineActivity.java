package com.example.android.sairamedicalstore.ui.medicine;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.android.sairamedicalstore.R;
import com.example.android.sairamedicalstore.SairaMedicalStoreApplication;
import com.example.android.sairamedicalstore.models.DefaultKeyValuePair;
import com.example.android.sairamedicalstore.models.Medicine;
import com.example.android.sairamedicalstore.models.User;
import com.example.android.sairamedicalstore.operations.MedicineOperations;
import com.example.android.sairamedicalstore.ui.search.SearchActivity;
import com.example.android.sairamedicalstore.utils.Constants;
import com.example.android.sairamedicalstore.utils.Utils;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ServerValue;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static com.example.android.sairamedicalstore.ui.poster.CreateOrUpdatePoster.mCurrentPosterImageURI;

public class AddOrUpdateMedicineActivity extends AppCompatActivity {

    Spinner mSpinnerMedicineCategory,mSpinnerMedicineType,mSpinnerMedicineAvailability,mSpinnerLooseMedicineAvailability;
    ImageView mImageViewMedicineImage,mImageViewGoBack;
    TextView mTextViewActivityTitle, mTextViewUploadMedicineImage;
    EditText mEditTextMedicineName,mEditTextMedicineManufacturerName,mEditTextComposition,mEditTextItemsInOnePack,
            mEditTextPricePerPack,mEditTextDisplayCategory;
    LinearLayout mLinearLayoutMainContent;
    Button mButtonAction;

    User mCurrentUser;
    Medicine mCurrentMedicine;
    MedicineOperations operationObject;
    String mMedicineImageUrl;
    ArrayList<String> mArrayListMedicineCategories, mArrayListMedicineTypes;
    ArrayList<DefaultKeyValuePair>  mArrayListDefaultMedicinePics;

    ArrayAdapter<String> typesAdapter;
    ArrayAdapter<String> categoriesAdapter;
    ArrayAdapter<CharSequence> yesNoAdapter;

    private static final int RC_COMPOSITION_PICKER = 1;
    private static final int RC_MANUFACTURER_PICKER = 2;
    private static final int RC_DISPLAY_CATEGORY_PICKER = 3;
    private static final int RC_MEDICINE_IMAGE_PICKER =  4;

    private FirebaseStorage mFirebaseStorage;
    private StorageReference mFirebaseStorageReference;
    private Uri mCurrentMedicineImageURI;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_medicine);

        Intent intent = getIntent();
        if (intent != null) {
            mCurrentMedicine = (Medicine) intent.getSerializableExtra("currentMedicine");
        }

        mCurrentUser = ((SairaMedicalStoreApplication) this.getApplication()).getCurrentUser();
        operationObject = new MedicineOperations(mCurrentUser,this);

        initializeScreen();

        getDefaultMedicinePics();

        yesNoAdapter = ArrayAdapter.createFromResource(this,
                R.array.availability_options, android.R.layout.simple_spinner_item);
        yesNoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerMedicineAvailability.setAdapter(yesNoAdapter);
        mSpinnerLooseMedicineAvailability.setAdapter(yesNoAdapter);


        setMedicineCategories();

        mSpinnerMedicineType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mMedicineImageUrl.equals("default"))
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
                Intent searchActivity = new Intent(AddOrUpdateMedicineActivity.this, SearchActivity.class);
                searchActivity.putExtra("whatToSearch",Constants.SEARCH_MEDICINE_MANUFACTURER);
                startActivityForResult(Intent.createChooser(searchActivity, "Complete action using"), RC_MANUFACTURER_PICKER);
            }
        });

        mEditTextComposition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldSelectedCompositionName = mEditTextComposition.getText().toString();
                Intent searchActivity = new Intent(AddOrUpdateMedicineActivity.this, SearchActivity.class);
                searchActivity.putExtra("whatToSearch",Constants.SEARCH_MEDICINE_COMPOSITION);
                searchActivity.putExtra("oldSelectedData",oldSelectedCompositionName);
                startActivityForResult(Intent.createChooser(searchActivity, "Complete action using"), RC_COMPOSITION_PICKER);
            }
        });

        mEditTextDisplayCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldSelectedDisplayCategories = mEditTextDisplayCategory.getText().toString();
                Intent searchActivity = new Intent(AddOrUpdateMedicineActivity.this, SearchActivity.class);
                searchActivity.putExtra("whatToSearch",Constants.SEARCH_DISPLAY_CATEGORY);
                searchActivity.putExtra("oldSelectedData",oldSelectedDisplayCategories);
                startActivityForResult(Intent.createChooser(searchActivity, "Complete action using"), RC_DISPLAY_CATEGORY_PICKER);
            }
        });

        mTextViewUploadMedicineImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent imagePickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
                imagePickerIntent.setType("image/*");
                imagePickerIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(imagePickerIntent, "Complete action using"), RC_MEDICINE_IMAGE_PICKER);
            }
        });

        mButtonAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onActionClick();
            }
        });

        if (mCurrentMedicine != null)
            mTextViewActivityTitle.setText("Update Medicine");
        else
            mTextViewActivityTitle.setText("Create Medicine");

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
        else if (requestCode == RC_MEDICINE_IMAGE_PICKER) {
            if(resultCode == RESULT_OK){

                mCurrentMedicineImageURI = data.getData();

                StorageReference selectedPhotoRef = mFirebaseStorageReference.child(data.getData().getLastPathSegment());
                UploadTask uploadTask = selectedPhotoRef.putFile(data.getData());
                uploadTask.addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // When the image has successfully uploaded, we get its download URL
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        try {
                            mMedicineImageUrl = downloadUrl.toString();
                            Glide.with(mImageViewMedicineImage.getContext())
                                    .load(mMedicineImageUrl)
                                    .into(mImageViewMedicineImage);
                        }catch (Exception ex){}
                    }
                });
            }
            else if (resultCode == RESULT_CANCELED)
                Toast.makeText(this, "Unable to pick this image file", Toast.LENGTH_SHORT).show();
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
        mImageViewGoBack = (ImageView) findViewById(R.id.image_view_go_back);

        mTextViewActivityTitle = (TextView) findViewById(R.id.text_view_activity_title);
        mTextViewUploadMedicineImage = (TextView) findViewById(R.id.text_view_upload_medicine_image);

        mEditTextMedicineName = (EditText) findViewById(R.id.edit_text_medicine_name);
        mEditTextMedicineManufacturerName = (EditText) findViewById(R.id.edit_text_medicine_manufacturer_name);
        mEditTextComposition = (EditText) findViewById(R.id.edit_text_medicine_composition);
        mEditTextItemsInOnePack = (EditText) findViewById(R.id.edit_text_no_of_items_in_one_pack);
        mEditTextPricePerPack = (EditText) findViewById(R.id.edit_text_price_per_pack);
        mEditTextDisplayCategory = (EditText) findViewById(R.id.edit_text_display_category);

        mButtonAction = (Button) findViewById(R.id.button_action);

        mArrayListMedicineCategories = new ArrayList<String>();
        mArrayListMedicineTypes = new ArrayList<String>();
        mArrayListDefaultMedicinePics = new ArrayList<DefaultKeyValuePair>();

        mFirebaseStorage = FirebaseStorage.getInstance();
        mFirebaseStorageReference = mFirebaseStorage.getReference().child(Constants.FIREBASE_LOCATION_TEMPORARY_STORAGE);


        if (mCurrentMedicine == null)
            mMedicineImageUrl = "default";
        else
            mMedicineImageUrl = mCurrentMedicine.getMedicineImageUrl();

    }

    private void setupForMedicineUpdate()
    {
        mEditTextMedicineName.setText(Utils.toLowerCaseExceptFirstLetter(mCurrentMedicine.getMedicineName()));
        mEditTextComposition.setText(Utils.toLowerCaseExceptFirstLetter(mCurrentMedicine.getMedicineComposition()));
        mEditTextMedicineManufacturerName.setText(Utils.toLowerCaseExceptFirstLetter(mCurrentMedicine.getMedicineManufacturerName()));

        if(mCurrentMedicine.getDisplayCategory() != null)
          mEditTextDisplayCategory.setText(Utils.toLowerCaseExceptFirstLetter(mCurrentMedicine.getDisplayCategory()));

        int spinnerPositionForMedicineType = typesAdapter.getPosition(mCurrentMedicine.getMedicineType());
        mSpinnerMedicineType.setSelection(spinnerPositionForMedicineType);

        int spinnerPositionForMedicineCategory = categoriesAdapter.getPosition(mCurrentMedicine.getMedicineCategory());
        mSpinnerMedicineCategory.setSelection(spinnerPositionForMedicineCategory);

        mEditTextItemsInOnePack.setText(String.valueOf(mCurrentMedicine.getNoOfItemsInOnePack()));
        mEditTextPricePerPack.setText(String.valueOf(mCurrentMedicine.getPricePerPack()));


        String availability;
        if (mCurrentMedicine.isMedicineAvailability())
            availability = "Yes";
        else
            availability = "No";
        int spinnerPositionForMedicineAvailability = yesNoAdapter.getPosition(availability);
        mSpinnerMedicineAvailability.setSelection(spinnerPositionForMedicineAvailability);

        String looseAvailability;
        if (mCurrentMedicine.isLooseAvailable())
            looseAvailability = "Yes";
        else
            looseAvailability = "No";
        int spinnerPositionForLooseMedicineAvailability = yesNoAdapter.getPosition(looseAvailability);
        mSpinnerLooseMedicineAvailability.setSelection(spinnerPositionForLooseMedicineAvailability);

        if (mCurrentMedicine.getMedicineImageUrl().equals("default"))
            Glide.with(mImageViewMedicineImage.getContext())
                    .load(mArrayListDefaultMedicinePics.get(getIndexFromArrayList(mSpinnerMedicineType.getSelectedItem().toString())).getValue())
                    .into(mImageViewMedicineImage);
        else {
            Glide.with(mImageViewMedicineImage.getContext())
                    .load(mCurrentMedicine.getMedicineImageUrl())
                    .into(mImageViewMedicineImage);
        }

        mButtonAction.setText(R.string.update_medicine);
    }

    private void setupForNewMedicine()
    {

    }

    public void onActionClick()
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

            if (mCurrentMedicine == null) {
                operationObject.AddNewMedicine(mEditTextMedicineName.getText().toString().toUpperCase(),
                        mEditTextMedicineManufacturerName.getText().toString().toUpperCase(),
                        mEditTextComposition.getText().toString().toUpperCase(),
                        selectedCategoryValue, selectedDisplayCategories, selectedTypeValue, mMedicineImageUrl,
                        Integer.valueOf(mEditTextItemsInOnePack.getText().toString()),
                        Double.valueOf(mEditTextPricePerPack.getText().toString()),
                        selectedAvailabilityValue, selectedLooseAvailabilityValue);

                Glide.with(mImageViewMedicineImage.getContext())
                        .load(mArrayListDefaultMedicinePics.get(getIndexFromArrayList(mSpinnerMedicineType.getSelectedItem().toString())).getValue())
                        .into(mImageViewMedicineImage);
            }
            else
            {
                mCurrentMedicine.setMedicineName(mEditTextMedicineName.getText().toString().toUpperCase());
                mCurrentMedicine.setMedicineManufacturerName(mEditTextMedicineManufacturerName.getText().toString().toUpperCase());
                mCurrentMedicine.setMedicineComposition(mEditTextComposition.getText().toString().toUpperCase());
                mCurrentMedicine.setMedicineCategory(selectedCategoryValue);
                mCurrentMedicine.setDisplayCategory(selectedDisplayCategories);
                mCurrentMedicine.setMedicineType(selectedTypeValue);
                mCurrentMedicine.setMedicineImageUrl(mMedicineImageUrl);
                mCurrentMedicine.setNoOfItemsInOnePack(Integer.valueOf(mEditTextItemsInOnePack.getText().toString()));
                mCurrentMedicine.setMedicineAvailability(selectedAvailabilityValue);
                mCurrentMedicine.setLooseAvailable(selectedLooseAvailabilityValue);
                mCurrentMedicine.setPricePerPack(Double.valueOf(mEditTextPricePerPack.getText().toString()));

                HashMap<String, Object> timestampLastUpdate = new HashMap<String, Object>();
                timestampLastUpdate.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);
                mCurrentMedicine.setTimestampLastUpdate(timestampLastUpdate);


                String timeStampInStringFormat = new SimpleDateFormat("MMM dd yyyy HH:mm:ss").format(new Date());
                HashMap<String, String>  tempPriceStack,tempUpdaterStack;

                if (mCurrentMedicine.getUpdaterStack() == null)
                    tempUpdaterStack  = new HashMap<>();
                else
                    tempUpdaterStack = mCurrentMedicine.getUpdaterStack();
                tempUpdaterStack.put(timeStampInStringFormat, mCurrentUser.getEmail());
                mCurrentMedicine.setUpdaterStack(tempUpdaterStack);

                if (mCurrentMedicine.getPriceStack() == null)
                    tempPriceStack = new HashMap<>();
                else
                    tempPriceStack = mCurrentMedicine.getPriceStack();
                tempPriceStack.put(timeStampInStringFormat, mEditTextPricePerPack.getText().toString());
                mCurrentMedicine.setPriceStack(tempPriceStack);


                operationObject.UpdateMedicine(mCurrentMedicine,mCurrentMedicineImageURI);
            }
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

                    categoriesAdapter = new ArrayAdapter<String>(AddOrUpdateMedicineActivity.this, android.R.layout.simple_spinner_item, mArrayListMedicineCategories);
                    categoriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mSpinnerMedicineCategory.setAdapter(categoriesAdapter);

                }

                setMedicineTypes();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void setMedicineTypes()
    {

        mArrayListMedicineTypes.clear();
        for (DefaultKeyValuePair pair :mArrayListDefaultMedicinePics) {
            mArrayListMedicineTypes.add(pair.getKey());
        }

        typesAdapter = new ArrayAdapter<String>(AddOrUpdateMedicineActivity.this,
                android.R.layout.simple_spinner_item, mArrayListMedicineTypes);
        typesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerMedicineType.setAdapter(typesAdapter);

        //set default pic as capsule as spineer's default value is capsule
        Glide.with(mImageViewMedicineImage.getContext())
                .load(mArrayListDefaultMedicinePics.get(getIndexFromArrayList(mSpinnerMedicineType.getSelectedItem().toString())).getValue())
                .into(mImageViewMedicineImage);

        if (mCurrentMedicine != null)
            setupForMedicineUpdate();

       /* Firebase medicineTypesRef = new Firebase(Constants.FIREBASE_URL_SAIRA_All_MEDICINE_TYPES);
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

                    typesAdapter = new ArrayAdapter<String>(AddOrUpdateMedicineActivity.this,
                            android.R.layout.simple_spinner_item, mArrayListMedicineTypes);
                    typesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mSpinnerMedicineType.setAdapter(typesAdapter);

                    //set default pic as capsule as spineer's default value is capsule
                    Glide.with(mImageViewMedicineImage.getContext())
                            .load(mArrayListDefaultMedicinePics.get(getIndexFromArrayList(mSpinnerMedicineType.getSelectedItem().toString())).getValue())
                            .into(mImageViewMedicineImage);

                }

                if (mCurrentMedicine != null)
                    setupForMedicineUpdate();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });*/
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
