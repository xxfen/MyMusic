package com.xxf.mymusic.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.xxf.mymusic.R;
import com.xxf.mymusic.base.BaseActivity;
import com.xxf.mymusic.bean.LyricContent;
import com.xxf.mymusic.bean.Music;
import com.xxf.mymusic.constant.Broadcast;
import com.xxf.mymusic.i.OnOkHttpListener;
import com.xxf.mymusic.util.OkHttpUtils;
import com.xxf.mymusic.util.OnlineLrcUtil;
import com.xxf.mymusic.util.StringUtil;
import com.xxf.mymusic.widgit.ListDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * author：xxf
 */
public class MusicPlayActivity extends BaseActivity {
    @BindView(R.id.seekbar)
    SeekBar seekbar;
    @BindView(R.id.tv_play_currenttime)
    TextView tvPlayCurrenttime;
    @BindView(R.id.tv_play_maxtime)
    TextView tvPlayMaxtime;
    @BindView(R.id.iv_play_flowtype)
    ImageView ivPlayFlowtype;
    @BindView(R.id.iv_play_previous)
    ImageView ivPlayPrevious;
    @BindView(R.id.iv_play_playbe)
    ImageView ivPlayPlaybe;
    @BindView(R.id.iv_play_next)
    ImageView ivPlayNext;
    @BindView(R.id.iv_play_list)
    ImageView ivPlayList;
    @BindView(R.id.line_music_content)
    LinearLayout lineMusicContent;
    private String TAG = "MusicPlayActivity";
    // private MediaPlayer mediaPlayer;
    private List<Music> musicList;
    private int index = 0;
    private int maxIndex;
    private int currentPosition;
    private int maxPosition;
    private boolean isTouch = false;
    private boolean isplay = false;
    private TextView musicName;
    private MyMUsicPlayBroadcastReceiver myMUsicPlayBroadcastReceiver;
    private int sendType;
    private List<LyricContent> lyricContentList;

    @Override

    protected int getContentLayoutId() {
        return R.layout.activity_musicplay;
    }

    @Override
    protected boolean initArgs(Bundle bundle) {
        try {
            musicList = bundle.getParcelableArrayList("musiclist");
            index = bundle.getInt("index");
            sendType = bundle.getInt("type");
            if (sendType == 2) {
                currentPosition = bundle.getInt("cposition");
            }
        } catch (Exception e) {
            Log.e(TAG, "initArgs: " + e.toString());
        }


        Log.e(TAG, "initArgs: " + (musicList != null));
        return musicList != null;
    }

    @Override
    protected void initData() {
        super.initData();
        if (sendType == 1) {
            sendMessage(index);
        }
        if (myMUsicPlayBroadcastReceiver == null) {
            myMUsicPlayBroadcastReceiver = new MyMUsicPlayBroadcastReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Broadcast.B);//com.xxf.mysbcreceiver
            registerReceiver(myMUsicPlayBroadcastReceiver, intentFilter);
        }
        maxIndex = musicList.size() - 1;
        maxPosition = musicList.get(index).getLength();
        ivPlayPlaybe.setBackgroundResource(R.drawable.ic_action_play);
        tvPlayCurrenttime.setText(StringUtil.getFormatHMS(currentPosition));
        tvPlayMaxtime.setText(StringUtil.getFormatHMS(maxPosition));
        //  mediaPlayer = new MediaPlayer();
        //音乐播放完成的监听
        //  mediaPlayer.setOnCompletionListener(listener);
        View l = LayoutInflater.from(this).inflate(R.layout.title_layout, null);
        musicName = l.findViewById(R.id.title);
        musicName.setText(musicList.get(index).getTitle());
        l.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        setTitleview(l);
        // prepare();
        //   receiver = new MyBroadcastReceiver();
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction("com.xxf.broadcastreceiver");
        // registerReceiver(receiver, intentFilter);
//        receiver.setMessage(new MyBroadcastReceiver.Message() {
//            @Override
//            public void getMUsic(Music musicList) {
//
//            }
//        });

