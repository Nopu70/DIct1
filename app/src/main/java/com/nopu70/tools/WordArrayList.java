package com.nopu70.tools;

import android.content.Context;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by nopu70 on 16-3-8.
 */
public class WordArrayList implements Serializable{

    Map<String, WordArray> waList;

    public WordArrayList(){
        waList = new HashMap<>();
    }

    public void addWordArray(WordArray array){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        array._ID = waList.size();
        waList.put(sdf.format(array.date), array);
    }

    public void addWordArray(String name, WordArray array){
        waList.put(name, array);
    }

    public Map<String, WordArray> getWaList(){
        return waList;
    }
}
