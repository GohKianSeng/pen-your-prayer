package com.belvia.penyourprayer.Common;

import com.belvia.penyourprayer.Common.Model.ModelFriendProfile;
import com.belvia.penyourprayer.Common.Model.ModelPrayer;
import com.belvia.penyourprayer.Common.Model.ModelPrayerRequest;
import com.belvia.penyourprayer.Common.Model.ModelUserLogin;
import com.belvia.penyourprayer.Database.Database;
import com.belvia.penyourprayer.QuickstartPreferences;
import com.belvia.penyourprayer.UI.MainActivity;
import com.belvia.penyourprayer.WebAPI.InterfaceFriends;
import com.belvia.penyourprayer.WebAPI.PrayerInterface;
import com.belvia.penyourprayer.WebAPI.httpClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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

    String OwnerID;
    String loginType;
    String HMacKey;
    String username;
    Gson gson;
    RestAdapter adapter;

    public DataLoading(MainActivity activity){
        this.mainActivity = activity;
        db = new Database(mainActivity);
    }

    public void fetchLatestDataFromServer(){
        OwnerID = String.valueOf(mainActivity.sharedPreferences.getLong(QuickstartPreferences.OwnerID, -1));
        loginType = mainActivity.sharedPreferences.getString(QuickstartPreferences.OwnerLoginType, "");
        HMacKey = mainActivity.sharedPreferences.getString(QuickstartPreferences.OwnerHMACKey, "");
        username = mainActivity.sharedPreferences.getString(QuickstartPreferences.OwnerUserName, "");

        gson = new GsonBuilder().setDateFormat(QuickstartPreferences.DefaultTimeFormat).create();
        adapter = new RestAdapter.Builder()
                .setConverter(new GsonConverter(gson))
                .setEndpoint(QuickstartPreferences.api_server)
                .setClient(new OkClient(new httpClient(loginType, username, HMacKey)))
                .build();

        mainActivity.setInitialSplashProgressText("Loading your latest prayer request ...");
        getAllPrayerRequest();
        mainActivity.setInitialSplashProgressText("Loading any new friends ...");
        getLatestFriends();
        mainActivity.setInitialSplashProgressText("Loading your latest prayers ...");
        getLatestOwnerPrayer();
        mainActivity.setInitialSplashProgressText("Loading latest public's prayers ...");
        getLatestOthersPrayer();
        mainActivity.setInitialSplashProgressText("Loading latest friends' prayers ...");
        getLatestFriendsPrayer();
    }

    public void getAllPrayerRequest(){
        db = new Database(mainActivity);

        ArrayList<ModelPrayerRequest> pr = db.getAllPrayerRequest_IDOnly();
        PrayerInterface int_pr = adapter.create(PrayerInterface.class);
        ArrayList<ModelPrayerRequest> latest_pr = int_pr.GetLatestPrayerRequest("a", pr);
        db.addPrayerRequest(latest_pr);
    }

    public void getLatestFriends(){
        db = new Database(mainActivity);

        ArrayList<ModelFriendProfile> f = db.getAllFriends_IDOnly(OwnerID);

        InterfaceFriends intf = adapter.create(InterfaceFriends.class);
        ArrayList<ModelFriendProfile> lf = intf.GetLatestFriends(f);
        db.addFriends(OwnerID, lf);
    }

    public void getLatestOwnerPrayer(){
        db = new Database(mainActivity);
        String latestPrayerID = db.getLastPrayerIDFromServer();

        PrayerInterface prayerInterface = adapter.create(PrayerInterface.class);
        ArrayList<ModelPrayer> lp = prayerInterface.GetLatestPrayers(latestPrayerID);
        db.AddPrayers(lp);

        //do once only.
        db.removeAllStrangers();
    }

    public void getLatestOthersPrayer(){
        PrayerInterface prayerInterface = adapter.create(PrayerInterface.class);
        ArrayList<ModelPrayer> lp = prayerInterface.GetLatestOthersPrayers("useless");
        ModelUserLogin d = lp.get(0).OwnerProfile;
        db.removeOthersPrayers();
        db.AddPrayers(lp);
    }

    public void getLatestFriendsPrayer(){
        PrayerInterface prayerInterface = adapter.create(PrayerInterface.class);
        ArrayList<ModelPrayer> lp = prayerInterface.GetLatestFriendsPrayers("useless");
        db.removeFriendsPrayers();
        db.AddPrayers(lp);
    }

}
