package com.digitalrupay.activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.digitalrupay.DigitalRupayApplication;
import com.digitalrupay.R;
import com.digitalrupay.adapters.PagerAdapter;
import com.digitalrupay.datamodels.CategoryDataModel;
import com.digitalrupay.datamodels.ComplaintsDataModel;
import com.digitalrupay.datamodels.ComplaintsListModelDB;
import com.digitalrupay.datamodels.CustomerDataModel;
import com.digitalrupay.datamodels.EmployeeDataModel;
import com.digitalrupay.datamodels.SessionData;
import com.digitalrupay.fragments.ComplaintsFragment;
import com.digitalrupay.fragments.RegisterComplaintFragment;
import com.digitalrupay.interfaces.CommunicationListener;
import com.digitalrupay.network.AsyncRequest;
import com.digitalrupay.network.ComplaintService;
import com.digitalrupay.network.ConnectivityReceiver;
import com.digitalrupay.network.PaymentService;
import com.digitalrupay.network.UpdateComplaint;
import com.digitalrupay.network.WsUrlConstants;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import static com.digitalrupay.network.WsUrlConstants.loginTypes;

/**
 * Created by Santosh on 10/2/2016.
 */

public class ComplaintsActivity extends BaseActivity implements CommunicationListener,  AsyncRequest.OnAsyncRequestComplete,ConnectivityReceiver.ConnectivityReceiverListener {

