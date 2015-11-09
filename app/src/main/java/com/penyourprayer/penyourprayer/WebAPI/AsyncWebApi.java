package com.penyourprayer.penyourprayer.WebAPI;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.penyourprayer.penyourprayer.Common.UserLoginModel;
import com.penyourprayer.penyourprayer.QuickstartPreferences;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by sisgks on 31/08/2015.
 */

public class AsyncWebApi extends AsyncTask {

    private CookieManager msCookieManager;
    public AsyncWebApiResponse delegate = null;
    private SharedPreferences sharedPreferences;

    public AsyncWebApi(Context context) {
        sharedPreferences = context.getSharedPreferences("PenYourPrayer.SharePreference", Context.MODE_PRIVATE);
        msCookieManager = new java.net.CookieManager();
    }

    public void onCompleteListener(AsyncWebApiResponse asyncResponse){
        delegate = asyncResponse;
    }

    @Override
    protected Object doInBackground(Object... arg0) {
        String type = (String)arg0[0];
        List param = (ArrayList) arg0[1];
        request(getURL(type), param);

        return null;
    }

    protected void onPostExecute(Object result) {
        if(delegate != null)
            delegate.WebAPITaskComplete(result);
    }

    private String getURL(String type) {
        return QuickstartPreferences.api_server + type;
    }

    private StringBuffer request(String urlString, List<WebApiParameter> params) {
        // TODO Auto-generated method stub
        StringBuffer chaine = new StringBuffer("");
        try{
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestProperty("User-Agent","Mozilla/5.0 ( compatible ) ");
            connection.setRequestProperty("Accept", "*/*");
            connection.setRequestMethod("POST");
            connection.setDoInput(true);

            connection.setRequestProperty("Cookie", sharedPreferences.getString(QuickstartPreferences.Cookie, ""));




            OutputStream os = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getQuery(params));
            writer.flush();
            writer.close();


            connection.connect();

            InputStream inputStream = connection.getInputStream();

            BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            while ((line = rd.readLine()) != null) {
                chaine.append(line);
            }

            Map<String, List<String>> headerFields = connection.getHeaderFields();
            List<String> cookiesHeader = headerFields.get("Set-Cookie");
            if(cookiesHeader != null)
            {
                for (String cookie : cookiesHeader)
                {
                    msCookieManager.getCookieStore().add(null, HttpCookie.parse(cookie).get(0));
                }
                sharedPreferences.edit().putString(QuickstartPreferences.Cookie, TextUtils.join(";", msCookieManager.getCookieStore().getCookies())).apply();
            }

        } catch (Exception e) {
            // writing exception to log
            e.printStackTrace();
        }

        return chaine;
    }

    private String getQuery(List<WebApiParameter> params) throws UnsupportedEncodingException
    {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (WebApiParameter pair : params)
        {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(pair.Key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.Value, "UTF-8"));
        }

        return result.toString();
    }

    public void RegisterNewUser(UserLoginModel user){
        List param = new ArrayList();
        param.add(new WebApiParameter("Name", user.Name));
        param.add(new WebApiParameter("UserName", user.ID));
        param.add(new WebApiParameter("URLPictureProfile", user.URLPictureProfile));
        param.add(new WebApiParameter("AccessSecret", user.accessSecret));
        param.add(new WebApiParameter("AccessToken", user.accessToken));
        param.add(new WebApiParameter("LoginType", user.loginType.toString()));
        param.add(new WebApiParameter("DeviceRegistrationToken", user.GoogleCloudMessagingDeviceID));
        //this.execute(QuickstartPreferences.RegisterNewUser, param);
    }

    public void GCMTokenRefresh(String Email, String DeviceRegistrationToken){
        List param = new ArrayList();
        param.add(new WebApiParameter("DeviceRegistrationToken", DeviceRegistrationToken));
        param.add(new WebApiParameter("UserName", Email));
        //this.execute(QuickstartPreferences.GCMTokenRefresh, param);
    }

    public void VerifyUserDevice(String Email, String DeviceRegistrationToken, String VerificationGUID) {
        List param = new ArrayList();
        param.add(new WebApiParameter("VerificationGUID", VerificationGUID));
        param.add(new WebApiParameter("UserName", Email));
        //this.execute(QuickstartPreferences.VerifyUserDevice, param);
    }
}
