package com.app.l.learningapp.activities;

import android.app.Activity;
import android.os.Bundle;

import com.app.l.learningapp.R;

/**
 * Created by liang on 15/7/28.
 */
public class CustomViewAct extends Activity {

    private int layoutId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        layoutId = getIntent().getIntExtra("layout", R.layout.wrong_layout);

        setContentView(layoutId);
    }
}
