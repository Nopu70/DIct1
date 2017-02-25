package com.nopu70.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import java.io.File;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by nopu70 on 16-3-8.
 */
public class WordArray {

    Context context;
    public int _ID = 0;
    int index = 1;
    int[] time_span = new int[7];
    public int cont  = 0;
    public String dictName;
    public Date date;
    Map<String, String> wordArray;

    public WordArray(Context context, String dictName){
        this.context = context;
        this.dictName = dictName;
        SharedPreferences sp = context.getSharedPreferences(dictName, context.MODE_PRIVATE);
        this.index = sp.getInt(dictName, 1);
        this.date = new Date();
        time_span[0] = 1000 * 60 * 20;
        time_span[1] = 1000 * 60 * 60;
        time_span[2] = 1000 * 60 * 60 * 8;
        time_span[3] = 1000 * 60 * 60 * 24;
        time_span[4] = 1000 * 60 * 60 * 48;
        time_span[5] = 1000 * 60 * 60 * 24 * 8;
        time_span[6] = 1000 * 60 * 60 * 24 * 30;
        setWordArray();
        sp.edit().putInt(dictName, index).commit();
    }

    public Map<String, String> getWordArray(){
        return wordArray;
    }

    public void setWordArray(){
        wordArray = new HashMap<>();
        File dict_file =
                new File(Environment.getExternalStorageDirectory().getPath()+"/DICT/dict/"+dictName+".db");
        if (dict_file!=null){

            SQLiteDatabase sd = SQLiteDatabase.openOrCreateDatabase(dict_file, null);
            int m = index+20;
            for (int i = index; i<m; i++){
                Cursor cursor = sd.rawQuery("SELECT * FROM new_word WHERE _id=?",new String[]{String.valueOf(index)});
                cursor.moveToNext();
                wordArray.put(cursor.getString(1), cursor.getString(2));
                cursor.close();
                index++;
            }
            sd.close();
        }
    }

    public void rTime(){
        cont++;
    }

    public int getTime_span(){
        return time_span[cont];
    }
}
