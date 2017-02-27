package com.digitalrupay.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.signature.StringSignature;
import com.digitalrupay.R;
import com.digitalrupay.activities.Customer.CustomerHome;
import com.digitalrupay.adapters.CustomersListAdapter;
import com.digitalrupay.datamodels.CategoryDataModel;
import com.digitalrupay.datamodels.CollectionDataModel;
import com.digitalrupay.datamodels.ComplaintsDataModel;
import com.digitalrupay.datamodels.CustomerDataModel;
import com.digitalrupay.datamodels.DetailsModel;
import com.digitalrupay.datamodels.EmployeeDataModel;
import com.digitalrupay.datamodels.LoginResultModel;
import com.digitalrupay.datamodels.SessionData;
import com.digitalrupay.datamodels.UserModel;
import com.digitalrupay.fragments.ComplaintsFragment;
import com.digitalrupay.fragments.RegisterComplaintFragment;
import com.digitalrupay.network.AsyncRequest;
import com.digitalrupay.network.WsUrlConstants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import static com.digitalrupay.network.WsUrlConstants.loginTypes;

public class LoginActivity extends BaseActivity implements AsyncRequest.OnAsyncRequestComplete {

    TextInputLayout userName, password, email;
    String employeeId = null,totalCollections,total_collections,total_outstaning,invoice_code;
    int serviceRequest;
    SQLiteDatabase database;
    EmployeeDataModel employeeDataModel;
    ArrayList<CategoryDataModel> categoryDataModelArrayList = new ArrayList<>();
    ProgressDialog pd;
    int CountofCustomers;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.d(WsUrlConstants.LOGIN_TYPE, loginType);
        pd = new ProgressDialog(LoginActivity.this);
        pd.setCancelable(false);
        pd.setIndeterminate(true);
        pd.setMessage("Loading Data.........");
        userName = (TextInputLayout) findViewById(R.id.text_input_layout_user_name);
        password = (TextInputLayout) findViewById(R.id.text_input_layout_password);
        email = (TextInputLayout) findViewById(R.id.text_input_layout_email);
        if (loginType.equals(loginTypes[2])) {
            userName.setVisibility(View.GONE);
        }else if( loginType.equals(loginTypes[3]) ){
            userName.setVisibility(View.GONE);
        }else if( loginType.equals(loginTypes[4]) ){
            userName.setVisibility(View.GONE);
        }
        else {
            email.setVisibility(View.GONE);
        }
    }

    public void checkLoginCredentials(View view) {
        String username = userName.getEditText().getText().toString().trim();
        String pwd = password.getEditText().getText().toString().trim();
        if (loginType.equals(loginTypes[2])) {
            username = email.getEditText().getText().toString().trim();
        }else if (loginType.equals(loginTypes[3])) {
            username = email.getEditText().getText().toString().trim();
        }else if (loginType.equals(loginTypes[4])) {
            username = email.getEditText().getText().toString().trim();
        }
        if (username != null && username.length() > 0) {
            if (pwd != null && pwd.length() > 0) {
                if (checkNetworkConnection()) {
                    serviceRequest=1;
                    AsyncRequest getPosts = new AsyncRequest(this, "GET", null, "Validating User..");
                    if (loginType.equals(loginTypes[2])) {
                        getPosts.execute(WsUrlConstants.employeeLoginUrl.replace(WsUrlConstants.EMAIL, username).replace(WsUrlConstants.PASSWORD, pwd));
                    }else if (loginType.equals(loginTypes[4])) {
                        getPosts.execute(WsUrlConstants.employeeLoginUrl.replace(WsUrlConstants.EMAIL, username).replace(WsUrlConstants.PASSWORD, pwd));
                } else {
                        getPosts.execute(WsUrlConstants.loginUrl.replace(WsUrlConstants.USERNAME, username).replace(WsUrlConstants.PASSWORD, pwd));
                    }
                }
            } else {
                Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Please enter username", Toast.LENGTH_SHORT).show();
        }
    }

    public void redirectToFeeListActivity(String parentId) {
        Intent feeList = new Intent(this, FeeListActivity.class);
        feeList.putExtra(WsUrlConstants.PARENT_ID, parentId);
        feeList.putExtra(WsUrlConstants.LOGIN_TYPE, loginType);
        feeList.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(feeList);
        finish();
    }

    public void redirectToCableBillingActivity(final String parentId) {
        pd.dismiss();
        final Dialog dialog=new Dialog(LoginActivity.this);
        dialog.setContentView(R.layout.dialogofcustomer);
        dialog.setCancelable(false);
        dialog.setTitle("Customers Data Count Info");
        TextView tvCount_Customers=(TextView)dialog.findViewById(R.id.tvCount_Customers);
        tvCount_Customers.setText(CountofCustomers+" Customers Records Loaded");
        Button button=(Button)dialog.findViewById(R.id.ok);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent feeList = new Intent(LoginActivity.this, CableBillingActivity.class);
                feeList.putExtra(WsUrlConstants.PARENT_ID, parentId);
                feeList.putExtra(WsUrlConstants.LOGIN_TYPE, loginType);
                feeList.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(feeList);
                dialog.dismiss();
                finish();
            }
        });
        dialog.show();
    }

    private ArrayList<NameValuePair> getParams() {
        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("start", "0"));
        params.add(new BasicNameValuePair("limit", "10"));
        params.add(new BasicNameValuePair("fields", "id,title"));
        return params;
    }
    @Override
    public void asyncResponse(String response) {
        if (response != null) {
                try {
                    if(serviceRequest==1) {
                        pd.show();
                        JSONObject responseObj = new JSONObject(response);
                        String message = "";
                        if (loginType.equals(loginTypes[2])) {
                            message = responseObj.getString("message");
                            if (message.equalsIgnoreCase("success")) {
                                parseEmployeeResponse(responseObj);
                                return;
                            }
                        } else if (loginType.equals(loginTypes[3])) {
                            message = responseObj.getString("message");
                            if (message.equalsIgnoreCase("success")) {
                                parseEmployeeResponse(responseObj);
                                return;
                            }
                        } else if (loginType.equals(loginTypes[4])) {
                            message = responseObj.getString("message");
                            if (message.equalsIgnoreCase("success")) {
                                parseEmployeeResponse(responseObj);
                                return;
                            }
                        } else {
                            JSONObject loginObject = responseObj.getJSONObject("LoginResponse");
                            message = loginObject.getString("message");
                        }
                        if (message.equalsIgnoreCase("success")) {
                            JsonReader jsonReader = new JsonReader(new InputStreamReader(new ByteArrayInputStream(response.toString().getBytes())));
                            Type dataListType = new TypeToken<HashMap<String, LoginResultModel>>() {
                            }.getType();
                            GsonBuilder gsonBuilder = new GsonBuilder();
                            Gson gson = gsonBuilder.create();
                            HashMap<String, LoginResultModel> loginResponse = (HashMap<String, LoginResultModel>) gson.fromJson(jsonReader, dataListType);
                            LoginResultModel loginResultModel = loginResponse.get("LoginResponse");
                            SessionData.getSessionDataInstance().saveLoginResponse(loginResultModel);
                            DetailsModel detailsModel = ((DetailsModel) loginResultModel.getDetails());
                            UserModel userModel = detailsModel.getUserModel();
                            redirectToFeeListActivity(userModel.getId());
                        } else {
                            Toast.makeText(this, "Invalid login credentials", Toast.LENGTH_SHORT).show();
                        }
                    }else if(serviceRequest==2){
                        JSONObject customersObj = new JSONObject(response);
                        CollectionDataModel collectionDataModel = null;
                        ArrayList<CollectionDataModel> collectionDataModelArrayList = new ArrayList<>();
                        Iterator<String> keys = customersObj.keys();
                        String message = customersObj.getString("message");
                        String text = customersObj.getString("text");
                        if (message.equalsIgnoreCase("success")) {
                            while (keys.hasNext()) {
                                String key = keys.next();
                                if (!key.equalsIgnoreCase("message") && !key.equalsIgnoreCase("text")) {
                                    collectionDataModel = new Gson().fromJson(customersObj.getJSONObject(key).toString(),
                                            new TypeToken<CollectionDataModel>() {
                                            }.getType());
                                    collectionDataModelArrayList.add(collectionDataModel);
                                }
                            }
                        }
                        if (collectionDataModelArrayList.size() > 0) {
                            totalCollections = collectionDataModelArrayList.get(0).getTotal_collections();
                            if (totalCollections == null) {
                                totalCollections = "0";
                            }
                        }
                        loadCollection();
                    }
                    else if(serviceRequest==3){
                            JSONObject customersObj = new JSONObject(response);
                            Iterator<String> keys = customersObj.keys();
                            total_collections = customersObj.getString("total_collections");
                            loadOutStation();
                    }else if(serviceRequest==4){
                            JSONObject customersObj = new JSONObject(response);
                            total_outstaning = customersObj.getString("total_outstaning");
                        LoadImage();
                    }else if(serviceRequest==5){
                        JSONObject customersObj = new JSONObject(response);
                        JSONObject inOBJ = customersObj.getJSONObject("0");
                        String business_name = inOBJ.getString("business_name");
                        String address1 = inOBJ.getString("address1");
                        String address2 = "";
                        if (!inOBJ.isNull("address2")) {
                            address2 = inOBJ.getString("address2");
                        }
                        String email = "";
                        if (!inOBJ.isNull("email")) {
                            email = inOBJ.getString("email");
                        }
                        String city = inOBJ.getString("city");
                        String state = inOBJ.getString("state");
                        String mobile = inOBJ.getString("mobile");
                        invoice_code=inOBJ.getString("invoice_code");
                        final String businessLogo = customersObj.getString("business_logo");
                        JSONObject businessObj = new JSONObject(customersObj.getJSONObject("0").toString());
                        String SERVICES_name = businessObj.getString("business_name");
                        String SERVICE_MOBILE_NUMBER = businessObj.getString("mobile");
                        WsUrlConstants.Adderss = address1 + "\n " + city;
                        database.execSQL("insert into empdata values('"+total_outstaning+"','"+total_collections+"','"+totalCollections+"','"+invoice_code+"','"+business_name+"','"+address1+"'," +
                                "'"+address2+"','"+email+"','"+city+"','"+state+"','"+mobile+"','"+SERVICE_MOBILE_NUMBER+"','"+SERVICES_name+"')");
                        loadCustomerList();
                    }
                    else if(serviceRequest==6){
                        try {

                            JSONObject customersObj = new JSONObject(response);
                            ArrayList<CustomerDataModel> listOfCustomers = new ArrayList<CustomerDataModel>();
                            CustomerDataModel customerDataModel;
                            Iterator<String> keys = customersObj.keys();
                            while (keys.hasNext()) {
                                String key = keys.next();
                                if (!key.equalsIgnoreCase("message") && !key.equalsIgnoreCase("text")) {
                                    customerDataModel = new Gson().fromJson(customersObj.getJSONObject(key).toString(),
                                            new TypeToken<CustomerDataModel>() {
                                            }.getType());
                                    listOfCustomers.add(customerDataModel);
                                }
                            }
                            pd.setMessage("Load The Customers Data Of "+listOfCustomers.size());
                            CountofCustomers=listOfCustomers.size();
                            database.execSQL("CREATE TABLE IF NOT EXISTS customersdata (cust_id varchar(20),first_name varchar(20),last_name varchar(20),addr1 varchar(20),addr2 varchar(20),country varchar(20)," +
                                    "state varchar(20),pin_code varchar(20),phone_no varchar(20),mobile_no varchar(20),email_id varchar(50),\n" +
                                    "group_id varchar(20),custom_customer_no varchar(20),mac_id varchar(20),stb_no varchar(20),card_no varchar(30),connection_date varchar(100),dob varchar(20),anniversary_date varchar(150),\n" +
                                    "remarks varchar(500),amount varchar(20),pending_amount varchar(20),monthly_bill varchar(50),tax_rate varchar(200),start_date varchar(50),end_date varchar(20),\n" +
                                    "status varchar(20),dateCreated varchar(20),inactive_date varchar(20),city varchar(20))");
                                for(int i=0; i<listOfCustomers.size(); i++){
                                    String First_name=listOfCustomers.get(i).getFirst_name();
                                    String Last_name=listOfCustomers.get(i).getLast_name();
                                    String Addr1=listOfCustomers.get(i).getAddr1();
                                    String Addr2=listOfCustomers.get(i).getAddr2();
                                    String Addr3=listOfCustomers.get(i).getAddr3();
                                    String Amount=listOfCustomers.get(i).getAmount();
                                    String Anniversary_date=listOfCustomers.get(i).getAnniversary_date();
                                    String Business_name=listOfCustomers.get(i).getBusiness_name();
                                    String Card_no=listOfCustomers.get(i).getCard_no();
                                    String Connection_date=listOfCustomers.get(i).getConnection_date();
                                    String Stb_no=listOfCustomers.get(i).getStb_no();
                                    String Status=listOfCustomers.get(i).getStatus();
                                    String State=listOfCustomers.get(i).getState();
                                    String Start_date=listOfCustomers.get(i).getStart_date();
                                    String Remarks=listOfCustomers.get(i).getRemarks();
                                    String Pin_code=listOfCustomers.get(i).getPin_code();
                                    String Phone_no=listOfCustomers.get(i).getPhone_no();
                                    String Tax_rate=listOfCustomers.get(i).getTax_rate();
                                    String Pending_amount=listOfCustomers.get(i).getPending_amount();
                                    String Mac_id=listOfCustomers.get(i).getMac_id();
                                    String Group_id=listOfCustomers.get(i).getGroup_id();
                                    String Inactive_date=listOfCustomers.get(i).getInactive_date();
                                    String Dob=listOfCustomers.get(i).getDob();
                                    String City=listOfCustomers.get(i).getCity();
                                    String Custom_customer_no=listOfCustomers.get(i).getCustom_customer_no();
                                    String Cust_id=listOfCustomers.get(i).getCust_id();
                                    String Country=listOfCustomers.get(i).getCountry();
                                    String Mobile_no=listOfCustomers.get(i).getMobile_no();
                                    String Email_id=listOfCustomers.get(i).getEmail_id();
                                    String Monthly_bill=listOfCustomers.get(i).getMonthlybill();
                                    String End_date=listOfCustomers.get(i).getEnd_date();
                                    String DateCreated=listOfCustomers.get(i).getDateCreated();
                                    String city=listOfCustomers.get(i).getCity();
                                    database.execSQL("insert into customersdata values('"+Cust_id+"' ,'"+First_name+"' ,'"+Last_name+"','"+Addr1+"','"+Addr2 +"','"+Country+"','"+State+"','"+Pin_code+"','"+Phone_no+"','"+Mobile_no+"','"+Email_id+"','"+Group_id+"'," +
                                            "'"+Custom_customer_no+"','"+Mac_id+"','"+Stb_no+"','"+Card_no+"','"+Connection_date+"','"+Dob+"','"+Anniversary_date+"','"+Remarks+"','"+Amount+"','"+Pending_amount+"','"+Monthly_bill+"','"+Tax_rate+"','"+Start_date+"','"+End_date +"','"+Status+"','"+DateCreated+"','"+Inactive_date+"','"+city+"')");
                                }
                            getCategories();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else if(serviceRequest == 7){
                        JSONObject customersObj = new JSONObject(response);
                        CategoryDataModel categoryDataModel = null;
                        Iterator<String> keys = customersObj.keys();
                        while (keys.hasNext()) {
                            String key = keys.next();
                            if (!key.equalsIgnoreCase("message") && !key.equalsIgnoreCase("text")) {
                                categoryDataModel = new Gson().fromJson(customersObj.getJSONObject(key).toString(),
                                        new TypeToken<CategoryDataModel>() {
                                        }.getType());
                                categoryDataModelArrayList.add(categoryDataModel);
                            }
                        }
                        String message = customersObj.getString("message");
                        String text = customersObj.getString("text");
                        if (message.equalsIgnoreCase("success")) {

                            // Spinner Drop down elements
                            ArrayList<String> categories = new ArrayList<String>();
                            for (CategoryDataModel categoryDataModel1 : categoryDataModelArrayList) {
                                categories.add(categoryDataModel1.getCategory());
                            }
                            SessionData.setCategoriesList(categoryDataModelArrayList);
                            database.execSQL("Create table if not exists Category (id varchar(20), category varchar(500))");
                            for(int i=0; i<categoryDataModelArrayList.size(); i++){
                                String id=categoryDataModelArrayList.get(i).getId();
                                String category=categoryDataModelArrayList.get(i).getCategory();
                                database.execSQL("insert into Category values('"+id+"', '"+category+"')");
                            }
                            getComplaints();
                        } else {
                            Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
                        }
                    }else  if (serviceRequest == 8){
                        JSONObject customersObj = new JSONObject(response);
                        ComplaintsDataModel complaintsDataModel = null;
                        ArrayList<ComplaintsDataModel> complaintsDataModelArrayList = new ArrayList<>();
                        Iterator<String> keys = customersObj.keys();
                        while (keys.hasNext()) {
                            String key = keys.next();
                            if (!key.equalsIgnoreCase("message") && !key.equalsIgnoreCase("text")) {
                                complaintsDataModel = new Gson().fromJson(customersObj.getJSONObject(key).toString(),
                                        new TypeToken<ComplaintsDataModel>() {
                                        }.getType());
                                complaintsDataModelArrayList.add(complaintsDataModel);
                            }
                        }
                        String message = customersObj.getString("message");
                        String text = customersObj.getString("text");
                        database.execSQL("Create table if not exists ComplaintsData(cust_id varchar(20),first_name varchar(20),last_name varchar(20),addr1 varchar(20),addr2 varchar(20),mobile_no varchar(20),email_id varchar(50),custom_customer_no varchar(20)," +
                                "remarks varchar(500),complaint_id varchar(20),comp_ticketno varchar(20),comp_cat varchar(20)," +
                                "complaint varchar(20),comp_status varchar(20),created_by varchar(20),created_date varchar(20),last_edited_by varchar(20),comp_remarks varchar(20))");
                        ArrayList<CategoryDataModel> categoryDataModelArrayList = SessionData.getCategoriesList();
                            if (categoryDataModelArrayList.size() > 0 && complaintsDataModelArrayList.size() > 0) {

                                ArrayList<ComplaintsDataModel> dummyList = new ArrayList<>();
                                for (ComplaintsDataModel complaintsDataModel1 : complaintsDataModelArrayList) {
                                    for (CategoryDataModel categoryDataModel : categoryDataModelArrayList) {
                                        if (categoryDataModel.getId().contains(complaintsDataModel1.getComp_cat())) {
                                            complaintsDataModel1.setComp_cat(categoryDataModel.getCategory());
                                            break;
                                        }
                                    }
                                    dummyList.add(complaintsDataModel1);
                                }
                                complaintsDataModelArrayList = dummyList;
                                if(complaintsDataModelArrayList.size()!=0) {
                                    for ( int i = 0;i<complaintsDataModelArrayList.size();i++){
                                        String First_name=complaintsDataModelArrayList.get(i).getFirst_name();
                                        String Last_name=complaintsDataModelArrayList.get(i).getLast_name();
                                        String Addr1=complaintsDataModelArrayList.get(i).getAddr1();
                                        String Addr2=complaintsDataModelArrayList.get(i).getAddr2();
                                        String Remarks=complaintsDataModelArrayList.get(i).getRemarks();
                                        String Custom_customer_no=complaintsDataModelArrayList.get(i).getCustom_customer_no();
                                        String Cust_id=complaintsDataModelArrayList.get(i).getCust_id();
                                        String Mobile_no=complaintsDataModelArrayList.get(i).getMobile_no();
                                        String Email_id=complaintsDataModelArrayList.get(i).getEmail_id();

                                        String complaint_id=complaintsDataModelArrayList.get(i).getComplaint_id();
                                        String customer_id=complaintsDataModelArrayList.get(i).getCustomer_id();
                                        String comp_ticketno=complaintsDataModelArrayList.get(i).getComp_ticketno();
                                        String comp_cat=complaintsDataModelArrayList.get(i).getComp_cat();
                                        String complaint=complaintsDataModelArrayList.get(i).getComplaint();
                                        String comp_status=complaintsDataModelArrayList.get(i).getComp_status();
                                        String created_by=complaintsDataModelArrayList.get(i).getCreated_by();
                                        String created_date=complaintsDataModelArrayList.get(i).getCreated_date();
                                        String last_edited_by=complaintsDataModelArrayList.get(i).getLast_edited_by();
                                        String comp_remarks=complaintsDataModelArrayList.get(i).getComp_remarks();
                                        database.execSQL("insert into ComplaintsData values('"+Cust_id+"','"+First_name+"','"+Last_name+"','"+Addr1+"','"+Addr2 +"','"+Mobile_no+"'," +
                                                "'"+Email_id+"','"+Custom_customer_no+"','"+Remarks+"','"+complaint_id+"','"+comp_ticketno+"','"+comp_cat+"'," +
                                                "'"+complaint+"','"+comp_status+"','"+created_by+"','"+created_date+"','"+last_edited_by+"','"+comp_remarks+"')");
                                    }
                                }
                            SessionData.setComplaintsDataModelArrayList(complaintsDataModelArrayList);
                                database.execSQL("create table if not exists sendPayment(temp_invoice varchar(20),custId varchar(20),employeeId varchar(20),amount varchar(20),trxnType varchar(20),chequeNumber varchar(20),bankName varchar(20),branchName varchar(20),date varchar(20),checkSend BOOL)");
                                database.execSQL("create table if not exists updateComplaintUrl(COMPLAINT_ID varchar(20),EMPLOYEE_ID varchar(20),REMARKS varchar(100),COMPLAINT_STATUS varchar(20), sendStats BOOL)");
                                database.execSQL("create table if not exists sendComplaint(employeeId varchar(20),complaintID varchar(20),custId varchar(20),complaintMsg varchar(500),category varchar(20),isSendComplaint BOOL)");
                                redirectToCableBillingActivity(employeeDataModel.getEmp_id());
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
        }
    }
    public void parseEmployeeResponse(JSONObject empObj) {
        try {

            Iterator<String> keys = empObj.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                Log.e("key id", key);
                if (!key.equalsIgnoreCase("message") && !key.equalsIgnoreCase("text")) {
                    employeeDataModel = new Gson().fromJson(empObj.getJSONObject(key).toString(),
                            new TypeToken<EmployeeDataModel>() {
                            }.getType());
                    if (loginType.equals(loginTypes[4])) {
                        SessionData.getSessionDataInstance().saveApartmentLoginResponse(employeeDataModel);
                        employeeId = SessionData.getApartmentLoginResult().getEmp_id();
                    }else if((loginType.equals(loginTypes[2]))) {
                        database=openOrCreateDatabase("digitalrupay",MODE_PRIVATE,null);
                        database.execSQL("create table if not exists empdata(total_outstaning varchar(20),total_collections varchar(20),TodaysCollection varchar(20),invoice_code varchar(20),business_name varchar(120),address1 varchar(200),address2 varchar(200),email varchar(20),city varchar(20),state varchar(20),mobile varchar(20),SERVICE_MOBILE_NUMBER varchar(20),SERVICES_name varchar(20))");
                        SessionData.getSessionDataInstance().saveEmployeeLoginResponse(employeeDataModel);
                        employeeId = SessionData.getEmployeeLoginResult().getEmp_id();
                    }
                    serviceRequest=2;
                    AsyncRequest asyncRequest = new AsyncRequest(this, "GET", new ArrayList<NameValuePair>(), "Fetching Data..");
                    asyncRequest.execute(WsUrlConstants.todaysTotalCollectionUrl.replace(WsUrlConstants.EMPLOYEE_ID, employeeId));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void loadCollection() {
        serviceRequest=3;
        AsyncRequest asyncRequest = new AsyncRequest(this, "GET", new ArrayList<NameValuePair>(), "Fetching Data..");
        asyncRequest.execute(WsUrlConstants.emp_tot_collections.replace(WsUrlConstants.EMPLOYEE_ID, employeeId));
    }
    private void loadOutStation() {
        serviceRequest=4;
        AsyncRequest asyncRequest = new AsyncRequest(this, "GET", new ArrayList<NameValuePair>(), "Fetching Data..");
        asyncRequest.execute(WsUrlConstants.emp_tot_outstanding.replace(WsUrlConstants.EMPLOYEE_ID, employeeId));
    }
    private void LoadImage() {
        serviceRequest=5;
        AsyncRequest asyncRequest = new AsyncRequest(this, "GET", new ArrayList<NameValuePair>(), "Fetching Data..");
        asyncRequest.execute(WsUrlConstants.businessLogoUrl);
    }
    private void loadCustomerList() {
        serviceRequest=6;
        AsyncRequest asyncRequest = new AsyncRequest(this, "GET", getParams1(), "Fetching Data..");
        asyncRequest.execute(WsUrlConstants.customerListUrl.replace(WsUrlConstants.EMPLOYEE_ID, employeeId));
    }
    private void getCategories() {
        serviceRequest = 7;
        AsyncRequest asyncRequest = new AsyncRequest(this, "GET", new ArrayList<NameValuePair>(), "Fetching Categories..");
        asyncRequest.execute(WsUrlConstants.categoriesUrl);
    }
    private void getComplaints() {
        serviceRequest = 8;
        AsyncRequest asyncRequest = new AsyncRequest(this, "GET", new ArrayList<NameValuePair>(), "Fetching Data..");
        asyncRequest.execute(WsUrlConstants.complaintsDetailsUrl.replace(WsUrlConstants.EMPLOYEE_ID, employeeId));
    }
    private ArrayList<NameValuePair> getParams1() {
        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        return params;
    }

}
