package com.digitalrupay.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.signature.StringSignature;
import com.digitalrupay.DigitalRupayApplication;
import com.digitalrupay.R;
import com.digitalrupay.adapters.PagerAdapter;
import com.digitalrupay.datamodels.EmployeeDataModel;
import com.digitalrupay.datamodels.SessionData;
import com.digitalrupay.fragments.ComplaintsFragment;
import com.digitalrupay.fragments.CustomersFragment;
import com.digitalrupay.interfaces.CommunicationListener;
import com.digitalrupay.network.AsyncRequest;
import com.digitalrupay.network.ConnectivityReceiver;
import com.digitalrupay.network.WsUrlConstants;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.digitalrupay.network.WsUrlConstants.loginTypes;

/**
 * Created by Santosh on 10/1/2016.
 */

public class CableBillingActivity extends BaseActivity implements CommunicationListener{

    AsyncRequest asyncRequest;
    boolean isCustomer;
    String employeeId;
    ImageView ivLogo;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cable_billing);
        if(loginType.equals(loginTypes[2])){
            setTitle("Cable Billing System", false);
        }else if(loginType.equals(loginTypes[4])){
            setTitle("Gated Community Billings", false);
        }
        ivLogo = (ImageView) findViewById(R.id.logo);
        EmployeeDataModel employeeDataModel=null;
        if(loginType.equals(loginTypes[4])) {
            employeeDataModel = SessionData.getSessionDataInstance().getApartmentLoginResult();
        }else if(loginType.equals(loginTypes[2])) {
            employeeDataModel= SessionData.getSessionDataInstance().getEmployeeLoginResult();
        }
        employeeId = employeeDataModel.getEmp_id();
        TextView tvEmpName = (TextView) findViewById(R.id.tvEmpName);
        tvEmpName.setText(employeeDataModel.getEmp_first_name() + " " + employeeDataModel.getEmp_last_name());
        TextView tvEmpMobile = (TextView) findViewById(R.id.tvEmpMobile);
        tvEmpMobile.setText("(" + employeeDataModel.getEmp_mobile_no() + ")");
    }
    @Override
    public void searchCustomer(String customerID) {
        isCustomer = true;
        asyncRequest = new AsyncRequest(this, "GET", new ArrayList<NameValuePair>(), "Fetching Data..");
        asyncRequest.execute(WsUrlConstants.customerDetailsUrl.replace(WsUrlConstants.CUSTOMER_ID, customerID).replace(WsUrlConstants.EMPLOYEE_ID, employeeId));
    }

    @Override
    public void getComplaints() {
        isCustomer = false;
        asyncRequest = new AsyncRequest(this, "GET", new ArrayList<NameValuePair>(), "Fetching Data..");
        asyncRequest.execute(WsUrlConstants.complaintsDetailsUrl.replace(WsUrlConstants.EMPLOYEE_ID, employeeId));
    }

    @Override
    public void registerComplaint(String customerId, String complaintMsg, String category,String customerNumber) {}

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == WsUrlConstants.COMPLAINTS_CODE) {
                if (data.getBooleanExtra(WsUrlConstants.RESULT, false)) {
                    getComplaints();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void navigateToCollections(View view) {
        Intent collections = new Intent(this, CollectionsActivity.class);
        collections.putExtra(WsUrlConstants.LOGIN_TYPE, loginType);
        startActivity(collections);
    }

    public void navigateToComplaints(View view) {
        SessionData.setComplaintsDataModelArrayList(null);
        SessionData.setCategoriesList(null);
        Intent complaints = new Intent(this, ComplaintsActivity.class);
        complaints.putExtra(WsUrlConstants.LOGIN_TYPE, loginType);
        startActivity(complaints);
    }

    public void navigateToChangeMobileNumber(View view) {
        Intent changeMobile = new Intent(this, ChangeMobileNumberActivity.class);
        changeMobile.putExtra(WsUrlConstants.LOGIN_TYPE, loginType);
        startActivity(changeMobile);
    }
    public void navigateToBluetoothdevicessettings(View view) {
        Intent changeMobile = new Intent(this, Menu_2ST_Activity.class);
        changeMobile.putExtra(WsUrlConstants.LOGIN_TYPE, loginType);
        startActivity(changeMobile);
    }

}
