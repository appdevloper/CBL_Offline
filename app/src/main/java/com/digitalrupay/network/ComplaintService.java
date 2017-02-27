package com.digitalrupay.network;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.digitalrupay.datamodels.CustomerPaymentSuccessModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.http.NameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by sridhar on 1/28/2017.
 */

public class ComplaintService extends Service implements AsyncRequest.OnAsyncRequestComplete{
    SQLiteDatabase database;
    String employeeId,customerId,complaintMsg,category,complaintID;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            database=openOrCreateDatabase("digitalrupay", Context.MODE_PRIVATE,null);
            Cursor complaintCursor = database.rawQuery("Select * from sendComplaint where isSendComplaint='false'", null);
            if(complaintCursor!=null) {
                if (complaintCursor.moveToFirst()) {
                    do {
                        int i1 = complaintCursor.getColumnIndex("custId");
                        customerId = complaintCursor.getString(i1);
                        int i2 = complaintCursor.getColumnIndex("complaintMsg");
                        complaintMsg =complaintCursor.getString(i2);
                        int i3 = complaintCursor.getColumnIndex("category");
                        category = complaintCursor.getString(i3);
                        int i4=complaintCursor.getColumnIndex("complaintID");
                        complaintID=complaintCursor.getString(i4);
                        int i5=complaintCursor.getColumnIndex("employeeId");
                        employeeId=complaintCursor.getString(i5);
                        AsyncRequest asyncRequest = new AsyncRequest(this, "GET", new ArrayList<NameValuePair>(), "Processing Request..");
                        asyncRequest.execute(WsUrlConstants.addComplaintUrl.replace(WsUrlConstants.EMPLOYEE_ID, employeeId).replace(WsUrlConstants.CUSTOMER_ID, customerId.replace(" ", "%20"))
                                .replace(WsUrlConstants.COMPLAINT_MSG, complaintMsg.replace(" ", "%20")).replace(WsUrlConstants.COMPLAINT_CATEGORY, category).replace(WsUrlConstants.complaintID,complaintID));
                        database.execSQL("UPDATE sendComplaint SET isSendComplaint='true' WHERE complaintID='" + complaintID + "'");
                        database.execSQL("DELETE FROM sendComplaint WHERE complaintID='" + complaintID + "'");
                    } while (complaintCursor.moveToNext());
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
                String message = customersObj.getString("message");
                String text = customersObj.getString("text");
            Log.e("Send","sendComplaint"+ complaintID);
            Log.e("Complaint","Complaint Success Sent");
                if (message.equalsIgnoreCase("success")) {
                    Log.e("Complaint","Complaint Success Sent");
                }
        } catch (Exception e) {

        }
    }
}