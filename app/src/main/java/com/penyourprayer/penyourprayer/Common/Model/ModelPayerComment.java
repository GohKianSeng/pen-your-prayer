package com.penyourprayer.penyourprayer.Common.Model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ModelPayerComment {

    public String CommentID;
    public String OwnerPrayerID;
    public String WhoID;
    public String WhoName;
    public String WhoProfilePicture;
    public String Comment;
    public Date CreatedWhen;
    public Date TouchedWhen;
    public boolean ServerSent;

    public ModelPayerComment(){}

    public String formattedCreatedWhen(){
        SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy hh:mm:ss aa");
        return format.format(CreatedWhen);
    }

    public String formattedTouchedWhen(){
        SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy hh:mm:ss aa");
        return format.format(TouchedWhen);
    }

}
