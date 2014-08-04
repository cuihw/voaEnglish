package com.example.zztest;

import java.util.ArrayList;
import java.util.HashMap;

import com.example.zztest.downloader.LocalFileCache;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class MainActivity extends Activity {

    protected static final String TAG = "MainActivity";

    private ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();

    private ListView mListView;

    private OnItemClickListener mItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            Log.d(TAG, "你点击了第" + position + "行");
            
            if (position == 4) {
                Intent intent = new Intent(MainActivity.this, ArticleActivity.class);
                intent.putExtra("channelindex", position);
                startActivity(intent);
                return;
            }

            Intent intent = new Intent(MainActivity.this, ChannelListViewActivity.class);
            intent.putExtra("channelindex", position);
            startActivity(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initListView();
        
        LocalFileCache.getInstance();
    }

    private void initListView() {

        Log.d(TAG, "initListView............");
        mListView = (ListView) findViewById(R.id.home_listview);

        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("ItemImage", R.drawable.home);
        map.put("ItemText", "每日更新");
        listItem.add(map);

        map = new HashMap<String, Object>();
        map.put("ItemImage", R.drawable.special);
        map.put("ItemText", "慢速英语");
        listItem.add(map);

        map = new HashMap<String, Object>();
        map.put("ItemImage", R.drawable.standard);
        map.put("ItemText", "标准英语");
        listItem.add(map);

        map = new HashMap<String, Object>();
        map.put("ItemImage", R.drawable.learning);
        map.put("ItemText", "英语教学");
        listItem.add(map);

        map = new HashMap<String, Object>();
        map.put("ItemImage", R.drawable.local_file);
        map.put("ItemText", "本地文档");
        listItem.add(map);

        SimpleAdapter mSimpleAdapter = new SimpleAdapter(this, listItem, R.layout.home_list_item, new String[] {"ItemImage", "ItemText"},
                        new int[] {R.id.img_item, R.id.text_item});

        mListView.setAdapter(mSimpleAdapter);

        mListView.setOnItemClickListener(mItemClickListener);
    }

}
