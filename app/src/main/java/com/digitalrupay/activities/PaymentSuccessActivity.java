package com.digitalrupay.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

import com.analogics.thermalAPI.Bluetooth_Printer_2inch_ThermalAPI;
import com.analogics.thermalprinter.AnalogicsThermalPrinter;
import com.crittercism.internal.c;
import com.digitalrupay.R;
import com.digitalrupay.datamodels.CustomerDataModel;
import com.digitalrupay.datamodels.CustomerPaymentSuccessModel;
import com.digitalrupay.datamodels.EmpCustomerPaymentModel;
import com.digitalrupay.datamodels.EmployeeDataModel;
import com.digitalrupay.datamodels.SessionData;
import com.digitalrupay.network.WsUrlConstants;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

import static com.digitalrupay.network.WsUrlConstants.loginTypes;

/**
 * Created by Santosh on 10/2/2016.
 */

public class PaymentSuccessActivity extends BaseActivity {

    TextView tvInvoice, tvTransactionType;
    String customerData = null;
    AnalogicsThermalPrinter conn = new AnalogicsThermalPrinter();
    String totalCollectionAmt = "0",TodaysCollection,total_collections,total_outstaning;
    String address = null, temp_invoice, amount, custId, FirstName, last_name, addr1, addr2, custom_customer_no, mobile_no, pending_amount, city, balance,custAddress,SERVICE_MOBILE_NUMBER,SERVICES_name,Adderss;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_payment_success_layout);
        tvInvoice = (TextView) findViewById(R.id.txtInvoiceNumber);
        tvTransactionType = (TextView) findViewById(R.id.txtPaymentMode);
        temp_invoice = getIntent().getExtras().getString("temp_invoice");
        address = SessionData.getSessionDataInstance().getBluetoothAddress();
        amount = getIntent().getExtras().getString("amount");
        tvInvoice.setText(temp_invoice);
        tvTransactionType.setText("By Cash");
        setTitle("Payment Success", false);
        String myFormat = "dd-MM-yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
        String currentDate = sdf.format(System.currentTimeMillis());
        custId = getIntent().getExtras().getString("custId");
        database = openOrCreateDatabase("digitalrupay", MODE_PRIVATE, null);
        Cursor c = database.rawQuery("select * from customersdata where cust_id='" + custId + "'", null);
        try {
            if (c.moveToFirst()) {
                do {
                    int i1 = c.getColumnIndex("first_name");
                    FirstName = c.getString(i1);
                    int i2 = c.getColumnIndex("last_name");
                    last_name = c.getString(i2);
                    int i3 = c.getColumnIndex("addr1");
                    addr1 = c.getString(i3);
                    int i4 = c.getColumnIndex("addr2");
                    addr2 = c.getString(i4);
                    int i5 = c.getColumnIndex("custom_customer_no");
                    custom_customer_no = c.getString(i5);
                    int i6 = c.getColumnIndex("mobile_no");
                    mobile_no = c.getString(i6);
                    int i7 = c.getColumnIndex("pending_amount");
                    pending_amount = c.getString(i7);
                    int i8 = c.getColumnIndex("city");
                    city = c.getString(i8);
                } while (c.moveToNext());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        database=openOrCreateDatabase("digitalrupay",MODE_PRIVATE,null);
        Cursor c1 = database.rawQuery("Select * from empdata", null);
        try {
            if (c1.moveToFirst()) {
                do {
                    int i1 = c1.getColumnIndex("TodaysCollection");
                    TodaysCollection = c1.getString(i1);
                    int i2 = c1.getColumnIndex("total_collections");
                    total_collections = c1.getString(i2);
                    int i3 = c1.getColumnIndex("total_outstaning");
                    total_outstaning = c1.getString(i3);
                    int i4=c1.getColumnIndex("SERVICE_MOBILE_NUMBER");
                    SERVICE_MOBILE_NUMBER=c1.getColumnName(i4);
                    int i5=c1.getColumnIndex("SERVICES_name");
                    SERVICES_name=c1.getColumnName(i5);
                    int i6=c1.getColumnIndex("address1");
                    Adderss=c1.getColumnName(i6);

                } while (c1.moveToNext());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        Integer paidAmt = Integer.parseInt(amount);
        Integer calTodaysCollection = Integer.parseInt(TodaysCollection);
        int addTodaysCollection=calTodaysCollection+paidAmt;

        Integer calTotalcollections = Integer.parseInt(total_collections);
        int addTotalcollections=calTotalcollections+paidAmt;

        Integer calTotaloutstaning = Integer.parseInt(total_outstaning);
        int addTotaloutstaning=calTotaloutstaning-paidAmt;

        database.execSQL("UPDATE empdata SET total_outstaning='"+addTotaloutstaning+"',total_collections='"+addTotalcollections+"',TodaysCollection='"+addTodaysCollection+"'");

        custAddress=addr1+"/n"+addr2;
            Integer pendingAmt = Integer.parseInt(pending_amount);
            if (pendingAmt > 0) {

                if (pendingAmt < paidAmt) {
                    balance = (paidAmt - pendingAmt) + "";
                } else {
                    balance = (pendingAmt - paidAmt) + "";
                }
            } else if (pendingAmt < 0) {
                if (pendingAmt < paidAmt) {
                    balance = "-" + (paidAmt - pendingAmt) + "";
                } else {
                    balance = "-" + (pendingAmt - paidAmt) + "";
                }
            }
        database.execSQL("UPDATE customersdata SET pending_amount='"+balance+"' WHERE cust_id='" + custId + "'");
            String paymentMode = null;
//        if (customerPaymentSuccessModel.getTransaction_type().equalsIgnoreCase("1")) {
            paymentMode = "By Cash";
//        } else {
//            paymentMode = "By Cheque";
//        }
            EmployeeDataModel employeeDataModel;
            if (loginType.equals(loginTypes[4])) {
                String amount;
                String getspiPaymentFor = getIntent().getExtras().getString("getspiPaymentFor");
                if (getspiPaymentFor.equalsIgnoreCase("2")) {
                    amount = "Maintenance";
                } else {
                    amount = getspiPaymentFor;
                }
                employeeDataModel = SessionData.getSessionDataInstance().getApartmentLoginResult();
                if (amount.equalsIgnoreCase("Maintenance")) {
                    customerData = "     MONTHLY BILL      " +
                            "\n------------------------\n " +
                            SERVICES_name + "\n" +
                            " " + SERVICE_MOBILE_NUMBER + "\n" +
                            " " + Adderss + "\n" +
//                "\n ----------------------- \n "+
                            " Date : " + currentDate +
                            "\n------------------------\n" +
                            "Cust.No : " + custom_customer_no +
                            "\nName    : " + FirstName + " " + last_name +
                            "\nAddr    : " + addr1 +
                            "\nMobile  : " + mobile_no +
                            "\n\nCurrent Due  : " + pendingAmt +
                            "\n\n------------------------" +
                            "  " + amount +
                            "\n Amount paid  : " + amount
                            + "\n Outstanding  : " + balance +
                            "\n------------------------\n" +
                            "\n      Receipt No : \n" + temp_invoice +
                            "\n\n       " + employeeDataModel.getEmp_first_name() + " " + employeeDataModel.getEmp_last_name() + "\n" +
                            "       " + employeeDataModel.getEmp_mobile_no() + "\n\n\n\n\n\n" +
                            "\n";
                } else {
                    customerData = "        RECEIPT     " +
                            "\n------------------------\n " +
                            SERVICES_name + "\n" +
                            " " + SERVICE_MOBILE_NUMBER + "\n" +
                            " " + Adderss + "\n" +
//                "\n ----------------------- \n "+
                            " Date : " + currentDate +
                            "\n------------------------\n" +
                            "Flat No : " + custom_customer_no +
                            "\nName    : " + FirstName + " " + last_name +
                            "\nAddr    : " + custAddress +
                            "\nMobile  : " + mobile_no +
                            "\n\n------------------------" +
                            amount.toUpperCase() +
                            "\n Amount paid  : " + amount +
                            "\n------------------------\n" +
                            "      Receipt No : \n" + temp_invoice +
                            "\n\n       " + employeeDataModel.getEmp_first_name() + " " + employeeDataModel.getEmp_last_name() + "\n" +
                            "       " + employeeDataModel.getEmp_mobile_no() + "\n\n\n\n\n\n" +
                            "\n";
                }
            } else {
                employeeDataModel = SessionData.getSessionDataInstance().getEmployeeLoginResult();
                customerData = "     MONTHLY BILL      " +
                        "\n------------------------\n " +
                        SERVICES_name + "\n" +
                        " " + SERVICE_MOBILE_NUMBER + "\n" +
                        " " + Adderss + "\n" +
//                "\n ----------------------- \n "+
                        " Date : " + currentDate +
                        "\n------------------------\n" +
                        "Cust.No : " + custom_customer_no +
                        "\nName    : " + FirstName + " " + last_name +
                        "\nAddr    : " + custAddress +
                        "\nMobile  : " + mobile_no +
                        "\n\nCurrent Due  : " + pendingAmt +
                        "\n\n------------------------" +
                        "\n Amount paid  : " + amount
                        + "\n Outstanding  : " + balance +
                        "\n------------------------\n" +
                        "      Receipt No : \n" + temp_invoice +
                        "\n\n   " + employeeDataModel.getEmp_first_name() + " " + employeeDataModel.getEmp_last_name() + "\n" +
                        "     " + employeeDataModel.getEmp_mobile_no() + "\n\n\n\n\n\n" +
                        "\n";
            }
        }


    public void redirectToCustomers(View view) {
//        Intent customers = new Intent(this, Menu_2ST_Activity.class);
//        customers.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(customers);
        finish();
    }

    public void printInvoice(View view){
//        CustomerDataModel customerDataModel = ((CustomerDataModel) getIntent().getExtras().getSerializable(WsUrlConstants.CUSTOMER_DATA));
//        Intent customers = new Intent(this, Menu_2ST_Activity.class);
//        Bundle bundle = new Bundle();
//        bundle.putSerializable(WsUrlConstants.CUSTOMER_DATA, customerDataModel);
//        bundle.putSerializable(WsUrlConstants.CUSTOMER_PAYMENT, customerPaymentSuccessModel);
//        customers.putExtras(bundle);
//        startActivity(customers);
//        finish();
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                PaymentSuccessActivity.this);

        // set title
        alertDialogBuilder.setTitle("Print Invoice");

        // set dialog message
        alertDialogBuilder
                .setMessage("Do You Want to Print")
                .setCancelable(false)
                .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        try {
                            SessionData.saveBluetoothAddress(address);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            conn.openBT(address);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        Bluetooth_Printer_2inch_ThermalAPI printer = new Bluetooth_Printer_2inch_ThermalAPI();
                        String data = "";
                        data = customerData;
                        String printdata = printer.font_Courier_24(data);
                        conn.printData(printdata);
                    }
                })
                .setNegativeButton("No",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

    }
    @Override
    public void onPause() {
        super.onPause();
        try {
            if(conn!=null) {
                conn.closeBT();
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void onBackPressed() {
        return;
    }
}
