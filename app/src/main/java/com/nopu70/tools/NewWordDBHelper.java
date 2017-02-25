package com.nopu70.tools;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by nopu70 on 16-3-1.
 */
public class NewWordDBHelper extends SQLiteOpenHelper {

    final String CREATE_TABLE = "create table new_word(_id integer primary " +
            "key autoincrement , word , detail)";

    public NewWordDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        System.out.println("-------onupdate called-------" +
                oldVersion + "--->" + newVersion);
    }
}
