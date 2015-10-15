package com.penyourprayer.penyourprayer.Common;

import android.graphics.Bitmap;

public class FriendProfileModel{
    public String name, img_url;
    public boolean selected = false;


    public FriendProfileModel(String name, String img_url, boolean selected){
        this.name = name;
        this.selected = selected;
        this.img_url = img_url;
    }

}
