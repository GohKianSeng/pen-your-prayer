package com.belvia.penyourprayer.Common.Model;

import java.io.Serializable;

public class ModelPrayerCommentReply implements Serializable {

    public String CommentReplyID;
    public String MainCommentID;
    public String OwnerPrayerID;
    public String WhoID;
    public String WhoName;
    public String WhoProfilePicture;
    public String CommentReply;
    public long CreatedWhen;
    public long TouchedWhen;
    public int InQueue;

    public ModelPrayerCommentReply(){}


}
