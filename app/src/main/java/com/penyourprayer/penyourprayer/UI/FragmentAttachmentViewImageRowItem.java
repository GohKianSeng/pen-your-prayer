package com.penyourprayer.penyourprayer.UI;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.penyourprayer.penyourprayer.Common.Adapter.AdapterViewPageImage;
import com.penyourprayer.penyourprayer.Common.ImageLoad.ImageLoader;
import com.penyourprayer.penyourprayer.Common.Model.ModelPrayerAttachement;
import com.penyourprayer.penyourprayer.Common.ViewPager.HackyViewPager;
import com.penyourprayer.penyourprayer.QuickstartPreferences;
import com.penyourprayer.penyourprayer.R;
import com.squareup.picasso.Picasso;
import com.viewpagerindicator.CirclePageIndicator;

import java.io.File;
import java.util.ArrayList;

import uk.co.senab.photoview.PhotoView;

/**
 * A placeholder fragment containing a simple view.
 */
public class FragmentAttachmentViewImageRowItem extends Fragment {
    private MainActivity mainActivity;
    private ModelPrayerAttachement attachment;
    private boolean allowModification = false;
    private PhotoView photoView;
    private ImageButton deleteImageButton;
    private ImageLoader imageloader;
    public FragmentAttachmentViewImageRowItem() {
        // Required empty public constructor
    }

    public void setResources(ModelPrayerAttachement att){
        this.attachment = att;
    }

    public static FragmentAttachmentViewImageRowItem newInstance(ModelPrayerAttachement att, boolean allowModification) {
        FragmentAttachmentViewImageRowItem fragment = new FragmentAttachmentViewImageRowItem();
        Bundle args = new Bundle();
        args.putBoolean("AllowModification", allowModification);
        fragment.setArguments(args);
        fragment.setResources(att);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        allowModification = getArguments().getBoolean("AllowModification");
        mainActivity = (MainActivity)this.getActivity();
        imageloader = new ImageLoader(mainActivity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_attachment_image_view_item, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        deleteImageButton = (ImageButton) view.findViewById(R.id.delete_attachment_imageButton);
        photoView = (PhotoView) view.findViewById(R.id.image_photoview);
        if(allowModification)
            deleteImageButton.setVisibility(View.VISIBLE);
        else
            deleteImageButton.setVisibility(View.GONE);

        LoadImage(attachment, photoView);

    }

    private void LoadImage(ModelPrayerAttachement att, PhotoView imgbutton){
        String path = "";
        File ls = new File(att.OriginalFilePath);
        if(ls.exists()) {
            Picasso.with(mainActivity).load(att.OriginalFilePath).into(imgbutton);
        }
        else{
            String url = "";
            url = QuickstartPreferences.api_server + "/api/attachment/DownloadPrayerAttachment?AttachmentID=" + att.GUID + "&UserID=" + mainActivity.OwnerID;
            Picasso.with(mainActivity).load(url).into(imgbutton);
        }
    }
}