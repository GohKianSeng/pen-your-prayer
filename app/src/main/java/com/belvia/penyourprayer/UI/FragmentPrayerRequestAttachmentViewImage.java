package com.belvia.penyourprayer.UI;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.belvia.penyourprayer.Common.Model.ModelPrayerRequestAttachement;
import com.belvia.penyourprayer.R;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class FragmentPrayerRequestAttachmentViewImage extends Fragment {

    private SliderLayout dSlider;
    private MainActivity mainActivity;
    private int SelectedPage = 0;
    private boolean allowModification = false;
    private ArrayList<ModelPrayerRequestAttachement> attachment;
    public FragmentPrayerRequestAttachmentViewImage() {
        // Required empty public constructor
    }

    public void setResources(ArrayList<ModelPrayerRequestAttachement> att){
        this.attachment = att;
    }

    public static FragmentPrayerRequestAttachmentViewImage newInstance(int page, ArrayList<ModelPrayerRequestAttachement> att, boolean allowModification) {
        FragmentPrayerRequestAttachmentViewImage fragment = new FragmentPrayerRequestAttachmentViewImage();
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

        dSlider = (SliderLayout)view.findViewById(R.id.slider);

        for(int x=0; x<attachment.size(); x++){
            ZoomSliderViewer textSliderView = new ZoomSliderViewer(mainActivity);
            // initialize a SliderLayout
            textSliderView
                    .description("")
                    .image(attachment.get(x).getAvailableURI(mainActivity))
                    .setScaleType(BaseSliderView.ScaleType.CenterInside);
            dSlider.addSlider(textSliderView);
        }

        dSlider.setPresetTransformer(SliderLayout.Transformer.Default);
        dSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        dSlider.setCurrentPosition(SelectedPage, true);

    }

}