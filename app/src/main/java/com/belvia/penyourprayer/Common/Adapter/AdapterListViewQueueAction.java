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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.belvia.penyourprayer.Common.Model.ModelPrayerComment;
import com.belvia.penyourprayer.Common.Model.ModelQueueAction;
import com.belvia.penyourprayer.Common.Model.ViewHolder.ViewHolderPrayerCommentModel;
import com.belvia.penyourprayer.Common.Model.ViewHolder.ViewHolderQueueActionModel;
import com.belvia.penyourprayer.Common.Utils;
import com.belvia.penyourprayer.Database.Database;
import com.belvia.penyourprayer.QuickstartPreferences;
import com.belvia.penyourprayer.R;
import com.belvia.penyourprayer.UI.MainActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterListViewQueueAction extends ArrayAdapter {
        private MainActivity mainactivity;
        private ArrayList<ModelQueueAction> queueAction;

        public AdapterListViewQueueAction(Context context, int resourcesID, ArrayList<ModelQueueAction> c) {
                super(context, resourcesID, c);

                // TODO Auto-generated constructor stub
                this.mainactivity = (MainActivity)context;
                queueAction = c;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

                ViewHolderQueueActionModel p = new ViewHolderQueueActionModel();

                LayoutInflater inflater = ((Activity)mainactivity).getLayoutInflater();
                if(position >= queueAction.size())
                        return convertView;
                if(convertView == null || ((ViewHolderQueueActionModel)convertView.getTag()).QueueActionID.compareToIgnoreCase(queueAction.get(position).IfExecutedGUID) != 0) {
                        convertView = inflater.inflate(R.layout.list_view_row_queue_action, parent, false);

                        p.progressBarLayout = (LinearLayout) convertView.findViewById(R.id.queue_action_progressbar_layout);
                        p.createdWhen = (TextView) convertView.findViewById(R.id.queue_action_createdwhen);
                        p.GUID = (TextView) convertView.findViewById(R.id.queue_action_guid);
                        p.item_type = (TextView) convertView.findViewById(R.id.queue_action_item_type);
                        p.sync_type = (TextView) convertView.findViewById(R.id.queue_action_sync_type);
                        p.progressBar = (ProgressBar) convertView.findViewById(R.id.queue_action_progressbar);
                        p.QueueActionID = queueAction.get(position).IfExecutedGUID;

                        convertView.setTag(p);
                }
                else{
                        p = (ViewHolderQueueActionModel)convertView.getTag();
                }


                /****************************************************************
                *
                * Set Data
                *
                * ****************************************************************/

                p.GUID.setText("ID: " + queueAction.get(position).IfExecutedGUID);
                p.sync_type.setText(queueAction.get(position).Action.toString());
                p.item_type.setText(queueAction.get(position).Item.toString());
                p.createdWhen.setText(queueAction.get(position).formattedCreatedWhen());
                if(position == 0){
                        p.progressBarLayout.setVisibility(View.VISIBLE);
                }
                return convertView;
        }

        public void updateQueueActionList(ArrayList<ModelQueueAction> c){
                this.queueAction = c;
                this.clear();
                this.addAll(c);
                this.notifyDataSetChanged();
        }
}