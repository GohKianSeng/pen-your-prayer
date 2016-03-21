package com.belvia.penyourprayer.Common.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by sisgks on 20/10/2015.
 */
public class ModelUserLogin {

    public enum LoginType {Email, Facebook, Twitter, GooglePlus};

    @SerializedName("LoginType")
    @Expose
    public LoginType loginType;

    @SerializedName("UserName")
    @Expose
    public String UserName;

    @SerializedName("Id")
    @Expose
    public long ID;

    public String password_secret;

    @SerializedName("DisplayName")
    @Expose
    public String Name;

    @SerializedName("ProfilePictureURL")
    @Expose
    public String URLPictureProfile;

    public String accessToken = "";

    public String GoogleCloudMessagingDeviceID;

    @SerializedName("Country")
    @Expose
    public String Country;

    @SerializedName("City")
    @Expose
    public String City;

    @SerializedName("Region")
    @Expose
    public String Region;

    @SerializedName("HMACHashKey")
    @Expose
    public String HMACHashKey;

    @SerializedName("EmailVerification")
    @Expose
    public boolean EmailVerification;

    @SerializedName("SocialMediaEmail")
    @Expose
    public String SocialMediaEmail;

    public ModelUserLogin(){}
}


