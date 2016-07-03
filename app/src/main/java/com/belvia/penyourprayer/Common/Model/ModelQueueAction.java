package com.belvia.penyourprayer.Common.Model;

import com.belvia.penyourprayer.Common.Utils;

/**
 * Created by sisgks on 20/11/2015.
 */
public class ModelQueueAction {

    public enum ItemType {Prayer, PrayerPublicView, PrayerTagFriends, PrayerContent, PrayerComment, PrayerCommentReply, PrayerAnswered, PrayerAmen, PrayerRequest };

    public enum ActionType {Insert, Update, Delete };

    public int ID;
    public ActionType Action;
    public ItemType Item;
    public String ItemID;
    public String IfExecutedGUID;
    public long createdWhen;

    public String formattedCreatedWhen(){
        return Utils.UnixTimeReadableString(createdWhen);
    }

    public ModelQueueAction(){}
}
