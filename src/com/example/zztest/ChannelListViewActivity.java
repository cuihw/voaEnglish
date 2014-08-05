package com.example.zztest;


import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.example.zztest.downloader.ArticleFile;
import com.example.zztest.downloader.CacheToFile;
import com.example.zztest.downloader.Download;
import com.example.zztest.downloader.DownloadTask;
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

    private static final String TAG = "ChannelListViewActivity";

    private ProgressDialog pd;

    HashMap<String, GrepArticleWebPage> grepArticleWebPageMap = new HashMap<String, GrepArticleWebPage>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        mListView = (ListView) findViewById(R.id.home_listview);

        mListView.setOnItemClickListener(mOnItemClickListener);

        Intent intent = getIntent();
        mChannleIndex = intent.getIntExtra("channelindex", 0);
        setTitle(Constant.channels[mChannleIndex]);

        getChannelData();
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
                break;
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
                    break;

                case Constant.UPDATE_TEXT:
//                    Object obj = msg.obj;
//                    if (obj instanceof GrepArticleWebPage) {
//                        GrepArticleWebPage gawp = (GrepArticleWebPage)obj;
//                        downloadAudioAndExtra(gawp);
//                    }

                    break;
                case Constant.DOWNLOAD_PROGRESS:

                    break;
                case Constant.DOWNLOAD_COMPLETED:

                    break;
                case Constant.DOWNLOAD_UPDATE:

                    break;
            }
        }
    };

    private void showProgress() {
        pd = ProgressDialog.show(ChannelListViewActivity.this, null, "Loading....    加载中，请稍后……");
    }

//    protected void saveLocalFileInfo(final Download dl) {
//
//        Log.d(TAG, "saveLocalFileInfo " + dl.getLocalFilename());
//
//        String url = dl.getUrl();
//
//        GrepArticleWebPage grepArticleWebPage = grepArticleWebPageMap.get(url);
//
//        if (grepArticleWebPage != null) {
//
//            String urlstring = grepArticleWebPage.getUrl();
//            
//            String articleKey = ArticleFile.getArticleFileByUrl(urlstring);
//            
//            ArticleFile af = LocalFileCache.getInstance().getLocalFileMap().get(articleKey);
//            if (af == null) {
//                af = new ArticleFile();
//                af.key = articleKey;
//            }
//
//            if (url.endsWith("lrc")) {
//                af.lrc = dl.getLocalFilename();
//                af.lrcUrl = grepArticleWebPage.getLrcUrl();
//            } else if (url.endsWith("mp3")) {
//                af.audio = dl.getLocalFilename();
//                af.audioUrl = grepArticleWebPage.getMp3webUrl();
//            }
//
//            if (af.localFileName == null) {
//                String article = grepArticleWebPage.getAtricle();
//                if (article != null) {
//                    String filename = Download.SDPATH + "/" + articleKey + ".txt";
//                    CacheToFile.writeFile(filename, article.getBytes());
//                    af.localFileName = filename;
//                }
//
//                String articleTrans = grepArticleWebPage.getTranstion();
//                if (articleTrans != null) {
//                    String filename = Download.SDPATH + "/" + articleKey + "_1.txt";
//                    CacheToFile.writeFile(filename, articleTrans.getBytes());
//                    af.translation = filename;
//                }
//            }
//
//            Log.d(TAG, "LocalFileCache.getInstance().wirteFile() " + af.key);
//            HashMap<String, ArticleFile>  map = LocalFileCache.getInstance().getLocalFileMap();
//            map.put(af.key, af);
//            LocalFileCache.getInstance().wirteFile();
//        }
//    }

    protected void getArticlefromWeb(int position) {
        ArticleFile af = mListItem.get(position);

        if (af.localFileName != null) {
            Intent intent = new Intent(ChannelListViewActivity.this, ArticleActivity.class);
            intent.putExtra("article_key", af.key);
            startActivity(intent);

        } else {
            GrepArticleWebPage grepArticleWebPage = new GrepArticleWebPage(mHandler, position, af);
            grepArticleWebPageMap.put(af.key, grepArticleWebPage);
        }
        

    }

//    protected void downloadAudioAndExtra(GrepArticleWebPage gawp) {
//        // TODO Auto-generated method stub
//
//        Log.d(TAG, "downloadAudioAndExtra ");
//
//        DownloadTask downloadTask = DownloadTask.getInstence();
//        downloadTask.setHandler(mHandler);
//        String urlstring = gawp.getLrcUrl();
//
//        if (urlstring != null) {
//            grepArticleWebPageMap.put(urlstring, gawp);
//            downloadTask.addTask(urlstring, gawp.mArticleInfo.key);
//        }
//        
//        String mp3string = gawp.getMp3webUrl();
//
//        if (mp3string!= null) {
//            grepArticleWebPageMap.put(mp3string, gawp);
//            downloadTask.addTask(mp3string, gawp.mArticleInfo.key);
//        }
//    }

}
