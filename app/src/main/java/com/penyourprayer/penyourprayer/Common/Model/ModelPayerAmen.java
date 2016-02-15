package com.penyourprayer.penyourprayer.Common.Model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class ModelPayerAmen {

    public String AmenID;
    public String WhoID;
    public String WhoName;
    public String WhoProfilePicture;
    public Date CreatedWhen;
    public Date TouchedWhen;

    public ModelPayerAmen(){}

    public String formattedCreatedWhen(){
        SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy hh:mm:ss aa");
        return format.format(CreatedWhen);
    }

    public String formattedTouchedWhen(){
        SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy hh:mm:ss aa");
        return format.format(TouchedWhen);
    }

    public String toDBFormattedTouchedWhen(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        return format.format(TouchedWhen);
    }

    public String toDBFormattedCreatedWhen(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        return format.format(CreatedWhen);
    }

}