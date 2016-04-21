package com.belvia.penyourprayer.Common.Adapter;

import android.widget.AbsListView;

import com.belvia.penyourprayer.UI.MainActivity;

public class EndlessScrollListener implements AbsListView.OnScrollListener {

    private int visibleThreshold = 5;
    private int currentPage = 0;
    private int previousTotal = 0;
    public boolean loading = true;
    MainActivity mainActivity;
    int currentCategory;

    public EndlessScrollListener(MainActivity mainActivity, int visibleThreshold, int currentCategory) {
        this.visibleThreshold = visibleThreshold;
        this.mainActivity = mainActivity;
        this.currentCategory = currentCategory;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (loading) {
            if (totalItemCount > previousTotal) {
                loading = false;
                previousTotal = totalItemCount;
                currentPage++;
            }
        }
        if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
            // I load the next page of gigs using a background task,
            // but you can call any function here.
            mainActivity.getMorePrayers(currentCategory);
            loading = true;
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }
}