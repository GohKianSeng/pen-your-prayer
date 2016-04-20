package com.belvia.penyourprayer.Common.Adapter;

/**
 * Created by sisgks on 06/10/2015.
 */

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.avocarrot.androidsdk.AvocarrotCustomListener;
import com.avocarrot.androidsdk.CustomModel;
import com.belvia.penyourprayer.Common.Model.ModelOwnerPrayer;
import com.belvia.penyourprayer.Common.Model.ModelPrayerAnswered;
import com.belvia.penyourprayer.Common.Model.ModelPrayerAttachement;
import com.belvia.penyourprayer.Common.Model.ModelPrayerComment;
import com.belvia.penyourprayer.Common.Model.ViewHolder.ViewHolderPrayerModel;
import com.belvia.penyourprayer.Common.Utils;
import com.belvia.penyourprayer.Database.Database;
import com.belvia.penyourprayer.QuickstartPreferences;
import com.belvia.penyourprayer.R;
import com.belvia.penyourprayer.UI.FragmentPrayerList;
import com.belvia.penyourprayer.UI.MainActivity;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.squareup.picasso.Picasso;
import com.facebook.ads.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AdapterListViewPrayer extends ArrayAdapter implements NativeAdsManager.Listener{
        private MainActivity mainactivity;
        private ArrayList<ModelOwnerPrayer> resources;
        int witdthHeight = 1;
        private FragmentPrayerList prayerlistView;
        private NativeAdsManager manager;

        public AdapterListViewPrayer(FragmentPrayerList fpl, Context context, int resourcesID, ArrayList<ModelOwnerPrayer> allprayers) {
                super(context, resourcesID, allprayers);

                prayerlistView = fpl;
                // TODO Auto-generated constructor stub
                this.mainactivity = (MainActivity)context;
                resources = allprayers;
                witdthHeight = Utils.dpToPx(mainactivity, QuickstartPreferences.thumbnailDPsize);

                manager = new NativeAdsManager(mainactivity, "1643913965854375_1719107795001658", 5);
                manager.setListener(this);
                manager.loadAds();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

                ViewHolderPrayerModel p = new ViewHolderPrayerModel();

                LayoutInflater inflater = mainactivity.getLayoutInflater();
                if(convertView == null || ((ViewHolderPrayerModel)convertView.getTag()).PrayerID.compareToIgnoreCase(resources.get(position).PrayerID)!=0 ) {
                        p.PrayerID = resources.get(position).PrayerID;
                        p.isPrayerAnswered = resources.get(position).numberOfAnswered > 0;
                        if(resources.get(position).isNativeAd){

                                convertView = inflater.inflate(R.layout.list_view_row_facebook_nativeads, parent, false);
                                p.nativeAdIcon = (ImageView)convertView.findViewById(R.id.native_ad_icon);
                                p.nativeAdTitle = (TextView)convertView.findViewById(R.id.native_ad_title);
                                p.nativeAdBody = (TextView)convertView.findViewById(R.id.native_ad_body);
                                p.nativeAdMedia = (MediaView)convertView.findViewById(R.id.native_ad_media);
                                p.nativeAdSocialContext = (TextView)convertView.findViewById(R.id.native_ad_social_context);
                                p.nativeAdCallToAction = (Button)convertView.findViewById(R.id.native_ad_call_to_action);
                        }
                        else if(p.isPrayerAnswered) {
                                convertView = inflater.inflate(R.layout.card_ui_answered_owner_layout, parent, false);
                                p.expandableTextView = (ExpandableTextView) convertView.findViewById(R.id.expandable_textview);
                                p.prayer_textView = (TextView) convertView.findViewById(R.id.card_ui_answered_prayer_textView);
                        }
                        else {
                                convertView = inflater.inflate(R.layout.card_ui_owner_layout, parent, false);
                                p.prayer_textView = (TextView) convertView.findViewById(R.id.card_ui_prayer_textView);
                        }
                        if(!resources.get(position).isNativeAd) {

                                p.thumbnailHorizontalView = (LinearLayout) convertView.findViewById(R.id.attachment_linearlayout);
                                p.amen_imageButton = (ImageButton) convertView.findViewById(R.id.card_ui_amen_imageButton);
                                p.comment_imageButton = (ImageButton) convertView.findViewById(R.id.card_ui_comment_imageButton);
                                p.tagfriend_imageButton = (ImageButton) convertView.findViewById(R.id.card_ui_tagfriend_imageButton);
                                p.publicView_imageButton = (ImageButton) convertView.findViewById(R.id.card_ui_publicview_imageButton);
                                p.answered_imageButton = (ImageButton) convertView.findViewById(R.id.card_ui_answered_imageButton);
                                p.delete_imageButton = (ImageButton) convertView.findViewById(R.id.card_ui_delete_imageButton);
                                p.progressBar = (ProgressBar) convertView.findViewById(R.id.card_ui_progressbar);
                                p.createdwhen_textview = (TextView) convertView.findViewById(R.id.card_ui_createdwhen);

                                p.amen_count_textview = (TextView) convertView.findViewById(R.id.card_ui_amen_count_textview);
                                //p.att  = db.getAllOwnerPrayerAttachment(resources.get(position).PrayerID);
                                p.containAttachment = resources.get(position).attachments.size() > 0;
                                p.image1 = (ImageButton) convertView.findViewById(R.id.prayerlist_imageButton1);
                                p.image2 = (ImageButton) convertView.findViewById(R.id.prayerlist_imageButton2);
                                p.image3 = (ImageButton) convertView.findViewById(R.id.prayerlist_imageButton3);
                                p.image4 = (ImageButton) convertView.findViewById(R.id.prayerlist_imageButton4);
                                p.image5 = (ImageButton) convertView.findViewById(R.id.prayerlist_imageButton5);
                                if (p.containAttachment)
                                        p.thumbnailHorizontalView.setVisibility(View.VISIBLE);
                                else
                                        p.thumbnailHorizontalView.setVisibility(View.GONE);
                        }
                        convertView.setTag(p);
                }
                else{
                        p = (ViewHolderPrayerModel)convertView.getTag();
                }

                if(resources.get(position).isNativeAd) {
                        NativeAd nativeAd = resources.get(position).facebook_nativeAd;

                        p.nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
                        p.nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
                        p.nativeAdTitle.setText(nativeAd.getAdTitle());
                        p.nativeAdBody.setText(nativeAd.getAdBody());

                        // Downloading and setting the ad icon.
                        NativeAd.Image adIcon = nativeAd.getAdIcon();
                        NativeAd.downloadAndDisplayImage(adIcon, p.nativeAdIcon);

                        p.nativeAdMedia.setNativeAd(nativeAd);

                        nativeAd.registerViewForInteraction(convertView);
                        return convertView;
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
                                ArrayList<ModelPrayerAnswered> answered = db.getAllOwnerPrayerAnswered(resources.get(position).PrayerID);
                                mainactivity.replaceWithPrayerAnswered(answered, resources.get(position).PrayerID);
                        }
                });

                p.comment_imageButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                Database db = new Database(mainactivity);
                                ArrayList<ModelPrayerComment> comments = db.getAllOwnerPrayerComment(resources.get(position).PrayerID);
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
                                updateItem(position, db.GetPrayer(resources.get(position).PrayerID));
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
                                updateItem(position, db.GetPrayer(resources.get(position).PrayerID));
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
                        if(x == 4) {
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

                if(resources.get(position).InQueue > 0) {
                        p.progressBar.setVisibility(View.VISIBLE);
                        p.createdwhen_textview.setVisibility(View.GONE);
                }
                else {
                        p.progressBar.setVisibility(View.INVISIBLE);
                        p.createdwhen_textview.setVisibility(View.VISIBLE);
                }
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
                        p.answered_imageButton.setImageResource(R.drawable.ic_actionbar_check_p);
                else
                        p.answered_imageButton.setImageResource(R.drawable.ic_actionbar_timeglass_g);

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

        private void updateItem(int index, ModelOwnerPrayer p){
                this.resources.set(index, p);
                this.notifyDataSetChanged();
        }

        @Override
        public void onAdsLoaded(){
                ModelOwnerPrayer prayer = new ModelOwnerPrayer();
                prayer.isNativeAd = true;
                prayer.facebook_nativeAd = manager.nextNativeAd();
                prayer.PrayerID = UUID.randomUUID().toString();
                this.resources.add(0, prayer);

                prayer = new ModelOwnerPrayer();
                prayer.isNativeAd = true;
                prayer.facebook_nativeAd = manager.nextNativeAd();
                prayer.PrayerID = UUID.randomUUID().toString();
                this.resources.add(prayer);
                this.notifyDataSetChanged();
        }

        @Override
        public void onAdError(AdError var1){

        }

}