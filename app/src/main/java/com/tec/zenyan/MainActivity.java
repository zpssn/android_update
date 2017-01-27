package com.tec.zenyan;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.tec.zenyan.activity.DownLoadUtils;
import com.tec.zenyan.activity.DownloadApk;
import com.tec.zenyan.common.AppMessage;
import com.tec.zenyan.common.Link;
import com.tec.zenyan.db.DatabaseHelper;
import com.tec.zenyan.module.DateJsonParse;
import com.tec.zenyan.module.OKhttpMethod;
import com.tec.zenyan.module.UpdateInfo;

import org.json.JSONException;

import java.io.ByteArrayOutputStream;


public class MainActivity extends AppCompatActivity {

    private OKhttpMethod mOKhttpMethod;
    private String TAG = "MainActivity";
    private DatabaseHelper dbHelper;
    private Bitmap bitmap_result;
    private Link mLink;
    private String result;
    private UpdateInfo mUpdateInfo;
    private DateJsonParse mDateJsonParse;
    private int new_version=0;
    private String new_image_version;
    private int now_version;
    private String Updates;
    private String Url;
    private ImageView welcome_image;
    private String now_image_version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        now_image_version = getIntent().getStringExtra("now_image_version");
        mDateJsonParse = new DateJsonParse();
        mOKhttpMethod = new OKhttpMethod();
        mLink = new Link();
        mUpdateInfo = new UpdateInfo();
        mOKhttpMethod.get(mLink.Constent_url);
        setContentView(R.layout.activity_main);
        welcome_image = (ImageView) findViewById(R.id.welcom_image);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        dbHelper = new DatabaseHelper(this, "Zenyanimage.db", null, 1);
        dbHelper.getWritableDatabase();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        //1.注册下载广播接收器
        DownloadApk.registerBroadcast(this);
        //2.删除已存在的Apk
        DownloadApk.removeFile(this);
        SharedPreferences setting = getSharedPreferences("SHARE_APP_TAG", 0);
        Boolean user_first = setting.getBoolean("FIRST",true);
        if(now_image_version==null){
            mOKhttpMethod.getImgBitmap(mLink.getBitmap_url);
            new Handler().postDelayed(new Runnable()
            {
                public void run()
                {
                    set_dbimage();
                }
            }, 2000);//网络延时检测操作
        }

        new Handler().postDelayed(new Runnable()
        {
            public void run()
            {
                check_update();
            }
        }, 1000);//网络延时检测操作

        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                SQLiteDatabase db = dbHelper.getWritableDatabase();
                Bitmap bmpout = null;
                Cursor cursor = db.rawQuery("select * from welcom_image", null);
                if (cursor.moveToFirst()) {
                    do {
                        byte[] in = cursor.getBlob(cursor.getColumnIndex("image"));
                        bmpout = BitmapFactory.decodeByteArray(in, 0, in.length);
                        Log.i(TAG,"db1:"+in);
                        Log.i(TAG,"db2:"+bmpout);
                    } while (cursor.moveToNext());
                }
                cursor.close();
                Log.i(TAG,"db3:"+bmpout);
                welcome_image.setImageBitmap(bmpout);
//                Intent intent = new Intent();
//                intent.setClass(MainActivity.this, ShowAppMessageActivity.class);
//                startActivity(intent);
            }
        });
    }


    @Override
    protected void onDestroy() {

        //4.反注册广播接收器
        DownloadApk.unregisterBroadcast(this);
        super.onDestroy();
    }
    private void set_dbimage(){
        SharedPreferences setting = getSharedPreferences("SHARE_APP_TAG", 0);
        Boolean user_first = setting.getBoolean("FIRST",true);
        bitmap_result = mOKhttpMethod.getwelcomeBitmap();
        if(bitmap_result==null){
            if(user_first){
                setting.edit().putBoolean("FIRST", true).commit();
            }
            Log.i(TAG,"null");
        }else{
            setting.edit().putBoolean("FIRST", false).commit();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap_result.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            insert(byteArray);
            Log.i(TAG,"null:"+byteArray);
            bitmap_result.recycle();
        }
    }

    public long insert(byte[] img)
    {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("DELETE FROM welcom_image;");
        ContentValues cv = new ContentValues();
        cv.put("image", img);
        cv.put("version",new_image_version);
        long result = db.insert("welcom_image", null, cv);
        cv.clear();
        return result;
    }
    private void check_update(){
        result = mOKhttpMethod.getUpdataData();
        if(result==null){

        }else{
            try {
                mUpdateInfo.setWelcome_ImageVersion(mDateJsonParse.getStringDate(result,"image_version"));
                mUpdateInfo.setVersion(mDateJsonParse.getIntDate(result,"version"));
                mUpdateInfo.setUpdates(mDateJsonParse.getStringDate(result,"updates"));
                mUpdateInfo.setUrl(mDateJsonParse.getStringDate(result,"url"));
                new_version = mUpdateInfo.getVersion();
                new_image_version = mUpdateInfo.getWelcome_ImageVersion();
                Updates = mUpdateInfo.getUpdates();
                Url = mUpdateInfo.getUrl();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Log.i(TAG,"result:"+result);
        now_version=AppMessage.getVersionCode(MainActivity.this);
        if(now_version<new_version){
            new AlertDialog.Builder(MainActivity.this).setTitle("系统提示")//设置对话框标题
                    .setMessage("检测到新版本，请升级！"+Updates)//设置显示的内容
                    .setPositiveButton("确定",new DialogInterface.OnClickListener() {//添加确定按钮
                        @Override
                        public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件
                            // TODO Auto-generated method stub
                            if (DownLoadUtils.getInstance(getApplicationContext()).canDownload()) {
                                DownloadApk.downloadApk(getApplicationContext(),Url , "更新中。。。", "APP后台提示");
                            } else{
                                DownLoadUtils.getInstance(getApplicationContext()).skipToDownloadManager();
                            }
                        }
                    }).setNegativeButton("返回",new DialogInterface.OnClickListener() {//添加返回按钮
                @Override
                public void onClick(DialogInterface dialog, int which) {//响应事件
                    // TODO Auto-generated method stub
                    Log.i("alertdialog"," 请保存数据！");
                }
            }).show();//在按键响应事件中显示此对话框
        }
        if(now_image_version==null){

        }else{
            if(!now_image_version.equals(new_image_version)){
                mOKhttpMethod.getImgBitmap(mLink.getBitmap_url);
                new Handler().postDelayed(new Runnable()
                {
                    public void run()
                    {
                        set_dbimage();
                    }
                }, 1000);//网络延时检测操作
            }
        }

    }
}
