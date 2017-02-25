package com.nopu70.dict;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.nopu70.services.EbbinghausService;
import com.nopu70.tools.WordArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by nopu70 on 16-3-9.
 */
public class RwordActivity extends AppCompatActivity {

    EbbinghausService.MBinder binder;
    String waName;
    ListView lv;
    ArrayAdapter adapter;
    List<String> ss;
    View footerView;
    WordArray wa;


    ServiceConnection con = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (EbbinghausService.MBinder)service;
            wa = binder.getWordArrayToName(waName);
            Map<String, String> array = wa.getWordArray();

            for (Map.Entry<String, String> entry : array.entrySet()){
                ss.add(entry.getKey().toString()+"\n"+array.get(entry.getKey()));
                adapter.notifyDataSetChanged();
            }
            unbindService(con);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    ServiceConnection con1 = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (EbbinghausService.MBinder)service;
            binder.addWordArray(waName, wa);
            wa.rTime();
            unbindService(con1);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rword_activity);

        lv = (ListView)findViewById(R.id.wordsList);
        footerView = LayoutInflater.from(this).inflate(R.layout.listfoot, null);
        ss = new ArrayList<>();
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, ss);
        lv.addFooterView(footerView);
        lv.setAdapter(adapter);

        Intent intent1 = getIntent();
        waName = intent1.getStringExtra("waName");

        Intent intent = new Intent("com.nopu70.services.EBHSSERVICE");
        intent.setPackage(getPackageName());
        bindService(intent, con, Service.BIND_AUTO_CREATE);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == ss.size()){
                    Intent intent = new Intent("com.nopu70.services.EBHSSERVICE");
                    intent.setPackage(getPackageName());
                    bindService(intent, con1, Service.BIND_AUTO_CREATE);
                    finish();
                }
            }
        });
    }
}
