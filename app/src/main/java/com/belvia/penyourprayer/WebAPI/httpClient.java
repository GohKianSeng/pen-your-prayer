package com.belvia.penyourprayer.WebAPI;

import android.util.Base64;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import okio.Buffer;

/**
 * Created by sisgks on 05/11/2015.
 */
public class httpClient extends OkHttpClient {
    private String LoginType;
    private String UserName;
    private String HMACKey;


    public httpClient(final String tLoginType, final String tUserName, String tHMACKey){
        super();
        this.LoginType = tLoginType;
        this.UserName = tUserName;
        this.HMACKey = tHMACKey;

        /**
         Pen Your Prayer Authorization Specification
         Encoding before send.
         Authorization for Registered User

         Authorization: hmac_logintype="email";
         hmac_username="zniter81@gmail.com";
         hmac_signature="wOJIO9A2W5mFwDgiDvZbTSMK%2FPY%3D";
         hmac_timestamp="Sun, 01 Nov 2015 15:31:08 GMT";
         hmac_nonce="4572616e48616d6d65724c61686176" random int value

         hashing order method + LoginType + UserName + tdate + nonce + query + content;
         */

        this.interceptors().add(new Interceptor() {
            @Override
            public com.squareup.okhttp.Response intercept(Interceptor.Chain chain) throws IOException {
                Request original = chain.request();
                try {
                    String method = original.method();
                    String contentMD5 = "";
                    if(method.toUpperCase() != "GET") {
                        String useless = bodyToString(original.body());
                        useless.toString();
                        contentMD5 = md5CheckSum(bodyToBytes(original.body())).toUpperCase();
                    }
                    String queryContent = original.uri().getQuery();
                    if(queryContent == null)
                        queryContent = "";
                    String now = getHttpFormatTime();

                    Random rand = new Random();
                    String Nonce = String.valueOf(rand.nextInt(32700));

                    String contentToHash = method.toUpperCase() + LoginType.toUpperCase() + UserName.toUpperCase() + now + Nonce + md5CheckSum(queryContent).toUpperCase() + contentMD5;
                    String hashContent = hmacSha1(contentToHash, HMACKey);
                    
                    String header = "hmac_logintype=\"" + LoginType.toUpperCase() + "\";" + "\n" +
                                    "hmac_username=\"" + UserName.toUpperCase() + "\";" + "\n" +
                                    "hmac_signature=\"" + hashContent + "\";" + "\n" +
                                    "hmac_timestamp=\"" + now + "\";" + "\n" +
                                    "hmac_nonce=\"" + Nonce + "\"";
                    String encodedString = URLEncoder.encode(header, "UTF-8");
                    Request request = original.newBuilder()
                            .header("Authorization", encodedString)
                            .method(original.method(), original.body())
                            .build();


                    return chain.proceed(request);
                } catch (Exception e) {
                    e.toString();
                }

                return null;
            }
        });
    }

    String getHttpFormatTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormat.format(calendar.getTime()).substring(0, 29);
    }

    private byte[] bodyToBytes(RequestBody request){

        try {
            final RequestBody copy = request;
            final Buffer buffer = new Buffer();
            copy.writeTo(buffer);
            return buffer.readByteArray();

        }
        catch (final Exception e) {
            return null;
        }
    }

    private String bodyToString(RequestBody request){

        try {
            final RequestBody copy = request;
            final Buffer buffer = new Buffer();
            copy.writeTo(buffer);
            String content = buffer.readUtf8();
            if(content.startsWith("\"") && content.endsWith("\"")){
                content = content.substring(1, content.length()-1);
            }
            return content;
        }
        catch (final Exception e) {
            return "";
        }
    }

    private String md5CheckSum(String in) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
            digest.reset();
            digest.update(in.getBytes());
            byte[] a = digest.digest();
            int len = a.length;
            StringBuilder sb = new StringBuilder(len << 1);
            for (int i = 0; i < len; i++) {
                sb.append(Character.forDigit((a[i] & 0xf0) >> 4, 16));
                sb.append(Character.forDigit(a[i] & 0x0f, 16));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) { e.printStackTrace(); }
        return "";
    }

    private String md5CheckSum(byte[] in) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
            digest.reset();
            digest.update(in);
            byte[] a = digest.digest();
            int len = a.length;
            StringBuilder sb = new StringBuilder(len << 1);
            for (int i = 0; i < len; i++) {
                sb.append(Character.forDigit((a[i] & 0xf0) >> 4, 16));
                sb.append(Character.forDigit(a[i] & 0x0f, 16));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) { e.printStackTrace(); }
        return "";
    }

    private String hmacSha1(String base_string, String key)
            throws UnsupportedEncodingException, NoSuchAlgorithmException,
            InvalidKeyException {

        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            SecretKeySpec secret = new SecretKeySpec(key.getBytes("UTF-8"), mac.getAlgorithm());
            mac.init(secret);
            byte[] digest = mac.doFinal(base_string.getBytes());

            return android.util.Base64.encodeToString(digest, Base64.DEFAULT).trim();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return "";
    }
}
