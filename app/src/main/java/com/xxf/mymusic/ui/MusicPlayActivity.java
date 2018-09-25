package com.xxf.mymusic.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.xxf.mymusic.R;
import com.xxf.mymusic.base.BaseActivity;
import com.xxf.mymusic.bean.LyricContent;
import com.xxf.mymusic.bean.LyricListContent;
import com.xxf.mymusic.bean.Music;
import com.xxf.mymusic.constant.Broadcast;
import com.xxf.mymusic.i.OnOkHttpListener;
import com.xxf.mymusic.util.OkHttpUtils;
import com.xxf.mymusic.util.StringUtil;
import com.xxf.mymusic.widgit.LrcView;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

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
    @BindView(R.id.lrcView)
    LrcView lrcView;

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
    private List<LyricContent> lyricContents;
    private List<LyricListContent> lyricListContents;

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
        getLrcList(musicList.get(index).getTitle() + "-" + musicList.get(index).getArtist(), musicList.get(index).getLength() + "");
        // 数值改变----onProgressChanged
        // 开始拖动----onStartTrackingTouch
        // 停止拖动----onStopTrackingTouch
        seekbar.setMax(musicList.get(index).getLength());
        seekbar.setProgress(currentPosition);
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
    /*private Runnable timeRunable = new Runnable() {
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
    private boolean isPause = false;//是否暂停*/

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
        getLrcList(musicList.get(index).getTitle() + "-" + musicList.get(index).getArtist(), musicList.get(index).getLength() + "");
        musicName.setText(musicList.get(index).getTitle());
        maxPosition = musicList.get(index).getLength();
        seekbar.setMax(maxPosition);
        //seekbar.setProgress(currentPosition);
        tvPlayCurrenttime.setText("00:00");
        tvPlayMaxtime.setText(StringUtil.getFormatHMS(maxPosition));
    }


    @OnClick(R.id.line_music_content)
    public void onViewClicked() {

    }


    private class MyMUsicPlayBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Broadcast.B)) {
                switch (intent.getIntExtra("type", -1)) {
                    case 1://是否播放
                        isplay = intent.getBooleanExtra("isplay", isplay);
                        if (isplay) {
                            int position = intent.getIntExtra("position", 0);
                            tvPlayCurrenttime.setText(StringUtil.getFormatHMS(position));
                            //播放进度
                            seekbar.setProgress(position);
                            lrcView.upCrrentPosition(position);
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

                    /*case 3:

                        break;*/
                }


            }
        }
    }

    public int itemId;
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x10:
                    //  if (lyricContents.isEmpty()) {
                    // tvLrcone.setText("歌词未找到!");
                    //  }
                    lrcView.setLyricContents(lyricContents);
                    lrcView.upCrrentPosition(currentPosition);
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


    private void getLrcList(String name, String duration) {
        String urlStr = null;
        try {
            urlStr = "http://lyrics.kugou.com/search?ver=1&man=yes&client=pc&keyword=" +
                    URLEncoder.encode(name, "utf-8") + "&duration=" +
                    URLEncoder.encode(duration, "utf-8") + "&hash=";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        OkHttpUtils.getInstance(this).doGet(urlStr, new OnOkHttpListener() {

            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int status = jsonObject.getInt("status");
                    if (status == 200) {
                        String candidates = jsonObject.getString("candidates");
                        lyricListContents = JSON.parseArray(candidates, LyricListContent.class);
                        Log.e(TAG, "onSuccess: " + lyricListContents.toString());
                        getLrcContent(URLEncoder.encode(lyricListContents.get(0).getId(), "utf-8"), URLEncoder.encode(lyricListContents.get(0).getAccesskey(), "utf-8"));
                    }


                    // handler.sendEmptyMessage(0x10);
                } catch (Exception e) {
                    Log.e(TAG, "onSuccess: " + e.toString());
                }
            }

            @Override
            public void onFailure(String error) {

            }

        });
    }

    private void getLrcContent(String id, String accesskey) {
        String urlStr = null;
        try {
            urlStr = "http://lyrics.kugou.com/download?ver=1&client=pc&id=" +
                    id +
                    "&accesskey=" +
                    accesskey +
                    "&fmt=lrc&charset=utf8";
        } catch (Exception e) {
            e.printStackTrace();
        }
        OkHttpUtils.getInstance(this).doGet(urlStr, new OnOkHttpListener() {

            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int status = jsonObject.getInt("status");
                    if (status == 200) {
                        String content = jsonObject.getString("content");
                        //base64解码
                        String str2 = new String(Base64.decode(content.getBytes(), Base64.DEFAULT));

                        //Log.e(TAG, "onSuccess: " + str2);
                        lyricContents = new ArrayList<>();
                        if (str2 != null) {
                            //替换字符
                            str2 = str2.replace("[", "");
                            str2 = str2.replace("]", "@");
                            Log.e(TAG, "onSuccess: str2===" + str2);
                            do {
                                //截取\n之前的字符串
                                String str = str2.substring(0, str2.indexOf("\n"));
                                // Log.e(TAG, "onSuccess: str===" + str);
                                String splitLrcData[] = str.split("@");
                                if (splitLrcData.length > 1) {
                                    LyricContent mLyricContent = new LyricContent();
                                    int LyricTime = TimeStr(splitLrcData[0]);// 取数组里面的第1个数据，放到TimeStr里都转成秒为单位后出来作为歌词时间。0 400 9490 12490 15860 15860 35560
                                    mLyricContent.setLyricTime(LyricTime);
                                    mLyricContent.setLyric(splitLrcData[1]);// [00:00.00, 我爱歌词网 www.5ilrc.com],取数组里面的第2个数据作为歌词。
                                    lyricContents.add(mLyricContent);
                                }
                                //截取\n之后的字符,并覆盖原值
                                str2 = str2.substring(str2.indexOf("\n") + 1);
                                //Log.e(TAG, "onSuccess: str2===" + str2);

                            } while (str2.indexOf("\n") != -1);

                        }
                        Log.e(TAG, "onSuccess: aa" + lyricContents.toString());
                        handler.sendEmptyMessage(0x10);

                    }
                    // handler.sendEmptyMessage(0x10);
                } catch (Exception e) {
                    Log.e(TAG, "onSuccess: " + e.toString());
                }
            }

            @Override
            public void onFailure(String error) {

            }
        });
    }

    public int TimeStr(String timeStr) {// 00:40.57
        timeStr = timeStr.replace(":", ".");//00.40.57
        timeStr = timeStr.replace(".", "@");//00@40@57
        String timeData[] = timeStr.split("@");//[00, 40, 57]
        int minute = Integer.parseInt(timeData[0]);//数组里的第1个数据是分0
        int second = Integer.parseInt(timeData[1]);//数组里的第2个数据是秒40
        int millisecond = Integer.parseInt(timeData[2]);//数组里的第3个数据是秒57
        int currentTime = (minute * 60 + second) * 1000 + millisecond * 10;//40000+570=40570
        return currentTime;
    }


}
