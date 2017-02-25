package com.nopu70.fragment.home;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nopu70.dict.R;
import com.nopu70.myadapter.DictsAdapter;
import com.nopu70.myadapter.DictsAdapter.OnRecyclerViewListener;
import com.nopu70.myview.DialogListView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by nopu70 on 15-12-21.
 */
public class OffdicFragment extends Fragment implements OnRecyclerViewListener{

    List dictList;
    RecyclerView dictsList;
    DictsAdapter adapter;
    Context context;
    SharedPreferences sp;
    int pos;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = (Context)activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.offdic_fragment, container, false);
        dictsList = (RecyclerView)view.findViewById(R.id.dictList);
        sp = context.getSharedPreferences("dict_list", context.MODE_PRIVATE);

        Map<String, Integer> dict_list = (Map<String, Integer>)sp.getAll();
        if (dict_list!=null){
            dictList = new ArrayList<String>();
        }
        for (Map.Entry<String, Integer> entry : dict_list.entrySet()) {
            dictList.add(entry.getKey().toString());
        }

        adapter = new DictsAdapter(dictList);
        adapter.setOnRecyclerViewListener(this);
        dictsList.setHasFixedSize(true);
        RecyclerView.LayoutManager manager =
                new LinearLayoutManager(context);
        dictsList.setLayoutManager(manager);
        dictsList.setAdapter(adapter);

        return view;
    }

    @Override
    public void onItemClick(final int position) {
        this.pos = position;
        final String dName = (String)dictList.get(pos);
        new AlertDialog.Builder(context)
                .setTitle("词库")
                .setMessage("如何操作词库？")
                .setPositiveButton("取消", null)
                .setNeutralButton("重置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sp.edit().putInt(dName, 1);
                    }
                })
                .setNegativeButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        adapter.notifyItemRemoved(pos);
                        dictList.remove(pos);
                        adapter.notifyItemRangeChanged(pos, adapter.getItemCount());
                        sp.edit().remove(dName);
                        String str = Environment.getExternalStorageDirectory().getPath().toString();
                        File file = new File(str+"/DICT/dict/"+dName+".db");
                        if (file!=null){
                            file.delete();
                        }
                    }
                })
                .create()
                .show();
    }

    @Override
    public boolean onItemLongClick(int position) {
        return false;
    }
}
