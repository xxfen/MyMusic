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
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
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
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.io.Serializable;
import java.util.ArrayList;
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
        layoutManager = new LinearLayoutManager(this);
        //
        Log.e(TAG, "initData: " + "oncreate");
        if (musicList.size() != 0) {
            if (!ServiceUtil.isServiceRunning(this, "com.xxf.mymusic.service.MyMusicService")) {
                Intent service = new Intent(this, MyMusicService.class);
                service.putParcelableArrayListExtra("musiclist", (ArrayList<? extends Parcelable>) musicList);
                startService(service);
                Log.e(TAG, "initData: " + "isServiceRunning=f");
                // bindService(service, connection, BIND_AUTO_CREATE);
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


   /* //实例服务连接接口
    public ServiceConnection connection = new ServiceConnection() {
        //连接成功回调
        public void onServiceConnected(ComponentName name, IBinder service) {
            myBinder = (MyMusicService.MyBinder) service;
//            mService = myBinder.getService();
            //  mBound = true;
        }

        public void onServiceDisconnected(ComponentName name) {

        }
    };*/


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

                        // startActivity(MusicPlayActivity.class, bundle);
                        startActivity(MusicPlayActivity.class, bundle, Intent.FLAG_ACTIVITY_NEW_TASK
                                // | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
                        );

                    }
                });
            }

        });

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
        //unbindService(connection);
    }
}
