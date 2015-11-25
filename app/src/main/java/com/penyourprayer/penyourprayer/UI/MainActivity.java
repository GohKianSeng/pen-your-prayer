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
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.penyourprayer.penyourprayer.Common.Interface.FragmentBackHandlerInterface;
import com.penyourprayer.penyourprayer.Common.FriendProfileModel;
import com.penyourprayer.penyourprayer.Common.ImageProcessor;
import com.penyourprayer.penyourprayer.Common.Interface.InterfacePrayerListUpdated;
import com.penyourprayer.penyourprayer.Common.ListViewAdapterDrawerProfileFriend;
import com.penyourprayer.penyourprayer.Common.ModelPrayerAttachement;
import com.penyourprayer.penyourprayer.Common.OwnerPrayerModel;
import com.penyourprayer.penyourprayer.Database.Database;
import com.penyourprayer.penyourprayer.Database.QueueAction;
import com.penyourprayer.penyourprayer.GoogleCloudMessaging.RegistrationIntentService;
import com.penyourprayer.penyourprayer.QuickstartPreferences;
import com.penyourprayer.penyourprayer.R;

import com.penyourprayer.penyourprayer.WebAPI.InterfaceUploadFile;
import com.penyourprayer.penyourprayer.WebAPI.Model.SimpleJsonResponse;
import com.penyourprayer.penyourprayer.WebAPI.PrayerInterface;
import com.penyourprayer.penyourprayer.WebAPI.httpClient;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import io.fabric.sdk.android.Fabric;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;
import retrofit.mime.TypedFile;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private DrawerLayout mDrawerLayout;
    private String TWITTER_KEY = "jSBnTpknelOuZX6e4Cg101oue", TWITTER_SECRET = "w5j7WPwHWwY4DSfJ82tRVZF7SBogZJ6XABptVt431uOowvwFKC";
    private boolean drawerCurrentFriendMode = true;
    public ArrayList<FriendProfileModel> friends;
    public ArrayList<FriendProfileModel> selectedFriends = new ArrayList<FriendProfileModel>();
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    public String OwnerID;
    public String OwnerDisplayName;
    public String OwnerProfilePictureURL;
    public SharedPreferences sharedPreferences;
    public ArrayList<ModelPrayerAttachement> attachment;
    private boolean paused = false;
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

        InitialiseHttpTranmissionQueue();

        sharedPreferences = this.getSharedPreferences("PenYourPrayer.SharePreference", Context.MODE_PRIVATE);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        setContentView(R.layout.activity_main);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        ImageView profileImage = (ImageView)findViewById(R.id.drawer_profile_image);
        profileImage.setImageBitmap(ImageProcessor.getRoundedBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.profile2)));

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
        StartHttpTranmissionQueue();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.BroadcastMessage));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        paused = true;
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

    private AsyncTask<String, Void, String> tQueue;

    private void InitialiseHttpTranmissionQueue(){
        tQueue = new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {

                Gson gson = new GsonBuilder().setDateFormat(QuickstartPreferences.DefaultTimeFormat).create();
                RestAdapter adapter = new RestAdapter.Builder()
                        .setConverter(new GsonConverter(gson))
                        .setEndpoint(QuickstartPreferences.api_server)
                        .setClient(new OkClient(new httpClient(params[0], params[1], params[2])))
                        .build();

                while(true){
                    if(paused)
                        break;
                    ProcessMessageQueue(adapter);
                    SystemClock.sleep(2000);
                }
                return "";
            }

            @Override
            protected void onPostExecute(String msg) {

            }

        };
    }

    public void StartHttpTranmissionQueue(){
        paused = false;
        tQueue = null;
        if(tQueue == null) {
            InitialiseHttpTranmissionQueue();
        }
        if(tQueue.getStatus() != AsyncTask.Status.RUNNING)
            tQueue.execute(this.sharedPreferences.getString(QuickstartPreferences.OwnerLoginType, ""),
                           this.sharedPreferences.getString(QuickstartPreferences.OwnerUserName, ""),
                           this.sharedPreferences.getString(QuickstartPreferences.OwnerHMACKey, ""));
    }

    private void ProcessMessageQueue(RestAdapter adapter){
        try {
            Database db = new Database(this);
            ArrayList<QueueAction> queue = db.getAllQueueItems();
            for (int x = 0; x < queue.size(); x++) {
                QueueAction p = queue.get(x);

                if (p.Item == QueueAction.ItemType.Prayer && p.Action == QueueAction.ActionType.Insert) {
                    submitNewPrayer(db, adapter, p.ItemID, p.ID, p.IfExecutedGUID);
                }
            }
            db.close();
        }
        catch(Exception e){
            String sdf = e.toString();
            sdf.toString();
        }
    }

    private void submitNewPrayer(Database db, RestAdapter adapter, String PrayerID, int QueueID, String IfExecutedGUID){

        OwnerPrayerModel p = db.GetPrayer(PrayerID);
        if(p == null)
            return;
        p.selectedFriends = db.getSelectedTagFriend(p.PrayerID);
        p.attachments = db.getAllOwnerPrayerAttachment(p.PrayerID);
        try {
                for(int x=0; x<p.attachments.size(); x++) {
                    ModelPrayerAttachement att = p.attachments.get(x);
                    SimpleJsonResponse response = uploadPrayerImage(att, adapter);
                    if (response.StatusCode == 202) {
                        //att.FileName = response.Description;
                        //p.attachments.add(att);
                    }
                }


            PrayerInterface prayerInterface = adapter.create(PrayerInterface.class);
            p.IfExecutedGUID = UUID.randomUUID().toString();
            SimpleJsonResponse response = prayerInterface.AddNewPrayer(p);
            if (response.StatusCode == 200) {

                db.deleteQueue(QueueID);
            }
            else if (response.StatusCode == 201) {
                long newPrayerID = Long.parseLong(response.Description);
                db.updateOwnerPrayerSent(p.PrayerID, newPrayerID);

                Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragment);
                if (f instanceof InterfacePrayerListUpdated) {
                    ((InterfacePrayerListUpdated) f).onListUpdate(db.getAllOwnerPrayer(OwnerID));
                }

                db.deleteQueue(QueueID);
            }
        } catch (Exception e) {
            String sdf = e.getMessage();
        }
    }

    private SimpleJsonResponse uploadPrayerImage(ModelPrayerAttachement att, RestAdapter tadapter){
        TypedFile attachmentImg = new TypedFile("multipart/form-data", new File(att.OriginalFilePath));
        InterfaceUploadFile interfaceUploadFile = tadapter.create(InterfaceUploadFile.class);
        SimpleJsonResponse json = interfaceUploadFile.CheckImageUploaded(att.GUID, att.FileName);
        if(json.StatusCode == 202 && json.Description.toUpperCase() == "NOTEXISTS")
            json = interfaceUploadFile.AddPrayerImage(att.GUID, attachmentImg);
        return json;
    }
}
