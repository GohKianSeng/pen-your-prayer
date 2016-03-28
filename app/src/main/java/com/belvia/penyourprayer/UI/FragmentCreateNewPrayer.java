package com.belvia.penyourprayer.UI;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.belvia.penyourprayer.Common.ImageLoad.ImageLoader;
import com.belvia.penyourprayer.Common.Interface.InterfaceFragmentBackHandler;
import com.belvia.penyourprayer.Common.Interface.InterfaceFragmentFriendsHandler;
import com.belvia.penyourprayer.Common.Interface.InterfaceFragmentPrayerRequestHandler;
import com.belvia.penyourprayer.Common.Model.ModelFriendProfile;
import com.belvia.penyourprayer.Common.Model.ModelPrayerAttachement;
import com.belvia.penyourprayer.Common.Model.ModelPrayerRequest;
import com.belvia.penyourprayer.Common.Utils;
import com.belvia.penyourprayer.Database.Database;
import com.belvia.penyourprayer.QuickstartPreferences;
import com.belvia.penyourprayer.R;
import com.belvia.penyourprayer.WebAPI.httpClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.onegravity.rteditor.RTEditText;
import com.onegravity.rteditor.RTManager;
import com.onegravity.rteditor.api.RTApi;
import com.onegravity.rteditor.api.RTMediaFactoryImpl;
import com.onegravity.rteditor.api.RTProxyImpl;
import com.onegravity.rteditor.api.format.RTFormat;
import com.onegravity.rteditor.toolbar.HorizontalRTToolbar;
import com.onegravity.rteditor.toolbar.ToolbarStatus;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

