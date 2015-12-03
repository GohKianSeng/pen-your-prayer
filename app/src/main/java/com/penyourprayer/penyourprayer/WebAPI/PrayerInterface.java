package com.penyourprayer.penyourprayer.WebAPI;

import com.penyourprayer.penyourprayer.Common.Model.ModelFriendProfile;
import com.penyourprayer.penyourprayer.Common.Model.ModelOwnerPrayer;
import com.penyourprayer.penyourprayer.Common.Model.ModelPayerComment;
import com.penyourprayer.penyourprayer.WebAPI.Model.SimpleJsonResponse;

import java.util.ArrayList;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by sisgks on 05/11/2015.
 */
public interface PrayerInterface {
    @POST("/api/Prayer/AddNewPrayer")
    SimpleJsonResponse AddNewPrayer(@Body ModelOwnerPrayer body);

    @GET("/api/Prayer/UpdatePublicView")
    SimpleJsonResponse UpdatePublicView(@Query("QueueActionGUID") String QueueActionGUID, @Query("PrayerID") String PrayerID, @Query("PublicView") boolean PublicView);

    @POST("/api/Prayer/UpdateTagFriends")
    SimpleJsonResponse UpdateTagFriends(@Query("QueueActionGUID") String QueueActionGUID, @Query("PrayerID") String PrayerID, @Body ArrayList<ModelFriendProfile> body);

    @POST("/api/Prayer/AddNewPrayerComment")
    SimpleJsonResponse AddNewPrayerComment(@Query("QueueActionGUID") String QueueActionGUID, @Query("PrayerID") String PrayerID, @Body ModelPayerComment body);

    @POST("/api/Prayer/UpdatePrayerComment")
    SimpleJsonResponse UpdatePrayerComment(@Query("QueueActionGUID") String QueueActionGUID, @Body ModelPayerComment body);

    @GET("/api/Prayer/DeletePrayerComment")
    SimpleJsonResponse DeletePrayerComment(@Query("QueueActionGUID") String QueueActionGUID, @Query("CommentID") String CommentID);

}