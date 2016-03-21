package com.belvia.penyourprayer.WebAPI.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by sisgks on 05/11/2015.
 */
public class IPAddressLocale {

    @SerializedName("country")
    @Expose
    public String country = "";

    @SerializedName("country_code")
    @Expose
    public String country_code = "";

    @SerializedName("country_code3")
    @Expose
    public String country_code3 = "";

    @SerializedName("region_code")
    @Expose
    public String region_code = "";

    @SerializedName("region")
    @Expose
    public String region = "";

    @SerializedName("city")
    @Expose
    public String city = "";



}
