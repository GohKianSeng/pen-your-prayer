package com.belvia.penyourprayer.QueueAction;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Build;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;

import com.belvia.penyourprayer.Common.Interface.InterfacePrayerAnsweredEditUpdated;
import com.belvia.penyourprayer.Common.Interface.InterfacePrayerAnsweredListViewUpdated;
import com.belvia.penyourprayer.Common.Interface.InterfacePrayerCommentEditUpdated;
import com.belvia.penyourprayer.Common.Interface.InterfacePrayerCommentListViewUpdated;
import com.belvia.penyourprayer.Common.Interface.InterfacePrayerListUpdated;
import com.belvia.penyourprayer.Common.Model.ModelPrayer;
import com.belvia.penyourprayer.Common.Model.ModelPrayerAnswered;
import com.belvia.penyourprayer.Common.Model.ModelPrayerAttachement;
import com.belvia.penyourprayer.Common.Model.ModelPrayerComment;
import com.belvia.penyourprayer.Common.Model.ModelPrayerCommentReply;
import com.belvia.penyourprayer.Common.Model.ModelPrayerRequest;
import com.belvia.penyourprayer.Common.Model.ModelPrayerRequestAttachement;
import com.belvia.penyourprayer.Common.Model.ModelQueueAction;
import com.belvia.penyourprayer.Common.Utils;
import com.belvia.penyourprayer.Database.Database;
import com.belvia.penyourprayer.QuickstartPreferences;
import com.belvia.penyourprayer.R;
import com.belvia.penyourprayer.UI.FragmentPrayerCommentReply;
import com.belvia.penyourprayer.UI.FragmentQueueAction;
import com.belvia.penyourprayer.UI.MainActivity;
import com.belvia.penyourprayer.WebAPI.InterfaceUploadFile;
import com.belvia.penyourprayer.WebAPI.Model.SimpleJsonResponse;
import com.belvia.penyourprayer.WebAPI.PrayerInterface;
import com.belvia.penyourprayer.WebAPI.httpClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;
import retrofit.mime.TypedFile;

/**
 * Created by sisgks on 27/11/2015.
 */
public class QueueAction extends AsyncTask<String, Void, String> {
    private final int notificationID = 90909;
    private boolean notificationShown = false;
    public boolean paused = false;
    MainActivity mainActivity;
    public QueueAction(MainActivity ma){
        super();
        mainActivity = ma;
    }

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

