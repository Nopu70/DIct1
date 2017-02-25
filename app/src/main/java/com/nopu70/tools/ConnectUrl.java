package com.nopu70.tools;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.nopu70.dict.Dictionary;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by nopu70 on 15-12-23.
 */
public class ConnectUrl {

    public String ph_en = "";
    public String ph_am = "";
    public String ph_en_mp3 = "";
    public String ph_am_mp3 = "";
    public String dailyCon = "";
    public String dailyNote = "";
    public Word word;
    public String realw;
    public List<Map<String, String>> partList = new ArrayList<>();
    public Map<String, String> exchanges = new HashMap<>();


    public String connect(String str){
        realw = str;

        HttpURLConnection urlConnection = null;
        try {
            str = "http://dict-co.iciba.com/api/dictionary.php?w="+java.net.URLEncoder.encode(str)
                    +"&type=json&key=69D390FDF5D52B924828F2E4649532D9";
            URL url = new URL(str);
            urlConnection = (HttpURLConnection)url.openConnection();
            urlConnection.setConnectTimeout(10000);
            urlConnection.setReadTimeout(10000);
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            String result = readInStream(in);
            return result;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            urlConnection.disconnect();
        }
    }

    public String connectDaily(){

        String str = "http://open.iciba.com/dsapi/";
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(str);
            urlConnection = (HttpURLConnection)url.openConnection();
            urlConnection.setConnectTimeout(10000);
            urlConnection.setReadTimeout(10000);
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            String result = readInStream(in);
            return result;
            
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            urlConnection.disconnect();
        }
    }

    public String parsingJOSN(String json){
        if (json == null){
            return "网络连接超时";
        }
        String str = "";
        try {
            JSONObject jsonObject = new JSONObject(json);

            JSONArray symbols = jsonObject.getJSONArray("symbols");
            JSONObject symbol = (JSONObject)symbols.opt(0);
            if (symbol.has("ph_en")){
                ph_en = symbol.getString("ph_en");
            }
            if (symbol.has("ph_am")){
                ph_am = symbol.getString("ph_am");
            }
            ph_en_mp3 = symbol.getString("ph_en_mp3");
            ph_am_mp3 = symbol.getString("ph_am_mp3");

            JSONObject exchange = jsonObject.getJSONObject("exchange");
            String[] strings = {"复数", "第三人称单数","过去式","过去分词","现在分词","比较级","最高级"};
            Iterator iterator = exchange.keys();
            int i1 = 0;
            while (iterator.hasNext()){
                String key = (String)iterator.next();
                if (exchange.getString(key).length()!=0){
                    exchanges.put(strings[i1], (String)exchange.getJSONArray(key).opt(0));
                }
                i1++;
            }

            if (symbol.has("parts")){
                JSONArray parts = symbol.getJSONArray("parts");

                for (int i=0; i<parts.length(); i++){
                    Map<String, String> part = new HashMap<>();
                    JSONObject p = (JSONObject)parts.opt(i);
                    //partsStr += p.getString("part")+'\n';
                    part.put("part", p.getString("part"));
                    JSONArray means = p.getJSONArray("means");
                    String mean = "";
                    for (int j=0; j<means.length(); j++){
                        mean += means.opt(j) + ";";
                    }
                    part.put("means", mean);
                    partList.add(part);
                }
            }
            word = new Word(realw, ph_en, ph_am, ph_en_mp3, ph_am_mp3, partList, exchanges);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return str;
    }

    public void parsingDailyJOSN(String json){
        System.err.println("daily"+json);
        if (json == null){
            return;
        }
        try {
            JSONObject jsonObject = new JSONObject(json);

            dailyCon = jsonObject.getString("content");
            dailyNote = jsonObject.getString("note");



        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Bitmap connectToImg(){

        String str;
        HttpURLConnection urlConnection = null;
        try {
            str = "http://cdn.iciba.com/web/news/longweibo/imag/"+ Dictionary.DATE_NOW+".jpg";
            URL url = new URL(str);
            urlConnection = (HttpURLConnection)url.openConnection();
            urlConnection.setConnectTimeout(10000);
            urlConnection.setReadTimeout(10000);
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            Bitmap bitmap = BitmapFactory.decodeStream(in);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, 640, 830);
            in.close();
            return bitmap;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            urlConnection.disconnect();
        }
    }

    public Bitmap connectToSmalllImg(){

        String str;
        HttpURLConnection urlConnection = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date = new Date(System.currentTimeMillis());
            String ds = sdf.format(date);
            str = "http://cdn.iciba.com/news/word/big_"+ Dictionary.DATE_NOW+"b.jpg";
            URL url = new URL(str);
            urlConnection = (HttpURLConnection)url.openConnection();
            urlConnection.setConnectTimeout(10000);
            urlConnection.setReadTimeout(10000);
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            Bitmap bitmap = BitmapFactory.decodeStream(in);
            bitmap = Bitmap.createBitmap(bitmap);
            in.close();
            return bitmap;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            urlConnection.disconnect();
        }
    }

    private String readInStream(InputStream in) {
        Scanner scanner = new Scanner(in).useDelimiter("\\A");
        return scanner.hasNext() ? scanner.next() : "";
    }

    private String unescapeUnicode(String str){
        StringBuffer b=new StringBuffer();
        Matcher m = Pattern.compile("\\\\u([0-9a-fA-F]{4})").matcher(str);
        while(m.find())
            b.append((char)Integer.parseInt(m.group(1),16));
        return b.toString();
    }
}
