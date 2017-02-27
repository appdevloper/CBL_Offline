package com.digitalrupay.datamodels;

import java.io.Serializable;

/**
 * Created by sridhar on 1/23/2017.
 */

public class EmpCustomerPaymentModel{
    String FirstName,last_name,addr1,addr2,custom_customer_no,mobile_no,pending_amount,city,cust_id;
    public String getFirstName() {
        return FirstName;
    }
    public void setFirstName(String first_name) {
        this.FirstName = first_name;
    }

    public String getlast_name() {
        return last_name;
    }
    public void setlast_name(String emp_last_name) {
        this.last_name = emp_last_name;
    }

    public String getaddr1() {
        return addr1;
    }
    public void setaddr1(String addr1) {
        this.addr1 = addr1;
    }

    public String getaddr2() {
        return addr2;
    }
    public void setaddr2(String addr2) {
        this.addr2 = addr2;
    }

    public String getcustom_customer_no() {
        return custom_customer_no;
    }
    public void setcustom_customer_no(String custom_customer_no) {
        this.custom_customer_no = custom_customer_no;
    }

    public String getmobile_no() {
        return mobile_no;
    }
    public void setmobile_no(String mobile_no) {
        this.mobile_no = mobile_no;
    }

    public String getpending_amount() {
        return pending_amount;
    }
    public void setpending_amount(String pending_amount) {
        this.pending_amount = pending_amount;
    }

    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }

    public String getCust_id() {
        return cust_id;
    }
    public void setCust_id(String cust_id) {
        this.cust_id = cust_id;
    }

}
