package com.app.l.learningapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.app.l.learningapp.activities.CustomViewAct;
import com.app.l.learningapp.activities.SwipeRefreshAct;
import com.app.l.learningapp.utils.MyLog;

/**
 * Created by liang on 15/7/28.
 */
public class LauncherAct extends Activity implements View.OnClickListener {

    private Button swipeButton, countViewButton;

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_actlayout);

        swipeButton = (Button) findViewById(R.id.swipe_refresh);
        swipeButton.setOnClickListener(this);

        countViewButton = (Button) findViewById(R.id.count_view);
        countViewButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.swipe_refresh:
                intent = new Intent(this, SwipeRefreshAct.class);
                break;
            case R.id.count_view:
                intent = new Intent(this, CustomViewAct.class);
                intent.putExtra("layout", R.layout.countview_layout);
                break;
            default:
                break;
        }
        startActivity(intent);
    }
}
