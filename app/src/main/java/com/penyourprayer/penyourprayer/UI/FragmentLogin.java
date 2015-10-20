package com.penyourprayer.penyourprayer.UI;

import android.accounts.Account;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
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
import com.penyourprayer.penyourprayer.Common.UserLoginModel;
import com.penyourprayer.penyourprayer.QuickstartPreferences;
import com.penyourprayer.penyourprayer.R;
import com.penyourprayer.penyourprayer.WebAPI.AsyncWebApi;
import com.penyourprayer.penyourprayer.WebAPI.AsyncWebApiResponse;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.models.User;


import android.net.Uri;
import android.widget.ProgressBar;
import android.widget.TextView;


import java.io.IOException;

import java.util.Arrays;


import io.fabric.sdk.android.Fabric;

/**
 * A placeholder fragment containing a simple view.
 */
public class FragmentLogin extends Fragment implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    TwitterAuthClient mTwitterAuthClient;
    private MainActivity mainActivity;
    private String TWITTER_KEY = "jSBnTpknelOuZX6e4Cg101oue", TWITTER_SECRET = "w5j7WPwHWwY4DSfJ82tRVZF7SBogZJ6XABptVt431uOowvwFKC";
    //private TextView mTextView;
    private CallbackManager mCallbackManager;
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

                UserLoginModel user = new UserLoginModel();
                user.accessToken = loginResult.getAccessToken().getToken();
                user.ID = profile.getId();
                user.Name = profile.getName();
                user.URLPictureProfile = profile.getProfilePictureUri(50, 50).toString();

                startLoginProcess(user);
            }
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
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        mCallbackManager = CallbackManager.Factory.create();

        mGoogleApiClient = new GoogleApiClient.Builder(this.getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(new Scope(Scopes.PROFILE))
                //.addScope(new Scope(Scopes.PLUS_LOGIN))
                .build();

        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this.getActivity(), new Twitter(authConfig));
        mTwitterAuthClient= new TwitterAuthClient();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }



    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainActivity = ((MainActivity) getActivity());

        loginProgressbar = (ProgressBar) view.findViewById(R.id.login_progressBar);
        email = (EditText) view.findViewById(R.id.email_editText);
        password = (EditText) view.findViewById(R.id.password_editText);
        emailLogin = (Button) view.findViewById(R.id.email_login_button);
        alternativeLoginTextView = (TextView) view.findViewById(R.id.alternativelogin_textView);
        alternativeLoginLayout = view.findViewById(R.id.AlternativeLoginLayout);
        otherLayout = view.findViewById(R.id.OtherLayout);

        LoginManager.getInstance().registerCallback(mCallbackManager, mCallback);

        ((ImageButton)view.findViewById(R.id.socal_login_facebook_imageButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLoginComponent(View.GONE);
                LoginManager.getInstance().logInWithReadPermissions(mainActivity, Arrays.asList("public_profile"));
            }
        });

        ((ImageButton) view.findViewById(R.id.socal_login_googleplus_imageButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoginComponent(View.GONE);
                loginProgressbar.setVisibility(View.VISIBLE);

                mShouldResolve = true;
                mGoogleApiClient.connect();
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

                        final UserLoginModel user = new UserLoginModel();
                        user.loginType = UserLoginModel.LoginType.Twitter;
                        user.ID = String.valueOf(twitterSessionResult.data.getId());
                        user.accessToken = twitterSessionResult.data.getAuthToken().token;
                        user.accessSecret = twitterSessionResult.data.getAuthToken().secret;

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


        view.findViewById(R.id.email_login_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoginComponent(View.GONE);
                loginProgressbar.setVisibility(View.VISIBLE);

                UserLoginModel user = new UserLoginModel();
                user.ID = email.getText().toString();
                user.password = password.getText().toString();
                user.loginType = UserLoginModel.LoginType.Email;

                startLoginProcess(user);
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
        mCallbackManager.onActivityResult(requestCode, resultCode, data);

        mTwitterAuthClient.onActivityResult(requestCode, resultCode, data);
    }












    //Google Plus

    /* Request code used to invoke sign in user interactions. */
    private static final int RC_SIGN_IN = 0;

    /* Is there a ConnectionResult resolution in progress? */
    private boolean mIsResolving = false;

    /* Should we automatically resolve ConnectionResults when possible? */
    private boolean mShouldResolve = false;

    private GoogleApiClient mGoogleApiClient;

    @Override
    public void onConnected(Bundle bundle) {
        // onConnected indicates that an account was selected on the device, that the selected
        // account has granted any requested permissions to our app and that we were able to
        // establish a service connection to Google Play services.
        //Log.d(TAG, "onConnected:" + bundle);
        //mShouldResolve = false;

        // Show the signed-in UI
        //showSignedInUI();
        if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
            Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
            String personName = currentPerson.getDisplayName();
            String personPhoto = currentPerson.getImage().getUrl();
            String personGooglePlusProfile = currentPerson.getUrl();


            UserLoginModel user = new UserLoginModel();
            user.loginType = UserLoginModel.LoginType.GooglePlus;
            user.ID = currentPerson.getId();
            user.Name = currentPerson.getDisplayName();
            user.URLPictureProfile = currentPerson.getUrl();


            final Context c = this.getContext();

            AsyncTask<UserLoginModel, Void, UserLoginModel> task = new AsyncTask<UserLoginModel, Void, UserLoginModel>() {
                @Override
                protected UserLoginModel doInBackground(UserLoginModel... params) {
                    String token = "";
                    UserLoginModel user = params[0];
                    String accountName = Plus.AccountApi.getAccountName(mGoogleApiClient);
                    Account account = new Account(accountName, GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
                    String scopes = "audience:server:client_id:" + "1036182018589-qq5e49a73sc4p0q9f02isfin56snbcsd.apps.googleusercontent.com"; // Not the app's client ID.
                    try {
                        user.accessToken = GoogleAuthUtil.getToken(c, account, scopes);
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
                protected void onPostExecute(UserLoginModel user) {
                    startLoginProcess(user);
                }

            };
            task.execute(user);

        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Could not connect to Google Play Services.  The user needs to select an account,
        // grant permissions or resolve an error in order to sign in. Refer to the javadoc for
        // ConnectionResult to see possible error codes.
        //Log.d(TAG, "onConnectionFailed:" + connectionResult);

        loginProgressbar.setVisibility(View.GONE);
        showLoginComponent(View.VISIBLE);

    }

    @Override
    public void onConnectionSuspended(int id){
        loginProgressbar.setVisibility(View.GONE);
        showLoginComponent(View.VISIBLE);
    }




    private void startLoginProcess(UserLoginModel user){
        SharedPreferences sharedPreferences = mainActivity.getSharedPreferences("PenYourPrayer.SharePreference", Context.MODE_PRIVATE);
        user.GoogleCloudMessagingDeviceID = sharedPreferences.getString(QuickstartPreferences.DeviceRegistrationToken, "");

        if(user.GoogleCloudMessagingDeviceID.length() == 0) {
            loginProgressbar.setVisibility(View.GONE);
            showLoginComponent(View.VISIBLE);
            //show some error meessage here
        }
        else{
            AsyncWebApi webSync = new AsyncWebApi(mainActivity);
            webSync.RegisterNewUser();
            webSync.onCompleteListener(new AsyncWebApiResponse() {
                @Override
                public void WebAPITaskComplete(Object output) {

                }
            });

        }
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
