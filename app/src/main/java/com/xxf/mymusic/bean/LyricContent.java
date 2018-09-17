package com.xxf.mymusic.bean;

/**
 * author：xxf
 */
public class LyricContent {
    private int lyricTime;
    private String lyric;

    @Override
    public String toString() {
        return "LyricContent{" +
                "lyricTime=" + lyricTime +
                ", lyric='" + lyric + '\'' +
                '}';
    }

    public int getLyricTime() {
        return lyricTime;
    }

    public String getLyric() {
        return lyric;
    }

    public void setLyricTime(int lyricTime) {
        this.lyricTime = lyricTime;
    }

    public void setLyric(String lyric) {
        this.lyric = lyric;
    }
}
