package com.penyourprayer.penyourprayer.Common.Model.ViewHolder;

import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ms.square.android.expandabletextview.ExpandableTextView;

/**
 * Created by sisgks on 07/10/2015.
 */
public class ViewHolderPrayerModel {

    public TextView prayer_textView, createdwhen_textview, serversent_textview, amen_count_textview;
    public ImageButton amen_imageButton, comment_imageButton, tagfriend_imageButton, publicView_imageButton, answered_imageButton, delete_imageButton;
    public LinearLayout thumbnailHorizontalView;
    //public ArrayList<ModelPrayerAttachement> att;
    public boolean isPrayerAnswered = false;
    public boolean containAttachment = false;
    public ImageButton image1, image2, image3, image4, image5;

    public ExpandableTextView expandableTextView;
    public String PrayerID;

    public ViewHolderPrayerModel(){

    }


}
