package com.xxf.mymusic.widgit;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.xxf.mymusic.R;

/**
 * author：xxf
 */
public class MyNotification {
    private Context mContent;

    public MyNotification() {

    }

    private void show(String title, String artist) {

        /**
         *  实例化通知栏构造器
         */

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContent, null);

        /**
         *  设置Builder
         */
        RemoteViews remoteViews = new RemoteViews(mContent.getPackageName(), R.layout.notification_music);
        remoteViews.setTextViewText(R.id.tv_notification_title, title);
        remoteViews.setTextViewText(R.id.tv_notification_artist, artist);
        remoteViews.setImageViewResource(R.id.iv_notification_icon, R.mipmap.ic_launcher_music);
        int requestCode1 = (int) SystemClock.uptimeMillis();
        Intent intent1 = new Intent("com.xxf.mymusic");
        // intent1.putExtra("close", "1");
        PendingIntent pendingIntent1 = PendingIntent.getBroadcast(mContent, requestCode1, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.iv_notification_icon, pendingIntent1);
        mBuilder.setContent(remoteViews);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher_music);
        Notification notification = mBuilder.build();



        if (android.os.Build.VERSION.SDK_INT >= 16) {
            notification = mBuilder.build();
            notification.bigContentView = remoteViews;
        }
        notification.contentView = remoteViews;
        NotificationManager manager = (NotificationManager) mContent.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(10, notification);
        //
        //
        //
        //
        //

        //设置标题
        /*mBuilder.setContentTitle("我是标题")
                //设置内容
                .setContentText("我是内容")
                //设置大图标
                .setLargeIcon(BitmapFactory.decodeResource(mContent.getResources(), R.mipmap.ic_launcher))
                //设置小图标
                .setSmallIcon(R.mipmap.ic_launcher_round)
                //设置通知时间
                .setWhen(System.currentTimeMillis())
                //首次进入时显示效果
                .setTicker("我是测试内容")
                //设置通知方式，声音，震动，呼吸灯等效果，这里通知方式为声音
                .setDefaults(Notification.DEFAULT_SOUND);*/
       /* Notification notification
                = mBuilder.build();
        // Notification.FLAG_ONGOING_EVENT;点击清除按钮不会清除消息通知,可以用来表示在正在运行
        notification.flags = Notification.FLAG_INSISTENT; // 一直进行，比如音乐一直播放，知道用户响应

        //发送通知请求
        notificationManager.notify(10, notification);*/

        //取消
        //  NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        //  manager.cancel(noticeId);
    }
}
