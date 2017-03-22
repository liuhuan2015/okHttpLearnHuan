package com.huan.mooc_okhttp;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by huan on 2017/3/22.
 */

public class OkHttpUtils {
    public static String mBaseUrl;

    private OkHttpUtils() {
    }

    //创建okHttpClinet对象，同时可以设置超时时间等
    private static OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
            .cookieJar(new CookieJar() {
                //把cookie保存到内存中，使应用在打开后访问服务器时每次Request的sessionId一致
                private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();

                @Override
                public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                    L.e("saveFromResponse url.host():" + url.host());
                    cookieStore.put(url.host(), cookies);
                }

                @Override
                public List<Cookie> loadForRequest(HttpUrl url) {
                    L.e("loadForRequest url.host():" + url.host());
                    List<Cookie> cookies = cookieStore.get(url.host());
                    return cookies != null ? cookies : new ArrayList<Cookie>();
                }
            })
            .connectTimeout(10, TimeUnit.SECONDS).build();

    /**
     * Get请求
     *
     * @param action 接口名称
     * @param map    参数
     * @return
     */
    public static Call baseGet(String action, Map<String, String> map) {
        if (mBaseUrl == null) {
            return null;
        }
        //构建Request
        Request.Builder builder = new Request.Builder();
        Request.Builder builder1 = builder.get().url(mBaseUrl);
        if (map != null) {
            //拼接出最终的get请求地址
            StringBuilder mUrl = new StringBuilder(mBaseUrl).append(action + "?");
            for (Map.Entry<String, String> entry : map.entrySet()) {
                mUrl.append("&" + entry.getKey() + "=" + entry.getValue());
            }
            builder1 = builder.get().url(String.valueOf(mUrl));
        }
        Request request = builder1.build();
        //将Request封装为Call
        Call call = okHttpClient.newCall(request);
        return call;
    }

    /**
     * 基本的post请求，参数全部是String
     *
     * @param action 接口名称
     * @param map    存放请求体，其中的参数值为String类型
     * @return
     */
    public static Call basePost(String action, Map<String, String> map) {
        //构建post请求体内容
        FormBody.Builder formBody = new FormBody.Builder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            formBody.add(entry.getKey(), entry.getValue());
        }
        FormBody body = formBody.build();
        //构建Request
        Request.Builder url = new Request.Builder().url(mBaseUrl + action).post(body);
        Request request = url.build();
        //将Request封装为Call
        Call call = okHttpClient.newCall(request);
        return call;
    }


    /**
     * 向服务器上传一个字符串，比如说一个Json串
     *
     * @param action
     * @param str
     * @return
     */
    public static Call doPostString(String action, String str) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain,charset=utf-8"), str);
        //构建Request
        Request.Builder url = new Request.Builder().url(mBaseUrl + action).post(requestBody);
        Request request = url.build();
        //将Request封装为Call
        Call call = okHttpClient.newCall(request);
        return call;
    }

    /**
     * 向服务器上传一个文件
     *
     * @param action
     * @param file
     * @return
     */
    public static Call doPostFile(String action, File file) {
        if (!file.exists()) {
            L.e(file.getAbsolutePath() + " not exist!");
            return null;
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);

        //构建Request
        Request.Builder url = new Request.Builder().url(mBaseUrl + action).post(requestBody);
        Request request = url.build();
        //将Request封装为Call
        Call call = okHttpClient.newCall(request);
        return call;
    }

    /**
     * 向服务器上传表单数据，带进度显示
     *
     * @param action
     * @param mapStrs  字符串类型的参数
     * @param mapFiles 文件类型的参数
     * @return
     */
    public static Call doUpLoad(String action, Map<String, String> mapStrs, Map<String, File> mapFiles) {

        if (mapFiles.size() == 0) {
            L.e(" Not have files");
        }

        MultipartBody.Builder bodyBuilder = new MultipartBody
                .Builder()
                .setType(MultipartBody.FORM);
        if (mapStrs.size() > 0) {
            for (Map.Entry<String, String> entry : mapStrs.entrySet()) {
                bodyBuilder.addFormDataPart(entry.getKey(), entry.getValue());
            }
        }
        if (mapFiles.size() > 0) {
            for (Map.Entry<String, File> entry : mapFiles.entrySet()) {
                bodyBuilder.addFormDataPart(entry.getKey(), entry.getKey()+".png", RequestBody.create(MediaType.parse("application/octet-stream"), entry.getValue()));
            }
        }
        MultipartBody multipartBody = bodyBuilder.build();

        CountingRequestBody countingRequestBody = new CountingRequestBody(multipartBody, new CountingRequestBody.Listener() {
            @Override
            public void onRequestProgress(long byteWrited, long contentLength) {
                L.e(byteWrited + "/" + contentLength);
            }
        });
        //构建Request
        Request.Builder builder = new Request.Builder().url(mBaseUrl + action).post(countingRequestBody);
        Request request = builder.build();
        //将Request封装为Call
        Call call = okHttpClient.newCall(request);
        return call;
    }


    /**
     * 下载一个文件，带进度显示
     *
     * @param url
     * @return
     */
    public static Call doDownLoad(String url) {
        //构建Request
        Request.Builder builder = new Request.Builder().get().url(url);
        Call call = okHttpClient.newCall(builder.build());
        return call;
    }


}
