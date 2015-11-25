package com.penyourprayer.penyourprayer.Database;

/**
 * Created by sisgks on 20/11/2015.
 */
public class QueueAction {

    public enum ItemType {Prayer, PrayerPublicView, PrayerTagFriends, PrayerContent };

    public enum ActionType {Insert, Update, Delete };

    public int ID;
    public ActionType Action;
    public ItemType Item;
    public String ItemID;
    public String IfExecutedGUID;
    public QueueAction(){}
}
