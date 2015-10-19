package com.penyourprayer.penyourprayer.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.ParseException;

import com.penyourprayer.penyourprayer.Common.FriendProfileModel;
import com.penyourprayer.penyourprayer.Common.OwnerPrayerModel;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

/**
 * Created by sisgks on 21/09/2015.
 */
public class Database extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 8;

    // Database Name
    private static final String DATABASE_NAME = "PenYourPrayerSQLite";
    private static final String tb_Friends = "CREATE TABLE tb_Friends ('GUID' TEXT NOT NULL UNIQUE, 'Name' TEXT NOT NULL, 'ProfilePicture' TEXT, PRIMARY KEY(GUID))";
    private static final String tb_OwnerPrayerAmen = "CREATE TABLE tb_OwnerPrayerAmen ('OwnerPrayerGUID' TEXT NOT NULL,'WhoGUID' TEXT NOT NULL,'WhoName' TEXT NOT NULL,'WhoProfilePicture' TEXT, 'CreatedWhen' DATETIME DEFAULT (DATETIME('NOW','LOCALTIME')), PRIMARY KEY(OwnerPrayerGUID,WhoGUID))";
    private static final String tb_OwnerPrayerComment = "CREATE TABLE tb_OwnerPrayerComment ('GUID' TEXT NOT NULL,'OwnerPrayerGUID' TEXT NOT NULL,'WhoGUID' TEXT NOT NULL,'WhoName' TEXT NOT NULL,'WhoProfilePicture' TEXT,'Comment' TEXT NOT NULL,'CreatedWhen' DATETIME DEFAULT (DATETIME('NOW','LOCALTIME')),'TouchedWhen' DATETIME DEFAULT (DATETIME('NOW','LOCALTIME')),PRIMARY KEY(GUID))";
    private static final String tb_OwnerPrayerTagFriends = "CREATE TABLE tb_OwnerPrayerTagFriends ( OwnerPrayerGUID TEXT NOT NULL, WhoGUID TEXT NOT NULL, PRIMARY KEY(OwnerPrayerGUID,WhoGUID))";
    private static final String tb_ownerPrayer = "CREATE TABLE tb_ownerPrayer ( GUID TEXT NOT NULL UNIQUE, CreatedWhen DATETIME NOT NULL DEFAULT (DATETIME('NOW','LOCALTIME')), TouchedWhen DATETIME NOT NULL DEFAULT (DATETIME('NOW','LOCALTIME')), Content TEXT NOT NULL, PublicView INTEGER NOT NULL DEFAULT 0, ServerSent INTEGER NOT NULL DEFAULT 0, Deleted INTEGER NOT NULL DEFAULT 0, PRIMARY KEY(GUID))";


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
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS tb_Friends");
        db.execSQL("DROP TABLE IF EXISTS tb_OwnerPrayerAmen");
        db.execSQL("DROP TABLE IF EXISTS tb_OwnerPrayerComment");
        db.execSQL("DROP TABLE IF EXISTS tb_OwnerPrayerTagFriends");
        db.execSQL("DROP TABLE IF EXISTS tb_ownerPrayer");
        onCreate(db);

        ContentValues values = new ContentValues();
        values.put("GUID", 234);
        values.put("Name", "Siew Lin");
        values.put("ProfilePicture", "https://encrypted-tbn1.gstatic.com/images?q=tbn:ANd9GcTy9gsPmNSg7MdCHvmdzn7DHOwSKcPko4q0wdiCuhgiUUWCGZ4rJA");
        db.insert("tb_Friends", null, values);

        values = new ContentValues();
        values.put("GUID", 235);
        values.put("Name", "Lilian Choo");
        values.put("ProfilePicture", "https://encrypted-tbn3.gstatic.com/images?q=tbn:ANd9GcT6mQRu_MLgguTBLnZzfviEid5cA1AiiNKboVwrhxgV-v2WrGQOUg");
        db.insert("tb_Friends", null, values);

        values = new ContentValues();
        values.put("GUID", 236);
        values.put("Name", "Joshua Ng");
        values.put("ProfilePicture", "https://encrypted-tbn1.gstatic.com/images?q=tbn:ANd9GcS_xHhCaeKTrC0G6VE49WleDv7_h6i5x1MxoQndwnVUb9bPClak");
        db.insert("tb_Friends", null, values);

    }

    public void openConnection(){
        this.getWritableDatabase();
    }

    public ArrayList<FriendProfileModel> getAllFriends(){
        ArrayList<FriendProfileModel> friend = new ArrayList<FriendProfileModel>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT GUID, Name, ProfilePicture FROM tb_Friends", null);

        while (c.moveToNext()) {
            FriendProfileModel p = new FriendProfileModel(c.getString(c.getColumnIndex("GUID")), c.getString(c.getColumnIndex("Name")), c.getString(c.getColumnIndex("ProfilePicture")), false);
            friend.add(p);
        }

        return friend;
    }

    public void AddNewPrayer(Context ctx, String prayer, boolean publicView, ArrayList<FriendProfileModel> selectedFriends){
        SQLiteDatabase db = getWritableDatabase();
        long id = -1;
        String tUUID = "";
        while(id == -1) {
            tUUID = UUID.randomUUID().toString();
            ContentValues cv = new ContentValues();
            cv.put("GUID", tUUID);
            cv.put("Content", prayer);
            cv.put("PublicView", publicView);

            id = db.insert("tb_ownerPrayer", null, cv);
        }
        for(int x=0; x<selectedFriends.size(); x++){
            FriendProfileModel f = selectedFriends.get(x);
            long id2 = -1;
            while(id2 == -1) {
                ContentValues cv = new ContentValues();
                cv.put("OwnerPrayerGUID", tUUID);
                cv.put("WhoGUID", f.GUID);

                id2 = db.insert("tb_OwnerPrayerTagFriends", null, cv);
            }
        }
    }

    public ArrayList<OwnerPrayerModel> getAllOwnerPrayer(String OwnerGUID){
        ArrayList<OwnerPrayerModel> prayer = new ArrayList<OwnerPrayerModel>();

        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT GUID, CreatedWhen, TouchedWhen, Content, PublicView, ServerSent, Deleted, " +
                "(SELECT COUNT(1) FROM tb_OwnerPrayerTagFriends WHERE OwnerPrayerGUID = A.GUID) AS NumberOfFriendsTag, " +
                "(SELECT COUNT(1) FROM tb_OwnerPrayerComment WHERE OwnerPrayerGUID = A.GUID) AS NumberOfComment, " +
                "(SELECT COUNT(1) FROM tb_OwnerPrayerAmen WHERE OwnerPrayerGUID = A.GUID) AS NumberOfAmen, " +
                "(SELECT COUNT(1) FROM tb_OwnerPrayerAmen WHERE OwnerPrayerGUID = A.GUID AND WhoGUID = '" + OwnerGUID + "') AS OwnerAmen " +
                "from tb_ownerPrayer AS A ORDER BY TouchedWhen DESC";

        Cursor c = db.rawQuery(query, new String[]{});
        while (c.moveToNext()) {
            OwnerPrayerModel o = new OwnerPrayerModel();
            o.GUID = c.getString(c.getColumnIndex("GUID"));
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
        return prayer;
    }

    public ArrayList<FriendProfileModel> getSelectedTagFriend(String GUID){
        String query = "SELECT C.GUID, C.Name, C.ProfilePicture FROM tb_ownerPrayer AS A INNER JOIN tb_OwnerPrayerTagFriends AS B ON A.GUID = B.OwnerPrayerGUID INNER JOIN tb_Friends AS C ON C.GUID = B.WhoGUID WHERE A.GUID = '" + GUID + "'";
        ArrayList<FriendProfileModel> selectedFriends = new ArrayList<FriendProfileModel>();

        SQLiteDatabase db = getReadableDatabase();

        Cursor c = db.rawQuery(query, new String[]{});
        while (c.moveToNext()) {
            FriendProfileModel f = new FriendProfileModel(c.getString(c.getColumnIndex("GUID")),
                    c.getString(c.getColumnIndex("Name")),
                    c.getString(c.getColumnIndex("ProfilePicture")),
                    true);
            selectedFriends.add(f);
        }
        return selectedFriends;
    }

    public void updateOwnerPrayerTagFriends(String GUID, ArrayList<FriendProfileModel> selectedFriends){
        SQLiteDatabase db = getWritableDatabase();
        db.delete("tb_OwnerPrayerTagFriends", "OwnerPrayerGUID" + "= '" + GUID + "'", null);
        for(int x=0; x<selectedFriends.size(); x++){
            ContentValues cv = new ContentValues();
            cv.put("OwnerPrayerGUID", GUID);
            cv.put("WhoGUID", selectedFriends.get(x).GUID);
            db.insert("tb_OwnerPrayerTagFriends", null, cv);
        }
    }

    public void updateOwnerPrayerPublicView(String GUID, boolean publicView){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("PublicView", publicView);
        db.update("tb_ownerPrayer", cv, "GUID = '" + GUID + "'", null);
    }

    public void AmenOwnerPrayer(String OwnerPrayerGUID, String whoGUID, String whoName, String whoProfilePicture, boolean amen){
        SQLiteDatabase db = getWritableDatabase();
        if(amen) {
            ContentValues cv = new ContentValues();
            cv.put("WhoGUID", whoGUID);
            cv.put("WhoName", whoName);
            cv.put("WhoProfilePicture", whoProfilePicture);
            cv.put("OwnerPrayerGUID", OwnerPrayerGUID);
            db.delete("tb_OwnerPrayerAmen", "OwnerPrayerGUID" + "= '" + OwnerPrayerGUID + "' AND WhoGUID = '" + whoGUID + "'", null);
            db.insert("tb_OwnerPrayerAmen", null, cv);
        }
        else{
            db.delete("tb_OwnerPrayerAmen", "OwnerPrayerGUID" + "= '" + OwnerPrayerGUID + "' AND WhoGUID = '" + whoGUID + "'", null);
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
