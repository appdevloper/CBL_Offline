package com.digitalrupay.network;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.digitalrupay.DigitalRupayApplication;
import com.digitalrupay.activities.SplashActivity;
import com.digitalrupay.activities.UpdateComplaintsActivity;

public class ConnectivityReceiver extends BroadcastReceiver {
    String TAG="ConnectivityReceiver";
    public static ConnectivityReceiverListener connectivityReceiverListener;

    public ConnectivityReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent arg1) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null
                && activeNetwork.isConnectedOrConnecting();

        if (connectivityReceiverListener != null) {
            connectivityReceiverListener.onNetworkConnectionChanged(isConnected);
        }
        if(isConnected){
            Log.e(TAG,"Inter Net Connected");
            Intent intent=new Intent(context, PaymentService.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startService(intent);
            Intent intent2=new Intent(context, ComplaintService.class);
            intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startService(intent2);
            Intent intent1=new Intent(context, UpdateComplaint.class);
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startService(intent1);
        }else{
            Log.e(TAG,"No Inter Net Connected");
        }

    }
    public static boolean isConnected() {
        ConnectivityManager
                cm = (ConnectivityManager) DigitalRupayApplication.getInstance().getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null
                && activeNetwork.isConnectedOrConnecting();
    }


    public interface ConnectivityReceiverListener {
        void onNetworkConnectionChanged(boolean isConnected);
    }

}