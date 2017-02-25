package com.nopu70.fragment.home;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.nopu70.dict.R;
import com.nopu70.tools.ConnectUrl;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by nopu70 on 15-12-21.
 */
public class DailyFragment extends Fragment {

    private View view;
    TextView dateT, note, con;
    ImageView picture;
    Bitmap bmp;
    ImageButton iBtn;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 234: {
                    Bundle bundle = msg.getData();
                    note.setText(bundle.getString("dCon"));
                    iBtn.setVisibility(View.VISIBLE);
                    con.setText(bundle.getString("dNote"));
                }
                    break;
                case 345:
                    picture.setImageBitmap(bmp);
            }
        }
    };


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.daily_fragment, container, false);
        dateT = (TextView)view.findViewById(R.id.date);
        note = (TextView)view.findViewById(R.id.note);
        con = (TextView)view.findViewById(R.id.con);
        picture = (ImageView)view.findViewById(R.id.picture);
        iBtn = (ImageButton)view.findViewById(R.id.dailyIBtn);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(System.currentTimeMillis());
        String ds = sdf.format(date);

        iBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaPlayer mp = new MediaPlayer();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date date = new Date(System.currentTimeMillis());
                String ds = sdf.format(date);
                try {
                    mp.reset();
                    mp.setDataSource("http://news.iciba.com/admin/tts/" + ds + "-day.mp3");
                    mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            mp.start();
                        }
                    });
                    mp.prepareAsync();
                }catch (IOException e){}
            }
        });

        dateT.setText(ds);
        new Thread(){
            @Override
            public void run() {
                ConnectUrl connectUrl = new ConnectUrl();
                connectUrl.parsingDailyJOSN(connectUrl.connectDaily());
                Bundle bundle = new Bundle();
                bundle.putString("dCon", connectUrl.dailyCon);
                bundle.putString("dNote", connectUrl.dailyNote);
                Message msg = new Message();
                msg.setData(bundle);
                msg.what = 234;
                handler.sendMessage(msg);
            }
        }.start();

        new Thread(){
            @Override
            public void run() {
                ConnectUrl connectUrl = new ConnectUrl();
                bmp = connectUrl.connectToSmalllImg();
                Message msg = new Message();
                msg.what = 345;
                handler.sendMessage(msg);
            }
        }.start();

        return view;
    }
}
