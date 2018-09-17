package com.xxf.mymusic.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.xxf.mymusic.constant.Broadcast;
import com.xxf.mymusic.constant.Music;
import com.xxf.mymusic.i.OnMusicPlayCallBack;

/**
 * author：xxf
 */
public class MyBroadcastReceiver extends BroadcastReceiver {

    private OnMusicPlayCallBack musicPlayCallBack;

    @Override
    public void onReceive(Context context, Intent intent) {

        switch (intent.getAction()) {
            case Broadcast.A1:
                int playint = intent.getIntExtra("PLAY", -1);
                switch (playint) {
                    case -1:
                        break;
                    case -2:
                        if (musicPlayCallBack != null)
                            musicPlayCallBack.playSeek(intent.getIntExtra("PROGRESS", 0));
                        break;
                    case Music.PLAY_PREVIOUS:
                        if (musicPlayCallBack != null)
                            musicPlayCallBack.playPrevious();
                        break;
                    case Music.PLAY_NEXT:
                        if (musicPlayCallBack != null)
                            musicPlayCallBack.playNext();
                        break;
                    case Music.PLAY_PAUSE:
                        if (musicPlayCallBack != null)
                            musicPlayCallBack.playorPause();
                        break;
                    default:
                        if (musicPlayCallBack != null)
                            musicPlayCallBack.playTo(playint);
                        break;
                }
                break;
         /*   case Broadcast.A2:
                //接收发送过来的广播内容
                int closeAll = intent.getIntExtra("closeAll", 0);
                if (closeAll == 1) {
                    if (musicPlayCallBack != null)
                        musicPlayCallBack.playFinsh();
                }
                break;*/

        }

    }

    public void setOnMusicPlayCallBack(OnMusicPlayCallBack musicPlayCallBack) {
        this.musicPlayCallBack = musicPlayCallBack;

    }

}
