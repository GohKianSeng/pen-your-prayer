package com.penyourprayer.penyourprayer.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.penyourprayer.penyourprayer.Common.Model.ModelFriendProfile;
import com.penyourprayer.penyourprayer.Common.Model.ModelPayerComment;
import com.penyourprayer.penyourprayer.Common.Model.ModelPrayerAttachement;
import com.penyourprayer.penyourprayer.Common.Model.ModelOwnerPrayer;
import com.penyourprayer.penyourprayer.Common.Model.ModelQueueAction;

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
    private static final int DATABASE_VERSION = 26;

    // Database Name
    private static final String DATABASE_NAME = "PenYourPrayerSQLite";
    private static final String tb_Friends = "CREATE TABLE tb_Friends ('UserID' TEXT NOT NULL UNIQUE, 'DisplayName' TEXT NOT NULL, 'ProfilePictureURL' TEXT, PRIMARY KEY(UserID))";
    private static final String tb_OwnerPrayerAmen = "CREATE TABLE tb_OwnerPrayerAmen ('OwnerPrayerID' TEXT NOT NULL,'WhoID' TEXT NOT NULL,'WhoName' TEXT NOT NULL,'WhoProfilePicture' TEXT, 'CreatedWhen' DATETIME DEFAULT (DATETIME('NOW','LOCALTIME')), PRIMARY KEY(OwnerPrayerID,WhoID))";
    private static final String tb_OwnerPrayerComment = "CREATE TABLE tb_OwnerPrayerComment (CommentID TEXT NOT NULL UNIQUE, OwnerPrayerID TEXT NOT NULL, WhoID TEXT NOT NULL, WhoName TEXT NOT NULL,WhoProfilePicture TEXT, Comment TEXT NOT NULL, ServerSent INTEGER NOT NULL DEFAULT 0, CreatedWhen DATETIME DEFAULT (DATETIME('NOW','LOCALTIME')), TouchedWhen DATETIME DEFAULT (DATETIME('NOW','LOCALTIME')), PRIMARY KEY(CommentID,OwnerPrayerID))";
    private static final String tb_OwnerPrayerTagFriends = "CREATE TABLE tb_OwnerPrayerTagFriends ( OwnerPrayerID TEXT NOT NULL, WhoID TEXT NOT NULL, PRIMARY KEY(OwnerPrayerID,WhoID))";
    private static final String tb_ownerPrayer = "CREATE TABLE tb_ownerPrayer (PrayerID TEXT NOT NULL UNIQUE, CreatedWhen DATETIME NOT NULL DEFAULT (DATETIME('NOW','LOCALTIME')), TouchedWhen DATETIME NOT NULL DEFAULT (DATETIME('NOW','LOCALTIME')), Content TEXT NOT NULL, PublicView INTEGER NOT NULL DEFAULT 0, ServerSent INTEGER NOT NULL DEFAULT 0, Deleted INTEGER NOT NULL DEFAULT 0, PRIMARY KEY(PrayerID))";
    private static final String tb_QueueAction = "CREATE TABLE tb_QueueAction (ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, Action TEXT NOT NULL, Item TEXT NOT NULL, ItemID TEXT NOT NULL, IfExecutedGUID TEXT NOT NULL)";
    private static final String tb_OwnerPrayerAttachment = "CREATE TABLE tb_OwnerPrayerAttachment (OwnerPrayerID TEXT NOT NULL, GUID TEXT NOT NULL, OriginalFilePath TEXT NOT NULL, FileName TEXT NOT NULL, PRIMARY KEY(OwnerPrayerID,GUID))";

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
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS tb_Friends");
        db.execSQL("DROP TABLE IF EXISTS tb_OwnerPrayerAmen");
        db.execSQL("DROP TABLE IF EXISTS tb_OwnerPrayerComment");
        db.execSQL("DROP TABLE IF EXISTS tb_OwnerPrayerTagFriends");
        db.execSQL("DROP TABLE IF EXISTS tb_ownerPrayer");
        db.execSQL("DROP TABLE IF EXISTS tb_QueueAction");
        db.execSQL("DROP TABLE IF EXISTS tb_OwnerPrayerAttachment");
        onCreate(db);

        ContentValues values = new ContentValues();
        values.put("UserID", -999999999999999943L);
        values.put("DisplayName", "Siew Lin");
        values.put("ProfilePictureURL", "https://encrypted-tbn1.gstatic.com/images?q=tbn:ANd9GcTy9gsPmNSg7MdCHvmdzn7DHOwSKcPko4q0wdiCuhgiUUWCGZ4rJA");
        db.insert("tb_Friends", null, values);

        values = new ContentValues();
        values.put("UserID", -999999999999999944L);
        values.put("DisplayName", "Lilian Choo");
        values.put("ProfilePictureURL", "https://encrypted-tbn3.gstatic.com/images?q=tbn:ANd9GcT6mQRu_MLgguTBLnZzfviEid5cA1AiiNKboVwrhxgV-v2WrGQOUg");
        db.insert("tb_Friends", null, values);

        values = new ContentValues();
        values.put("UserID", 7700000000000000000L);
        values.put("DisplayName", "Joshua Ng");
        values.put("ProfilePictureURL", "https://encrypted-tbn1.gstatic.com/images?q=tbn:ANd9GcS_xHhCaeKTrC0G6VE49WleDv7_h6i5x1MxoQndwnVUb9bPClak");
        db.insert("tb_Friends", null, values);

    }

    public void openConnection(){
        this.getWritableDatabase();
    }

    public ArrayList<ModelFriendProfile> getAllFriends(){
        ArrayList<ModelFriendProfile> friend = new ArrayList<ModelFriendProfile>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT UserID, DisplayName, ProfilePictureURL FROM tb_Friends", null);

        while (c.moveToNext()) {
            ModelFriendProfile p = new ModelFriendProfile(c.getString(c.getColumnIndex("UserID")), c.getString(c.getColumnIndex("DisplayName")), c.getString(c.getColumnIndex("ProfilePictureURL")), false);
            friend.add(p);
        }
        if(c != null)
            c.close();
        return friend;
    }

    public void AddNewPrayer(Context ctx, String prayer, boolean publicView, ArrayList<ModelFriendProfile> selectedFriends, ArrayList<ModelPrayerAttachement> attachment){
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
                "(SELECT COUNT(1) FROM tb_OwnerPrayerAmen WHERE OwnerPrayerID = A.PrayerID) AS NumberOfAmen, " +
                "(SELECT COUNT(1) FROM tb_OwnerPrayerAmen WHERE OwnerPrayerID = A.PrayerID AND WhoID = '" + OwnerID + "') AS OwnerAmen " +
                "from tb_ownerPrayer AS A ORDER BY TouchedWhen DESC";

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
            o.ownerAmen = convertToBoolean(c.getInt(c.getColumnIndex("OwnerAmen")));
            prayer.add(o);
        }
        if(c != null)
            c.close();
        return prayer;
    }

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
        db.delete("tb_QueueAction", "ID = " + String.valueOf(QueueID) , null);
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

    public ArrayList<ModelFriendProfile> getSelectedTagFriend(String PrayerID){
        String query = "SELECT C.UserID, C.DisplayName, C.ProfilePictureURL FROM tb_ownerPrayer AS A INNER JOIN tb_OwnerPrayerTagFriends AS B ON A.PrayerID = B.OwnerPrayerID INNER JOIN tb_Friends AS C ON C.UserID = B.WhoID WHERE A.PrayerID = '" + PrayerID + "'";
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
        }
        else{
            db.delete("tb_OwnerPrayerAmen", "OwnerPrayerID" + "= '" + OwnerPrayerID + "' AND WhoID = '" + WhoID + "'", null);
        }
    }

    public void AddNewAttachment(ModelPrayerAttachement p){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("PrayerID", p.PrayerID);
        cv.put("GUID", p.GUID);
        cv.put("OriginalFilePath", p.OriginalFilePath);
        cv.put("FileName", p.FileName);
        db.insert("tb_OwnerPrayerAttachment", null, cv);
    }

    public void updateAttachmentFilename(String newFilename, String GUID, String OwnerPrayerID){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("FileName", newFilename);
        db.update("tb_OwnerPrayerAttachment", cv, "OwnerPrayerID = '" + OwnerPrayerID + "' AND GUID = '" + GUID + "'", null);
    }

    public ArrayList<ModelPrayerAttachement> getAllOwnerPrayerAttachment(String PrayerID){
        ArrayList<ModelPrayerAttachement> attachements = new ArrayList<ModelPrayerAttachement>();

        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT OwnerPrayerID, GUID, OriginalFilePath, FileName FROM tb_OwnerPrayerAttachment " +
                       "WHERE OwnerPrayerID = '"+PrayerID+"'";

        Cursor c = db.rawQuery(query, new String[]{});
        while (c.moveToNext()) {
            ModelPrayerAttachement o = new ModelPrayerAttachement();
            o.PrayerID = c.getString(c.getColumnIndex("OwnerPrayerID"));
            o.GUID = c.getString(c.getColumnIndex("GUID"));
            o.OriginalFilePath = c.getString(c.getColumnIndex("OriginalFilePath"));
            o.FileName = c.getString(c.getColumnIndex("FileName"));
            attachements.add(o);
        }
        if(c != null)
            c.close();
        return attachements;
    }

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

    public void updateOwnerPrayerComment(ModelPayerComment comment){
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

    public ArrayList<ModelPayerComment> getAllOwnerPrayerComment(String PrayerID){

        String query = "SELECT CommentID, OwnerPrayerID, WhoID, WhoName, WhoProfilePicture, Comment, ServerSent, CreatedWhen, TouchedWhen FROM tb_OwnerPrayerComment  WHERE OwnerPrayerID = '" + PrayerID + "' ORDER BY CreatedWhen DESC";
        ArrayList<ModelPayerComment> comments = new ArrayList<ModelPayerComment>();

        SQLiteDatabase db = getReadableDatabase();

        Cursor c = db.rawQuery(query, new String[]{});
        while (c.moveToNext()) {
            ModelPayerComment f = new ModelPayerComment();

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

    public ModelPayerComment GetPrayerComment(String CommentID){
        String query = "SELECT CommentID, OwnerPrayerID, WhoID, WhoName, WhoProfilePicture, Comment, ServerSent, CreatedWhen, TouchedWhen FROM tb_OwnerPrayerComment  WHERE CommentID = '" + CommentID + "'";
        SQLiteDatabase db = getReadableDatabase();
        ModelPayerComment f = new ModelPayerComment();


        Cursor c = db.rawQuery(query, new String[]{});
        while (c.moveToNext()) {
            f = new ModelPayerComment();

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
