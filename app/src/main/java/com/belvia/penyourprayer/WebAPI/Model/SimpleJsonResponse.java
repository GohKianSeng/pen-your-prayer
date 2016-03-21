package com.belvia.penyourprayer.WebAPI.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by sisgks on 05/11/2015.
 */
public class SimpleJsonResponse {

    @SerializedName("StatusCode")
    @Expose
    public int StatusCode;

    @SerializedName("MessageType")
    @Expose
    public String MessageType;

    @SerializedName("Description")
    @Expose
    public String Description;
}
