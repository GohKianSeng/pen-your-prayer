package com.penyourprayer.penyourprayer.UI;


import android.app.Activity;
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
import android.widget.EditText;
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
import com.onegravity.rteditor.toolbar.HorizontalRTToolbar;
import com.penyourprayer.penyourprayer.Common.Interface.InterfaceFragmentBackHandler;
import com.penyourprayer.penyourprayer.Common.Model.ModelPrayerAttachement;
import com.penyourprayer.penyourprayer.Common.Model.ModelPrayerRequest;
import com.penyourprayer.penyourprayer.Common.Model.ModelPrayerRequestAttachement;
import com.penyourprayer.penyourprayer.Common.Utils;
import com.penyourprayer.penyourprayer.Database.Database;
import com.penyourprayer.penyourprayer.QuickstartPreferences;
import com.penyourprayer.penyourprayer.R;
import com.penyourprayer.penyourprayer.WebAPI.httpClient;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

public class FragmentCreateNewPrayerRequest extends Fragment implements InterfaceFragmentBackHandler {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private int PICK_IMAGE_REQUEST = 1122;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private RTManager mRTManager;
    private MainActivity mainActivity;
    // TODO: Rename and change types of parameters
    private boolean isModification = false;
    private ImageButton actionbar_done;
    RTEditText mRTMessageField;
    private ImageButton imageButton1, imageButton2, imageButton3, imageButton4, imageButton5;
    private ImageButton actionbar_attachment_imageButton, actionbar_answered_imageButton;
    private EditText subject;
    private LinearLayout attachment_layout;
    private boolean answered = false;
    int witdthHeight = 1;
    private ModelPrayerRequest prayerRequest = null;
    private EditText answerComment;

    public FragmentCreateNewPrayerRequest() {
        // Required empty public constructor
    }

    public static FragmentCreateNewPrayerRequest newInstance(ModelPrayerRequest pr, boolean forModification, boolean answered) {
        FragmentCreateNewPrayerRequest fragment = new FragmentCreateNewPrayerRequest();
        fragment.isModification = forModification;
        fragment.prayerRequest = pr;
        fragment.answered = answered;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivity = ((MainActivity) getActivity());
        mainActivity.attachment = new  ArrayList<ModelPrayerAttachement>();
        witdthHeight = Utils.dpToPx(mainActivity, QuickstartPreferences.thumbnailDPsize);

        this.getActivity().setTheme(R.style.ThemeLight);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);

        View mCustomView = inflater.inflate(R.layout.actionbar_create_new_prayer_request, null);
        actionBar.setCustomView(mCustomView);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.show();

