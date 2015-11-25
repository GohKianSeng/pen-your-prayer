package com.penyourprayer.penyourprayer.WebAPI;

import com.penyourprayer.penyourprayer.Common.OwnerPrayerModel;
import com.penyourprayer.penyourprayer.WebAPI.Model.SimpleJsonResponse;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by sisgks on 05/11/2015.
 */
public interface PrayerInterface {
    @POST("/api/Prayer/AddNewPrayer")
    SimpleJsonResponse AddNewPrayer(@Body OwnerPrayerModel body);
}