public class FragmentCreateNewPrayer extends Fragment implements InterfaceFragmentBackHandler, InterfaceFragmentPrayerRequestHandler, InterfaceFragmentFriendsHandler {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private int PICK_IMAGE_REQUEST = 1122;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private RTManager mRTManager;
    private MainActivity mainActivity;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ImageButton actionbar_done;
    RTEditText mRTMessageField;
    private boolean publicView = false;
    private RestAdapter adapter;
    private ImageButton imageButton1, imageButton2, imageButton3, imageButton4, imageButton5;
    private ImageButton actionbar_attachment_imageButton;
    private ImageLoader imageLoader;
    private LinearLayout attachment_layout;
    private String OwnerLoginType;
    private String OwnerUserName;
    private String HMACKey;
    private ImageButton tagFriend;
    int witdthHeight = 1;
    private HorizontalRTToolbar rtToolbar0;

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
        witdthHeight = Utils.dpToPx(mainActivity, QuickstartPreferences.thumbnailDPsize);
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
                mainActivity.selectedFriends = new ArrayList<ModelFriendProfile>();
                onBackPressed();
            }
        });

        if(mainActivity.selectedFriends.size() > 0)
            ((ImageButton)mCustomView.findViewById(R.id.createnewprayer_tagfriend_ImageButton)).setImageResource(R.drawable.ic_actionbar_tagfriend_w);

        tagFriend = (ImageButton)mCustomView.findViewById(R.id.createnewprayer_tagfriend_ImageButton);
        tagFriend.setOnClickListener(new View.OnClickListener() {
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

        actionbar_attachment_imageButton = (ImageButton)mCustomView.findViewById(R.id.createnewprayer_attachment_ImageButton);
        actionbar_attachment_imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
// Always show the chooser (if there are multiple options available)
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

            }
        });

        actionbar_done = (ImageButton)mCustomView.findViewById(R.id.createnewprayer_done_ImageButton);
        actionbar_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNewPrayer();
            }
        });
        actionbar_done.setEnabled(false);

        return inflater.inflate(R.layout.fragment_create_new_prayer, container, false);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //mainActivity.lockDrawer(false);

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
        rtToolbar0 = (HorizontalRTToolbar) view.findViewById(R.id.rte_toolbar);
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
                    actionbar_done.setEnabled(true);
                } else {
                    actionbar_done.setImageResource(R.drawable.ic_actionbar_done_p);
                    actionbar_done.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        updateAttachmentView();

        String draft = mainActivity.sharedPreferences.getString(QuickstartPreferences.DraftNewPrayer, "");
        if(draft.length() > 0) {
            mRTMessageField.setRichTextEditing(true, draft);
            mRTMessageField.setSelection(mRTMessageField.getText(RTFormat.SPANNED).length());
        }
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

    @Override
    public void onPrayerRequestPressed(ModelPrayerRequest pr){
        ToolbarStatus toolbar = rtToolbar0.getCurrentToolBarStats();
        mainActivity.showNavigationDrawer(Gravity.RIGHT, false);
        String hashTag = "<b><u>" + pr.Subject + "</u></b><br/>";
        String content = mRTMessageField.getText(RTFormat.HTML);
        mRTMessageField.requestFocus();
        if(content.length() <= 0)
            mRTMessageField.setRichTextEditing(true, hashTag);
        else
            mRTMessageField.setRichTextEditing(true, content + "<br/>" + hashTag);

        mRTMessageField.setSelection(mRTMessageField.getText(RTFormat.SPANNED).length());
        rtToolbar0.setToolbarStatus(toolbar);

    }

    @Override
    public void onFriendPressed(ModelFriendProfile friend){
        Toast toast = Toast.makeText(mainActivity, friend.DisplayName + ", tag to prayer", Toast.LENGTH_SHORT);
        toast.show();

        for(int x=0; x<mainActivity.selectedFriends.size(); x++){
            if(mainActivity.selectedFriends.get(x).UserID.compareToIgnoreCase(friend.UserID) == 0){

                return;
            }
        }
        mainActivity.showNavigationDrawer(Gravity.LEFT, false);
        mainActivity.selectedFriends.add(friend);

        if(mainActivity.selectedFriends.size() > 0)
            tagFriend.setImageResource(R.drawable.ic_actionbar_tagfriend_w);

    }

    public void onBackPressed() {
        // auto save the content
        if(mRTMessageField.getText().toString().trim().length() > 0)
            mainActivity.sharedPreferences.edit().putString(QuickstartPreferences.DraftNewPrayer, mRTMessageField.getText().toString().trim()).apply();

        mainActivity.popBackFragmentStack();
    }

    private void saveNewPrayer(){
        String prayer = mRTMessageField.getText(RTFormat.HTML);

        Database db = new Database(mainActivity);
        db.AddNewPrayer(prayer, publicView, mainActivity.selectedFriends, mainActivity.attachment);

        mainActivity.sharedPreferences.edit().putString(QuickstartPreferences.DraftNewPrayer, "").apply();

        mainActivity.attachment = new ArrayList<ModelPrayerAttachement>();
        mainActivity.selectedFriends = new ArrayList<ModelFriendProfile>();
        mainActivity.popBackFragmentStack();
    }

    private void showAttachmentImage(int page){
        mainActivity.replaceWithAttachmentViewImage(page, mainActivity.attachment, true);
    }

    private void updateAttachmentView(){
        imageButton1.setOnClickListener(null);
        imageButton2.setOnClickListener(null);
        imageButton3.setOnClickListener(null);
        imageButton4.setOnClickListener(null);
        imageButton5.setOnClickListener(null);

        for(int x=0; x<mainActivity.attachment.size(); x++){

            actionbar_attachment_imageButton.setImageResource(R.drawable.ic_actionbar_image_attachment_w);

            attachment_layout.setVisibility(View.VISIBLE);
            if(x==0) {
                Picasso.with(mainActivity).load(mainActivity.attachment.get(x).OriginalFilePath).resize(witdthHeight, witdthHeight).centerCrop().into(imageButton1);
                imageButton1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showAttachmentImage(0);
                    }
                });
            }
            if(x==1) {
                Picasso.with(mainActivity).load(mainActivity.attachment.get(x).OriginalFilePath).resize(witdthHeight, witdthHeight).centerCrop().into(imageButton2);
                imageButton2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showAttachmentImage(1);
                    }
                });
            }
            if(x==2) {
                Picasso.with(mainActivity).load(mainActivity.attachment.get(x).OriginalFilePath).resize(witdthHeight, witdthHeight).centerCrop().into(imageButton3);
                imageButton3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showAttachmentImage(2);
                    }
                });
            }
            if(x==3) {
                Picasso.with(mainActivity).load(mainActivity.attachment.get(x).OriginalFilePath).resize(witdthHeight, witdthHeight).centerCrop().into(imageButton4);
                imageButton4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showAttachmentImage(3);
                    }
                });
            }
            if(x==4) {
                Picasso.with(mainActivity).load(mainActivity.attachment.get(x).OriginalFilePath).resize(witdthHeight, witdthHeight).centerCrop().into(imageButton5);
                imageButton5.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showAttachmentImage(4);
                    }
                });
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && data != null && resultCode == Activity.RESULT_OK) {
            File f = Utils.getAbsolutePath(data, mainActivity);

            ModelPrayerAttachement o = new ModelPrayerAttachement();
            o.OriginalFilePath = "file://" + f.getPath();
            o.GUID = UUID.randomUUID().toString().replace("-", "");
            o.FileName = f.getName();
            o.UserID = mainActivity.OwnerID;
            mainActivity.attachment.add(o);

            updateAttachmentView();

        }
    }
}
