package com.example.android.sairamedicalstore.ui.order;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.android.sairamedicalstore.R;
import com.example.android.sairamedicalstore.models.Prescription;
import com.example.android.sairamedicalstore.models.PrescriptionPage;
import com.example.android.sairamedicalstore.ui.MainActivity;
import com.example.android.sairamedicalstore.ui.mediaFileUpload.ViewImageSlideshowActivity;
import com.example.android.sairamedicalstore.ui.prescription.MyPrescriptionsActivity;
import com.example.android.sairamedicalstore.utils.Constants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by chandan on 12-08-2017.
 */

class PrescriptionDetailsAdapter extends RecyclerView.Adapter<PrescriptionDetailsAdapter.ViewHolder> {

    private ArrayList<Prescription> mArrayListAllAvailablePrescriptions;
    private Activity mActivity;
    int currentPageCount;

    public PrescriptionDetailsAdapter(ArrayList<Prescription> data, Activity activity) {
        this.mArrayListAllAvailablePrescriptions = data;
        this.mActivity = activity;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public LinearLayout mLinearLayoutRecycleItem;
        public ImageView mImageViewPrescription;
        public TextView mTextViewPrescriptionName;
        public ViewHolder(View itemView) {
            super(itemView);
            mLinearLayoutRecycleItem = (LinearLayout) itemView.findViewById(R.id.linear_layout_recycle_item);
            mImageViewPrescription = (ImageView) itemView.findViewById(R.id.image_view_prescription);
            mTextViewPrescriptionName = (TextView) itemView.findViewById(R.id.text_view_prescription_name);
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public PrescriptionDetailsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                    int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycle_item_for_prescription, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(PrescriptionDetailsAdapter.ViewHolder holder, final int position) {

        final ArrayList<PrescriptionPage> activePrescriptionPages = new ArrayList<>();
        final ArrayList<String> activePrescriptionPageImageUrls = new ArrayList<>();

        for (Map.Entry<String,PrescriptionPage> eachPage :mArrayListAllAvailablePrescriptions.get(position).getPrescriptionPages().entrySet()) {
            if (eachPage.getValue().isActive()) {
                activePrescriptionPages.add(eachPage.getValue());
                activePrescriptionPageImageUrls.add(eachPage.getValue().getImageUri());
            }
        }

        if (activePrescriptionPages.size() <= 0)
            return;

        Glide.with(holder.mImageViewPrescription.getContext())
                .load(activePrescriptionPages.get(0).getImageUri())
                .into(holder.mImageViewPrescription);
        holder.mTextViewPrescriptionName.setText(mArrayListAllAvailablePrescriptions.get(position).getPrescriptionName());

        holder.mImageViewPrescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //openImageSlideShowDialog(activePrescriptionPages);

                Intent intentToViewImageSlideshow = new Intent(mActivity, ViewImageSlideshowActivity.class);
                intentToViewImageSlideshow.putExtra("arrayListImageUrlsForSlideShow",activePrescriptionPageImageUrls);
                intentToViewImageSlideshow.putExtra("slideshowName",mArrayListAllAvailablePrescriptions.get(position).getPrescriptionName());
                mActivity.startActivity(intentToViewImageSlideshow);

            }
        });


    }

    @Override
    public int getItemCount() {
        return mArrayListAllAvailablePrescriptions.size();
    }

    public void openImageSlideShowDialog(final ArrayList<PrescriptionPage> activePrescriptionPages)
    {
        final Dialog viewImageDialog = new Dialog(mActivity);
        viewImageDialog.setContentView(R.layout.dialog_view_image);

        final ImageView imageViewDialogImage = (ImageView) viewImageDialog.findViewById(R.id.image_view_dialog_image);
        ImageView imageViewCloseDialog = (ImageView) viewImageDialog.findViewById(R.id.image_view_close_dialog);

        Glide.with(imageViewDialogImage.getContext())
                .load(activePrescriptionPages.get(0).getImageUri())
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        viewImageDialog.show();
                        return false;
                    }
                })
                .into(imageViewDialogImage);


        imageViewCloseDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewImageDialog.dismiss();
            }
        });

    }



}
