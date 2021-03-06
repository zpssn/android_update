package com.tec.zenyan.module;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.tec.zenyan.MyApplication;
import com.tec.zenyan.common.Link;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.Request;

/**
 * Created by kisss on 2016/12/28.
 */

public class OKhttpMethod {
    private String TAG = "OKhttp";
    public Context mContext;
    private String UpdataData;
    private Bitmap welcomebitmap;

    public Bitmap getwelcomeBitmap(){
        return welcomebitmap;
    }

    public  void setBitmap(Bitmap welcomeimg){
        this.welcomebitmap =  welcomeimg;
    }

    public String getUpdataData() {
        return UpdataData;
    }

    public void setUpdataData(String data) {
        this.UpdataData = data;
    }

    public void post(String url){
        OkHttpUtils
                .postString()
                .url(url)
                .content(new Gson().toJson(new Use("zhy", "123")))
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new UseCallback()
                {

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Toast.makeText(mContext,"erro",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(Use response, int id) {
                        Toast.makeText(mContext,"key:"+response.key + "value:"+response.value+"other"+response.other,Toast.LENGTH_SHORT).show();
                    }
                });
    }
    public void get(String url){

        OkHttpUtils
                .get()
                .url(url)
                .build()
                .execute(new StringCallback()
                {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.i(TAG,"RESULT"+response);
                        setUpdataData(response);
                    }
                });
    }


    public void getImgBitmap(String url){
        OkHttpUtils
                .get()//
                .url(url)//
                .build()//
                .execute(new BitmapCallback()
                {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e("onError:", "onError:"+ e.getMessage());
                    }

                    @Override
                    public void onResponse(Bitmap response, int id) {
//                        mImageView.setImageBitmap(response);
                        setBitmap(response);
                    }

                });
    }
    public int download_apk(String url){
        int progresss = 0;
        OkHttpUtils//
                .get()//
                .url(url)//
                .build()//
                .execute(new FileCallBack(Environment.getExternalStorageDirectory().getAbsolutePath(), "gson-2.2.1.jar")//
                {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(File response, int id) {

                    }

                    public void inProgress(float progress)
                    {
                        progress = ((int) (100 * progress));
                    }

                });
                return progresss;
    }
}
