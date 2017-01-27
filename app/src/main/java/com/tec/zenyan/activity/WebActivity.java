package com.tec.zenyan.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

import com.tec.zenyan.R;
import com.tec.zenyan.common.AppMessage;
import com.tec.zenyan.common.Link;
import com.tec.zenyan.db.DatabaseHelper;
import com.tec.zenyan.module.DateJsonParse;
import com.tec.zenyan.module.OKhttpMethod;
import com.tec.zenyan.module.UpdateInfo;
import com.tec.zenyan.view.MyAlerDialog;

import org.json.JSONException;

import java.io.ByteArrayOutputStream;

/**
 * Created by kiss on 2017/1/27.
 */

public class WebActivity extends Activity {
    private OKhttpMethod mOKhttpMethod;
    private String TAG = "WebActivity";
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
    private String now_image_version;
    private WebView webView;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        now_image_version = getIntent().getStringExtra("now_image_version");
        mDateJsonParse = new DateJsonParse();
        mOKhttpMethod = new OKhttpMethod();
        mLink = new Link();
        mUpdateInfo = new UpdateInfo();
        mOKhttpMethod.get(mLink.Constent_url);
        setContentView(R.layout.web_activity);
        webView = (WebView) findViewById(R.id.webView);
        dbHelper = new DatabaseHelper(this, "Zenyanimage.db", null, 1);
        dbHelper.getWritableDatabase();
        //1.注册下载广播接收器
        DownloadApk.registerBroadcast(this);
        //2.删除已存在的Apk
        DownloadApk.removeFile(this);
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
        setlistener();
    }
    private void setlistener(){
        webView.loadUrl(mLink.main_url);
        dialog = ProgressDialog.show(this,null,"页面加载中，请稍后..");
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url);
                return true;
            }
            @Override
            public void onPageFinished(WebView view,String url)
            {
                dialog.dismiss();
            }
            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                // TODO Auto-generated method stub
                super.onReceivedError(view, errorCode, description, failingUrl);
                make_dialog();
            }
        });
        webView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {  //表示按返回键
                        webView.goBack();   //后退
                        //webview.goForward();//前进
                        return true;    //已处理
                    }
                }
                return false;
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
    private void make_dialog(){
        new android.app.AlertDialog.Builder(WebActivity.this).setTitle("系统提示")//设置对话框标题
                .setMessage("加载失败，请检测网络稍后再试或致电客服！")//设置显示的内容
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
        now_version= AppMessage.getVersionCode(WebActivity.this);
        if(now_version<new_version){
            new AlertDialog.Builder(WebActivity.this).setTitle("系统提示")//设置对话框标题
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
