package com.penyourprayer.penyourprayer.QueueAction;

import android.os.AsyncTask;
import android.os.Build;
import android.os.SystemClock;
import android.support.v4.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.penyourprayer.penyourprayer.Common.Interface.InterfacePrayerCommentEditUpdated;
import com.penyourprayer.penyourprayer.Common.Interface.InterfacePrayerCommentListViewUpdated;
import com.penyourprayer.penyourprayer.Common.Interface.InterfacePrayerListUpdated;
import com.penyourprayer.penyourprayer.Common.Model.ModelFriendProfile;
import com.penyourprayer.penyourprayer.Common.Model.ModelOwnerPrayer;
import com.penyourprayer.penyourprayer.Common.Model.ModelPayerComment;
import com.penyourprayer.penyourprayer.Common.Model.ModelPrayerAttachement;
import com.penyourprayer.penyourprayer.Database.Database;
import com.penyourprayer.penyourprayer.Common.Model.ModelQueueAction;
import com.penyourprayer.penyourprayer.QuickstartPreferences;
import com.penyourprayer.penyourprayer.R;
import com.penyourprayer.penyourprayer.UI.MainActivity;
import com.penyourprayer.penyourprayer.WebAPI.InterfaceUploadFile;
import com.penyourprayer.penyourprayer.WebAPI.Model.SimpleJsonResponse;
import com.penyourprayer.penyourprayer.WebAPI.PrayerInterface;
import com.penyourprayer.penyourprayer.WebAPI.httpClient;

import java.io.File;
import java.util.ArrayList;

import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;
import retrofit.mime.TypedFile;

/**
 * Created by sisgks on 27/11/2015.
 */
public class QueueAction extends AsyncTask<String, Void, String> {

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

    private void ProcessMessageQueue(RestAdapter adapter){
        try {
            Database db = new Database(mainActivity);
            ArrayList<ModelQueueAction> queue = db.getAllQueueItems();
            for (int x = 0; x < queue.size(); x++) {
                ModelQueueAction p = queue.get(x);

                if (p.Item == ModelQueueAction.ItemType.Prayer && p.Action == ModelQueueAction.ActionType.Insert) {
                    submitNewPrayer(db, adapter, p.ItemID, p.ID, p.IfExecutedGUID);
                }
                else if(p.Item == ModelQueueAction.ItemType.PrayerPublicView && p.Action == ModelQueueAction.ActionType.Update){
                    submitUpdatePrayerPublicView(db, adapter, p.ItemID, p.ID, p.IfExecutedGUID);
                }
                else if(p.Item == ModelQueueAction.ItemType.PrayerTagFriends && p.Action == ModelQueueAction.ActionType.Update){
                    submitUpdatePrayerTagFriends(db, adapter, p.ItemID, p.ID, p.IfExecutedGUID);
                }
                else if(p.Item == ModelQueueAction.ItemType.PrayerComment && p.Action == ModelQueueAction.ActionType.Insert){
                    submitnewPrayerComment(db, adapter, p.ItemID, p.ID, p.IfExecutedGUID);
                }
                else if(p.Item == ModelQueueAction.ItemType.PrayerComment && p.Action == ModelQueueAction.ActionType.Delete){
                    submitDeletePrayerComment(db, adapter, p.ItemID, p.ID, p.IfExecutedGUID);
                }
                else if(p.Item == ModelQueueAction.ItemType.PrayerComment && p.Action == ModelQueueAction.ActionType.Update){
                    submitUpdatePrayerComment(db, adapter, p.ItemID, p.ID, p.IfExecutedGUID);
                }
            }
            db.close();
        }
        catch(Exception e){
            String sdf = e.toString();
            sdf.toString();
        }
    }

