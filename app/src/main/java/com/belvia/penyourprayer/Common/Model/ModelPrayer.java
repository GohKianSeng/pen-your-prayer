package com.belvia.penyourprayer.Common.Model;

import com.avocarrot.androidsdk.CustomModel;
import com.belvia.penyourprayer.Common.Utils;
import com.belvia.penyourprayer.QuickstartPreferences;
import com.facebook.ads.NativeAd;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by sisgks on 19/10/2015.
 */
public class ModelPrayer implements Serializable {

    public String UserID = "";
    public String PrayerID = "";
    public long CreatedWhen = 0;
    public long TouchedWhen = 0;
    public String Content = "";
    public boolean publicView = false;
    public int InQueue = 0;
    public boolean deleted = false;
    public long numberOfFriendsTag = 0;
    public long numberOfAmen = 0;
    public long numberOfComment = 0;
    public long numberOfAnswered = 0;
    public String Answered = "";
    public boolean ownerAmen = false;
    public ArrayList<ModelPrayerAttachement> attachments = new ArrayList<ModelPrayerAttachement>();
    public ArrayList<ModelFriendProfile> selectedFriends = new ArrayList<ModelFriendProfile>();
    public ArrayList<ModelPrayerComment> comments = new ArrayList<ModelPrayerComment>();
    public ArrayList<ModelPrayerAnswered> answers = new ArrayList<ModelPrayerAnswered>();
    public ArrayList<ModelPrayerAmen> amen = new ArrayList<ModelPrayerAmen>();

    public transient boolean isNativeAd = false;
    public enum AdNetwork {Facebook, Avocarrot };
    public transient AdNetwork adNetwork;
    public transient NativeAd facebook_nativeAd;
    public transient CustomModel avocarrot_nativeAds;


    public String IfExecutedGUID;

    public ModelPrayer(){}

    public String formattedCreatedWhen(){
        return Utils.UnixTimeReadableString(CreatedWhen);
    }
}
