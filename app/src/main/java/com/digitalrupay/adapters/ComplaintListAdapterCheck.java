package com.digitalrupay.adapters;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.digitalrupay.R;
import com.digitalrupay.datamodels.ComplaintsListModelDB;

import java.util.ArrayList;

/**
 * Created by sridhar on 2/6/2017.
 */

public class ComplaintListAdapterCheck extends BaseAdapter {
    private Context context;
    private ArrayList<ComplaintsListModelDB> complaintsDataModelArrayList;
    public ComplaintListAdapterCheck(Context activity, ArrayList<ComplaintsListModelDB> complaintsDataModelArrayList) {
        this.context = activity;
        this.complaintsDataModelArrayList = complaintsDataModelArrayList;
    }

    @Override
    public int getCount() {
        return complaintsDataModelArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return complaintsDataModelArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.complaints_item, parent, false);
        TextView tvCustomerName = (TextView) convertView.findViewById(R.id.txtCustomerName);
        TextView tvCategory = (TextView) convertView.findViewById(R.id.txtCategory);
        TextView tvComplaintMsg = (TextView)convertView.findViewById(R.id.txtComplaintMsg);
        TextView tvStatus = (TextView) convertView.findViewById(R.id.txtStatus);
        TextView tvRemarks = (TextView) convertView.findViewById(R.id.txtRemarks);
        ComplaintsListModelDB complaintsDataModel=complaintsDataModelArrayList.get(position);
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
        tvRemarks.setText("Remarks: " + complaintsDataModel.getRemarks());
        convertView.setTag(complaintsDataModel.getComplaint_id());
        return convertView;
    }
}
