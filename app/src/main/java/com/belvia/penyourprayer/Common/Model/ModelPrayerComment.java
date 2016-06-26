package com.belvia.penyourprayer.Common.Model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class ModelPrayerComment implements Serializable {

    public ArrayList<ModelPrayerCommentReply> commentReply = new ArrayList<ModelPrayerCommentReply>();
    public String CommentID;
    public String OwnerPrayerID;
    public String WhoID;
    public String WhoName;
    public String WhoProfilePicture;
    public String Comment;
    public long CreatedWhen;
    public long TouchedWhen;
    public int InQueue;

    public ModelPrayerComment(){}


}
