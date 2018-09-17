package com.xxf.mymusic.util;

import com.xxf.mymusic.bean.LyricContent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * author：xxf
 */
public class StringUtil {
    /**
     * 根据毫秒返回时分秒
     *
     * @param time
     * @return
     */
    public static String getFormatHMS(long time) {
        time = time / 1000;//总秒数
        int s = (int) (time % 60);//秒
        int m = (int) (time / 60);//分
        //int h = (int) (time / 3600);//秒
        // return String.format("%02d:%02d:%02d", h, m, s);
        return String.format("%02d:%02d", m, s);
    }


    public static String subString(String str, int first, int last) {
        if (str.length() > last) {
            return str.substring(first, last) + "…";
        } else {
            return str;
        }
    }


}
