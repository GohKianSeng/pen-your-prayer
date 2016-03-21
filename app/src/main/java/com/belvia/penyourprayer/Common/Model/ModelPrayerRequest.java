package com.belvia.penyourprayer.Common.Model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class ModelPrayerRequest {

    public ArrayList<ModelPrayerRequestAttachement> attachments = new ArrayList<ModelPrayerRequestAttachement>();
    public String PrayerRequestID;
    public String Subject;
    public String Description;
    public boolean Answered;
    public Date AnsweredWhen;
    public String AnswerComment;
    public Date CreatedWhen;
    public Date TouchedWhen;

    public ModelPrayerRequest(){}

    public ModelPrayerRequest(String ID, String subject, String desc, boolean ans, Date ansWhen, String answerComment, Date createdWhen, Date TouchedWhen){
        this.PrayerRequestID = ID;
        this.Subject = subject;
        this.Description = desc;
        this.Answered = ans;
        this.AnsweredWhen = ansWhen;
        this.CreatedWhen = createdWhen;
        this.TouchedWhen = TouchedWhen;
        this.AnswerComment = answerComment;
    }

    public String formattedAnsweredWhen(){
        SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy hh:mm:ss aa");
        return format.format(AnsweredWhen);
    }

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
