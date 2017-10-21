package com.example.android.sairamedicalstore.ui;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import java.util.HashMap;
import java.util.Map;

import static android.R.attr.dialogTitle;
import static com.example.android.sairamedicalstore.ui.CartActivity.mCurrentCart;

public class AddNewPrescriptionActivity extends AppCompatActivity {

    public static ImageView mImageViewPrescriptionPage;
    LinearLayout mLinearLayoutAddNewPage,mLinearLayoutForRecyclerView;
    TextView mTextViewSave;

    private static final int RC_PHOTO_PICKER =  1;
    User mCurrentUser;
    String mCurrentPrescriptionId;
    HashMap<String,String> mHashMapPrescriptionPages;
    public static Dialog saveDataDialog;
    RecyclerView.Adapter recyclerViewAdapter;
    RecyclerView recyclerView;


    private FirebaseStorage mFirebaseStorage;
    private StorageReference mFirebaseCurrentPrescriptionStorageRef;
    Firebase mFirebaseCurrentUserAllPrescriptionsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_prescription);

        mCurrentUser = ((SairaMedicalStoreApplication) this.getApplication()).getCurrentUser();

        initialization();

        mLinearLayoutAddNewPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent imagePickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
                imagePickerIntent.setType("image/*");
                imagePickerIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(imagePickerIntent, "Complete action using"), RC_PHOTO_PICKER);
            }
        });

        mTextViewSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mHashMapPrescriptionPages != null && mHashMapPrescriptionPages.size() > 0)
                    openSaveDataDialog();

            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_PHOTO_PICKER)
        {
            if(resultCode == RESULT_OK){

                StorageReference selectedPhotoRef = mFirebaseCurrentPrescriptionStorageRef.child(data.getData().getLastPathSegment());
                UploadTask uploadTask = selectedPhotoRef.putFile(data.getData());
                uploadTask.addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // When the image has successfully uploaded, we get its download URL
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        try {
                            addNewPageToPrescription(downloadUrl.toString());
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

    public void addNewPageToPrescription(String imageDownloadURI)
    {
        Glide.with(mImageViewPrescriptionPage.getContext())
                .load(imageDownloadURI)
                .into(mImageViewPrescriptionPage);

        int countNoOfPages = 0;
        boolean isPageAlreadyExists = false;
        for (final Map.Entry<String, String> eachPage : mHashMapPrescriptionPages.entrySet())
        {
            if(eachPage.getValue().equals(imageDownloadURI))
            {
                isPageAlreadyExists = true;
                break;
            }
            countNoOfPages++;
        }

        if(!isPageAlreadyExists)
        {
            mHashMapPrescriptionPages.put("page"+ (countNoOfPages + 1),imageDownloadURI);
            setRecyclerView();
        }

    }

    private void initialization()
    {
        mImageViewPrescriptionPage = (ImageView) findViewById(R.id.image_view_prescription_page);
        mLinearLayoutAddNewPage = (LinearLayout) findViewById(R.id.linear_layout_add_new_page);
        mLinearLayoutForRecyclerView = (LinearLayout) findViewById(R.id.linear_layout_for_recycler_view);
        mTextViewSave = (TextView)  findViewById(R.id.text_view_save_new_prescription);

        mHashMapPrescriptionPages = new HashMap<>();

        mFirebaseStorage = FirebaseStorage.getInstance();
        mFirebaseCurrentUserAllPrescriptionsRef = new Firebase(Constants.FIREBASE_URL_SAIRA_All_PRESCRIPTIONS).child(Utils.encodeEmail(mCurrentUser.getEmail()));
        mCurrentPrescriptionId = mFirebaseCurrentUserAllPrescriptionsRef.push().getKey();
        mFirebaseCurrentPrescriptionStorageRef = mFirebaseStorage.getReference().child(Constants.FIREBASE_LOCATION_All_PRESCRIPTIONS).child(Utils.encodeEmail(mCurrentUser.getEmail())).child(mCurrentPrescriptionId);

    }

    public void setRecyclerView()
    {
        recyclerView = new RecyclerView(AddNewPrescriptionActivity.this);
        recyclerView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));

        RecyclerView.LayoutManager layoutManager  = new LinearLayoutManager(AddNewPrescriptionActivity.this,LinearLayoutManager.HORIZONTAL,false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerViewAdapter = new AllPrescriptionPagesAdapter(mHashMapPrescriptionPages,AddNewPrescriptionActivity.this);
        recyclerView.setAdapter(recyclerViewAdapter);

        mLinearLayoutForRecyclerView.addView(recyclerView);

    }


    public void openSaveDataDialog() {

        if(saveDataDialog != null) {
            saveDataDialog = null;
        }

        saveDataDialog = new Dialog(this);
        saveDataDialog.setContentView(R.layout.dialog_add_new_item);

        final EditText editTextName = (EditText) saveDataDialog.findViewById(R.id.edit_text_name);
        TextView textViewDone = (TextView) saveDataDialog.findViewById(R.id.text_view_done);
        TextView textViewDiscard = (TextView) saveDataDialog.findViewById(R.id.text_view_discard);
        final TextView textViewErrorMsg = (TextView) saveDataDialog.findViewById(R.id.text_view_error_message);

        saveDataDialog.show();

        textViewDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextName.getText().toString().trim().length() > 0) {
                    String prescriptionName = editTextName.getText().toString().toUpperCase();
                    savePrescription(prescriptionName);
                } else {
                    textViewErrorMsg.setText("Provide Prescription Name");
                }
            }
        });

        textViewDiscard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDataDialog.dismiss();
            }
        });
    }

    public void savePrescription(String prescriptionName)
    {
        PrescriptionOperations obj = new PrescriptionOperations(this);
        obj.addNewPrescription(mCurrentPrescriptionId,mHashMapPrescriptionPages,prescriptionName);

    }




}
