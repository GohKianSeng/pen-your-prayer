package com.penyourprayer.penyourprayer.Common.Model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
    public boolean ownerAmen = false;
    public ArrayList<ModelPrayerAttachement> attachments = new ArrayList<ModelPrayerAttachement>();
    public ArrayList<ModelFriendProfile> selectedFriends = new ArrayList<ModelFriendProfile>();
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
}