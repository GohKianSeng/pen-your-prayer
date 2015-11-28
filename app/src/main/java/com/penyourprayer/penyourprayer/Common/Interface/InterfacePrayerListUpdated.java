package com.penyourprayer.penyourprayer.Common.Interface;

import com.penyourprayer.penyourprayer.Common.Model.ModelOwnerPrayer;

import java.util.ArrayList;

/**
 * Created by sisgks on 14/10/2015.
 */

public interface InterfacePrayerListUpdated {
    public void onListUpdate(ArrayList<ModelOwnerPrayer> allprayers);
}