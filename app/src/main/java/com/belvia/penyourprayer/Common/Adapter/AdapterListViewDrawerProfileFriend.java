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
import android.widget.TextView;

import com.belvia.penyourprayer.Common.Model.ModelFriendProfile;
import com.belvia.penyourprayer.Common.Model.ViewHolder.ViewHolderTagFriendModel;
import com.belvia.penyourprayer.Common.Utils;
import com.belvia.penyourprayer.QuickstartPreferences;
import com.belvia.penyourprayer.R;
import com.belvia.penyourprayer.UI.MainActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterListViewDrawerProfileFriend extends ArrayAdapter {
        public ArrayList<ModelFriendProfile> friends = null;
        public Context context;
        int witdthHeight = 1;
        public AdapterListViewDrawerProfileFriend(Context context, int resourcesID, ArrayList<ModelFriendProfile> resource) {
                super(context, resourcesID, resource);
                // TODO Auto-generated constructor stub
                this.context = context;
                this.friends = resource;
                witdthHeight = Utils.dpToPx((MainActivity)context, QuickstartPreferences.thumbnailDPsize);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

                ViewHolderTagFriendModel p = new ViewHolderTagFriendModel();

                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                if (convertView == null) {
                        convertView = inflater.inflate(R.layout.list_view_row_friends_drawer, parent, false);

                        p.name_TextView = (TextView) convertView.findViewById(R.id.drawer_profile_friend_name_textView);
                        p.profile_ImageView = (CircleImageView) convertView.findViewById(R.id.drawer_profile_friend_image_imageView);
                        convertView.setTag(p);
                } else {
                        p = (ViewHolderTagFriendModel) convertView.getTag();
                }

                if(!friends.get(position).isAction) {
                        p.name_TextView.setText(friends.get(position).DisplayName);
                        if(friends.get(position).ProfilePictureURL != null && friends.get(position).ProfilePictureURL.length() > 0)
                                Picasso.with(convertView.getContext()).load(friends.get(position).ProfilePictureURL).resize(witdthHeight, witdthHeight).centerCrop().into(p.profile_ImageView);
                        //imageLoader.DisplayImage(friends.get(position).ProfilePictureURL, p.profile_ImageView, true);
                }
                else{
                        p.name_TextView.setText(friends.get(position).actionName.toString());
                }
                return convertView;
        }
}