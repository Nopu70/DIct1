package com.nopu70.myview;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.nopu70.dict.R;
import com.nopu70.tools.ConTs;
import com.rey.material.widget.Spinner;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Handler;

/**
 * Created by Nopu70 on 2016/1/30.
 */
public class TSView extends RelativeLayout {

    EditText sentence;
    TextView rtSentence;
    Button btnTs;
    android.widget.Spinner sSrc, sDect;

    String strSrc="auto", strDict="auto";
    String[] strs = {"auto", "zh", "en", "jp", "kor", "spa", "fra", "th", "ara", "ru", "pt",
            "de", "it", "nl", "el"};

    android.os.Handler handler = new android.os.Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case  398:{
                    Bundle b = msg.getData();
                    rtSentence.setText(b.getString("result"));
                }
            }
        }
    };

    public TSView(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.translation_sentence, this);

        sentence = (EditText)findViewById(R.id.sentence);
        rtSentence = (TextView)findViewById(R.id.rtSentence);
        btnTs = (Button)findViewById(R.id.btnTs);
        sSrc = (android.widget.Spinner)findViewById(R.id.sSrc);
        sDect = (android.widget.Spinner)findViewById(R.id.sDect);

        sSrc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                strSrc = strs[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        sDect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                strDict = strs[position];
                System.out.println(strDict);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnTs.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    @Override
                    public void run() {
                        String str = sentence.getText().toString();
                        if (str.length()!=0){
                            ConTs conTs = new ConTs();
                            str = conTs.connectTs(str, strSrc, strDict);
                            str = conTs.parsingConTsJOSN(str);
                        }
                        Message msg = new Message();
                        msg.what = 398;
                        Bundle bundle = new Bundle();
                        bundle.putString("result", str);
                        msg.setData(bundle);
                        handler.sendMessage(msg);
                    }
                }.start();
            }
        });
    }


}
