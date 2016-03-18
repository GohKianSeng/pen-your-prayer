package com.penyourprayer.penyourprayer.UI;


import android.app.Fragment;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.penyourprayer.penyourprayer.Common.DataLoading;
import com.penyourprayer.penyourprayer.Common.Model.ModelFriendProfile;
import com.penyourprayer.penyourprayer.Database.Database;
import com.penyourprayer.penyourprayer.QuickstartPreferences;
import com.penyourprayer.penyourprayer.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentInitialSplash#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentInitialSplash extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ArrayList<ModelFriendProfile> friends;

    private MainActivity mainActivity;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentInitialSplash.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentInitialSplash newInstance(String param1, String param2) {
        FragmentInitialSplash fragment = new FragmentInitialSplash();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public FragmentInitialSplash() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //ActionBarActivity activity = (ActionBarActivity) this.getActivity();
        //ActionBar aBar = activity.getSupportActionBar();
        //aBar.hide();

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_initial_splash, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        mainActivity = ((MainActivity) getActivity());

        // Loading Font Face
        Typeface tf = Typeface.createFromAsset(this.getActivity().getAssets(), "fonts/journal.ttf");
        ((TextView) view.findViewById(R.id.splash_appname)).setTypeface(tf);



        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                long userid = mainActivity.sharedPreferences.getLong(QuickstartPreferences.OwnerID, 0);
                String hmacKey = mainActivity.sharedPreferences.getString(QuickstartPreferences.OwnerHMACKey, "");
                if(userid !=  0 && hmacKey.length() > 0){
                    DataLoading dl = new DataLoading(mainActivity);
                    dl.fetchLatestDataFromServer();
                    return "AUTOLOGIN";
                }

                SystemClock.sleep(1000);
                //start checking, to login or already login.
                return "";
            }

            @Override
            protected void onPostExecute(String action) {

                if(action.compareToIgnoreCase("AUTOLOGIN") == 0){
                    Database db = new Database(mainActivity);
                    mainActivity.OwnerID = String.valueOf(mainActivity.sharedPreferences.getLong(QuickstartPreferences.OwnerID, 0));
                    mainActivity.OwnerDisplayName = mainActivity.sharedPreferences.getString(QuickstartPreferences.OwnerDisplayName, "");
                    mainActivity.OwnerProfilePictureURL = mainActivity.sharedPreferences.getString(QuickstartPreferences.OwnerProfilePictureURL, "");
                    mainActivity.friends = db.getAllFriends(mainActivity.OwnerID);
                    mainActivity.prayerRequest = db.getAllUnansweredPrayerRequest();
                    mainActivity.loadLeftRightDrawerContent(true);
                    mainActivity.replaceWithPrayerListFragment();
                }
                else{
                    mainActivity.replaceWithLoginFragment();
                }
            }

        };
        task.execute();
    }


}
