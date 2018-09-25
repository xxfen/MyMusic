package com.xxf.mymusic.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.xxf.mymusic.bean.LyricContent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * author：xxf
 */
public class OnlineLrcUtil {

    private static String TAG = "OnlineLrcUtil";
    private static OnlineLrcUtil instance;
    public static String lrcRootPath;//缓存地址

    public static final String queryLrcURLRoot = "http://geci.me/api/lyric/";
    private ArrayList<LyricContent> LyricList;

    public static OnlineLrcUtil getInstance(Context context) {
        if (null == instance) {
            instance = new OnlineLrcUtil();
            lrcRootPath = context.getExternalCacheDir() + "/MyMusic/Lyrics/";

        }
        return instance;
    }


    /**
     * 读取歌词
     *
     * @param path
     * @return
     */
    public List<LyricContent> readLRC(String path) {
        //定义一个StringBuilder对象，用来存放歌词内容
        StringBuilder stringBuilder = new StringBuilder();
        File f = new File(path.replace(".mp3", ".lrc"));//path.replace(".mp3", ".lrc")
        LyricList = new ArrayList<>();
        try {
            //创建一个文件输入流对象
            FileInputStream fis = new FileInputStream(f);
            InputStreamReader isr = new InputStreamReader(fis, "utf-8");
            BufferedReader br = new BufferedReader(isr);
            String s = "";

            while ((s = br.readLine()) != null) {
                //替换字符
                s = s.replace("[", "");
                s = s.replace("]", "@");

                //分离“@”字符
                String splitLrcData[] = s.split("@");
                if (splitLrcData.length > 1) {

                    LyricContent mLyricContent = new LyricContent();
                    mLyricContent.setLyric(splitLrcData[1]);// [00:00.00, 我爱歌词网 www.5ilrc.com],取数组里面的第2个数据作为歌词。
                    int LyricTime = TimeStr(splitLrcData[0]);// 取数组里面的第1个数据，放到TimeStr里都转成秒为单位后出来作为歌词时间。0 400 9490 12490 15860 15860 35560
                    mLyricContent.setLyricTime(LyricTime);
                    LyricList.add(mLyricContent);
                }
            }
            stringBuilder.append("有歌词哦！");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            stringBuilder.append("木有歌词文件，赶紧去下载！...");

        } catch (IOException e) {
            e.printStackTrace();
            stringBuilder.append("木有读取到歌词哦！");

        }

        return LyricList;
    }


    public int TimeStr(String timeStr) {// 00:40.57
        timeStr = timeStr.replace(":", ".");//00.40.57
        timeStr = timeStr.replace(".", "@");//00@40@57
        String timeData[] = timeStr.split("@");//[00, 40, 57]
        int minute = Integer.parseInt(timeData[0]);//数组里的第1个数据是分0
        int second = Integer.parseInt(timeData[1]);//数组里的第2个数据是秒40
        int millisecond = Integer.parseInt(timeData[2]);//数组里的第3个数据是秒57
        int currentTime = (minute * 60 + second) * 1000 + millisecond * 10;//40000+570=40570
        return currentTime;
    }

    /**
     * 缓存到本地
     *
     * @param
     * @param
     * @return
     * @throws IOException
     */
    public boolean getFileFromServer(final String name, final String duration) throws IOException {
        //如果相等的话表示当前的sdcard挂载在手机上并且是可用的
        //  if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
        String path = lrcRootPath + URLEncoder.encode(name, "utf-8");
        Log.e(TAG, "getFileFromServer: path==" + lrcRootPath + URLEncoder.encode(name, "utf-8") + ".lrc");
        String urlStr = "http://lyrics.kugou.com/search?ver=1&man=yes&client=pc&keyword=" +
                URLEncoder.encode(name, "utf-8") + "&duration=" +
                URLEncoder.encode(duration, "utf-8") + "&hash=";

        Log.e(TAG, "getFileFromServer: " + urlStr);
        URL url = null;
        try {
            url = new URL(urlStr);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            //  L.d(" url = new URL(uri)出现异常");
            Log.e(TAG, "getFileFromServer:  url = new URL(uri)出现异常");
            Log.e(TAG, "getFileFromServer: " + e.toString());
        }
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "getFileFromServer: (HttpURLConnection) url.openConnection()出现异常！");
            // L.d(" conn = (HttpURLConnection) url.openConnection()出现异常！");
        }
        conn.setConnectTimeout(5000);
        InputStream is = null;
        try {
            is = conn.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
            //   L.d("  is = conn.getInputStream()出现异常");
            Log.e(TAG, "getFileFromServer:  is = conn.getInputStream()出现异常");
        }
        long time = System.currentTimeMillis();//当前时间的毫秒数
        File file = new File(path.replace(".mp3", ".lrc"));
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e(TAG, "getFileFromServer:  new FileOutputStream异常");
        }
        BufferedInputStream bis = new BufferedInputStream(is);
        byte[] buffer = new byte[1024];
        int len;
        // int total = 0;
        while ((len = bis.read(buffer)) != -1) {
            fos.write(buffer, 0, len);
            //total += len;
        }
        fos.close();
        bis.close();
        is.close();
        Log.e(TAG, "getFileFromServer: 获取文件是否存在：" + file.exists());
        return file.exists();
        // } else {
        //   return false;
        // }
    }
//    public void searchLyric(final String name, final String duration){
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    //建立连接 -- 查找歌曲
//                    String urlStr = "http://lyrics.kugou.com/search?ver=1&man=yes&client=pc&keyword=" + name + "&duration=" + duration + "&hash=";
//                    URL url = new URL(URLEncoder.encode(urlStr,"utf-8"));  //字符串进行URL编码
//                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//                    conn.connect();
//
//                    //读取流 -- JSON歌曲列表
//                    InputStream input = conn.getInputStream();
//                    String res = FileUtil.formatStreamToString(input);  //流转字符串
//
//                    JSONObject json1 = new JSONObject(res);  //字符串读取为JSON
//                    JSONArray json2 = json1.getJSONArray("candidates");
//                    JSONObject json3 = json2.getJSONObject(0);
//
//                    //建立连接 -- 查找歌词
//                    urlStr = "http://lyrics.kugou.com/download?ver=1&client=pc&id=" + json3.get("id") + "&accesskey=" + json3.get("accesskey") + "&fmt=lrc&charset=utf8";
//                    url = new URL(encodeUrl(urlStr));
//                    conn = (HttpURLConnection) url.openConnection();
//                    conn.connect();
//
//                    //读取流 -- 歌词
//                    input = conn.getInputStream();
//                    res = FileUtil.formatStreamToString(input);
//                    JSONObject json4 = new JSONObject(res);
//
//                    //获取歌词base64，并进行解码
//                    String base64 = json4.getString("content");
//                    final String lyric = Base64.getFromBASE64(base64);
//
//                    Log.i("lyric", lyric);
//
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            showLyric.setText(lyric);
//                        }
//                    });
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//    }

   /* urlStr = "http://lyrics.kugou.com/download?ver=1&client=pc&id=" +
            json3.get("id") +
            "&accesskey=" +
            json3.get("accesskey") +
            "&fmt=lrc&charset=utf8";*/

}