    public void StartHttpTranmissionQueue(){
        paused = false;

        if(mainActivity.sharedPreferences.getString(QuickstartPreferences.OwnerLoginType, "").length() <=0 ||
           mainActivity.sharedPreferences.getString(QuickstartPreferences.OwnerUserName, "").length() <=0 ||
           mainActivity.sharedPreferences.getString(QuickstartPreferences.OwnerHMACKey, "").length() <=0)
            return ;

        if(this.getStatus() != AsyncTask.Status.RUNNING) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                this.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mainActivity.sharedPreferences.getString(QuickstartPreferences.OwnerLoginType, ""),
                        mainActivity.sharedPreferences.getString(QuickstartPreferences.OwnerUserName, ""),
                        mainActivity.sharedPreferences.getString(QuickstartPreferences.OwnerHMACKey, ""));
            else {
                this.execute(mainActivity.sharedPreferences.getString(QuickstartPreferences.OwnerLoginType, ""),
                        mainActivity.sharedPreferences.getString(QuickstartPreferences.OwnerUserName, ""),
                        mainActivity.sharedPreferences.getString(QuickstartPreferences.OwnerHMACKey, ""));
            }
        }
    }

    public void closeSyncProcessNotification(){
        if(notificationShown) {
            mainActivity.mNotificationManager.cancel(notificationID);
            notificationShown = false;
        }
    }

    public void showSyncProcessNotification(){
        NotificationCompat.Builder mBuilder =   new NotificationCompat.Builder(mainActivity)
                .setSmallIcon(R.drawable.uploadanimation) // notification icon
                .setContentTitle("Notification!") // title for notification
                .setContentText("Hello word") // message for notification
                .setColor(0xff800080)
                .setOngoing(true)
                .setAutoCancel(false); // clear notification after click
        //Intent intent = new Intent(mainActivity, MainActivity.class);
        //PendingIntent pi = PendingIntent.getActivity(mainActivity,0,intent, PendingIntent.FLAG_ONE_SHOT);
        //mBuilder.setContentIntent(pi);


        Intent resultIntent = new Intent(mainActivity, MainActivity.class);
        resultIntent.setAction(Intent.ACTION_MAIN);
        resultIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resultIntent.putExtra("uploadingNotificationClicked", true);
        resultIntent.addFlags(Intent.FILL_IN_ACTION);

        PendingIntent pendingIntent = PendingIntent.getActivity(mainActivity, 0, resultIntent, 0);
        mBuilder.setContentIntent(pendingIntent);

        notificationShown = true;
        mainActivity.mNotificationManager.notify(notificationID, mBuilder.build());
    }

    private void ProcessMessageQueue(RestAdapter adapter){
        try {
            Database db = new Database(mainActivity);
            ArrayList<ModelQueueAction> queue = db.getAllQueueItems();
            if(queue.size() > 0){
                showSyncProcessNotification();
            }
            for (int x = 0; x < queue.size(); x++) {
                ModelQueueAction p = queue.get(x);

                if (p.Item == ModelQueueAction.ItemType.PrayerRequest && p.Action == ModelQueueAction.ActionType.Insert) {
                    ModelPrayerRequest t = (ModelPrayerRequest) Utils.deserialize(p.ItemID);
                    submitNewPrayerRequest(db, adapter, t, p.ID, p.IfExecutedGUID);
                }
                else if(p.Item == ModelQueueAction.ItemType.PrayerRequest && p.Action == ModelQueueAction.ActionType.Delete){
                    submitDeletePrayerRequest(db, adapter, p.ItemID, p.ID, p.IfExecutedGUID);
                }
                if (p.Item == ModelQueueAction.ItemType.PrayerRequest && p.Action == ModelQueueAction.ActionType.Update) {
                    ModelPrayerRequest t = (ModelPrayerRequest) Utils.deserialize(p.ItemID);
                    submitUpdatePrayerRequest(db, adapter, t, p.ID, p.IfExecutedGUID);
                }
                else if (p.Item == ModelQueueAction.ItemType.Prayer && p.Action == ModelQueueAction.ActionType.Insert) {
                    ModelPrayer t = (ModelPrayer) Utils.deserialize(p.ItemID);
                    submitNewPrayer(db, adapter, t, p.ID, p.IfExecutedGUID);
                }
                else if (p.Item == ModelQueueAction.ItemType.Prayer && p.Action == ModelQueueAction.ActionType.Delete) {
                    submitDeletePrayer(db, adapter, p.ItemID, p.ID, p.IfExecutedGUID);
                }
                else if(p.Item == ModelQueueAction.ItemType.PrayerPublicView && p.Action == ModelQueueAction.ActionType.Update){
                    ModelPrayer t = (ModelPrayer) Utils.deserialize(p.ItemID);
                    submitUpdatePrayerPublicView(db, adapter,t, p.ID, p.IfExecutedGUID);
                }
                else if(p.Item == ModelQueueAction.ItemType.PrayerTagFriends && p.Action == ModelQueueAction.ActionType.Update){
                    ModelPrayer t = (ModelPrayer) Utils.deserialize(p.ItemID);
                    submitUpdatePrayerTagFriends(db, adapter, t, p.ID, p.IfExecutedGUID);
                }
                else if(p.Item == ModelQueueAction.ItemType.PrayerComment && p.Action == ModelQueueAction.ActionType.Insert){
                    ModelPrayerComment t = (ModelPrayerComment) Utils.deserialize(p.ItemID);
                    submitnewPrayerComment(db, adapter, t, p.ID, p.IfExecutedGUID);
                }
                else if(p.Item == ModelQueueAction.ItemType.PrayerComment && p.Action == ModelQueueAction.ActionType.Delete){
                    submitDeletePrayerComment(db, adapter, p.ItemID, p.ID, p.IfExecutedGUID);
                }
                else if(p.Item == ModelQueueAction.ItemType.PrayerComment && p.Action == ModelQueueAction.ActionType.Update){
                    ModelPrayerComment t = (ModelPrayerComment) Utils.deserialize(p.ItemID);
                    submitUpdatePrayerComment(db, adapter, t, p.ID, p.IfExecutedGUID);
                }
                else if(p.Item == ModelQueueAction.ItemType.PrayerAmen && p.Action == ModelQueueAction.ActionType.Insert){
                    ModelPrayer t = (ModelPrayer) Utils.deserialize(p.ItemID);
                    submitNewPrayerAmen(db, adapter, t, p.ID, p.IfExecutedGUID);
                }
                else if(p.Item == ModelQueueAction.ItemType.PrayerAmen && p.Action == ModelQueueAction.ActionType.Delete){
                    ModelPrayer t = (ModelPrayer) Utils.deserialize(p.ItemID);
                    submitDeletePrayerAmen(db, adapter, t, p.ID, p.IfExecutedGUID);
                }
                else if(p.Item == ModelQueueAction.ItemType.PrayerAnswered && p.Action == ModelQueueAction.ActionType.Insert){
                    ModelPrayerAnswered t = (ModelPrayerAnswered) Utils.deserialize(p.ItemID);
                    submitnewPrayerAnswered(db, adapter, t, p.ID, p.IfExecutedGUID);
                }
                else if(p.Item == ModelQueueAction.ItemType.PrayerAnswered && p.Action == ModelQueueAction.ActionType.Update){
                    ModelPrayerAnswered t = (ModelPrayerAnswered) Utils.deserialize(p.ItemID);
                    submitUpdatePrayerAnswered(db, adapter, t, p.ID, p.IfExecutedGUID);
                }
                else if(p.Item == ModelQueueAction.ItemType.PrayerAnswered && p.Action == ModelQueueAction.ActionType.Delete){
                    submitDeletePrayerAnswered(db, adapter, p.ItemID, p.ID, p.IfExecutedGUID);
                }
                else if(p.Item == ModelQueueAction.ItemType.PrayerCommentReply && p.Action == ModelQueueAction.ActionType.Insert){
                    ModelPrayerCommentReply t = (ModelPrayerCommentReply) Utils.deserialize(p.ItemID);
                    submitnewPrayerCommentReply(db, adapter, t, p.ID, p.IfExecutedGUID);
                }
                else if(p.Item == ModelQueueAction.ItemType.PrayerCommentReply && p.Action == ModelQueueAction.ActionType.Delete){
                    submitDeletePrayerCommentReply(db, adapter, p.ItemID, p.ID, p.IfExecutedGUID);
                }
                else if(p.Item == ModelQueueAction.ItemType.PrayerCommentReply && p.Action == ModelQueueAction.ActionType.Update){
                    ModelPrayerCommentReply t = (ModelPrayerCommentReply) Utils.deserialize(p.ItemID);
                    submitUpdatePrayerCommentReply(db, adapter, t, p.ID, p.IfExecutedGUID);
                }

                Fragment f = mainActivity.getSupportFragmentManager().findFragmentById(R.id.fragment);
                if (f instanceof FragmentQueueAction) {
                    ((FragmentQueueAction) f).onQueueActionUpdate();
                }
            }
            closeSyncProcessNotification();
            db.close();
        }
        catch(Exception e){
            String sdf = e.toString();
            sdf.toString();
        }
    }

    private void submitDeletePrayerRequest(Database db, RestAdapter adapter, String PrayerRequestID, int QueueID, String IfExecutedGUID){

        PrayerInterface prayerInterface = adapter.create(PrayerInterface.class);
        SimpleJsonResponse response = prayerInterface.DeletePrayerRequest(IfExecutedGUID, PrayerRequestID);
        if (response.StatusCode == 200) {
            if(response.Description.toUpperCase().compareToIgnoreCase("NOTEXISTS") == 0){
                return;
            }

            db.deleteQueue(QueueID);
            //update the commentID from server
        }
    }

    private void submitUpdatePrayerRequest(Database db, RestAdapter adapter, ModelPrayerRequest p, int QueueID, String IfExecutedGUID) {
        if(p == null)
            return;

        p.attachments = db.getAllPrayerRequestAttachment(p.PrayerRequestID);
        try {

            PrayerInterface prayerInterface = adapter.create(PrayerInterface.class);
            SimpleJsonResponse response = prayerInterface.UpdatePrayerRequest(IfExecutedGUID, p);
            if (response.StatusCode == 200) {

                db.deleteQueue(QueueID);
                db.decrementPrayerRequestInQueue(p.PrayerRequestID);
                mainActivity.onPrayerRequestUpdated();
            }

        } catch (Exception e) {
            String sdf = e.getMessage();
        }
    }

    private void submitNewPrayerRequest(Database db, RestAdapter adapter, ModelPrayerRequest p, int QueueID, String IfExecutedGUID) {
        if(p == null)
            return;

        p.attachments = db.getAllPrayerRequestAttachment(p.PrayerRequestID);
        try {
            for(int x=0; x<p.attachments.size(); x++) {
                ModelPrayerRequestAttachement att = p.attachments.get(x);
                SimpleJsonResponse response = uploadPrayerImage(att, adapter);
                if (response.StatusCode == 202) {
                    String newFilename = "";
                    if(response.Description.toUpperCase().startsWith("EXISTS-")){
                        newFilename = response.Description.substring(7);
                    }
                    else{
                        newFilename = response.Description;
                    }

                    db.updatePrayerRequestAttachmentFilename(newFilename, att.GUID, p.PrayerRequestID);
                }
            }

            PrayerInterface prayerInterface = adapter.create(PrayerInterface.class);
            SimpleJsonResponse response = prayerInterface.AddNewPrayerRequest(IfExecutedGUID, p);
            if (response.StatusCode == 200) {
                if(response.Description.toUpperCase().startsWith("OK-") || response.Description.toUpperCase().startsWith("EXISTS-")){
                    String newPrayerRequestID = response.Description.substring(response.Description.indexOf("-") + 1);
                    db.UpdatePrayerRequestID(newPrayerRequestID, p.PrayerRequestID);
                    p.PrayerRequestID = newPrayerRequestID;
                    db.decrementPrayerRequestInQueue(p.PrayerRequestID);
                }

                db.deleteQueue(QueueID);
                mainActivity.onPrayerRequestUpdated();
            }

        } catch (Exception e) {
            String sdf = e.getMessage();
        }


    }

    private void submitnewPrayerAnswered(Database db, RestAdapter adapter, ModelPrayerAnswered p, int QueueID, String IfExecutedGUID){
        if(p == null)
            return;

        PrayerInterface prayerInterface = adapter.create(PrayerInterface.class);
        SimpleJsonResponse response = prayerInterface.AddNewPrayerAnswered(IfExecutedGUID, p.OwnerPrayerID, p);
        if (response.StatusCode == 200) {
            if(response.Description.toUpperCase().startsWith("OK-") || response.Description.toUpperCase().startsWith("EXISTS-")){
                String newAnsweredID = response.Description.substring(response.Description.indexOf("-") + 1);
                db.UpdatePrayerAnsweredID(newAnsweredID, p.AnsweredID, p.OwnerPrayerID);
                p.AnsweredID = newAnsweredID;
            }

            db.decrementPrayerAnsweredInQueue(p.AnsweredID);
            db.deleteQueue(QueueID);

            Fragment f = mainActivity.getSupportFragmentManager().findFragmentById(R.id.fragment);
            if (f instanceof InterfacePrayerAnsweredListViewUpdated && mainActivity.OwnerID.length() > 0) {
                ((InterfacePrayerAnsweredListViewUpdated) f).onAnswerUpdate(db.getAllOwnerPrayerAnswered(p.OwnerPrayerID));
            }
            else if (f instanceof InterfacePrayerAnsweredEditUpdated && mainActivity.OwnerID.length() > 0) {
                ((InterfacePrayerAnsweredEditUpdated) f).onAnswerUpdate(p);
            }
        }
    }

    private void submitUpdatePrayerAnswered(Database db, RestAdapter adapter, ModelPrayerAnswered p, int QueueID, String IfExecutedGUID){
        if(p == null)
            return;

        PrayerInterface prayerInterface = adapter.create(PrayerInterface.class);
        SimpleJsonResponse response = prayerInterface.UpdatePrayerAnswered(IfExecutedGUID, p.OwnerPrayerID, p);
        if (response.StatusCode == 200) {
            if(response.Description.toUpperCase().startsWith("OK-") || response.Description.toUpperCase().startsWith("EXISTS-")){
                String newAnsweredID = response.Description.substring(response.Description.indexOf("-") + 1);
                db.UpdatePrayerAnsweredID(newAnsweredID, p.AnsweredID, p.OwnerPrayerID);
                p.AnsweredID = newAnsweredID;
            }

            db.decrementPrayerAnsweredInQueue(p.AnsweredID);
            db.deleteQueue(QueueID);

            Fragment f = mainActivity.getSupportFragmentManager().findFragmentById(R.id.fragment);
            if (f instanceof InterfacePrayerAnsweredListViewUpdated && mainActivity.OwnerID.length() > 0) {
                ((InterfacePrayerAnsweredListViewUpdated) f).onAnswerUpdate(db.getAllOwnerPrayerAnswered(p.OwnerPrayerID));
            }
            else if (f instanceof InterfacePrayerAnsweredEditUpdated && mainActivity.OwnerID.length() > 0) {
                ((InterfacePrayerAnsweredEditUpdated) f).onAnswerUpdate(p);
            }
        }
    }

    private void submitDeletePrayerAnswered(Database db, RestAdapter adapter, String AnsweredID, int QueueID, String IfExecutedGUID){

        PrayerInterface prayerInterface = adapter.create(PrayerInterface.class);
        SimpleJsonResponse response = prayerInterface.DeletePrayerAnswered(IfExecutedGUID, AnsweredID);
        if (response.StatusCode == 200) {
            if(response.Description.toUpperCase().compareToIgnoreCase("NOTEXISTS") == 0){
                return;
            }

            db.deleteQueue(QueueID);
            //update the commentID from server
        }
    }

    private void submitDeletePrayerAmen(Database db, RestAdapter adapter, ModelPrayer p, int QueueID, String IfExecutedGUID){
        PrayerInterface prayerInterface = adapter.create(PrayerInterface.class);
        SimpleJsonResponse response = prayerInterface.DeletePrayerAmen(IfExecutedGUID, p.PrayerID);
        if (response.StatusCode == 200) {
            db.deleteQueue(QueueID);

            db.decrementPrayerInQueue(String.valueOf(p.PrayerID));
            Fragment f = mainActivity.getSupportFragmentManager().findFragmentById(R.id.fragment);
            if (f instanceof InterfacePrayerListUpdated && mainActivity.OwnerID.length() > 0) {
                ((InterfacePrayerListUpdated) f).onListUpdate(p.PrayerCategory);
            }
        }
    }

    private void submitNewPrayerAmen(Database db, RestAdapter adapter, ModelPrayer p, int QueueID, String IfExecutedGUID){
        PrayerInterface prayerInterface = adapter.create(PrayerInterface.class);
        SimpleJsonResponse response = prayerInterface.AddNewPrayerAmen(IfExecutedGUID, p.PrayerID);
        if (response.StatusCode == 200) {
            db.deleteQueue(QueueID);

            db.decrementPrayerInQueue(String.valueOf(p.PrayerID));
            Fragment f = mainActivity.getSupportFragmentManager().findFragmentById(R.id.fragment);
            if (f instanceof InterfacePrayerListUpdated && mainActivity.OwnerID.length() > 0) {
                ((InterfacePrayerListUpdated) f).onListUpdate(p.PrayerCategory);
            }
        }
    }

    private void submitUpdatePrayerComment(Database db, RestAdapter adapter, ModelPrayerComment p, int QueueID, String IfExecutedGUID){
        if(p == null)
            return;

        PrayerInterface prayerInterface = adapter.create(PrayerInterface.class);
        SimpleJsonResponse response = prayerInterface.UpdatePrayerComment(IfExecutedGUID, p);
        if (response.StatusCode == 200) {
            db.deleteQueue(QueueID);
            db.decrementPrayerCommentInQueue(p.CommentID);

            Fragment f = mainActivity.getSupportFragmentManager().findFragmentById(R.id.fragment);
            if (f instanceof InterfacePrayerCommentListViewUpdated && mainActivity.OwnerID.length() > 0) {
                ((InterfacePrayerCommentListViewUpdated) f).onCommentUpdate(db.getAllOwnerPrayerComment(p.OwnerPrayerID));
            }
        }
    }

    private void submitUpdatePrayerCommentReply(Database db, RestAdapter adapter, ModelPrayerCommentReply p, int QueueID, String IfExecutedGUID){
        if(p == null)
            return;

        PrayerInterface prayerInterface = adapter.create(PrayerInterface.class);
        SimpleJsonResponse response = prayerInterface.UpdatePrayerCommentReply(IfExecutedGUID, p);
        if (response.StatusCode == 200) {
            db.deleteQueue(QueueID);
            db.decrementPrayerCommentReplyInQueue(p.CommentReplyID);

            Fragment f = mainActivity.getSupportFragmentManager().findFragmentById(R.id.fragment);
            if (f instanceof FragmentPrayerCommentReply && mainActivity.OwnerID.length() > 0) {
                ((FragmentPrayerCommentReply) f).onCommentReplyUpdate(db.getAllOwnerPrayerCommentReply(p.OwnerPrayerID, p.MainCommentID));
            }
        }
    }

    private void submitDeletePrayerComment(Database db, RestAdapter adapter, String CommentID, int QueueID, String IfExecutedGUID){

        PrayerInterface prayerInterface = adapter.create(PrayerInterface.class);
        SimpleJsonResponse response = prayerInterface.DeletePrayerComment(IfExecutedGUID, CommentID);
        if (response.StatusCode == 200) {
            if(response.Description.toUpperCase().compareToIgnoreCase("NOTEXISTS") == 0){
                return;
            }

            db.deleteQueue(QueueID);
            //update the commentID from server
        }
    }

    private void submitDeletePrayerCommentReply(Database db, RestAdapter adapter, String CommentID, int QueueID, String IfExecutedGUID){

        PrayerInterface prayerInterface = adapter.create(PrayerInterface.class);
        SimpleJsonResponse response = prayerInterface.DeletePrayerCommentReply(IfExecutedGUID, CommentID);
        if (response.StatusCode == 200) {
            if(response.Description.toUpperCase().compareToIgnoreCase("NOTEXISTS") == 0){
                return;
            }

            db.deleteQueue(QueueID);
            //update the commentID from server
        }
    }

    private void submitnewPrayerCommentReply(Database db, RestAdapter adapter, ModelPrayerCommentReply p, int QueueID, String IfExecutedGUID){
        if(p == null)
            return;

        PrayerInterface prayerInterface = adapter.create(PrayerInterface.class);
        SimpleJsonResponse response = prayerInterface.AddNewPrayerCommentReply(IfExecutedGUID, p.OwnerPrayerID, p.MainCommentID, p);
        if (response.StatusCode == 200) {
            if(response.Description.toUpperCase().startsWith("OK-") || response.Description.toUpperCase().startsWith("EXISTS-")){
                String newCommentReplyID = response.Description.substring(response.Description.indexOf("-") + 1);
                db.UpdatePrayerCommentReplyID(newCommentReplyID, p.CommentReplyID);
                p.CommentReplyID = newCommentReplyID;
            }
            db.decrementPrayerCommentReplyInQueue(p.CommentReplyID);
            db.deleteQueue(QueueID);

            Fragment f = mainActivity.getSupportFragmentManager().findFragmentById(R.id.fragment);
            if (f instanceof FragmentPrayerCommentReply && mainActivity.OwnerID.length() > 0) {
                ((FragmentPrayerCommentReply) f).onCommentReplyUpdate(db.getAllOwnerPrayerCommentReply(p.OwnerPrayerID, p.MainCommentID));
            }
            else if (f instanceof InterfacePrayerCommentEditUpdated && mainActivity.OwnerID.length() > 0) {
                //((InterfacePrayerCommentEditUpdated) f).onCommentUpdate(p);
            }
        }
    }

    private void submitnewPrayerComment(Database db, RestAdapter adapter, ModelPrayerComment p, int QueueID, String IfExecutedGUID){
        if(p == null)
            return;

        PrayerInterface prayerInterface = adapter.create(PrayerInterface.class);
        SimpleJsonResponse response = prayerInterface.AddNewPrayerComment(IfExecutedGUID, p.OwnerPrayerID, p);
        if (response.StatusCode == 200) {
            if(response.Description.toUpperCase().startsWith("OK-") || response.Description.toUpperCase().startsWith("EXISTS-")){
                String newCommentID = response.Description.substring(response.Description.indexOf("-") + 1);
                db.UpdatePrayerCommentID(newCommentID, p.CommentID, p.OwnerPrayerID);
                p.CommentID = newCommentID;
            }
            db.decrementPrayerCommentInQueue(p.CommentID);
            db.deleteQueue(QueueID);

            Fragment f = mainActivity.getSupportFragmentManager().findFragmentById(R.id.fragment);
            if (f instanceof InterfacePrayerCommentListViewUpdated && mainActivity.OwnerID.length() > 0) {
                ((InterfacePrayerCommentListViewUpdated) f).onCommentUpdate(db.getAllOwnerPrayerComment(p.OwnerPrayerID));
            }
            else if (f instanceof InterfacePrayerCommentEditUpdated && mainActivity.OwnerID.length() > 0) {
                ((InterfacePrayerCommentEditUpdated) f).onCommentUpdate(p);
            }
        }
    }

    private void submitUpdatePrayerPublicView(Database db, RestAdapter adapter, ModelPrayer p, int QueueID, String IfExecutedGUID){
        if(p == null)
            return;

        PrayerInterface prayerInterface = adapter.create(PrayerInterface.class);
        SimpleJsonResponse response = prayerInterface.UpdatePublicView(IfExecutedGUID, p.PrayerID, p.publicView);
        if (response.StatusCode == 200 && response.Description.toUpperCase().compareToIgnoreCase("NOTEXISTS") != 0) {
            db.deleteQueue(QueueID);

            db.decrementPrayerInQueue(String.valueOf(p.PrayerID));
            Fragment f = mainActivity.getSupportFragmentManager().findFragmentById(R.id.fragment);
            if (f instanceof InterfacePrayerListUpdated && mainActivity.OwnerID.length() > 0) {
                ((InterfacePrayerListUpdated) f).onListUpdate(R.id.prayerlist_category_mine);
            }
        }
    }

    private void submitUpdatePrayerTagFriends(Database db, RestAdapter adapter, ModelPrayer p, int QueueID, String IfExecutedGUID){
        if(p.selectedFriends == null)
            return;

        PrayerInterface prayerInterface = adapter.create(PrayerInterface.class);
        SimpleJsonResponse response = prayerInterface.UpdateTagFriends(IfExecutedGUID, p.PrayerID, p.selectedFriends);
        if (response.StatusCode == 200 && response.Description.toUpperCase().compareToIgnoreCase("NOTEXISTS") != 0) {
            db.deleteQueue(QueueID);

            db.decrementPrayerInQueue(String.valueOf(p.PrayerID));
            Fragment f = mainActivity.getSupportFragmentManager().findFragmentById(R.id.fragment);
            if (f instanceof InterfacePrayerListUpdated && mainActivity.OwnerID.length() > 0) {
                ((InterfacePrayerListUpdated) f).onListUpdate(R.id.prayerlist_category_mine);
            }
        }
    }

    private void submitDeletePrayer(Database db, RestAdapter adapter, String PrayerID, int QueueID, String IfExecutedGUID){

        PrayerInterface prayerInterface = adapter.create(PrayerInterface.class);
        SimpleJsonResponse response = prayerInterface.DeletePrayer(IfExecutedGUID, PrayerID);
        if (response.StatusCode == 200) {
            db.deleteQueue(QueueID);
        }
    }

    private void submitNewPrayer(Database db, RestAdapter adapter, ModelPrayer p, int QueueID, String IfExecutedGUID){
        if(p == null)
            return;
        p.selectedFriends = db.getSelectedTagFriend(p.PrayerID, mainActivity.OwnerID);
        p.attachments = db.getAllOwnerPrayerAttachment(p.PrayerID);
        try {
            for(int x=0; x<p.attachments.size(); x++) {
                ModelPrayerAttachement att = p.attachments.get(x);
                SimpleJsonResponse response = uploadPrayerImage(att, adapter);
                if (response.StatusCode == 202) {
                    String newFilename = "";
                    if(response.Description.toUpperCase().startsWith("EXISTS-")){
                        newFilename = response.Description.substring(7);
                    }
                    else{
                        newFilename = response.Description;
                    }

                    db.updateAttachmentFilename(newFilename, att.GUID, p.PrayerID);
                }
            }


            PrayerInterface prayerInterface = adapter.create(PrayerInterface.class);
            p.IfExecutedGUID = IfExecutedGUID;
            long newPrayerID = -1;
            SimpleJsonResponse response = prayerInterface.AddNewPrayer(p);
            if (response.StatusCode == 201 && response.Description.startsWith("EXISTS-")) {
                newPrayerID = Long.parseLong(response.Description.substring(6));
            }
            else if (response.StatusCode == 201) {
                newPrayerID = Long.parseLong(response.Description);
            }

            db.updateOwnerPrayerSent(p.PrayerID, newPrayerID);
            db.deleteQueue(QueueID);
            db.decrementPrayerInQueue(String.valueOf(newPrayerID));

            Fragment f = mainActivity.getSupportFragmentManager().findFragmentById(R.id.fragment);
            if (f instanceof InterfacePrayerListUpdated && mainActivity.OwnerID.length() > 0) {
                ((InterfacePrayerListUpdated) f).onListUpdate(R.id.prayerlist_category_mine);
            }



        } catch (Exception e) {
            String sdf = e.getMessage();
        }
    }

    private SimpleJsonResponse uploadPrayerImage(ModelPrayerRequestAttachement att, RestAdapter tadapter){
        String fileToSend = cropAndSave(att.OriginalFilePath.substring(7), att.GUID);
        File f = new File(fileToSend);

        TypedFile attachmentImg = new TypedFile("multipart/form-data", new File(fileToSend));
        InterfaceUploadFile interfaceUploadFile = tadapter.create(InterfaceUploadFile.class);
        SimpleJsonResponse json = interfaceUploadFile.CheckImageUploaded(att.GUID, f.getName());
        if(json.StatusCode == 202 && json.Description.compareToIgnoreCase("NOTEXISTS") ==0)
            json = interfaceUploadFile.AddPrayerImage(att.GUID, attachmentImg);
        return json;
    }

    private SimpleJsonResponse uploadPrayerImage(ModelPrayerAttachement att, RestAdapter tadapter){
        String fileToSend = cropAndSave(att.OriginalFilePath.substring(7), att.GUID);
        File f = new File(fileToSend);

        TypedFile attachmentImg = new TypedFile("multipart/form-data", new File(fileToSend));
        InterfaceUploadFile interfaceUploadFile = tadapter.create(InterfaceUploadFile.class);
        SimpleJsonResponse json = interfaceUploadFile.CheckImageUploaded(att.GUID, f.getName());
        if(json.StatusCode == 202 && json.Description.compareToIgnoreCase("NOTEXISTS") ==0)
            json = interfaceUploadFile.AddPrayerImage(att.GUID, attachmentImg);
        return json;
    }

    public String cropAndSave(String pathOfInputImage, String GUID){

        try
        {
            File f = new File(pathOfInputImage);
            File dir = new File(f.getParent() + "/resized");
            dir.mkdir();
            String pathOfOutputImage = dir.getPath() + "/" + f.getName();
            f = new File(pathOfOutputImage);
            if(f.exists()){
                return pathOfOutputImage;
            }
            int inWidth = 0;
            int inHeight = 0;
            float ratio = 0.0f;
            int dstWidth = 0; int dstHeight = 0;

            InputStream in = new FileInputStream(pathOfInputImage);

            // decode image size (decode metadata only, not the whole image)
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, options);
            in.close();
            in = null;

            // save width and height
            inWidth = options.outWidth;
            inHeight = options.outHeight;

            if(inHeight > QuickstartPreferences.Dimension  || inWidth > QuickstartPreferences.Dimension ) {
                if(inHeight > inWidth){
                    dstHeight = QuickstartPreferences.Dimension;
                    ratio = (float)inHeight / (float)QuickstartPreferences.Dimension;
                    dstWidth = (int)((float)inWidth / ratio);
                }
                if(inHeight < inWidth){
                    dstWidth = QuickstartPreferences.Dimension;
                    ratio = (float)inWidth / (float)QuickstartPreferences.Dimension;
                    dstHeight = (int)((float)inHeight / ratio);
                }
                else{
                    return pathOfInputImage;
                }

                // decode full image pre-resized
                in = new FileInputStream(pathOfInputImage);
                options = new BitmapFactory.Options();
                // calc rought re-size (this is no exact resize)
                options.inSampleSize = Math.max(inWidth / dstWidth, inHeight / dstHeight);
                // decode full image
                Bitmap roughBitmap = BitmapFactory.decodeStream(in, null, options);

                // calc exact destination size
                Matrix m = new Matrix();
                RectF inRect = new RectF(0, 0, roughBitmap.getWidth(), roughBitmap.getHeight());
                RectF outRect = new RectF(0, 0, dstWidth, dstHeight);
                outRect = new RectF(0, 0, roughBitmap.getWidth(), roughBitmap.getHeight());
                m.setRectToRect(inRect, outRect, Matrix.ScaleToFit.CENTER);
                float[] values = new float[9];
                m.getValues(values);

                // resize bitmap
                Bitmap resizedBitmap = Bitmap.createScaledBitmap(roughBitmap, (int) (roughBitmap.getWidth() * values[0]), (int) (roughBitmap.getHeight() * values[4]), true);

                // save image
                try {
                    FileOutputStream out = new FileOutputStream(pathOfOutputImage);
                    resizedBitmap.compress(Bitmap.CompressFormat.JPEG, QuickstartPreferences.Quality, out);
                    return pathOfOutputImage;
                } catch (Exception e) {
                    e.toString();
                }
            }
        }
        catch (Exception e)
        {
            e.toString();
        }
        return pathOfInputImage;
    }
}


