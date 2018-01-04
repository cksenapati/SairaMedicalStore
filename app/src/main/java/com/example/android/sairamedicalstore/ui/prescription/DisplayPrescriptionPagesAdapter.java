package com.example.android.sairamedicalstore.ui.prescription;

import android.app.Activity;
import android.app.Dialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.android.sairamedicalstore.R;

import java.util.ArrayList;


/**
 * Created by chandan on 12-08-2017.
 */

class DisplayPrescriptionPagesAdapter extends RecyclerView.Adapter<DisplayPrescriptionPagesAdapter.ViewHolder> {

    private ArrayList<String> mArrayListAllPagesOfCurrentPrescription;
    private boolean isPrescriptionActive;
    private Activity mActivity;

    public DisplayPrescriptionPagesAdapter(ArrayList<String> data,boolean isPrescriptionActive, Activity activity) {
        this.mArrayListAllPagesOfCurrentPrescription = data;
        this.isPrescriptionActive = isPrescriptionActive;
        this.mActivity = activity;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView mImageViewSinglePrescriptionPage;
        public ImageView mImageViewRemovePage;
        public RelativeLayout mRelativeLayoutSinglePrescriptionPage;

        public ViewHolder(View itemView) {
            super(itemView);
            mRelativeLayoutSinglePrescriptionPage = (RelativeLayout) itemView.findViewById(R.id.relative_layout_single_prescription_page);
            mImageViewSinglePrescriptionPage = (ImageView) itemView.findViewById(R.id.image_view_single_prescription_page);
            mImageViewRemovePage = (ImageView) itemView.findViewById(R.id.image_view_remove_page);
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public DisplayPrescriptionPagesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                         int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycle_prescription_pages, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final DisplayPrescriptionPagesAdapter.ViewHolder holder, final int position) {
        Glide.with(holder.mImageViewSinglePrescriptionPage.getContext())
                .load(mArrayListAllPagesOfCurrentPrescription.get(position))
                .into(holder.mImageViewSinglePrescriptionPage);

        if (isPrescriptionActive)
            holder.mImageViewRemovePage.setVisibility(View.VISIBLE);
        else
            holder.mImageViewRemovePage.setVisibility(View.GONE);

        holder.mImageViewSinglePrescriptionPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageDialog(mArrayListAllPagesOfCurrentPrescription.get(position));
            }
        });

        holder.mImageViewRemovePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //holder.mImageViewSinglePrescriptionPage.setVisibility(View.GONE);
                mArrayListAllPagesOfCurrentPrescription.remove(position);
                notifyItemRemoved(position);
                Toast.makeText(mActivity,"Page Removed",  Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public int getItemCount() {
        return mArrayListAllPagesOfCurrentPrescription.size();
    }

    public void openImageDialog(String imageUri)
    {
        final Dialog viewImageDialog = new Dialog(mActivity);
        viewImageDialog.setContentView(R.layout.dialog_view_image);

        ImageView imageViewDialogImage = (ImageView) viewImageDialog.findViewById(R.id.image_view_dialog_image);
        ImageView imageViewCloseDialog = (ImageView) viewImageDialog.findViewById(R.id.image_view_close_dialog);

        Glide.with(imageViewDialogImage.getContext())
                .load(imageUri)
                .into(imageViewDialogImage);

        viewImageDialog.show();

        imageViewCloseDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewImageDialog.dismiss();
            }
        });
    }

}
