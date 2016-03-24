package com.belvia.penyourprayer.Common.Model;

public class ModelFriendProfile {

    public enum ActionName {
        Logout, Settings
    }

    public ActionName actionName;
    public boolean isAction = false;
    public String DisplayName, ProfilePictureURL;
    public boolean selected = false;
    public String UserID = "";

    public ModelFriendProfile(String UserID, String name, String img_url, boolean selected){
        this.UserID = UserID;
        this.DisplayName = name;
        this.selected = selected;
        this.ProfilePictureURL = img_url;
    }

    public ModelFriendProfile(String UserID){
        this.UserID = UserID;
    }

    public ModelFriendProfile(ActionName actionName){
        this.actionName = actionName;
        this.isAction = true;
    }

}