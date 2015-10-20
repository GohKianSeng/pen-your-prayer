package com.penyourprayer.penyourprayer.UI;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.BitmapFactory;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.penyourprayer.penyourprayer.Common.FragmentBackHandlerInterface;
import com.penyourprayer.penyourprayer.Common.FriendProfileModel;
import com.penyourprayer.penyourprayer.Common.ImageProcessor;
import com.penyourprayer.penyourprayer.Common.ListViewAdapterDrawerProfileFriend;
import com.penyourprayer.penyourprayer.Common.ListViewAdapterProfileFriend;
import com.penyourprayer.penyourprayer.Database.Database;
import com.penyourprayer.penyourprayer.GoogleCloudMessaging.RegistrationIntentService;
import com.penyourprayer.penyourprayer.QuickstartPreferences;
import com.penyourprayer.penyourprayer.R;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import io.fabric.sdk.android.Fabric;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private DrawerLayout mDrawerLayout;
    private String TWITTER_KEY = "jSBnTpknelOuZX6e4Cg101oue", TWITTER_SECRET = "w5j7WPwHWwY4DSfJ82tRVZF7SBogZJ6XABptVt431uOowvwFKC";
    private boolean drawerCurrentFriendMode = true;
    public ArrayList<FriendProfileModel> friends;
    public ArrayList<FriendProfileModel> selectedFriends = new ArrayList<FriendProfileModel>();
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    public String OwnerGUID = "sdfsdf1323123";
    public String OwnerName = "Kian Seng";
    public String OwnerProfilePicture = "https://encrypted-tbn1.gstatic.com/images?q=tbn:ANd9GcTy9gsPmNSg7MdCHvmdzn7DHOwSKcPko4q0wdiCuhgiUUWCGZ4rJA";
    SharedPreferences sharedPreferences;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //android.support.v4.app.Fragment fragment = null;
        Object sss =  this.getFragmentManager();
        Fragment fragment=  this.getSupportFragmentManager().findFragmentById(R.id.fragment);
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onBackPressed(){

        Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragment);
        if (f instanceof FragmentBackHandlerInterface) {
            ((FragmentBackHandlerInterface) f).onBackPressed();
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = this.getSharedPreferences("PenYourPrayer.SharePreference", Context.MODE_PRIVATE);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        setContentView(R.layout.activity_main);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        ImageView profileImage = (ImageView)findViewById(R.id.drawer_profile_image);
        profileImage.setImageBitmap(ImageProcessor.getRoundedCornerBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.profile2)));

        ((LinearLayout)findViewById(R.id.drawer_profile_layout)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(drawerCurrentFriendMode){
                    ((ImageView)v.findViewById(R.id.drawer_profile_menu_button)).setImageResource(R.drawable.ic_drawer_menu_up);
                    drawerCurrentFriendMode = false;
                }
                else{
                    ((ImageView)v.findViewById(R.id.drawer_profile_menu_button)).setImageResource(R.drawable.ic_drawer_menu_down);
                    drawerCurrentFriendMode = true;
                }
                loadDrawerContent(drawerCurrentFriendMode);
            }
        });


        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String Topic = sharedPreferences.getString(QuickstartPreferences.BroadcastMessageTopic, "");
            }
        };


        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.BroadcastMessage));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                //Log.i(TAG, "This device is not supported.");
                //Device not supported.
                finish();
            }
            return false;
        }
        return true;
    }

    private void printHashKeyForFacebook(){
        // Add code to print out the key hash
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.penyourprayer.penyourprayer",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String key = Base64.encodeToString(md.digest(), Base64.DEFAULT);
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }

    public void loadDrawerContent(boolean drawerCurrentFriendMode){
        ArrayList<FriendProfileModel> settings = new ArrayList<FriendProfileModel>();

        FriendProfileModel set = new FriendProfileModel(FriendProfileModel.ActionName.Logout);
        settings.add(set);
        set = new FriendProfileModel(FriendProfileModel.ActionName.Settings);
        settings.add(set);


        if(drawerCurrentFriendMode){
            ListView list = (ListView)findViewById(R.id.drawer_listview);

            ListViewAdapterDrawerProfileFriend adapter = new ListViewAdapterDrawerProfileFriend(this, R.layout.list_view_row_friends_drawer, friends);
            list.setAdapter(adapter);

            list.setItemsCanFocus(false);
            list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                }
            });
        }
        else{
            ListView list = (ListView)findViewById(R.id.drawer_listview);
            ListViewAdapterDrawerProfileFriend adapter = new ListViewAdapterDrawerProfileFriend(this, R.layout.list_view_row_friends_drawer, settings);
            list.setAdapter(adapter);

            list.setItemsCanFocus(false);
            list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                }
            });
        }
    }

    public void popBackFragmentStack() {
        getSupportFragmentManager().popBackStack();
    }

    public void lockDrawer(boolean lockDrawer) {
        if(!lockDrawer){
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        }
        else{
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }

    }

    public boolean isNavigationDrawerOpened(){
        return mDrawerLayout.isDrawerOpen(Gravity.LEFT);
    }

    public void showNavigationDrawer(boolean show){
        if(!show)
            mDrawerLayout.openDrawer(Gravity.LEFT);
        else
            mDrawerLayout.closeDrawer(Gravity.LEFT);
    }

    public void replaceWithLoginFragment(){

        // Create fragment and give it an argument specifying the article it should show
        Fragment newFragment = new FragmentLogin();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);

        transaction.replace(R.id.fragment, newFragment);
        //transaction.addToBackStack(newFragment.getTag());

        transaction.commit();
    }

    public void replaceWithPrayerListFragment(){

        // Create fragment and give it an argument specifying the article it should show
        Fragment newFragment = new FragmentPrayerList();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);

        transaction.replace(R.id.fragment, newFragment);
        //transaction.addToBackStack(newFragment.getTag());

        transaction.commit();
    }

    public void replaceWithSignUpFragment(){

        // Create fragment and give it an argument specifying the article it should show
        Fragment newFragment = new FragmentSignUp();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);

        transaction.replace(R.id.fragment, newFragment);
        transaction.addToBackStack(null);

        transaction.commit();
    }

    public void replaceWithCreateNewPrayerFragment(){
        // Create fragment and give it an argument specifying the article it should show

        Fragment createNewPrayerFragment = new FragmentCreateNewPrayer();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);

        transaction.replace(R.id.fragment, createNewPrayerFragment);
        transaction.addToBackStack(null);

        transaction.commit();
    }

    public void replaceWithTagAFriend(String GUID){
        // Create fragment and give it an argument specifying the article it should show
        Fragment newFragment = FragmentTagAFriend.newInstance(GUID);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);

        transaction.replace(R.id.fragment, newFragment);
        transaction.addToBackStack(null);

        transaction.commit();
    }
}
