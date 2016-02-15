package com.penyourprayer.penyourprayer.Common.Adapter;

/**
 * Created by sisgks on 06/10/2015.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.penyourprayer.penyourprayer.Common.ImageLoad.ImageLoader;
import com.penyourprayer.penyourprayer.Common.Model.ModelOwnerPrayer;
import com.penyourprayer.penyourprayer.Common.Model.ModelPayerAnswered;
import com.penyourprayer.penyourprayer.Common.Model.ModelPayerComment;
import com.penyourprayer.penyourprayer.Common.Model.ModelPrayerAttachement;
import com.penyourprayer.penyourprayer.Common.Model.ViewHolder.ViewHolderPrayerModel;
import com.penyourprayer.penyourprayer.Common.Utils;
import com.penyourprayer.penyourprayer.Database.Database;
import com.penyourprayer.penyourprayer.QuickstartPreferences;
import com.penyourprayer.penyourprayer.R;
import com.penyourprayer.penyourprayer.UI.FragmentPrayerList;
import com.penyourprayer.penyourprayer.UI.MainActivity;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Response;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class AdapterListViewPrayer extends ArrayAdapter {
        private MainActivity mainactivity;
        private ArrayList<ModelOwnerPrayer> resources;
        private Database db;
        private Html.ImageGetter imgGetter;
        private ImageLoader imageLoader;
        int witdthHeight = 1;
        private FragmentPrayerList prayerlistView;
        public AdapterListViewPrayer(FragmentPrayerList fpl, Context context, int resourcesID, ArrayList<ModelOwnerPrayer> allprayers) {
                super(context, resourcesID, allprayers);
                prayerlistView = fpl;
                // TODO Auto-generated constructor stub
                this.mainactivity = (MainActivity)context;
                resources = allprayers;
                db = new Database(mainactivity);
                imageLoader = new ImageLoader(mainactivity);
                witdthHeight = Utils.dpToPx(mainactivity, QuickstartPreferences.thumbnailDPsize);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

                ViewHolderPrayerModel p = new ViewHolderPrayerModel();

                LayoutInflater inflater = mainactivity.getLayoutInflater();
                if(convertView == null || ((ViewHolderPrayerModel)convertView.getTag()).PrayerID.compareToIgnoreCase(resources.get(position).PrayerID)!=0 ) {
                        p.PrayerID = resources.get(position).PrayerID;
                        p.isPrayerAnswered = resources.get(position).numberOfAnswered > 0;
                        if(p.isPrayerAnswered) {
                                convertView = inflater.inflate(R.layout.card_ui_answered_owner_layout, parent, false);
                                p.expandableTextView = (ExpandableTextView) convertView.findViewById(R.id.expandable_textview);
                                p.prayer_textView = (TextView) convertView.findViewById(R.id.card_ui_answered_prayer_textView);
                        }
                        else {
                                convertView = inflater.inflate(R.layout.card_ui_owner_layout, parent, false);
                                p.prayer_textView = (TextView) convertView.findViewById(R.id.card_ui_prayer_textView);
                        }
                        p.thumbnailHorizontalView = (LinearLayout) convertView.findViewById(R.id.attachment_linearlayout);
                        p.amen_imageButton = (ImageButton) convertView.findViewById(R.id.card_ui_amen_imageButton);
                        p.comment_imageButton = (ImageButton) convertView.findViewById(R.id.card_ui_comment_imageButton);
                        p.tagfriend_imageButton = (ImageButton) convertView.findViewById(R.id.card_ui_tagfriend_imageButton);
                        p.publicView_imageButton = (ImageButton) convertView.findViewById(R.id.card_ui_publicview_imageButton);
                        p.answered_imageButton = (ImageButton) convertView.findViewById(R.id.card_ui_answered_imageButton);
                        p.delete_imageButton = (ImageButton) convertView.findViewById(R.id.card_ui_delete_imageButton);
                        p.serversent_textview = (TextView) convertView.findViewById(R.id.card_ui_serversent_textview);
                        p.createdwhen_textview = (TextView) convertView.findViewById(R.id.card_ui_createdwhen);

                        p.amen_count_textview = (TextView) convertView.findViewById(R.id.card_ui_amen_count_textview);
                        //p.att  = db.getAllOwnerPrayerAttachment(resources.get(position).PrayerID);
                        p.containAttachment = resources.get(position).attachments.size() > 0;
                        p.image1 = (ImageButton) convertView.findViewById(R.id.prayerlist_imageButton1);
                        p.image2 = (ImageButton) convertView.findViewById(R.id.prayerlist_imageButton2);
                        p.image3 = (ImageButton) convertView.findViewById(R.id.prayerlist_imageButton3);
                        p.image4 = (ImageButton) convertView.findViewById(R.id.prayerlist_imageButton4);
                        p.image5 = (ImageButton) convertView.findViewById(R.id.prayerlist_imageButton5);
                        if(p.containAttachment)
                                p.thumbnailHorizontalView.setVisibility(View.VISIBLE);
                        else
                                p.thumbnailHorizontalView.setVisibility(View.GONE);

                        convertView.setTag(p);
                }
                else{
                        p = (ViewHolderPrayerModel)convertView.getTag();
                }

                p.delete_imageButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                new AlertDialog.Builder(mainactivity)
                                        .setTitle("Delete Prayer?")
                                        .setMessage("Are you sure?")
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                        Database db = new Database(mainactivity);
                                                        db.deletePrayer(resources.get(position).PrayerID);
                                                        resources = db.getAllOwnerPrayer(mainactivity.OwnerID);
                                                        prayerlistView.removeItem(position);
                                                }
                                        })
                                        .setNegativeButton("No", null)
                                        .show();
                        }
                });

                p.answered_imageButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                Database db = new Database(mainactivity);
                                ArrayList<ModelPayerAnswered> answered = db.getAllOwnerPrayerAnswered(resources.get(position).PrayerID);
                                mainactivity.replaceWithPrayerAnswered(answered, resources.get(position).PrayerID);
                        }
                });

                p.comment_imageButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                Database db = new Database(mainactivity);
                                ArrayList<ModelPayerComment> comments = db.getAllOwnerPrayerComment(resources.get(position).PrayerID);
                                mainactivity.replaceWithPrayerComment(comments, resources.get(position).PrayerID);
                        }
                });

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
                                resources.get(position).selectedFriends = db.getSelectedTagFriend(resources.get(position).PrayerID, mainactivity.OwnerID);
                                mainactivity.selectedFriends = resources.get(position).selectedFriends;
                                mainactivity.replaceWithTagAFriend(resources.get(position).PrayerID);
                        }
                });





                /****************************************************************
                *
                * Set Data
                *
                * ****************************************************************/

                p.image1.setOnClickListener(null);
                p.image2.setOnClickListener(null);
                p.image3.setOnClickListener(null);
                p.image4.setOnClickListener(null);
                p.image5.setOnClickListener(null);

                for(int x=0; x<resources.get(position).attachments.size(); x++){

                        if(x==0) {
                                LoadImage(resources.get(position).attachments, x, p.image1, x);
                        }
                        if(x==1) {
                                LoadImage(resources.get(position).attachments, x, p.image2, x);
                        }
                        if(x==2) {
                                LoadImage(resources.get(position).attachments, x, p.image3, x);
                        }
                        if(x==3) {
                                LoadImage(resources.get(position).attachments, x, p.image4, x);
                        }
                        if(x==4) {
                                LoadImage(resources.get(position).attachments, x, p.image5, x);
                        }
                }

                p.amen_count_textview.setText(String.valueOf(resources.get(position).numberOfAmen));



                if(resources.get(position).numberOfAnswered == 0) {
                        p.prayer_textView.setText(Html.fromHtml(resources.get(position).Content));
                }
                else {
                        if(p.expandableTextView != null)
                                p.expandableTextView.setText(Html.fromHtml(resources.get(position).Content));
                        p.prayer_textView.setText(resources.get(position).Answered);
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

                if(resources.get(position).numberOfAnswered > 0)
                        p.answered_imageButton.setImageResource(R.drawable.answered_2);
                else
                        p.answered_imageButton.setImageResource(R.drawable.answered_1);

                if(resources.get(position).publicView)
                        p.publicView_imageButton.setImageResource(R.drawable.public_2);
                else
                        p.publicView_imageButton.setImageResource(R.drawable.public_1);

                return convertView;
        }

        private void LoadImage(final ArrayList<ModelPrayerAttachement> att, final int position, final ImageButton imgbutton, final int imagePosition){
                Picasso.with(mainactivity).load(att.get(position).getAvailableURI(mainactivity)).resize(witdthHeight, witdthHeight).centerCrop().into(imgbutton);
                imgbutton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                showAttachmentImage(imagePosition, att);
                        }
                });
        }

        private void showAttachmentImage(int page, ArrayList<ModelPrayerAttachement> att){
                mainactivity.replaceWithAttachmentViewImage(page, att, false);
        }

        public void updatePrayerList(ArrayList<ModelOwnerPrayer> allprayers){
                this.resources = allprayers;
                this.notifyDataSetChanged();
        }

}