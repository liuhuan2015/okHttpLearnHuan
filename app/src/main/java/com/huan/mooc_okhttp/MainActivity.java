package com.huan.mooc_okhttp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private TextView tv_result;
    private ImageView iv_pic;
    private static String mBaseUrl = "http://192.168.9.106/mooc_okhttp/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_result = (TextView) findViewById(R.id.tv_result);
        iv_pic = (ImageView) findViewById(R.id.iv_pic);
    }

    public void doGet(View view) {
        Api.doGet("zhangsan", "1234").enqueue(getResponseCallback());
    }

    public void doPost(View view) {
        Api.doPost("lisi", "333333").enqueue(getResponseCallback());
    }

    public void doPostString(View view) {
        Api.doPostString("{username=wangwu,password=321}").enqueue(getResponseCallback());

    }

    public void doPostFile(View view) {
        File file = new File(Environment.getExternalStorageDirectory(), "nvhai.png");
        Api.doPostFile(file).enqueue(getResponseCallback());

    }

    public void doUpLoad(View view) {
        Map<String, String> strMap = new HashMap<String, String>();
        Map<String, File> fileMap = new HashMap<String, File>();

        File file = new File(Environment.getExternalStorageDirectory(), "nvhai.png");
        if (!file.exists()) {
            L.e(file.getAbsolutePath() + " not exist!");
        } else {
            fileMap.put("mPhoto", file);
        }
        File file2 = new File(Environment.getExternalStorageDirectory(), "xiaonanhai.png");
        if (!file2.exists()) {
            L.e(file2.getAbsolutePath() + " not exist!");
        } else {
            fileMap.put("mPhotoTwo", file2);
        }

        strMap.put("username", "zhangsanfeng");
        strMap.put("password", "999999");
        Api.doUpLoad(strMap, fileMap).enqueue(getResponseCallback());
    }

    public void doDownLoad(View view) {
        Api.doDownLoad().enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                L.e("onFailure:" + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                L.e("......onResponse");
                final long total = response.body().contentLength();
                long sum = 0L;

                InputStream inputStream = response.body().byteStream();
                int len = 0;
                File file = new File(Environment.getExternalStorageDirectory(), "xiaonanhai.png");
                FileOutputStream fos = new FileOutputStream(file);
                byte[] byt = new byte[128];
                while ((len = inputStream.read(byt)) != -1) {
                    fos.write(byt, 0, len);
                    sum += len;
                    L.e(sum + "/" + total);
                    final long finalSum = sum;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv_result.setText(finalSum + "/" + total);
                        }
                    });
                }
                fos.flush();
                fos.close();
                inputStream.close();
                L.e("Download Success !");
            }
        });
    }

    public void doDownLoadImg(View view) {
        Api.doDownLoad().enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                L.e("onFailure:" + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                L.e("......onResponse");
                InputStream inputStream = response.body().byteStream();
                final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                //在这个位置，如果不确定图片的大小。我们一定要注意图片压缩问题
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        iv_pic.setImageBitmap(bitmap);
                    }
                });
                L.e("Download Success !");
            }
        });
    }

    @NonNull
    private Callback getResponseCallback() {
        return new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                L.e("onFailure:" + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //此处不处于UI线程，这样设计可能是为了支持一些大文件下载之类的操作
                final String body = response.body().string();
                L.e("onResponse:" + body);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv_result.setText(body);
                    }
                });
            }
        };
    }


}
