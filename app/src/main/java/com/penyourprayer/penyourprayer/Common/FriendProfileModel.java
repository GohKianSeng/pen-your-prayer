package com.penyourprayer.penyourprayer.Common;

import android.graphics.Bitmap;

public class FriendProfileModel{

    public enum ActionName {
        Logout, Settings
    }

    public ActionName actionName;
    public boolean isAction = false;
    public String name, img_url;
    public boolean selected = false;
    public String GUID = "";

    public FriendProfileModel(String GUID, String name, String img_url, boolean selected){
        this.GUID = GUID;
        this.name = name;
        this.selected = selected;
        this.img_url = img_url;
    }

    public FriendProfileModel(ActionName actionName){
        this.actionName = actionName;
        this.isAction = true;
    }

}
