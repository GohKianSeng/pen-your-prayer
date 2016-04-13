package com.belvia.penyourprayer.Common.Model;

import com.belvia.penyourprayer.Common.Utils;
import com.belvia.penyourprayer.QuickstartPreferences;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by sisgks on 19/10/2015.
 */
public class ModelOwnerPrayer implements Serializable {

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

    public String IfExecutedGUID;

    public ModelOwnerPrayer(){}

    public String formattedCreatedWhen(){
        return Utils.UnixTimeReadableString(CreatedWhen);
    }
}
