package com.tec.zenyan.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.tec.zenyan.MainActivity;
import com.tec.zenyan.R;
import com.tec.zenyan.db.DatabaseHelper;

/**
 * Created by kiss on 2017/1/26.
 */

public class WelcomeActivity extends Activity{
    private DatabaseHelper dbHelper;
    private String now_image_version;
    private LinearLayout background;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new DatabaseHelper(this, "Zenyanimage.db", null, 1);
        dbHelper.getWritableDatabase();
        init();
    }
    private void init(){
        SharedPreferences setting = getSharedPreferences("SHARE_APP_TAG", 0);
        Boolean user_first = setting.getBoolean("FIRST",true);
        if(user_first){//第一次
            toActivity();
            now_image_version=null;
        }else{
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.welcome);
            background = (LinearLayout)findViewById(R.id.welcome_background);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            Bitmap bmpout = null;
            Cursor cursor = db.rawQuery("select * from welcom_image", null);
            if (cursor.moveToFirst()) {
                do {
                    byte[] in = cursor.getBlob(cursor.getColumnIndex("image"));
                    bmpout = BitmapFactory.decodeByteArray(in, 0, in.length);
                    now_image_version = cursor.getString(cursor.
                            getColumnIndex("version"));
                } while (cursor.moveToNext());
            }
            cursor.close();
            BitmapDrawable bd=new BitmapDrawable(bmpout);
            background.setBackground(bd);
            new Handler().postDelayed(new Runnable()
            {
                public void run()
                {
                    toActivity();
                }
            }, 3000);//停3秒
        }
    }
    private void toActivity(){
        Intent intent = new Intent();
        intent.putExtra("now_image_version",now_image_version);
        intent.setClass(WelcomeActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
