package com.digitalrupay.activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.digitalrupay.R;
import com.digitalrupay.adapters.EmpCollectionListAdapter;
import com.digitalrupay.datamodels.CollectionDataModel;
import com.digitalrupay.datamodels.EmpCollectionDataModel;
import com.digitalrupay.datamodels.EmployeeDataModel;
import com.digitalrupay.datamodels.SessionData;
import com.digitalrupay.network.AsyncRequest;
import com.digitalrupay.network.WsUrlConstants;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.http.NameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import static com.digitalrupay.network.WsUrlConstants.loginTypes;

/**
 * Created by Santosh on 10/5/2016.
 */

public class CollectionsActivity extends BaseActivity{

    TextView tvTodaysCollection,tv_Collection_collection_amt,tv_OutStation_amt;
    String totalCollectionAmt = "0",TodaysCollection,total_collections,total_outstaning;;
    int serviceRequest;
    String employeeId = null;
    SQLiteDatabase database;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collections);
        setTitle("Collections", true);
        tvTodaysCollection = (TextView) findViewById(R.id.tv_todays_collection_amt);
        tv_Collection_collection_amt=(TextView)findViewById(R.id.tv_Collection_collection_amt);
        tv_OutStation_amt=(TextView)findViewById(R.id.tv_OutStation_amt);
        database=openOrCreateDatabase("digitalrupay",MODE_PRIVATE,null);
        Cursor c = database.rawQuery("Select * from empdata", null);
        try {
            if (c.moveToFirst()) {
                do {
                    int i1 = c.getColumnIndex("TodaysCollection");
                    TodaysCollection = c.getString(i1);
                    int i2 = c.getColumnIndex("total_collections");
                    total_collections = c.getString(i2);
                    int i3 = c.getColumnIndex("total_outstaning");
                    total_outstaning = c.getString(i3);
                } while (c.moveToNext());
            }
            tv_OutStation_amt.setText("Rs." + total_outstaning + "/-");
            tv_Collection_collection_amt.setText("Rs." + total_collections + "/-");
            tvTodaysCollection.setText("Rs." + TodaysCollection + "/-");
            totalCollectionAmt = TodaysCollection;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void navigateToPayments(View view) {
        Intent payments = new Intent(this, PaymentsActivity.class);
        payments.putExtra(WsUrlConstants.LOGIN_TYPE, loginType);
        startActivity(payments);
    }
    public void navigateToTodaysCollections(View view) {
        Intent todayCollection = new Intent(this, TodayCollectionsActivity.class);
        todayCollection.putExtra(WsUrlConstants.AMOUNT, totalCollectionAmt);
        startActivity(todayCollection);
    }
    public void navigateToSummary(View view){
        Intent summary=new Intent(CollectionsActivity.this,SummaryActivity.class);
        startActivity(summary);
    }
    public void navigateToOutStation(View view){
        Intent summary=new Intent(CollectionsActivity.this,OutStationActivity.class);
        startActivity(summary);
    }
    public void navigateToCollection(View view){
        Intent summary=new Intent(CollectionsActivity.this,EmpCollectionActivity.class);
        startActivity(summary);
    }
}
