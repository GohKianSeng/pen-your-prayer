package com.penyourprayer.penyourprayer.Common;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by sisgks on 19/10/2015.
 */
public class OwnerPrayerModel {

    public String GUID = "";
    public Date CreatedWhen = null;
    public Date TouchedWhen = null;
    public String Content = "";
    public boolean publicView = false;
    public boolean ServerSent = false;
    public boolean deleted = false;
    public int NumberOfFriendsTag = 0;

    public ArrayList<FriendProfileModel> selectedFriends = new ArrayList<FriendProfileModel>();

    public OwnerPrayerModel(){}
}
