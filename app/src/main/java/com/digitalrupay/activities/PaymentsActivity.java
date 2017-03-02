package com.digitalrupay.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.digitalrupay.DigitalRupayApplication;
import com.digitalrupay.R;
import com.digitalrupay.adapters.PaymentsListAdapter;
import com.digitalrupay.adapters.view_holders.PaymentsListHolder;
import com.digitalrupay.datamodels.CustomerDataModel;
import com.digitalrupay.datamodels.CustomerPaymentSuccessModel;
import com.digitalrupay.datamodels.EmpCustomerPaymentModel;
import com.digitalrupay.datamodels.EmployeeDataModel;
import com.digitalrupay.datamodels.MaintainceModel;
import com.digitalrupay.datamodels.PaymentSuccessModel;
import com.digitalrupay.datamodels.SessionData;
import com.digitalrupay.network.AsyncRequest;
import com.digitalrupay.network.AsyncRequestEx;
import com.digitalrupay.network.ComplaintService;
import com.digitalrupay.network.ConnectivityReceiver;
import com.digitalrupay.network.PaymentService;
import com.digitalrupay.network.UpdateComplaint;
import com.digitalrupay.network.WsUrlConstants;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.http.NameValuePair;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Random;

import static com.digitalrupay.network.WsUrlConstants.loginTypes;

/**
 * Created by Santosh on 10/6/2016.
 */

