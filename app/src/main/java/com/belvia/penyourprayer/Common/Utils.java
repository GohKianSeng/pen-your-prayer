package com.belvia.penyourprayer.Common;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.DisplayMetrics;

import com.belvia.penyourprayer.QuickstartPreferences;
import com.belvia.penyourprayer.UI.MainActivity;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    public static void CopyStream(InputStream is, OutputStream os)
    {
        final int buffer_size=1024;
        try
        {
            byte[] bytes=new byte[buffer_size];
            for(;;)
            {
                int count=is.read(bytes, 0, buffer_size);
                if(count==-1)
                    break;
                os.write(bytes, 0, count);
            }
        }
        catch(Exception ex){}
    }

    public static String TempUserID(MainActivity mainActivity){
        String tempuserid = mainActivity.sharedPreferences.getString(QuickstartPreferences.RandomUserID, GenerateRandomID());
        mainActivity.sharedPreferences.edit().putString(QuickstartPreferences.RandomUserID, tempuserid);
        return tempuserid;
    }

    private static String GenerateRandomID(){
        final int min = 1;
        final int max = 2147483647;
        Random r = new Random();
        return String.valueOf(r.nextInt(max)) + String.valueOf(r.nextInt(max));
    }

    public static boolean isEmailValid(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{1,10}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    public static int dpToPx(MainActivity ma,int dp) {
        DisplayMetrics displayMetrics = ma.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    public static File getAbsolutePath(Intent data, MainActivity mainActivity){
        Uri uri = data.getData();
        String[] projection = { MediaStore.Images.Media.DATA };

        Cursor cursor = mainActivity.getContentResolver().query(uri, projection, null, null, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(projection[0]);
        String picturePath = cursor.getString(columnIndex); // returns null
        cursor.close();

        return new File(picturePath);
    }

    public static boolean isInternetAvailable(MainActivity mainActivity) {
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)mainActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            //we are connected to a network
            connected = true;
        }
        else
            connected = false;

        return connected;
    }
}
