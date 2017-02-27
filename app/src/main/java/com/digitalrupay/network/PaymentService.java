package com.digitalrupay.network;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.digitalrupay.activities.PaymentsActivity;
import com.digitalrupay.datamodels.CustomerPaymentSuccessModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.http.NameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by sridhar on 1/23/2017.
 */

public class PaymentService extends Service implements AsyncRequest.OnAsyncRequestComplete{
    SQLiteDatabase database;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
        database=openOrCreateDatabase("digitalrupay", Context.MODE_PRIVATE,null);
        Cursor c = database.rawQuery("Select * from sendPayment where checkSend='false'", null);
        if(c!=null) {
            if (c.moveToFirst()) {
                do {
                    Log.e("Payment Services", "Entry");
                    String temp_invoice,custId,amount,trxnType,chequeNumber,bankName,branchName,date,employeeId;
                    int i1 = c.getColumnIndex("temp_invoice");
                    temp_invoice = c.getString(i1);
                    int i2 = c.getColumnIndex("custId");
                    custId = c.getString(i2);
                    int i3 = c.getColumnIndex("amount");
                    amount = c.getString(i3);
                    int i4 = c.getColumnIndex("trxnType");
                    trxnType = c.getString(i4);
                    int i5 = c.getColumnIndex("chequeNumber");
                    chequeNumber = c.getString(i5);
                    int i6 = c.getColumnIndex("bankName");
                    bankName = c.getString(i6);
                    int i7 = c.getColumnIndex("branchName");
                    branchName = c.getString(i7);
                    int i8 = c.getColumnIndex("date");
                    date = c.getString(i8);
                    int i9 = c.getColumnIndex("employeeId");
                    employeeId = c.getString(i9);
                    database.execSQL("UPDATE sendPayment SET checkSend='true' WHERE temp_invoice='" + temp_invoice + "'");
                    database.execSQL("DELETE FROM sendPayment WHERE temp_invoice='" + temp_invoice + "'");
                    Log.e("Payment Services", "Send To Server");
                    AsyncRequest asyncRequest = new AsyncRequest(PaymentService.this, "GET", new ArrayList<NameValuePair>(), "Processing Request..");
                    asyncRequest.execute(WsUrlConstants.cablePaymentUrl.replace(WsUrlConstants.EMPLOYEE_ID, employeeId)
                            .replace(WsUrlConstants.Temp_Invoice, temp_invoice)
                            .replace(WsUrlConstants.CUSTOMER_ID, custId)
                            .replace(WsUrlConstants.AMOUNT, amount)
                            .replace(WsUrlConstants.TRANSACTION_TYPE, trxnType)
                            .replace(WsUrlConstants.CHEQUE_NUMBER, chequeNumber)
                            .replace(WsUrlConstants.BANK_NAME, bankName)
                            .replace(WsUrlConstants.BRANCH_NAME, branchName)
                            .replace(WsUrlConstants.TRANSACTION_DATE, date)
                            .replace(WsUrlConstants.REMARKS, "Remarks"));
                } while (c.moveToNext());
            }
        }
        } catch (Exception e) {
        }
        return START_STICKY;
    }
    public void onDestroy() {
        stopSelf();
    }

    @Override
    public void asyncResponse(String response) {
        try {
                JSONObject customersObj = new JSONObject(response);
                Iterator<String> keys = customersObj.keys();
                String message = customersObj.getString("message");
                String text = customersObj.getString("text");
                if (message.equalsIgnoreCase("success")) {
                    Log.e("Payment Services", "Upload the into services");
                    CustomerPaymentSuccessModel customerPaymentSuccessModel = null;
                    while (keys.hasNext()) {
                        String key = keys.next();
                        if (!key.equalsIgnoreCase("message") && !key.equalsIgnoreCase("text")) {
                            customerPaymentSuccessModel = new Gson().fromJson(customersObj.getJSONObject(key).toString(),
                                    new TypeToken<CustomerPaymentSuccessModel>() {
                                    }.getType());
                            Log.e("PayMent",text);
                        }
                    }

                } else {
                    Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
                }
        } catch (Exception e) {

        }
    }
}