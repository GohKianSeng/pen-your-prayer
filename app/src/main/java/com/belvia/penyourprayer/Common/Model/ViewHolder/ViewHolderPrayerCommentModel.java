package com.belvia.penyourprayer.Common.Model.ViewHolder;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by sisgks on 07/10/2015.
 */
public class ViewHolderPrayerCommentModel {

    public TextView touchedwhen_textView, displayname_textview, comment_textview;
    public ImageView profilePicture_imageView;
    public ProgressBar progressBar;
    public String CommentID;

    public View OwnerLayout;
    public TextView edit_imageButton;
    public TextView delete_imageButton;

    public ViewHolderPrayerCommentModel(){

    }


}
