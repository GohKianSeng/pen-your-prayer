package com.penyourprayer.penyourprayer.Common;

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
import android.widget.TextView;

import com.penyourprayer.penyourprayer.Database.Database;
import com.penyourprayer.penyourprayer.R;
import com.penyourprayer.penyourprayer.UI.MainActivity;

import java.util.ArrayList;

public class ListViewAdapterPrayer extends ArrayAdapter {
        public ArrayList<OwnerPrayerModel> resource;
        private MainActivity mainactivity;
        public ListViewAdapterPrayer(Context context, int resourcesID, ArrayList<OwnerPrayerModel> resource) {
                super(context, resourcesID, resource);
                // TODO Auto-generated constructor stub
                this.mainactivity = (MainActivity)context;
                this.resource = resource;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

                PrayerModelViewHolder p = new PrayerModelViewHolder();

                LayoutInflater inflater = ((Activity)mainactivity).getLayoutInflater();
                if(convertView == null) {
                        convertView = inflater.inflate(R.layout.card_ui_owner_layout, parent, false);
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
                                if(resource.get(position).publicView){
                                        resource.get(position).publicView = false;
                                        db.updateOwnerPrayerPublicView(resource.get(position).GUID, false);
                                        ((ImageButton) v).setImageResource(R.drawable.public_1);
                                }
                                else{
                                        resource.get(position).publicView = true;
                                        db.updateOwnerPrayerPublicView(resource.get(position).GUID, true);
                                        ((ImageButton) v).setImageResource(R.drawable.public_2);
                                }
                        }
                });

                p.amen_imageButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                Database db = new Database(mainactivity);
                                if (resource.get(position).ownerAmen) {
                                        resource.get(position).ownerAmen = false;
                                        ((ImageButton) v).setImageResource(R.drawable.amen_1);
                                        db.AmenOwnerPrayer(resource.get(position).GUID, mainactivity.OwnerGUID, mainactivity.OwnerName, mainactivity.OwnerProfilePicture, false);
                                } else {
                                        resource.get(position).ownerAmen = true;
                                        db.AmenOwnerPrayer(resource.get(position).GUID, mainactivity.OwnerGUID, mainactivity.OwnerName, mainactivity.OwnerProfilePicture, true);
                                        ((ImageButton) v).setImageResource(R.drawable.amen_2);
                                }
                        }
                });

                p.tagfriend_imageButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                Database db = new Database(mainactivity);
                                resource.get(position).selectedFriends = db.getSelectedTagFriend(resource.get(position).GUID);
                                mainactivity.selectedFriends = resource.get(position).selectedFriends;
                                mainactivity.replaceWithTagAFriend(resource.get(position).GUID);
                        }
                });





                //set data
                p.amen_count_textview.setText(String.valueOf(resource.get(position).numberOfAmen));
                p.prayer_textView.setText(Html.fromHtml(resource.get(position).Content));
                p.createdwhen_textview.setText("Pen Date: " + resource.get(position).formattedCreatedWhen());
                if(resource.get(position).ServerSent)
                        p.serversent_textview.setText("Synced.");
                //set all the content to the components
                if(resource.get(position).numberOfFriendsTag > 0)
                        p.tagfriend_imageButton.setImageResource(R.drawable.tagfriend_2);
                else
                        p.tagfriend_imageButton.setImageResource(R.drawable.tagfriend_1);

                if(resource.get(position).ownerAmen)
                        p.amen_imageButton.setImageResource(R.drawable.amen_2);
                else
                        p.amen_imageButton.setImageResource(R.drawable.amen_1);

                if(resource.get(position).numberOfComment > 0)
                        p.comment_imageButton.setImageResource(R.drawable.comment_2);
                else
                        p.comment_imageButton.setImageResource(R.drawable.comment_1);

                if(resource.get(position).publicView)
                        p.publicView_imageButton.setImageResource(R.drawable.public_2);
                else
                        p.publicView_imageButton.setImageResource(R.drawable.public_1);

                return convertView;
        }
}