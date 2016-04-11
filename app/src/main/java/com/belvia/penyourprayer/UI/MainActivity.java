package com.belvia.penyourprayer.UI;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.belvia.penyourprayer.Common.Adapter.AdapterListViewDrawerPrayerRequest;
import com.belvia.penyourprayer.Common.Adapter.AdapterListViewDrawerProfileFriend;
import com.belvia.penyourprayer.Common.DataLoading;
import com.belvia.penyourprayer.Common.Interface.InterfaceFragmentBackHandler;
import com.belvia.penyourprayer.Common.Interface.InterfaceFragmentFriendsHandler;
import com.belvia.penyourprayer.Common.Interface.InterfaceFragmentPrayerRequestHandler;
import com.belvia.penyourprayer.Common.Model.ModelFriendProfile;
import com.belvia.penyourprayer.Common.Model.ModelPrayerAnswered;
import com.belvia.penyourprayer.Common.Model.ModelPrayerAttachement;
import com.belvia.penyourprayer.Common.Model.ModelPrayerComment;
import com.belvia.penyourprayer.Common.Model.ModelPrayerRequest;
import com.belvia.penyourprayer.Common.Model.ModelPrayerRequestAttachement;
import com.belvia.penyourprayer.Common.Model.ModelUserLogin;
import com.belvia.penyourprayer.Common.SocialLogin.Facebook;
import com.belvia.penyourprayer.Common.SocialLogin.GooglePlus;
import com.belvia.penyourprayer.Common.Utils;
import com.belvia.penyourprayer.Database.Database;
import com.belvia.penyourprayer.GoogleCloudMessaging.RegistrationIntentService;
import com.belvia.penyourprayer.QueueAction.QueueAction;
import com.belvia.penyourprayer.QuickstartPreferences;
import com.belvia.penyourprayer.R;
import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private DrawerLayout mDrawerLayout;
    private String TWITTER_KEY = "jSBnTpknelOuZX6e4Cg101oue", TWITTER_SECRET = "w5j7WPwHWwY4DSfJ82tRVZF7SBogZJ6XABptVt431uOowvwFKC";
    private boolean drawerCurrentFriendMode = true;
    public ArrayList<ModelFriendProfile> friends;
    public ArrayList<ModelPrayerRequest> prayerRequest;
    public ArrayList<ModelFriendProfile> selectedFriends = new ArrayList<ModelFriendProfile>();
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    public String OwnerID;
    public String OwnerDisplayName;
    public String OwnerProfilePictureURL;
    public SharedPreferences sharedPreferences;
    public ArrayList<ModelPrayerAttachement> attachment;
    public ArrayList<ModelPrayerRequestAttachement> pr_attachment;
    private QueueAction qa;
    private boolean UnAnsweredType = true;
    private ImageButton PrayerRequestType;
    public GoogleApiClient mGoogleApiClient;
    private AdapterListViewDrawerPrayerRequest pr_adapter;
    public ImageView userProfileImage;
    public TextView userProfileDisplayName;
    private boolean checkFbstatusNow = false;
    private Facebook fb;
    private AdapterListViewDrawerProfileFriend adapter;

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
        if (f instanceof InterfaceFragmentBackHandler) {
            ((InterfaceFragmentBackHandler) f).onBackPressed();
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = this.getSharedPreferences("PenYourPrayer.SharePreference", Context.MODE_PRIVATE);

        FacebookSdk.sdkInitialize(this.getApplicationContext());

        printHashKeyForFacebook();
        //getContact();
        qa = new QueueAction(this);

        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig), new Crashlytics());
        setContentView(R.layout.activity_main);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        userProfileImage = (ImageView)findViewById(R.id.drawer_profile_image);
        userProfileDisplayName = (TextView)findViewById(R.id.drawer_profile_name);

        PrayerRequestType = (ImageButton) findViewById(R.id.drawer_prayer_request_type);
        PrayerRequestType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UnAnsweredType = !UnAnsweredType;
                Database db = new Database(v.getContext());

                if (UnAnsweredType) {
                    pr_adapter.refreshAllItem(db.getAllUnansweredPrayerRequest());
                    PrayerRequestType.setImageResource(R.drawable.ic_actionbar_timeglass_w);
                } else {
                    pr_adapter.refreshAllItem(db.getAllAnsweredPrayerRequest());
                    PrayerRequestType.setImageResource(R.drawable.ic_actionbar_check_w);
                }
            }
        });

        findViewById(R.id.drawer_prayer_request_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceWithCreateNewPrayerRequestFragment();
            }
        });

        ((LinearLayout)findViewById(R.id.drawer_profile_layout)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerCurrentFriendMode) {
                    ((ImageView) v.findViewById(R.id.drawer_profile_menu_button)).setImageResource(R.drawable.ic_drawer_menu_up);
                    drawerCurrentFriendMode = false;
                } else {
                    ((ImageView) v.findViewById(R.id.drawer_profile_menu_button)).setImageResource(R.drawable.ic_drawer_menu_down);
                    drawerCurrentFriendMode = true;
                }
                loadLeftRightDrawerContent(drawerCurrentFriendMode);
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
        qa.StartHttpTranmissionQueue();
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

    public void loadLeftRightDrawerContent(boolean drawerCurrentFriendMode){
        ArrayList<ModelFriendProfile> settings = new ArrayList<ModelFriendProfile>();

        ModelFriendProfile set = new ModelFriendProfile(ModelFriendProfile.ActionName.Logout);
        settings.add(set);
        set = new ModelFriendProfile(ModelFriendProfile.ActionName.Settings);
        settings.add(set);


        if(drawerCurrentFriendMode){
            ListView list = (ListView)findViewById(R.id.drawer_listview);

            AdapterListViewDrawerProfileFriend adapter = new AdapterListViewDrawerProfileFriend(this, R.layout.list_view_row_friends_drawer, friends);
            list.setAdapter(adapter);

            list.setItemsCanFocus(false);
            list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            list.setOnItemClickListener(null);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragment);
                    if (f instanceof InterfaceFragmentFriendsHandler) {
                        ((InterfaceFragmentFriendsHandler) f).onFriendPressed(friends.get(position));
                    }
                }
            });
        }
        else{
            ListView list = (ListView)findViewById(R.id.drawer_listview);
            AdapterListViewDrawerProfileFriend adapter = new AdapterListViewDrawerProfileFriend(this, R.layout.list_view_row_friends_drawer, settings);
            list.setAdapter(adapter);

            list.setItemsCanFocus(false);
            list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            list.setOnItemClickListener(null);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if(((ModelFriendProfile)parent.getItemAtPosition(position)).actionName.compareTo(ModelFriendProfile.ActionName.Logout) == 0){
                        ((MainActivity)parent.getContext()).logout();
                        ((MainActivity)parent.getContext()).replaceWithLoginFragment();
                    }
                }
            });
        }


        //load right drawer
        ListView rq_list = (ListView)findViewById(R.id.drawer_right_listview);
        pr_adapter = new AdapterListViewDrawerPrayerRequest(this, R.layout.list_view_row_prayer_request_drawer, prayerRequest);
        rq_list.setAdapter(pr_adapter);

        rq_list.setItemsCanFocus(false);
        rq_list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        rq_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragment);
                if (f instanceof InterfaceFragmentPrayerRequestHandler) {
                    ((InterfaceFragmentPrayerRequestHandler) f).onPrayerRequestPressed(prayerRequest.get(position));
                }
                else {
                    replaceWithModifyPrayerRequestFragment(prayerRequest.get(position), false);
                }
            }
        });

        rq_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, final View view, final int position, long id) {

                LayoutInflater inflater = getLayoutInflater();

                PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
                popupMenu.inflate(R.menu.prayer_request_menu);

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        ModelPrayerRequest prayerRequest = (ModelPrayerRequest) pr_adapter.getItem(position);

                        if (item.toString().compareToIgnoreCase("Edit") == 0) {
                            replaceWithModifyPrayerRequestFragment(prayerRequest, false);
                        } else if (item.toString().compareToIgnoreCase("Delete") == 0) {
                            Database db = new Database(view.getContext());
                            db.DeletePrayerRequest(prayerRequest.PrayerRequestID);
                            pr_adapter.remove(prayerRequest);
                            pr_adapter.notifyDataSetChanged();
                        } else if (item.toString().compareToIgnoreCase("Answered") == 0) {
                            replaceWithModifyPrayerRequestFragment(prayerRequest, true);
                        }

                        return true;
                    }
                });

                popupMenu.show();


                return false;
            }
        });
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

    public boolean isNavigationDrawerOpened(int LeftRight){
        return mDrawerLayout.isDrawerOpen(LeftRight);
    }

    public void showNavigationDrawer(int LeftRight, boolean show){
        if(show)
            mDrawerLayout.openDrawer(LeftRight);
        else
            mDrawerLayout.closeDrawer(LeftRight);
    }

    public void refreshRightDrawer(){

        Database db = new Database(this);

        if (UnAnsweredType) {
            pr_adapter.refreshAllItem(db.getAllUnansweredPrayerRequest());
        } else {
            pr_adapter.refreshAllItem(db.getAllAnsweredPrayerRequest());
        }
    }

    public void reloadPrayerRequest() {
        pr_adapter.refreshAllItem(prayerRequest);
    }

    public void replaceWithPrayerCommentModification(ModelPrayerComment c){
        // Create fragment and give it an argument specifying the article it should show
        Fragment newFragment = FragmentPrayerCommentEdit.newInstance(c);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);

        transaction.replace(R.id.fragment, newFragment);
        transaction.addToBackStack(null);

        transaction.commit();
    }

    public void replaceWithPrayerAnsweredModification(ModelPrayerAnswered a){
        // Create fragment and give it an argument specifying the article it should show
        Fragment newFragment = FragmentPrayerAnsweredEdit.newInstance(a);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);

        transaction.replace(R.id.fragment, newFragment);
        transaction.addToBackStack(null);

        transaction.commit();
    }

    public void replaceWithPrayerAnswered(ArrayList<ModelPrayerAnswered> answer, String PrayerID){

        // Create fragment and give it an argument specifying the article it should show
        Fragment newFragment = FragmentPrayerAnswered.newInstance(answer, PrayerID);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);

        transaction.replace(R.id.fragment, newFragment);
        transaction.addToBackStack(null);

        transaction.commit();
    }

    public void replaceWithPrayerComment(ArrayList<ModelPrayerComment> comment, String PrayerID){

        // Create fragment and give it an argument specifying the article it should show
        Fragment newFragment = FragmentPrayerComment.newInstance(comment, PrayerID);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);

        transaction.replace(R.id.fragment, newFragment);
        transaction.addToBackStack(null);

        transaction.commit();
    }

    public void replaceWithLoginFragment(){

        // Create fragment and give it an argument specifying the article it should show
        Fragment newFragment = new FragmentLogin();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);

        transaction.replace(R.id.fragment, newFragment);

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
        Fragment newFragment = new FragmentSignupStep1();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);

        transaction.replace(R.id.fragment, newFragment);
        transaction.addToBackStack("fragment_login");

        transaction.commit();
    }

    public void replaceWithSignUpStep2Fragment(String fullname){

        // Create fragment and give it an argument specifying the article it should show
        Fragment newFragment = FragmentSignupStep2.newInstance(fullname);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);

        transaction.replace(R.id.fragment, newFragment);
        transaction.addToBackStack(null);

        transaction.commit();
    }

    public void replaceWithSignUpStep3Fragment(String fullname, String email){

        // Create fragment and give it an argument specifying the article it should show
        Fragment newFragment = FragmentSignupStep3.newInstance(fullname, email);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);

        transaction.replace(R.id.fragment, newFragment);
        transaction.addToBackStack(null);

        transaction.commit();
    }

    public void replaceWithSignUpStep4Fragment(String fullname, String email){

        // Create fragment and give it an argument specifying the article it should show
        Fragment newFragment = FragmentSignupStep4.newInstance(fullname, email);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);

        transaction.replace(R.id.fragment, newFragment);
        transaction.addToBackStack(null);

        transaction.commit();
    }

    public void replaceWithResetPasswordFragment(){

        // Create fragment and give it an argument specifying the article it should show
        Fragment newFragment = new FragmentResetPassword();

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

    public void replaceWithCreateNewPrayerRequestFragment(){
        // Create fragment and give it an argument specifying the article it should show

        Fragment createNewPrayerRequestFragment = new FragmentCreateNewPrayerRequest();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);

        transaction.replace(R.id.fragment, createNewPrayerRequestFragment);
        transaction.addToBackStack(null);

        transaction.commit();
    }

    public void replaceWithModifyPrayerRequestFragment(ModelPrayerRequest pr, boolean answered){
        // Create fragment and give it an argument specifying the article it should show

        Fragment createNewPrayerRequestFragment = FragmentCreateNewPrayerRequest.newInstance(pr, true, answered);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);

        transaction.replace(R.id.fragment, createNewPrayerRequestFragment);
        transaction.addToBackStack(null);

        transaction.commit();
    }

    public void replaceWithAttachmentViewImage(int page, ArrayList<ModelPrayerAttachement> att, boolean allowModification){
        // Create fragment and give it an argument specifying the article it should show
        Fragment newFragment = FragmentAttachmentViewImage.newInstance(page, att, allowModification);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);

        transaction.replace(R.id.fragment, newFragment);
        transaction.addToBackStack(null);

        transaction.commit();
    }

    public void replaceWithPrayerRequestAttachmentViewImage(int page, ArrayList<ModelPrayerRequestAttachement> att, boolean allowModification){
        // Create fragment and give it an argument specifying the article it should show
        Fragment newFragment = FragmentPrayerRequestAttachmentViewImage.newInstance(page, att, allowModification);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);

        transaction.replace(R.id.fragment, newFragment);
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

    public void clearAllFragmentStackToLoginFragment(){
        getSupportFragmentManager().popBackStack("fragment_login", FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    public void getContact(){

        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                if (Integer.parseInt(cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phone = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        phone.toString();
                    }
                    pCur.close();
                }

                name.toString();

                Cursor emailCur = cr.query(
                        ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                        new String[]{id}, null);
                while (emailCur.moveToNext()) {
                    // This would allow you get several email addresses
                    // if the email addresses were stored in an array
                    String email = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                    String emailType = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));

                    email.toString();
                    emailType.toString();
                }
                emailCur.close();
            }
        }
    }

    public void isSocialLoginValid(){
        String loginType = this.sharedPreferences.getString(QuickstartPreferences.OwnerLoginType, "");
        if(loginType.compareToIgnoreCase(ModelUserLogin.LoginType.GooglePlus.toString()) == 0){
            GooglePlus gp = new GooglePlus(this);
            gp.checkLoginStatus();
        }
        else if(loginType.compareToIgnoreCase(ModelUserLogin.LoginType.Facebook.toString()) == 0){
            fb = new Facebook(this);
            fb.checkLoginStatus();

        }
    }

    public void loadInitialLaunchData(){
        final MainActivity ma = this;
        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                DataLoading dl = new DataLoading(ma);
                dl.fetchLatestDataFromServer();
                return "";
            }

            @Override
            protected void onPostExecute(String action) {
                Database db = new Database(ma);
                int witdthHeight = Utils.dpToPx(ma, QuickstartPreferences.thumbnailDPsize);

                Picasso.with(ma).load(ma.OwnerProfilePictureURL).resize(witdthHeight, witdthHeight).centerCrop().into(userProfileImage);
                userProfileDisplayName.setText(ma.OwnerDisplayName);

                ma.friends = db.getAllFriends(ma.OwnerID);
                ma.prayerRequest = db.getAllUnansweredPrayerRequest();
                ma.loadLeftRightDrawerContent(true);
                ma.replaceWithPrayerListFragment();
            }

        };
        task.execute();
    }

    public void logout(){
        this.sharedPreferences.edit().remove(QuickstartPreferences.OwnerID).apply();
        this.sharedPreferences.edit().remove(QuickstartPreferences.OwnerDisplayName).apply();
        this.sharedPreferences.edit().remove(QuickstartPreferences.OwnerProfilePictureURL).apply();
        this.sharedPreferences.edit().remove(QuickstartPreferences.OwnerHMACKey).apply();
        String loginType = this.sharedPreferences.getString(QuickstartPreferences.OwnerLoginType, "");
        this.sharedPreferences.edit().remove(QuickstartPreferences.OwnerUserName).apply();
        this.sharedPreferences.edit().remove(QuickstartPreferences.OwnerLoginType).apply();

        if(loginType.compareToIgnoreCase(ModelUserLogin.LoginType.GooglePlus.toString()) ==0){
            if(mGoogleApiClient.isConnected()){
                Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                mGoogleApiClient.disconnect();
            }
        }
        else if(loginType.compareToIgnoreCase(ModelUserLogin.LoginType.Facebook.toString()) ==0){
            LoginManager.getInstance().logOut();
        }

        Database db = new Database(this);
        db.ClearData();
    }
}
