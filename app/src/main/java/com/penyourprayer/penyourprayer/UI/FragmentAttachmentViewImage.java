package com.penyourprayer.penyourprayer.UI;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.penyourprayer.penyourprayer.Common.Adapter.AdapterViewPageImage;
import com.penyourprayer.penyourprayer.Common.Model.ModelPrayerAttachement;
import com.penyourprayer.penyourprayer.Common.ViewPager.HackyViewPager;
import com.penyourprayer.penyourprayer.R;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class FragmentAttachmentViewImage extends Fragment {
    private MainActivity mainActivity;
    private HackyViewPager viewpager;
    private static final String ISLOCKED_ARG = "isLocked";
    private int SelectedPage = 0;
    private boolean allowModification = false;
    private ArrayList<ModelPrayerAttachement> attachment;
    public FragmentAttachmentViewImage() {
        // Required empty public constructor
    }

    public void setResources(ArrayList<ModelPrayerAttachement> att){
        this.attachment = att;
    }

    public static FragmentAttachmentViewImage newInstance(int page, ArrayList<ModelPrayerAttachement> att, boolean allowModification) {
        FragmentAttachmentViewImage fragment = new FragmentAttachmentViewImage();
        Bundle args = new Bundle();
        args.putInt("SelectedPage", page);
        args.putBoolean("AllowModification", allowModification);
        fragment.setArguments(args);
        fragment.setResources(att);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SelectedPage = getArguments().getInt("SelectedPage");
        allowModification = getArguments().getBoolean("AllowModification");
        mainActivity = (MainActivity)this.getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        return inflater.inflate(R.layout.fragment_attachment_image_view, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainActivity = (MainActivity)getActivity();

        viewpager = (HackyViewPager) view.findViewById(R.id.view_pager_attachment_image);
        viewpager.setAdapter(new AdapterViewPageImage(this.getFragmentManager(), mainActivity, attachment, allowModification));

        CirclePageIndicator lineIndicator = (CirclePageIndicator) view.findViewById(R.id.view_pager_attachment_image_indicator);
        lineIndicator.setViewPager(viewpager);

        viewpager.setCurrentItem(SelectedPage);

        if (savedInstanceState != null) {
            boolean isLocked = savedInstanceState.getBoolean(ISLOCKED_ARG, false);
            ((HackyViewPager) viewpager).setLocked(isLocked);
        }

    }

}