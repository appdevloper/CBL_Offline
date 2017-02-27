package com.digitalrupay;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.crittercism.app.Crittercism;
import com.digitalrupay.activities.PaymentsActivity;
import com.digitalrupay.activities.SplashActivity;
import com.digitalrupay.network.ComplaintService;
import com.digitalrupay.network.ConnectivityReceiver;
import com.digitalrupay.network.PaymentService;
import com.digitalrupay.network.UpdateComplaint;

/**
 * Created by Santosh on 8/19/2016.
 */


//
public class DigitalRupayApplication extends Application implements ConnectivityReceiver.ConnectivityReceiverListener {
    boolean isConnected;
    private static Context appContext;
    private static DigitalRupayApplication mInstance;
    @Override
    public void onCreate() {
        super.onCreate();
        mInstance=this;
        appContext = getApplicationContext();
        setAppContext(getApplicationContext());
        try {
            Crittercism.initialize(getApplicationContext(), "a1bec79e4faf413c8ae77c46e4e1086d00555300");
        } catch (Exception e) {
            e.printStackTrace();
        }
        isConnected = ConnectivityReceiver.isConnected();
//        if(isConnected){
//            Intent intent=new Intent(DigitalRupayApplication.this, PaymentService.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startService(intent);
//            Intent intent2=new Intent(DigitalRupayApplication.this, ComplaintService.class);
//            intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startService(intent2);
//            Intent intent1=new Intent(DigitalRupayApplication.this, UpdateComplaint.class);
//            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startService(intent1);
//        }
    }

    public static Context getAppContext() {
        return appContext;
    }

    private static void setAppContext(Context appContext) {
        DigitalRupayApplication.appContext = appContext;
    }
    public static synchronized DigitalRupayApplication getInstance() {
        return mInstance;
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }
    public void onNetworkConnectionChanged(boolean Connected) {
        isConnected=Connected;
    }
}
