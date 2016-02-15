package com.penyourprayer.penyourprayer.UI;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import uk.co.senab.photoview.PhotoView;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.penyourprayer.penyourprayer.R;

/**
 * Created by sisgks on 26/01/2016.
 */
public class ZoomSliderViewer extends BaseSliderView {

    public ZoomSliderViewer(Context context) {
        super(context);
    }

    @Override
    public View getView() {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.daimajia_zoom_slider,null);
        PhotoView target = (PhotoView)v.findViewById(R.id.zoom_slider_image);
        bindEventAndShow(v, target);
        return v;
    }
}