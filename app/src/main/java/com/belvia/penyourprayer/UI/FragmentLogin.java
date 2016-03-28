package com.belvia.penyourprayer.UI;

import android.accounts.Account;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.belvia.penyourprayer.Common.SocialLogin.Facebook;
import com.belvia.penyourprayer.Common.SocialLogin.GooglePlus;
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
import com.belvia.penyourprayer.Common.DataLoading;
import com.belvia.penyourprayer.Common.Model.ModelUserLogin;
import com.belvia.penyourprayer.Common.Utils;
import com.belvia.penyourprayer.Database.Database;
import com.belvia.penyourprayer.QuickstartPreferences;
import com.belvia.penyourprayer.R;
import com.belvia.penyourprayer.WebAPI.Model.SimpleJsonResponse;
import com.belvia.penyourprayer.WebAPI.UserAccountInterface;
import com.belvia.penyourprayer.WebAPI.httpClient;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.models.User;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;

import io.fabric.sdk.android.Fabric;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;

/**
 * A placeholder fragment containing a simple view.
 */
public class FragmentLogin extends Fragment  {

    private RestAdapter adapter;
    private TwitterAuthClient mTwitterAuthClient;
    private MainActivity mainActivity;
    private String TWITTER_KEY = "jSBnTpknelOuZX6e4Cg101oue", TWITTER_SECRET = "w5j7WPwHWwY4DSfJ82tRVZF7SBogZJ6XABptVt431uOowvwFKC";
    //private TextView mTextView;
    private CallbackManager mCallbackManager;
    private Facebook fb;
    private ModelUserLogin user;

    private FacebookCallback<LoginResult> mCallback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            String accesstoken = loginResult.getAccessToken().getToken();
            Profile profile = Profile.getCurrentProfile();

            //13/10/2015 CAAXXIYv53qcBAE1a0ZA0YCPEWMHKCrY9RWoIkDnJl7A3tk4MOZCMep8W5zudStR4aY3X2Gp7AThwtGfaIxBvZCi0YsKWdyYC5xY6EGRwZAomBAKyk6blel62R3j2j9CsjSOk2n6LtQvCVdsXawO1KZBkVpWaZB784KJuQVPfOM4Kt4KSAYYZAfs5Droi5lg2Wjr5B2GKpRV6BZAfFQUZCuGy8lfQTae67viY02uauk1oX6wZDZD
            //get accesstoken for below.  https://graph.facebook.com/oauth/access_token?client_id=1643913965854375&client_secret=c452f73b77ad32cb7ce20aa7b2eb9c2c&grant_type=client_credentials
            //check if user  https://graph.facebook.com/debug_token?access_token=1643913965854375|k20-5EWrMw-Y3HQK9KpriMvqtaI&input_token=CAAXXIYv53qcBABBeCsx36mD8BJZANGjC5lvys8MlHcoSZAGHvmX81UhD7nXQdn4tsG6094oGl3keXgmor7KKz2dEYs801DNbrfMbZCdirQ5I001f4vA8r3kYxSzqsk0cqKG12JdFQwsD6Ig82XAl4sZAqtYsCJAYPTfvFdX5MKsitcPiGAyRrRHi25ZB8ukMzZAZAREqq9qlWyxrvPLSZCUujDX2ZAKRGW2co39fXWHR9vQZDZD
            //access the api on behalf of user. https://graph.facebook.com/oauth/access_token?grant_type=fb_exchange_token&client_id=1643913965854375&client_secret=c452f73b77ad32cb7ce20aa7b2eb9c2c&fb_exchange_token=CAAXXIYv53qcBAGz20x10buKcCgUq1YgrZB36mMKhqtFRN7vtjaxK84ZBZBocMXvlgkQTwySPVTCXxCREVJZCHX2riODZAof9K7wV7Do1VVoElJw2YvD4OYhN0wDIjBZCEAz4NvjM5yfYa2W8r3Uf4pWh36RmkHXBXeDV6vPUlDOsqpNBeHyUKkZCmZC0ZCas7lB8RuXRagX5NCqd48QkmuOZCbZBf9L8xBwpN5tUHscWjOe2QZDZD
            if(profile != null){

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
            showLoginComponent(View.VISIBLE);
        }

