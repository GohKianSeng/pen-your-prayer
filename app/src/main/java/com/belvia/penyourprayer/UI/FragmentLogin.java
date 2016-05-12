package com.belvia.penyourprayer.UI;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.belvia.penyourprayer.Common.Model.ModelUserLogin;
import com.belvia.penyourprayer.Common.SocialLogin.Facebook;
import com.belvia.penyourprayer.Common.SocialLogin.GooglePlus;
import com.belvia.penyourprayer.Common.Utils;
import com.belvia.penyourprayer.QuickstartPreferences;
import com.belvia.penyourprayer.R;
import com.belvia.penyourprayer.WebAPI.Model.SimpleJsonResponse;
import com.belvia.penyourprayer.WebAPI.UserAccountInterface;
import com.belvia.penyourprayer.WebAPI.httpClient;

import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;

/**
 * A placeholder fragment containing a simple view.
 */
public class FragmentLogin extends Fragment  {

    private RestAdapter adapter;
    private MainActivity mainActivity;
    private Facebook fb;
    private GooglePlus gp;
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
        mainActivity.logout();

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
            }
        });

        ((ImageButton) view.findViewById(R.id.socal_login_googleplus_imageButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoginComponent(View.GONE);
                loginProgressbar.setVisibility(View.VISIBLE);
                gp = new GooglePlus(mainActivity);
                gp.loginGooglePlus();
            }
        });

        emailLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (email.getText().toString().trim().length() > 0 && password.getText().toString().length() > 0) {

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

        if(gp != null)
            gp.AfterActivityResult_Connect();
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
                        mainActivity.qa.StartHttpTranmissionQueue();

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