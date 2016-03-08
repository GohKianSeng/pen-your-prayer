package com.penyourprayer.penyourprayer.UI;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PopupMenu;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.penyourprayer.penyourprayer.Common.Adapter.AdapterListViewAnswered;
import com.penyourprayer.penyourprayer.Common.Interface.InterfacePrayerAnsweredListViewUpdated;
import com.penyourprayer.penyourprayer.Common.Model.ModelPrayerAnswered;
import com.penyourprayer.penyourprayer.Database.Database;
import com.penyourprayer.penyourprayer.R;

import java.util.ArrayList;

public class FragmentPrayerAnswered extends Fragment implements InterfacePrayerAnsweredListViewUpdated {
    private MainActivity mainActivity;
    public ArrayList<ModelPrayerAnswered> answered;
    public String PrayerID;
    private ImageButton donebutton;
    private ListView answered_listView;
    private EditText answered_editText;
    private AdapterListViewAnswered adapterListViewAnswered;
    public FragmentPrayerAnswered() {
        // Required empty public constructor
    }

    public static FragmentPrayerAnswered newInstance(ArrayList<ModelPrayerAnswered> answer, String PrayerID) {
        FragmentPrayerAnswered fragment = new FragmentPrayerAnswered();
        fragment.answered = answer;
        fragment.PrayerID = PrayerID;
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_prayer_answered, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        answered_editText = (EditText) view.findViewById(R.id.answered_editText);
        answered_editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(answered_editText.getText().toString().length() > 0)
                    donebutton.setVisibility(View.VISIBLE);
                else
                    donebutton.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        donebutton = (ImageButton) view.findViewById(R.id.answered_done_imageButton);
        donebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Database db = new Database(mainActivity);
                db.addOwnerPrayerAnswered(PrayerID, answered_editText.getText().toString(), mainActivity.OwnerID, mainActivity.OwnerDisplayName, mainActivity.OwnerProfilePictureURL);
                ModelPrayerAnswered newAnswer = db.getAllOwnerPrayerAnswered(PrayerID).get(0);
                adapterListViewAnswered.addComment(newAnswer);
                answered_editText.setText("");
                donebutton.setVisibility(View.GONE);
            }
        });

        answered_listView = (ListView) view.findViewById(R.id.answered_listView);
        adapterListViewAnswered = new AdapterListViewAnswered(this.getActivity(), R.layout.list_view_row_prayer_comment, answered);
        if (answered_listView != null) {
            answered_listView.setAdapter(adapterListViewAnswered);
        }

        answered_listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                LayoutInflater inflater = ((Activity)mainActivity).getLayoutInflater();

                PopupMenu popupMenu = new PopupMenu(mainActivity, view);
                popupMenu.inflate(R.menu.prayer_comment_menu);

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        ModelPrayerAnswered answer = (ModelPrayerAnswered) adapterListViewAnswered.getItem(position);

                        if (item.toString().compareToIgnoreCase("Edit") == 0) {
                            mainActivity.replaceWithPrayerAnsweredModification(answer);
                        } else if (item.toString().compareToIgnoreCase("Delete") == 0) {
                            Database db = new Database(mainActivity);
                            db.DeletePrayerAnswered(answer.AnsweredID);
                            adapterListViewAnswered.remove(answer);
                            adapterListViewAnswered.notifyDataSetChanged();
                        }

                        return true;
                    }
                });

                popupMenu.show();


                return false;
            }
        });

    }

    @Override
    public void onCommentUpdate(final ArrayList<ModelPrayerAnswered> comment){
        Runnable run = new Runnable(){
            public void run(){
                adapterListViewAnswered.updateCommentList(comment);
            }
        };
        mainActivity.runOnUiThread(run);
    }
}
