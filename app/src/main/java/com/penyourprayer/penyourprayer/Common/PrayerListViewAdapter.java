package com.penyourprayer.penyourprayer.Common;

/**
 * Created by sisgks on 06/10/2015.
 */

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.penyourprayer.penyourprayer.R;

import java.util.ArrayList;

public class PrayerListViewAdapter extends ArrayAdapter {
        private Context context;
        public ArrayList<PrayerModel> resource;

        public PrayerListViewAdapter(Context context, int resourcesID,  ArrayList<PrayerModel> resource) {
                super(context, resourcesID, resource);
                // TODO Auto-generated constructor stub
                this.context = context;
                this.resource = resource;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

                PrayerModelViewHolder p = new PrayerModelViewHolder();

                LayoutInflater inflater = ((Activity)context).getLayoutInflater();
                if(convertView == null) {
                        convertView = inflater.inflate(R.layout.card_ui_owner_layout, parent, false);
                        p.prayer_textView = (TextView) convertView.findViewById(R.id.prayer_textView);
                        p.amen_imageButton = (ImageButton) convertView.findViewById(R.id.amen_imageButton);

                        convertView.setTag(p);
                }
                else{
                        p = (PrayerModelViewHolder)convertView.getTag();
                }

                p.amen_imageButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                if(resource.get(position).amenSelected) {
                                        ((ImageButton)v).setImageResource(R.drawable.amen_1);
                                        resource.get(position).amenSelected = false;
                                }
                                else{
                                        ((ImageButton)v).setImageResource(R.drawable.amen_2);
                                        resource.get(position).amenSelected = true;
                                }
                        }
                });

                //set all the content to the components
                if(resource.get(position).amenSelected)
                        p.amen_imageButton.setImageResource(R.drawable.amen_2);
                else
                        p.amen_imageButton.setImageResource(R.drawable.amen_1);

                return convertView;
        }
}