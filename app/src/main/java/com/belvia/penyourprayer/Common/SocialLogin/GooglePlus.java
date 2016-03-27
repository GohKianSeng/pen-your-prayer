package com.belvia.penyourprayer.Common.SocialLogin;

import android.accounts.Account;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.belvia.penyourprayer.Common.Model.ModelUserLogin;
import com.belvia.penyourprayer.Common.Utils;
import com.belvia.penyourprayer.QuickstartPreferences;
import com.belvia.penyourprayer.UI.MainActivity;
import com.belvia.penyourprayer.WebAPI.UserAccountInterface;
import com.belvia.penyourprayer.WebAPI.httpClient;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import java.io.IOException;

import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;

/**
 * Created by ks on 3/26/2016.
 */
public class GooglePlus implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    public MainActivity mainActivity;
    public boolean mShouldResolve;
    public GooglePlus(MainActivity ma){
        this.mainActivity = ma;
        mShouldResolve = false;
    }

    public void loginGooglePlus(){
        mShouldResolve = true;
        mainActivity.mGoogleApiClient = new GoogleApiClient.Builder(mainActivity)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .enableAutoManage(mainActivity, 22, this)
                .addApi(Plus.API)
                .addScope(new Scope(Scopes.PROFILE))
                .build();
    }

    public void checkLoginStatus(){
        if(mainActivity.mGoogleApiClient.isConnected()){
            //already login can proceed
        }
        else{
            mainActivity.replaceWithLoginFragment();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        // onConnected indicates that an account was selected on the device, that the selected
        // account has granted any requested permissions to our app and that we were able to
        // establish a service connection to Google Play services.
        //Log.d(TAG, "onConnected:" + bundle);
        //mShouldResolve = false;

        // Show the signed-in UI
        //showSignedInUI();
        if (Plus.PeopleApi.getCurrentPerson(mainActivity.mGoogleApiClient) != null) {
            Person currentPerson = Plus.PeopleApi.getCurrentPerson(mainActivity.mGoogleApiClient);
            String personName = currentPerson.getDisplayName();
            String personPhoto = currentPerson.getImage().getUrl();
            String personGooglePlusProfile = currentPerson.getUrl();


            ModelUserLogin user = new ModelUserLogin();
            user.loginType = ModelUserLogin.LoginType.GooglePlus;
            user.UserName = currentPerson.getId();
            user.Name = currentPerson.getDisplayName();
            user.URLPictureProfile = currentPerson.getUrl();




            AsyncTask<ModelUserLogin, Void, ModelUserLogin> task = new AsyncTask<ModelUserLogin, Void, ModelUserLogin>() {
                @Override
                protected ModelUserLogin doInBackground(ModelUserLogin... params) {
                    String token = "";
                    ModelUserLogin user = params[0];
                    String accountName = Plus.AccountApi.getAccountName(mainActivity.mGoogleApiClient);
                    user.SocialMediaEmail = accountName;
                    Account account = new Account(accountName, GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
                    String scopes = "audience:server:client_id:" + "1036182018589-qq5e49a73sc4p0q9f02isfin56snbcsd.apps.googleusercontent.com"; // Not the app's client ID.
                    try {
                        String googleToken = GoogleAuthUtil.getToken(mainActivity, account, scopes);
                        user.accessToken = googleToken;
                    } catch (IOException e) {
                        String sss = "";
                        //Log.e(TAG, "Error retrieving ID token.", e);
                        return null;
                    } catch (GoogleAuthException e) {
                        //Log.e(TAG, "Error retrieving ID token.", e);
                        String ddd = "";
                        return null;
                    }

                    return user;
                    //https://www.googleapis.com/oauth2/v3/tokeninfo?id_token=eyJhbGciOiJSUzI1NiIsImtpZCI6Ijg4NTEyODVhNzM5ZjY0YTY0MGVjOGU5YTc2MjVlMjAzYWMwNGMwOTAifQ.eyJpc3MiOiJhY2NvdW50cy5nb29nbGUuY29tIiwiYXVkIjoiMTAzNjE4MjAxODU4OS1xcTVlNDlhNzNzYzRwMHE5ZjAyaXNmaW41NnNuYmNzZC5hcHBzLmdvb2dsZXVzZXJjb250ZW50LmNvbSIsInN1YiI6IjExNzg4NzA0NTM3ODc4ODY4NTMyOCIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJhenAiOiIxMDM2MTgyMDE4NTg5LWtxMjZmMnFpM2dhZGlvMWFnZ3QwcWFvYzEzZ3Z2NjQ2LmFwcHMuZ29vZ2xldXNlcmNvbnRlbnQuY29tIiwiZW1haWwiOiJ6bml0ZXI4MUBnbWFpbC5jb20iLCJpYXQiOjE0NDE2MTE1NzMsImV4cCI6MTQ0MTYxNTE3MywibmFtZSI6IktpYW4gU2VuZyIsInBpY3R1cmUiOiJodHRwczovL2xoNi5nb29nbGV1c2VyY29udGVudC5jb20vLWNKRVp5aUk5N05VL0FBQUFBQUFBQUFJL0FBQUFBQUFBQnkwLzdGU2dMYmIxd21ZL3M5Ni1jL3Bob3RvLmpwZyIsImdpdmVuX25hbWUiOiJLaWFuIiwiZmFtaWx5X25hbWUiOiJTZW5nIn0.Z-3MpKeZo2GYrMHrCxTMn39clk0JTJOYTubLi4s0q6AXfiqyw2ZLKN_YvugZfNttsfBh4YKnscTXFiAWFpXVyfXkG-s5YpDu1SGz2aVyHZtOIE8WLAXNn3aETuwNIe8BnndbwwSp9b1Hn-z9vj4q1iSM-em0El3z-tHD-VG5e9PDcGP5XLhLQWtX89ClOfsk3xNFMVo__Gd8HIB8BBBqxdX6DT491lRpa78WMA65oQ31TPwAdrcfzi3m9HzIbQTknopwsAPOKd-kBKR66udcaUKZa9fZ_lxhDb_aF_td-CF8Co3YhOPmzJBNWguCtcjB-SqAF5d5w67z-hgbt6TigQ
                    /**
                     *
                     * {
                     "iss": "accounts.google.com",
                     this is my AppID "aud": "1036182018589-qq5e49a73sc4p0q9f02isfin56snbcsd.apps.googleusercontent.com",
                     Signed in unique userid "sub": "117887045378788685328",
                     "email_verified": "true",
                     "azp": "1036182018589-kq26f2qi3gadio1aggt0qaoc13gvv646.apps.googleusercontent.com",
                     "email": "zniter81@gmail.com",
                     "iat": "1441611573",
                     "exp": "1441615173",
                     "name": "Kian Seng",
                     "picture": "https://lh6.googleusercontent.com/-cJEZyiI97NU/AAAAAAAAAAI/AAAAAAAABy0/7FSgLbb1wmY/s96-c/photo.jpg",
                     "given_name": "Kian",
                     "family_name": "Seng",
                     "alg": "RS256",
                     "kid": "8851285a739f64a640ec8e9a7625e203ac04c090"
                     }
                     *
                     *
                     */
                }

                @Override
                protected void onPostExecute(ModelUserLogin user) {
                    canProceedPrayerList(user);
                }

            };

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, user);
            else
                task.execute(user);

        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Could not connect to Google Play Services.  The user needs to select an account,
        // grant permissions or resolve an error in order to sign in. Refer to the javadoc for
        // ConnectionResult to see possible error codes.
        //Log.d(TAG, "onConnectionFailed:" + connectionResult);
        if(!mShouldResolve || !connectionResult.hasResolution()) {
            // show login page
        }
    }

    @Override
    public void onConnectionSuspended(int id){

    }

    private void canProceedPrayerList(ModelUserLogin user){

        user.GoogleCloudMessagingDeviceID = mainActivity.sharedPreferences.getString(QuickstartPreferences.DeviceRegistrationToken, "");

        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(QuickstartPreferences.api_server)
                .setClient(new OkClient(new httpClient("ANONYMOUS", Utils.TempUserID(mainActivity), QuickstartPreferences.AnonymousHMACKey)))
                .build();

        UserAccountInterface useracctInt = adapter.create(UserAccountInterface.class);
        useracctInt.SocialLogin(user.loginType.toString(), user.UserName, "", user.accessToken ,"Android", user.GoogleCloudMessagingDeviceID, "", new retrofit.Callback<ModelUserLogin>() {
            @Override
            public void success(ModelUserLogin model, Response response) {
                if (model.HMACHashKey == null || model.HMACHashKey.length() == 0) {

                    new AlertDialog.Builder(mainActivity)
                            .setTitle("Login Fail!")
                            .setMessage("Invalid Credentials.")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    //showLoginComponent(View.VISIBLE);
                                    //loginProgressbar.setVisibility(View.GONE);
                                }
                            })
                            .show();

                } else {

                    mainActivity.OwnerID = String.valueOf(model.ID);
                    mainActivity.OwnerDisplayName = model.Name;
                    mainActivity.OwnerProfilePictureURL = model.URLPictureProfile;
                    mainActivity.sharedPreferences.edit().putLong(QuickstartPreferences.OwnerID, model.ID).apply();
                    mainActivity.sharedPreferences.edit().putString(QuickstartPreferences.OwnerDisplayName, model.Name).apply();
                    mainActivity.sharedPreferences.edit().putString(QuickstartPreferences.OwnerProfilePictureURL, model.URLPictureProfile).apply();
                    mainActivity.sharedPreferences.edit().putString(QuickstartPreferences.OwnerHMACKey, model.HMACHashKey).apply();
                    mainActivity.sharedPreferences.edit().putString(QuickstartPreferences.OwnerLoginType, model.loginType.toString()).apply();
                    mainActivity.sharedPreferences.edit().putString(QuickstartPreferences.OwnerUserName, model.UserName).apply();

                    //loaddata();


                }
            }

            @Override
            public void failure(RetrofitError error) {
                if (error.getLocalizedMessage().compareToIgnoreCase("Canceled") == 0 && error.getKind().compareTo(RetrofitError.Kind.NETWORK) == 0) {

                    new AlertDialog.Builder(mainActivity)
                            .setTitle("Login Fail!")
                            .setMessage("No Internet Connection")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    //showLoginComponent(View.VISIBLE);
                                    //loginProgressbar.setVisibility(View.GONE);
                                }
                            })
                            .show();
                } else {
                    new AlertDialog.Builder(mainActivity)
                            .setTitle("Login Fail!")
                            .setMessage("Invalid Credentials.")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    //showLoginComponent(View.VISIBLE);
                                    //loginProgressbar.setVisibility(View.GONE);
                                }
                            })
                            .show();
                }
            }
        });



    }
}
