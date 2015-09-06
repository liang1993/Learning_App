package com.app.l.learningapp.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.app.l.learningapp.R;

/**
 * Created by liang on 15/9/6.
 * 背景为进度展示的组合控件
 */
public class ProgressView extends FrameLayout{
    private ProgressBar progressBar;
    private TextView title;
    private TextView des;
    public ProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.prgress_layout, this);
        title = (TextView)this.findViewById(R.id.title);
        des = (TextView)this.findViewById(R.id.des);
        progressBar = (ProgressBar)this.findViewById(R.id.progress);
    }
    /*
    工作时需要实现一个月份显示日期进度的View，在这通过组合控件的形式实现
    progressBar充满了整个背景，可以通过调用progressBar的api来
    调整背景颜色的比例，已达到显示进度的目的。
    具体的方法实现比较简单就在这里省略了。
     */
}
