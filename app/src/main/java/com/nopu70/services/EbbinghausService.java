package com.nopu70.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;

import com.nopu70.dict.Dictionary;
import com.nopu70.dict.R;
import com.nopu70.tools.WordArray;
import com.nopu70.tools.WordArrayList;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by nopu70 on 16-3-8.
 */
public class EbbinghausService extends Service {

    public class MBinder extends Binder{
        public void addWordArray(WordArray array){
            list.addWordArray(array);
        }
        public void addWordArray(String name, WordArray array){
            list.addWordArray(name, array);
        }
        public Map<String, WordArray> getWordArrayList(){
            return list.getWaList();
        }
        public WordArray getWordArrayToName(String name){
            return list.getWaList().get(name);
        }
        public void startBackNotifi(){
            backNotifi();
        }
    }

    WordArrayList list = null;

    MBinder binder = new MBinder();
    Timer timer;
    Map<String, WordArray> wordArrays;
    NotificationManager notificationManager;
    NotificationCompat.Builder builder;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        list = new WordArrayList();
        saveList();

        notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        builder = new NotificationCompat.Builder(getApplicationContext());

        if (loadList()==null){
            return;
        }
        list = (WordArrayList)loadList();
        wordArrays = list.getWaList();

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        if (sp.getBoolean("setBackNotifi", true)){
            backNotifi();
        }
    }

    @Override
    public void onDestroy() {
        saveList();
        super.onDestroy();
    }

    private void saveList(){

        FileOutputStream fos;
        ObjectOutputStream oos;

        try {
            fos = openFileOutput("wordArrayList", MODE_PRIVATE);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(list);
            fos.close();
            oos.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private Object loadList(){

        FileInputStream fis;
        ObjectInputStream ois;
        Object obj = null;

        try {
            fis = openFileInput("wordArrayList");
            ois = new ObjectInputStream(fis);
            obj =  ois.readObject();
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }catch (ClassNotFoundException e){
            e.printStackTrace();
            return  null;
        }

        return obj;
    }

    private void backNotifi(){
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Date d = new Date();
                for (Map.Entry<String, WordArray> entry : wordArrays.entrySet()) {
                    WordArray wa = wordArrays.get(entry.getKey());
                    int timeSpan = wa.getTime_span();

                    if ((d.getTime()-wa.date.getTime())>timeSpan){
                        builder.setContentTitle("学习提醒")
                                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                                .setContentText("有需要学习的词汇,来自" + wa.dictName)
                                .setAutoCancel(true)
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setPriority(Notification.PRIORITY_DEFAULT)
                                .setDefaults(Notification.DEFAULT_VIBRATE)
                                .setSound(Uri.parse("android.resource://com.nopu70.dict/" + R.raw.bdspeech_recognition_success));
                        Intent intent = new Intent(EbbinghausService.this, Dictionary.class);
                        PendingIntent intent1 =
                                PendingIntent.getActivities(getApplicationContext(), 0, new Intent[]{intent}, 0);
                        builder.setContentIntent(intent1);
                        notificationManager.notify(wa._ID, builder.build());
                    }
                }
            }
        }, 0, 300000);
    }
}
