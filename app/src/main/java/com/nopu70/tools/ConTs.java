package com.nopu70.tools;

import android.os.Bundle;
import android.os.Message;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

/**
 * Created by nopu70 on 16-3-1.
 */
public class ConTs {

    public String connectTs(String str, String strSrc, String strDict){

        String sign = "20160301000014065"+str+"1435660288"+"TK6TeBYt0sh3ooitz1Ck";
        String s = "http://api.fanyi.baidu.com/api/trans/vip/translate?q="
                +java.net.URLEncoder.encode(str)+"&from="+strSrc+"&to="+strDict
                +"&appid=20160301000014065&salt=1435660288&sign="+MD5(sign);
        try {
            URL url = new URL(s);
            URLConnection urlConnection = (HttpURLConnection)url.openConnection();
            urlConnection.setConnectTimeout(10000);
            urlConnection.setReadTimeout(10000);
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            String result = readInStream(in);

            return  result;
        }catch (IOException e){

        }

        return null;
    }

    public String parsingConTsJOSN(String string){
        try {
            JSONObject root = new JSONObject(string);
            if (root.has("trans_result")){
                JSONArray trans_result = root.getJSONArray("trans_result");
                JSONObject t0 = (JSONObject)trans_result.opt(0);
                String result = t0.getString("dst");
                return  result;
            }
        }catch (JSONException e){}
        return null;
    }

    private String readInStream(InputStream in) {
        Scanner scanner = new Scanner(in).useDelimiter("\\A");
        return scanner.hasNext() ? scanner.next() : "";
    }

    private String MD5(String sourceStr) {
        String result = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(sourceStr.getBytes());
            byte b[] = md.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            result = buf.toString();
            System.out.println("MD5(" + sourceStr + ",32) = " + result);
//            System.out.println("MD5(" + sourceStr + ",16) = " + buf.toString().substring(8, 24));
        } catch (NoSuchAlgorithmException e) {
            System.out.println(e);
        }
        return result;
    }


}
