package com.penyourprayer.penyourprayer.Common.Interface;

import com.penyourprayer.penyourprayer.Common.Model.ModelPrayerComment;

import java.util.ArrayList;

/**
 * Created by sisgks on 14/10/2015.
 */

public interface InterfacePrayerCommentListViewUpdated {
    public void onCommentUpdate(ArrayList<ModelPrayerComment> comment);
}