        ((ImageButton)mCustomView.findViewById(R.id.createnewprayer_request_back_ImageButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        actionbar_answered_imageButton = (ImageButton)mCustomView.findViewById(R.id.createnewprayer_request_answered_ImageButton);
        actionbar_answered_imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (answered) {
                    actionbar_answered_imageButton.setImageResource(R.drawable.ic_actionbar_uncheck_w);
                    answered = false;
                    answerComment.setVisibility(View.GONE);
                } else {
                    actionbar_answered_imageButton.setImageResource(R.drawable.ic_actionbar_check_w);
                    answered = true;
                    answerComment.setVisibility(View.VISIBLE);
                }
                checkIfAllowDoneActionButton();
            }
        });


        actionbar_attachment_imageButton = (ImageButton)mCustomView.findViewById(R.id.createnewprayer_request_attachment_ImageButton);
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

        actionbar_done = (ImageButton)mCustomView.findViewById(R.id.createnewprayer_request_done_ImageButton);
        actionbar_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isModification)
                    saveNewPrayerRequest();
                else
                    updatePrayerRequest();
            }
        });
        actionbar_done.setEnabled(false);

        return inflater.inflate(R.layout.fragment_create_prayer_reqeuest, container, false);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainActivity.lockDrawer(true);

        answerComment = (EditText) view.findViewById(R.id.create_prayer_request_answer_comment);

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

        subject = (EditText)view.findViewById(R.id.create_prayer_request_subject);
        subject.requestFocus();
        subject.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkIfAllowDoneActionButton();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if(prayerRequest != null){
            subject.setText(prayerRequest.Subject.substring(1));
            mRTMessageField.setRichTextEditing(true, prayerRequest.Description);
            if(!answered)
                answered = prayerRequest.Answered;
            actionbar_answered_imageButton.setVisibility(View.VISIBLE);
            if(answered){
                actionbar_answered_imageButton.setImageResource(R.drawable.ic_actionbar_check_w);
                answerComment.setVisibility(View.VISIBLE);
            }
            mainActivity.pr_attachment = prayerRequest.attachments;
            updateAttachmentView();
            checkIfAllowDoneActionButton();
        }

        if(answered){
            InputMethodManager imm = (InputMethodManager) this.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(answerComment, InputMethodManager.SHOW_IMPLICIT);
        }
        else {
            InputMethodManager imm = (InputMethodManager) this.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(subject, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    public void checkIfAllowDoneActionButton(){
        if(!isModification){
            if (subject.getText().toString().trim().length() > 0) {
                actionbar_done.setImageResource(R.drawable.ic_actionbar_done_w);
                actionbar_done.setEnabled(true);
            } else {
                actionbar_done.setImageResource(R.drawable.ic_actionbar_done_p);
                actionbar_done.setEnabled(false);
            }
        }
        else{
            if(this.answered && answerComment.getText().toString().trim().length() > 0 && subject.getText().toString().trim().length() > 0) {
                actionbar_done.setImageResource(R.drawable.ic_actionbar_done_w);
                actionbar_done.setEnabled(true);
            }
            else if(!this.answered && subject.getText().toString().trim().length() > 0) {
                actionbar_done.setImageResource(R.drawable.ic_actionbar_done_w);
                actionbar_done.setEnabled(true);
            }
            else{
                actionbar_done.setImageResource(R.drawable.ic_actionbar_done_p);
                actionbar_done.setEnabled(false);
            }
        }
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

    private void saveNewPrayerRequest(){
        String description = mRTMessageField.getText(RTFormat.HTML);

        Database db = new Database(mainActivity);
        db.AddNewPrayerRequest(subject.getText().toString(), description, mainActivity.pr_attachment);

        mainActivity.prayerRequest = db.getAllUnansweredPrayerRequest();
        mainActivity.reloadPrayerRequest();
        mainActivity.popBackFragmentStack();
    }

    private void updatePrayerRequest(){
        String description = mRTMessageField.getText(RTFormat.HTML);

        Database db = new Database(mainActivity);
        db.UpdatePrayerRequest(prayerRequest.PrayerRequestID, answered, answerComment.getText().toString().trim(), subject.getText().toString(), description);

        mainActivity.prayerRequest = db.getAllUnansweredPrayerRequest();
        mainActivity.reloadPrayerRequest();
        mainActivity.popBackFragmentStack();
    }

    private void showAttachmentImage(int page){
        mainActivity.replaceWithPrayerRequestAttachmentViewImage(page, mainActivity.pr_attachment, true);
    }

    private void updateAttachmentView(){
        imageButton1.setOnClickListener(null);
        imageButton2.setOnClickListener(null);
        imageButton3.setOnClickListener(null);
        imageButton4.setOnClickListener(null);
        imageButton5.setOnClickListener(null);

        for(int x=0; x<mainActivity.pr_attachment.size(); x++){

            actionbar_attachment_imageButton.setImageResource(R.drawable.ic_actionbar_image_attachment_w);

            attachment_layout.setVisibility(View.VISIBLE);
            if(x==0) {
                Picasso.with(mainActivity).load(mainActivity.pr_attachment.get(x).OriginalFilePath).resize(witdthHeight, witdthHeight).centerCrop().into(imageButton1);
                imageButton1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showAttachmentImage(0);
                    }
                });
            }
            if(x==1) {
                Picasso.with(mainActivity).load(mainActivity.pr_attachment.get(x).OriginalFilePath).resize(witdthHeight, witdthHeight).centerCrop().into(imageButton2);
                imageButton2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showAttachmentImage(1);
                    }
                });
            }
            if(x==2) {
                Picasso.with(mainActivity).load(mainActivity.pr_attachment.get(x).OriginalFilePath).resize(witdthHeight, witdthHeight).centerCrop().into(imageButton3);
                imageButton3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showAttachmentImage(2);
                    }
                });
            }
            if(x==3) {
                Picasso.with(mainActivity).load(mainActivity.pr_attachment.get(x).OriginalFilePath).resize(witdthHeight, witdthHeight).centerCrop().into(imageButton4);
                imageButton4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showAttachmentImage(3);
                    }
                });
            }
            if(x==4) {
                Picasso.with(mainActivity).load(mainActivity.pr_attachment.get(x).OriginalFilePath).resize(witdthHeight, witdthHeight).centerCrop().into(imageButton5);
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
        //mRTManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && data != null && resultCode == Activity.RESULT_OK) {
            File f = Utils.getAbsolutePath(data, mainActivity);

            ModelPrayerRequestAttachement o = new ModelPrayerRequestAttachement();
            o.OriginalFilePath = "file://" + f.getPath();
            o.GUID = UUID.randomUUID().toString().replace("-", "");
            o.FileName = f.getName();
            o.UserID = mainActivity.OwnerID;
            mainActivity.pr_attachment.add(o);

            updateAttachmentView();

        }
    }
}
