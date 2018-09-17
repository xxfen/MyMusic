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

    public static OnlineLrcUtil getInstance(Context context) {
        if (null == instance) {
            instance = new OnlineLrcUtil();
            lrcRootPath = context.getExternalCacheDir() + "/MyMusic/Lyrics/";

        }
        return instance;
    }


    public List<LyricContent> getLyricContent(String title, String artist) throws IOException {
        File ffile = new File(lrcRootPath);
        if (!ffile.exists()) {
            ffile.mkdirs();
        }
        Log.e(TAG, "getLyricContent: " + ffile.exists());
        String p = lrcRootPath + title + " - " + artist + ".lrc";
        File file = new File(p);
        if (!file.exists()) {//不存在
            getFileFromServer(queryLrcURLRoot + title + "/" + artist, p);
        }
        return Read(p);
    }

    /**
     * 解析lrc文件
     *
     * @param path
     * @throws FileNotFoundException
     * @throws IOException
     */
    public ArrayList<LyricContent> Read(String path) throws FileNotFoundException, IOException {
        String Lrc_data = "";
        File mFile = new File(path);// /mnt/sdcard/我不知道爱是什么.lrc
        FileInputStream mFileInputStream = new FileInputStream(mFile);
        InputStreamReader mInputStreamReader = new InputStreamReader(mFileInputStream, "GB2312");
        BufferedReader mBufferedReader = new BufferedReader(mInputStreamReader);
        ArrayList<LyricContent> LyricList = new ArrayList<LyricContent>();
        while ((Lrc_data = mBufferedReader.readLine()) != null) {//[ti:我不知道爱是什么] ar:艾怡良]
            Lrc_data = Lrc_data.replace("[", "");// ti:我不知道爱是什么]
            Lrc_data = Lrc_data.replace("]", "@");// ti:我不知道爱是什么@
            String splitLrc_data[] = Lrc_data.split("@");// [00:00.00, 我爱歌词网 www.5ilrc.com]split是去掉@并在此处用逗号分隔成两个字符串。最后放到一个数组里。
            if (splitLrc_data.length > 1) {
                LyricContent mLyricContent = new LyricContent();
                mLyricContent.setLyric(splitLrc_data[1]);// [00:00.00, 我爱歌词网 www.5ilrc.com],取数组里面的第2个数据作为歌词。
                int LyricTime = TimeStr(splitLrc_data[0]);// 取数组里面的第1个数据，放到TimeStr里都转成秒为单位后出来作为歌词时间。0 400 9490 12490 15860 15860 35560
                mLyricContent.setLyricTime(LyricTime);
                LyricList.add(mLyricContent);
            }
        }
        mBufferedReader.close();
        mInputStreamReader.close();
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

    public static boolean getFileFromServer(String uri, String p) throws IOException {
        //如果相等的话表示当前的sdcard挂载在手机上并且是可用的
        //  if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
        Log.e(TAG, "getFileFromServer: url==" + uri);
        Log.e(TAG, "getFileFromServer: path==" + p);
        URL url = null;
        try {
            url = new URL(uri);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            //  L.d(" url = new URL(uri)出现异常");
            Log.e(TAG, "getFileFromServer:  url = new URL(uri)出现异常");
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
        File file = new File(p);
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
}
