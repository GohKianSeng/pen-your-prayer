package com.penyourprayer.penyourprayer.Common.Interface;

import com.penyourprayer.penyourprayer.Common.Model.ModelPayerAnswered;
import com.penyourprayer.penyourprayer.Common.Model.ModelPayerComment;

import java.util.ArrayList;

/**
 * Created by sisgks on 14/10/2015.
 */

public interface InterfacePrayerAnsweredListViewUpdated {
    public void onCommentUpdate(ArrayList<ModelPayerAnswered> answered);
}