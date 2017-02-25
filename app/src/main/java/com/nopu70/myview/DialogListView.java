package com.nopu70.myview;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.nopu70.dict.R;
import com.nopu70.fragment.home.ReadingFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by nopu70 on 16-3-8.
 */
public class DialogListView extends LinearLayout {

    public interface OnClickListItemListener{
        public void onClick(String dictName);
    }

    ListView listView;
    List<String> dictList;
    OnClickListItemListener ocil;

    public DialogListView(Context context) {
        super(context);
    }

    public DialogListView(Context context, ReadingFragment readingFragment){
        super(context);
        LayoutInflater.from(context).inflate(R.layout.dialog_listview, this);

        listView = (ListView)findViewById(R.id.dialog_lv);
        SharedPreferences sp = context.getSharedPreferences("dict_list", context.MODE_PRIVATE);

        Map<String, Integer> dict_list = (Map<String, Integer>)sp.getAll();
        if (dict_list!=null){
            dictList = new ArrayList<String>();
        }
        for (Map.Entry<String, Integer> entry : dict_list.entrySet()) {
            dictList.add(entry.getKey().toString());
        }

        ArrayAdapter adapter = new ArrayAdapter(context, android.R.layout.simple_list_item_1, dictList);
        listView.setAdapter(adapter);
        ocil = (OnClickListItemListener)readingFragment;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ocil.onClick(dictList.get(position));
            }
        });
    }
}
