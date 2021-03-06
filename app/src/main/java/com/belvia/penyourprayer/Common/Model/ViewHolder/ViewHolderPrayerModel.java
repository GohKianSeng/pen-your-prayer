package com.belvia.penyourprayer.Common.Model.ViewHolder;

import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.ads.MediaView;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by sisgks on 07/10/2015.
 */
public class ViewHolderPrayerModel {

    public TextView prayer_textView, createdwhen_textview, amen_count_textview;
    public ImageButton amen_imageButton, comment_imageButton, tagfriend_imageButton, publicView_imageButton, answered_imageButton, delete_imageButton;
    public LinearLayout thumbnailHorizontalView;
    public ProgressBar progressBar;
    public boolean isPrayerAnswered = false;
    public boolean containAttachment = false;
    public ImageButton image1, image2, image3, image4, image5;
    public CircleImageView profileImage;
    public TextView profileName;

    public ExpandableTextView expandableTextView;
    public String PrayerID;


    /*
    *
    * for Native ads
    *
    * */

    public ImageView nativeAdIcon;
    public TextView nativeAdTitle;
    public TextView nativeAdBody;
    public MediaView nativeAdMedia;
    public TextView nativeAdSocialContext;
    public Button nativeAdCallToAction;

    public ViewHolderPrayerModel(){

    }


}
