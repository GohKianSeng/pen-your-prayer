package com.belvia.penyourprayer.WebAPI;

import com.belvia.penyourprayer.Common.Model.ModelFriendProfile;
import com.belvia.penyourprayer.Common.Model.ModelPrayer;
import com.belvia.penyourprayer.Common.Model.ModelPrayerAnswered;
import com.belvia.penyourprayer.Common.Model.ModelPrayerComment;
import com.belvia.penyourprayer.Common.Model.ModelPrayerCommentReply;
import com.belvia.penyourprayer.Common.Model.ModelPrayerRequest;
import com.belvia.penyourprayer.WebAPI.Model.SimpleJsonResponse;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by sisgks on 05/11/2015.
 */
public interface PrayerInterface {
    @GET("/api/Prayer/GetLatestPrayers")
    ArrayList<ModelPrayer> GetLatestPrayers(@Query("PrayerID") String PrayerID);

    @GET("/api/Prayer/GetLatestOthersPrayers")
    ArrayList<ModelPrayer> GetLatestOthersPrayers(@Query("useless") String useless);

    @GET("/api/Prayer/GetLatestFriendsPrayers")
    ArrayList<ModelPrayer> GetLatestFriendsPrayers(@Query("useless") String useless);

    @GET("/api/Prayer/GetLatestFriendsPrayers")
    void GetLatestFriendsPrayersWithCallback(@Query("useless") String useless, Callback<ArrayList<ModelPrayer>> cb);

    @GET("/api/Prayer/GetLatestOthersPrayers")
    void GetLatestOthersPrayersWithCallback(@Query("useless") String useless, Callback<ArrayList<ModelPrayer>> cb);

    @GET("/api/Prayer/GetPastFriendsPrayers")
    void GetPastFriendsPrayers(@Query("LastPrayerID") String LastPrayerID, Callback<ArrayList<ModelPrayer>> cb);

    @GET("/api/Prayer/GetPastOthersPrayers")
    void GetPastOthersPrayers(@Query("LastPrayerID") String LastPrayerID, Callback<ArrayList<ModelPrayer>> cb);

    @POST("/api/Prayer/AddNewPrayer")
    SimpleJsonResponse AddNewPrayer(@Body ModelPrayer body);

    @GET("/api/Prayer/DeletePrayer")
    SimpleJsonResponse DeletePrayer(@Query("QueueActionGUID") String QueueActionGUID, @Query("PrayerID") String PrayerID);

    @GET("/api/Prayer/UpdatePublicView")
    SimpleJsonResponse UpdatePublicView(@Query("QueueActionGUID") String QueueActionGUID, @Query("PrayerID") String PrayerID, @Query("PublicView") boolean PublicView);

    @POST("/api/Prayer/UpdateTagFriends")
    SimpleJsonResponse UpdateTagFriends(@Query("QueueActionGUID") String QueueActionGUID, @Query("PrayerID") String PrayerID, @Body ArrayList<ModelFriendProfile> body);

    @POST("/api/Prayer/AddNewPrayerComment")
    SimpleJsonResponse AddNewPrayerComment(@Query("QueueActionGUID") String QueueActionGUID, @Query("PrayerID") String PrayerID, @Body ModelPrayerComment body);

    @POST("/api/Prayer/AddNewPrayerCommentReply")
    SimpleJsonResponse AddNewPrayerCommentReply(@Query("QueueActionGUID") String QueueActionGUID, @Query("PrayerID") String PrayerID, @Query("MainCommentID") String MainCommentID, @Body ModelPrayerCommentReply body);

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

    @POST("/api/Prayer/UpdatePrayerAnswered")
    SimpleJsonResponse UpdatePrayerAnswered(@Query("QueueActionGUID") String QueueActionGUID, @Query("PrayerID") String PrayerID, @Body ModelPrayerAnswered body);

    @GET("/api/Prayer/DeletePrayerAnswered")
    SimpleJsonResponse DeletePrayerAnswered(@Query("QueueActionGUID") String QueueActionGUID, @Query("AnsweredID") String AnsweredID);

    @POST("/api/Prayer/AddNewPrayerRequest")
    SimpleJsonResponse AddNewPrayerRequest(@Query("QueueActionGUID") String QueueActionGUID, @Body ModelPrayerRequest body);

    @POST("/api/Prayer/UpdatePrayerRequest")
    SimpleJsonResponse UpdatePrayerRequest(@Query("QueueActionGUID") String QueueActionGUID, @Body ModelPrayerRequest body);

    @POST("/api/Prayer/GetLatestPrayerRequest")
    ArrayList<ModelPrayerRequest> GetLatestPrayerRequest(@Query("Useless") String Useless, @Body ArrayList<ModelPrayerRequest> pr);

    @GET("/api/Prayer/DeletePrayerRequest")
    SimpleJsonResponse DeletePrayerRequest(@Query("QueueActionGUID") String QueueActionGUID, @Query("PrayerRequestID") String PrayerRequestID);
}
