package com.digitalrupay.adapters.view_holders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.digitalrupay.R;
import com.digitalrupay.adapters.ComplaintsListAdapter;
import com.digitalrupay.datamodels.ComplaintsDataModel;
import com.digitalrupay.datamodels.ComplaintsListModelDB;

/**
 * Created by Santosh on 10/1/2016.
 */

public class ComplaintsListHolder extends RecyclerView.ViewHolder {

    public TextView tvCustomerName, tvCategory, tvComplaintMsg, tvStatus, tvRemarks;
    ImageView ivArrow;
    public RelativeLayout relativeLayout;
    View view;
    Context context;
    public ComplaintsListHolder(Context con, View v, View.OnClickListener listener) {
        super(v);
        this.view = v;
        tvCustomerName = (TextView) v.findViewById(R.id.txtCustomerName);
        tvCategory = (TextView) v.findViewById(R.id.txtCategory);
        tvComplaintMsg = (TextView) v.findViewById(R.id.txtComplaintMsg);
        tvStatus = (TextView) v.findViewById(R.id.txtStatus);
        tvRemarks = (TextView) v.findViewById(R.id.txtRemarks);
        relativeLayout=(RelativeLayout)v.findViewById(R.id.complaint_item);
        context=con;
    }

    public void setComplaintsData(final ComplaintsListModelDB complaintsDataModel) {
        view.setTag(complaintsDataModel);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,complaintsDataModel.getComplaint_id(),Toast.LENGTH_LONG).show();
            }
        });
        String firstName = "", lastName = "", customerNumber = "";
        if (complaintsDataModel.getFirst_name() != null) {
            firstName = complaintsDataModel.getFirst_name().trim();
        }
        if (complaintsDataModel.getLast_name() != null) {
            lastName = complaintsDataModel.getLast_name().trim();
        }
        if (complaintsDataModel.getCustom_customer_no() != null) {
            customerNumber = complaintsDataModel.getCustom_customer_no();
        }
        tvCustomerName.setText(firstName + " " + lastName + " - (" + customerNumber + ")"+"\nTicket No : "+complaintsDataModel.getComp_ticketno());
        tvCategory.setText("Category: " + complaintsDataModel.getComp_cat());
        tvComplaintMsg.setText("Complaint Message: " + complaintsDataModel.getComplaint());
        String status = complaintsDataModel.getStatus();
        if (status != null) {
            if (status.equalsIgnoreCase("1")) {
                tvStatus.setText("Status: Processing");
            } else if (status.equalsIgnoreCase("0")) {
                tvStatus.setText("Status: Pending");
            } else {
                tvStatus.setText("Status: Completed");
            }
        }
        tvRemarks.setText("Remarks: " + complaintsDataModel.getComp_remarks());
    }
}
