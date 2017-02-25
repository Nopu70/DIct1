package com.nopu70.fragment.home;

import android.app.Activity;
import android.app.Fragment;
import android.app.Service;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.nopu70.dict.Dictionary;
import com.nopu70.dict.R;
import com.nopu70.myview.DialogListView;
import com.nopu70.services.EbbinghausService;
import com.nopu70.tools.Word;
import com.nopu70.tools.WordArray;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by nopu70 on 15-12-21.
 */
public class ReadingFragment extends Fragment implements DialogListView.OnClickListItemListener {

    Activity activity;
    ListView waL;
    FloatingActionButton fab;
    List<String> walName = new ArrayList<>();
    ArrayAdapter adapter;
    Map<String, WordArray> list;
    String intent_action = "com.nopu70.intent.action.RWORD";

    EbbinghausService.MBinder binder;
    ServiceConnection con = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (EbbinghausService.MBinder)service;
            System.out.println("ServiceConnected");
            updateList();
            adapter = new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_1, walName);
            waL.setAdapter(adapter);

            waL.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    WordArray wordArray = list.get(walName.get(position));
                    Intent intent = new Intent();
                    intent.setAction(intent_action);
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.putExtra("waName", walName.get(position));
                    startActivity(intent);
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            System.out.println("ServiceDisconnected");
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.reading_fragment, container, false);

        waL = (ListView)view.findViewById(R.id.waL);
        fab = (FloatingActionButton)view.findViewById(R.id.fab);

        activity = getActivity();
        Intent intent = new Intent("com.nopu70.services.EBHSSERVICE");
        intent.setPackage(getActivity().getPackageName());
        activity.bindService(intent, con, Service.BIND_AUTO_CREATE);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNetDialog();
            }
        });

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        activity.unbindService(con);
    }

    public void showNetDialog(){
        new AlertDialog.Builder(activity)
                .setTitle("选择词库")
                .setView(new DialogListView(activity, this))
                .setPositiveButton("CANCEL", null)
                .setNegativeButton("OK", null)
                .create()
                .show();
    }

    @Override
    public void onClick(String dictName) {
        binder.addWordArray(new WordArray(activity, dictName));
        updateList();
        System.out.println(walName.size());
        adapter.notifyDataSetChanged();
    }

    private void updateList(){
        list = binder.getWordArrayList();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        walName.clear();
        if (list.size()!=0){
            for (Map.Entry<String, WordArray> entry : list.entrySet()){
                WordArray wa = list.get(entry.getKey());
                walName.add(sdf.format(wa.date));
            }
        }
    }
}
