package com.digitalrupay.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.digitalrupay.R;
import com.digitalrupay.datamodels.ComplaintsDataModel;
import com.digitalrupay.datamodels.ComplaintsListModelDB;
import com.digitalrupay.datamodels.EmployeeDataModel;
import com.digitalrupay.datamodels.SessionData;
import com.digitalrupay.network.AsyncRequest;
import com.digitalrupay.network.ComplaintService;
import com.digitalrupay.network.ConnectivityReceiver;
import com.digitalrupay.network.UpdateComplaint;
import com.digitalrupay.network.WsUrlConstants;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Santosh on 10/7/2016.
 */

public class UpdateComplaintsActivity extends BaseActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    public TextView tvCustomerName, tvCategory;
    EditText edtRemarks;
    Spinner spnStatus;
    String selectedStatus, employeeId,comp_status,Complaint_id,setcomp_status;
    boolean isConnected;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_complaint);
        setTitle("Update Complaint", true);
        Complaint_id=getIntent().getExtras().getString("Complaint_id");
        tvCustomerName = (TextView) findViewById(R.id.txtCustomerName);
        tvCategory = (TextView) findViewById(R.id.txtCategory);
        edtRemarks = (EditText) findViewById(R.id.edtRemarks);
        String firstName = "", lastName = "", customerNumber = "",Comp_cat="";
        database=openOrCreateDatabase("digitalrupay",MODE_PRIVATE,null);
        Cursor c = database.rawQuery("select * from ComplaintsData where complaint_id='"+Complaint_id+"'", null);
        try {
            if (c.moveToFirst()) {
                do {
                    int i1=c.getColumnIndex("first_name");
                     firstName=c.getString(i1);

                    int i2=c.getColumnIndex("last_name");
                     lastName=c.getString(i2);

                    int i3=c.getColumnIndex("addr1");
                    String addr1=c.getString(i3);

                    int i5=c.getColumnIndex("comp_ticketno");
                    String comp_ticketno=c.getString(i5);

                    int i6=c.getColumnIndex("mobile_no");
                    String mobile_no=c.getString(i6);

                    int i8=c.getColumnIndex("comp_cat");
                    Comp_cat=c.getString(i8);

                    int i9=c.getColumnIndex("cust_id");
                    String Cust_id=c.getString(i9);

                    int i10=c.getColumnIndex("comp_remarks");
                    String comp_remarks=c.getString(i10);

                    int i11=c.getColumnIndex("comp_status");
                     comp_status=c.getString(i11);

                    int i12=c.getColumnIndex("complaint");
                    String complaint=c.getString(i12);

                    int i13=c.getColumnIndex("custom_customer_no");
                    customerNumber=c.getString(i13);
                } while (c.moveToNext());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        tvCustomerName.setText(firstName + " " + lastName + " - (" +  customerNumber +")");
        tvCategory.setText("Category: " + Comp_cat);

        spnStatus = (Spinner) findViewById(R.id.spnStatus);
        spnStatus.setSelection(Integer.parseInt(comp_status));
        spnStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedStatus = (String) parent.getItemAtPosition(position);
                selectedStatus = position + "";
                setcomp_status=position + "";
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        EmployeeDataModel employeeDataModel;
        if(SessionData.getSessionDataInstance().getApartmentLoginResult()!= null) {
            employeeDataModel = SessionData.getSessionDataInstance().getApartmentLoginResult();
        }else {
            employeeDataModel= SessionData.getSessionDataInstance().getEmployeeLoginResult();
        }
        employeeId = employeeDataModel.getEmp_id();
    }

    public void updateComplaint(View view) {
        isConnected = ConnectivityReceiver.isConnected();
        String remarks = edtRemarks.getText().toString().trim().replace(" ","%20");
        String saveremarks = edtRemarks.getText().toString();
        database.execSQL("insert into updateComplaintUrl values('"+Complaint_id+"','"+employeeId+"','"+remarks+"','"+selectedStatus+"','false')");
        database.execSQL("Update ComplaintsData set comp_status='"+setcomp_status+"',comp_remarks='"+saveremarks+"' where complaint_id='"+Complaint_id+"'");
        if(isConnected){
            Intent intent=new Intent(UpdateComplaintsActivity.this, ComplaintService.class);
            startService(intent);
        }
        Intent updateComplaint = new Intent();
        updateComplaint.putExtra(WsUrlConstants.RESULT, true);
        setResult(WsUrlConstants.COMPLAINTS_CODE, updateComplaint);
        finish();
    }

//    @Override
//    public void asyncResponse(String response) {
//        try {
//            JSONObject customersObj = new JSONObject(response);
//            String message = customersObj.getString("message");
//            String text = customersObj.getString("text");
//            if (message.equalsIgnoreCase("success")) {
//                Intent updateComplaint = new Intent();
//                updateComplaint.putExtra(WsUrlConstants.RESULT, true);
//                setResult(WsUrlConstants.COMPLAINTS_CODE, updateComplaint);
//                finish();
//            }
//            Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
    @Override
    public void onNetworkConnectionChanged(boolean Connected) {
        isConnected=Connected;
    }
}
