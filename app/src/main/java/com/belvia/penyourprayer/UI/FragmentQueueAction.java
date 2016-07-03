package com.belvia.penyourprayer.UI;


import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.belvia.penyourprayer.Common.Adapter.AdapterListViewComment;
import com.belvia.penyourprayer.Common.Adapter.AdapterListViewQueueAction;
import com.belvia.penyourprayer.Common.Interface.InterfacePrayerCommentListViewUpdated;
import com.belvia.penyourprayer.Common.Model.ModelPrayerComment;
import com.belvia.penyourprayer.Common.Model.ModelQueueAction;
import com.belvia.penyourprayer.Database.Database;
import com.belvia.penyourprayer.R;

import java.util.ArrayList;

public class FragmentQueueAction extends Fragment {
    private MainActivity mainActivity;
    public ArrayList<ModelQueueAction> queueAction;
    private AdapterListViewQueueAction adapterListViewQueueAction;
    public FragmentQueueAction() {
        // Required empty public constructor
    }

    public static FragmentQueueAction newInstance() {
        FragmentQueueAction fragment = new FragmentQueueAction();
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

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);

        View mCustomView = inflater.inflate(R.layout.actionbar_fragment_title, null);
        ((TextView)mCustomView.findViewById(R.id.actionbar_title)).setText("Sync Items");

        ImageView imageView = (ImageView) mCustomView.findViewById(R.id.actionbar_icon);
        imageView.setBackgroundResource(R.drawable.uploadanimation);
        AnimationDrawable frameAnimation = (AnimationDrawable) imageView.getBackground();
        frameAnimation.start();

        actionBar.setCustomView(mCustomView);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.show();

        return inflater.inflate(R.layout.fragment_queue_action_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Database db = new Database(mainActivity);
        queueAction = db.getAllQueueItems();

        ListView queueaction_listView = (ListView) view.findViewById(R.id.queue_action_listView);
        adapterListViewQueueAction = new AdapterListViewQueueAction(this.getActivity(), R.layout.list_view_row_queue_action, queueAction);
        if (queueaction_listView != null) {
            queueaction_listView.setAdapter(adapterListViewQueueAction);
        }
    }

    public void onQueueActionUpdate(){
        Runnable run = new Runnable(){
            public void run(){
                Database db = new Database(mainActivity);
                queueAction = db.getAllQueueItems();
                adapterListViewQueueAction.updateQueueActionList(queueAction);
            }
        };
        mainActivity.runOnUiThread(run);
    }
}
