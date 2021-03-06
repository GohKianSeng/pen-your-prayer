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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.avocarrot.vastparser.model.Linear;
import com.belvia.penyourprayer.Common.Model.ModelPrayerComment;
import com.belvia.penyourprayer.Common.Model.ViewHolder.ViewHolderPrayerCommentModel;
import com.belvia.penyourprayer.Common.Utils;
import com.belvia.penyourprayer.Database.Database;
import com.belvia.penyourprayer.QuickstartPreferences;
import com.belvia.penyourprayer.R;
import com.belvia.penyourprayer.UI.MainActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterListViewComment extends ArrayAdapter {
        private MainActivity mainactivity;
        private ArrayList<ModelPrayerComment> comment;
        private int witdthHeight;
        private AdapterListViewComment current;
        public AdapterListViewComment(Context context, int resourcesID, ArrayList<ModelPrayerComment> c) {
                super(context, resourcesID, c);

                // TODO Auto-generated constructor stub
                this.mainactivity = (MainActivity)context;
                comment = c;
                witdthHeight = Utils.dpToPx(mainactivity, QuickstartPreferences.thumbnailDPsize);
                current = this;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

                ViewHolderPrayerCommentModel p = new ViewHolderPrayerCommentModel();

                LayoutInflater inflater = ((Activity)mainactivity).getLayoutInflater();
                if(position >= comment.size())
                        return convertView;
                if(convertView == null || ((ViewHolderPrayerCommentModel)convertView.getTag()).CommentID.compareToIgnoreCase(comment.get(position).CommentID) != 0) {
                        convertView = inflater.inflate(R.layout.list_view_row_prayer_comment, parent, false);

                        p.viewPreviousReplyLayout = (LinearLayout) convertView.findViewById(R.id.comment_reply_previous_layout);
                        p.commentreply1_Layout = (LinearLayout) convertView.findViewById(R.id.comment_reply_layout1);
                        p.commentreply2_Layout = (LinearLayout) convertView.findViewById(R.id.comment_reply_layout2);
                        p.commentreply1_profilePicture_imageview = (ImageView) convertView.findViewById(R.id.comment_profile_img_imageView1);
                        p.commentreply2_profilePicture_imageview = (ImageView) convertView.findViewById(R.id.comment_profile_img_imageView2);
                        p.commentreply1_name_textview = (TextView) convertView.findViewById(R.id.comment_profile_name_textView1);
                        p.commentreply2_name_textview = (TextView) convertView.findViewById(R.id.comment_profile_name_textView2);
                        p.commentreply1_content_textview = (TextView) convertView.findViewById(R.id.comment_textView1);
                        p.commentreply2_content_textview = (TextView) convertView.findViewById(R.id.comment_textView2);

                        p.replyMainLayout = (LinearLayout) convertView.findViewById(R.id.comment_reply_main_layout);
                        p.comment_textview = (TextView) convertView.findViewById(R.id.comment_textView);
                        p.displayname_textview = (TextView) convertView.findViewById(R.id.comment_profile_name_textView);
                        p.touchedwhen_textView = (TextView) convertView.findViewById(R.id.comment_touchedwhen_textView);
                        p.profilePicture_imageView = (ImageView) convertView.findViewById(R.id.comment_profile_img_imageView);
                        p.progressBar = (ProgressBar) convertView.findViewById(R.id.comment_progressbar);
                        p.CommentID = comment.get(position).CommentID;
                        p.edit_imageButton = (TextView) convertView.findViewById(R.id.comment_edit_button);
                        p.delete_imageButton = (TextView) convertView.findViewById(R.id.comment_delete_button);
                        p.reply_imageButton = (TextView) convertView.findViewById(R.id.comment_reply_button);
                        p.OwnerLayout = convertView.findViewById(R.id.comment_owner_layout);

                        p.edit_imageButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                        mainactivity.replaceWithPrayerCommentModification(comment.get(position));
                                }
                        });
                        p.delete_imageButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                        Database db = new Database(mainactivity);
                                        db.DeletePrayerComment(comment.get(position).CommentID);
                                        comment.remove(position);
                                        current.notifyDataSetChanged();
                                }
                        });

                        View.OnClickListener i = new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                        mainactivity.replaceWithPrayerCommentReply(comment.get(position).CommentID, comment.get(position).OwnerPrayerID);
                                }
                        };

                        p.reply_imageButton.setOnClickListener(i);
                        p.viewPreviousReplyLayout.setOnClickListener(i);
                        p.replyMainLayout.setOnClickListener(i);

                        if(comment.get(position).commentReply.size() > 0) {
                                if(comment.get(position).commentReply.size() > 2) {
                                        p.viewPreviousReplyLayout.setVisibility(View.VISIBLE);
                                }
                                p.replyMainLayout.setVisibility(View.VISIBLE);

                                for(int xx=0; xx < comment.get(position).commentReply.size(); xx++){
                                        if(xx==0){
                                                p.commentreply2_content_textview.setText(comment.get(position).commentReply.get(xx).CommentReply);
                                                p.commentreply2_name_textview.setText(comment.get(position).commentReply.get(xx).WhoName);
                                                Picasso.with(convertView.getContext()).load(comment.get(position).commentReply.get(xx).WhoProfilePicture).resize(witdthHeight, witdthHeight).centerCrop().into(p.commentreply2_profilePicture_imageview);
                                                p.commentreply2_Layout.setVisibility(View.VISIBLE);
                                        }

                                        if(xx==1){
                                                p.commentreply1_content_textview.setText(comment.get(position).commentReply.get(xx).CommentReply);
                                                p.commentreply1_name_textview.setText(comment.get(position).commentReply.get(xx).WhoName);
                                                Picasso.with(convertView.getContext()).load(comment.get(position).commentReply.get(xx).WhoProfilePicture).resize(witdthHeight, witdthHeight).centerCrop().into(p.commentreply1_profilePicture_imageview);
                                                p.commentreply1_Layout.setVisibility(View.VISIBLE);
                                        }
                                }
                        }
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
                if(comment.get(position).CreatedWhen != comment.get(position).TouchedWhen)
                        modification = "Edited: ";

                if(comment.get(position).InQueue > 0) {
                        p.progressBar.setVisibility(View.VISIBLE);
                        p.touchedwhen_textView.setVisibility(View.GONE);
                }
                else{
                        modification = "Synced, " + modification;
                        p.progressBar.setVisibility(View.INVISIBLE);
                        p.touchedwhen_textView.setVisibility(View.VISIBLE);
                }

                p.comment_textview.setText(comment.get(position).Comment);
                p.displayname_textview.setText(comment.get(position).WhoName);
                p.touchedwhen_textView.setText(modification + Utils.UnixTimeReadableString(comment.get(position).TouchedWhen));

                if(comment.get(position).WhoID.compareToIgnoreCase(mainactivity.OwnerID) == 0){
                        p.OwnerLayout.setVisibility(View.VISIBLE);
                }
                else{
                        p.OwnerLayout.setVisibility(View.GONE);
                }

                if(mainactivity.OwnerProfilePictureURL != null && mainactivity.OwnerProfilePictureURL.length() > 0)
                        Picasso.with(convertView.getContext()).load(comment.get(position).WhoProfilePicture).resize(witdthHeight, witdthHeight).centerCrop().into(p.profilePicture_imageView);

                return convertView;
        }

        public void addComment(ModelPrayerComment c){
                this.comment.add(0, c);
                this.notifyDataSetChanged();
        }

        public void updateCommentList(ArrayList<ModelPrayerComment> c){
                this.comment = c;
                this.clear();
                this.addAll(c);
                this.notifyDataSetChanged();
        }
}