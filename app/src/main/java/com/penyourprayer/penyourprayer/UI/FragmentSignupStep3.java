package com.penyourprayer.penyourprayer.UI;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.larswerkman.holocolorpicker.Util;
import com.penyourprayer.penyourprayer.Common.Utils;
import com.penyourprayer.penyourprayer.QuickstartPreferences;
import com.penyourprayer.penyourprayer.R;
import com.penyourprayer.penyourprayer.WebAPI.LocaleInterface;
import com.penyourprayer.penyourprayer.WebAPI.Model.IPAddressLocale;
import com.penyourprayer.penyourprayer.WebAPI.Model.SimpleJsonResponse;
import com.penyourprayer.penyourprayer.WebAPI.UserAccountInterface;
import com.penyourprayer.penyourprayer.WebAPI.httpClient;

import org.w3c.dom.Text;

import java.util.Date;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;

public class FragmentSignupStep3 extends Fragment {
    private MainActivity mainActivity;
    private EditText password_edittext, confirm_password_edit;
    private Button next_button;
    private TextWatcher tw;
    private static final String FULLNAME = "FULLNAME";
    private static final String EMAIL = "EMAIL";
    private String fullname = "";
    private String email = "";
    private RestAdapter adapter, localeAdapter;
    private ProgressDialog progress;
    private TextView passwordNotMatch_Textview;

    public static FragmentSignupStep3 newInstance(String fn, String em) {
        FragmentSignupStep3 fragment = new FragmentSignupStep3();
        Bundle args = new Bundle();
        args.putString(FULLNAME, fn);
        args.putString(EMAIL, em);
        fragment.setArguments(args);
        return fragment;
    }

    public FragmentSignupStep3() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            fullname = getArguments().getString(FULLNAME);
            email = getArguments().getString(EMAIL);
        }

        mainActivity = (MainActivity)getActivity();

        adapter = new RestAdapter.Builder()
                .setEndpoint(QuickstartPreferences.api_server)
                .setClient(new OkClient(new httpClient("ANONYMOUS", Utils.TempUserID(mainActivity),QuickstartPreferences.AnonymousHMACKey)))
                .build();
        localeAdapter = new RestAdapter.Builder()
                .setEndpoint(QuickstartPreferences.locale_server)
                .build();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fragment_signup_step3, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tw = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(passwordValidationSuccessful()) {
                    next_button.setVisibility(View.VISIBLE);
                    passwordNotMatch_Textview.setVisibility(View.GONE);
                }
                else {
                    next_button.setVisibility(View.GONE);
                    passwordNotMatch_Textview.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        passwordNotMatch_Textview = (TextView) view.findViewById(R.id.password_not_match_textview);
        password_edittext = (EditText)view.findViewById(R.id.password_editText);
        confirm_password_edit = (EditText)view.findViewById(R.id.confirm_password_editText);
        password_edittext.addTextChangedListener(tw);
        confirm_password_edit.addTextChangedListener(tw);


        next_button = (Button) view.findViewById(R.id.signup_step3_finish_button);
        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress = ProgressDialog.show(mainActivity, "Registering",
                        "Shouldn't be too long...", true);
                GetLocaleAndStartRegistering();
            }
        });

        password_edittext.requestFocus();
        InputMethodManager imm = (InputMethodManager) this.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(password_edittext, InputMethodManager.SHOW_IMPLICIT);
    }

    private void GetLocaleAndStartRegistering(){
        IPAddressLocale s = new IPAddressLocale();
        s.country = "Singapore";
        RegisterUser(new IPAddressLocale());
        if(true)
            return;

        //need to find new ipgeo location service.
        LocaleInterface LI = localeAdapter.create(LocaleInterface.class);
        LI.getLocale(new Callback<IPAddressLocale>() {
            @Override
            public void success(IPAddressLocale ipAddressLocale, Response response) {
                RegisterUser(ipAddressLocale);
            }

            @Override
            public void failure(RetrofitError error) {
                //need to think what to do if fail.
            }
        });
    }

    private void RegisterUser(IPAddressLocale userLocale){
        UserAccountInterface userInterface = adapter.create(UserAccountInterface.class);
        userInterface.registerNewUser("Email", email,
                fullname, "",
                password_edittext.getText().toString(), "Android",
                mainActivity.sharedPreferences.getString(QuickstartPreferences.DeviceRegistrationToken, ""),
                userLocale.country, userLocale.region, userLocale.city,
                "", new Callback<SimpleJsonResponse>() {
                    @Override
                    public void success(SimpleJsonResponse model, Response response) {
                        progress.dismiss();
                        if (model.StatusCode == 200) {
                            registrationSuccessful();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        progress.dismiss();
                        if (((SimpleJsonResponse) error.getBody()).Description.compareToIgnoreCase("EXISTS") == 0) {
                            new AlertDialog.Builder(mainActivity)
                                    .setTitle("Registration Fail")
                                    .setMessage("Oops! The email address is already registered. Do you want to reset the password?")
                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            mainActivity.popBackFragmentStack();
                                        }
                                    })
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            //mainActivity.clearAllFragmentStackToLoginFragment();
                                        }
                                    })
                                    .show();
                        }
                    }
                });
    }

    private void registrationSuccessful(){
        mainActivity.replaceWithSignUpStep4Fragment(fullname, email);
    }

    private boolean passwordValidationSuccessful(){
        if(password_edittext.getText().toString().length() > 0 && confirm_password_edit.getText().toString().length() > 0
           && password_edittext.getText().toString().compareTo(confirm_password_edit.getText().toString()) == 0){
            return true;
        }
        return false;
    }

    private static boolean isEmailValid(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }
}
