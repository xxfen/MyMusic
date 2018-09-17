package com.xxf.mymusic.i;

/**
 * author：xxf
 */
public interface OnMusicPlayCallBack {
    void playorPause();//播放暂停

    void playPrevious();//播放上一首

    void playNext();//播放下一首

    void playTo(int index);//播放某一首

    void playSeek(int Progress);//播放进度

   // void playFinsh();//结束播放
}
