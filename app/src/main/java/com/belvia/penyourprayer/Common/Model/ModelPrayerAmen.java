package com.belvia.penyourprayer.Common.Model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class ModelPrayerAmen implements Serializable {

    public String AmenID;
    public String WhoID;
    public String WhoName;
    public String WhoProfilePicture;
    public long CreatedWhen;

    public ModelPrayerAmen(){}

}
