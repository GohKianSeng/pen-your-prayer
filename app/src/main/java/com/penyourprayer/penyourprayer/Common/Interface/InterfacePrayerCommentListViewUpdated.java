package com.penyourprayer.penyourprayer.Common.Interface;

import com.penyourprayer.penyourprayer.Common.Model.ModelOwnerPrayer;
import com.penyourprayer.penyourprayer.Common.Model.ModelPayerComment;

import java.util.ArrayList;

/**
 * Created by sisgks on 14/10/2015.
 */

public interface InterfacePrayerCommentListViewUpdated {
    public void onCommentUpdate(ArrayList<ModelPayerComment> comment);
}