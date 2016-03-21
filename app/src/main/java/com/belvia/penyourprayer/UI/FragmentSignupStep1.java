package com.belvia.penyourprayer.UI;


import android.content.Context;
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

import com.belvia.penyourprayer.R;

public class FragmentSignupStep1 extends Fragment {
    private MainActivity mainActivity;
    private EditText fullname_edittext;
    private Button next_button;

    public FragmentSignupStep1() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fragment_signup_step1, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainActivity = (MainActivity)getActivity();
        fullname_edittext = (EditText)view.findViewById(R.id.full_name_editText);
        fullname_edittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (fullname_edittext.getText().toString().length() > 0) {
                    next_button.setVisibility(View.VISIBLE);
                } else {
                    next_button.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        next_button = (Button) view.findViewById(R.id.signup_step1_next_button);
        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.replaceWithSignUpStep2Fragment(fullname_edittext.getText().toString().trim());
            }
        });

        fullname_edittext.requestFocus();
        InputMethodManager imm = (InputMethodManager) this.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(fullname_edittext, InputMethodManager.SHOW_IMPLICIT);
    }
}
