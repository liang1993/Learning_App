package com.app.l.learningapp;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.app.l.learningapp.activities.CustomViewAct;
import com.app.l.learningapp.activities.SwipeRefreshAct;

/**
 * Created by liang on 15/7/28.
 */
public class LauncherAct extends AppCompatActivity implements View.OnClickListener {

    private Button swipeButton, countViewButton, titleViewButton,listViewButton
            ,frescoButton;

    private Intent intent;

    private final String INTENTTAG = "layout";

    private Toolbar toolbar;

    private DrawerLayout mDrawerLayout;

    private ListView listView;

    private String[] lvs = {"List Item 01", "List Item 02", "List Item 03", "List Item 04"};

    private ArrayAdapter arrayAdapter;

    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_actlayout);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.dl_left);
        listView = (ListView)findViewById(R.id.lv_left_menu);
        toolbar.setTitle("ToolBar");
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);//返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, lvs);
        listView.setAdapter(arrayAdapter);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.draw_open, R.string.draw_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        mDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        swipeButton = (Button) findViewById(R.id.swipe_refresh);
        swipeButton.setOnClickListener(this);

        countViewButton = (Button) findViewById(R.id.count_view);
        countViewButton.setOnClickListener(this);

        titleViewButton = (Button) findViewById(R.id.title_view);
        titleViewButton.setOnClickListener(this);

        listViewButton = (Button)findViewById(R.id.my_list_view);
        listViewButton.setOnClickListener(this);

        frescoButton = (Button)findViewById(R.id.fresco_demo);
        frescoButton.setOnClickListener(this);
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
            case R.id.fresco_demo:
                intent = new Intent(this,CustomViewAct.class);
                intent.putExtra(INTENTTAG,R.layout.fresco_demo_layout);
            default:
                break;
        }
        startActivity(intent);
    }
}
