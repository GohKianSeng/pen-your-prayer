package com.penyourprayer.penyourprayer.UI;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.penyourprayer.penyourprayer.Common.Utils;
import com.penyourprayer.penyourprayer.QuickstartPreferences;
import com.penyourprayer.penyourprayer.R;
import com.penyourprayer.penyourprayer.WebAPI.Model.IPAddressLocale;
import com.penyourprayer.penyourprayer.WebAPI.Model.SimpleJsonResponse;
import com.penyourprayer.penyourprayer.WebAPI.UserAccountInterface;
import com.penyourprayer.penyourprayer.WebAPI.httpClient;
import com.twitter.sdk.android.core.models.User;

import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;

public class FragmentSignupStep2 extends Fragment {
    private MainActivity mainActivity;
    private EditText email_edittext;
    private Button next_button;
    private static final String FULLNAME = "FULLNAME";
    private String fullname = "";
    private TextView emailTakenTextView;
    private LinearLayout progressbarLayout;
    public static FragmentSignupStep2 newInstance(String fn) {
        FragmentSignupStep2 fragment = new FragmentSignupStep2();
        Bundle args = new Bundle();
        args.putString(FULLNAME, fn);
        fragment.setArguments(args);
        return fragment;
    }

    public FragmentSignupStep2() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            fullname = getArguments().getString(FULLNAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fragment_signup_step2, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainActivity = (MainActivity)getActivity();

        progressbarLayout  =(LinearLayout) view.findViewById(R.id.signup_step2_progressbar_layout);
        next_button = (Button) view.findViewById(R.id.signup_step2_next_button);
        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.replaceWithSignUpStep3Fragment(fullname, email_edittext.getText().toString().trim());
            }
        });

        emailTakenTextView = (TextView) view.findViewById(R.id.email_already_registered_textview);
        email_edittext = (EditText)view.findViewById(R.id.email_editText);
        email_edittext.addTextChangedListener(new TextWatcher() {
            private Timer timer = new Timer();
            private final long DELAY = 1000; // milliseconds

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                timer.cancel();
                timer = new Timer();

                next_button.setVisibility(View.GONE);
                emailTakenTextView.setVisibility(View.GONE);

                if(!Utils.isEmailValid(email_edittext.getText().toString().trim()))
                    return;
                timer.schedule(
                        new TimerTask() {
                            @Override
                            public void run() {
                                mHandler.obtainMessage(1).sendToTarget();
                                RestAdapter adapter = new RestAdapter.Builder()
                                        .setEndpoint(QuickstartPreferences.api_server)
                                        .setClient(new OkClient(new httpClient("ANONYMOUS", Utils.TempUserID(mainActivity), QuickstartPreferences.AnonymousHMACKey)))
                                        .build();
                                UserAccountInterface useracctInt = adapter.create(UserAccountInterface.class);
                                useracctInt.CheckUserNameExists("Email", email_edittext.getText().toString().trim(), "", new Callback<SimpleJsonResponse>() {
                                    @Override
                                    public void success(SimpleJsonResponse model, Response response) {
                                        if(model.Description.compareToIgnoreCase("NOT EXIST")==0){
                                            next_button.setVisibility(View.VISIBLE);
                                            emailTakenTextView.setVisibility(View.GONE);
                                            progressbarLayout.setVisibility(View.GONE);
                                        }
                                        else{
                                            next_button.setVisibility(View.GONE);
                                            emailTakenTextView.setVisibility(View.VISIBLE);
                                            progressbarLayout.setVisibility(View.GONE);
                                        }
                                    }

                                    @Override
                                    public void failure(RetrofitError error) {
                                        next_button.setVisibility(View.GONE);
                                        progressbarLayout.setVisibility(View.GONE);
                                        emailTakenTextView.setVisibility(View.GONE);
                                    }
                                });
                            }
                        },
                        DELAY
                );
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        email_edittext.requestFocus();
        InputMethodManager imm = (InputMethodManager) this.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(email_edittext, InputMethodManager.SHOW_IMPLICIT);
    }

    public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            progressbarLayout.setVisibility(View.VISIBLE);
        }
    };
}
