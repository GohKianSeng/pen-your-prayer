package com.penyourprayer.penyourprayer.Common.Adapter;

/**
 * Created by sisgks on 06/10/2015.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.penyourprayer.penyourprayer.Common.Model.ModelPrayerRequest;
import com.penyourprayer.penyourprayer.Common.Model.ModelPrayerRequestAttachement;
import com.penyourprayer.penyourprayer.Common.Model.ViewHolder.ViewHolderPrayerRequestModel;
import com.penyourprayer.penyourprayer.Common.Utils;
import com.penyourprayer.penyourprayer.QuickstartPreferences;
import com.penyourprayer.penyourprayer.R;
import com.penyourprayer.penyourprayer.UI.MainActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterListViewDrawerPrayerRequest extends ArrayAdapter {
        public ArrayList<ModelPrayerRequest> prayerRequest = null;
        private MainActivity mainactivity;
        private int witdthHeight;
        public AdapterListViewDrawerPrayerRequest(Context context, int resourcesID, ArrayList<ModelPrayerRequest> resource) {
                super(context, resourcesID, resource);
                // TODO Auto-generated constructor stub

                this.mainactivity = (MainActivity)context;
                this.prayerRequest = resource;
                witdthHeight = Utils.dpToPx(mainactivity, QuickstartPreferences.thumbnailDPsize);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

                ViewHolderPrayerRequestModel p = new ViewHolderPrayerRequestModel();

                LayoutInflater inflater = mainactivity.getLayoutInflater();
                if (convertView == null || ((ViewHolderPrayerRequestModel) convertView.getTag()).PrayerRequestID.compareToIgnoreCase(prayerRequest.get(position).PrayerRequestID) != 0) {
                        convertView = inflater.inflate(R.layout.list_view_row_prayer_request_drawer, parent, false);

                        p.PrayerRequestID = prayerRequest.get(position).PrayerRequestID;
                        p.subject_TextView = (TextView) convertView.findViewById(R.id.drawer_prayer_request_subject_textView);
                        p.createdwhen_TextView = (TextView) convertView.findViewById(R.id.drawer_prayer_request_createdwhen);
                        p.answered_TextView = (TextView) convertView.findViewById(R.id.drawer_prayer_request_answered);
                        if(prayerRequest.get(position).attachments != null && prayerRequest.get(position).attachments.size() > 0)
                                p.containAttachment = true;

                        p.image1 = (ImageView) convertView.findViewById(R.id.drawer_prayer_request_imageButton1);
                        p.image2 = (ImageView) convertView.findViewById(R.id.drawer_prayer_request_imageButton2);
                        p.image3 = (ImageView) convertView.findViewById(R.id.drawer_prayer_request_imageButton3);
                        p.image4 = (ImageView) convertView.findViewById(R.id.drawer_prayer_request_imageButton4);
                        p.image5 = (ImageView) convertView.findViewById(R.id.drawer_prayer_request_imageButton5);

                        convertView.setTag(p);
                } else {
                        p = (ViewHolderPrayerRequestModel) convertView.getTag();
                }

                if(prayerRequest.get(position).CreatedWhen.compareTo(prayerRequest.get(position).TouchedWhen) == -1)
                        p.createdwhen_TextView.setText(prayerRequest.get(position).formattedTouchedWhen());
                else
                        p.createdwhen_TextView.setText(prayerRequest.get(position).formattedCreatedWhen());

                p.subject_TextView.setText(prayerRequest.get(position).Subject);

                if(prayerRequest.get(position).Answered) {
                        p.answered_TextView.setText("Answered");
                        p.createdwhen_TextView.setText(prayerRequest.get(position).formattedAnsweredWhen());
                }
                else
                        p.answered_TextView.setText("");


                for(int x=0; x<prayerRequest.get(position).attachments.size(); x++){

                        if(x==0) {
                                LoadImage(prayerRequest.get(position).attachments, x, p.image1, x);
                        }
                        if(x==1) {
                                LoadImage(prayerRequest.get(position).attachments, x, p.image2, x);
                        }
                        if(x==2) {
                                LoadImage(prayerRequest.get(position).attachments, x, p.image3, x);
                        }
                        if(x==3) {
                                LoadImage(prayerRequest.get(position).attachments, x, p.image4, x);
                        }
                        if(x==4) {
                                LoadImage(prayerRequest.get(position).attachments, x, p.image5, x);
                        }
                }


                return convertView;
        }

        private void LoadImage(final ArrayList<ModelPrayerRequestAttachement> att, final int position, final ImageView imgbutton, final int imagePosition){
                Picasso.with(mainactivity).load(att.get(position).getAvailableURI(mainactivity)).resize(witdthHeight, witdthHeight).centerCrop().into(imgbutton);
        }

        public void refreshAllItem(ArrayList<ModelPrayerRequest> resource){
                this.prayerRequest = resource;
                this.clear();
                this.addAll(resource);
                this.notifyDataSetChanged();
        }
}