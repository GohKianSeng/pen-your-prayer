package com.belvia.penyourprayer.Common.SocialLogin;

import android.accounts.Account;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.belvia.penyourprayer.Common.Model.ModelUserLogin;
import com.belvia.penyourprayer.Common.Utils;
import com.belvia.penyourprayer.QuickstartPreferences;
import com.belvia.penyourprayer.R;
import com.belvia.penyourprayer.UI.MainActivity;
import com.belvia.penyourprayer.WebAPI.UserAccountInterface;
import com.belvia.penyourprayer.WebAPI.httpClient;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.People;
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
public class GooglePlus {

    public String signedInID;
    public MainActivity mainActivity;
    public boolean mShouldResolve;
    public GooglePlus(MainActivity ma){
        this.mainActivity = ma;
        mShouldResolve = false;

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken("1036182018589-qq5e49a73sc4p0q9f02isfin56snbcsd.apps.googleusercontent.com")
                .build();

        mainActivity.mGoogleApiClient = new GoogleApiClient.Builder(mainActivity)
                //.enableAutoManage(mainActivity /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    public void AfterActivityResult_Connect(){
        mainActivity.mGoogleApiClient.connect();
    }

    public void loginGooglePlus(){
        mainActivity.StartGoogleSignInActvity();
    }

    public void checkLoginStatus(){
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mainActivity.mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    handleSignInResult(googleSignInResult);
                }
            });
            mainActivity.mGoogleApiClient.connect();
        }
    }

    public void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            ModelUserLogin user = new ModelUserLogin();
            user.loginType = ModelUserLogin.LoginType.GooglePlus;
            user.UserName = acct.getId();
            user.Name = acct.getDisplayName();
            user.URLPictureProfile = acct.getPhotoUrl().toString();
            user.SocialMediaEmail = acct.getEmail();
            user.accessToken = acct.getIdToken();
            canProceedPrayerList(user);
        } else {
            mainActivity.replaceWithLoginFragment();
        }
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

                    mainActivity.loadInitialLaunchData();
                    mainActivity.qa.StartHttpTranmissionQueue();

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
