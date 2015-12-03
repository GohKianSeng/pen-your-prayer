package com.penyourprayer.penyourprayer.Common.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

import com.penyourprayer.penyourprayer.Common.ImageLoad.ImageLoader;
import com.penyourprayer.penyourprayer.Common.Model.ModelPrayerAttachement;
import com.penyourprayer.penyourprayer.R;
import com.penyourprayer.penyourprayer.UI.FragmentAttachmentViewImageRowItem;
import com.penyourprayer.penyourprayer.UI.MainActivity;

import java.util.ArrayList;

import uk.co.senab.photoview.PhotoView;
/**
 * Created by sisgks on 30/11/2015.
 */
public class AdapterViewPageImage extends FragmentStatePagerAdapter {
    private boolean allowModification = false;
    private MainActivity mainActivity;
    private ImageLoader imageLoader;
    ArrayList<ModelPrayerAttachement> attachment;

    public AdapterViewPageImage(FragmentManager fm, MainActivity ma, ArrayList<ModelPrayerAttachement> att, boolean allowModification){
        super(fm);
        mainActivity = ma;
        imageLoader = new ImageLoader(mainActivity);
        attachment = att;
        this.allowModification = allowModification;
    }

    @Override
    public Fragment getItem(int pos) {
        return FragmentAttachmentViewImageRowItem.newInstance(attachment.get(pos), this.allowModification);
    }

    @Override
    public int getCount() {
        return attachment.size();
    }

    //@Override
    //public boolean isViewFromObject(View view, Object o) {
    //    return o==view;
    //}

    //@Override
    //public Object instantiateItem(final ViewGroup container, int position) {
    //    PhotoView photoView = new PhotoView(container.getContext());
    //    photoView.setScaleType(ImageView.ScaleType.FIT_CENTER);
    //    imageLoader.DisplayImage(attachment.get(position).URLPath, photoView, false);

        // Now just add PhotoView to ViewPager and return it
    //    container.addView(photoView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

    //    return container;
    //}

    //@Override
    //public void destroyItem(ViewGroup container, int position, Object object) {
    //    container.removeView((View) object);
    //}
}
