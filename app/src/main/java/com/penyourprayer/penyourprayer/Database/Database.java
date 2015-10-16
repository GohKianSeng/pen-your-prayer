package com.penyourprayer.penyourprayer.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.penyourprayer.penyourprayer.Common.FriendProfileModel;

import java.util.ArrayList;

/**
 * Created by sisgks on 21/09/2015.
 */
public class Database extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 3;

    // Database Name
    private static final String DATABASE_NAME = "PenYourPrayerSQLite";
    private static final String tb_Friends = "CREATE TABLE tb_Friends ('GUID' TEXT NOT NULL UNIQUE, 'Name' TEXT NOT NULL, 'ProfilePicture' TEXT, PRIMARY KEY(GUID))";
    private static final String tb_OwnerPrayerAmen = "CREATE TABLE tb_OwnerPrayerAmen ('OwnerPrayerGUID' TEXT NOT NULL,'WhoGUID' TEXT NOT NULL,'WhoName' TEXT NOT NULL,'WhoProfilePicture' TEXT, 'CreatedWhen' INTEGER DEFAULT CURRENT_TIMESTAMP, PRIMARY KEY(OwnerPrayerGUID,WhoGUID))";
    private static final String tb_OwnerPrayerComment = "CREATE TABLE tb_OwnerPrayerComment ('GUID' TEXT NOT NULL,'OwnerPrayerGUID' TEXT NOT NULL,'WhoGUID' TEXT NOT NULL,'WhoName' TEXT NOT NULL,'WhoProfilePicture' TEXT,'Comment' TEXT NOT NULL,'CreatedWhen' INTEGER DEFAULT CURRENT_TIMESTAMP,'TouchedWhen' INTEGER DEFAULT CURRENT_TIMESTAMP,PRIMARY KEY(GUID))";
    private static final String tb_OwnerPrayerTagFriends = "CREATE TABLE tb_OwnerPrayerTagFriends ( 'OwnerPrayerGUID' TEXT NOT NULL, 'WhoGUID' TEXT NOT NULL, 'WhoName' TEXT NOT NULL, 'WhoProfilePicture' TEXT, PRIMARY KEY(OwnerPrayerGUID,WhoGUID))";
    private static final String tb_ownerPrayer = "CREATE TABLE tb_ownerPrayer ( 'GUID' TEXT NOT NULL UNIQUE, 'CreatedWhen' INTEGER NOT NULL DEFAULT CURRENT_TIMESTAMP, 'TouchedWhen' INTEGER NOT NULL DEFAULT CURRENT_TIMESTAMP, 'Content' TEXT NOT NULL, 'PublicView' INTEGER DEFAULT 0, 'Deleted' INTEGER DEFAULT 0, PRIMARY KEY(GUID))";


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
}