    private Spinner spnCategories;
    private EditText edtCustomerID, edtComplaintMsg;
    private AsyncRequest asyncRequest = null;
    private String category, employeeId;
    private boolean isCategory;
    private PagerAdapter adapter;
    private ComplaintsFragment complaintsFragment;
    private RegisterComplaintFragment registerComplaintFragment;
    private int serviceRequest;
    private ViewPager viewPager;
    private ArrayList<ComplaintsListModelDB> complaintsDataModelArrayList=new ArrayList<>();
    SQLiteDatabase database;
    ArrayList<CategoryDataModel> categoryDataModelArrayList = new ArrayList<>();
    boolean isConnected;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaints);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Complaints");
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Complaints List"));
        tabLayout.addTab(tabLayout.newTab().setText("Register A Complaint"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager = (ViewPager) findViewById(R.id.pager);
        adapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int tabPos = tab.getPosition();
                viewPager.setCurrentItem(tab.getPosition());
                if (tabPos == 0) {
                    complaintsFragment = (ComplaintsFragment) adapter.getFragment(tabPos);
                } else {
                    registerComplaintFragment = (RegisterComplaintFragment) adapter.getFragment(tabPos);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        EmployeeDataModel employeeDataModel = null;
        if(loginType.equals(loginTypes[4])) {
            employeeDataModel = SessionData.getSessionDataInstance().getApartmentLoginResult();
        }else if(loginType.equals(loginTypes[2])) {
            employeeDataModel= SessionData.getSessionDataInstance().getEmployeeLoginResult();
        }
        employeeId = employeeDataModel.getEmp_id();
        database=openOrCreateDatabase("digitalrupay",MODE_PRIVATE,null);
        Cursor c = database.rawQuery("select * from ComplaintsData where (comp_status='0' OR comp_status='1') ORDER BY 'complaint_id' DESC", null);
        try {
            if (c.moveToFirst()) {
                do {
                    int i1=c.getColumnIndex("first_name");
                    String FirstName=c.getString(i1);

                    int i2=c.getColumnIndex("last_name");
                    String last_name=c.getString(i2);

                    int i3=c.getColumnIndex("addr1");
                    String addr1=c.getString(i3);

                    int i5=c.getColumnIndex("comp_ticketno");
                    String comp_ticketno=c.getString(i5);

                    int i6=c.getColumnIndex("mobile_no");
                    String mobile_no=c.getString(i6);

                    int i8=c.getColumnIndex("comp_cat");
                    String comp_cat=c.getString(i8);

                    int i9=c.getColumnIndex("cust_id");
                    String Cust_id=c.getString(i9);

                    int i10=c.getColumnIndex("comp_remarks");
                    String comp_remarks=c.getString(i10);

                    int i11=c.getColumnIndex("comp_status");
                    String comp_status=c.getString(i11);

                    int i12=c.getColumnIndex("complaint");
                    String complaint=c.getString(i12);

                    int i13=c.getColumnIndex("custom_customer_no");
                    String custom_customer_no=c.getString(i13);

                    int i14=c.getColumnIndex("complaint_id");
                    String Complaint_id=c.getString(i14);

                    ComplaintsListModelDB complaintsListModelDB=new ComplaintsListModelDB();
                    complaintsListModelDB.setFirst_name(FirstName);
                    complaintsListModelDB.setLast_name(last_name);
                    complaintsListModelDB.setAddr1(addr1);
                    complaintsListModelDB.setComp_ticketno(comp_ticketno);
                    complaintsListModelDB.setMobile_no(mobile_no);
                    complaintsListModelDB.setCust_id(Cust_id);
                    complaintsListModelDB.setComp_cat(comp_cat);
                    complaintsListModelDB.setRemarks(comp_remarks);
                    complaintsListModelDB.setStatus(comp_status);
                    complaintsListModelDB.setComplaint(complaint);
                    complaintsListModelDB.setComplaint_id(Complaint_id);
                    complaintsListModelDB.setCustom_customer_no(custom_customer_no);

                    complaintsDataModelArrayList.add(complaintsListModelDB);
                } while (c.moveToNext());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        getCategories();
    }

    @Override
    public void searchCustomer(String customerID) {
        try {
            serviceRequest = 4;
            Log.d("search customer: ", customerID);
            AsyncRequest asyncRequest = new AsyncRequest(this, "GET", new ArrayList<NameValuePair>(), "Fetching Data..");
            asyncRequest.execute(WsUrlConstants.customerDetailsUrl.replace(WsUrlConstants.CUSTOMER_ID, customerID).replace(WsUrlConstants.EMPLOYEE_ID, employeeId));
        }catch (Exception e){

        }
    }

    @Override
    public void getComplaints() {
        try {
            serviceRequest = 1;
            asyncRequest = new AsyncRequest(this, "GET", new ArrayList<NameValuePair>(), "Fetching Data..");
            asyncRequest.execute(WsUrlConstants.complaintsDetailsUrl.replace(WsUrlConstants.EMPLOYEE_ID, employeeId));
        }catch (Exception e){

        }
    }

    void getCategories() {
        try {
            serviceRequest = 2;
            asyncRequest = new AsyncRequest(this, "GET", new ArrayList<NameValuePair>(), "Fetching Categories..");
            asyncRequest.execute(WsUrlConstants.categoriesUrl);
        }catch (Exception  e){
            complaintsFragment = (ComplaintsFragment) adapter.getFragment(0);
            if (complaintsFragment != null) {
                complaintsFragment.setComplaints(complaintsDataModelArrayList);
            }
        }
    }

    @Override
    public void registerComplaint(String customerId, String complaintMsg, String category,String complaintID) {
        try {
            serviceRequest = 3;

            database.execSQL("Insert into sendComplaint values('"+employeeId+"','"+complaintID+"','"+customerId+"','"+complaintMsg+"','"+category+"','false')");
            isConnected = ConnectivityReceiver.isConnected();
            if(isConnected){
                Intent intent=new Intent(ComplaintsActivity.this, ComplaintService.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startService(intent);
            }
            registerComplaintFragment = (RegisterComplaintFragment) adapter.getFragment(1);
            registerComplaintFragment.edtComplaintMsg.setText("");
            Intent complaintSuccessActivity = new Intent(this, ComplaintSuccessActivity.class);
            startActivityForResult(complaintSuccessActivity, 1000);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void asyncResponse(String response) {
        if (serviceRequest == 1) {
                complaintsFragment = (ComplaintsFragment) adapter.getFragment(0);
                if (complaintsFragment != null) {
                  complaintsFragment.setComplaints(complaintsDataModelArrayList);
                }
        } else
        if (serviceRequest == 2) {
//            try {
//                JSONObject customersObj = new JSONObject(response);
//                CategoryDataModel categoryDataModel = null;
//                Iterator<String> keys = customersObj.keys();
//                while (keys.hasNext()) {
//                    String key = keys.next();
//                    if (!key.equalsIgnoreCase("message") && !key.equalsIgnoreCase("text")) {
//                        categoryDataModel = new Gson().fromJson(customersObj.getJSONObject(key).toString(),
//                                new TypeToken<CategoryDataModel>() {
//                                }.getType());
//                        categoryDataModelArrayList.add(categoryDataModel);
//                    }
//                }
//                String message = customersObj.getString("message");
//                String text = customersObj.getString("text");
//                if (message.equalsIgnoreCase("success")) {
//
//                    // Spinner Drop down elements
//                    ArrayList<String> categories = new ArrayList<String>();
//                    for (CategoryDataModel categoryDataModel1 : categoryDataModelArrayList) {
//                        categories.add(categoryDataModel1.getCategory());
//                    }
//                    SessionData.setCategoriesList(categoryDataModelArrayList);
//                    registerComplaintFragment = (RegisterComplaintFragment) adapter.getFragment(1);
//                    if (registerComplaintFragment != null) {
//                        registerComplaintFragment.setCategories(categoryDataModelArrayList);
//                    }
//                } else {
//                    Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
//                }
                getComplaints();
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
        } else if (serviceRequest == 3) {
//            try {
//                JSONObject customersObj = new JSONObject(response);
//                String message = customersObj.getString("message");
//                String text = customersObj.getString("text");
//                if (message.equalsIgnoreCase("success")) {

//                }
//                Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
        } else if (serviceRequest == 4) {
//            try {
//                JSONObject customersObj = new JSONObject(response);
//                String message = customersObj.getString("message");
//                String text = customersObj.getString("text");
//                Iterator<String> keys = customersObj.keys();
//                CustomerDataModel customerDataModel = null;
//                if (message.equalsIgnoreCase("success")) {
//                    while (keys.hasNext()) {
//                        String key = keys.next();
//                        if (!key.equalsIgnoreCase("message") && !key.equalsIgnoreCase("text")) {
//                            customerDataModel = new Gson().fromJson(customersObj.getJSONObject(key).toString(),
//                                    new TypeToken<CustomerDataModel>() {
//                                    }.getType());
//                        }
//                    }
//
//                    if (customerDataModel != null) {
//                        registerComplaintFragment = (RegisterComplaintFragment) adapter.getFragment(1);
//                        registerComplaintFragment.setCustomerData(customerDataModel);
//                    }
//                }
//                Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getComplaints();
        viewPager.setCurrentItem(0);
    }
    @Override
    public void onNetworkConnectionChanged(boolean Connected) {
        isConnected=Connected;
    }

    @Override
    protected void onResume() {
        super.onResume();
        DigitalRupayApplication.getInstance().setConnectivityListener(this);
//        try {
//            if (isConnected) {
//                Intent intent=new Intent(ComplaintsActivity.this, PaymentService.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startService(intent);
//                Intent intent2=new Intent(ComplaintsActivity.this, ComplaintService.class);
//                intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startService(intent2);
//                Intent intent1=new Intent(ComplaintsActivity.this, UpdateComplaint.class);
//                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startService(intent1);
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }
    }
}
