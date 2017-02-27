package com.digitalrupay.fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.digitalrupay.R;
import com.digitalrupay.activities.ComplaintsActivity;
import com.digitalrupay.datamodels.CategoryDataModel;
import com.digitalrupay.datamodels.CustomerDataModel;
import com.digitalrupay.datamodels.EmployeeDataModel;
import com.digitalrupay.datamodels.SessionData;
import com.digitalrupay.interfaces.CommunicationListener;
import com.digitalrupay.network.AsyncRequest;
import com.digitalrupay.network.ConnectivityReceiver;
import com.digitalrupay.network.PaymentService;
import com.digitalrupay.network.WsUrlConstants;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Random;

/**
 * Created by Santosh on 10/6/2016.
 */

public class RegisterComplaintFragment extends Fragment implements AsyncRequest.OnAsyncRequestComplete {

    Spinner spnCategories;
    String category, custId, employeeId;
    public EditText edtCustomerID, edtComplaintMsg, searchCustomer;
    CommunicationListener communicationListener;
    ArrayList<CategoryDataModel> categoriesList;
    TextView tvName, tvAddress, tvMobileNumber;
    int serviceRequest = 1;
    ImageView ivSearch;
    SQLiteDatabase database;
    ArrayList<CategoryDataModel> categoryDataModels=new ArrayList<>();
    String firstName = "", lastName = "", customerNumber = "",Addr1="",Mobile_no="",city="",Addr2="",Email_id="",comp_cat="";
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        communicationListener = (CommunicationListener) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_complaint, container, false);

        spnCategories = (Spinner) view.findViewById(R.id.spnCategories);
        edtComplaintMsg = (EditText) view.findViewById(R.id.edtComplaintMsg);

        tvName = (TextView) view.findViewById(R.id.txtName);
        tvAddress = (TextView) view.findViewById(R.id.txtAddress);
        tvMobileNumber = (TextView) view.findViewById(R.id.txtMobileNumber);
        database = getActivity().openOrCreateDatabase("digitalrupay", getActivity().MODE_PRIVATE, null);
        ((FrameLayout) view.findViewById(R.id.fl_payment)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerComplaint(v);
            }
        });

        ivSearch = (ImageView) view.findViewById(R.id.imgSearch);
        ivSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchString = searchCustomer.getText().toString().trim();
                Log.d("search string", searchString);
                if (searchCustomer != null && searchCustomer.length() > 0) {
                    searchCustomer(searchString);
                } else {
                    Toast.makeText(getActivity(), "Please Enter Customer ID or Mobile number", Toast.LENGTH_SHORT).show();
                }
            }
        });
        searchCustomer = (EditText) view.findViewById(R.id.searchCustomer);
        searchCustomer.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String searchString = v.getText().toString().trim();
                    Log.d("search string", searchString);
                    if (searchCustomer != null && searchCustomer.length() > 0) {
                        communicationListener.searchCustomer(searchString);
                    } else {
                        Toast.makeText(getActivity(), "Please Enter Customer ID or Mobile number", Toast.LENGTH_SHORT).show();
                    }
                }
                return false;
            }
        });

        categoriesList = SessionData.getCategoriesList();
        setCategories(categoriesList);

        EmployeeDataModel employeeDataModel;
        if(SessionData.getSessionDataInstance().getApartmentLoginResult()!= null) {
            employeeDataModel = SessionData.getSessionDataInstance().getApartmentLoginResult();
        }else {
            employeeDataModel= SessionData.getSessionDataInstance().getEmployeeLoginResult();
        }
        employeeId = employeeDataModel.getEmp_id();
        return view;
    }

    public void searchCustomer(String customerID) {

        Cursor c = database.rawQuery("select * from customersdata where custom_customer_no='" + customerID + "'", null);
        try {
            if (c.moveToFirst()) {
                do {
                    int i1 = c.getColumnIndex("first_name");
                    firstName = c.getString(i1);
                    int i2 = c.getColumnIndex("last_name");
                    lastName = c.getString(i2);
                    int i3 = c.getColumnIndex("addr1");
                    Addr1 = c.getString(i3);
                    int i5 = c.getColumnIndex("custom_customer_no");
                    customerNumber = c.getString(i5);
                    int i6 = c.getColumnIndex("mobile_no");
                    Mobile_no = c.getString(i6);
                    int i8 = c.getColumnIndex("city");
                    city = c.getString(i8);
                    int i9=c.getColumnIndex("cust_id");
                    custId=c.getString(i9);
                    int i10=c.getColumnIndex("addr2");
                    Addr2=c.getString(i10);
                    int i11=c.getColumnIndex("email_id");
                    Email_id=c.getString(i11);
                } while (c.moveToNext());
            }
            Log.e("Serch",firstName + " " + lastName + " - (" + customerNumber + ")"+Addr1 + ", " + city);
            tvName.setText(firstName + " " + lastName + " - (" + customerNumber + ")");
            tvAddress.setText(Addr1 + ", " + city);
            tvMobileNumber.setText(Mobile_no);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setCategories(final ArrayList<CategoryDataModel> categoriesList) {
        ArrayList<String> categories = new ArrayList<String>();
        database=getActivity().openOrCreateDatabase("digitalrupay",getActivity().MODE_PRIVATE,null);
        Cursor c = database.rawQuery("select * from Category", null);
        try {
            if (c.moveToFirst()) {
                do {
                    int i=c.getColumnIndex("category");
                    String category=c.getString(i);
                    categories.add(category);
                    int i1=c.getColumnIndex("id");
                    String id=c.getString(i1);
                    CategoryDataModel categoryDataModel=new CategoryDataModel();
                    categoryDataModel.setCategory(category);
                    categoryDataModel.setId(id);
                    categoryDataModels.add(categoryDataModel);
                } while (c.moveToNext());
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spnCategories.setAdapter(dataAdapter);

        spnCategories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category = categoryDataModels.get(position).getId();
                comp_cat=categoryDataModels.get(position).getCategory();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void registerComplaint(View view) {
        String complaintMsg = edtComplaintMsg.getText().toString().trim();
        if (complaintMsg != null && complaintMsg.length() > 0) {
                Random r = new Random();
                int Low = 999;
                int High = 999999;
                int Result = r.nextInt(High-Low) + Low;
                String complaintID=""+Result;

                Calendar c = Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy : HHmm");
                String created_date=""+df.format(c.getTime());
                Log.e("complaintID",complaintID);
                database.execSQL("insert into ComplaintsData values('"+custId+"','"+firstName+"','"+lastName+"','"+Addr1+"','"+Addr2 +"','"+Mobile_no+"'," +
                        "'"+Email_id+"','"+customerNumber+"','','"+complaintID+"','"+complaintID+"','"+comp_cat+"'," +
                        "'"+complaintMsg+"','0','"+employeeId+"','"+created_date+"','','N/A')");
            communicationListener.registerComplaint(custId, complaintMsg, category,complaintID);
            } else {
                Toast.makeText(getActivity(), "Please Enter Compliant Message", Toast.LENGTH_SHORT).show();
            }
    }
    @Override
    public void asyncResponse(String response) {
        try {
            if (serviceRequest == 1) {
                JSONObject customersObj = new JSONObject(response);
                Log.e("customersObj",""+customersObj);
                String message = customersObj.getString("message");
                String text = customersObj.getString("text");
                Iterator<String> keys = customersObj.keys();
                CustomerDataModel customerDataModel = null;
                if (message.equalsIgnoreCase("success")) {
                    while (keys.hasNext()) {
                        String key = keys.next();
                        if (!key.equalsIgnoreCase("message") && !key.equalsIgnoreCase("text")) {
                            customerDataModel = new Gson().fromJson(customersObj.getJSONObject(key).toString(),
                                    new TypeToken<CustomerDataModel>() {
                                    }.getType());
                        }
                    }
                    if (customerDataModel != null) {
                        custId = customerDataModel.getCust_id();
                        tvName.setText(customerDataModel.getFirst_name().trim() + " " + customerDataModel.getLast_name().trim() + " - (" + customerDataModel.getCustom_customer_no() + ")");
                        tvAddress.setText(customerDataModel.getAddr2() + ", " + customerDataModel.getCity());
                        tvMobileNumber.setText(customerDataModel.getMobile_no());
                    }
                }
                Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
