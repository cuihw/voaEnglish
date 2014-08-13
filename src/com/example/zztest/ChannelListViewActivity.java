package com.example.zztest;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zztest.downloader.ArticleFile;
import com.example.zztest.downloader.LocalFileCache;

public class ChannelListViewActivity extends Activity {

    private int mChannleIndex = 0;

    private static GrepChannelFormWebpage mGrepWebpageStandard_update;

    private static GrepChannelFormWebpage mGrepWebpageSpecial_update;

    private static GrepChannelFormWebpage mGrepWebpageLearning_update;

    private static GrepChannelFormWebpage mGrepWebpage;

    private ListView mListView;

    ChannelListViewAdapter mChannelListViewAdapter;

    ArrayList<ArticleFile> mListItem;

    ArticleFile mSelectedArticleFile;

    private static final String TAG = "ChannelListViewActivity";

    private static final int ITEM_DELETE = 1;

    private static final int ITEM_DELETE_ALL = 2;

    private static final int ITEM_DOWNLOAD_ALL = 3;

    private ProgressDialog pd;

    HashMap<String, GrepArticleWebPage> grepArticleWebPageMap = new HashMap<String, GrepArticleWebPage>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        mChannleIndex = intent.getIntExtra("channelindex", 0);

        setContentView(R.layout.activity_main);
        mListView = (ListView) findViewById(R.id.home_listview);

        mListView.setOnItemClickListener(mOnItemClickListener);

        mListView.setOnLongClickListener(mOnLongClickListener);

        setTitle(Constant.channels[mChannleIndex]);
        getChannelData();

        registerForContextMenu(mListView);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {

        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("请选择操作");
        if (mChannleIndex == 4) {
            menu.add(0, ITEM_DELETE, 0, "删除");
            menu.add(0, ITEM_DELETE_ALL, 1, "删除所有");
        } else {
            menu.add(0, ITEM_DOWNLOAD_ALL, 0, "全部下载");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        String title = ((TextView) info.targetView.findViewById(R.id.title_text)).getText().toString();


        switch (item.getItemId()) {
            case ITEM_DELETE_ALL:
                Toast.makeText(this, "ITEM_DELETE_ALL", Toast.LENGTH_SHORT).show();
                mListItem.clear();
                LocalFileCache.getInstance().clear();
                break;
            case ITEM_DELETE:
                Toast.makeText(this, "ITEM_DELETE", Toast.LENGTH_SHORT).show();
                for (ArticleFile file : mListItem) {
                    if (file.title.equals(title)) {
                        mSelectedArticleFile = file;
                        mListItem.remove(file);
                        break;
                    }
                }
                if (mSelectedArticleFile != null) {
                    LocalFileCache.getInstance().deleteFile(mSelectedArticleFile);
                    LocalFileCache.getInstance().writeFile();
                    mSelectedArticleFile = null;
                }

                break;
            case ITEM_DOWNLOAD_ALL:
                Toast.makeText(this, "全部下载，请挨个点击。", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        mChannelListViewAdapter.notifyDataSetChanged();
        return super.onContextItemSelected(item);

    }



    private void getChannelData() {
        switch (mChannleIndex) {
            case 0:
                getEverydayUpdate();
                break;
            case 1:
                getSpecialUpdate();
                break;
            case 2:
                getStandardUpdate();
                break;
            case 3:
                getLearningUpdate();
                break;
            case 4:
                getLocalUpdate();
                break;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mListItem != null) {
            mChannelListViewAdapter.notifyDataSetChanged();
        }
    }

    private void getLocalUpdate() {
        HashMap<String, ArticleFile> map = LocalFileCache.getInstance().getLocalFileMap();
        if (map != null) {
            Iterator<Entry<String, ArticleFile>> iter = map.entrySet().iterator();

            mListItem = new ArrayList<ArticleFile>();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                ArticleFile localFile = (ArticleFile) entry.getValue();
                mListItem.add(localFile);
            }
            mChannelListViewAdapter = new ChannelListViewAdapter(ChannelListViewActivity.this, mListItem);
            mListView.setAdapter(mChannelListViewAdapter);

        }
    }

    private void getLearningUpdate() {

        if (mGrepWebpageLearning_update == null) {
            mGrepWebpageLearning_update = new GrepChannelFormWebpage(mHandler);
        }

        if (mGrepWebpageLearning_update.getChannelTitle() != null) {
            ChannelListViewActivity.this.setTitle(mGrepWebpageLearning_update.getChannelTitle());
        }

        mListItem = mGrepWebpageLearning_update.getListArticleFile();
        if (mListItem != null) {

            mChannelListViewAdapter = new ChannelListViewAdapter(ChannelListViewActivity.this, mListItem);
            mListView.setAdapter(mChannelListViewAdapter);

        } else {

            showProgress();
            mGrepWebpageLearning_update.getListItemFromWebPage(Constant.VOA_English_Learning);
        }
    }

    private void getSpecialUpdate() {

        if (mGrepWebpageSpecial_update == null) {
            mGrepWebpageSpecial_update = new GrepChannelFormWebpage(mHandler);
        }
        mListItem = mGrepWebpageSpecial_update.getListArticleFile();

        if (mGrepWebpageSpecial_update.getChannelTitle() != null) {
            ChannelListViewActivity.this.setTitle(mGrepWebpageSpecial_update.getChannelTitle());
        }

        if (mListItem != null) {
            mChannelListViewAdapter = new ChannelListViewAdapter(ChannelListViewActivity.this, mListItem);
            mListView.setAdapter(mChannelListViewAdapter);
        } else {
            showProgress();
            mGrepWebpageSpecial_update.getListItemFromWebPage(Constant.VOA_Special_English);
        }
    }

    private void getStandardUpdate() {

        if (mGrepWebpageStandard_update == null) {
            mGrepWebpageStandard_update = new GrepChannelFormWebpage(mHandler);
        }

        if (mGrepWebpageStandard_update.getChannelTitle() != null) {
            ChannelListViewActivity.this.setTitle(mGrepWebpageStandard_update.getChannelTitle());
        }

        mListItem = mGrepWebpageStandard_update.getListArticleFile();
        if (mListItem != null) {
            mChannelListViewAdapter = new ChannelListViewAdapter(ChannelListViewActivity.this, mListItem);
            mListView.setAdapter(mChannelListViewAdapter);

        } else {

            showProgress();
            mGrepWebpageStandard_update.getListItemFromWebPage(Constant.VOA_Standard_1);
        }
    }

    private void getEverydayUpdate() {
        WebPageLink wpl = Constant.VOA_ROOT;
        if (mGrepWebpage == null) {
            mGrepWebpage = new GrepChannelFormWebpage(mHandler);
        }

        if (mGrepWebpage.getChannelTitle() != null) {
            ChannelListViewActivity.this.setTitle(mGrepWebpage.getChannelTitle());
        }

        mListItem = mGrepWebpage.getListArticleFile();
        if (mListItem != null) {
            mChannelListViewAdapter = new ChannelListViewAdapter(ChannelListViewActivity.this, mListItem);
            mListView.setAdapter(mChannelListViewAdapter);
        } else {
            showProgress();
            mGrepWebpage.getListItemFromWebPage(wpl);
        }
    }

    private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int position, long ID) {
            Log.d(TAG, "your click position is: " + position);
            getArticlefromWeb(position);
        }
    };

