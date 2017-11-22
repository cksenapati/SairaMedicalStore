package com.example.android.sairamedicalstore.ui.prescription;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.sairamedicalstore.R;
import com.example.android.sairamedicalstore.models.Prescription;

import java.util.ArrayList;

import static com.example.android.sairamedicalstore.ui.prescription.PrescriptionsActivity.arrayListSelectedPrescriptionIds;
import static com.example.android.sairamedicalstore.ui.prescription.PrescriptionsActivity.mCurrentPageNumber;
import static com.example.android.sairamedicalstore.ui.prescription.PrescriptionsActivity.mCurrentPrescription;
import static com.example.android.sairamedicalstore.ui.prescription.PrescriptionsActivity.mImageViewPrescription;
import static com.example.android.sairamedicalstore.ui.prescription.PrescriptionsActivity.mImageViewPrescriptionSelectionOption;
import static com.example.android.sairamedicalstore.ui.prescription.PrescriptionsActivity.mTextViewPrescriptionName;

/**
 * Created by chandan on 12-08-2017.
 */

class AllAvailablePrescriptionsAdapter extends RecyclerView.Adapter<AllAvailablePrescriptionsAdapter.ViewHolder> {

    private ArrayList<Prescription> mArrayListAllAvailablePrescriptions;
    private Activity mActivity;

    public AllAvailablePrescriptionsAdapter(ArrayList<Prescription> data, Activity activity) {
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
    public AllAvailablePrescriptionsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                          int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycle_item_for_prescription, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(AllAvailablePrescriptionsAdapter.ViewHolder holder, final int position) {

        Glide.with(holder.mImageViewPrescription.getContext())
                .load(mArrayListAllAvailablePrescriptions.get(position).getPrescriptionPages().get("page1"))
                .into(holder.mImageViewPrescription);
        holder.mTextViewPrescriptionName.setText(mArrayListAllAvailablePrescriptions.get(position).getPrescriptionName());

       holder.mLinearLayoutRecycleItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentPrescription = mArrayListAllAvailablePrescriptions.get(position);
                mCurrentPageNumber = 1;
                Glide.with(mImageViewPrescription.getContext())
                        .load(mCurrentPrescription.getPrescriptionPages().get("page"+mCurrentPageNumber))
                        .into(mImageViewPrescription);
                mTextViewPrescriptionName.setText(mCurrentPrescription.getPrescriptionName());
                checkWhetherPrescriptionIsSelected();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mArrayListAllAvailablePrescriptions.size();
    }

    public void checkWhetherPrescriptionIsSelected()
    {
        for (String eachPrescriptionId:arrayListSelectedPrescriptionIds) {
           if(eachPrescriptionId.equals(mCurrentPrescription.getPrescriptionId()))
           {
               mImageViewPrescriptionSelectionOption.setImageResource(R.drawable.ic_checked_48);
               return;
           }
        }

        mImageViewPrescriptionSelectionOption.setImageResource(R.drawable.ic_check_48);

    }




}
