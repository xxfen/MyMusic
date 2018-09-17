package com.xxf.mymusic.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Parcelable;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.xxf.mymusic.R;
import com.xxf.mymusic.bean.Music;
import com.xxf.mymusic.constant.Broadcast;
import com.xxf.mymusic.i.OnMusicPlayCallBack;
import com.xxf.mymusic.ui.MusicPlayActivity;
import com.xxf.mymusic.util.SharedPreferencesUtils;
import com.xxf.mymusic.util.StringUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MyMusicService extends Service {
    private static final String TAG = "oooo";
    private MediaPlayer mediaPlayer;
    private List<Music> musicList;
    private int index = 0;
    private int maxIndex;
    private int currentPosition;
    private String historyPath;
    private MyBroadcastReceiver receiver;

    @Override
    public void onCreate() {
        super.onCreate();

        mediaPlayer = new MediaPlayer();
        //音乐播放完成的监听
        mediaPlayer.setOnCompletionListener(listener);


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        receiver = new MyBroadcastReceiver();
        // receiver = new MyServiceBcReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Broadcast.A1);//com.xxf.mysbcreceiver
        intentFilter.addAction(Broadcast.A2);//com.xxf.mysbcreceiver
        Log.e(TAG, "onStartCommand: " + "c");
        registerReceiver(receiver, intentFilter);
        receiver.setOnMusicPlayCallBack(musicPlayCallBack);
        //--
        //--
        musicList = intent.getParcelableArrayListExtra("musiclist");
        maxIndex = musicList.size() - 1;
        try {
            String musicId = (String) SharedPreferencesUtils.getParam(this, "music_id", "-1");
            for (int i = 0; i < musicList.size(); i++) {
                if ((musicList.get(i).getId() + "").equals(musicId)) {
                    index = i;
                    currentPosition = (int) SharedPreferencesUtils.getParam(this, "music_progress", 0);
                    break;
                }

            }
            // Log.e(TAG, "onBind: ");
            // if (mediaPlayer.isPlaying())
            mediaPlayer.reset();

            mediaPlayer.setDataSource(musicList.get(index).getPath());

            //准备资源
            mediaPlayer.prepare();
            mediaPlayer.seekTo(currentPosition);
            showNotification(musicList.get(index).getTitle(), musicList.get(index).getArtist());
        } catch (IOException e) {
            e.printStackTrace();
            //  Log.e(TAG, "onBind: " + e.toString());
        }
        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public IBinder onBind(Intent intent) {

        return new MyBinder();
    }


    public class MyBinder extends Binder {
        public MyMusicService getService() {
            return MyMusicService.this;
        }

        public void playingto(int playint) {
            index = playint;
            prepare();
            mediaPlayer.start();
        }

        public boolean isMusicPlay() {
            if (mediaPlayer != null) {
                return mediaPlayer.isPlaying();
            }
            return false;
        }
    }

    //音乐播放完成的监听
    private MediaPlayer.OnCompletionListener listener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            //
            mp.reset();
            playNext();
            getCurrentPosition();
            sendMessageCall(2);

            //更新通知栏
            upDataNotification(musicList.get(index).getTitle(), musicList.get(index).getArtist(), mediaPlayer.isPlaying());
        }
    };
    private NotificationCompat.Builder mBuilder;
    private RemoteViews remoteViews;
    private Notification notification;

    private void showNotification(String title, String artist) {
        Log.e(TAG, "showNotification: " + "11111111");
        if (mBuilder == null)
            mBuilder = new NotificationCompat.Builder(this, "MUSIC");

        /**
         *  设置Builder
         */
        if (remoteViews == null)
            remoteViews = new RemoteViews(this.getPackageName(), R.layout.notification_music);
        remoteViews.setTextViewText(R.id.tv_notification_title, StringUtil.subString(title, 0, 5));
        remoteViews.setTextViewText(R.id.tv_notification_artist, artist);
        remoteViews.setImageViewResource(R.id.iv_notification_icon, R.mipmap.ic_launcher_music);
        remoteViews.setImageViewResource(R.id.iv_notification_play, R.drawable.ic_action_play);
        remoteViews.setImageViewResource(R.id.iv_notification_next, R.drawable.ic_action_next);
        remoteViews.setImageViewResource(R.id.iv_notification_close, R.drawable.ic_action_close);
        Intent intent = new Intent();
        intent.setAction(Broadcast.A1);
        intent.putExtra("PLAY", com.xxf.mymusic.constant.Music.PLAY_NEXT);
        int requestCode = (int) SystemClock.uptimeMillis();
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.iv_notification_next, pendingIntent);

        // Context tcontent = MyActivityManager.getInstance().getCurrentActivity();
        // Intent intent1 = new Intent(tcontent != null ? tcontent : this, MusicPlayActivity.class);
        Intent intent1 = new Intent(this, MusicPlayActivity.class);
        // intent1.setAction(Broadcast.A2);
        // intent1.setClass(this, MusicListActivity.class);
        // intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("musiclist", (ArrayList<? extends Parcelable>) musicList);
        bundle.putInt("index", index);
        bundle.putInt("type", 2);
        bundle.putInt("cposition", mediaPlayer.getCurrentPosition());
        intent1.putExtras(bundle);
      /*  intent1.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
                     //   | Intent.FLAG_ACTIVITY_SINGLE_TOP
        );*/
        //intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        //intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);//目标activity已在栈顶则跳转过去，不在栈顶则在栈顶新建activity
        // intent1.putExtra("PLAY", com.xxf.mymusic.constant.Music.PLAY_NEXT);
        PendingIntent pendingIntent1 = PendingIntent.getActivity(this, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.notification, pendingIntent1);

       /* Intent intent1 = new Intent(this, MusicPlayActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("musiclist", (ArrayList<? extends Parcelable>) musicList);
        bundle.putInt("index", index);
        intent1.putExtras(bundle);
        // intent1.setAction("com.xxf.mymusic");
        //intent1.putExtra("close", "1");
        //intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent
                .FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        int requestCode1 = (int) SystemClock.uptimeMillis();
        PendingIntent pendingIntent1 = PendingIntent.getBroadcast(this, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.notification, pendingIntent1);*/


        Intent intent2 = new Intent();
        intent2.setAction(Broadcast.A1);
        intent2.putExtra("PLAY", com.xxf.mymusic.constant.Music.PLAY_PAUSE);
        int requestCode2 = (int) SystemClock.uptimeMillis();
        PendingIntent pendingIntent2 = PendingIntent.getBroadcast(this, requestCode2, intent2, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.iv_notification_play, pendingIntent2);

        Intent intent3 = new Intent();
        intent3.setAction(Broadcast.A2);
        intent3.putExtra("closeAll", 1);
        int requestCode3 = (int) SystemClock.uptimeMillis();
        PendingIntent pendingIntent3 = PendingIntent.getBroadcast(this, requestCode3, intent3, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.iv_notification_close, pendingIntent3);

        // Intent intent3 = new Intent();
        // intent3.setAction("com.xxf.mysbcreceiver");
        // intent3.putExtra("playint", -10);
        // PendingIntent pendingIntent3 = PendingIntent.getBroadcast(this, requestCode1, intent3, PendingIntent.FLAG_UPDATE_CURRENT);
        //remoteViews.setOnClickPendingIntent(R.id.iv_notification_next, pendingIntent3);
        // mBuilder.setOngoing(true);
        mBuilder.setContent(remoteViews);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher_music);
        mBuilder.setPriority(NotificationCompat.PRIORITY_MAX);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notification = mBuilder.build();

        if (android.os.Build.VERSION.SDK_INT >= 16) {
            // notification = mBuilder.build();
            notification.bigContentView = remoteViews;
        }
        notification.flags = Notification.FLAG_ONGOING_EVENT;
        notification.contentView = remoteViews;

        manager.notify(10, notification);
    }

    private void upDataNotification(String title, String artist, boolean isplay) {
        Log.e(TAG, "upDataNotification: ");
        remoteViews.setTextViewText(R.id.tv_notification_title, StringUtil.subString(title, 0, 5));
        remoteViews.setTextViewText(R.id.tv_notification_artist, artist);
        if (isplay) {
            remoteViews.setImageViewResource(R.id.iv_notification_play, R.drawable.ic_action_stap);
        } else {
            remoteViews.setImageViewResource(R.id.iv_notification_play, R.drawable.ic_action_play);
        }
        //  notification.priority = NotificationManager.IMPORTANCE_MIN;
        notification.contentView = remoteViews;
        NotificationManager manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(10, notification);
    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case -1:
                    break;
                case 0x9:

                    break;
                case 0x10://播放下一首
                    playNext();
                    sendMessageCall(2);
                    timeRunable.run();
                    break;
                case 0x11://播放上一首
                    playPrevious();
                    sendMessageCall(2);
                    timeRunable.run();
                    break;
                case 0x12://播放||暂停
                    play_pause();
                    sendMessageCall(1);
                    timeRunable.run();
                    break;
                case 0x66:

                    break;
                default://播放某一首
                    index = msg.what;
                    Log.e(TAG, "playNext: " + index);
                    prepare();
                    mediaPlayer.start();
                    timeRunable.run();
                    break;
            }
            //更新通知栏
            upDataNotification(musicList.get(index).getTitle(), musicList.get(index).getArtist(), mediaPlayer.isPlaying());
        }
    };

    //播放准备||重置
    public void prepare() {
        if (mediaPlayer != null) {
            try {
                // if (mediaPlayer.isPlaying())
                mediaPlayer.reset();
                mediaPlayer.setDataSource(musicList.get(index).getPath());
                //准备资源
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // upDataNotification(musicList.get(index).getTitle(), musicList.get(index).getArtist(), mediaPlayer.isPlaying());
    }

    //播放||暂停
    public boolean play_pause() {
        // mediaPlayer.isPlaying();
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                // isPause = true;
            } else {
                mediaPlayer.start();
                // isPause = false;

            }
        }
        return mediaPlayer.isPlaying();
    }

    //播放下一首
    public void playNext() {
        index = index == maxIndex ? 0 : index + 1;
        Log.e(TAG, "playNext: " + index);
        prepare();
        mediaPlayer.start();

    }


    //播放上一首
    public void playPrevious() {
        index = index == 0 ? maxIndex : index - 1;
        Log.e(TAG, "playPrevious: " + index);
        prepare();
        mediaPlayer.start();

    }

    //获取当前播放进度
    public int getCurrentPosition() {
        if (mediaPlayer != null) {
            currentPosition = mediaPlayer.getCurrentPosition();
        }
        return currentPosition;
    }


    public void sendMessageCall(int type) {
        Intent intent = new Intent();
        intent.setAction(Broadcast.B);
        intent.putExtra("type", type);
        switch (type) {
            case 1://是否播放
                intent.putExtra("isplay", mediaPlayer.isPlaying());
                // if (mediaPlayer.isPlaying()) {
                intent.putExtra("position", currentPosition);
                // }
                break;

            case 2://播放切换
                intent.putExtra("index", index);
                break;

        }
        // intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        // intent.setComponent(new ComponentName(getApplication().getPackageName(), "com.xxf.com.mysbcreceiver"));
        sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isDestroy = true;
        SharedPreferencesUtils.saveHistoryMusic(this, musicList.get(index).getId() + "", mediaPlayer.getCurrentPosition());
        mediaPlayer.stop();
        mediaPlayer.release();
        //取消
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(10);
        //  manager.deleteNotificationChannel("MUSIC");
        unregisterReceiver(receiver);
        Log.e(TAG, "onDestroy: " + "service");
    }

    /**
     * 播放监听回调
     */
    private OnMusicPlayCallBack musicPlayCallBack = new OnMusicPlayCallBack() {
        @Override
        public void playorPause() {
            handler.sendEmptyMessage(0x12);
        }

        @Override
        public void playPrevious() {

            handler.sendEmptyMessage(0x11);
        }

        @Override
        public void playNext() {

            handler.sendEmptyMessage(0x10);
        }

        @Override
        public void playTo(int index) {
            handler.sendEmptyMessage(index);
        }

        @Override
        public void playSeek(int Progress) {
            mediaPlayer.pause();
            mediaPlayer.seekTo(Progress);
            mediaPlayer.start();
        }

        /*@Override
        public void playFinsh() {
            onDestroy();
        }*/

    };

    private boolean isDestroy = false;
    /*****************计时器*******************/
    private Runnable timeRunable = new Runnable() {
        @Override
        public void run() {
            Log.e(TAG, "run: 0000");
            //getCurrentPosition();
            if (!isDestroy)
                if (mediaPlayer.isPlaying()) {
                    currentPosition = mediaPlayer.getCurrentPosition();
                    sendMessageCall(1);
                    Log.e(TAG, "run: 1111");
                    // if (!isPause) {
                    //递归调用本runable对象，实现每隔一秒一次执行任务
                    mhandle.postDelayed(this, 1000);
                    //  }
                    // } else {

                }
        }
    };
    //计时器
    private Handler mhandle = new Handler();
    //private boolean isPause = false;//是否暂停

    /*****************计时器*******************/
}
