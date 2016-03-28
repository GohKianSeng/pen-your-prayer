package com.belvia.penyourprayer.Common.SocialLogin;

import android.accounts.Account;
import android.app.AlertDialog;
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
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;

import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;

/**
 * Created by ks on 3/26/2016.
 */
public class Facebook implements FacebookCallback<LoginResult>{

    public interface InitializeDone {
        /**
         * Called when the sdk has been initialized.
         */
        void onInitialized();
    }

    public MainActivity mainActivity;
    public CallbackManager mCallbackManager;
    private ModelUserLogin user;

    public Facebook(MainActivity ma, final InitializeDone callback){
        mainActivity = ma;

        FacebookSdk.sdkInitialize(mainActivity.getApplicationContext(), new FacebookSdk.InitializeCallback() {
            @Override
            public void onInitialized() {
                if(callback != null){
                    callback.onInitialized();
                }
            }
        });
        mCallbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(mCallbackManager, this);

        user = new ModelUserLogin();

    }

    public Facebook(MainActivity ma){
        mainActivity = ma;

        FacebookSdk.sdkInitialize(mainActivity.getApplicationContext());
        mCallbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(mCallbackManager, this);

        user = new ModelUserLogin();
    }

    @Override
    public void onSuccess(LoginResult loginResult) {
        Profile profile = Profile.getCurrentProfile();

        //13/10/2015 CAAXXIYv53qcBAE1a0ZA0YCPEWMHKCrY9RWoIkDnJl7A3tk4MOZCMep8W5zudStR4aY3X2Gp7AThwtGfaIxBvZCi0YsKWdyYC5xY6EGRwZAomBAKyk6blel62R3j2j9CsjSOk2n6LtQvCVdsXawO1KZBkVpWaZB784KJuQVPfOM4Kt4KSAYYZAfs5Droi5lg2Wjr5B2GKpRV6BZAfFQUZCuGy8lfQTae67viY02uauk1oX6wZDZD
        //get accesstoken for below.  https://graph.facebook.com/oauth/access_token?client_id=1643913965854375&client_secret=c452f73b77ad32cb7ce20aa7b2eb9c2c&grant_type=client_credentials
        //check if user  https://graph.facebook.com/debug_token?access_token=1643913965854375|k20-5EWrMw-Y3HQK9KpriMvqtaI&input_token=CAAXXIYv53qcBABBeCsx36mD8BJZANGjC5lvys8MlHcoSZAGHvmX81UhD7nXQdn4tsG6094oGl3keXgmor7KKz2dEYs801DNbrfMbZCdirQ5I001f4vA8r3kYxSzqsk0cqKG12JdFQwsD6Ig82XAl4sZAqtYsCJAYPTfvFdX5MKsitcPiGAyRrRHi25ZB8ukMzZAZAREqq9qlWyxrvPLSZCUujDX2ZAKRGW2co39fXWHR9vQZDZD
        //access the api on behalf of user. https://graph.facebook.com/oauth/access_token?grant_type=fb_exchange_token&client_id=1643913965854375&client_secret=c452f73b77ad32cb7ce20aa7b2eb9c2c&fb_exchange_token=CAAXXIYv53qcBAGz20x10buKcCgUq1YgrZB36mMKhqtFRN7vtjaxK84ZBZBocMXvlgkQTwySPVTCXxCREVJZCHX2riODZAof9K7wV7Do1VVoElJw2YvD4OYhN0wDIjBZCEAz4NvjM5yfYa2W8r3Uf4pWh36RmkHXBXeDV6vPUlDOsqpNBeHyUKkZCmZC0ZCas7lB8RuXRagX5NCqd48QkmuOZCbZBf9L8xBwpN5tUHscWjOe2QZDZD
        if(profile != null){

            user.loginType = ModelUserLogin.LoginType.Facebook;
            user.accessToken = loginResult.getAccessToken().getToken();
            user.UserName = profile.getId();
            user.Name = profile.getName();
            user.URLPictureProfile = profile.getProfilePictureUri(50, 50).toString();
        }

        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {
                    user.SocialMediaEmail = object.getString("email");
                    canProceedPrayerList(user);
                }
                catch(Exception e){}

            }

        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id, first_name, last_name, email,gender, birthday, location"); // Parámetros que pedimos a facebook
        request.setParameters(parameters);
        request.executeAsync();
    }

    @Override
    public void onCancel() {

    }

    @Override
    public void onError(FacebookException error) {

    }

    public void loginFacebook(){
        LoginManager.getInstance().logInWithReadPermissions(mainActivity, Arrays.asList("public_profile", "email", "user_friends"));
    }

    public void checkLoginStatus(){
        com.facebook.AccessToken accessToken = com.facebook.AccessToken.getCurrentAccessToken();
        if(accessToken != null){
            loginFacebook();
        }
        else{
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
