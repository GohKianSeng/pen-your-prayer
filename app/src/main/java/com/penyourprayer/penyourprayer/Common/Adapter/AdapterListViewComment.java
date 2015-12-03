package com.penyourprayer.penyourprayer.Common.Adapter;

/**
 * Created by sisgks on 06/10/2015.
 */

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.penyourprayer.penyourprayer.Common.ImageLoad.ImageLoader;
import com.penyourprayer.penyourprayer.Common.Model.ModelOwnerPrayer;
import com.penyourprayer.penyourprayer.Common.Model.ModelPayerComment;
import com.penyourprayer.penyourprayer.Common.Model.ModelPrayerAttachement;
import com.penyourprayer.penyourprayer.Common.Model.ViewHolder.ViewHolderPrayerCommentModel;
import com.penyourprayer.penyourprayer.Common.Utils;
import com.penyourprayer.penyourprayer.Database.Database;
import com.penyourprayer.penyourprayer.QuickstartPreferences;
import com.penyourprayer.penyourprayer.R;
import com.penyourprayer.penyourprayer.UI.MainActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterListViewComment extends ArrayAdapter {
        private MainActivity mainactivity;
        private ArrayList<ModelPayerComment> comment;
        private Database db;
        private ImageLoader imageLoader;
        int witdthHeight = 1;
        public AdapterListViewComment(Context context, int resourcesID, ArrayList<ModelPayerComment> c) {
                super(context, resourcesID, c);
                // TODO Auto-generated constructor stub
                this.mainactivity = (MainActivity)context;
                comment = c;
                db = new Database(mainactivity);
                imageLoader = new ImageLoader(mainactivity);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

                ViewHolderPrayerCommentModel p = new ViewHolderPrayerCommentModel();

                LayoutInflater inflater = ((Activity)mainactivity).getLayoutInflater();
                if(convertView == null) {
                        convertView = inflater.inflate(R.layout.list_view_row_prayer_comment, parent, false);
                        p.comment_textview = (TextView) convertView.findViewById(R.id.comment_textView);
                        p.displayname_textview = (TextView) convertView.findViewById(R.id.comment_profile_name_textView);
                        p.touchedwhen_textView = (TextView) convertView.findViewById(R.id.comment_touchedwhen_textView);
                        p.profilePicture_imageView = (ImageView) convertView.findViewById(R.id.comment_profile_img_imageView);
                        convertView.setTag(p);
                }
                else{
                        p = (ViewHolderPrayerCommentModel)convertView.getTag();
                }


                /****************************************************************
                *
                * Set Data
                *
                * ****************************************************************/


                String modification = "";
                if(comment.get(position).CreatedWhen.compareTo(comment.get(position).TouchedWhen) != 0)
                        modification = "Edited: ";
                if(comment.get(position).ServerSent)
                        modification = "Synced, " + modification;

                p.comment_textview.setText(comment.get(position).Comment);
                p.displayname_textview.setText(comment.get(position).WhoName);
                p.touchedwhen_textView.setText(modification + comment.get(position).formattedCreatedWhen());

                return convertView;
        }

        public void addComment(ModelPayerComment c){
                this.insert(c, 0);
                this.notifyDataSetChanged();
        }

        public void updateCommentList(ArrayList<ModelPayerComment> c){
                this.comment = c;
                this.clear();
                this.addAll(c);
                this.notifyDataSetChanged();
        }
}