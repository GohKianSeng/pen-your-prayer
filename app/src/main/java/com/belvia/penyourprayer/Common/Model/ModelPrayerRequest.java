package com.belvia.penyourprayer.Common.Model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class ModelPrayerRequest implements Serializable {

    public ArrayList<ModelPrayerRequestAttachement> attachments = new ArrayList<ModelPrayerRequestAttachement>();
    public String PrayerRequestID;
    public String Subject;
    public String Description;
    public boolean Answered;
    public long AnsweredWhen;
    public String AnswerComment;
    public long CreatedWhen;
    public long TouchedWhen;
    public int InQueue = 0;

    public ModelPrayerRequest(){}

    public ModelPrayerRequest(String ID, String subject, String desc, boolean ans, long ansWhen, String answerComment, int InQueue, long createdWhen, long TouchedWhen){
        this.PrayerRequestID = ID;
        this.Subject = subject;
        this.Description = desc;
        this.Answered = ans;
        this.AnsweredWhen = ansWhen;
        this.CreatedWhen = createdWhen;
        this.TouchedWhen = TouchedWhen;
        this.AnswerComment = answerComment;
        this.InQueue = InQueue;
    }
}
