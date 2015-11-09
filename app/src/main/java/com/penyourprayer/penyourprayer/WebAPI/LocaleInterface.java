package com.penyourprayer.penyourprayer.WebAPI;

import com.penyourprayer.penyourprayer.WebAPI.Model.IPAddressLocale;;
import retrofit.Callback;
import retrofit.http.GET;


/**
 * Created by sisgks on 05/11/2015.
 */

public interface LocaleInterface {

    @GET("/geoip")
    void getLocale(Callback<IPAddressLocale> cb);

}

