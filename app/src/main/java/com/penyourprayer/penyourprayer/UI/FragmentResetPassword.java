package com.penyourprayer.penyourprayer.UI;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.penyourprayer.penyourprayer.Common.Utils;
import com.penyourprayer.penyourprayer.QuickstartPreferences;
import com.penyourprayer.penyourprayer.R;
import com.penyourprayer.penyourprayer.WebAPI.Model.SimpleJsonResponse;
import com.penyourprayer.penyourprayer.WebAPI.UserAccountInterface;
import com.penyourprayer.penyourprayer.WebAPI.httpClient;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;

public class FragmentResetPassword extends Fragment {
    private MainActivity mainActivity;
    private EditText email;
    private RestAdapter adapter;
    private ProgressDialog progress;


    public FragmentResetPassword() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivity = (MainActivity)getActivity();

        adapter = new RestAdapter.Builder()
                .setEndpoint(QuickstartPreferences.api_server)
                .setClient(new OkClient(new httpClient("ANONYMOUS", Utils.TempUserID(mainActivity),QuickstartPreferences.AnonymousHMACKey)))
                .build();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_resetpassword, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        email = ((EditText)view.findViewById(R.id.resetpassword_email_editText));

        view.findViewById(R.id.resetpassword_ok_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progress = ProgressDialog.show(mainActivity, "Reset Password", "Shouldn't be too long...", true);

                UserAccountInterface userInterface = adapter.create(UserAccountInterface.class);
                userInterface.ResetPassword("Email", email.getText().toString().trim(), "", new Callback<SimpleJsonResponse>() {
                    @Override
                    public void success(SimpleJsonResponse model, Response response) {
                        progress.dismiss();
                        new AlertDialog.Builder(mainActivity)
                                .setTitle("Reset Passwrd Email Resent!")
                                .setMessage("We have sent your an email to reset your password. Do check if it's in the spam folder.")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        mainActivity.clearAllFragmentStackToLoginFragment();

                                    }
                                })
                                .show();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        progress.dismiss();
                        new AlertDialog.Builder(mainActivity)
                                .setTitle("Reset Password Fail")
                                .setMessage("We believe the email address entered does not exists in our records.")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })
                                .show();
                    }
                });

            }
        });
    }


}
