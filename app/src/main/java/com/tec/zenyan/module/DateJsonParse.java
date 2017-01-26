package com.tec.zenyan.module;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by kisss on 2016/12/29.
 */

public class DateJsonParse {

    public String getStringDate(String data,String getdata) throws JSONException{
        JSONObject result = new JSONObject(data);
        String getData = result.getString(getdata);
        return getData;
    }
    public int getIntDate(String data,String getdata) throws JSONException{
        JSONObject result = new JSONObject(data);
        int getData = Integer.parseInt(result.getString(getdata));
        return getData;
    }
}
