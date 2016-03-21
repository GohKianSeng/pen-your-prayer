package com.belvia.penyourprayer.Common.Model.ViewHolder;

import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by sisgks on 07/10/2015.
 */
public class ViewHolderPrayerRequestModel {

    public TextView subject_TextView, answered_TextView, createdwhen_TextView;
    public String PrayerRequestID;
    public boolean containAttachment = false;
    public ImageView image1, image2, image3, image4, image5;


    public ViewHolderPrayerRequestModel(){

    }
}
