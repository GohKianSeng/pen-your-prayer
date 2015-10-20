package com.penyourprayer.penyourprayer.Common;

/**
 * Created by sisgks on 20/10/2015.
 */
public class UserLoginModel {

    public enum LoginType {Email, Facebook, Twitter, GooglePlus};
    public LoginType loginType;
    public String ID;
    public String password;
    public String Name;
    public String URLPictureProfile;
    public String accessToken;
    public String accessSecret;
    public String GoogleCloudMessagingDeviceID;
    public UserLoginModel(){}
}