        @Override
        public void onError(FacebookException error) {
            showLoginComponent(View.VISIBLE);
        }
    };

    private EditText email;
    private EditText password;
    private Button emailLogin;
    private TextView alternativeLoginTextView;
    private View alternativeLoginLayout, otherLayout;
    private ProgressBar loginProgressbar;


    public FragmentLogin() {
    }

    @Override
    public void onCreate(Bundle safeInstanceState){
        super.onCreate(safeInstanceState);
        mainActivity = ((MainActivity) getActivity());
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        mCallbackManager = CallbackManager.Factory.create();

        user = new ModelUserLogin();

        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this.getActivity(), new Twitter(authConfig));
        mTwitterAuthClient= new TwitterAuthClient();

        adapter = new RestAdapter.Builder()
                .setEndpoint(QuickstartPreferences.api_server)
                .setClient(new OkClient(new httpClient("ANONYMOUS", Utils.TempUserID(mainActivity), QuickstartPreferences.AnonymousHMACKey)))
                .build();


        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.hide();

        mainActivity.lockDrawer(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }



    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loginProgressbar = (ProgressBar) view.findViewById(R.id.login_progressBar);
        email = (EditText) view.findViewById(R.id.email_editText);
        password = (EditText) view.findViewById(R.id.password_editText);
        emailLogin = (Button) view.findViewById(R.id.email_login_button);
        alternativeLoginTextView = (TextView) view.findViewById(R.id.alternativelogin_textView);
        alternativeLoginLayout = view.findViewById(R.id.AlternativeLoginLayout);
        otherLayout = view.findViewById(R.id.OtherLayout);

        LoginManager.getInstance().registerCallback(mCallbackManager, mCallback);

        view.findViewById(R.id.forgot_password_textView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.replaceWithResetPasswordFragment();
            }
        });

        ((ImageButton)view.findViewById(R.id.socal_login_facebook_imageButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLoginComponent(View.GONE);
                fb = new Facebook(mainActivity);
                fb.loginFacebook();
                //LoginManager.getInstance().logInWithReadPermissions(mainActivity, Arrays.asList("public_profile", "email", "user_friends"));
            }
        });

        ((ImageButton) view.findViewById(R.id.socal_login_googleplus_imageButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoginComponent(View.GONE);
                loginProgressbar.setVisibility(View.VISIBLE);
                GooglePlus gp = new GooglePlus(mainActivity);
                gp.loginGooglePlus();
            }
        });

        ((ImageButton) view.findViewById(R.id.socal_login_twitter_imageButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoginComponent(View.GONE);
                loginProgressbar.setVisibility(View.VISIBLE);
                mTwitterAuthClient.authorize(mainActivity, new com.twitter.sdk.android.core.Callback<TwitterSession>() {

                    @Override
                    public void success(Result<TwitterSession> twitterSessionResult) {

                        final ModelUserLogin user = new ModelUserLogin();
                        user.loginType = ModelUserLogin.LoginType.Twitter;
                        user.UserName = String.valueOf(twitterSessionResult.data.getId());
                        user.accessToken = twitterSessionResult.data.getAuthToken().token;
                        user.password_secret = twitterSessionResult.data.getAuthToken().secret;

                        TwitterSession session = Twitter.getSessionManager().getActiveSession();
                        Twitter.getApiClient(session).getAccountService()
                                .verifyCredentials(true, false, new Callback<User>() {

                                    @Override
                                    public void success(Result<User> userResult) {

                                        user.Name = userResult.data.name;
                                        user.URLPictureProfile = userResult.data.profileBackgroundImageUrlHttps;
                                        startLoginProcess(user);
                                    }

                                    @Override
                                    public void failure(TwitterException e) {

                                    }

                                });
                    }

                    @Override
                    public void failure(TwitterException e) {
                        e.printStackTrace();
                    }
                });
            }
        });


        emailLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(email.getText().toString().trim().length() > 0 && password.getText().toString().length()>0) {

                    showLoginComponent(View.GONE);
                    loginProgressbar.setVisibility(View.VISIBLE);

                    ModelUserLogin user = new ModelUserLogin();
                    user.UserName = email.getText().toString();
                    user.password_secret = password.getText().toString();
                    user.loginType = ModelUserLogin.LoginType.Email;

                    startLoginProcess(user);
                }
            }
        });

        view.findViewById(R.id.email_signup_textView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.replaceWithSignUpFragment();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(fb != null)
            fb.mCallbackManager.onActivityResult(requestCode, resultCode, data);

        mTwitterAuthClient.onActivityResult(requestCode, resultCode, data);
    }

    private void startLoginProcess(ModelUserLogin user){
        user.GoogleCloudMessagingDeviceID = mainActivity.sharedPreferences.getString(QuickstartPreferences.DeviceRegistrationToken, "");

        if(user.GoogleCloudMessagingDeviceID.length() == 0) {
            //loginProgressbar.setVisibility(View.GONE);
            //showLoginComponent(View.VISIBLE);
            //show some error meessage here
        }
        else{
            UserAccountInterface useracctInt = adapter.create(UserAccountInterface.class);
            useracctInt.Login(user.loginType.toString(), user.UserName, "Android", user.password_secret, user.GoogleCloudMessagingDeviceID, "", new retrofit.Callback<ModelUserLogin>() {
                @Override
                public void success(ModelUserLogin model, Response response) {
                    if(!model.EmailVerification){
                        new AlertDialog.Builder(mainActivity)
                                .setTitle("Account not activated!")
                                .setMessage("Please check your email to activate your account. if your did not receive, please click resend.")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        showLoginComponent(View.VISIBLE);
                                        loginProgressbar.setVisibility(View.GONE);
                                    }
                                })
                                .setNegativeButton("Resend", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        ResendAccountActivation();
                                    }
                                })
                                .show();
                    }
                    else if(model.HMACHashKey == null || model.HMACHashKey.length() == 0){

                        new AlertDialog.Builder(mainActivity)
                                .setTitle("Login Fail!")
                                .setMessage("Invalid Credentials.")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        showLoginComponent(View.VISIBLE);
                                        loginProgressbar.setVisibility(View.GONE);
                                    }
                                })
                                .show();

                    }
                    else{

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
                    if(error.getLocalizedMessage().compareToIgnoreCase("Canceled") == 0 && error.getKind().compareTo(RetrofitError.Kind.NETWORK) == 0){

                        new AlertDialog.Builder(mainActivity)
                                .setTitle("Login Fail!")
                                .setMessage("No Internet Connection")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        showLoginComponent(View.VISIBLE);
                                        loginProgressbar.setVisibility(View.GONE);
                                    }
                                })
                                .show();
                    }
                    else {
                        new AlertDialog.Builder(mainActivity)
                                .setTitle("Login Fail!")
                                .setMessage("Invalid Credentials.")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        showLoginComponent(View.VISIBLE);
                                        loginProgressbar.setVisibility(View.GONE);
                                    }
                                })
                                .show();
                    }
                }
            });
        }
    }

    private void ResendAccountActivation(){

        showLoginComponent(View.VISIBLE);
        loginProgressbar.setVisibility(View.GONE);

        UserAccountInterface userInterface = adapter.create(UserAccountInterface.class);
        userInterface.ResendAccountActivation("Email", email.getText().toString(), "", new retrofit.Callback<SimpleJsonResponse>() {
            @Override
            public void success(SimpleJsonResponse model, Response response) {
                Toast.makeText(mainActivity, "Activation Email Resent", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });

    }

    private void showLoginComponent(int show){
        email.setVisibility(show);
        password.setVisibility(show);
        emailLogin.setVisibility(show);
        alternativeLoginTextView.setVisibility(show);
        alternativeLoginLayout.setVisibility(show);
        otherLayout.setVisibility(show);
    }
}