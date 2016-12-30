package com.tec.zenyan.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Created by kisss on 2016/12/30.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String CREATE_Type = "create table type ("

            + "Tid text, "
            + "Name text)";
    public static final String CREATE_Brand = "create table brand ("

            + "Bid text, "
            + "Name text)";
    public static final String CREATE_Rmodel = "create table rmodel ("

            + "Rmodel text, "
            + "Rid text)";
    public static final String CREATE_Irdata = "create table irdata ("
            + "id integer primary key autoincrement, "
            + "Irtype text, "
            + "Irimage text, "
            + "Irname text, "
            + "Irzip text, "
            + "Irdata text)";
    private Context mContext;
    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory
            factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_Type);
        db.execSQL(CREATE_Brand);
        db.execSQL(CREATE_Rmodel);
        db.execSQL(CREATE_Irdata);

        Toast.makeText(mContext, "Create succeeded", Toast.LENGTH_SHORT).show(); }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }
}
