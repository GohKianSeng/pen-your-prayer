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
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;
import com.belvia.penyourprayer.Common.ImageLoad.ImageLoader;
import com.belvia.penyourprayer.Common.Model.ModelFriendProfile;
import com.belvia.penyourprayer.Common.Model.ViewHolder.ViewHolderTagFriendModel;
import com.belvia.penyourprayer.R;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterListViewProfileFriend extends ArrayAdapter {
        public ArrayList<ModelFriendProfile> friends = null;
        public Context context;
        private ImageLoader imageLoader;

        public AdapterListViewProfileFriend(Context context, int resourcesID, ArrayList<ModelFriendProfile> resource) {
                super(context, resourcesID, resource);
                // TODO Auto-generated constructor stub
                this.context = context;
                this.friends = resource;
                imageLoader=new ImageLoader(context.getApplicationContext());
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

                ViewHolderTagFriendModel p = new ViewHolderTagFriendModel();

                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                if (convertView == null) {
                        convertView = inflater.inflate(R.layout.list_view_row_tag_friend, parent, false);

                        p.name_TextView = (TextView) convertView.findViewById(R.id.profile_name_textView);
                        p.selected_CheckBox= (CheckBox) convertView.findViewById(R.id.profile_checkBox);
                        p.profile_ImageView = (CircleImageView)convertView.findViewById(R.id.profile_img_imageView);

                        convertView.setTag(p);
                }
                else{
                        p = (ViewHolderTagFriendModel) convertView.getTag();
                }


                p.name_TextView.setText(friends.get(position).DisplayName);
                p.selected_CheckBox.setChecked(friends.get(position).selected);
                imageLoader.DisplayImage(friends.get(position).ProfilePictureURL, p.profile_ImageView, true);

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
                                friends = (ArrayList<ModelFriendProfile>) results.values;
                                notifyDataSetChanged();
                        }

                        @Override
                        protected FilterResults performFiltering(CharSequence constraint) {
                                //Log.d(Constants.TAG, "**** PERFORM FILTERING for: " + constraint);
                                ArrayList<ModelFriendProfile> filteredResults = getFilteredResults(constraint);

                                FilterResults results = new FilterResults();
                                results.values = filteredResults;

                                return results;
                        }
                };
        }

        private ArrayList<ModelFriendProfile> getFilteredResults(CharSequence constraint) {

                ArrayList<ModelFriendProfile> test = new ArrayList<ModelFriendProfile>();
                for(int x=0; x<10; x++) {
                        //ModelFriendProfile f = new ModelFriendProfile("Siewlin No" + String.valueOf(x), "http://images.pajezy.com/notes/profile.png", false);
                        //test.add(f);
                }
                return test;
        }
}