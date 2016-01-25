package com.penyourprayer.penyourprayer.Common.Adapter;

/**
 * Created by sisgks on 06/10/2015.
 */

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.penyourprayer.penyourprayer.Common.Model.ModelPayerAnswered;
import com.penyourprayer.penyourprayer.Common.Model.ViewHolder.ViewHolderPrayerAnsweredModel;
import com.penyourprayer.penyourprayer.R;
import com.penyourprayer.penyourprayer.UI.MainActivity;

import java.util.ArrayList;

public class AdapterListViewAnswered extends ArrayAdapter {
        private MainActivity mainactivity;
        private ArrayList<ModelPayerAnswered> answered;
        public AdapterListViewAnswered(Context context, int resourcesID, ArrayList<ModelPayerAnswered> a) {
                super(context, resourcesID, a);
                // TODO Auto-generated constructor stub
                this.mainactivity = (MainActivity)context;
                answered = a;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

                ViewHolderPrayerAnsweredModel p = new ViewHolderPrayerAnsweredModel();

                LayoutInflater inflater = ((Activity)mainactivity).getLayoutInflater();
                if(convertView == null) {
                        convertView = inflater.inflate(R.layout.list_view_row_prayer_comment, parent, false);
                        p.answered_textview = (TextView) convertView.findViewById(R.id.comment_textView);
                        p.displayname_textview = (TextView) convertView.findViewById(R.id.comment_profile_name_textView);
                        p.touchedwhen_textView = (TextView) convertView.findViewById(R.id.comment_touchedwhen_textView);
                        p.profilePicture_imageView = (ImageView) convertView.findViewById(R.id.comment_profile_img_imageView);
                        convertView.setTag(p);
                }
                else{
                        p = (ViewHolderPrayerAnsweredModel)convertView.getTag();
                }


                /****************************************************************
                *
                * Set Data
                *
                * ****************************************************************/


                String modification = "";
                if(answered.get(position).CreatedWhen.compareTo(answered.get(position).TouchedWhen) != 0)
                        modification = "Edited: ";
                if(answered.get(position).ServerSent)
                        modification = "Synced, " + modification;

                p.answered_textview.setText(answered.get(position).Answered);
                p.displayname_textview.setText(answered.get(position).WhoName);
                p.touchedwhen_textView.setText(modification + answered.get(position).formattedCreatedWhen());

                return convertView;
        }

        public void addComment(ModelPayerAnswered a){
                this.insert(a, 0);
                this.notifyDataSetChanged();
        }

        public void updateCommentList(ArrayList<ModelPayerAnswered> a){
                this.answered = a;
                this.clear();
                this.addAll(a);
                this.notifyDataSetChanged();
        }
}