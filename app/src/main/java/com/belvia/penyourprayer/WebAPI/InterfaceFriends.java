package com.belvia.penyourprayer.WebAPI;

import com.belvia.penyourprayer.Common.Model.ModelFriendProfile;

import java.util.ArrayList;

import retrofit.http.Body;
import retrofit.http.POST;

/**
 * Created by sisgks on 05/11/2015.
 */
public interface InterfaceFriends {
    @POST("/api/Friends/GetLatestFriends")
    ArrayList<ModelFriendProfile> GetLatestFriends(@Body ArrayList<ModelFriendProfile> f);


}