        // 数值改变----onProgressChanged
        // 开始拖动----onStartTrackingTouch
        // 停止拖动----onStopTrackingTouch
        seekbar.setProgress(currentPosition);
        seekbar.setMax(musicList.get(index).getLength());
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (isTouch) {
                    currentPosition = seekBar.getProgress();
                    tvPlayCurrenttime.setText(StringUtil.getFormatHMS(currentPosition));
                    sendMessage(-2, seekBar.getProgress());
                    // mediaPlayer.pause();
                    // mediaPlayer.seekTo(seekBar.getProgress());
                    // mediaPlayer.start();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isTouch = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isTouch = false;
            }
        });


    }


    /*//音乐播放完成的监听
    private MediaPlayer.OnCompletionListener listener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            Log.e(TAG, "onCompletion: " + "播放完成");
            ivPlayPlaybe.setBackgroundResource(R.drawable.ic_action_stap);
            //
            mp.reset();
            playNext();
        }
    };*/


    @OnClick({R.id.iv_play_flowtype, R.id.iv_play_previous, R.id.iv_play_playbe, R.id.iv_play_next, R.id.iv_play_list})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_play_flowtype:
                break;
            case R.id.iv_play_previous:
                sendMessage(com.xxf.mymusic.constant.Music.PLAY_PREVIOUS);

                break;
            case R.id.iv_play_playbe:
                sendMessage(com.xxf.mymusic.constant.Music.PLAY_PAUSE);
                break;
            case R.id.iv_play_next:
                sendMessage(com.xxf.mymusic.constant.Music.PLAY_NEXT);
                break;
            case R.id.iv_play_list:
                break;
        }
    }

    /*****************计时器*******************/
    private Runnable timeRunable = new Runnable() {
        @Override
        public void run() {
            //getCurrentPosition();
            // if (mediaPlayer.isPlaying()) {
            seekbar.setProgress(currentPosition);
            tvPlayCurrenttime.setText(StringUtil.getFormatHMS(currentPosition));
            // if (!isPause) {
            //递归调用本runable对象，实现每隔一秒一次执行任务
            mhandle.postDelayed(this, 1000);
            //  }
            // } else {

            // }
        }
    };
    //计时器
    private Handler mhandle = new Handler();
    private boolean isPause = false;//是否暂停

    /*****************计时器*******************/

    public void sendMessage(int playint) {
        Intent intent = new Intent();
        intent.setAction(Broadcast.A1);
        intent.putExtra("PLAY", playint);
        Log.e(TAG, "sendMessage: " + playint);
        //intent.setAction("XXX.XXX");
        // intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        // intent.setComponent(new ComponentName(getApplication().getPackageName(), "com.xxf.com.mysbcreceiver"));
        sendBroadcast(intent);

    }

    public void sendMessage(int playint, int progress) {
        Intent intent = new Intent();
        intent.setAction(Broadcast.A1);
        intent.putExtra("PLAY", playint);
        intent.putExtra("PROGRESS", progress);
        Log.e(TAG, "sendMessage: " + playint);
        sendBroadcast(intent);
    }

    public void upUi(int index) {
        musicName.setText(musicList.get(index).getTitle());
        maxPosition = musicList.get(index).getLength();
        seekbar.setMax(maxPosition);
        tvPlayCurrenttime.setText("00:00");
        tvPlayMaxtime.setText(StringUtil.getFormatHMS(maxPosition));
    }


    @OnClick(R.id.line_music_content)
    public void onViewClicked() {
        OkHttpUtils.getInstance(this).doGet(OnlineLrcUtil.queryLrcURLRoot + musicList.get(index).getTitle() + "/"// + musicList.get(index).getArtist()
                , new OnOkHttpListener() {
            @Override
            public void onTokenError() {

            }

            @Override
            public void onSuccess(String response) {
                lyricContentList = JSON.parseArray(response, LyricContent.class);
                handler.sendEmptyMessage(0x10);
            }

            @Override
            public void onFailure(String error) {

            }

            @Override
            public void onMsg(String msg) {

            }

            @Override
            public void onEmpty() {
                Log.e(TAG, "onEmpty: 没有找到");
            }
        });


    }

    private class MyMUsicPlayBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Broadcast.B)) {
                switch (intent.getIntExtra("type", -1)) {
                    case 1://是否播放
                        isplay = intent.getBooleanExtra("isplay", isplay);
                        if (isplay) {
                            tvPlayCurrenttime.setText(StringUtil.getFormatHMS(intent.getIntExtra("position", 0)));
                            seekbar.setProgress(intent.getIntExtra("position", 0));
                            ivPlayPlaybe.setBackgroundResource(R.drawable.ic_action_stap);
                        } else {
                            ivPlayPlaybe.setBackgroundResource(R.drawable.ic_action_play);
                        }
                        break;

                    case 2://播放切换
                        int ind = intent.getIntExtra("index", -1);
                        if (ind != -1) {
                            index = ind;
                            upUi(index);
                        }
                        break;

                    /*case 3://播放进度

                        break;*/
                }


            }
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x10:
                    List<Map<String, String>> listems = new ArrayList<>();
                    for (int i = 0; i < lyricContentList.size(); i++) {
                        Map<String, String> listem = new HashMap<>();
                        listem.put("name", lyricContentList.get(i).getLyric());
                        listem.put("id", lyricContentList.get(i).getLyricTime() + "");
                        listems.add(listem);
                    }
                    new ListDialog(MusicPlayActivity.this, 0, "请选择", listems, new ListDialog.OnItemlist() {
                        @SuppressWarnings("AlibabaRemoveCommentedCode")
                        @Override
                        public void onitemClick(AdapterView<?> adapterView, View view, int i, long l) {

                            try {
                                if (lyricContentList == null) {
                                    lyricContentList = OnlineLrcUtil.getInstance(MusicPlayActivity.this).getLyricContent(musicList.get(index).getTitle(), musicList.get(index).getArtist());
                                } else {
                                    Log.e(TAG, "onViewClicked: " + lyricContentList.toString());
                                }
                            } catch (Exception e) {
                                lyricContentList = new ArrayList<>();
                                Log.e(TAG, "onViewClicked: null");
                            }
                        }
                    }).show();
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // mediaPlayer.stop();
        unregisterReceiver(myMUsicPlayBroadcastReceiver);
    }
}
