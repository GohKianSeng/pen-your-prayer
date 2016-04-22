package com.belvia.penyourprayer.UI;


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

import com.belvia.penyourprayer.Common.Adapter.AdapterListViewPrayer;
import com.belvia.penyourprayer.Common.Interface.InterfacePrayerListUpdated;
import com.belvia.penyourprayer.Common.Model.ModelPrayer;
import com.belvia.penyourprayer.Database.Database;
import com.belvia.penyourprayer.R;
import com.melnykov.fab.FloatingActionButton;

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
    public AdapterListViewPrayer prayerArrayAdapter;
    public FragmentPrayerList() {
        // Required empty public constructor
    }
    private ArrayList<ModelPrayer> allprayers;
    private ListView listView;
    private int currentCategory = R.id.prayerlist_category_mine;
    private boolean loading = false;
    private int visibleThreshold = 2;
    private int previousTotal = 0;
    private AbsListView.OnScrollListener mainScrollListener;
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

        previousPrayerListCategory = view.findViewById(currentCategory);

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
                prayer_list_swiperefresh.setEnabled(false);
                mainActivity.getLatestPrayer(currentCategory);
            }
        });
        prayer_list_swiperefresh.setColorSchemeColors(Color.parseColor("#800080"));

        if (view != null) {
            InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        //int listImages[] = new int[]{R.drawable.angry_1, R.drawable.angry_2,
        //        R.drawable.angry_3, R.drawable.angry_4, R.drawable.angry_5};

        listView = (ListView) view.findViewById(R.id.prayer_listView);
        TextView current = ((TextView)view.findViewById(currentCategory));

        current.setOnClickListener(prayerCategoryListener);
        current.setTextColor(Color.parseColor("#800080"));
        current.setTypeface(null, Typeface.BOLD);
        //false the category to refresh
        int temp = currentCategory;
        currentCategory = 0;
        //false the category to refresh
        onChangePrayerCategory(temp);

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
                        actionBar.hide();
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
                        actionBar.show();
                    }
                }
                mLastFirstVisibleItem=firstVisibleItem;


                int topRowVerticalPosition =
                        (listView == null || listView.getChildCount() == 0) ?
                                0 : listView.getChildAt(0).getTop();
                if(currentCategory != R.id.prayerlist_category_mine)
                    prayer_list_swiperefresh.setEnabled(firstVisibleItem == 0 && topRowVerticalPosition >= 0);
                else{
                    prayer_list_swiperefresh.setEnabled(false);
                }


                if(currentCategory != R.id.prayerlist_category_mine) {
                    if (loading) {
                        if (totalItemCount > previousTotal) {
                            loading = false;
                            previousTotal = totalItemCount;
                        }
                    }
                    if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                        // I load the next page of gigs using a background task,
                        // but you can call any function here.
                        mainActivity.getMorePastPrayers(currentCategory);
                        loading = true;
                    }
                }
            }
        });
    }

    public void removeItem(int position){
        prayerArrayAdapter.remove(prayerArrayAdapter.getItem(position));
        prayerArrayAdapter.notifyDataSetChanged();
    }

    private void onChangePrayerCategory(int category){
        if(currentCategory != category){
            currentCategory = category;
            switch (category){
                case R.id.prayerlist_category_mine:
                    getCategoryMinePrayer();
                    break;
                case R.id.prayerlist_category_public:
                    getCategoryPublicPrayer();
                    break;
                case R.id.prayerlist_category_friend:
                    getCategoryFriendsPrayer();
                    break;
            }

        }
    }

    private void getCategoryFriendsPrayer() {
        Database db = new Database(mainActivity);
        allprayers = db.getAllFriendsPrayer();

        prayerArrayAdapter = new AdapterListViewPrayer(this, this.getActivity(), R.layout.card_ui_owner_layout, allprayers);

        listView.setFastScrollEnabled(true);
        listView.setAdapter(prayerArrayAdapter);
        prayer_list_swiperefresh.setEnabled(false);
    }

    private void getCategoryPublicPrayer() {
        Database db = new Database(mainActivity);
        allprayers = db.getAllPublicPrayer();

        prayerArrayAdapter = new AdapterListViewPrayer(this, this.getActivity(), R.layout.card_ui_owner_layout, allprayers);

        listView.setFastScrollEnabled(true);
        listView.setAdapter(prayerArrayAdapter);
        prayer_list_swiperefresh.setEnabled(false);
    }

    private void getCategoryMinePrayer(){
        Database db = new Database(mainActivity);
        allprayers = db.getAllOwnerPrayer(mainActivity.OwnerID);

        prayerArrayAdapter = new AdapterListViewPrayer(this, this.getActivity(), R.layout.card_ui_owner_layout, allprayers);

        listView.setFastScrollEnabled(true);
        listView.setAdapter(prayerArrayAdapter);
        prayer_list_swiperefresh.setEnabled(false);
    }

    public void replacePrayerList(int selectedCategory){
        if (selectedCategory == R.id.prayerlist_category_public && currentCategory == selectedCategory) {
            currentCategory = 0;
            //false the category to refresh
            onChangePrayerCategory(R.id.prayerlist_category_public);
        }
        else if (selectedCategory == R.id.prayerlist_category_friend && currentCategory == selectedCategory) {
            currentCategory = 0;
            //false the category to refresh
            onChangePrayerCategory(R.id.prayerlist_category_friend);
        }
    }

    @Override
    public void onListUpdate(final int selectedCategory){
        Runnable run = new Runnable() {
            public void run() {
                Database db = new Database(mainActivity);
                if (selectedCategory == R.id.prayerlist_category_mine && currentCategory == selectedCategory) {

                    ArrayList<ModelPrayer> allprayers = db.getAllOwnerPrayer(mainActivity.OwnerID);
                    prayerArrayAdapter.updatePrayerList(allprayers);
                }
                else if (selectedCategory == R.id.prayerlist_category_public && currentCategory == selectedCategory) {
                    loading = false;
                    prayer_list_swiperefresh.setRefreshing(false);
                    prayer_list_swiperefresh.setEnabled(true);
                }
                else if (selectedCategory == R.id.prayerlist_category_friend && currentCategory == selectedCategory) {
                    loading = false;
                    prayer_list_swiperefresh.setRefreshing(false);
                    prayer_list_swiperefresh.setEnabled(true);
                }
            }
        };
        mainActivity.runOnUiThread(run);

    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