public class PaymentsActivity extends BaseActivity implements AsyncRequest.OnAsyncRequestComplete,AdapterView.OnItemSelectedListener,AsyncRequestEx.OnAsyncRequestComplete,ConnectivityReceiver.ConnectivityReceiverListener {
    TextView tvError;
    EditText searchCustomer;
    SQLiteDatabase database;
    int serviceRequest;
    String employeeId;
    ImageView ivSearch;
    PaymentsListAdapter complaintsListAdapter;
    RecyclerView PaymentList;
    ArrayList<EmpCustomerPaymentModel> empCustomerPaymentModels=new ArrayList<>();
    boolean isConnected;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_customers);
        setTitle("Customer Payment", true);
        PaymentList = (RecyclerView)findViewById(R.id.PaymentList);
        tvError = (TextView) findViewById(R.id.errMsg);

        ivSearch = (ImageView) findViewById(R.id.imgSearch);
        ivSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PaymentsListHolder.strings.clear();
                empCustomerPaymentModels.clear();
                String searchString = searchCustomer.getText().toString().trim();
                if (searchCustomer != null && searchCustomer.length() > 0) {
                    tvError.setVisibility(View.GONE);
                    searchCustomer(searchString);
                } else {
                    Toast.makeText(PaymentsActivity.this, "Please Enter Customer ID or Mobile number", Toast.LENGTH_SHORT).show();
                }
                hideKeyBoard(v);
            }
        });
        searchCustomer = (EditText) findViewById(R.id.searchCustomer);
        searchCustomer.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String searchString = v.getText().toString().trim();
                    Log.d("search string", searchString);
                    if (searchCustomer != null && searchCustomer.length() > 0) {
                        tvError.setVisibility(View.GONE);
                        searchCustomer(searchString);
                    } else {
                        Toast.makeText(PaymentsActivity.this, "Please Enter Customer ID or Mobile number", Toast.LENGTH_SHORT).show();
                    }
                }
                return false;
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
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    }
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }
    public void searchCustomer(String customerID) {
        database=openOrCreateDatabase("digitalrupay",MODE_PRIVATE,null);
        Cursor c = database.rawQuery("select * from customersdata where custom_customer_no='"+customerID+"' OR mobile_no='"+customerID+"' OR (first_name LIKE '%"+customerID+"%') OR  (last_name LIKE '%"+customerID+"%') OR stb_no='"+customerID+"' OR mac_id='"+customerID+"' OR card_no='"+customerID+"'", null);
        try {
            if (c.moveToFirst()) {
                do {
                    int i1=c.getColumnIndex("first_name");
                    String FirstName=c.getString(i1);
                    int i2=c.getColumnIndex("last_name");
                    String last_name=c.getString(i2);
                    int i3=c.getColumnIndex("addr1");
                    String addr1=c.getString(i3);
                    int i4=c.getColumnIndex("addr2");
                    String addr2=c.getString(i4);
                    int i5=c.getColumnIndex("custom_customer_no");
                    String custom_customer_no=c.getString(i5);
                    int i6=c.getColumnIndex("mobile_no");
                    String mobile_no=c.getString(i6);
                    int i7=c.getColumnIndex("pending_amount");
                    String pending_amount=c.getString(i7);
                    int i8=c.getColumnIndex("city");
                    String city=c.getString(i8);
                    int i9=c.getColumnIndex("cust_id");
                    String Cust_id=c.getString(i9);

                    EmpCustomerPaymentModel empCustomerPaymentModel=new EmpCustomerPaymentModel();
                    empCustomerPaymentModel.setFirstName(FirstName);
                    empCustomerPaymentModel.setlast_name(last_name);
                    empCustomerPaymentModel.setaddr1(addr1);
                    empCustomerPaymentModel.setaddr2(addr2);
                    empCustomerPaymentModel.setCity(city);
                    empCustomerPaymentModel.setcustom_customer_no(custom_customer_no);
                    empCustomerPaymentModel.setpending_amount(pending_amount);
                    empCustomerPaymentModel.setmobile_no(mobile_no);
                    empCustomerPaymentModel.setCust_id(Cust_id);
                    empCustomerPaymentModels.add(empCustomerPaymentModel);
                     } while (c.moveToNext());
            }
            setCustomerData();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public void asyncResponse1(String response) {
        parseCustomerDetails1(response);
    }
    private void parseCustomerDetails1(String response) {
        try {
            JSONObject customersObj = new JSONObject(response);
            Iterator<String> keys = customersObj.keys();
            String message = customersObj.getString("message");
            String text = customersObj.getString("text");
            PaymentsListHolder.strings.add("Maintenance");
            if (serviceRequest == 1) {
                if (message.equalsIgnoreCase("success")) {
                    MaintainceModel maintainceModel=new MaintainceModel();
                    while (keys.hasNext()) {
                        String key = keys.next();
                        if (!key.equalsIgnoreCase("message") && !key.equalsIgnoreCase("text")) {
                            maintainceModel = new Gson().fromJson(customersObj.getJSONObject(key).toString(),
                                    new TypeToken<MaintainceModel>() {
                                    }.getType());
                            setCustomerData1(maintainceModel);
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void setCustomerData1(MaintainceModel DataModel) {
        String ecat_name=DataModel.getEcat_name();
        PaymentsListHolder.strings.add(ecat_name);
    }
    @Override
    public void asyncResponse(String response) {
        parseCustomerDetails(response);
    }
    void parseCustomerDetails(String response) {
        try {
            JSONObject customersObj = new JSONObject(response);
            Iterator<String> keys = customersObj.keys();
            String message = customersObj.getString("message");
            String text = customersObj.getString("text");
            if (serviceRequest == 1) {
//                if (message.equalsIgnoreCase("success")) {
//                    while (keys.hasNext()) {
//                        String key = keys.next();
//                        if (!key.equalsIgnoreCase("message") && !key.equalsIgnoreCase("text")) {
//                            customerDataModel = new Gson().fromJson(customersObj.getJSONObject(key).toString(),
//                                    new TypeToken<CustomerDataModel>() {
//                                    }.getType());
//                            CustomerDataModelArrayList.add(customerDataModel);
//                        }
//                    }
//                }
//                    setCustomerData();
            } else if (serviceRequest == 2) {
                if (message.equalsIgnoreCase("success")) {
                    Toast.makeText(this, "Payment Success", Toast.LENGTH_SHORT).show();
                    CustomerPaymentSuccessModel customerPaymentSuccessModel = null;
                    while (keys.hasNext()) {
                        String key = keys.next();
                        if (!key.equalsIgnoreCase("message") && !key.equalsIgnoreCase("text")) {
                            customerPaymentSuccessModel = new Gson().fromJson(customersObj.getJSONObject(key).toString(),
                                    new TypeToken<CustomerPaymentSuccessModel>() {
                                    }.getType());
                        }
                    }

                } else {
                    Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
                }
            } else {
                if (message.equalsIgnoreCase("success")) {
                    AlertDialog messageDialog = new AlertDialog.Builder(this).setTitle("Success")
                            .setMessage("Next Payment Date Updated Successfully")
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
//                                    resetAllViews();
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_info
                            ).setCancelable(false).show();
                }
                Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setCustomerData() {
        complaintsListAdapter = new PaymentsListAdapter(this, empCustomerPaymentModels);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        PaymentList.setLayoutManager(llm);
        PaymentList.setAdapter(complaintsListAdapter);
    }
    public void sendPayment(final String custId, final String amount, final String trxnType, final String chequeNumber, final String bankName, final String branchName, final String date) {
        String invoice_code=null;
        database=openOrCreateDatabase("digitalrupay",MODE_PRIVATE,null);
        Cursor c1 = database.rawQuery("select * from empdata", null);
        try {
            if (c1.moveToFirst()) {
                do {
                    int i1 = c1.getColumnIndex("invoice_code");
                    invoice_code = c1.getString(i1);
                } while (c1.moveToNext());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        Random r = new Random();
        int Low = 99;
        int High = 99999;
        int Result = r.nextInt(High-Low) + Low;
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyMMddHHmmss");
        String temp_invoice = invoice_code+"/"+df.format(c.getTime())+"/"+Result;
        database.execSQL("insert into sendPayment values('"+temp_invoice+"','"+custId+"','"+employeeId+"','"+amount+"','"+trxnType+"','"+chequeNumber+"','"+bankName+"','"+branchName+"','"+date+"','false')");
        isConnected = ConnectivityReceiver.isConnected();
        if (isConnected){
            Log.e("Check","isConnected   :-   "+isConnected);
            Intent intent=new Intent(PaymentsActivity.this, PaymentService.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Log.e("Call","Service of PaymentService");
            startService(intent);
            Log.e("is","OPen");
        }
                    Intent paymentActivity = new Intent(this, PaymentSuccessActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("temp_invoice", temp_invoice);
                    bundle.putString("amount", amount);
                    bundle.putString("custId",custId);
                    bundle.putString(WsUrlConstants.LOGIN_TYPE, loginType);
                    paymentActivity.putExtras(bundle);
                    startActivityForResult(paymentActivity, 1000);
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        resetAllViews();
    }
    void resetAllViews() {
        searchCustomer.setText("");
        empCustomerPaymentModels.clear();
        complaintsListAdapter.notifyDataSetChanged();
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
//                Intent intent=new Intent(PaymentsActivity.this, PaymentService.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startService(intent);
//                Intent intent2=new Intent(PaymentsActivity.this, ComplaintService.class);
//                intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startService(intent2);
//                Intent intent1=new Intent(PaymentsActivity.this, UpdateComplaint.class);
//                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startService(intent1);
//            }
//        }catch (Exception e){
//
//        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        resetAllViews();
    }
}
