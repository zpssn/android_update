package com.tec.zenyan;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
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
    private Link mLink;
    private String result;
    private UpdateInfo mUpdateInfo;
    private DateJsonParse mDateJsonParse;
    private int new_version;
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
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        //1.注册下载广播接收器
        DownloadApk.registerBroadcast(this);
        //2.删除已存在的Apk
        DownloadApk.removeFile(this);

        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
//                do{
//                    result = mOKhttpMethod.getUpdataData();
//                } while (result==null);
//                try {
//                    mUpdateInfo.setVersion(mDateJsonParse.getIntDate(result,"version"));
//                    mUpdateInfo.setUpdates(mDateJsonParse.getStringDate(result,"updates"));
//                    mUpdateInfo.setUrl(mDateJsonParse.getStringDate(result,"url"));
//                    new_version = mUpdateInfo.getVersion();
//                    Updates = mUpdateInfo.getUpdates();
//                    Url = mUpdateInfo.getUrl();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, ShowAppMessageActivity.class);
                startActivity(intent);
                now_version=AppMessage.getVersionCode(MainActivity.this);
                Log.i("Main","result"+now_version);

                //3.如果手机已经启动下载程序，执行downloadApk。否则跳转到设置界面
//                if (DownLoadUtils.getInstance(getApplicationContext()).canDownload()) {
//                    DownloadApk.downloadApk(getApplicationContext(), "http://qn-apk.wdjcdn.com/4/d9/c97e8081178f12868e16334b62729d94.apk", "Hobbees更新", "Hobbees");
//                } else {
//                    DownLoadUtils.getInstance(getApplicationContext()).skipToDownloadManager();
//                }
            }
        });
    }

    @Override
    protected void onDestroy() {

        //4.反注册广播接收器
        DownloadApk.unregisterBroadcast(this);
        super.onDestroy();
    }
}
