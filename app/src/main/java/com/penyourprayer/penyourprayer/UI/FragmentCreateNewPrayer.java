package com.penyourprayer.penyourprayer.UI;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.onegravity.rteditor.RTEditText;
import com.onegravity.rteditor.RTManager;
import com.onegravity.rteditor.api.RTApi;
import com.onegravity.rteditor.api.RTMediaFactoryImpl;
import com.onegravity.rteditor.api.RTProxyImpl;
import com.onegravity.rteditor.api.format.RTFormat;
import com.onegravity.rteditor.api.media.RTImage;
import com.onegravity.rteditor.toolbar.HorizontalRTToolbar;
import com.penyourprayer.penyourprayer.Common.ImageLoad.ImageLoader;
import com.penyourprayer.penyourprayer.Common.Interface.InterfaceFragmentBackHandler;
import com.penyourprayer.penyourprayer.Common.Model.ModelFriendProfile;
import com.penyourprayer.penyourprayer.Common.Model.ModelPrayerAttachement;
import com.penyourprayer.penyourprayer.Database.Database;
import com.penyourprayer.penyourprayer.QuickstartPreferences;
import com.penyourprayer.penyourprayer.R;
import com.penyourprayer.penyourprayer.WebAPI.httpClient;

import java.util.ArrayList;
import java.util.UUID;

import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

public class FragmentCreateNewPrayer extends Fragment implements InterfaceFragmentBackHandler {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private RTManager mRTManager;
    private MainActivity mainActivity;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ImageButton actionbar_done;
    RTEditText mRTMessageField;
    private ImageButton createnew_prayer_tag_friend_ImageButton;
    private boolean publicView = false;
    private RestAdapter adapter;
    private ImageButton imageButton1, imageButton2, imageButton3, imageButton4, imageButton5;
    private ImageLoader imageLoader;
    private LinearLayout attachment_layout;
    private String OwnerLoginType;
    private String OwnerUserName;
    private String HMACKey;

    public FragmentCreateNewPrayer() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        mainActivity = ((MainActivity) getActivity());
        mainActivity.attachment = new  ArrayList<ModelPrayerAttachement>();

        imageLoader = new ImageLoader(mainActivity);

        this.getActivity().setTheme(R.style.ThemeLight);

        OwnerLoginType = mainActivity.sharedPreferences.getString(QuickstartPreferences.OwnerLoginType, "");
        OwnerUserName = mainActivity.sharedPreferences.getString(QuickstartPreferences.OwnerUserName, "");
        HMACKey = mainActivity.sharedPreferences.getString(QuickstartPreferences.OwnerHMACKey, "");

