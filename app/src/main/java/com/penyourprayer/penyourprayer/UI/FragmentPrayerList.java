package com.penyourprayer.penyourprayer.UI;


import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.melnykov.fab.FloatingActionButton;
import com.penyourprayer.penyourprayer.Common.Adapter.AdapterListViewPrayer;
import com.penyourprayer.penyourprayer.Common.Interface.InterfacePrayerListUpdated;
import com.penyourprayer.penyourprayer.Common.Model.ModelOwnerPrayer;
import com.penyourprayer.penyourprayer.Database.Database;
import com.penyourprayer.penyourprayer.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentPrayerList extends Fragment implements InterfacePrayerListUpdated {

    private ActionBar actionBar;
    private ImageView mImageView;
    private TextView mTextView;
    private FloatingActionButton fab;
    private MainActivity mainActivity;
    private ImageButton prayer_list_prayer_request_option_ImageButton, prayer_list_friend_option_ImageButton;
    private SwipeRefreshLayout prayer_list_swiperefresh;
    private View previousPrayerListCategory = null;
    private AdapterListViewPrayer prayerArrayAdapter;
    public FragmentPrayerList() {
        // Required empty public constructor
    }
    private ArrayList<ModelOwnerPrayer> allprayers;
    private ListView listView;

    @Override
    public void onCreate(Bundle safeInstanceState){
        super.onCreate(safeInstanceState);
        mainActivity = ((MainActivity) getActivity());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);

        View mCustomView = inflater.inflate(R.layout.actionbar_prayer_list, null);
        actionBar.setCustomView(mCustomView);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.show();

        prayer_list_friend_option_ImageButton = (ImageButton)mCustomView.findViewById(R.id.prayer_list_friend_option_ImageButton);
        prayer_list_friend_option_ImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mainActivity.isNavigationDrawerOpened(Gravity.LEFT)) {
                    prayer_list_friend_option_ImageButton.setImageResource(R.drawable.ic_actionbar_drawer_close);
                    mainActivity.showNavigationDrawer(Gravity.LEFT, false);
                } else {
                    prayer_list_friend_option_ImageButton.setImageResource(R.drawable.ic_actionbar_drawer_open);
                    prayer_list_prayer_request_option_ImageButton.setImageResource(R.drawable.ic_actionbar_drawer_open);
                    mainActivity.showNavigationDrawer(Gravity.LEFT, true);
                    mainActivity.showNavigationDrawer(Gravity.RIGHT, false);
                }

            }
        });

        prayer_list_prayer_request_option_ImageButton = (ImageButton)mCustomView.findViewById(R.id.prayer_list_prayer_request_option_ImageButton);
        prayer_list_prayer_request_option_ImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mainActivity.isNavigationDrawerOpened(Gravity.RIGHT)) {
                    prayer_list_prayer_request_option_ImageButton.setImageResource(R.drawable.ic_actionbar_drawer_open);
                    mainActivity.showNavigationDrawer(Gravity.RIGHT, false);
                } else {
                    prayer_list_prayer_request_option_ImageButton.setImageResource(R.drawable.ic_actionbar_drawer_close);
                    prayer_list_friend_option_ImageButton.setImageResource(R.drawable.ic_actionbar_drawer_close);
                    mainActivity.showNavigationDrawer(Gravity.RIGHT, true);
                    mainActivity.showNavigationDrawer(Gravity.LEFT, false);
                }

            }
        });

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_prayer_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainActivity.lockDrawer(false);

        View.OnClickListener prayerCategoryListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(previousPrayerListCategory != null){
                    ((TextView)previousPrayerListCategory).setTextColor(Color.parseColor("#8a8a8a"));
                    ((TextView)previousPrayerListCategory).setTypeface(null, Typeface.NORMAL);
                }
                ((TextView)v).setTextColor(Color.parseColor("#800080"));
                ((TextView)v).setTypeface(null, Typeface.BOLD);

                previousPrayerListCategory = v;
                onChangePrayerCategory(v.getId());
            }
        };

        ((TextView)view.findViewById(R.id.prayerlist_category_all)).setOnClickListener(prayerCategoryListener);
        ((TextView)view.findViewById(R.id.prayerlist_category_mine)).setOnClickListener(prayerCategoryListener);
        ((TextView)view.findViewById(R.id.prayerlist_category_friend)).setOnClickListener(prayerCategoryListener);
        ((TextView)view.findViewById(R.id.prayerlist_category_public)).setOnClickListener(prayerCategoryListener);

        prayer_list_swiperefresh = (SwipeRefreshLayout)view.findViewById(R.id.prayer_list_swiperefresh);
        prayer_list_swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        prayer_list_swiperefresh.setRefreshing(false);
                    }
                }, 5000);
            }
        });
        prayer_list_swiperefresh.setColorSchemeColors(Color.parseColor("#800080"));

        if (view != null) {
            InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        //int listImages[] = new int[]{R.drawable.angry_1, R.drawable.angry_2,
        //        R.drawable.angry_3, R.drawable.angry_4, R.drawable.angry_5};


        Database db = new Database(mainActivity);
        allprayers = db.getAllOwnerPrayer(mainActivity.OwnerID);

        prayerArrayAdapter = new AdapterListViewPrayer(this, this.getActivity(), R.layout.card_ui_owner_layout, allprayers);

        listView = (ListView) view.findViewById(R.id.prayer_listView);
        listView.setFastScrollEnabled(true);
        listView.setAdapter(prayerArrayAdapter);


        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.attachToListView(listView);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.replaceWithCreateNewPrayerFragment();
            }
        });

        listView.setOnScrollListener(new AbsListView.OnScrollListener(){
            private int mLastFirstVisibleItem;
            private int scrollCount = 0;
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                //super.onScrollStateChanged(view,scrollState);
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                Log.e("Testing", String.valueOf(mLastFirstVisibleItem) + " - " + String.valueOf(firstVisibleItem) );
                if(mLastFirstVisibleItem<firstVisibleItem)
                {
                    if(scrollCount >= 0)
                        scrollCount = -1;
                    else if(scrollCount < 0)
                        scrollCount --;

                    if(scrollCount < -1) {
                        fab.hide(true);
                        //actionBar.hide();
                    }
                }
                if(mLastFirstVisibleItem>firstVisibleItem)
                {
                    if(scrollCount <= 0)
                        scrollCount = 1;
                    else if(scrollCount > 0)
                        scrollCount ++;

                    if(scrollCount > 1) {
                        fab.show(true);
                        //actionBar.show();
                    }
                }
                mLastFirstVisibleItem=firstVisibleItem;


                int topRowVerticalPosition =
                        (listView == null || listView.getChildCount() == 0) ?
                                0 : listView.getChildAt(0).getTop();
                prayer_list_swiperefresh.setEnabled(firstVisibleItem == 0 && topRowVerticalPosition >= 0);
            }
        });

    }

    public void removeItem(int position){
        prayerArrayAdapter.remove(prayerArrayAdapter.getItem(position));
        prayerArrayAdapter.notifyDataSetChanged();
    }

    private void onChangePrayerCategory(int category){
        Log.e("Testing", "Category - " + String.valueOf(category));
    }

    @Override
    public void onListUpdate(final ArrayList<ModelOwnerPrayer> allprayers){
        Runnable run = new Runnable(){
            public void run(){
                prayerArrayAdapter.updatePrayerList(allprayers);
            }
        };
        mainActivity.runOnUiThread(run);

    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
