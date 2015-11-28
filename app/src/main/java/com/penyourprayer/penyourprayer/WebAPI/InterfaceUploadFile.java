package com.penyourprayer.penyourprayer.WebAPI;

import com.penyourprayer.penyourprayer.WebAPI.Model.SimpleJsonResponse;

import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Query;
import retrofit.mime.TypedFile;

/**
 * Created by sisgks on 05/11/2015.
 */
public interface InterfaceUploadFile {

    @Multipart
    @POST("/api/Attachment/AddPrayerImage")
    SimpleJsonResponse AddPrayerImage(@Query("GUID") String GUID, @Part("pathImage") TypedFile pathImage);

    @GET("/api/Attachment/CheckImageUploaded")
    SimpleJsonResponse CheckImageUploaded(@Query("GUID") String GUID, @Query("filename") String filename);
}
