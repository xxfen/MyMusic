package com.xxf.mymusic.base;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.xxf.mymusic.R;
import com.xxf.mymusic.constant.Broadcast;
import com.xxf.mymusic.service.MyMusicService;

import java.util.HashSet;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * author：xxf
 */
public abstract class BaseActivity extends AppCompatActivity {
    private Unbinder mUnbinder;
    private MyBaseActiviy_Broad oBaseActiviy_Broad;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //在界面初始化之前初始化窗口
        initWindows();
        if (initArgs(getIntent().getExtras())) {
            int layoutId = getContentLayoutId();
            setContentView(layoutId);
            //绑定初始化ButterKnife
            ButterKnife.bind(this);
            //动态注册广播
            if (oBaseActiviy_Broad == null)
                oBaseActiviy_Broad = new MyBaseActiviy_Broad();
            IntentFilter intentFilter = new IntentFilter(Broadcast.A2);
            registerReceiver(oBaseActiviy_Broad, intentFilter);
            initWidget();
            initData();

        } else {

            try {
                throw new Exception("传递参数错误");
            } catch (Exception e) {
                e.printStackTrace();
            }
            finish();
        }
    }


    /**
     * 初始化窗口
     */
    protected void initWindows() {

    }

    /**
     * 初始化参数
     *
     * @param bundle 需要初始化的参数
     * @return 如果参数正确返回true, 错误返回false
     */
    protected boolean initArgs(Bundle bundle) {


        return true;
    }

    /**
     * 获得当前界面的资源文件Id
     *
     * @return 界面的资源文件Id
     */
    protected abstract int getContentLayoutId();

    /**
     * 初始化控件
     */
    protected void initWidget() {
        mUnbinder = ButterKnife.bind(this);
    }

    /**
     * 初始化数据
     */
    protected void initData() {

    }

    /**
     * 跳转 不带参
     *
     * @param cl
     */
    public void startActivity(Class cl) {
        startActivity(cl, null);
    }

    public void setTitleview(View l) {
        LinearLayout layout = (LinearLayout) ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 120);
        params.setMargins(20, 20, 20, 20);//4个参数按顺序分别是设置左上右下边距
        if (l.getParent() != null)
            ((ViewGroup) l.getParent()).removeView(l);

        layout.addView(l, 0);

    }


    /**
     * 跳转 带参
     *
     * @param cl
     */
    public void startActivity(Class cl, Bundle bundle) {
        Intent intent = new Intent(this, cl);
        if (bundle == null) {
            startActivity(intent);
        } else {
            intent.putExtras(bundle);
            startActivity(intent);
        }

    }

    /**
     * 跳转 带参
     *
     * @param cl
     */
    public void startActivity(Class cl, Bundle bundle, int flags) {
        Intent intent = new Intent(this, cl);
        intent.setFlags(flags);
        if (bundle == null) {
            startActivity(intent);
        } else {
            intent.putExtras(bundle);
            startActivity(intent);
        }

    }

    /**
     * 跳转 带接收返回值 不带参
     *
     * @param cl
     * @param rcode
     */
    public void startActivityForResult(Class cl, int rcode) {
        startActivityForResult(cl, null, rcode);
    }

    /**
     * 跳转 带接收返回值 带参
     *
     * @param cl
     * @param rcode
     */
    public void startActivityForResult(Class cl, Bundle bundle, int rcode) {
        Intent intent = new Intent(this, cl);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, rcode);
    }


    @Override
    public void onBackPressed() {
        //获得当前Activity的所有Fragment
        @SuppressWarnings("RestrictedApi")
        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
        if (fragmentList != null && fragmentList.size() > 0) {
            for (Fragment fragment : fragmentList) {
                //是否是我们自定义的Fragment
                if (fragment instanceof BaseFragment) {
                    if (((BaseFragment) fragment).onBackPressed()) {
                        //Fragment处理了返回事件
                        return;
                    }
                }
            }
        }
        super.onBackPressed();
        finish();
    }

    //在销毁的方法里面注销广播
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(oBaseActiviy_Broad);//注销广播

    }

    //定义一个广播
    public class MyBaseActiviy_Broad extends BroadcastReceiver {

        public void onReceive(Context arg0, Intent intent) {
            //接收发送过来的广播内容
            int closeAll = intent.getIntExtra("closeAll", 0);
            if (closeAll == 1) {
                Intent stopservice = new Intent(arg0, MyMusicService.class);
                stopService(stopservice);
                finish();//销毁BaseActivity
            }
        }

    }
}
