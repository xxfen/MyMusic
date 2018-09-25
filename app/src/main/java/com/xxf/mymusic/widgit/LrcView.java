package com.xxf.mymusic.widgit;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.xxf.mymusic.bean.LyricContent;

import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * author：xxf
 */
public class LrcView extends View {
    //控件宽高
    private int mWith;
    private int mHeight;
    private Paint tPaint;
    private Rect oRect;
    private List<LyricContent> lyricContents;
    private int crrentPosition;
    private int lineSpace;
    private int lineLrcStartTime;
    private int lineLrcEndTime;
    private int lrcHeight;


    public LrcView(Context context) {
        super(context);
    }

    public LrcView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {

        //-------------
        oRect = new Rect();
        tPaint = new Paint();
        tPaint.setAntiAlias(true);
        tPaint.setStyle(Paint.Style.FILL);
        tPaint.setTextSize(45);
        tPaint.setAntiAlias(true);
        tPaint.setStrokeWidth(4);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWith = MeasureSpec.getSize(widthMeasureSpec);    //取出宽度的确切数值
        mHeight = MeasureSpec.getSize(heightMeasureSpec);    //取出高度的确切数值
        // lrcHeight = mHeight / 2;
    }

    private int lineLrcIndex = -1;
    private int uplrcHeight;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (lyricContents != null) {
            canvas.translate(mWith / 2, uplrcHeight + 30);
            // Log.e(TAG, "onDraw: " + uplrcHeight);
            for (int j = 0; j < lyricContents.size(); j++) {
                if (lyricContents.get(j).getLyric() != null) {
                    if (j == lineLrcIndex) {
                        drawCentreText(canvas, lyricContents.get(j).getLyric(), j, Color.GREEN);
                    } else {
                        if (j + 8 >= lineLrcIndex && j - 7 <= lineLrcIndex) {
                            drawCentreText(canvas, lyricContents.get(j).getLyric(), j, Color.BLUE);
                        }

                    }
                }
            }

        } else {
            canvas.translate(mWith / 2, mHeight / 2);
            drawCentreText(canvas, "暂无歌词", 0, Color.BLUE);
        }
    }

    private boolean isDestroy = false;
    private int Count = 0;
    /*****************计时器*******************/
    private Runnable timeRunable = new Runnable() {
        @Override
        public void run() {
            Log.e(TAG, "run---: 22222");
            if (!isDestroy) {
                Log.e(TAG, "run---: false");
                if (Count < 20) {
                    Log.e(TAG, "run---: i=" + Count);
                    uplrcHeight = lrcHeight + lineSpace * (20 - Count) / 20;
                    invalidate();
                    ++Count;
                    mhandle.postDelayed(this, 1);//递归调用本runable对象，实现每隔xxx一次执行任务
                } else {
                    isDestroy = true;
                    Count = 0;
                }
            }


        }
    };
    //计时器
    private Handler mhandle = new Handler();
    //private boolean isPause = false;//是否暂停

    /*****************计时器*******************/


    private void drawCentreText(Canvas canvas, String text, int lines, int color) {
        tPaint.setColor(color);
        tPaint.getTextBounds(text, 0, text.length(), oRect);
        // tPaint.setColor(Color.BLACK);
        /*
         * 控件宽度/2 - 文字宽度/2
         */
        float v = tPaint.measureText(text);//文字宽度
        float startX = -v / 2;

        /*
         * 控件高度/2 + 文字高度/2,绘制文字从文字左下角开始,因此"+"
         */
        // float startY = getHeight() / 2 + oRect.height() / 2;
       /* Paint.FontMetricsInt fm = tPaint.getFontMetricsInt();
        //fm.bottom - fm.top//文字高度
        int startY = -fm.descent + (fm.bottom - fm.top) / 2;*/
        if (lineSpace == 0) {
            Paint.FontMetricsInt fm = tPaint.getFontMetricsInt();
            //fm.bottom - fm.top//文字高度
            int startY = -fm.descent + (fm.bottom - fm.top) / 2;
            lineSpace = 70 + startY;
        }
        // 绘制文字
        canvas.drawText(text, startX, lineSpace - 70 + lineSpace * lines, tPaint);
    }

    public void setLyricContents(List<LyricContent> lyricContents) {
        this.lyricContents = lyricContents;
    }


    public void upCrrentPosition(int crrentPosition) {
        this.crrentPosition = crrentPosition;
        int index = 0;

        Log.e(TAG, "upCrrentPosition: " + crrentPosition);
        if (lyricContents != null) {
            getCrrentLine:
            for (int i = 0; i < lyricContents.size(); i++) {
                lineLrcStartTime = lyricContents.get(i).getLyricTime();
                if (i + 1 < lyricContents.size()) {

                    lineLrcEndTime = lyricContents.get(i + 1).getLyricTime();
                    if (crrentPosition >= lineLrcStartTime && crrentPosition <= lineLrcEndTime) {
                        index = i;
                        Log.e(TAG, "onDraw: " + lineLrcIndex);
                        break getCrrentLine;
                    }
                } else if (i + 1 == lyricContents.size()) {
                    if (crrentPosition >= lineLrcStartTime) {
                        index = i;
                        Log.e(TAG, "onDraw: " + lineLrcIndex);
                        break getCrrentLine;
                    }
                }
            }
            if (index != lineLrcIndex) {
                Log.e(TAG, "upCrrentPosition---: 不等于");
                lineLrcIndex = index;
                lrcHeight = mHeight / 2 - lineLrcIndex * lineSpace;

                /*if (lineLrcEndTime - lineLrcStartTime < 1000) {

                }*/
                //invalidate();
                isDestroy = false;
                timeRunable.run();

            }

        }
    }


}