package com.app.l.learningapp.activities;

import android.app.Activity;
import android.os.Bundle;

import com.app.l.learningapp.R;
import com.app.l.learningapp.customview.mylistview.MyListView;
import com.app.l.learningapp.customview.mylistview.MyListViewAd;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liang on 15/7/28.
 */
public class CustomViewAct extends Activity {

    private int layoutId;

    private MyListView myListView;

    private MyListViewAd mAdapter;

    private List<String> contentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        layoutId = getIntent().getIntExtra("layout", R.layout.wrong_layout);

        setContentView(layoutId);
        if (layoutId == R.layout.mylistview_demo_layout) {
            myListView = (MyListView) findViewById(R.id.my_list_view);
            initList();
            myListView.setOndeleteListener(new MyListView.OnDeleteListener() {
                @Override
                public void onDelete(int index) {
                    contentList.remove(index);
                    mAdapter.notifyDataSetChanged();
                }
            });
            mAdapter = new MyListViewAd(this, 0, contentList);
            myListView.setAdapter(mAdapter);
        }
    }

    private void initList() {
        contentList.add("Content Item 1");
        contentList.add("Content Item 2");
        contentList.add("Content Item 3");
        contentList.add("Content Item 4");
        contentList.add("Content Item 5");
        contentList.add("Content Item 6");
        contentList.add("Content Item 7");
        contentList.add("Content Item 8");
        contentList.add("Content Item 9");
        contentList.add("Content Item 10");
        contentList.add("Content Item 11");
        contentList.add("Content Item 12");
        contentList.add("Content Item 13");
        contentList.add("Content Item 14");
        contentList.add("Content Item 15");
        contentList.add("Content Item 16");
        contentList.add("Content Item 17");
        contentList.add("Content Item 18");
        contentList.add("Content Item 19");
        contentList.add("Content Item 20");
    }
}
