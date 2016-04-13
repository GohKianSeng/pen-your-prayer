package com.belvia.penyourprayer.Common.Model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class ModelPrayerAnswered implements Serializable {

    public String AnsweredID;
    public String OwnerPrayerID;
    public String WhoID;
    public String WhoName;
    public String WhoProfilePicture;
    public String Answered;
    public long CreatedWhen;
    public long TouchedWhen;
    public int InQueue;

    public ModelPrayerAnswered(){}

}
