package com.belvia.penyourprayer.Common.Interface;

import com.belvia.penyourprayer.Common.Model.ModelPrayerAnswered;

import java.util.ArrayList;

/**
 * Created by sisgks on 14/10/2015.
 */

public interface InterfacePrayerAnsweredListViewUpdated {
    public void onAnswerUpdate(ArrayList<ModelPrayerAnswered> answered);
}