package com.digitalrupay.network;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by sridhar on 2/6/2017.
 */

public class UpdateComplaint extends Service implements AsyncRequest.OnAsyncRequestComplete {
    SQLiteDatabase database;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            database = openOrCreateDatabase("digitalrupay", Context.MODE_PRIVATE, null);
            Cursor complaintCursor = database.rawQuery("Select * from updateComplaintUrl where sendStats='false'", null);
            if (complaintCursor != null) {
                if (complaintCursor.moveToFirst()) {
                    String COMPLAINT_ID="",EMPLOYEE_ID="",REMARKS="",COMPLAINT_STATUS="";
                    do {
                        int i1 = complaintCursor.getColumnIndex("COMPLAINT_ID");
                        COMPLAINT_ID = complaintCursor.getString(i1);
                        int i2 = complaintCursor.getColumnIndex("EMPLOYEE_ID");
                        EMPLOYEE_ID = complaintCursor.getString(i2);
                        int i3 = complaintCursor.getColumnIndex("REMARKS");
                        REMARKS = complaintCursor.getString(i3);
                        int i4 = complaintCursor.getColumnIndex("COMPLAINT_STATUS");
                        COMPLAINT_STATUS = complaintCursor.getString(i4);
                        AsyncRequest asyncRequest = new AsyncRequest(this, "GET", new ArrayList<NameValuePair>(), "Processing Request..");
                        asyncRequest.execute(WsUrlConstants.updateComplaintUrl.replace(WsUrlConstants.COMPLAINT_ID, COMPLAINT_ID)
                        .replace(WsUrlConstants.EMPLOYEE_ID, EMPLOYEE_ID).replace(WsUrlConstants.REMARKS, REMARKS).replace(WsUrlConstants.COMPLAINT_STATUS, COMPLAINT_STATUS));
                        database.execSQL("UPDATE updateComplaintUrl SET sendStats='true' WHERE COMPLAINT_ID='" + COMPLAINT_ID + "'");
                        database.execSQL("DELETE FROM updateComplaintUrl WHERE COMPLAINT_ID='" + COMPLAINT_ID + "'");
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
        } catch (Exception e) {

        }
    }
}