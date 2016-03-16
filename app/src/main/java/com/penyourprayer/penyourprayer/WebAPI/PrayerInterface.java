package com.penyourprayer.penyourprayer.WebAPI;

import com.penyourprayer.penyourprayer.Common.Model.ModelFriendProfile;
import com.penyourprayer.penyourprayer.Common.Model.ModelOwnerPrayer;
import com.penyourprayer.penyourprayer.Common.Model.ModelPrayerAnswered;
import com.penyourprayer.penyourprayer.Common.Model.ModelPrayerComment;
import com.penyourprayer.penyourprayer.Common.Model.ModelPrayerRequest;
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
    @GET("/api/Prayer/GetLatestPrayers")
    ArrayList<ModelOwnerPrayer> GetLatestPrayers(@Query("PrayerID") String PrayerID);

    @POST("/api/Prayer/AddNewPrayer")
    SimpleJsonResponse AddNewPrayer(@Body ModelOwnerPrayer body);

    @GET("/api/Prayer/DeletePrayer")
    SimpleJsonResponse DeletePrayer(@Query("QueueActionGUID") String QueueActionGUID, @Query("PrayerID") String PrayerID);

    @GET("/api/Prayer/UpdatePublicView")
    SimpleJsonResponse UpdatePublicView(@Query("QueueActionGUID") String QueueActionGUID, @Query("PrayerID") String PrayerID, @Query("PublicView") boolean PublicView);

    @POST("/api/Prayer/UpdateTagFriends")
    SimpleJsonResponse UpdateTagFriends(@Query("QueueActionGUID") String QueueActionGUID, @Query("PrayerID") String PrayerID, @Body ArrayList<ModelFriendProfile> body);

    @POST("/api/Prayer/AddNewPrayerComment")
    SimpleJsonResponse AddNewPrayerComment(@Query("QueueActionGUID") String QueueActionGUID, @Query("PrayerID") String PrayerID, @Body ModelPrayerComment body);

    @POST("/api/Prayer/UpdatePrayerComment")
    SimpleJsonResponse UpdatePrayerComment(@Query("QueueActionGUID") String QueueActionGUID, @Body ModelPrayerComment body);

    @GET("/api/Prayer/DeletePrayerComment")
    SimpleJsonResponse DeletePrayerComment(@Query("QueueActionGUID") String QueueActionGUID, @Query("CommentID") String CommentID);

    @GET("/api/Prayer/AddNewPrayerAmen")
    SimpleJsonResponse AddNewPrayerAmen(@Query("QueueActionGUID") String QueueActionGUID, @Query("PrayerID") String PrayerID);

    @GET("/api/Prayer/DeletePrayerAmen")
    SimpleJsonResponse DeletePrayerAmen(@Query("QueueActionGUID") String QueueActionGUID, @Query("PrayerID") String PrayerID);

    @POST("/api/Prayer/AddNewPrayerAnswered")
    SimpleJsonResponse AddNewPrayerAnswered(@Query("QueueActionGUID") String QueueActionGUID, @Query("PrayerID") String PrayerID, @Body ModelPrayerAnswered body);

    @POST("/api/Prayer/AddNewPrayerRequest")
    SimpleJsonResponse AddNewPrayerRequest(@Query("QueueActionGUID") String QueueActionGUID, @Body ModelPrayerRequest body);

    @POST("/api/Prayer/UpdatePrayerRequest")
    SimpleJsonResponse UpdatePrayerRequest(@Query("QueueActionGUID") String QueueActionGUID, @Body ModelPrayerRequest body);

    @POST("/api/Prayer/GetLatestPrayerRequest")
    ArrayList<ModelPrayerRequest> GetLatestPrayerRequest(@Query("Useless") String Useless, @Body ArrayList<ModelPrayerRequest> pr);

    @GET("/api/Prayer/DeletePrayerRequest")
    SimpleJsonResponse DeletePrayerRequest(@Query("QueueActionGUID") String QueueActionGUID, @Query("PrayerRequestID") String PrayerRequestID);
}
