package com.tec.zenyan;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.tec.zenyan.activity.DownLoadUtils;
import com.tec.zenyan.activity.DownloadApk;
import com.tec.zenyan.activity.ShowAppMessageActivity;
import com.tec.zenyan.common.AppMessage;
import com.tec.zenyan.common.Link;
import com.tec.zenyan.module.DateJsonParse;
import com.tec.zenyan.module.OKhttpMethod;
import com.tec.zenyan.module.UpdateInfo;

import org.json.JSONException;


public class MainActivity extends AppCompatActivity {

    private OKhttpMethod mOKhttpMethod;
    private String TAG = "MainActivity";
    private Link mLink;
    private String result;
    private UpdateInfo mUpdateInfo;
    private DateJsonParse mDateJsonParse;
    private int new_version=0;
    private int now_version;
    private String Updates;
    private String Url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDateJsonParse = new DateJsonParse();
        mOKhttpMethod = new OKhttpMethod();
        mLink = new Link();
        mUpdateInfo = new UpdateInfo();
        mOKhttpMethod.get(mLink.Constent_url);
        mOKhttpMethod.getImgBitmap(mLink.getBitmap_url);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        //1.注册下载广播接收器
        DownloadApk.registerBroadcast(this);
        //2.删除已存在的Apk
        DownloadApk.removeFile(this);
        SharedPreferences setting = getSharedPreferences("SHARE_APP_TAG", 0);
        Boolean user_first = setting.getBoolean("FIRST",true);
        if(user_first){//第一次
            set_dbimage();
        }else{

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
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, ShowAppMessageActivity.class);
                startActivity(intent);
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
        if(true){
            setting.edit().putBoolean("FIRST", false).commit();
        }else{
            setting.edit().putBoolean("FIRST", true).commit();
        }

    }
    private void check_update(){
        result = mOKhttpMethod.getUpdataData();
        if(result==null){

        }else{
            try {
                mUpdateInfo.setVersion(mDateJsonParse.getIntDate(result,"version"));
                mUpdateInfo.setUpdates(mDateJsonParse.getStringDate(result,"updates"));
                mUpdateInfo.setUrl(mDateJsonParse.getStringDate(result,"url"));
                new_version = mUpdateInfo.getVersion();
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
    }
}