    private void submitUpdatePrayerComment(Database db, RestAdapter adapter, String CommentID, int QueueID, String IfExecutedGUID){
        ModelPayerComment p = db.GetPrayerComment(CommentID);
        if(p == null)
            return;

        PrayerInterface prayerInterface = adapter.create(PrayerInterface.class);
        SimpleJsonResponse response = prayerInterface.UpdatePrayerComment(IfExecutedGUID, p);
        if (response.StatusCode == 200) {
            db.deleteQueue(QueueID);

            Fragment f = mainActivity.getSupportFragmentManager().findFragmentById(R.id.fragment);
            if (f instanceof InterfacePrayerCommentListViewUpdated && mainActivity.OwnerID.length() > 0) {
                ((InterfacePrayerCommentListViewUpdated) f).onCommentUpdate(db.getAllOwnerPrayerComment(p.OwnerPrayerID));
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

    private void submitnewPrayerComment(Database db, RestAdapter adapter, String CommentID, int QueueID, String IfExecutedGUID){
        ModelPayerComment p = db.GetPrayerComment(CommentID);
        if(p == null)
            return;

        PrayerInterface prayerInterface = adapter.create(PrayerInterface.class);
        SimpleJsonResponse response = prayerInterface.AddNewPrayerComment(IfExecutedGUID, p.OwnerPrayerID, p);
        if (response.StatusCode == 200) {
            if(response.Description.toUpperCase().startsWith("OK-")){
                String newCommentID = response.Description.substring(3);
                db.UpdatePrayerCommentID(newCommentID, p.CommentID, p.OwnerPrayerID);
                p.CommentID = newCommentID;
            }

            Fragment f = mainActivity.getSupportFragmentManager().findFragmentById(R.id.fragment);
            if (f instanceof InterfacePrayerCommentListViewUpdated && mainActivity.OwnerID.length() > 0) {
                ((InterfacePrayerCommentListViewUpdated) f).onCommentUpdate(db.getAllOwnerPrayerComment(p.OwnerPrayerID));
            }
            else if (f instanceof InterfacePrayerCommentEditUpdated && mainActivity.OwnerID.length() > 0) {
                ((InterfacePrayerCommentEditUpdated) f).onCommentUpdate(p);
            }
            db.deleteQueue(QueueID);
        }
    }

    private void submitUpdatePrayerPublicView(Database db, RestAdapter adapter, String PrayerID, int QueueID, String IfExecutedGUID){
        ModelOwnerPrayer p = db.GetPrayer(PrayerID);
        if(p == null)
            return;

        PrayerInterface prayerInterface = adapter.create(PrayerInterface.class);
        SimpleJsonResponse response = prayerInterface.UpdatePublicView(IfExecutedGUID, PrayerID, p.publicView);
        if (response.StatusCode == 200 && response.Description.toUpperCase().compareToIgnoreCase("NOTEXISTS") != 0) {
            db.deleteQueue(QueueID);
        }
    }

    private void submitUpdatePrayerTagFriends(Database db, RestAdapter adapter, String PrayerID, int QueueID, String IfExecutedGUID){
        ArrayList<ModelFriendProfile> selectedFriends = db.getSelectedTagFriend(PrayerID);
        if(selectedFriends == null)
            return;

        PrayerInterface prayerInterface = adapter.create(PrayerInterface.class);
        SimpleJsonResponse response = prayerInterface.UpdateTagFriends(IfExecutedGUID, PrayerID, selectedFriends);
        if (response.StatusCode == 200 && response.Description.toUpperCase().compareToIgnoreCase("NOTEXISTS") != 0) {
            db.deleteQueue(QueueID);
        }
    }

    private void submitNewPrayer(Database db, RestAdapter adapter, String PrayerID, int QueueID, String IfExecutedGUID){

        ModelOwnerPrayer p = db.GetPrayer(PrayerID);
        if(p == null)
            return;
        p.selectedFriends = db.getSelectedTagFriend(p.PrayerID);
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

            Fragment f = mainActivity.getSupportFragmentManager().findFragmentById(R.id.fragment);
            if (f instanceof InterfacePrayerListUpdated && mainActivity.OwnerID.length() > 0) {
                ((InterfacePrayerListUpdated) f).onListUpdate(db.getAllOwnerPrayer(mainActivity.OwnerID));
            }

            db.deleteQueue(QueueID);

        } catch (Exception e) {
            String sdf = e.getMessage();
        }
    }

    private SimpleJsonResponse uploadPrayerImage(ModelPrayerAttachement att, RestAdapter tadapter){
        TypedFile attachmentImg = new TypedFile("multipart/form-data", new File(att.OriginalFilePath.substring(7)));
        InterfaceUploadFile interfaceUploadFile = tadapter.create(InterfaceUploadFile.class);
        SimpleJsonResponse json = interfaceUploadFile.CheckImageUploaded(att.GUID, att.FileName);
        if(json.StatusCode == 202 && json.Description.compareToIgnoreCase("NOTEXISTS") ==0)
            json = interfaceUploadFile.AddPrayerImage(att.GUID, attachmentImg);
        return json;
    }
}
