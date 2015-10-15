package com.penyourprayer.penyourprayer.UI;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.BitmapFactory;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.penyourprayer.penyourprayer.Common.FragmentBackHandlerInterface;
import com.penyourprayer.penyourprayer.Common.ImageProcessor;
import com.penyourprayer.penyourprayer.R;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import io.fabric.sdk.android.Fabric;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private String TWITTER_KEY = "jSBnTpknelOuZX6e4Cg101oue", TWITTER_SECRET = "w5j7WPwHWwY4DSfJ82tRVZF7SBogZJ6XABptVt431uOowvwFKC";
    private boolean drawerCurrentFriendMode = true;
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

    public void lockDrawer(boolean lockDrawer) {
        if(!lockDrawer){
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        }
        else{
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

            }
        });


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

    public void popBackFragmentStack(){
        getSupportFragmentManager().popBackStack();
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

    public void replaceWithTagAFriend(){
        // Create fragment and give it an argument specifying the article it should show
        Fragment newFragment = new FragmentTagAFriend();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);

        transaction.replace(R.id.fragment, newFragment);
        transaction.addToBackStack(null);

        transaction.commit();
    }
}
