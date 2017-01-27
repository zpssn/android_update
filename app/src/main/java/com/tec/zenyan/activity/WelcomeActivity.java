package com.tec.zenyan.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
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
        if(!isNetworkAvailable(this)){
            make_dialog();
        }else{
            Intent intent = new Intent();
            intent.putExtra("now_image_version",now_image_version);
            intent.setClass(WelcomeActivity.this, WebActivity.class);
//        intent.setClass(WelcomeActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
    private static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected())
            {
                // 当前网络是连接的
                if (info.getState() == NetworkInfo.State.CONNECTED)
                {
                    // 当前所连接的网络可用
                    return true;
                }
            }
        }
        return false;
    }
    private void make_dialog(){
        new AlertDialog.Builder(WelcomeActivity.this).setTitle("系统提示")//设置对话框标题
            .setMessage("当前网络不可用，请检测网络连接！")//设置显示的内容
            .setPositiveButton("确定",new DialogInterface.OnClickListener() {//添加确定按钮
                @Override
                public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件
                    // TODO Auto-generated method stub
                    Intent intent =  new Intent(Settings.ACTION_SETTINGS);
                    startActivity(intent);
                    finish();
                }
            }).setNegativeButton("返回",new DialogInterface.OnClickListener() {//添加返回按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {//响应事件
                // TODO Auto-generated method stub
                finish();
            }

        }).show();//在按键响应事件中显示此对话框
    }
}
