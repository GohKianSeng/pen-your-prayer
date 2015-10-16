package com.penyourprayer.penyourprayer.UI;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;

import com.onegravity.rteditor.RTEditText;
import com.onegravity.rteditor.RTManager;
import com.onegravity.rteditor.api.RTApi;
import com.onegravity.rteditor.api.RTMediaFactoryImpl;
import com.onegravity.rteditor.api.RTProxyImpl;
import com.onegravity.rteditor.toolbar.HorizontalRTToolbar;
import com.penyourprayer.penyourprayer.Common.FragmentBackHandlerInterface;
import com.penyourprayer.penyourprayer.R;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentCreateNewPrayer#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentCreateNewPrayer extends Fragment implements FragmentBackHandlerInterface{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private RTManager mRTManager;
    private MainActivity mainActivity;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    RTEditText mRTMessageField;
    private ImageButton createnew_prayer_tag_friend_ImageButton;
    private boolean publicView = false;
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentCreateNewPrayer.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentCreateNewPrayer newInstance(String param1, String param2) {
        FragmentCreateNewPrayer fragment = new FragmentCreateNewPrayer();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

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
        this.getActivity().setTheme(R.style.ThemeLight);
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
                mainActivity.replaceWithTagAFriend();
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

        return inflater.inflate(R.layout.fragment_create_new_prayer, container, false);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainActivity.lockDrawer(true);

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
}
