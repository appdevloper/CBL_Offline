package com.digitalrupay.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.digitalrupay.R;
import com.digitalrupay.activities.UpdateComplaintsActivity;
import com.digitalrupay.adapters.ComplaintListAdapterCheck;
import com.digitalrupay.adapters.ComplaintsListAdapter;
import com.digitalrupay.datamodels.ComplaintsDataModel;
import com.digitalrupay.datamodels.ComplaintsListModelDB;
import com.digitalrupay.interfaces.CommunicationListener;
import com.digitalrupay.network.WsUrlConstants;

import java.util.ArrayList;

/**
 * Created by Santosh on 10/1/2016.
 */

public class ComplaintsFragment extends Fragment{

    ListView rvComplaints;
    TextView errMsg;
    FrameLayout flAddComplaint;
    CommunicationListener communicationListener;
    Intent complaintsActivity;
    ArrayList<ComplaintsListModelDB> complaintsData;
    public ComplaintListAdapterCheck complaintsListAdapter;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        communicationListener = (CommunicationListener) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_complaints, container, false);
        rvComplaints = (ListView) view.findViewById(R.id.rvComplaints);
        errMsg = (TextView) view.findViewById(R.id.errMsg);
        flAddComplaint = (FrameLayout) view.findViewById(R.id.fl_add_complaint);
        rvComplaints.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String Complaint_id=view.getTag().toString();
                complaintsActivity = new Intent(getActivity(), UpdateComplaintsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("Complaint_id",Complaint_id);
                complaintsActivity.putExtras(bundle);
                startActivityForResult(complaintsActivity, WsUrlConstants.COMPLAINTS_CODE);
            }
        });
        return view;
    }

    public void setComplaints(ArrayList<ComplaintsListModelDB> complaintsDataModelArrayList) {
        if (complaintsDataModelArrayList != null && complaintsDataModelArrayList.size() > 0) {
            complaintsData=complaintsDataModelArrayList;
            complaintsListAdapter = new ComplaintListAdapterCheck(getActivity(), complaintsData);
            rvComplaints.setAdapter(complaintsListAdapter);
        } else {
            errMsg.setVisibility(View.VISIBLE);
//            errMsg.setText(text);
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == WsUrlConstants.COMPLAINTS_CODE) {
           complaintsListAdapter.notifyDataSetChanged();
            if (data.getBooleanExtra(WsUrlConstants.RESULT, false)) {
                communicationListener.getComplaints();
            }
        }
    }
}
