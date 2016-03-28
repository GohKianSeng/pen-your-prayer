package com.belvia.penyourprayer.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.belvia.penyourprayer.Common.Model.ModelFriendProfile;
import com.belvia.penyourprayer.Common.Model.ModelOwnerPrayer;
import com.belvia.penyourprayer.Common.Model.ModelPrayerAmen;
import com.belvia.penyourprayer.Common.Model.ModelPrayerAnswered;
import com.belvia.penyourprayer.Common.Model.ModelPrayerAttachement;
import com.belvia.penyourprayer.Common.Model.ModelPrayerComment;
import com.belvia.penyourprayer.Common.Model.ModelPrayerRequest;
import com.belvia.penyourprayer.Common.Model.ModelPrayerRequestAttachement;
import com.belvia.penyourprayer.Common.Model.ModelQueueAction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

/**
 * Created by sisgks on 21/09/2015.
 */
public class Database extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 47;

    // Database Name
    private static final String DATABASE_NAME = "PenYourPrayerSQLite";
    private static final String tb_Friends = "CREATE TABLE tb_Friends ('UserID' TEXT NOT NULL, 'OwnerID' TEXT NOT NULL, 'DisplayName' TEXT NOT NULL, 'ProfilePictureURL' TEXT, PRIMARY KEY(UserID, OwnerID))";
    private static final String tb_OwnerPrayerAmen = "CREATE TABLE tb_OwnerPrayerAmen ('OwnerPrayerID' TEXT NOT NULL,'WhoID' TEXT NOT NULL,'WhoName' TEXT NOT NULL,'WhoProfilePicture' TEXT, 'CreatedWhen' DATETIME DEFAULT (DATETIME('NOW','LOCALTIME')), PRIMARY KEY(OwnerPrayerID,WhoID))";
    private static final String tb_OwnerPrayerComment = "CREATE TABLE tb_OwnerPrayerComment (CommentID TEXT NOT NULL UNIQUE, OwnerPrayerID TEXT NOT NULL, WhoID TEXT NOT NULL, WhoName TEXT NOT NULL,WhoProfilePicture TEXT, Comment TEXT NOT NULL, ServerSent INTEGER NOT NULL DEFAULT 0, CreatedWhen DATETIME DEFAULT (DATETIME('NOW','LOCALTIME')), TouchedWhen DATETIME DEFAULT (DATETIME('NOW','LOCALTIME')), PRIMARY KEY(CommentID,OwnerPrayerID))";
    private static final String tb_OwnerPrayerAnswered = "CREATE TABLE tb_OwnerPrayerAnswered (AnsweredID TEXT NOT NULL UNIQUE, OwnerPrayerID TEXT NOT NULL, WhoID TEXT NOT NULL, WhoName TEXT NOT NULL,WhoProfilePicture TEXT, Answered TEXT NOT NULL, ServerSent INTEGER NOT NULL DEFAULT 0, CreatedWhen DATETIME DEFAULT (DATETIME('NOW','LOCALTIME')), TouchedWhen DATETIME DEFAULT (DATETIME('NOW','LOCALTIME')), PRIMARY KEY(AnsweredID,OwnerPrayerID))";
    private static final String tb_OwnerPrayerTagFriends = "CREATE TABLE tb_OwnerPrayerTagFriends ( OwnerPrayerID TEXT NOT NULL, WhoID TEXT NOT NULL, PRIMARY KEY(OwnerPrayerID,WhoID))";
    private static final String tb_ownerPrayer = "CREATE TABLE tb_ownerPrayer (PrayerID TEXT NOT NULL UNIQUE, CreatedWhen DATETIME NOT NULL DEFAULT (DATETIME('NOW','LOCALTIME')), TouchedWhen DATETIME NOT NULL DEFAULT (DATETIME('NOW','LOCALTIME')), Content TEXT NOT NULL, PublicView INTEGER NOT NULL DEFAULT 0, ServerSent INTEGER NOT NULL DEFAULT 0, Deleted INTEGER NOT NULL DEFAULT 0, PRIMARY KEY(PrayerID))";
    private static final String tb_QueueAction = "CREATE TABLE tb_QueueAction (ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, Action TEXT NOT NULL, Item TEXT NOT NULL, ItemID TEXT NOT NULL, IfExecutedGUID TEXT NOT NULL)";
    private static final String tb_OwnerPrayerAttachment = "CREATE TABLE tb_OwnerPrayerAttachment (OwnerPrayerID TEXT NOT NULL, GUID TEXT NOT NULL, UserID TEXT NOT NULL, OriginalFilePath TEXT NOT NULL, FileName TEXT NOT NULL, PRIMARY KEY(OwnerPrayerID,GUID))";
    private static final String tb_PrayerRequest = "CREATE TABLE tb_PrayerRequest (PrayerRequestID TEXT NOT NULL UNIQUE, Subject TEXT NOT NULL, Description TEXT, Answered NOT NULL DEFAULT 0, AnsweredWhen DATETIME, AnswerComment TEXT, CreatedWhen DATETIME DEFAULT (DATETIME('NOW','LOCALTIME')), TouchedWhen DATETIME DEFAULT (DATETIME('NOW','LOCALTIME')), PRIMARY KEY(PrayerRequestID))";
    private static final String tb_PrayerRequestAttachment = "CREATE TABLE tb_PrayerRequestAttachment (PrayerRequestID TEXT NOT NULL, GUID TEXT NOT NULL, UserID TEXT NOT NULL, OriginalFilePath TEXT NOT NULL, FileName TEXT NOT NULL, PRIMARY KEY(PrayerRequestID, GUID))";

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        try {
            openConnection();
        }catch(Exception e){
            String sdf = "";
            sdf = e.toString();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase database) {

        database.execSQL(tb_Friends);
        database.execSQL(tb_OwnerPrayerAmen);
        database.execSQL(tb_OwnerPrayerComment);
        database.execSQL(tb_OwnerPrayerTagFriends);
        database.execSQL(tb_ownerPrayer);
        database.execSQL(tb_QueueAction);
        database.execSQL(tb_OwnerPrayerAttachment);
        database.execSQL(tb_OwnerPrayerAnswered);
        database.execSQL(tb_PrayerRequest);
        database.execSQL(tb_PrayerRequestAttachment);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS tb_Friends");
        db.execSQL("DROP TABLE IF EXISTS tb_OwnerPrayerAmen");
        db.execSQL("DROP TABLE IF EXISTS tb_OwnerPrayerComment");
        db.execSQL("DROP TABLE IF EXISTS tb_OwnerPrayerAnswered");
        db.execSQL("DROP TABLE IF EXISTS tb_OwnerPrayerTagFriends");
        db.execSQL("DROP TABLE IF EXISTS tb_ownerPrayer");
        db.execSQL("DROP TABLE IF EXISTS tb_QueueAction");
        db.execSQL("DROP TABLE IF EXISTS tb_OwnerPrayerAttachment");
        db.execSQL("DROP TABLE IF EXISTS tb_PrayerRequest");
        db.execSQL("DROP TABLE IF EXISTS tb_PrayerRequestAttachment");
        onCreate(db);
    }

    public void openConnection(){
        this.getWritableDatabase();
    }

    /***********************************************
     *
     * clear Database
     *
     **********************************************/
    public void ClearData() {
        SQLiteDatabase db = getWritableDatabase();
        onUpgrade(db, 1, 2);
    }



    /***********************************************
     *
     * Queue Action section
     *
     **********************************************/
    public ArrayList<ModelQueueAction> getAllQueueItems(){
        ArrayList<ModelQueueAction> queue = new ArrayList<ModelQueueAction>();
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT ID, Action, Item, ItemID, IfExecutedGUID FROM tb_QueueAction ORDER BY ID ASC";
        Cursor c = db.rawQuery(query, new String[]{});
        while (c.moveToNext()) {
            ModelQueueAction o = new ModelQueueAction();
            o.ID = c.getInt(c.getColumnIndex("ID"));
            o.Action = ModelQueueAction.ActionType.valueOf(c.getString(c.getColumnIndex("Action")));
            o.Item = ModelQueueAction.ItemType.valueOf(c.getString(c.getColumnIndex("Item")));
            o.ItemID = c.getString(c.getColumnIndex("ItemID"));
            o.IfExecutedGUID = c.getString(c.getColumnIndex("IfExecutedGUID"));
            queue.add(o);
        }
        if(c != null)
            c.close();
        return queue;
    }

    public void deleteQueue(int QueueID){
        SQLiteDatabase db = getWritableDatabase();
        db.delete("tb_QueueAction", "ID = " + String.valueOf(QueueID), null);
    }

    /***********************************************
     *
     * Prayer Request section
     *
     **********************************************/
    public void DeletePrayerRequest(String PrayerRequestID){
        SQLiteDatabase db = getWritableDatabase();

        db.delete("tb_PrayerRequest", "PrayerRequestID = '" + PrayerRequestID + "'", null);
        db.delete("tb_PrayerRequestAttachment", "PrayerRequestID = '" + PrayerRequestID + "'", null);


        ContentValues cv = new ContentValues();
        cv.put("Action", ModelQueueAction.ActionType.Delete.toString());
        cv.put("Item", ModelQueueAction.ItemType.PrayerRequest.toString());
        cv.put("ItemID", PrayerRequestID);
        cv.put("IfExecutedGUID", UUID.randomUUID().toString());
        db.insert("tb_QueueAction", null, cv);
    }

    public void UpdatePrayerRequest(String PrayerRequestID, boolean answered, String answeredComment, String subject, String description) {
        SQLiteDatabase db = getWritableDatabase();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        String date =  format.format(new Date());


        ContentValues cv = new ContentValues();
        cv.put("Subject", "#" + subject);
        cv.put("Description", description);
        cv.put("TouchedWhen", date);
        cv.put("Answered", answered);
        if(answered) {
            cv.put("AnswerComment", answeredComment);
            cv.put("AnsweredWhen", date);

        }

        db.update("tb_PrayerRequest", cv, "PrayerRequestID = '" + PrayerRequestID + "'", null);

        cv = new ContentValues();
        cv.put("Action", ModelQueueAction.ActionType.Update.toString());
        cv.put("Item", ModelQueueAction.ItemType.PrayerRequest.toString());
        cv.put("ItemID", PrayerRequestID);
        cv.put("IfExecutedGUID", UUID.randomUUID().toString());
        db.insert("tb_QueueAction", null, cv);
    }

    public void AddNewPrayerRequest(String subject, String description, ArrayList<ModelPrayerRequestAttachement> attachment){
        SQLiteDatabase db = getWritableDatabase();
        long id = -1;
        String tUUID = "";
        while(id == -1) {
            tUUID = UUID.randomUUID().toString();
            ContentValues cv = new ContentValues();
            cv.put("PrayerRequestID", tUUID);
            cv.put("Subject", "#" + subject);
            cv.put("Description", description);

            id = db.insert("tb_PrayerRequest", null, cv);
        }
        if(attachment != null) {
            for (int x = 0; x < attachment.size(); x++) {
                ModelPrayerRequestAttachement f = attachment.get(x);
                long id2 = -1;
                while (id2 == -1) {
                    ContentValues cv = new ContentValues();
                    cv.put("PrayerRequestID", tUUID);
                    cv.put("GUID", f.GUID);
                    cv.put("OriginalFilePath", f.OriginalFilePath);
                    cv.put("FileName", f.FileName);
                    cv.put("UserID", f.UserID);
                    id2 = db.insert("tb_PrayerRequestAttachment", null, cv);
                }
            }
        }

        ContentValues cv = new ContentValues();
        cv.put("Action", ModelQueueAction.ActionType.Insert.toString());
        cv.put("Item", ModelQueueAction.ItemType.PrayerRequest.toString());
        cv.put("ItemID", tUUID);
        cv.put("IfExecutedGUID", UUID.randomUUID().toString());
        db.insert("tb_QueueAction", null, cv);
    }

    public ArrayList<ModelPrayerRequest> getAllAnsweredPrayerRequest(){
        ArrayList<ModelPrayerRequest> pr = new ArrayList<ModelPrayerRequest>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT PrayerRequestID, Subject, Description, Answered, AnsweredWhen, AnswerComment, CreatedWhen, TouchedWhen FROM tb_PrayerRequest WHERE Answered = 1 ORDER BY CreatedWhen DESC", null);

        while (c.moveToNext()) {

            ModelPrayerRequest p = new ModelPrayerRequest(c.getString(c.getColumnIndex("PrayerRequestID")), c.getString(c.getColumnIndex("Subject")), c.getString(c.getColumnIndex("Description")), convertToBoolean(c.getInt(c.getColumnIndex("Answered"))), convertToDateTime(c.getString(c.getColumnIndex("AnsweredWhen"))), c.getString(c.getColumnIndex("AnswerComment")), convertToDateTime(c.getString(c.getColumnIndex("CreatedWhen"))), convertToDateTime(c.getString(c.getColumnIndex("TouchedWhen"))));
            p.attachments = getPrayerRequestAttachment(p.PrayerRequestID, db);

            pr.add(p);
        }
        if(c != null)
            c.close();
        return pr;
    }

    public ArrayList<ModelPrayerRequest> getAllUnansweredPrayerRequest(){
        ArrayList<ModelPrayerRequest> pr = new ArrayList<ModelPrayerRequest>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT PrayerRequestID, Subject, Description, Answered, AnsweredWhen, AnswerComment, CreatedWhen, TouchedWhen FROM tb_PrayerRequest WHERE Answered = 0 ORDER BY CreatedWhen DESC", null);

        while (c.moveToNext()) {

            ModelPrayerRequest p = new ModelPrayerRequest(c.getString(c.getColumnIndex("PrayerRequestID")), c.getString(c.getColumnIndex("Subject")), c.getString(c.getColumnIndex("Description")), convertToBoolean(c.getInt(c.getColumnIndex("Answered"))), convertToDateTime(c.getString(c.getColumnIndex("AnsweredWhen"))), c.getString(c.getColumnIndex("AnswerComment")), convertToDateTime(c.getString(c.getColumnIndex("CreatedWhen"))), convertToDateTime(c.getString(c.getColumnIndex("TouchedWhen"))));
            p.attachments = getPrayerRequestAttachment(p.PrayerRequestID, db);

            pr.add(p);
        }
        if(c != null)
            c.close();
        return pr;
    }

    public ModelPrayerRequest GetPrayerRequest(String PrayerRequestID){
        ModelPrayerRequest pr = new ModelPrayerRequest();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT PrayerRequestID, Subject, Description, Answered, AnsweredWhen, AnswerComment, CreatedWhen, TouchedWhen FROM tb_PrayerRequest WHERE PrayerRequestID = '" + PrayerRequestID + "' ORDER BY CreatedWhen DESC", null);

        while (c.moveToNext()) {
            pr = new ModelPrayerRequest(c.getString(c.getColumnIndex("PrayerRequestID")), c.getString(c.getColumnIndex("Subject")), c.getString(c.getColumnIndex("Description")), convertToBoolean(c.getInt(c.getColumnIndex("Answered"))), convertToDateTime(c.getString(c.getColumnIndex("AnsweredWhen"))), c.getString(c.getColumnIndex("AnswerComment")), convertToDateTime(c.getString(c.getColumnIndex("CreatedWhen"))), convertToDateTime(c.getString(c.getColumnIndex("TouchedWhen"))));
            pr.attachments = getPrayerRequestAttachment(pr.PrayerRequestID, db);


        }
        if(c != null)
            c.close();
        return pr;
    }

    public void UpdatePrayerRequestID(String newPrayerRequestID, String prayerRequestID){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("PrayerRequestID", newPrayerRequestID);
        db.update("tb_PrayerRequest", cv, "PrayerRequestID = '" + prayerRequestID + "'", null);

        cv = new ContentValues();
        cv.put("ItemID", newPrayerRequestID);
        db.update("tb_QueueAction", cv, "ItemID = '" + prayerRequestID + "'", null);

        //might have more item to update.
        //i.e. when a prayer is been tag how to update the content.
    }

    public ArrayList<ModelPrayerRequest> getAllPrayerRequest_IDOnly(){
        ArrayList<ModelPrayerRequest> pr = new ArrayList<ModelPrayerRequest>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT PrayerRequestID FROM tb_PrayerRequest", null);

        while (c.moveToNext()) {

            ModelPrayerRequest p = new ModelPrayerRequest();
            p.PrayerRequestID = c.getString(c.getColumnIndex("PrayerRequestID"));
            pr.add(p);
        }
        if(c != null)
            c.close();
        return pr;
    }

    public void addPrayerRequest(ArrayList<ModelPrayerRequest> pr){
        SQLiteDatabase db = getWritableDatabase();
        if(pr == null)
            return;
        for(int x=0; x<pr.size(); x++) {
            ContentValues cv = new ContentValues();
            cv.put("PrayerRequestID", pr.get(x).PrayerRequestID);
            cv.put("Subject", pr.get(x).Subject);
            cv.put("Description", pr.get(x).Description);
            cv.put("Answered", pr.get(x).Answered);
            cv.put("AnswerComment", pr.get(x).AnswerComment);
            if(pr.get(x).Answered)
                cv.put("AnsweredWhen", pr.get(x).toDBFormattedAnsweredWhen());

            cv.put("CreatedWhen", pr.get(x).toDBFormattedCreatedWhen());
            cv.put("TouchedWhen", pr.get(x).toDBFormattedTouchedWhen());
            db.insert("tb_PrayerRequest", null, cv);

            if (pr.get(x).attachments != null){
                for(int y=0; y < pr.get(x).attachments.size(); y++){
                    ModelPrayerRequestAttachement f =  pr.get(x).attachments.get(y);
                    long id2 = -1;
                    while(id2 == -1) {
                        cv = new ContentValues();
                        cv.put("PrayerRequestID", pr.get(x).PrayerRequestID);
                        cv.put("GUID", pr.get(x).attachments.get(y).GUID);
                        cv.put("OriginalFilePath", pr.get(x).attachments.get(y).OriginalFilePath);
                        cv.put("FileName", pr.get(x).attachments.get(y).FileName);
                        cv.put("UserID", pr.get(x).attachments.get(y).UserID);
                        id2 = db.insert("tb_PrayerRequestAttachment", null, cv);
                    }
                }
            }

        }
    }

    /***********************************************
     *
     * Prayer section
     *
     **********************************************/
    public void AddPrayers(ArrayList<ModelOwnerPrayer> prayers, String UserID, String DisplayName, String ProfilePixURL){
        SQLiteDatabase db = getWritableDatabase();
        if(prayers == null)
            return;
        for(int x=0; x<prayers.size(); x++){
            ContentValues cv = new ContentValues();
            cv.put("PrayerID", prayers.get(x).PrayerID);
            cv.put("Content", prayers.get(x).Content);
            cv.put("PublicView", prayers.get(x).publicView);
            cv.put("ServerSent", true);
            cv.put("CreatedWhen", prayers.get(x).toDBFormattedCreatedWhen());
            cv.put("TouchedWhen", prayers.get(x).toDBFormattedTouchedWhen());
            db.insert("tb_ownerPrayer", null, cv);

            if(prayers.get(x).amen != null)
                AddAmens(db, prayers.get(x).amen, prayers.get(x).PrayerID);
            if(prayers.get(x).answers != null)
                AddAnswereds(db, prayers.get(x).answers, prayers.get(x).PrayerID, UserID, DisplayName, ProfilePixURL);
            if(prayers.get(x).attachments != null)
                AddAttachments(db, prayers.get(x).attachments, prayers.get(x).PrayerID);
            if(prayers.get(x).comments != null)
                AddComments(db, prayers.get(x).comments, prayers.get(x).PrayerID);
            if(prayers.get(x).selectedFriends != null)
                AddTagFriends(db, prayers.get(x).selectedFriends, prayers.get(x).PrayerID);
        }
    }

    public void AddNewPrayer(String prayer, boolean publicView, ArrayList<ModelFriendProfile> selectedFriends, ArrayList<ModelPrayerAttachement> attachment){
        SQLiteDatabase db = getWritableDatabase();
        long id = -1;
        String tUUID = "";
        while(id == -1) {
            tUUID = UUID.randomUUID().toString();
            ContentValues cv = new ContentValues();
            cv.put("PrayerID", tUUID);
            cv.put("Content", prayer);
            cv.put("PublicView", publicView);

            id = db.insert("tb_ownerPrayer", null, cv);
        }
        for(int x=0; x<selectedFriends.size(); x++){
            ModelFriendProfile f = selectedFriends.get(x);
            long id2 = -1;
            while(id2 == -1) {
                ContentValues cv = new ContentValues();
                cv.put("OwnerPrayerID", tUUID);
                cv.put("WhoID", f.UserID);

                id2 = db.insert("tb_OwnerPrayerTagFriends", null, cv);
            }
        }

        for(int x=0; x<attachment.size(); x++){
            ModelPrayerAttachement f = attachment.get(x);
            long id2 = -1;
            while(id2 == -1) {
                ContentValues cv = new ContentValues();
                cv.put("OwnerPrayerID", tUUID);
                cv.put("GUID", f.GUID);
                cv.put("OriginalFilePath", f.OriginalFilePath);
                cv.put("FileName", f.FileName);
                cv.put("UserID", f.UserID);
                id2 = db.insert("tb_OwnerPrayerAttachment", null, cv);
            }
        }

        ContentValues cv = new ContentValues();
        cv.put("Action", ModelQueueAction.ActionType.Insert.toString());
        cv.put("Item", ModelQueueAction.ItemType.Prayer.toString());
        cv.put("ItemID", tUUID);
        cv.put("IfExecutedGUID", UUID.randomUUID().toString());
        db.insert("tb_QueueAction", null, cv);
    }

    public ArrayList<ModelOwnerPrayer> getAllOwnerPrayer(String OwnerID){
        ArrayList<ModelOwnerPrayer> prayer = new ArrayList<ModelOwnerPrayer>();

        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT PrayerID, CreatedWhen, TouchedWhen, Content, PublicView, ServerSent, Deleted, " +
                "(SELECT COUNT(1) FROM tb_OwnerPrayerTagFriends WHERE OwnerPrayerID = A.PrayerID) AS NumberOfFriendsTag, " +
                "(SELECT COUNT(1) FROM tb_OwnerPrayerComment WHERE OwnerPrayerID = A.PrayerID) AS NumberOfComment, " +
                "(SELECT COUNT(1) FROM tb_OwnerPrayerAnswered WHERE OwnerPrayerID = A.PrayerID) AS NumberOfAnswered, " +
                "ifnull((SELECT Answered FROM tb_OwnerPrayerAnswered WHERE OwnerPrayerID = A.PrayerID ORDER BY CreatedWhen DESC LIMIT 1),'') AS Answered, " +
                "(SELECT COUNT(1) FROM tb_OwnerPrayerAmen WHERE OwnerPrayerID = A.PrayerID) AS NumberOfAmen, " +
                "(SELECT COUNT(1) FROM tb_OwnerPrayerAmen WHERE OwnerPrayerID = A.PrayerID AND WhoID = '" + OwnerID + "') AS OwnerAmen " +

                "from tb_ownerPrayer AS A WHERE Deleted = 0 ORDER BY TouchedWhen DESC";

        Cursor c = db.rawQuery(query, new String[]{});
        while (c.moveToNext()) {
            ModelOwnerPrayer o = new ModelOwnerPrayer();
            o.PrayerID = c.getString(c.getColumnIndex("PrayerID"));
            //2015-10-18 23:28:21
            o.CreatedWhen = convertToDateTime(c.getString(c.getColumnIndex("CreatedWhen")));
            o.TouchedWhen = convertToDateTime(c.getString(c.getColumnIndex("TouchedWhen")));
            o.Content = c.getString(c.getColumnIndex("Content"));
            o.publicView = convertToBoolean(c.getInt(c.getColumnIndex("PublicView")));
            o.ServerSent = convertToBoolean(c.getInt(c.getColumnIndex("ServerSent")));
            o.deleted = convertToBoolean(c.getInt(c.getColumnIndex("Deleted")));
            o.numberOfFriendsTag = c.getLong(c.getColumnIndex("NumberOfFriendsTag"));
            o.numberOfAmen = c.getLong(c.getColumnIndex("NumberOfAmen"));
            o.numberOfComment = c.getLong(c.getColumnIndex("NumberOfComment"));
            o.numberOfAnswered = c.getLong(c.getColumnIndex("NumberOfAnswered"));
            o.Answered = c.getString(c.getColumnIndex("Answered"));
            o.ownerAmen = convertToBoolean(c.getInt(c.getColumnIndex("OwnerAmen")));
            o.attachments = getAllOwnerPrayerAttachment(o.PrayerID, db);
            prayer.add(o);
        }
        if(c != null)
            c.close();
        return prayer;
    }

    public ModelOwnerPrayer GetPrayer(String PrayerID){
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT PrayerID, CreatedWhen, TouchedWhen, Content, PublicView, ServerSent, Deleted " +
                       "FROM tb_ownerPrayer AS A " +
                       "WHERE PrayerID = '" + PrayerID + "'";

        Cursor c = db.rawQuery(query, new String[]{});
        ModelOwnerPrayer o = new ModelOwnerPrayer();
        if(c.moveToNext()) {
            o.PrayerID = c.getString(c.getColumnIndex("PrayerID"));
            o.CreatedWhen = convertToDateTime(c.getString(c.getColumnIndex("CreatedWhen")));
            o.TouchedWhen = convertToDateTime(c.getString(c.getColumnIndex("TouchedWhen")));
            o.Content = c.getString(c.getColumnIndex("Content"));
            o.publicView = convertToBoolean(c.getInt(c.getColumnIndex("PublicView")));
            o.ServerSent = convertToBoolean(c.getInt(c.getColumnIndex("ServerSent")));
            o.deleted = convertToBoolean(c.getInt(c.getColumnIndex("Deleted")));
        }
        if(c != null)
            c.close();
        return o;
    }

    public void deletePrayer(String PrayerID){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        String date =  format.format(new Date());

        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("Deleted", 1);
        cv.put("TouchedWhen", date);
        db.update("tb_ownerPrayer", cv, "PrayerID = '" + PrayerID + "'", null);

        cv = new ContentValues();
        cv.put("Action", ModelQueueAction.ActionType.Delete.toString());
        cv.put("Item", ModelQueueAction.ItemType.Prayer.toString());
        cv.put("ItemID", PrayerID);
        cv.put("IfExecutedGUID", UUID.randomUUID().toString());
        db.insert("tb_QueueAction", null, cv);
    }

    public String getLastPrayerIDFromServer(){
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT PrayerID FROM tb_ownerPrayer AS A " +
                "WHERE ServerSent = 1 ORDER BY PrayerID DESC LIMIT 1";

        Cursor c = db.rawQuery(query, new String[]{});
        if(c.moveToNext()) {

            String id = c.getString(c.getColumnIndex("PrayerID"));
            c.close();
            return id;
        }

        if(c != null)
            c.close();

        return "0";
    }



    /***********************************************
     *
     * Tag Friends section
     *
     **********************************************/
    public void addFriends(String UserID, ArrayList<ModelFriendProfile> f){
        SQLiteDatabase db = getWritableDatabase();
        for(int x=0; x<f.size(); x++) {
            ContentValues values = new ContentValues();
            values.put("OwnerID", UserID);
            values.put("UserID", f.get(x).UserID);
            values.put("DisplayName", f.get(x).DisplayName);
            values.put("ProfilePictureURL", f.get(x).ProfilePictureURL);
            db.insert("tb_Friends", null, values);
        }
    }

    public ArrayList<ModelFriendProfile> getSelectedTagFriend(String PrayerID, String OwnerID){
        String query = "SELECT C.UserID, C.DisplayName, C.ProfilePictureURL FROM tb_ownerPrayer AS A INNER JOIN tb_OwnerPrayerTagFriends AS B ON A.PrayerID = B.OwnerPrayerID INNER JOIN tb_Friends AS C ON C.UserID = B.WhoID AND C.OwnerID = '" + OwnerID + "' WHERE A.PrayerID = '" + PrayerID + "'";
        ArrayList<ModelFriendProfile> selectedFriends = new ArrayList<ModelFriendProfile>();

        SQLiteDatabase db = getReadableDatabase();

        Cursor c = db.rawQuery(query, new String[]{});
        while (c.moveToNext()) {
            ModelFriendProfile f = new ModelFriendProfile(c.getString(c.getColumnIndex("UserID")),
                    c.getString(c.getColumnIndex("DisplayName")),
                    c.getString(c.getColumnIndex("ProfilePictureURL")),
                    true);
            selectedFriends.add(f);
        }
        if(c != null)
            c.close();
        return selectedFriends;
    }

    public void updateOwnerPrayerTagFriends(String OwnerPrayerID, ArrayList<ModelFriendProfile> selectedFriends){
        SQLiteDatabase db = getWritableDatabase();
        db.delete("tb_OwnerPrayerTagFriends", "OwnerPrayerID" + "= '" + OwnerPrayerID + "'", null);
        for(int x=0; x<selectedFriends.size(); x++){
            ContentValues cv = new ContentValues();
            cv.put("OwnerPrayerID", OwnerPrayerID);
            cv.put("WhoID", selectedFriends.get(x).UserID);
            db.insert("tb_OwnerPrayerTagFriends", null, cv);
        }

        ContentValues cv = new ContentValues();
        cv.put("Action", ModelQueueAction.ActionType.Update.toString());
        cv.put("Item", ModelQueueAction.ItemType.PrayerTagFriends.toString());
        cv.put("ItemID", OwnerPrayerID);
        cv.put("IfExecutedGUID", UUID.randomUUID().toString());
        db.insert("tb_QueueAction", null, cv);
    }

    public ArrayList<ModelFriendProfile> getAllFriends(String OwnerID){
        ArrayList<ModelFriendProfile> friend = new ArrayList<ModelFriendProfile>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT UserID, DisplayName, ProfilePictureURL FROM tb_Friends WHERE OwnerID = '" + OwnerID + "'", null);

        while (c.moveToNext()) {
            ModelFriendProfile p = new ModelFriendProfile(c.getString(c.getColumnIndex("UserID")), c.getString(c.getColumnIndex("DisplayName")), c.getString(c.getColumnIndex("ProfilePictureURL")), false);
            friend.add(p);
        }
        if(c != null)
            c.close();
        return friend;
    }

    public ArrayList<ModelFriendProfile> getAllFriends_IDOnly(String OwnerID){
        ArrayList<ModelFriendProfile> friend = new ArrayList<ModelFriendProfile>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT UserID FROM tb_Friends WHERE OwnerID = '" + OwnerID + "'", null);

        while (c.moveToNext()) {
            ModelFriendProfile p = new ModelFriendProfile(c.getString(c.getColumnIndex("UserID")));
            friend.add(p);
        }
        if(c != null)
            c.close();
        return friend;
    }

    public void AddTagFriends(SQLiteDatabase db, ArrayList<ModelFriendProfile> friend, String PrayerID){
        for(int x=0; x<friend.size(); x++) {
            ContentValues cv = new ContentValues();
            cv.put("OwnerPrayerID", PrayerID);
            cv.put("WhoID", friend.get(x).UserID);
            db.insert("tb_OwnerPrayerTagFriends", null, cv);
        }
    }

    /***********************************************
     *
     * Public View section
     *
     **********************************************/
    public void updateOwnerPrayerPublicView(String PrayerID, boolean publicView){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("PublicView", publicView);
        db.update("tb_ownerPrayer", cv, "PrayerID = '" + PrayerID + "'", null);

        cv = new ContentValues();
        cv.put("Action", ModelQueueAction.ActionType.Update.toString());
        cv.put("Item", ModelQueueAction.ItemType.PrayerPublicView.toString());
        cv.put("ItemID", PrayerID);
        cv.put("IfExecutedGUID", UUID.randomUUID().toString());
        db.insert("tb_QueueAction", null, cv);

    }



    /***********************************************
     *
     * Prayer Sent section
     *
     **********************************************/
    public void updateOwnerPrayerSent(String PrayerID, long newPrayerID){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("ServerSent", true);
        cv.put("PrayerID", newPrayerID);
        db.update("tb_ownerPrayer", cv, "PrayerID = '" + PrayerID + "'", null);

        cv = new ContentValues();
        cv.put("OwnerPrayerID", newPrayerID);
        db.update("tb_OwnerPrayerTagFriends", cv, "OwnerPrayerID = '" + PrayerID + "'", null);

        cv = new ContentValues();
        cv.put("OwnerPrayerID", newPrayerID);
        db.update("tb_OwnerPrayerAttachment", cv, "OwnerPrayerID = '" + PrayerID + "'", null);

        cv = new ContentValues();
        cv.put("ItemID", newPrayerID);
        db.update("tb_QueueAction", cv, "ItemID = '" + PrayerID + "'", null);
    }



    /***********************************************
     *
     * Amen section
     *
     **********************************************/
    public void AmenOwnerPrayer(String OwnerPrayerID, String WhoID, String whoName, String whoProfilePicture, boolean amen){
        SQLiteDatabase db = getWritableDatabase();
        if(amen) {
            ContentValues cv = new ContentValues();
            cv.put("WhoID", WhoID);
            cv.put("WhoName", whoName);
            cv.put("WhoProfilePicture", whoProfilePicture);
            cv.put("OwnerPrayerID", OwnerPrayerID);
            db.delete("tb_OwnerPrayerAmen", "OwnerPrayerID" + "= '" + OwnerPrayerID + "' AND WhoID = '" + WhoID + "'", null);
            db.insert("tb_OwnerPrayerAmen", null, cv);

            cv = new ContentValues();
            cv.put("Action", ModelQueueAction.ActionType.Insert.toString());
            cv.put("Item", ModelQueueAction.ItemType.PrayerAmen.toString());
            cv.put("ItemID", OwnerPrayerID);
            cv.put("IfExecutedGUID", UUID.randomUUID().toString());
            db.insert("tb_QueueAction", null, cv);
        }
        else{
            db.delete("tb_OwnerPrayerAmen", "OwnerPrayerID" + "= '" + OwnerPrayerID + "' AND WhoID = '" + WhoID + "'", null);

            ContentValues cv = new ContentValues();
            cv.put("Action", ModelQueueAction.ActionType.Delete.toString());
            cv.put("Item", ModelQueueAction.ItemType.PrayerAmen.toString());
            cv.put("ItemID", OwnerPrayerID);
            cv.put("IfExecutedGUID", UUID.randomUUID().toString());
            db.insert("tb_QueueAction", null, cv);
        }
    }

    public void AddAmens(SQLiteDatabase db, ArrayList<ModelPrayerAmen> amen, String PrayerID){
        for(int x=0; x<amen.size(); x++) {
            ContentValues cv = new ContentValues();
            cv.put("OwnerPrayerID", PrayerID);
            cv.put("WhoID", amen.get(x).WhoID);
            cv.put("WhoName", amen.get(x).WhoName);
            cv.put("WhoProfilePicture", amen.get(x).WhoProfilePicture);
            cv.put("CreatedWhen", amen.get(x).toDBFormattedCreatedWhen());
            db.insert("tb_OwnerPrayerAmen", null, cv);
        }
    }


    /***********************************************
     *
     * Attachment section
     *
     **********************************************/
    public void AddAttachments(SQLiteDatabase db, ArrayList<ModelPrayerAttachement> attachments, String PrayerID){
        for(int x=0; x<attachments.size(); x++) {
            ContentValues cv = new ContentValues();
            cv.put("OwnerPrayerID", PrayerID);
            cv.put("UserID", attachments.get(x).UserID);
            cv.put("GUID", attachments.get(x).GUID);
            cv.put("OriginalFilePath", attachments.get(x).OriginalFilePath);
            cv.put("FileName", attachments.get(x).FileName);
            db.insert("tb_OwnerPrayerAttachment", null, cv);
        }
    }

    public void updateAttachmentFilename(String newFilename, String GUID, String OwnerPrayerID){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("FileName", newFilename);
        db.update("tb_OwnerPrayerAttachment", cv, "OwnerPrayerID = '" + OwnerPrayerID + "' AND GUID = '" + GUID + "'", null);
    }

    private ArrayList<ModelPrayerAttachement> getAllOwnerPrayerAttachment(String PrayerID, SQLiteDatabase db_readonly){
        ArrayList<ModelPrayerAttachement> attachements = new ArrayList<ModelPrayerAttachement>();

        String query = "SELECT OwnerPrayerID, UserID, GUID, OriginalFilePath, FileName FROM tb_OwnerPrayerAttachment " +
                "WHERE OwnerPrayerID = '"+PrayerID+"'";

        Cursor c = db_readonly.rawQuery(query, new String[]{});
        while (c.moveToNext()) {
            ModelPrayerAttachement o = new ModelPrayerAttachement();
            o.PrayerID = c.getString(c.getColumnIndex("OwnerPrayerID"));
            o.GUID = c.getString(c.getColumnIndex("GUID"));
            o.OriginalFilePath = c.getString(c.getColumnIndex("OriginalFilePath"));
            o.FileName = c.getString(c.getColumnIndex("FileName"));
            o.UserID = c.getString(c.getColumnIndex("UserID"));
            attachements.add(o);
        }
        if(c != null)
            c.close();
        return attachements;
    }

    public ArrayList<ModelPrayerAttachement> getAllOwnerPrayerAttachment(String PrayerID){
        SQLiteDatabase db = getReadableDatabase();
        return getAllOwnerPrayerAttachment(PrayerID, db);
    }

    public void updatePrayerRequestAttachmentFilename(String newFilename, String GUID, String PrayerRequestID){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("FileName", newFilename);
        db.update("tb_PrayerRequestAttachment", cv, "PrayerRequestID = '" + PrayerRequestID + "' AND GUID = '" + GUID + "'", null);
    }

    public ArrayList<ModelPrayerRequestAttachement> getAllPrayerRequestAttachment(String PrayerRequestID){
        SQLiteDatabase db = getReadableDatabase();
        return getPrayerRequestAttachment(PrayerRequestID, db);
    }

    private ArrayList<ModelPrayerRequestAttachement> getPrayerRequestAttachment(String PrayerRequestID, SQLiteDatabase db_readonly){
        ArrayList<ModelPrayerRequestAttachement> attachements = new ArrayList<ModelPrayerRequestAttachement>();

        String query = "SELECT PrayerRequestID, UserID, GUID, OriginalFilePath, FileName FROM tb_PrayerRequestAttachment " +
                "WHERE PrayerRequestID = '"+PrayerRequestID+"'";

        Cursor c = db_readonly.rawQuery(query, new String[]{});
        while (c.moveToNext()) {
            ModelPrayerRequestAttachement o = new ModelPrayerRequestAttachement();
            o.PrayerRequestID = c.getString(c.getColumnIndex("PrayerRequestID"));
            o.GUID = c.getString(c.getColumnIndex("GUID"));
            o.OriginalFilePath = c.getString(c.getColumnIndex("OriginalFilePath"));
            o.FileName = c.getString(c.getColumnIndex("FileName"));
            o.UserID = c.getString(c.getColumnIndex("UserID"));
            attachements.add(o);
        }
        if(c != null)
            c.close();
        return attachements;
    }


    /***********************************************
     *
     * Comment section
     *
     **********************************************/
    public void addOwnerPrayerComment(String PrayerID, String comment, String WhoID, String WhoName, String WhoProfilePicture){
        SQLiteDatabase db = getWritableDatabase();
        String CommentID = UUID.randomUUID().toString();

        ContentValues cv = new ContentValues();
        cv.put("OwnerPrayerID", PrayerID);
        cv.put("CommentID", CommentID);
        cv.put("comment", comment);
        cv.put("WhoID", WhoID);
        cv.put("WhoName", WhoName);
        cv.put("WhoProfilePicture", WhoProfilePicture);
        db.insert("tb_OwnerPrayerComment", null, cv);

        cv = new ContentValues();
        cv.put("Action", ModelQueueAction.ActionType.Insert.toString());
        cv.put("Item", ModelQueueAction.ItemType.PrayerComment.toString());
        cv.put("ItemID", CommentID);
        cv.put("IfExecutedGUID", UUID.randomUUID().toString());
        db.insert("tb_QueueAction", null, cv);
    }

    public void updateOwnerPrayerComment(ModelPrayerComment comment){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        String date =  format.format(new Date());

        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("Comment", comment.Comment);
        cv.put("TouchedWhen", date);
        db.update("tb_OwnerPrayerComment", cv, "OwnerPrayerID = '" + comment.OwnerPrayerID + "' AND CommentID = '" + comment.CommentID + "'", null);

        cv = new ContentValues();
        cv.put("Action", ModelQueueAction.ActionType.Update.toString());
        cv.put("Item", ModelQueueAction.ItemType.PrayerComment.toString());
        cv.put("ItemID", comment.CommentID);
        cv.put("IfExecutedGUID", UUID.randomUUID().toString());
        db.insert("tb_QueueAction", null, cv);
    }

    public ArrayList<ModelPrayerComment> getAllOwnerPrayerComment(String PrayerID){

        String query = "SELECT CommentID, OwnerPrayerID, WhoID, WhoName, WhoProfilePicture, Comment, ServerSent, CreatedWhen, TouchedWhen FROM tb_OwnerPrayerComment  WHERE OwnerPrayerID = '" + PrayerID + "' ORDER BY CreatedWhen DESC";
        ArrayList<ModelPrayerComment> comments = new ArrayList<ModelPrayerComment>();

        SQLiteDatabase db = getReadableDatabase();

        Cursor c = db.rawQuery(query, new String[]{});
        while (c.moveToNext()) {
            ModelPrayerComment f = new ModelPrayerComment();

            f.OwnerPrayerID = c.getString(c.getColumnIndex("OwnerPrayerID"));
            f.CommentID = c.getString(c.getColumnIndex("CommentID"));
            f.WhoID = c.getString(c.getColumnIndex("WhoID"));
            f.WhoName = c.getString(c.getColumnIndex("WhoName"));
            f.WhoProfilePicture = c.getString(c.getColumnIndex("WhoProfilePicture"));
            f.Comment = c.getString(c.getColumnIndex("Comment"));
            f.ServerSent = convertToBoolean(c.getInt(c.getColumnIndex("ServerSent")));
            f.CreatedWhen = convertToDateTime(c.getString(c.getColumnIndex("CreatedWhen")));
            f.TouchedWhen = convertToDateTime(c.getString(c.getColumnIndex("TouchedWhen")));
            comments.add(f);
        }
        if(c != null)
            c.close();
        return comments;
    }

    public ModelPrayerComment GetPrayerComment(String CommentID){
        String query = "SELECT CommentID, OwnerPrayerID, WhoID, WhoName, WhoProfilePicture, Comment, ServerSent, CreatedWhen, TouchedWhen FROM tb_OwnerPrayerComment  WHERE CommentID = '" + CommentID + "'";
        SQLiteDatabase db = getReadableDatabase();
        ModelPrayerComment f = new ModelPrayerComment();


        Cursor c = db.rawQuery(query, new String[]{});
        while (c.moveToNext()) {
            f = new ModelPrayerComment();

            f.OwnerPrayerID = c.getString(c.getColumnIndex("OwnerPrayerID"));
            f.CommentID = c.getString(c.getColumnIndex("CommentID"));
            f.WhoID = c.getString(c.getColumnIndex("WhoID"));
            f.WhoName = c.getString(c.getColumnIndex("WhoName"));
            f.WhoProfilePicture = c.getString(c.getColumnIndex("WhoProfilePicture"));
            f.Comment = c.getString(c.getColumnIndex("Comment"));
            f.ServerSent = convertToBoolean(c.getInt(c.getColumnIndex("ServerSent")));
            f.CreatedWhen = convertToDateTime(c.getString(c.getColumnIndex("CreatedWhen")));
            f.TouchedWhen = convertToDateTime(c.getString(c.getColumnIndex("TouchedWhen")));


        }
        if(c != null)
            c.close();
        return f;
    }

    public void UpdatePrayerCommentID(String newCommentID, String commentID, String OwnerPrayerID){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("CommentID", newCommentID);
        cv.put("ServerSent", true);
        db.update("tb_OwnerPrayerComment", cv, "OwnerPrayerID = '" + OwnerPrayerID + "' AND CommentID = '" + commentID + "'", null);

        cv = new ContentValues();
        cv.put("ItemID", newCommentID);
        db.update("tb_QueueAction", cv, "ItemID = '" + commentID + "'", null);

    }

    public void DeletePrayerComment(String CommentID){
        SQLiteDatabase db = getWritableDatabase();

        db.delete("tb_OwnerPrayerComment", "CommentID = '" + CommentID + "'", null);

        ContentValues cv = new ContentValues();
        cv.put("Action", ModelQueueAction.ActionType.Delete.toString());
        cv.put("Item", ModelQueueAction.ItemType.PrayerComment.toString());
        cv.put("ItemID", CommentID);
        cv.put("IfExecutedGUID", UUID.randomUUID().toString());
        db.insert("tb_QueueAction", null, cv);
    }

    public void AddComments(SQLiteDatabase db, ArrayList<ModelPrayerComment> comments, String PrayerID){
        for(int x=0; x<comments.size(); x++) {
            ContentValues cv = new ContentValues();
            cv.put("OwnerPrayerID", PrayerID);
            cv.put("CommentID", comments.get(x).CommentID);
            cv.put("WhoID", comments.get(x).WhoID);
            cv.put("WhoName", comments.get(x).WhoName);
            cv.put("WhoProfilePicture", comments.get(x).WhoProfilePicture);
            cv.put("Comment", comments.get(x).Comment);
            cv.put("ServerSent", true);
            cv.put("TouchedWhen", comments.get(x).toDBFormattedTouchedWhen());
            cv.put("CreatedWhen", comments.get(x).toDBFormattedCreatedWhen());
            db.insert("tb_OwnerPrayerComment", null, cv);
        }
    }


    /***********************************************
     *
     * Answered section
     *
     **********************************************/
    public ArrayList<ModelPrayerAnswered> getAllOwnerPrayerAnswered(String PrayerID){

        String query = "SELECT AnsweredID, OwnerPrayerID, WhoID, WhoName, WhoProfilePicture, Answered, ServerSent, CreatedWhen, TouchedWhen FROM tb_OwnerPrayerAnswered  WHERE OwnerPrayerID = '" + PrayerID + "' ORDER BY CreatedWhen DESC";
        ArrayList<ModelPrayerAnswered> comments = new ArrayList<ModelPrayerAnswered>();

        SQLiteDatabase db = getReadableDatabase();

        Cursor c = db.rawQuery(query, new String[]{});
        while (c.moveToNext()) {
            ModelPrayerAnswered f = new ModelPrayerAnswered();

            f.OwnerPrayerID = c.getString(c.getColumnIndex("OwnerPrayerID"));
            f.AnsweredID = c.getString(c.getColumnIndex("AnsweredID"));
            f.WhoID = c.getString(c.getColumnIndex("WhoID"));
            f.WhoName = c.getString(c.getColumnIndex("WhoName"));
            f.WhoProfilePicture = c.getString(c.getColumnIndex("WhoProfilePicture"));
            f.Answered = c.getString(c.getColumnIndex("Answered"));
            f.ServerSent = convertToBoolean(c.getInt(c.getColumnIndex("ServerSent")));
            f.CreatedWhen = convertToDateTime(c.getString(c.getColumnIndex("CreatedWhen")));
            f.TouchedWhen = convertToDateTime(c.getString(c.getColumnIndex("TouchedWhen")));
            comments.add(f);
        }
        if(c != null)
            c.close();
        return comments;
    }

    public void updateOwnerPrayerAnswered(ModelPrayerAnswered ans){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        String date =  format.format(new Date());

        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("Answered", ans.Answered);
        cv.put("TouchedWhen", date);
        db.update("tb_OwnerPrayerAnswered", cv, "OwnerPrayerID = '" + ans.OwnerPrayerID + "' AND AnsweredID = '" + ans.AnsweredID + "'", null);

        cv = new ContentValues();
        cv.put("Action", ModelQueueAction.ActionType.Update.toString());
        cv.put("Item", ModelQueueAction.ItemType.PrayerAnswered.toString());
        cv.put("ItemID", ans.AnsweredID);
        cv.put("IfExecutedGUID", UUID.randomUUID().toString());
        db.insert("tb_QueueAction", null, cv);
    }

    public void addOwnerPrayerAnswered(String PrayerID, String comment, String WhoID, String WhoName, String WhoProfilePicture){
        SQLiteDatabase db = getWritableDatabase();
        String AnsweredID = UUID.randomUUID().toString();

        ContentValues cv = new ContentValues();
        cv.put("OwnerPrayerID", PrayerID);
        cv.put("AnsweredID", AnsweredID);
        cv.put("Answered", comment);
        cv.put("WhoID", WhoID);
        cv.put("WhoName", WhoName);
        cv.put("WhoProfilePicture", WhoProfilePicture);
        db.insert("tb_OwnerPrayerAnswered", null, cv);

        cv = new ContentValues();
        cv.put("Action", ModelQueueAction.ActionType.Insert.toString());
        cv.put("Item", ModelQueueAction.ItemType.PrayerAnswered.toString());
        cv.put("ItemID", AnsweredID);
        cv.put("IfExecutedGUID", UUID.randomUUID().toString());
        db.insert("tb_QueueAction", null, cv);
    }

    public void DeletePrayerAnswered(String AnsweredID){
        SQLiteDatabase db = getWritableDatabase();

        db.delete("tb_OwnerPrayerComment", "AnsweredID = '" + AnsweredID + "'", null);

        ContentValues cv = new ContentValues();
        cv.put("Action", ModelQueueAction.ActionType.Delete.toString());
        cv.put("Item", ModelQueueAction.ItemType.PrayerAnswered.toString());
        cv.put("ItemID", AnsweredID);
        cv.put("IfExecutedGUID", UUID.randomUUID().toString());
        db.insert("tb_QueueAction", null, cv);
    }

    public ModelPrayerAnswered GetPrayerAnswered(String AnsweredID){
        String query = "SELECT AnsweredID, OwnerPrayerID, WhoID, WhoName, WhoProfilePicture, Answered, ServerSent, CreatedWhen, TouchedWhen FROM tb_OwnerPrayerAnswered  WHERE AnsweredID = '" + AnsweredID + "'";
        SQLiteDatabase db = getReadableDatabase();
        ModelPrayerAnswered f = new ModelPrayerAnswered();


        Cursor c = db.rawQuery(query, new String[]{});
        while (c.moveToNext()) {
            f = new ModelPrayerAnswered();

            f.OwnerPrayerID = c.getString(c.getColumnIndex("OwnerPrayerID"));
            f.AnsweredID = c.getString(c.getColumnIndex("AnsweredID"));
            f.WhoID = c.getString(c.getColumnIndex("WhoID"));
            f.WhoName = c.getString(c.getColumnIndex("WhoName"));
            f.WhoProfilePicture = c.getString(c.getColumnIndex("WhoProfilePicture"));
            f.Answered = c.getString(c.getColumnIndex("Answered"));
            f.ServerSent = convertToBoolean(c.getInt(c.getColumnIndex("ServerSent")));
            f.CreatedWhen = convertToDateTime(c.getString(c.getColumnIndex("CreatedWhen")));
            f.TouchedWhen = convertToDateTime(c.getString(c.getColumnIndex("TouchedWhen")));


        }
        if(c != null)
            c.close();
        return f;
    }

    public void UpdatePrayerAnsweredID(String newAnsweredID, String answeredID, String OwnerPrayerID){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("AnsweredID", newAnsweredID);
        cv.put("ServerSent", true);
        db.update("tb_OwnerPrayerAnswered", cv, "OwnerPrayerID = '" + OwnerPrayerID + "' AND AnsweredID = '" + answeredID + "'", null);

        cv = new ContentValues();
        cv.put("ItemID", newAnsweredID);
        db.update("tb_QueueAction", cv, "ItemID = '" + answeredID + "'", null);

    }

    public void AddAnswereds(SQLiteDatabase db, ArrayList<ModelPrayerAnswered> answers, String PrayerID, String WhoID, String DisplayName, String ProfilePictureURL){
        for(int x=0; x<answers.size(); x++) {
            ContentValues cv = new ContentValues();
            cv.put("OwnerPrayerID", PrayerID);
            cv.put("AnsweredID", answers.get(x).AnsweredID);
            cv.put("WhoID", WhoID);
            cv.put("WhoName", DisplayName);
            cv.put("WhoProfilePicture", ProfilePictureURL);
            cv.put("Answered", answers.get(x).Answered);
            cv.put("ServerSent", true);
            cv.put("TouchedWhen", answers.get(x).toDBFormattedTouchedWhen());
            cv.put("CreatedWhen", answers.get(x).toDBFormattedCreatedWhen());
            db.insert("tb_OwnerPrayerAnswered", null, cv);
        }
    }













    private boolean convertToBoolean(int bool){
        if(bool == 0)
            return false;
        else
            return true;
    }

    private Date convertToDateTime(String dtStart){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date;
        try {
            date = format.parse(dtStart);
            return date;
        } catch (Exception e) {

        }
        return null;
    }
}
