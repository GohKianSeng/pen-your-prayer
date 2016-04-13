package com.belvia.penyourprayer.Common.Adapter;

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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.belvia.penyourprayer.Common.Model.ModelPrayerAnswered;
import com.belvia.penyourprayer.Common.Model.ViewHolder.ViewHolderPrayerAnsweredModel;
import com.belvia.penyourprayer.Common.Utils;
import com.belvia.penyourprayer.R;
import com.belvia.penyourprayer.UI.MainActivity;

import java.util.ArrayList;

public class AdapterListViewAnswered extends ArrayAdapter {
        private MainActivity mainactivity;
        private ArrayList<ModelPrayerAnswered> answered;
        public AdapterListViewAnswered(Context context, int resourcesID, ArrayList<ModelPrayerAnswered> a) {
                super(context, resourcesID, a);
                // TODO Auto-generated constructor stub
                this.mainactivity = (MainActivity)context;
                answered = a;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

                ViewHolderPrayerAnsweredModel p = new ViewHolderPrayerAnsweredModel();

                LayoutInflater inflater = ((Activity)mainactivity).getLayoutInflater();
                if(convertView == null || ((ViewHolderPrayerAnsweredModel)convertView.getTag()).AnsweredID.compareToIgnoreCase(answered.get(position).AnsweredID) != 0) {
                        convertView = inflater.inflate(R.layout.list_view_row_prayer_comment, parent, false);
                        p.answered_textview = (TextView) convertView.findViewById(R.id.comment_textView);
                        p.displayname_textview = (TextView) convertView.findViewById(R.id.comment_profile_name_textView);
                        p.touchedwhen_textView = (TextView) convertView.findViewById(R.id.comment_touchedwhen_textView);
                        p.profilePicture_imageView = (ImageView) convertView.findViewById(R.id.comment_profile_img_imageView);
                        p.progressBar = (ProgressBar) convertView.findViewById(R.id.comment_progressbar);
                        p.AnsweredID = answered.get(position).AnsweredID;
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
                if(answered.get(position).CreatedWhen != answered.get(position).TouchedWhen)
                        modification = "Edited: ";

                if(answered.get(position).InQueue > 0) {
                        p.progressBar.setVisibility(View.VISIBLE);
                        p.touchedwhen_textView.setVisibility(View.GONE);
                }
                else{
                        modification = "Synced, " + modification;
                        p.progressBar.setVisibility(View.INVISIBLE);
                        p.touchedwhen_textView.setVisibility(View.VISIBLE);
                }

                p.answered_textview.setText(answered.get(position).Answered);
                p.displayname_textview.setText(answered.get(position).WhoName);
                p.touchedwhen_textView.setText(modification + Utils.UnixTimeReadableString(answered.get(position).CreatedWhen));

                return convertView;
        }

        public void addComment(ModelPrayerAnswered a){
                this.insert(a, 0);
                this.notifyDataSetChanged();
        }

        public void updateCommentList(ArrayList<ModelPrayerAnswered> a){
                this.answered = a;
                this.clear();
                this.addAll(a);
                this.notifyDataSetChanged();
        }
}