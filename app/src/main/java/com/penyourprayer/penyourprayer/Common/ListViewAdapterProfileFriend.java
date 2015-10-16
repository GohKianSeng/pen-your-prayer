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

public class ListViewAdapterProfileFriend extends ArrayAdapter {
        public ArrayList<FriendProfileModel> friends = null;
        public Context context;
        private ImageLoader imageLoader;

        public ListViewAdapterProfileFriend(Context context, int resourcesID, ArrayList<FriendProfileModel> resource) {
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
                        convertView = inflater.inflate(R.layout.list_view_row_tag_friend, parent, false);

                        p.name_TextView = (TextView) convertView.findViewById(R.id.profile_name_textView);
                        p.selected_CheckBox= (CheckBox) convertView.findViewById(R.id.profile_checkBox);
                        p.profile_ImageView = (ImageView)convertView.findViewById(R.id.profile_img_imageView);

                        convertView.setTag(p);
                }
                else{
                        p = (TagFriendModelViewHolder) convertView.getTag();
                }


                p.name_TextView.setText(friends.get(position).name);
                p.selected_CheckBox.setChecked(friends.get(position).selected);
                imageLoader.DisplayImage(friends.get(position).img_url, p.profile_ImageView);

                p.selected_CheckBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((CheckBox) v).setSelected(((CheckBox) v).isChecked());

                        friends.get(position).selected = ((CheckBox) v).isChecked();

                    }
                });

                return convertView;
        }

        @Override
        public Filter getFilter() {
                return new Filter() {
                        @SuppressWarnings("unchecked")
                        @Override
                        protected void publishResults(CharSequence constraint, FilterResults results) {
                                //Log.d(Constants.TAG, "**** PUBLISHING RESULTS for: " + constraint);
                                friends = (ArrayList<FriendProfileModel>) results.values;
                                notifyDataSetChanged();
                        }

                        @Override
                        protected FilterResults performFiltering(CharSequence constraint) {
                                //Log.d(Constants.TAG, "**** PERFORM FILTERING for: " + constraint);
                                ArrayList<FriendProfileModel> filteredResults = getFilteredResults(constraint);

                                FilterResults results = new FilterResults();
                                results.values = filteredResults;

                                return results;
                        }
                };
        }

        private ArrayList<FriendProfileModel> getFilteredResults(CharSequence constraint) {

                ArrayList<FriendProfileModel> test = new ArrayList<FriendProfileModel>();
                for(int x=0; x<10; x++) {
                        //FriendProfileModel f = new FriendProfileModel("Siewlin No" + String.valueOf(x), "http://images.pajezy.com/notes/profile.png", false);
                        //test.add(f);
                }
                return test;
        }
}