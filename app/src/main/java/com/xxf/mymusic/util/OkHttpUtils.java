package com.xxf.mymusic.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Property;

import com.xxf.mymusic.i.OnOkHttpListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * author：xxf
 */
public class OkHttpUtils {

    @SuppressLint("StaticFieldLeak")
    private static OkHttpUtils instance;
    private static OkHttpClient okHttpClient;
    @SuppressLint("StaticFieldLeak")
    private static Context mContext;

    public static OkHttpUtils getInstance(Context mContext) {
        OkHttpUtils.mContext = mContext;
        if (instance == null) {
            synchronized (OkHttpUtils.class) {
                if (instance == null) {
                    instance = new OkHttpUtils();
                    okHttpClient = new OkHttpClient().newBuilder()
                            .readTimeout(5000, TimeUnit.MILLISECONDS)
                            .writeTimeout(30000, TimeUnit.MILLISECONDS)
                            .connectTimeout(10000, TimeUnit.MILLISECONDS)
                            .build();

                }
            }
        }
        return instance;
    }


    /**
     * @param url 接口地址
     * @param请求头参数
     * @paramoMap 携带参数
     * @paramonOkHttpListener 请求状态监听
     */
    public void doGet(String url, final OnOkHttpListener onOkHttpListener) {
        Request.Builder builder = new Request.Builder();
        builder.url(url);
        Log.e("---------", "doGet: " + url);
        //构建新连接
        Request request = builder.build();
        Call call = okHttpClient.newCall(request);
        //if (IsIntent()) {
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                String error = e.getMessage();
                onOkHttpListener.onFailure(error);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String message = response.body().string();

                try {
                    JSONObject jsonObject = new JSONObject(message);
                    String code = jsonObject.getString("code");
                    String count = jsonObject.getString("count");

                    if (code.equals("0")) {
                        if (count.equals("0")) {
                            onOkHttpListener.onEmpty();
                        } else {
                            onOkHttpListener.onSuccess(jsonObject.getString("result"));
                        }

                    }
                } catch (Exception e) {
                    onOkHttpListener.onFailure(e.getMessage());
                }
            }
        });
    }

}
