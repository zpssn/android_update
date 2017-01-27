package com.tec.zenyan.module;

/**
 * Created by kisss on 2016/12/28.
 */

public class UpdateInfo {

    private int version;

    private String updates;

    private String url;

    private String imageversion;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
    public String getWelcome_ImageVersion(){
        return imageversion;
    }
    public void setWelcome_ImageVersion(String version){
        this.imageversion = version;
    }

    public String getUpdates() {
        return updates;
    }

    public void setUpdates(String updates) {
        this.updates = updates;
    }

}

