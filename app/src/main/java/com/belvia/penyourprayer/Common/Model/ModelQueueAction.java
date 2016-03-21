package com.belvia.penyourprayer.Common.Model;

/**
 * Created by sisgks on 20/11/2015.
 */
public class ModelQueueAction {

    public enum ItemType {Prayer, PrayerPublicView, PrayerTagFriends, PrayerContent, PrayerComment, PrayerAnswered, PrayerAmen, PrayerRequest };

    public enum ActionType {Insert, Update, Delete };

    public int ID;
    public ActionType Action;
    public ItemType Item;
    public String ItemID;
    public String IfExecutedGUID;
    public ModelQueueAction(){}
}
