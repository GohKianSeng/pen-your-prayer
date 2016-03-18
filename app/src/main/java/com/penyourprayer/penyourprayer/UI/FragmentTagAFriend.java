package com.penyourprayer.penyourprayer.UI;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.penyourprayer.penyourprayer.Common.Adapter.AdapterListViewProfileFriend;
import com.penyourprayer.penyourprayer.Common.Model.ModelFriendProfile;
import com.penyourprayer.penyourprayer.Database.Database;
import com.penyourprayer.penyourprayer.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentTagAFriend#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentTagAFriend extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "GUID";

    private ListView list;
    private ArrayList<ModelFriendProfile> friends;
    private boolean startSearching = false;
    private String searchFor = "";
    // TODO: Rename and change types of parameters
    private String GUID;
    private MainActivity mainActivity;
    EditText searcheditText;
    private AdapterListViewProfileFriend adapter;

    public static FragmentTagAFriend newInstance(String GUID) {
        FragmentTagAFriend fragment = new FragmentTagAFriend();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, GUID);
        fragment.setArguments(args);
        return fragment;
    }

    public FragmentTagAFriend() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mainActivity = ((MainActivity) getActivity());
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            GUID = getArguments().getString(ARG_PARAM1);
        }
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
                if (GUID != null && GUID.length() > 0) {
                    mainActivity.selectedFriends = new ArrayList<ModelFriendProfile>();
                }
                mainActivity.popBackFragmentStack();
            }
        });

        ((ImageButton)mCustomView.findViewById(R.id.tagafriend_done_ImageButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<ModelFriendProfile> selectedFriends = new ArrayList<ModelFriendProfile>();
                for (int x = 0; x < friends.size(); x++) {
                    if (friends.get(x).selected)
                        selectedFriends.add(friends.get(x));
                }
                if (GUID == null) {
                    mainActivity.selectedFriends = selectedFriends;
                    mainActivity.popBackFragmentStack();
                } else if (GUID.length() > 0) {
                    Database db = new Database(mainActivity);
                    db.updateOwnerPrayerTagFriends(GUID, selectedFriends);
                    mainActivity.selectedFriends = new ArrayList<ModelFriendProfile>();
                    mainActivity.popBackFragmentStack();
                }
            }
        });

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tag_afriend, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Database db = new Database(mainActivity);
        friends = db.getAllFriends(mainActivity.OwnerID);
        updateFriendList();


        searcheditText = (EditText) view.findViewById(R.id.search_editText);
        searcheditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((actionId == EditorInfo.IME_ACTION_DONE) || ((event.getKeyCode() == KeyEvent.KEYCODE_ENTER) && (event.getAction() == KeyEvent.ACTION_DOWN))) {
                    String tString = searcheditText.getText().toString();
                    if (tString.trim().length() > 0) {
                        adapter.getFilter().filter(searchFor);
                    }
                }
                return true;
            }
        });


        list = (ListView)view.findViewById(R.id.tag_a_friend_listView);


        adapter = new AdapterListViewProfileFriend(this.getActivity(), R.layout.list_view_row_tag_friend, friends);
        list.setAdapter(adapter);

        list.setItemsCanFocus(false);
        // we want multiple clicks
        list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ModelFriendProfile f = (ModelFriendProfile)parent.getItemAtPosition(position);
                f.selected = !f.selected;

                ((CheckBox)view.findViewById(R.id.profile_checkBox)).setChecked(f.selected);
            }
        });
    }

    private void updateFriendList(){
        for(int x=0; x< friends.size(); x++){
            for(int y=0; y<mainActivity.selectedFriends.size(); y++){
                if(friends.get(x).UserID.compareToIgnoreCase(mainActivity.selectedFriends.get(y).UserID) == 0){
                    friends.get(x).selected = true;
                }
            }
        }
    }

}
