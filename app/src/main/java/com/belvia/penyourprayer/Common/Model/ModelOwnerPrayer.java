package com.belvia.penyourprayer.Common.Model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by sisgks on 19/10/2015.
 */
public class ModelOwnerPrayer {

    public String PrayerID = "";
    public Date CreatedWhen = null;
    public Date TouchedWhen = null;
    public String Content = "";
    public boolean publicView = false;
    public boolean ServerSent = false;
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
        SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy hh:mm:ss aa");
        return format.format(CreatedWhen);
    }

    public String formattedTouchedWhen(){
        SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy hh:mm:ss aa");
        return format.format(TouchedWhen);
    }

    public String toDBFormattedTouchedWhen(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        return format.format(TouchedWhen);
    }

    public String toDBFormattedCreatedWhen(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        return format.format(CreatedWhen);
    }
}
