package com.penyourprayer.penyourprayer.UI;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.penyourprayer.penyourprayer.Common.Interface.InterfacePrayerAnsweredEditUpdated;
import com.penyourprayer.penyourprayer.Common.Model.ModelPayerAnswered;
import com.penyourprayer.penyourprayer.Database.Database;
import com.penyourprayer.penyourprayer.R;

public class FragmentPrayerAnsweredEdit extends Fragment implements InterfacePrayerAnsweredEditUpdated {
    private MainActivity mainActivity;
    public ModelPayerAnswered answer;
    public EditText answered_editText;
    private ImageButton donebutton;
    private boolean dirty = false;
    public FragmentPrayerAnsweredEdit() {
        // Required empty public constructor
    }

    public static FragmentPrayerAnsweredEdit newInstance(ModelPayerAnswered a) {
        FragmentPrayerAnsweredEdit fragment = new FragmentPrayerAnsweredEdit();
        fragment.answer = a;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivity = (MainActivity)getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);

        View mCustomView = inflater.inflate(R.layout.actionbar_tag_a_friend, null);
        actionBar.setCustomView(mCustomView);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.show();

        ((ImageButton)mCustomView.findViewById(R.id.tagafriend_back_ImageButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.popBackFragmentStack();
            }
        });

        donebutton = (ImageButton) mCustomView.findViewById(R.id.tagafriend_done_ImageButton);
        donebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dirty) {
                    answer.Answered = answered_editText.getText().toString();
                    Database db = new Database(mainActivity);
                    db.updateOwnerPrayerAnswered(answer);
                }
                mainActivity.popBackFragmentStack();
            }
        });

        return inflater.inflate(R.layout.fragment_prayer_answered_edit, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        answered_editText = (EditText) view.findViewById(R.id.answered_modify_editText);
        answered_editText.setText(answer.Answered);
        answered_editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(answered_editText.getText().toString().length() > 0) {
                    donebutton.setImageResource(R.drawable.ic_actionbar_done_w);
                    donebutton.setEnabled(true);
                }
                else {
                    donebutton.setImageResource(R.drawable.ic_actionbar_done_p);
                    donebutton.setEnabled(false);
                }

                if(answered_editText.getText().toString().compareToIgnoreCase(answer.Answered) == 0)
                    dirty = false;
                else
                    dirty = true;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @Override
    public void onCommentUpdate(ModelPayerAnswered a){
        this.answer = a;
    }
}
