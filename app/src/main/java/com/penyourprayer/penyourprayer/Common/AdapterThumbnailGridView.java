package com.penyourprayer.penyourprayer.Common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.penyourprayer.penyourprayer.R;

import java.io.File;
import java.util.ArrayList;

public class AdapterThumbnailGridView extends BaseAdapter{
    private Context mContext;
    ArrayList<ModelPrayerAttachement> attachment;
    private ImageLoader imageLoader;
    public AdapterThumbnailGridView(Context c, ArrayList<ModelPrayerAttachement> att) {
        mContext = c;
        attachment = att;
        imageLoader=new ImageLoader(c.getApplicationContext());
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return attachment.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewHolderAttachmentModel p = new ViewHolderAttachmentModel();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.card_ui_image, null);
            p.attachment_imageButton = (ImageButton)convertView.findViewById(R.id.thumbnail_imageButton);
            convertView.setTag(p);
        } else {
            p = (ViewHolderAttachmentModel)convertView.getTag();
        }


        if(attachment.get(position).URLPath != null && !attachment.get(position).URLPath.startsWith("?")) {
            File file = new File(attachment.get(position).URLPath);
            if(file.exists()) {
                imageLoader.DisplayImage(attachment.get(position).URLPath, p.attachment_imageButton, false);
            }
            else{
                // get the image from url
                // local file not exists might need to grab from internet
                p.attachment_imageButton.setImageResource(R.drawable.profile2);
            }
        }
        else{
            // get the image from url
            p.attachment_imageButton.setImageResource(R.drawable.profile2);
        }

        return convertView;
    }

    public void updateAttachment(ArrayList<ModelPrayerAttachement> att){
        attachment = att;
        this.notifyDataSetChanged();
    }
}