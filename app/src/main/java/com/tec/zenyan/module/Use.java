package com.tec.zenyan.module;

/**
 * Created by kisss on 2016/12/28.
 */

public class Use {

    public String key ;
    public String value  ;
    public String other;

    public Use(String key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String toString()
    {
        return "Use{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                ", other='" + other + '\'' +
                '}';
    }
}