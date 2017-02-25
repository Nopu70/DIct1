package com.nopu70.myview;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nopu70.dict.Dictionary;
import com.nopu70.dict.R;
import com.nopu70.tools.ConnectUrl;
import com.nopu70.tools.NewWordDBHelper;
import com.nopu70.tools.Word;

import java.io.IOException;
import java.util.Map;

/**
 * Created by Nopu70 on 2016/1/30.
 */
public class TWView extends RelativeLayout implements View.OnClickListener{


    public static final String SEARCH = "com.nopu70.action.SEARCH";

    TextView amTxt;
    TextView emTxt;
    ImageButton amIBtn;
    ImageButton emIBtn;
    ShowResult showResult;
    LinearLayout addWord;
    LinearLayout quit;
    LinearLayout tran;
    WebView wv;
    Word wp;
    Context context;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 456: {
                    amTxt.setText("美["+wp.ph_am+"]");
                    emTxt.setText("英[" + wp.ph_en + "]");
                    amIBtn.setVisibility(VISIBLE);
                    emIBtn.setVisibility(VISIBLE);
                    String con = msg.getData().getString("word");
                    wv.loadDataWithBaseURL(null, con, "text/html", "utf-8", null);
                }

                    break;
            }
        }
    };

    public TWView(final Context context) {
        super(context);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.translation_word, this);

        amIBtn = (ImageButton)findViewById(R.id.amIBtn);
        emIBtn = (ImageButton)findViewById(R.id.emIBtn);
        wv = (WebView)findViewById(R.id.conWeb);
        addWord = (LinearLayout)findViewById(R.id.addWord);
        quit = (LinearLayout)findViewById(R.id.quit);
        tran = (LinearLayout)findViewById(R.id.tran);

        IntentFilter filter = new IntentFilter(SEARCH);
        filter.setPriority(0);
        showResult = new ShowResult();
        context.registerReceiver(showResult, filter);
        amTxt = (TextView)findViewById(R.id.amTxt);
        emTxt = (TextView)findViewById(R.id.emTxt);


        amIBtn.setOnClickListener(this);
        emIBtn.setOnClickListener(this);
        addWord.setOnClickListener(this);
        quit.setOnClickListener(this);
        tran.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.amIBtn:{
                MediaPlayer mp = new MediaPlayer();
                try {
                    mp.reset();
                    mp.setDataSource(wp.ph_am_mp3);
                    mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            mp.start();
                        }
                    });
                    mp.prepareAsync();
                }catch (IOException e){}
            }
                break;
            case R.id.emIBtn:{
                MediaPlayer mp = new MediaPlayer();
                try {
                    mp.reset();
                    mp.setDataSource(wp.ph_en_mp3);
                    mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            mp.start();
                        }
                    });
                    mp.prepareAsync();
                }catch (IOException e){}
            }
                break;
            case R.id.addWord:{
                if (wp!=null){
                    ContentValues cv = new ContentValues();
                    String str = "美["+wp.ph_am+"]"+"  "+"英["+wp.ph_en+"]"+"\n";
                    for (Map<String, String> map : wp.partList) {
                        String part = map.get("part");
                        String means = map.get("means");
                        str += part+":"+means+";";
                    }
                    cv.put("word", wp.w);
                    cv.put("detail", str);
                    NewWordDBHelper dbHelper =
                            new NewWordDBHelper(context, "words.db", null, 1);
                    SQLiteDatabase sd = dbHelper.getWritableDatabase();
                    Cursor cursor = sd.query("new_word", new String[]{"_id,word,detail"}, "word like ?",
                            new String[]{"%" + wp.w + "%"}, null, null, null, null);
                    if (cursor.getCount()==0){
                        sd.insert("new_word", null, cv);
                    }
                    cursor.close();
                }
            }
                break;
            case R.id.quit:{
                Activity activity = (Activity)context;
                activity.finish();
            }
                break;
            case R.id.tran:{
                if (MySearchView.str!=null){
                    Intent intent = new Intent("com.nopu70.action.SEARCH");
                    intent.putExtra("word", MySearchView.str);
                    context.sendOrderedBroadcast(intent, null);
                }
            }
        }
    }

    public class ShowResult extends BroadcastReceiver{
        @Override
        public void onReceive(final Context context, Intent intent) {
            final String wordIpt = intent.getStringExtra("word");
            System.out.println("Search is receivered");
            new Thread(){
                @Override
                public void run() {

                    ConnectUrl connectUrl = new ConnectUrl();
                    connectUrl.parsingJOSN(connectUrl.connect(wordIpt));
                    Word word = connectUrl.word;
                    if (word == null){
                        return;
                    }
                    StringBuilder sb = new StringBuilder();
                    sb.append("<!DOCTYPE html><head><meta chartset=\"utf-8\"></meta><link rel=\"stylesheet\" type=\"text/css\" href=\"file:///android_asset/css.css\"></head><body><ul class=\"base\">");
                    for (Map<String, String> map : word.partList){
                        String part = map.get("part");
                        String means = map.get("means");
                        sb.append("<li><span class=\"prop\">" + part + "</span><p>" + means + "</p></li>");
                    }
                    sb.append("<li class=\"change\"><span class=\"prop chinese\">变形</span><ul class=\"lp\">");
                    for (String key : word.exchange.keySet()) {
                        if (word.exchange.get(key)!=null && word.exchange.get(key)!=""){
                            sb.append("<li><span>"+key+"：</span>"+word.exchange.get(key)+"</li>");
                        }
                        System.out.println("="+word.exchange.get(key)+"=");
                    }
                    sb.append("</ul></li></ul></body>");
                    wp = word;
                    Bundle bundle = new Bundle();
                    bundle.putString("word", sb.toString());
                    Message msg = new Message();
                    msg.what = 456;
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                }
            }.start();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        context.unregisterReceiver(showResult);
    }
}
