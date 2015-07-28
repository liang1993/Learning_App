package com.app.l.learningapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.app.l.learningapp.activities.CustomViewAct;
import com.app.l.learningapp.activities.SwipeRefreshAct;

/**
 * Created by liang on 15/7/28.
 */
public class LauncherAct extends Activity implements View.OnClickListener {

    private Button swipeButton, countViewButton, titleViewButton,listViewButton;

    private Intent intent;

    private final String INTENTTAG = "layout";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_actlayout);

        swipeButton = (Button) findViewById(R.id.swipe_refresh);
        swipeButton.setOnClickListener(this);

        countViewButton = (Button) findViewById(R.id.count_view);
        countViewButton.setOnClickListener(this);

        titleViewButton = (Button) findViewById(R.id.title_view);
        titleViewButton.setOnClickListener(this);

        listViewButton = (Button)findViewById(R.id.my_list_view);
        listViewButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.swipe_refresh:
                intent = new Intent(this, SwipeRefreshAct.class);
                break;
            case R.id.count_view:
                intent = new Intent(this, CustomViewAct.class);
                intent.putExtra(INTENTTAG, R.layout.countview_layout);
                break;
            case R.id.title_view:
                intent = new Intent(this, CustomViewAct.class);
                intent.putExtra(INTENTTAG, R.layout.title_view_demo);
            case R.id.my_list_view:
                intent = new Intent(this, CustomViewAct.class);
                intent.putExtra(INTENTTAG,R.layout.mylistview_demo_layout);
            default:
                break;
        }
        startActivity(intent);
    }
}
