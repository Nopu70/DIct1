package com.nopu70.dict;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import com.nopu70.fragment.home.DailyFragment;
import com.nopu70.fragment.home.OffdicFragment;
import com.nopu70.fragment.home.ReadingFragment;
import com.nopu70.fragment.home.SettingFragment;
import com.nopu70.fragment.home.TranslationFragment;
import com.nopu70.fragment.home.WordsBookFragment;
import com.nopu70.fragment.menu.LeftMenuActivity;
import com.nopu70.services.EbbinghausService;
import com.nopu70.tools.Trie;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.EventListener;
import java.util.Map;


/**
 * Created by nopu70 on 15-12-9.
 */

public class Dictionary extends AppCompatActivity implements LeftMenuActivity.OnMenuClickListener{

    DrawerLayout dl;
    ActionBarDrawerToggle mabdt;
    Toolbar toolbar;
    TextView tvHint;
    MediaPlayer mp;
    int INIT_APP = 123;
    public static Trie tree = new Trie();
    public static final String DATE_NOW;
    NetRequestReceiver receiver;
    static {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(System.currentTimeMillis());
        DATE_NOW = sdf.format(date);
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 123:initApp();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dictionary_home);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tvHint = (TextView)findViewById(R.id.tv_hint);
        mp = new MediaPlayer();
        receiver = new NetRequestReceiver();
        IntentFilter filter = new IntentFilter("com.nopu70.action.GET_NET_INFO");
        registerReceiver(receiver, filter);

        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        dl = (DrawerLayout) findViewById(R.id.drawer);
        mabdt = new ActionBarDrawerToggle(this, dl, toolbar, R.string.open, R.string.close);
        mabdt.syncState();
        dl.setDrawerListener(mabdt);

        TranslationFragment tf = new TranslationFragment();
        getFragmentManager().beginTransaction()
                .replace(R.id.homeFrame, tf)
                .commit();

        Animation ani = new AlphaAnimation(0f,1f);
        ani.setDuration(1500);
        ani.setRepeatMode(Animation.REVERSE);
        ani.setRepeatCount(Animation.INFINITE);
        tvHint.startAnimation(ani);

        tvHint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mp.reset();
                    mp.setDataSource("http://news.iciba.com/admin/tts/" + DATE_NOW + "-day.mp3");
                    mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            mp.start();
                        }
                    });
                    mp.prepareAsync();
                } catch (IOException e) {
                }

            }
        });

        isFirstInit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R .menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void showHomePage(int page) {
        Fragment f = null;
        switch (page){
            case 0:
                f = new TranslationFragment();
                changePage(f);
                break;
            case 1:
                f = new ReadingFragment();
                changePage(f);
                break;
            case 2:
                f = new DailyFragment();
                changePage(f);
                break;
            case 3:
                f = new WordsBookFragment();
                changePage(f);
                break;
            case 4:
                f = new OffdicFragment();
                changePage(f);
                break;
            case 5:
                f = new SettingFragment();
                changePage(f);
                break;
        }
    }
    public void changePage(Fragment f){
        getFragmentManager().beginTransaction()
                .replace(R.id.homeFrame, f)
                .commit();
        dl.closeDrawers();

    }

    public class NetRequestReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager cm = (ConnectivityManager)context.getSystemService(context.CONNECTIVITY_SERVICE);
            NetworkInfo ni = cm.getActiveNetworkInfo();
            if (ni == null || !ni.isConnectedOrConnecting()){
                showNetDialog();
            }
        }
    }

    public void showNetDialog(){
        new AlertDialog.Builder(this)
                .setTitle("需要网络")
                .setMessage("设置网络？")
                .setPositiveButton("CANCEL", null)
                .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                        startActivity(intent);
                    }
                })
                .create()
                .show();
    }

    private void isFirstInit(){
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
            int version = info.versionCode;
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
            int lastVersion = sp.getInt("V_KEY", 0);
            if (version>lastVersion){
                sp.edit().putInt("V_KEY", version).commit();
                System.out.println(version +" "+lastVersion);
                String filePath = Environment.getExternalStorageDirectory().getPath();
                File file = new File(filePath+"/DICT/DB/");
                if (!file.exists()) {
                    file.mkdirs();
                }
                file = new File(filePath+"/DICT/DB/dict.db");
                InputStream myInput;
                OutputStream myOutput = new FileOutputStream(file, false);
                myInput = this.getAssets().open("myDict.db");
                byte[] buffer = new byte[1024];
                int length = myInput.read(buffer);
                while(length > 0)
                {
                    myOutput.write(buffer, 0, length);
                    length = myInput.read(buffer);
                }

                myOutput.flush();
                myInput.close();
                myOutput.close();
            }
        }catch (PackageManager.NameNotFoundException e){
        }catch (IOException e){
            System.out.println(e.getMessage());
        }
        Message msg = new Message();
        msg.what = INIT_APP;
        handler.sendMessage(msg);
    }

    private void initApp(){
        new Thread(){
            @Override
            public void run() {
                SharedPreferences sp = getSharedPreferences("dict_list", MODE_PRIVATE);
                String sdcardPath = Environment.getExternalStorageDirectory().getPath();
                int i = 0;
                File f = new File(sdcardPath+"/DICT/DB/dict.db");
                SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(f, null);
                Cursor cursor = database.rawQuery("select * from Words", null);
                while (cursor.moveToNext()) {
                    i++;
                    tree.insert(cursor.getString(0));
                }
                cursor.close();
                database.close();
                System.out.println("tree init is done" + i);
                File file = new File(sdcardPath, "/DICT/dict/");
                if (file.exists()){
                    file.mkdirs();
                }
                File[] files = file.listFiles();
                Map<String, Integer> dl = (Map<String, Integer>)sp.getAll();
                sp.edit().clear().commit();
                if (files!=null){
                    for (File dict : files) {
                        String s = dict.getName();
                        s = s.substring(0, s.lastIndexOf("."));
                        if (dl.containsKey(s)){
                            sp.edit().putInt(s, dl.get(s)).commit();
                        }else {
                            sp.edit().putInt(s, 0).commit();
                        }
                    }
                }
                Intent intent1 = new Intent(Dictionary.this, EbbinghausService.class);
                startService(intent1);
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}
