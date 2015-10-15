package com.penyourprayer.penyourprayer.UI;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.penyourprayer.penyourprayer.Common.FriendProfileModel;
import com.penyourprayer.penyourprayer.Common.ProfileFriendListViewAdapter;
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
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private ListView list;
    private ArrayList<FriendProfileModel> friends;
    private boolean startSearching = false;
    private String searchFor = "";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private MainActivity mainActivity;
    EditText searcheditText;
    private ProfileFriendListViewAdapter adapter;
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentTagAFriend.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentTagAFriend newInstance(String param1, String param2) {
        FragmentTagAFriend fragment = new FragmentTagAFriend();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public FragmentTagAFriend() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tag_afriend, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainActivity = ((MainActivity) getActivity());

        friends = new ArrayList<FriendProfileModel>();
        for(int x=0; x<10; x++) {
            FriendProfileModel f = new FriendProfileModel("Joshua No" + String.valueOf(x), "http://images.pajezy.com/notes/profile.png", false);
            friends.add(f);
        }


        searcheditText = (EditText) view.findViewById(R.id.search_editText);
        searcheditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ( (actionId == EditorInfo.IME_ACTION_DONE) || ((event.getKeyCode() == KeyEvent.KEYCODE_ENTER) && (event.getAction() == KeyEvent.ACTION_DOWN ))){
                    String tString = searcheditText.getText().toString();
                    if(tString.trim().length() > 0)
                    {
                        adapter.getFilter().filter(searchFor);
                    }
                }
                return true;
            }
        });


        list = (ListView)view.findViewById(R.id.tag_a_friend_listView);


        adapter = new ProfileFriendListViewAdapter(this.getActivity(), R.layout.list_view_row_tag_friend, friends);
        list.setAdapter(adapter);
        int sss = adapter.getCount();

        list.setItemsCanFocus(false);
        // we want multiple clicks
        list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FriendProfileModel f = (FriendProfileModel)parent.getItemAtPosition(position);
                f.selected = !f.selected;

                ((CheckBox)view.findViewById(R.id.profile_checkBox)).setChecked(f.selected);
            }
        });
    }

}
