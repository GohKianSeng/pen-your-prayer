package com.penyourprayer.penyourprayer.Common;
import com.penyourprayer.penyourprayer.QuickstartPreferences;
import com.penyourprayer.penyourprayer.UI.MainActivity;

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
}
