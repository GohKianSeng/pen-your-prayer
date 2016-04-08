package com.belvia.penyourprayer.QueueAction;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Build;
import android.os.SystemClock;
import android.support.v4.app.Fragment;

import com.belvia.penyourprayer.Common.Interface.InterfacePrayerCommentEditUpdated;
import com.belvia.penyourprayer.Common.Interface.InterfacePrayerCommentListViewUpdated;
import com.belvia.penyourprayer.Common.Interface.InterfacePrayerListUpdated;
import com.belvia.penyourprayer.Common.Model.ModelFriendProfile;
import com.belvia.penyourprayer.Common.Model.ModelOwnerPrayer;
import com.belvia.penyourprayer.Common.Model.ModelPrayerAnswered;
import com.belvia.penyourprayer.Common.Model.ModelPrayerAttachement;
import com.belvia.penyourprayer.Common.Model.ModelPrayerComment;
import com.belvia.penyourprayer.Common.Model.ModelPrayerRequest;
import com.belvia.penyourprayer.Common.Model.ModelPrayerRequestAttachement;
import com.belvia.penyourprayer.Common.Model.ModelQueueAction;
import com.belvia.penyourprayer.Common.Utils;
import com.belvia.penyourprayer.Database.Database;
import com.belvia.penyourprayer.QuickstartPreferences;
import com.belvia.penyourprayer.R;
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

                if (p.Item == ModelQueueAction.ItemType.PrayerRequest && p.Action == ModelQueueAction.ActionType.Insert) {
                    submitNewPrayerRequest(db, adapter, p.ItemID, p.ID, p.IfExecutedGUID);
                }
                else if(p.Item == ModelQueueAction.ItemType.PrayerRequest && p.Action == ModelQueueAction.ActionType.Delete){
                    submitDeletePrayerRequest(db, adapter, p.ItemID, p.ID, p.IfExecutedGUID);
                }
                if (p.Item == ModelQueueAction.ItemType.PrayerRequest && p.Action == ModelQueueAction.ActionType.Update) {
                    submitUpdatePrayerRequest(db, adapter, p.ItemID, p.ID, p.IfExecutedGUID);
                }
                else if (p.Item == ModelQueueAction.ItemType.Prayer && p.Action == ModelQueueAction.ActionType.Insert) {
                    submitNewPrayer(db, adapter, p.ItemID, p.ID, p.IfExecutedGUID);
                }
                else if (p.Item == ModelQueueAction.ItemType.Prayer && p.Action == ModelQueueAction.ActionType.Delete) {
                    submitDeletePrayer(db, adapter, p.ItemID, p.ID, p.IfExecutedGUID);
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
                else if(p.Item == ModelQueueAction.ItemType.PrayerAmen && p.Action == ModelQueueAction.ActionType.Insert){
                    submitNewPrayerAmen(db, adapter, p.ItemID, p.ID, p.IfExecutedGUID);
                }
                else if(p.Item == ModelQueueAction.ItemType.PrayerAmen && p.Action == ModelQueueAction.ActionType.Delete){
                    submitDeletePrayerAmen(db, adapter, p.ItemID, p.ID, p.IfExecutedGUID);
                }
                else if(p.Item == ModelQueueAction.ItemType.PrayerAnswered && p.Action == ModelQueueAction.ActionType.Insert){
                    submitnewPrayerAnswered(db, adapter, p.ItemID, p.ID, p.IfExecutedGUID);
                }
                else if(p.Item == ModelQueueAction.ItemType.PrayerAnswered && p.Action == ModelQueueAction.ActionType.Update){
                    ModelPrayerAnswered t = (ModelPrayerAnswered) Utils.deserialize(p.ItemID);
                    submitUpdatePrayerAnswered(db, adapter, t, p.ID, p.IfExecutedGUID);
                }

            }
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

    private void submitUpdatePrayerRequest(Database db, RestAdapter adapter, String PrayerRequestID, int QueueID, String IfExecutedGUID) {
        ModelPrayerRequest p = db.GetPrayerRequest(PrayerRequestID);
        if(p == null)
            return;

        p.attachments = db.getAllPrayerRequestAttachment(p.PrayerRequestID);
        try {

            PrayerInterface prayerInterface = adapter.create(PrayerInterface.class);
            SimpleJsonResponse response = prayerInterface.UpdatePrayerRequest(IfExecutedGUID, p);
            if (response.StatusCode == 200) {

                db.deleteQueue(QueueID);
            }

        } catch (Exception e) {
            String sdf = e.getMessage();
        }
    }

    private void submitNewPrayerRequest(Database db, RestAdapter adapter, String PrayerRequestID, int QueueID, String IfExecutedGUID) {
        ModelPrayerRequest p = db.GetPrayerRequest(PrayerRequestID);
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
                }


                db.deleteQueue(QueueID);
            }

        } catch (Exception e) {
            String sdf = e.getMessage();
        }


    }

    private void submitnewPrayerAnswered(Database db, RestAdapter adapter, String AnsweredID, int QueueID, String IfExecutedGUID){
        ModelPrayerAnswered p = db.GetPrayerAnswered(AnsweredID);
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


            db.deleteQueue(QueueID);
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


            db.deleteQueue(QueueID);
        }
    }

    private void submitDeletePrayerAmen(Database db, RestAdapter adapter, String PrayerID, int QueueID, String IfExecutedGUID){
        PrayerInterface prayerInterface = adapter.create(PrayerInterface.class);
        SimpleJsonResponse response = prayerInterface.DeletePrayerAmen(IfExecutedGUID, PrayerID);
        if (response.StatusCode == 200) {
            db.deleteQueue(QueueID);
        }
    }

    private void submitNewPrayerAmen(Database db, RestAdapter adapter, String PrayerID, int QueueID, String IfExecutedGUID){
        PrayerInterface prayerInterface = adapter.create(PrayerInterface.class);
        SimpleJsonResponse response = prayerInterface.AddNewPrayerAmen(IfExecutedGUID, PrayerID);
        if (response.StatusCode == 200) {
            db.deleteQueue(QueueID);
        }
    }

    private void submitUpdatePrayerComment(Database db, RestAdapter adapter, String CommentID, int QueueID, String IfExecutedGUID){
        ModelPrayerComment p = db.GetPrayerComment(CommentID);
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
        ModelPrayerComment p = db.GetPrayerComment(CommentID);
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
        ArrayList<ModelFriendProfile> selectedFriends = db.getSelectedTagFriend(PrayerID, mainActivity.OwnerID);
        if(selectedFriends == null)
            return;

        PrayerInterface prayerInterface = adapter.create(PrayerInterface.class);
        SimpleJsonResponse response = prayerInterface.UpdateTagFriends(IfExecutedGUID, PrayerID, selectedFriends);
        if (response.StatusCode == 200 && response.Description.toUpperCase().compareToIgnoreCase("NOTEXISTS") != 0) {
            db.deleteQueue(QueueID);
        }
    }

    private void submitDeletePrayer(Database db, RestAdapter adapter, String PrayerID, int QueueID, String IfExecutedGUID){

        PrayerInterface prayerInterface = adapter.create(PrayerInterface.class);
        SimpleJsonResponse response = prayerInterface.DeletePrayer(IfExecutedGUID, PrayerID);
        if (response.StatusCode == 200) {
            db.deleteQueue(QueueID);
        }
    }

    private void submitNewPrayer(Database db, RestAdapter adapter, String PrayerID, int QueueID, String IfExecutedGUID){

        ModelOwnerPrayer p = db.GetPrayer(PrayerID);
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

            Fragment f = mainActivity.getSupportFragmentManager().findFragmentById(R.id.fragment);
            if (f instanceof InterfacePrayerListUpdated && mainActivity.OwnerID.length() > 0) {
                ((InterfacePrayerListUpdated) f).onListUpdate(db.getAllOwnerPrayer(mainActivity.OwnerID));
            }

            db.deleteQueue(QueueID);

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


