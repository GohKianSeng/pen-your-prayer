package com.penyourprayer.penyourprayer.Common.Model;

import com.penyourprayer.penyourprayer.QuickstartPreferences;
import com.penyourprayer.penyourprayer.UI.MainActivity;

import java.io.File;

public class ModelPrayerAttachement {

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
