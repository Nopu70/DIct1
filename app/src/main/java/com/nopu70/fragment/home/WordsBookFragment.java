package com.nopu70.fragment.home;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nopu70.dict.R;
import com.nopu70.myadapter.WorldsAdapter;
import com.nopu70.tools.NewWordDBHelper;
import com.nopu70.tools.Word;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by nopu70 on 15-12-21.
 */
public class WordsBookFragment extends Fragment implements WorldsAdapter.OnRecyclerViewListener{

    RecyclerView wordRList;
    WorldsAdapter adapter;
    List<Map<String, String>> words;
    NewWordDBHelper nwdh;

    Context context;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.context = (Context)activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.wordsbook_fragment, container, false);
        wordRList = (RecyclerView)view.findViewById(R.id.wordRList);

        words = new ArrayList<>();
        nwdh = new NewWordDBHelper(context, "words.db", null, 1);
        SQLiteDatabase sd = nwdh.getReadableDatabase();
        Cursor cursor = sd.rawQuery("select * from new_word", null);
        while (cursor.moveToNext()){
            Map<String, String> map = new HashMap<>();
            map.put("word", cursor.getString(1));
            map.put("detail", cursor.getString(2));
            words.add(map);
        }
        cursor.close();
        sd.close();

        adapter = new WorldsAdapter(words);
        adapter.setOnRecyclerViewListener(this);
        wordRList.setHasFixedSize(true);
        RecyclerView.LayoutManager manager =
                new LinearLayoutManager(context);
        wordRList.setLayoutManager(manager);
        wordRList.setAdapter(adapter);
        return  view;
    }

    @Override
    public void onItemClick(int position) {

    }

    @Override
    public boolean onItemLongClick(int position) {
        SQLiteDatabase sd = nwdh.getWritableDatabase();
        adapter.notifyItemRemoved(position);
        sd.execSQL("DELETE FROM new_word WHERE WORD LIKE '"+words.get(position).get("word")+"'");
        words.remove(position);
        adapter.notifyItemRangeChanged(position, adapter.getItemCount());
        sd.close();
        return false;
    }
}
