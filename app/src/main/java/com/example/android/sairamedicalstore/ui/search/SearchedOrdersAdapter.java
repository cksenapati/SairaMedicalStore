package com.example.android.sairamedicalstore.ui.search;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.example.android.sairamedicalstore.R;
import com.example.android.sairamedicalstore.SairaMedicalStoreApplication;
import com.example.android.sairamedicalstore.models.DefaultKeyValuePair;
import com.example.android.sairamedicalstore.models.Order;
import com.example.android.sairamedicalstore.utils.Constants;
import com.example.android.sairamedicalstore.utils.Utils;
import com.firebase.client.Query;
import com.firebase.ui.FirebaseListAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;


/**
 * Created by chandan on 17-08-2017.
 */

public class SearchedOrdersAdapter extends FirebaseListAdapter<Order> {

    ArrayList<DefaultKeyValuePair> mArrayListDefaultMedicinePics;

    public SearchedOrdersAdapter(Activity activity, Class<Order> modelClass, int modelLayout, Query ref) {
        super(activity,modelClass, modelLayout, ref);
        this.mActivity = activity;
        mArrayListDefaultMedicinePics = ((SairaMedicalStoreApplication) mActivity.getApplication()).getArrayListDefaultMedicinePics();
    }

    @Override
    protected void populateView(View view, Order order) {
        TextView textViewOrderId = (TextView) view.findViewById(R.id.text_view_order_id);
        textViewOrderId.setText("Order Id : "+order.getOrderId());

        TextView textViewOrderPlacedOn = (TextView) view.findViewById(R.id.text_view_order_placed_on);
        textViewOrderPlacedOn.setText("Order Date : "+Utils.SIMPLE_DATE_ONLY_FORMAT.format(
                new Date(order.getTimestampOrderPlacedLong())));

        TextView textViewOrderStatus = (TextView) view.findViewById(R.id.text_view_order_status);
        textViewOrderStatus.setText(Utils.toLowerCaseExceptFirstLetter(order.getOrderStatus()));
        textViewOrderStatus.setTextColor(getOrderStatusTextColor(order.getOrderStatus()));

        TextView textViewOrderTotalPrice = (TextView) view.findViewById(R.id.text_view_order_total_price);

        double totalPayable = 0.0 ;
        for (Map.Entry<String, Double> eachEntry : order.getOrderPricingDetails().entrySet())
            totalPayable += eachEntry.getValue();
        totalPayable = (double) Math.round( totalPayable *100) / 100;

        textViewOrderTotalPrice.setText("RS. "+Double.toString(totalPayable));

    }

    public int getOrderStatusTextColor(String status)
    {
        int returnColorCode;

        if(status.equals(Constants.ORDER_STATUS_PLACED))
            returnColorCode = mActivity.getResources().getColor(R.color.success_message_text_color);
        else if(status.equals(Constants.ORDER_STATUS_DELIVERED))
            returnColorCode = mActivity.getResources().getColor(R.color.success_message_text_color);
        else if(status.equals(Constants.ORDER_STATUS_CANCELED))
            returnColorCode = mActivity.getResources().getColor(R.color.fail_message_text_color);
        else if(status.equals(Constants.ORDER_STATUS_RETURNED))
            returnColorCode = mActivity.getResources().getColor(R.color.fail_message_text_color);
        else if(status.equals(Constants.ORDER_STATUS_REQUESTED_FOR_RETURN))
            returnColorCode = mActivity.getResources().getColor(R.color.under_process_message_text_color);
        else if(status.equals(Constants.ORDER_STATUS_DISPATCHED))
            returnColorCode = mActivity.getResources().getColor(R.color.under_process_message_text_color);
        else
            returnColorCode = mActivity.getResources().getColor(R.color.under_process_message_text_color);

        return returnColorCode;

    }

}
