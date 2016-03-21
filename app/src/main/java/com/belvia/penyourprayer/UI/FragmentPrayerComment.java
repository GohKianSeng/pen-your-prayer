package com.belvia.penyourprayer.UI;


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

import com.belvia.penyourprayer.Common.Adapter.AdapterListViewComment;
import com.belvia.penyourprayer.Common.Interface.InterfacePrayerCommentListViewUpdated;
import com.belvia.penyourprayer.Common.Model.ModelPrayerComment;
import com.belvia.penyourprayer.Common.Utils;
import com.belvia.penyourprayer.Database.Database;
import com.belvia.penyourprayer.QuickstartPreferences;
import com.belvia.penyourprayer.R;
import com.belvia.penyourprayer.WebAPI.httpClient;

import java.util.ArrayList;

import retrofit.RestAdapter;
import retrofit.client.OkClient;

public class FragmentPrayerComment extends Fragment implements InterfacePrayerCommentListViewUpdated {
    private MainActivity mainActivity;
    private RestAdapter adapter;
    public ArrayList<ModelPrayerComment> comment;
    public String PrayerID;
    private ImageButton donebutton;
    private ListView comment_listView;
    private EditText comment_editText;
    private AdapterListViewComment adapterListViewComment;
    public FragmentPrayerComment() {
        // Required empty public constructor
    }

    public static FragmentPrayerComment newInstance(ArrayList<ModelPrayerComment> comment, String PrayerID) {
        FragmentPrayerComment fragment = new FragmentPrayerComment();
        fragment.comment = comment;
        fragment.PrayerID = PrayerID;
        return fragment;
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
        return inflater.inflate(R.layout.fragment_prayer_comment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        comment_editText = (EditText) view.findViewById(R.id.comment_editText);
        comment_editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(comment_editText.getText().toString().length() > 0)
                    donebutton.setVisibility(View.VISIBLE);
                else
                    donebutton.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        donebutton = (ImageButton) view.findViewById(R.id.comment_done_imageButton);
        donebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Database db = new Database(mainActivity);
                db.addOwnerPrayerComment(PrayerID, comment_editText.getText().toString(), mainActivity.OwnerID, mainActivity.OwnerDisplayName, mainActivity.OwnerProfilePictureURL);
                ModelPrayerComment newcomment = db.getAllOwnerPrayerComment(PrayerID).get(0);
                adapterListViewComment.addComment(newcomment);
                comment_editText.setText("");
                donebutton.setVisibility(View.GONE);
            }
        });

        comment_listView = (ListView) view.findViewById(R.id.comment_listView);
        adapterListViewComment = new AdapterListViewComment(this.getActivity(), R.layout.list_view_row_prayer_comment, comment);
        if (comment_listView != null) {
            comment_listView.setAdapter(adapterListViewComment);
        }

        comment_listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                LayoutInflater inflater = ((Activity)mainActivity).getLayoutInflater();

                PopupMenu popupMenu = new PopupMenu(mainActivity, view);
                popupMenu.inflate(R.menu.prayer_comment_menu);

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        ModelPrayerComment comment = (ModelPrayerComment) adapterListViewComment.getItem(position);

                        if (item.toString().compareToIgnoreCase("Edit") == 0) {
                            mainActivity.replaceWithPrayerCommentModification(comment);
                        } else if (item.toString().compareToIgnoreCase("Delete") == 0) {
                            Database db = new Database(mainActivity);
                            db.DeletePrayerComment(comment.CommentID);
                            adapterListViewComment.remove(comment);
                            adapterListViewComment.notifyDataSetChanged();
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
    public void onCommentUpdate(final ArrayList<ModelPrayerComment> comment){
        Runnable run = new Runnable(){
            public void run(){
                adapterListViewComment.updateCommentList(comment);
            }
        };
        mainActivity.runOnUiThread(run);
    }
}
