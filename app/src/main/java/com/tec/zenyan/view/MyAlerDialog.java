package com.tec.zenyan.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;

import com.tec.zenyan.activity.WelcomeActivity;

/**
 * Created by kiss on 2017/1/27.
 */

public class MyAlerDialog {
    public Context conText;
    public String Message;
    public void MyAlerDialog(Context context,String message){
        conText=context;
        Message=message;
    }
    public void make_dialog(){
        new AlertDialog.Builder(conText).setTitle("系统提示")//设置对话框标题
                .setMessage(Message)//设置显示的内容
                .setPositiveButton("确定",new DialogInterface.OnClickListener() {//添加确定按钮
                    @Override
                    public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件
                        // TODO Auto-generated method stub

                    }
                }).setNegativeButton("返回",new DialogInterface.OnClickListener() {//添加返回按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {//响应事件
                // TODO Auto-generated method stub

            }

        }).show();//在按键响应事件中显示此对话框
    }
}
