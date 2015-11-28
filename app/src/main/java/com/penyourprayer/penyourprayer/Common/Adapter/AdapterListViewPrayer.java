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
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.penyourprayer.penyourprayer.Common.ImageLoad.ImageLoader;
import com.penyourprayer.penyourprayer.Common.Model.ModelOwnerPrayer;
import com.penyourprayer.penyourprayer.Common.Model.ViewHolder.ViewHolderPrayerModel;
import com.penyourprayer.penyourprayer.Database.Database;
import com.penyourprayer.penyourprayer.R;
import com.penyourprayer.penyourprayer.UI.MainActivity;

import java.util.ArrayList;

public class AdapterListViewPrayer extends ArrayAdapter {
        private MainActivity mainactivity;
        private ArrayList<ModelOwnerPrayer> resources;
        private Database db;
        private Html.ImageGetter imgGetter;
        private ImageLoader imageLoader;
        public AdapterListViewPrayer(Context context, int resourcesID, ArrayList<ModelOwnerPrayer> allprayers) {
                super(context, resourcesID, allprayers);
                // TODO Auto-generated constructor stub
                this.mainactivity = (MainActivity)context;
                resources = allprayers;
                db = new Database(mainactivity);
                imageLoader = new ImageLoader(mainactivity);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

                ViewHolderPrayerModel p = new ViewHolderPrayerModel();

                LayoutInflater inflater = ((Activity)mainactivity).getLayoutInflater();
                if(convertView == null) {
                        convertView = inflater.inflate(R.layout.card_ui_owner_layout, parent, false);
                        p.thumbnailHorizontalView = (LinearLayout) convertView.findViewById(R.id.attachment_linearlayout);
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
                        p.att  = db.getAllOwnerPrayerAttachment(resources.get(position).PrayerID);
                        p.image1 = (ImageButton) convertView.findViewById(R.id.prayerlist_imageButton1);
                        p.image2 = (ImageButton) convertView.findViewById(R.id.prayerlist_imageButton2);
                        p.image3 = (ImageButton) convertView.findViewById(R.id.prayerlist_imageButton3);
                        p.image4 = (ImageButton) convertView.findViewById(R.id.prayerlist_imageButton4);
                        p.image5 = (ImageButton) convertView.findViewById(R.id.prayerlist_imageButton5);
                        if(p.att.size() > 0)
                                p.thumbnailHorizontalView.setVisibility(View.VISIBLE);
                        else
                                p.thumbnailHorizontalView.setVisibility(View.GONE);
                        convertView.setTag(p);
                }
                else{
                        p = (ViewHolderPrayerModel)convertView.getTag();
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
                for(int x=0; x<p.att.size(); x++){
                        if(x==0)
                                imageLoader.DisplayImage(p.att.get(x).URLPath, p.image1, false);
                        if(x==1)
                                imageLoader.DisplayImage(p.att.get(x).URLPath, p.image2, false);
                        if(x==2)
                                imageLoader.DisplayImage(p.att.get(x).URLPath, p.image3, false);
                        if(x==3)
                                imageLoader.DisplayImage(p.att.get(x).URLPath, p.image4, false);
                        if(x==4)
                                imageLoader.DisplayImage(p.att.get(x).URLPath, p.image5, false);
                }

                p.amen_count_textview.setText(String.valueOf(resources.get(position).numberOfAmen));
                p.prayer_textView.setText(Html.fromHtml(resources.get(position).Content));
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

        public void updatePrayerList(ArrayList<ModelOwnerPrayer> allprayers){
                this.resources = allprayers;
                this.notifyDataSetChanged();
        }
}