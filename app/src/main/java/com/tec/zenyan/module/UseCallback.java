package com.tec.zenyan.module;

/**
 * Created by kisss on 2016/12/28.
 */

import com.google.gson.Gson;

import com.zhy.http.okhttp.callback.Callback;


import java.io.IOException;

import okhttp3.Response;

/**
 * Created by zhy on 15/12/14.
 */
public abstract class UseCallback extends Callback<Use>
{
    @Override
    public Use parseNetworkResponse(Response response, int id) throws IOException
    {
        String string = response.body().string();
        Use use = new Gson().fromJson(string, Use.class);
        return use;
    }

}
