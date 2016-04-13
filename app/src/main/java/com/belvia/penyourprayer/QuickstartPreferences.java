package com.belvia.penyourprayer;

/**
 * Created by sisgks on 31/08/2015.
 */
public class QuickstartPreferences {

    public static final int thumbnailDPsize = 70;

    public static final String DefaultTimeFormat = "yyyy-MM-dd'T'HH:mm:sszzz";
    public static final String DefaultReadableTimeFormat = "dd MMM yyyy hh:mm:ss aa";

    //OwnerGUID
    public static final String OwnerLoginType = "OwnerLoginType";
    public static final String OwnerUserName = "OwnerUserName";
    public static final String OwnerID = "OwnerID";
    public static final String OwnerDisplayName = "OwnerDisplayName";
    public static final String OwnerProfilePictureURL = "OwnerProfilePictureURL";
    public static final String OwnerHMACKey = "OwnerHMACKey";

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

    //RandomDeviceID
    public static final String RandomUserID = "RandomUserID";
    public static final String AnonymousHMACKey = "sSLPIZ4XmZRNEwDAQxY0yuC+MeSQVzxXJNbZTgAlRF/zAocJlTU4WPOjAXmQwbZ+XhkoYGS8exqzUKhxqCfY4SXOJTB+GG5R";

    //Web url
    public static final String api_server = "http://pyptesting.com/";
    public static final String locale_server = "https://www.telize.com/";

    //upload image standard
    public static final int Dimension = 3840;
    public static final int Quality = 10;

    //create new prayer auto save content
    public static final String DraftNewPrayer = "DraftNewPrayer";

    //
    public static final String DraftNewPrayerRequest = "DraftNewPrayerRequest";
    public static final String DraftNewPrayerRequestSubject = "DraftNewPrayerRequestSubject";
    public static final String DraftNewPrayerRequestAttachment = "DraftNewPrayerRequestAttachment";
}
