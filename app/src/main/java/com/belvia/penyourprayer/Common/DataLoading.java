package com.belvia.penyourprayer.Common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.belvia.penyourprayer.Common.Model.ModelFriendProfile;
import com.belvia.penyourprayer.Common.Model.ModelOwnerPrayer;
import com.belvia.penyourprayer.Common.Model.ModelPrayerRequest;
import com.belvia.penyourprayer.Database.Database;
import com.belvia.penyourprayer.QuickstartPreferences;
import com.belvia.penyourprayer.UI.MainActivity;
import com.belvia.penyourprayer.WebAPI.InterfaceFriends;
import com.belvia.penyourprayer.WebAPI.PrayerInterface;
import com.belvia.penyourprayer.WebAPI.httpClient;

import java.util.ArrayList;

import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

/**
 * Created by sisgks on 20/01/2016.
 */
public class DataLoading {

    MainActivity mainActivity;
    Database db;

    public DataLoading(MainActivity activity){
        this.mainActivity = activity;
        db = new Database(mainActivity);
    }

    public void fetchLatestDataFromServer(){
        getAllPrayerRequest();
        getLatestFriends();
        getLatestOwnerPrayer();
    }

    public void getAllPrayerRequest(){
        db = new Database(mainActivity);

        String OwnerID = String.valueOf(mainActivity.sharedPreferences.getLong(QuickstartPreferences.OwnerID, -1));
        String loginType = mainActivity.sharedPreferences.getString(QuickstartPreferences.OwnerLoginType, "");
        String HMacKey = mainActivity.sharedPreferences.getString(QuickstartPreferences.OwnerHMACKey, "");
        String username = mainActivity.sharedPreferences.getString(QuickstartPreferences.OwnerUserName, "");
        Gson gson = new GsonBuilder().setDateFormat(QuickstartPreferences.DefaultTimeFormat).create();
        RestAdapter adapter = new RestAdapter.Builder()
                .setConverter(new GsonConverter(gson))
                .setEndpoint(QuickstartPreferences.api_server)
                .setClient(new OkClient(new httpClient(loginType, username, HMacKey)))
                .build();


        ArrayList<ModelPrayerRequest> pr = db.getAllPrayerRequest_IDOnly();
        PrayerInterface int_pr = adapter.create(PrayerInterface.class);
        ArrayList<ModelPrayerRequest> latest_pr = int_pr.GetLatestPrayerRequest("a",pr);
        db.addPrayerRequest(latest_pr);
    }

    public void getLatestFriends(){
        db = new Database(mainActivity);

        String OwnerID = String.valueOf(mainActivity.sharedPreferences.getLong(QuickstartPreferences.OwnerID, -1));
        String loginType = mainActivity.sharedPreferences.getString(QuickstartPreferences.OwnerLoginType, "");
        String HMacKey = mainActivity.sharedPreferences.getString(QuickstartPreferences.OwnerHMACKey, "");
        String username = mainActivity.sharedPreferences.getString(QuickstartPreferences.OwnerUserName, "");
        Gson gson = new GsonBuilder().setDateFormat(QuickstartPreferences.DefaultTimeFormat).create();
        RestAdapter adapter = new RestAdapter.Builder()
                .setConverter(new GsonConverter(gson))
                .setEndpoint(QuickstartPreferences.api_server)
                .setClient(new OkClient(new httpClient(loginType, username, HMacKey)))
                .build();

        ArrayList<ModelFriendProfile> f = db.getAllFriends_IDOnly(OwnerID);

        InterfaceFriends intf = adapter.create(InterfaceFriends.class);
        ArrayList<ModelFriendProfile> lf = intf.GetLatestFriends(f);
        db.addFriends(OwnerID, lf);
    }

    public void getLatestOwnerPrayer(){
        String loginType = mainActivity.sharedPreferences.getString(QuickstartPreferences.OwnerLoginType, "");
        String HMacKey = mainActivity.sharedPreferences.getString(QuickstartPreferences.OwnerHMACKey, "");
        String username = mainActivity.sharedPreferences.getString(QuickstartPreferences.OwnerUserName, "");
        String displayName = mainActivity.sharedPreferences.getString(QuickstartPreferences.OwnerDisplayName, "");
        String profilePixURL = mainActivity.sharedPreferences.getString(QuickstartPreferences.OwnerProfilePictureURL, "");
        String userID = String.valueOf(mainActivity.sharedPreferences.getLong(QuickstartPreferences.OwnerID, -1));
        Gson gson = new GsonBuilder().setDateFormat(QuickstartPreferences.DefaultTimeFormat).create();
        RestAdapter adapter = new RestAdapter.Builder()
                .setConverter(new GsonConverter(gson))
                .setEndpoint(QuickstartPreferences.api_server)
                .setClient(new OkClient(new httpClient(loginType, username, HMacKey)))
                .build();

        db = new Database(mainActivity);
        String latestPrayerID = db.getLastPrayerIDFromServer();

        PrayerInterface prayerInterface = adapter.create(PrayerInterface.class);
        ArrayList<ModelOwnerPrayer> lp = prayerInterface.GetLatestPrayers(latestPrayerID);
        db.AddPrayers(lp, userID, displayName, profilePixURL);
    }
}