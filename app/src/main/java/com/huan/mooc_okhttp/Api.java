package com.huan.mooc_okhttp;

import android.support.v4.util.ArrayMap;

import java.io.File;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by huan on 2017/3/21.
 */

public class Api {
    static {
        //这个服务器地址是我在自己电脑上搭建的服务器的地址
        OkHttpUtils.mBaseUrl = "http://192.168.9.106/mooc_okhttp/";
    }

    public static Call doGet(String name, String password) {
        Map<String, String> params = new ArrayMap<String, String>();
        params.put("username", name);
        params.put("password", password);
        return OkHttpUtils.baseGet("login", params);
    }

    public static Call doPost(String name, String password) {
        Map<String, String> params = new ArrayMap<String, String>();
        params.put("username", name);
        params.put("password", password);
        return OkHttpUtils.basePost("login", params);
    }

    /**
     * 向服务器上传一个字符串
     *
     * @return
     */
    public static Call doPostString(String str) {
        return OkHttpUtils.doPostString("doPostString", str);
    }

    /**
     * 向服务器上传一个文件
     *
     * @return
     */
    public static Call doPostFile(File file) {
        if (!file.exists()) {
            L.e(file.getAbsolutePath() + " not exist!");
            return null;
        }
        return OkHttpUtils.doPostFile("doPostFile", file);
    }

    /**
     * 上传表单数据，可以上传多个文件
     *
     * @return
     */
    public static Call doUpLoad(Map<String, String> mapStrs, Map<String, File> mapFiles) {
        return OkHttpUtils.doUpLoad("doUpLoad", mapStrs, mapFiles);
    }

    public static Call doDownLoad() {
        String url = "http://192.168.9.106/mooc_okhttp/files/xiaonanhai.png";
        return OkHttpUtils.doDownLoad(url);
    }
}
