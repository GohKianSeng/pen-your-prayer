package com.penyourprayer.penyourprayer.WebAPI;

import com.penyourprayer.penyourprayer.WebAPI.Model.IPAddressLocale;
import com.penyourprayer.penyourprayer.WebAPI.Model.SimpleJsonResponse;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by sisgks on 05/11/2015.
 */
public interface UserAccountInterface {
    @POST("/api/useraccount")
    void registerNewUser(@Query("LoginType") String LoginType, @Query("UserName") String UserName,
                         @Query("Name") String Name, @Query("ProfilePictureURL") String ProfilePictureURL,
                         @Query("Password") String Password, @Query("MobilePlatform") String MobilePlatform,
                         @Query("PushNotificationID") String PushNotificationID,
                         @Query("Country") String Country, @Query("Region") String Region, @Query("City") String City,

                         @Body String body, Callback<SimpleJsonResponse> cb);

    @GET("/api/useraccount")
    void CheckUserNameExists(@Query("LoginType") String LoginType, @Query("UserName") String UserName,
                         @Query("checkExist") boolean checkExist, Callback<SimpleJsonResponse> cb);
}
