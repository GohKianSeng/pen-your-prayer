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
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.penyourprayer.penyourprayer.R;

import java.util.ArrayList;

public class ListViewAdapterDrawerProfileFriend extends ArrayAdapter {
        public ArrayList<FriendProfileModel> friends = null;
        public Context context;
        private ImageLoader imageLoader;

        public ListViewAdapterDrawerProfileFriend(Context context, int resourcesID, ArrayList<FriendProfileModel> resource) {
                super(context, resourcesID, resource);
                // TODO Auto-generated constructor stub
                this.context = context;
                this.friends = resource;
                imageLoader=new ImageLoader(context.getApplicationContext());
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

                TagFriendModelViewHolder p = new TagFriendModelViewHolder();

                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                if (convertView == null) {
                        convertView = inflater.inflate(R.layout.list_view_row_friends_drawer, parent, false);

                        p.name_TextView = (TextView) convertView.findViewById(R.id.drawer_profile_friend_name_textView);
                        p.profile_ImageView = (ImageView) convertView.findViewById(R.id.drawer_profile_friend_image_imageView);
                        convertView.setTag(p);
                } else {
                        p = (TagFriendModelViewHolder) convertView.getTag();
                }

                if(!friends.get(position).isAction) {
                        p.name_TextView.setText(friends.get(position).DisplayName);
                        imageLoader.DisplayImage(friends.get(position).ProfilePictureURL, p.profile_ImageView, true);
                }
                else{
                        p.name_TextView.setText(friends.get(position).actionName.toString());
                }
                return convertView;
        }
}