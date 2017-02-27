package com.digitalrupay.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.digitalrupay.R;
import com.digitalrupay.adapters.view_holders.ComplaintsListHolder;
import com.digitalrupay.datamodels.ComplaintsDataModel;
import com.digitalrupay.datamodels.ComplaintsListModelDB;

import java.util.ArrayList;

/**
 * Created by Santosh on 10/1/2016.
 */

public class ComplaintsListAdapter extends RecyclerView.Adapter<ComplaintsListHolder> {

    private Context context;
    private ArrayList<ComplaintsListModelDB> complaintsDataModelArrayList;
    private View.OnClickListener listener;
    private LayoutInflater mInflator;

    public ComplaintsListAdapter(Context context, ArrayList<ComplaintsListModelDB> complaintsDataModelArrayList) {
        this.context = context;
        this.complaintsDataModelArrayList = complaintsDataModelArrayList;
        mInflator = LayoutInflater.from(context);
    }

//    public View.OnClickListener getListener() {
//        return listener;
//    }
//
//    public void setListener(View.OnClickListener listener) {
//        this.listener = listener;
//    }

    @Override
    public ComplaintsListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View parentAnimalView = mInflator.inflate(R.layout.complaints_item, parent, false);
        return new ComplaintsListHolder(context,parentAnimalView, listener);
    }

    @Override
    public void onBindViewHolder(ComplaintsListHolder holder, int position) {
        ComplaintsListHolder complaintsListHolder = (ComplaintsListHolder) holder;
        complaintsListHolder.setComplaintsData(complaintsDataModelArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return complaintsDataModelArrayList.size();
    }
}
