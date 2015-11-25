package com.penyourprayer.penyourprayer.Common;

import android.graphics.Bitmap;

public class FriendProfileModel{

    public enum ActionName {
        Logout, Settings
    }

    public ActionName actionName;
    public boolean isAction = false;
    public String DisplayName, ProfilePictureURL;
    public boolean selected = false;
    public String UserID = "";

    public FriendProfileModel(String UserID, String name, String img_url, boolean selected){
        this.UserID = UserID;
        this.DisplayName = name;
        this.selected = selected;
        this.ProfilePictureURL = img_url;
    }

    public FriendProfileModel(ActionName actionName){
        this.actionName = actionName;
        this.isAction = true;
    }

}
