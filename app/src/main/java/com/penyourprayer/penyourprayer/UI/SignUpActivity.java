package com.penyourprayer.penyourprayer.UI;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.penyourprayer.penyourprayer.QuickstartPreferences;
import com.penyourprayer.penyourprayer.R;
import com.penyourprayer.penyourprayer.WebAPI.AsyncWebApi;
import com.penyourprayer.penyourprayer.WebAPI.AsyncWebApiResponse;
import com.penyourprayer.penyourprayer.GoogleCloudMessaging.RegistrationIntentService;

public class SignUpActivity extends AppCompatActivity {

    private AsyncWebApi webSync;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "SignUpActivity";

    Button buttonRegister;
    EditText editTextName, editTextEmail, editTextPassword1, editTextPassword2;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        sharedPreferences = this.getSharedPreferences("PenYourPrayer.SharePreference", Context.MODE_PRIVATE); //PreferenceManager.getDefaultSharedPreferences(context);
        webSync =new AsyncWebApi(this);

        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextPassword1 = (EditText) findViewById(R.id.editTextPassword1);
        editTextPassword2 = (EditText) findViewById(R.id.editTextPassword2);

        buttonRegister = (Button) findViewById(R.id.buttonRegister);

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                webSync = new AsyncWebApi(view.getContext());
                webSync.onCompleteListener(null);
                //webSync.RegisterNewUser(editTextName.getText().toString(), editTextEmail.getText().toString(), editTextPassword1.getText().toString(), sharedPreferences.getString(QuickstartPreferences.DeviceRegistrationToken, ""));
            }
        });

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String Topic = sharedPreferences.getString(QuickstartPreferences.BroadcastMessageTopic, "");

                if(Topic.compareToIgnoreCase(QuickstartPreferences.BroadcastMessageTopic_ReceiveTokenFromGCM) == 0) {
                    String status = sharedPreferences.getString(QuickstartPreferences.AppStatus, QuickstartPreferences.AppStatusNew);
                    if(status.compareToIgnoreCase(QuickstartPreferences.AppStatusSignedIn) == 0){
                        webSync =new AsyncWebApi(context);
                        webSync.onCompleteListener(null);
                        webSync.GCMTokenRefresh(sharedPreferences.getString(QuickstartPreferences.Email, ""), sharedPreferences.getString(QuickstartPreferences.DeviceRegistrationToken, ""));
                    }

                    // Received Device Registration Token from GCM
                    // UI Update.
                }
                else if(Topic.compareToIgnoreCase(QuickstartPreferences.BroadcastMessageTopic_ReceiveVerificationFromServer) == 0) {
                    String VerificationGUID = sharedPreferences.getString(QuickstartPreferences.VerificationGUID, "");
                    webSync =new AsyncWebApi(context);
                    webSync.onCompleteListener(new AsyncWebApiResponse() {
                        @Override
                        public void WebAPITaskComplete(Object output) {
                            String status = sharedPreferences.getString(QuickstartPreferences.AppStatus, QuickstartPreferences.AppStatusNew);
                            if(status.compareToIgnoreCase(QuickstartPreferences.AppStatusNew) == 0){
                                sharedPreferences.edit().putString(QuickstartPreferences.Email, editTextEmail.getText().toString()).apply();
                                sharedPreferences.edit().putString(QuickstartPreferences.Name, editTextName.getText().toString()).apply();
                                sharedPreferences.edit().putString(QuickstartPreferences.Password, editTextPassword1.getText().toString()).apply();
                                sharedPreferences.edit().putString(QuickstartPreferences.AppStatus, QuickstartPreferences.AppStatusSignedIn).apply();


                                //sign up successful.
                            }
                        }
                    });
                    webSync.VerifyUserDevice(editTextEmail.getText().toString(), sharedPreferences.getString(QuickstartPreferences.DeviceRegistrationToken, ""), VerificationGUID);

                }
            }
        };

        String status = sharedPreferences.getString(QuickstartPreferences.AppStatus, QuickstartPreferences.AppStatusNew);
        if (checkPlayServices() && status.compareToIgnoreCase(QuickstartPreferences.AppStatusNew) == 0) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    private void openAlert(View view) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SignUpActivity.this);

        alertDialogBuilder.setTitle(this.getTitle()+ " decision");
        alertDialogBuilder.setMessage("Are you sure? ");
        // set positive button: Yes message
        alertDialogBuilder.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int id) {

            }
        });
        // set negative button: No message
        alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int id) {

            }
        });
        // set neutral button: Exit the app message
        alertDialogBuilder.setNeutralButton("Exit the app",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int id) {

            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        // show alert
        alertDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.BroadcastMessage));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }
}