        Gson gson = new GsonBuilder().setDateFormat(QuickstartPreferences.DefaultTimeFormat).create();
        adapter = new RestAdapter.Builder()
                .setConverter(new GsonConverter(gson))
                .setEndpoint(QuickstartPreferences.api_server)
                .setClient(new OkClient(new httpClient(OwnerLoginType, OwnerUserName, HMACKey)))
                .build();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);

        View mCustomView = inflater.inflate(R.layout.actionbar_create_new_prayer, null);
        actionBar.setCustomView(mCustomView);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.show();

        ((ImageButton)mCustomView.findViewById(R.id.createnewprayer_back_ImageButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if(mainActivity.selectedFriends.size() > 0)
            ((ImageButton)mCustomView.findViewById(R.id.createnewprayer_tagfriend_ImageButton)).setImageResource(R.drawable.ic_actionbar_tagfriend_w);

        ((ImageButton)mCustomView.findViewById(R.id.createnewprayer_tagfriend_ImageButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.replaceWithTagAFriend(null);
            }
        });

        ((ImageButton)mCustomView.findViewById(R.id.createnewprayer_public_ImageButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (publicView) {
                    ((ImageButton) v).setImageResource(R.drawable.ic_actionbar_public_p);
                    publicView = false;
                } else {
                    ((ImageButton) v).setImageResource(R.drawable.ic_actionbar_public_w);
                    publicView = true;
                }
            }
        });

        actionbar_done = (ImageButton)mCustomView.findViewById(R.id.createnewprayer_done_ImageButton);
        actionbar_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNewPrayer();
            }
        });


        return inflater.inflate(R.layout.fragment_create_new_prayer, container, false);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainActivity.lockDrawer(true);

        attachment_layout = (LinearLayout)view.findViewById(R.id.create_prayer_attachment_layout);
        imageButton1 = (ImageButton)view.findViewById(R.id.create_prayer_attachment_imageButton1);
        imageButton2 = (ImageButton)view.findViewById(R.id.create_prayer_attachment_imageButton2);
        imageButton3 = (ImageButton)view.findViewById(R.id.create_prayer_attachment_imageButton3);
        imageButton4 = (ImageButton)view.findViewById(R.id.create_prayer_attachment_imageButton4);
        imageButton5 = (ImageButton)view.findViewById(R.id.create_prayer_attachment_imageButton5);

        // initialize rich text manager
        RTMediaFactoryImpl sfd = new RTMediaFactoryImpl(this.getActivity().getApplicationContext());
        RTApi rtApi = new RTApi(this.getActivity(), new RTProxyImpl(this.getActivity()), new RTMediaFactoryImpl(this.getActivity().getApplicationContext(), true));
        mRTManager = new RTManager(rtApi, savedInstanceState);

        ViewGroup toolbarContainer = (ViewGroup) view.findViewById(R.id.rte_toolbar_container);

        // register toolbar 0 (if it exists)
        HorizontalRTToolbar rtToolbar0 = (HorizontalRTToolbar) view.findViewById(R.id.rte_toolbar);
        if (rtToolbar0 != null) {
            mRTManager.registerToolbar(toolbarContainer, rtToolbar0);
        }


        // register message editor
        mRTMessageField = (RTEditText) view.findViewById(R.id.rtEditText_1);
        mRTManager.registerEditor(mRTMessageField, true);
        mRTMessageField.requestFocus();
        mRTMessageField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mRTMessageField.getText().toString().trim().length() > 0) {
                    actionbar_done.setImageResource(R.drawable.ic_actionbar_done_w);
                    actionbar_done.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            saveNewPrayer();

                        }
                    });
                } else {
                    actionbar_done.setImageResource(R.drawable.ic_actionbar_done_p);
                    actionbar_done.setOnClickListener(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        InputMethodManager imm = (InputMethodManager) this.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mRTMessageField, InputMethodManager.SHOW_IMPLICIT);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        mRTManager.onSaveInstanceState(outState);


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mRTManager != null) {
            mRTManager.onDestroy(true);
        }
    }

    private boolean checkIfContentfilled(){
        if(mRTMessageField.getText().toString().trim().length() > 0){
            return true;
        }
        else
            return false;
    }

    public void onBackPressed() {
        if(checkIfContentfilled()){
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            mainActivity.popBackFragmentStack();
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            //No button clicked
                            mainActivity.popBackFragmentStack();
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
            builder.setMessage("Would you like to save and complete it later?").setTitle("To be continue?").setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).setNeutralButton("Cancel", dialogClickListener).show();
        }
        else
            mainActivity.popBackFragmentStack();
    }

    private void saveNewPrayer(){
        String prayer = mRTMessageField.getText(RTFormat.HTML);

        Database db = new Database(mainActivity);
        db.AddNewPrayer(mainActivity, prayer, publicView, mainActivity.selectedFriends, mainActivity.attachment);

        mainActivity.attachment = new ArrayList<ModelPrayerAttachement>();
        mainActivity.selectedFriends = new ArrayList<ModelFriendProfile>();
        mainActivity.popBackFragmentStack();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //mRTManager.onActivityResult(requestCode, resultCode, data);
        if(data != null) {
            RTImage image = (RTImage) data.getSerializableExtra("RTE_RESULT_MEDIA");
            ModelPrayerAttachement o = new ModelPrayerAttachement();
            o.URLPath = image.getFilePath(RTFormat.HTML);
            o.OriginalFilePath = image.getFilePath(RTFormat.HTML);
            o.GUID = UUID.randomUUID().toString().replace("-", "");
            o.FileName = image.getFileName();
            mainActivity.attachment.add(o);

            for(int x=0; x<mainActivity.attachment.size(); x++){
                attachment_layout.setVisibility(View.VISIBLE);
                if(x==0)
                    imageLoader.DisplayImage(mainActivity.attachment.get(x).URLPath, imageButton1, false);
                if(x==1)
                    imageLoader.DisplayImage(mainActivity.attachment.get(x).URLPath, imageButton2, false);
                if(x==2)
                    imageLoader.DisplayImage(mainActivity.attachment.get(x).URLPath, imageButton3, false);
                if(x==3)
                    imageLoader.DisplayImage(mainActivity.attachment.get(x).URLPath, imageButton4, false);
                if(x==4)
                    imageLoader.DisplayImage(mainActivity.attachment.get(x).URLPath, imageButton5, false);
            }
        }
    }
}
