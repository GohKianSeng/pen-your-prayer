package com.belvia.penyourprayer.Common.Model;

import com.belvia.penyourprayer.QuickstartPreferences;
import com.belvia.penyourprayer.UI.MainActivity;

import java.io.File;
import java.io.Serializable;

public class ModelPrayerAttachement implements Serializable {

    public String PrayerID;
    public String OriginalFilePath;
    public String GUID;
    public String FileName;
    public String UserID;

    public String getAvailableURI(MainActivity mainactivity){
        File ls = new File(OriginalFilePath.substring(7));
        if(ls.exists()) {
            return OriginalFilePath;
        }
        else{
            return QuickstartPreferences.api_server + "/api/attachment/DownloadPrayerAttachment?AttachmentID=" + GUID + "&UserID=" + mainactivity.OwnerID;

        }
    }

    public ModelPrayerAttachement(){}

}
