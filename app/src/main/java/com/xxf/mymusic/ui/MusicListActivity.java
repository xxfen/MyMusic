package com.xxf.mymusic.ui;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Parcelable;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xxf.mymusic.R;
import com.xxf.mymusic.base.BaseActivity;
import com.xxf.mymusic.bean.Music;
import com.xxf.mymusic.service.MyBroadcastReceiver;
import com.xxf.mymusic.service.MyMusicService;
import com.xxf.mymusic.util.ContentResolverHelper;
import com.xxf.mymusic.util.ServiceUtil;
import com.xxf.mymusic.util.SharedPreferencesUtils;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * author：xxf
 */
public class MusicListActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks {
    private static final String TAG = "MusicListActivity";
    @BindView(R.id.recycler)
    RecyclerView recycler;
    private List<Music> musicList;
    public final static String[] PERMS_WRITE = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private LinearLayoutManager layoutManager;
    private PowerManager.WakeLock mWakeLock;
    private int index;


    // private MyMusicService mService;


    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_musiclist;
    }

    @Override
    protected void initData() {
        super.initData();
        //android 8.0 必须创建频道并设置优先级
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            // mBuilder.setPriority(NotificationManager.IMPORTANCE_MIN);
//            mBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
            CharSequence channelName = "音乐播放";//channel_name

            String description = "控制音乐播放及切换下一首";//channel_description
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("MUSIC"//CHANNEL_ID
                    , channelName, importance);
            channel.setDescription(description);
            manager.createNotificationChannel(channel);
        }
        // musicResolver = getContentResolver();

        // getMusicData();
        // Log.e(TAG, "initData: " + musicList.toString());
        requestAllPower();
        musicList = new ContentResolverHelper(this).getMusic();
        String musicId = (String) SharedPreferencesUtils.getParam(this, "music_id", "-1");
        for (int i = 0; i < musicList.size(); i++) {
            if ((musicList.get(i).getId() + "").equals(musicId)) {
                index = i;
                //currentPosition = (int) SharedPreferencesUtils.getParam(this, "music_progress", 0);
                break;
            }

        }
        if (musicList == null) {
            return;
        }
        layoutManager = new LinearLayoutManager(this);
        //
        Log.e(TAG, "initData: " + "oncreate");
        if (musicList.size() != 0) {
            if (!ServiceUtil.isServiceRunning(this, "com.xxf.mymusic.service.MyMusicService")) {
                Intent service = new Intent(this, MyMusicService.class);
                service.putParcelableArrayListExtra("musiclist", (ArrayList<? extends Parcelable>) musicList);
                startService(service);
                // startForegroundService(service);
                getLock(this);
                Log.e(TAG, "initData: " + "isServiceRunning=f");
                bindService(service, connection, BIND_AUTO_CREATE);
            }
        }
        //
        View l = LayoutInflater.from(this).inflate(R.layout.title_layout, null);
        TextView textView = l.findViewById(R.id.title);
        ImageView img = l.findViewById(R.id.back);
        img.setBackgroundResource(R.mipmap.ic_launcher_music);
        textView.setText("My music");
        /*l.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MusicListActivity.this, "点击了标题", Toast.LENGTH_SHORT).show();
            }
        });*/
        setTitleview(l);
    }

    /**
     * 同步方法   得到休眠锁
     *
     * @param context
     * @return
     */
    synchronized private void getLock(Context context) {
        if (mWakeLock == null) {
            PowerManager mgr = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, MyMusicService.class.getName());
            mWakeLock.setReferenceCounted(true);
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis((System.currentTimeMillis()));
            int hour = c.get(Calendar.HOUR_OF_DAY);
            if (hour >= 23 || hour <= 6) {
                mWakeLock.acquire(5000);
            } else {
                mWakeLock.acquire(300000);
            }
        }
        Log.v(TAG, "get lock");
    }

    synchronized private void releaseLock() {
        if (mWakeLock != null) {
            if (mWakeLock.isHeld()) {
                mWakeLock.release();
                Log.v(TAG, "release lock");
            }

            mWakeLock = null;
        }
    }

    //实例服务连接接口
    public ServiceConnection connection = new ServiceConnection() {
        //连接成功回调
        public void onServiceConnected(ComponentName name, IBinder service) {
            // myBinder = (MyMusicService.MyBinder) service;
//            mService = myBinder.getService();
            //  mBound = true;
        }

        public void onServiceDisconnected(ComponentName name) {

        }
    };


    public void requestAllPower() {
        if (EasyPermissions.hasPermissions(this, PERMS_WRITE)) {
            //  Toast.makeText(this, "有权限", Toast.LENGTH_SHORT).show();
            handler.sendEmptyMessage(0x10);
        } else {//申请权限
            EasyPermissions.requestPermissions(this, "权限",
                    10, PERMS_WRITE);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    private void setRecyclerView() {
        Log.e(TAG, "setRecyclerView: ");
        recycler.setLayoutManager(layoutManager);
        recycler.setAdapter(new CommonAdapter<Music>(this, R.layout.item_music, musicList) {

            @Override
            protected void convert(ViewHolder holder, final Music music, final int position) {
                //  Log.e(TAG, "convert: " + music.getId());
                if (index == position) {
                    holder.setTextColor(R.id.music_title, Color.GREEN);
                    holder.setTextColor(R.id.music_author, Color.GREEN);
                    holder.setTextColor(R.id.index, Color.GREEN);
                } else {
                    holder.setTextColor(R.id.music_title, Color.GRAY);
                    holder.setTextColor(R.id.music_author, Color.GRAY);
                    holder.setTextColor(R.id.index, Color.GRAY);
                }
                holder.setText(R.id.index, position + 1 + "");
                holder.setText(R.id.music_title, music.getTitle());
                holder.setText(R.id.music_author, music.getArtist());
                holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // myBinder.playingto(position);
                        Bundle bundle = new Bundle();
                        bundle.putParcelableArrayList("musiclist", (ArrayList<? extends Parcelable>) musicList);
                        bundle.putInt("index", position);
                        bundle.putInt("type", 1);
                        index = position;
                        // startActivity(MusicPlayActivity.class, bundle);
                        startActivity(MusicPlayActivity.class, bundle, Intent.FLAG_ACTIVITY_NEW_TASK
                                // | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
                        );

                    }
                });
            }

        });
        //recycler.scrollToPosition(index);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        Toast.makeText(this, "用户授权成功", Toast.LENGTH_SHORT).show();
        handler.sendEmptyMessage(0x10);
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        Toast.makeText(this, "用户授权失败", Toast.LENGTH_SHORT).show();
        /**
         * 若是在权限弹窗中，用户勾选了'NEVER ASK AGAIN.'或者'不在提示'，且拒绝权限。
         * 这时候，需要跳转到设置界面去，让用户手动开启。
         */
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x10:
                    Log.e(TAG, "handleMessage: " + musicList.size());
                    // Log.e(TAG, "handleMessage: " + musicList.toString());
                    if (musicList.size() != 0)
                        setRecyclerView();
                    break;
            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Intent intent = new Intent(Intent.ACTION_MAIN);//ACTION_MAIN：应用程序入口点
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//FLAG_ACTIVITY_NEW_TASK：默认的跳转类型,会重新创建一个新的Activity
        intent.addCategory(Intent.CATEGORY_HOME);//CATEGORY_HOME：显示当前应用的主界面
        getApplicationContext().startActivity(intent);
        return super.onKeyDown(keyCode, event);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy: " + "activity");
        releaseLock();
        unbindService(connection);
    }

    private void upPlayItem() {

    }
}
