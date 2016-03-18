package com.penyourprayer.penyourprayer.UI;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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

public class FragmentSignupStep4 extends Fragment {
    private MainActivity mainActivity;
    private static final String FULLNAME = "FULLNAME";
    private static final String EMAIL = "EMAIL";
    private String fullname = "";
    private String email = "";
    private RestAdapter adapter;
    private ProgressDialog progress;
    public static FragmentSignupStep4 newInstance(String fn, String em) {
        FragmentSignupStep4 fragment = new FragmentSignupStep4();
        Bundle args = new Bundle();
        args.putString(FULLNAME, fn);
        args.putString(EMAIL, em);
        fragment.setArguments(args);
        return fragment;
    }

    public FragmentSignupStep4() {
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fragment_signup_step4, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.signup_step4_ok_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.clearAllFragmentStackToLoginFragment();
            }
        });

        ((TextView)view.findViewById(R.id.signup_email_textView)).setText(email);

        view.findViewById(R.id.signup_step4_resend_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progress = ProgressDialog.show(mainActivity, "Resending", "Shouldn't be too long...", true);

                UserAccountInterface userInterface = adapter.create(UserAccountInterface.class);
                userInterface.ResendAccountActivation("Email", email, "", new Callback<SimpleJsonResponse>() {
                    @Override
                    public void success(SimpleJsonResponse model, Response response) {
                        progress.dismiss();
                        new AlertDialog.Builder(mainActivity)
                                .setTitle("Activation Email Resent!")
                                .setMessage("We have resent it out! Do check if it's in the spam folder.")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        //mainActivity.clearAllFragmentStackToLoginFragment();

                                    }
                                })
                                .show();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        progress.dismiss();
                        new AlertDialog.Builder(mainActivity)
                                .setTitle("Activation Email Resent!")
                                .setMessage("We believe your account might had been activated. Please try to login.")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        mainActivity.clearAllFragmentStackToLoginFragment();

                                    }
                                })
                                .show();
                    }
                });

            }
        });

        InputMethodManager imm = (InputMethodManager)this.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


}
