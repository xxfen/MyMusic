package com.xxf.mymusic.bean;

import java.util.List;

/**
 * author：xxf
 */
public class LyricListContent {

    @Override
    public String toString() {
        return "LyricListContent{" +
                "soundname='" + soundname + '\'' +
                ", krctype=" + krctype +
                ", nickname='" + nickname + '\'' +
                ", originame='" + originame + '\'' +
                ", accesskey='" + accesskey + '\'' +
                ", origiuid='" + origiuid + '\'' +
                ", score=" + score +
                ", hitlayer=" + hitlayer +
                ", duration=" + duration +
                ", sounduid='" + sounduid + '\'' +
                ", song='" + song + '\'' +
                ", uid='" + uid + '\'' +
                ", transuid='" + transuid + '\'' +
                ", transname='" + transname + '\'' +
                ", adjust=" + adjust +
                ", id='" + id + '\'' +
                ", singer='" + singer + '\'' +
                ", language='" + language + '\'' +
                ", parinfo=" + parinfo +
                '}';
    }

    /**
     * soundname :
     * krctype : 2
     * nickname :
     * originame :
     * accesskey : C750F27C890BE3FF1BE831E1160CAF14
     * parinfo : []
     * origiuid : 0
     * score : 60
     * hitlayer : 7
     * duration : 314000
     * sounduid : 0
     * song : 大鱼
     * uid : 410927974
     * transuid : 0
     * transname :
     * adjust : 0
     * id : 26122912
     * singer : 周深
     * language :
     */

    private String soundname;
    private int krctype;
    private String nickname;
    private String originame;
    private String accesskey;
    private String origiuid;
    private int score;
    private int hitlayer;
    private int duration;
    private String sounduid;
    private String song;
    private String uid;
    private String transuid;
    private String transname;
    private int adjust;
    private String id;
    private String singer;
    private String language;
    private List<?> parinfo;

    public String getSoundname() {
        return soundname;
    }

    public void setSoundname(String soundname) {
        this.soundname = soundname;
    }

    public int getKrctype() {
        return krctype;
    }

    public void setKrctype(int krctype) {
        this.krctype = krctype;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getOriginame() {
        return originame;
    }

    public void setOriginame(String originame) {
        this.originame = originame;
    }

    public String getAccesskey() {
        return accesskey;
    }

    public void setAccesskey(String accesskey) {
        this.accesskey = accesskey;
    }

    public String getOrigiuid() {
        return origiuid;
    }

    public void setOrigiuid(String origiuid) {
        this.origiuid = origiuid;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getHitlayer() {
        return hitlayer;
    }

    public void setHitlayer(int hitlayer) {
        this.hitlayer = hitlayer;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getSounduid() {
        return sounduid;
    }

    public void setSounduid(String sounduid) {
        this.sounduid = sounduid;
    }

    public String getSong() {
        return song;
    }

    public void setSong(String song) {
        this.song = song;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTransuid() {
        return transuid;
    }

    public void setTransuid(String transuid) {
        this.transuid = transuid;
    }

    public String getTransname() {
        return transname;
    }

    public void setTransname(String transname) {
        this.transname = transname;
    }

    public int getAdjust() {
        return adjust;
    }

    public void setAdjust(int adjust) {
        this.adjust = adjust;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public List<?> getParinfo() {
        return parinfo;
    }

    public void setParinfo(List<?> parinfo) {
        this.parinfo = parinfo;
    }
}
