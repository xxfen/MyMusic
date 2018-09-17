package com.xxf.mymusic.widgit;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.xxf.mymusic.R;

import java.util.List;
import java.util.Map;


/**
 * Created by Administrator on 2018/3/6 0006.
 */

public class ListDialog extends Dialog implements View.OnClickListener {
    private ListView listDialog;
    private LinearLayout lineDialogListcontent;
    private TextView CancelTxt;
    private TextView PromptTxt;
    private Context mContext;
    private OnItemlist onItemlist;
    private String title;
    private List<Map<String, String>> listems;

    public ListDialog(Context context) {
        super(context);
    }

    /**
     * list列表对话框
     *
     * @param context
     * @param themeResId 对话框样式
     * @param listems    集合
     * @param listener   回调监听
     */
    public ListDialog(Context context, int themeResId, List<Map<String, String>> listems, final OnItemlist listener) {
        super(context, themeResId);
        new ListDialog(context, themeResId, null, listems, listener);
    }

    /**
     * list列表对话框
     *
     * @param context
     * @param themeResId 对话框样式
     * @param listems    集合
     * @param listener   回调监听
     */
    public ListDialog(Context context, int themeResId, String title, List<Map<String, String>> listems, final OnItemlist listener) {
        super(context, themeResId);
        this.mContext = context;
        this.listems = listems;
        this.title = title;
        this.onItemlist = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_com);
        setCanceledOnTouchOutside(false);
        listDialog = (ListView) findViewById(R.id.list_dialog);
        lineDialogListcontent = (LinearLayout) findViewById(R.id.line_dialog_listcontent);
        CancelTxt = findViewById(R.id.tv_dialogcom_cancel);
        PromptTxt = findViewById(R.id.tv_dialogcom_title);
        CancelTxt.setOnClickListener(this);
        if (title == null) {
            PromptTxt.setVisibility(View.GONE);
        } else {
            PromptTxt.setText(title);
        }
        lineDialogListcontent.setVisibility(View.VISIBLE);
        listDialog.setAdapter(new SimpleAdapter(mContext, listems, R.layout.dialog_listitem, new String[]{"name"}, new int[]{R.id.tv_dialog_item}));
        listDialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                onItemlist.onitemClick(adapterView, view, i, l);
                dismiss();
            }
        });
    }

    /**
     * 输入对话框提交回调接口
     */
    public interface OnCloseListener {
        void oneditClick(boolean boo);
    }

    /**
     * 列表对话框点击回调接口
     */
    public interface OnItemlist {
        void onitemClick(AdapterView<?> adapterView, View view, int i, long l);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_dialogcom_cancel:
                this.dismiss();
                break;
           /* case R.id.tv_dialogcont_cancel:
                onCloseListener.oneditClick(false);
                this.dismiss();
                break;*/
            /*case R.id.tv_dialogcont_determine:
                onCloseListener.oneditClick(true);
                this.dismiss();
                break;*/

        }
    }
}
