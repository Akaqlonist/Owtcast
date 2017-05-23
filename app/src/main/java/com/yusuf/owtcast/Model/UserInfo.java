package com.yusuf.owtcast.Model;

import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by DragonWarrior on 1/29/2017.
 */

public class UserInfo {

    @SerializedName("error")
    public boolean error;

    @SerializedName("message")
    public String message;

    @SerializedName("firstname")
    public String firstName;

    @SerializedName("lastname")
    public String lastName;

    @SerializedName("DriverID")
    public String driverID;

    @SerializedName("DriverMobile")
    public String driverMobile;

    @SerializedName("DriverEmail")
    public String driverEmail;

    @SerializedName("DriverCity")
    public String driverCity;

    @SerializedName("DriverRef")
    public String driverRef;

    @SerializedName("BankName")
    public String bankName;

    @SerializedName("BankAccount")
    public String bankAccount;

    @SerializedName("DriverType")
    public String driverType;

    @SerializedName("DriverApi")
    public String driverApi;

    @SerializedName("DriverStatus")
    public String driverStatus;

    public UserInfo(boolean error , String firstName, String lastName, String driverID, String driverMobile, String driverEmail, String driverCity, String driverRef, String bankName, String bankAccount, String driverType, String driverApi, String driverStatus)
    {
        this.error = error;
        this.firstName = firstName;
        this.lastName = lastName;
        this.driverID = driverID;
        this.driverMobile = driverMobile;
        this.driverEmail = driverEmail;
        this.driverCity = driverCity;
        this.driverRef = driverRef;
        this.bankName = bankName;
        this.bankAccount = bankAccount;
        this.driverType = driverType;
        this.driverApi = driverApi;
        this.driverStatus = driverStatus;
    }
}
