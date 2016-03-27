package com.belvia.penyourprayer.WebAPI;

import com.belvia.penyourprayer.Common.Model.ModelUserLogin;
import com.belvia.penyourprayer.WebAPI.Model.SimpleJsonResponse;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by sisgks on 05/11/2015.
 */
public interface UserAccountInterface {
    @POST("/api/useraccount/RegisterNewUser")
    void registerNewUser(@Query("LoginType") String LoginType, @Query("UserName") String UserName,
                         @Query("Name") String Name, @Query("ProfilePictureURL") String ProfilePictureURL,
                         @Query("Password") String Password, @Query("MobilePlatform") String MobilePlatform,
                         @Query("PushNotificationID") String PushNotificationID,
                         @Query("Country") String Country, @Query("Region") String Region, @Query("City") String City,

                         @Body String body, Callback<SimpleJsonResponse> cb);

    @POST("/api/useraccount/CheckUserNameExists")
    void CheckUserNameExists(@Query("LoginType") String LoginType, @Query("UserName") String UserName,
                             @Body String body, Callback<SimpleJsonResponse> cb);

    @POST("/api/useraccount/ResendAccountActivation")
    void ResendAccountActivation(@Query("LoginType") String LoginType, @Query("UserName") String UserName,
                                 @Body String body, Callback<SimpleJsonResponse> cb);

    @POST("/api/useraccount/ResetPassword")
    void ResetPassword(@Query("LoginType") String LoginType, @Query("UserName") String UserName,
                                 @Body String body, Callback<SimpleJsonResponse> cb);

    @POST("/api/useraccount/Login")
    void Login(@Query("LoginType") String LoginType, @Query("UserName") String UserName,
               @Query("@MobilePlatform") String MobilePlatform, @Query("Password") String Password,
               @Query("PushNotificationID") String PushNotificationID,
               @Body String body, Callback<ModelUserLogin> cb);

    @POST("/api/useraccount/SocialLogin")
    void SocialLogin(@Query("LoginType") String LoginType, @Query("UserName") String UserName,
                     @Query("Secret") String Secret, @Query("AccessToken") String AccessToken,
                     @Query("MobilePlatform") String MobilePlatform, @Query("PushNotificationID") String PushNotificationID,
                     @Body String body, Callback<ModelUserLogin> cb);
}


//string LoginType, string UserName, string Secret, string AccessToken, string PushNotificationID, string MobilePlatform