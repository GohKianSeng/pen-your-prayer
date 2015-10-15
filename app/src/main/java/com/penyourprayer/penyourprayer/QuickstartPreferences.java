package com.penyourprayer.penyourprayer;

/**
 * Created by sisgks on 31/08/2015.
 */
public class QuickstartPreferences {

    //Google cloud messaging id
    public static final String gcm_defaultSenderId = "1036182018589";

    //Broadcast message
    public static final String BroadcastMessage = "BroadcastMessage";
    public static final String BroadcastMessageTopic = "BroadcastMessageTopic";
    public static final String BroadcastMessageTopic_ReceiveTokenFromGCM = "BroadcastMessageTopic_ReceiveTokenFromGCM";
    public static final String BroadcastMessageTopic_ReceiveVerificationFromServer = "BroadcastMessageTopic_ReceiveVerificationFromServer";

    //token received from GCM
    public static final String DeviceRegistrationToken = "DeviceRegistrationToken";

    //Verification GUID received from server
    public static final String VerificationGUID = "VerificationGUID";

    //PenYourPrayer Status
    public static final String AppStatus = "AppStatus";
    public static final String AppStatusNew = "AppStatusNew";
    public static final String AppStatusSignedIn = "AppStatusSignedIn";

    //User information
    public static final String Email = "Email";
    public static final String Name = "Name";
    public static final String Password = "Password";
    public static final String Cookie = "Cookie";

    //Web url
    public static final String api_server = "http://99.99.99.101/";
    public static final String RegisterNewUser = "/Account/RegisterNewUser";
    public static final String VerifyUserDevice = "/Account/VerifyUserDevice";
    public static final String GCMTokenRefresh = "/Account/GCMTokenRefresh";

}