    private OnLongClickListener mOnLongClickListener = new OnLongClickListener(){
        @Override
        public boolean onLongClick(View v) {
            return false;
        }};

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            super.handleMessage(msg);
            switch (msg.what) {
                case Constant.UP_DATE_DATA:
                    Log.d(TAG, "handleMessage(Message msg) UP_DATE_DATA");
                    pd.dismiss();
                    getChannelData();
                    break;
                case Constant.FAILED_UPDATE:
                    pd.dismiss();
                    Toast.makeText(ChannelListViewActivity.this, "网络状况不好，不能更新文章列表！ \r\n跳转到本地文章。", Toast.LENGTH_SHORT).show();
                    mChannleIndex = 4;
                    getChannelData();
                    break;

                case Constant.UPDATE_TEXT:

                    break;
                case Constant.DOWNLOAD_PROGRESS:
                    if (mChannelListViewAdapter != null) {
                        mChannelListViewAdapter.notifyDataSetChanged();
                    }
                    break;

                case Constant.DOWNLOAD_COMPLETED:
                    if (mChannelListViewAdapter != null) {
                        mChannelListViewAdapter.notifyDataSetChanged();
                    }
                    break;

                case Constant.DOWNLOAD_UPDATE:

                    break;
            }
        }
    };

    private void showProgress() {
        pd = ProgressDialog.show(ChannelListViewActivity.this, null, "Loading....    加载中，请稍后……");
    }

    protected void getArticlefromWeb(int position) {
        ArticleFile af = mListItem.get(position);

        if (af.localFileName != null) {
            Intent intent = new Intent(ChannelListViewActivity.this, ArticleActivity.class);
            intent.putExtra("article_key", af.key);
            startActivity(intent);

        } else {
            GrepArticleWebPage grepArticleWebPage = grepArticleWebPageMap.get(af.key);
            if (grepArticleWebPage == null) {
                grepArticleWebPage = new GrepArticleWebPage(mHandler, position, af);
                grepArticleWebPageMap.put(af.key, grepArticleWebPage);
            }
        }


    }

}
