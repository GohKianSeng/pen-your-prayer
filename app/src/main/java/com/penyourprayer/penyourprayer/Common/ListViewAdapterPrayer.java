package com.penyourprayer.penyourprayer.Common;

/**
 * Created by sisgks on 06/10/2015.
 */

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.penyourprayer.penyourprayer.Database.Database;
import com.penyourprayer.penyourprayer.R;
import com.penyourprayer.penyourprayer.UI.MainActivity;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class ListViewAdapterPrayer extends ArrayAdapter {
        private MainActivity mainactivity;
        private ArrayList<OwnerPrayerModel> resources;

        private Html.ImageGetter imgGetter;

        public ListViewAdapterPrayer(Context context, int resourcesID, ArrayList<OwnerPrayerModel> allprayers) {
                super(context, resourcesID, allprayers);
                // TODO Auto-generated constructor stub
                this.mainactivity = (MainActivity)context;
                resources = allprayers;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

                PrayerModelViewHolder p = new PrayerModelViewHolder();

                LayoutInflater inflater = ((Activity)mainactivity).getLayoutInflater();
                if(convertView == null) {
                        convertView = inflater.inflate(R.layout.card_ui_owner_layout, parent, false);
                        p.thumbnailScrollView = (HorizontalScrollView) convertView.findViewById(R.id.thumbnail_horizontalScrollView);
                        p.gridview = (GridView) convertView.findViewById(R.id.thumbnailGridView);
                        p.prayer_textView = (TextView) convertView.findViewById(R.id.card_ui_prayer_textView);
                        p.amen_imageButton = (ImageButton) convertView.findViewById(R.id.card_ui_amen_imageButton);
                        p.comment_imageButton = (ImageButton) convertView.findViewById(R.id.card_ui_comment_imageButton);
                        p.tagfriend_imageButton = (ImageButton) convertView.findViewById(R.id.card_ui_tagfriend_imageButton);
                        p.publicView_imageButton = (ImageButton) convertView.findViewById(R.id.card_ui_publicview_imageButton);
                        p.answered_imageButton = (ImageButton) convertView.findViewById(R.id.card_ui_answered_imageButton);
                        p.delete_imageButton = (ImageButton) convertView.findViewById(R.id.card_ui_delete_imageButton);
                        p.serversent_textview = (TextView) convertView.findViewById(R.id.card_ui_serversent_textview);
                        p.createdwhen_textview = (TextView) convertView.findViewById(R.id.card_ui_createdwhen);
                        p.amen_count_textview = (TextView) convertView.findViewById(R.id.card_ui_amen_count_textview);
                        convertView.setTag(p);
                }
                else{
                        p = (PrayerModelViewHolder)convertView.getTag();
                }

                p.publicView_imageButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                Database db = new Database(mainactivity);
                                if(resources.get(position).publicView){
                                        resources.get(position).publicView = false;
                                        db.updateOwnerPrayerPublicView(resources.get(position).PrayerID, false);
                                        ((ImageButton) v).setImageResource(R.drawable.public_1);
                                }
                                else{
                                        resources.get(position).publicView = true;
                                        db.updateOwnerPrayerPublicView(resources.get(position).PrayerID, true);
                                        ((ImageButton) v).setImageResource(R.drawable.public_2);
                                }
                        }
                });

                p.amen_imageButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                Database db = new Database(mainactivity);
                                if (resources.get(position).ownerAmen) {
                                        resources.get(position).ownerAmen = false;
                                        ((ImageButton) v).setImageResource(R.drawable.amen_1);
                                        db.AmenOwnerPrayer(resources.get(position).PrayerID, mainactivity.OwnerID, mainactivity.OwnerDisplayName, mainactivity.OwnerProfilePictureURL, false);
                                } else {
                                        resources.get(position).ownerAmen = true;
                                        db.AmenOwnerPrayer(resources.get(position).PrayerID, mainactivity.OwnerID, mainactivity.OwnerDisplayName, mainactivity.OwnerProfilePictureURL, true);
                                        ((ImageButton) v).setImageResource(R.drawable.amen_2);
                                }
                        }
                });

                p.tagfriend_imageButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                Database db = new Database(mainactivity);
                                resources.get(position).selectedFriends = db.getSelectedTagFriend(resources.get(position).PrayerID);
                                mainactivity.selectedFriends = resources.get(position).selectedFriends;
                                mainactivity.replaceWithTagAFriend(resources.get(position).PrayerID);
                        }
                });





                /****************************************************************
                *
                * Set Data
                *
                * ****************************************************************/
                p.amen_count_textview.setText(String.valueOf(resources.get(position).numberOfAmen));
                p.prayer_textView.setText(Html.fromHtml(resources.get(position).Content));


                Database db = new Database(mainactivity);
                ArrayList<ModelPrayerAttachement> att  = db.getAllOwnerPrayerAttachment(resources.get(position).PrayerID);
                if(att.size() > 0)
                {
                        p.thumbnailScrollView.setVisibility(View.VISIBLE);
                        AdapterThumbnailGridView asd = new AdapterThumbnailGridView(mainactivity, att);
                        p.gridview.setNumColumns(att.size());
                        LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams)p.gridview.getLayoutParams();
                        linearParams.width=att.size() * 220;
                        p.gridview.setLayoutParams(linearParams);
                        p.gridview.setAdapter(asd);
                }

                p.createdwhen_textview.setText("Pen Date: " + resources.get(position).formattedCreatedWhen());
                if(resources.get(position).ServerSent)
                        p.serversent_textview.setText("Synced.");
                else
                        p.serversent_textview.setText("Saved.");
                //set all the content to the components
                if(resources.get(position).numberOfFriendsTag > 0)
                        p.tagfriend_imageButton.setImageResource(R.drawable.tagfriend_2);
                else
                        p.tagfriend_imageButton.setImageResource(R.drawable.tagfriend_1);

                if(resources.get(position).ownerAmen)
                        p.amen_imageButton.setImageResource(R.drawable.amen_2);
                else
                        p.amen_imageButton.setImageResource(R.drawable.amen_1);

                if(resources.get(position).numberOfComment > 0)
                        p.comment_imageButton.setImageResource(R.drawable.comment_2);
                else
                        p.comment_imageButton.setImageResource(R.drawable.comment_1);

                if(resources.get(position).publicView)
                        p.publicView_imageButton.setImageResource(R.drawable.public_2);
                else
                        p.publicView_imageButton.setImageResource(R.drawable.public_1);

                return convertView;
        }

        public void updatePrayerList(ArrayList<OwnerPrayerModel> allprayers){
                this.resources = allprayers;
                this.notifyDataSetChanged();
        }
}