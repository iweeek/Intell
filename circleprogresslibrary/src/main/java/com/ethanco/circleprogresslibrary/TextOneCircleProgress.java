package com.ethanco.circleprogresslibrary;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ethanco.circleprogresslibrary.utils.DisplayUtil;

/**
 * @Description 具有文本显示的CircleProgress
 * Created by YOLANDA on 2015-12-11.
 */
public class TextOneCircleProgress extends RelativeLayout {
    private final int headColor;
    private final int subHeadColor;
    private final float headSize;
    private final float subHeadSize;
    private final TextView tvBottomHead;
    private final int bottomHeadColor;
    private final float bottomHeadSize;
    private TickCircleProgress tickCircleProgress;
    private final TextView tvHead;
    private final TextView tvSubHead;

    public TextOneCircleProgress(Context context) {
        this(context, null);
    }

    public TextOneCircleProgress(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TextOneCircleProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TextCircleProgress);
        headColor = ta.getColor(R.styleable.TextCircleProgress_headColor, Color.BLACK);
        subHeadColor = ta.getColor(R.styleable.TextCircleProgress_subHeadColor, Color.GRAY);
        headSize = DisplayUtil.px2sp(context, ta.getDimension(R.styleable.TextCircleProgress_headSize, 30));
        subHeadSize = DisplayUtil.px2sp(context, ta.getDimension(R.styleable.TextCircleProgress_subHeadSize, 18));
        bottomHeadColor = ta.getColor(R.styleable.TextCircleProgress_bottomHeadColor, Color.BLACK);
        bottomHeadSize = DisplayUtil.px2sp(context, ta.getDimension(R.styleable.TextCircleProgress_bottomHeadSize, 22));
        ta.recycle();

        View contentView = View.inflate(context, R.layout.layout_circleprogress_text_good, this);
        tickCircleProgress = (TickCircleProgress) contentView.findViewById(R.id.tickCircleProgress2);
        tvHead = (TextView) contentView.findViewById(R.id.tvHead2);
        tvSubHead = (TextView) contentView.findViewById(R.id.tvSubhead2);
        tvBottomHead = (TextView) contentView.findViewById(R.id.tvBottomHead2);

        tvHead.setTextColor(headColor);
        tvHead.setTextSize(headSize);
        tvSubHead.setTextColor(subHeadColor);
        tvSubHead.setTextSize(subHeadSize);
        tvBottomHead.setTextColor(bottomHeadColor);
        tvBottomHead.setTextSize(bottomHeadSize);
    }

    // ============================================================
    // = Z- 对外开放的方法
    // ============================================================

    /**
     * 设置子进度条进度
     *
     * @param progress
     */
    public void setSubProgress(int progress) {
        tickCircleProgress.setProgress(progress);
    }

    /**
     * 设置主标题
     *
     * @param text
     */
    public void setHead(String text) {
        tvHead.setText(text);
    }

    /**
     * 设置副标题
     *
     * @param text
     */
    public void setSubHead(String text) {
        tvSubHead.setText(text);
    }

    /**
     * 设置底部标题
     *
     * @param text
     */
    public void setBottomHead(String text) {
        tvBottomHead.setText(text);
    }
}
