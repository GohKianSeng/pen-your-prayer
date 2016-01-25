package com.penyourprayer.penyourprayer.WebAPI;

import com.penyourprayer.penyourprayer.Common.Model.ModelFriendProfile;
import com.penyourprayer.penyourprayer.Common.Model.ModelOwnerPrayer;
import com.penyourprayer.penyourprayer.Common.Model.ModelPayerAnswered;
import com.penyourprayer.penyourprayer.Common.Model.ModelPayerComment;
import com.penyourprayer.penyourprayer.WebAPI.Model.SimpleJsonResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by sisgks on 05/11/2015.
 */
public interface InterfaceFriends {
    @POST("/api/Friends/GetLatestFriends")
    ArrayList<ModelFriendProfile> GetLatestFriends(@Body ArrayList<ModelFriendProfile> f);


}
