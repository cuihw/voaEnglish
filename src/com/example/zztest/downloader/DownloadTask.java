package com.example.zztest.downloader;


import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.zztest.Constant;

// String filename = context.getFilesDir().getAbsolutePath() + "/cache/PART_" + mills + "_" ;
// String filename = Environment.getExternalStorageDirectory().getAbsolutePath() + "/cache/PART_" +
// mills + "_" ;

public class DownloadTask implements Observer {

    private static final String TAG = "DownloadTask";

    private int LIST_SIZE = 10;

    private Download mCurrentDownload;

    int state = Download.IDLE;

    private static DownloadTask instence;

    private Handler mHandler;

    private HashMap<String, Object> downloadUrlMap = new HashMap<String, Object>();

    ArrayList<HashMap<String, ArticleFile>> localFileList;

    BlockingQueue<Download> mDownloadQueue = new LinkedBlockingQueue<Download>(LIST_SIZE);

    public static synchronized DownloadTask getInstence() {
        if (instence == null) {
            instence = new DownloadTask();
        }
        return instence;
    }

    public Download getCurrentDownload() {
        return mCurrentDownload;
    }

    public void setHandler(Handler handler) {
        mHandler = handler;
    }

    @Override
    public void update(Observable arg0, Object arg1) {
        state = mCurrentDownload.getStatus();


        if (state == Download.COMPLETE) {
            if (mHandler != null) {
                Message msg = mHandler.obtainMessage();
                msg.what = Constant.DOWNLOAD_UPDATE;
                Download dl = mCurrentDownload;

                msg.obj = dl;
                mHandler.sendMessage(msg);
            }

            Log.d(TAG, "beginTask() mDownloadQueue.size() = " + mDownloadQueue.size());
            Log.d(TAG, "download completed!!!");
            synchronized (mCurrentDownload) {
                mCurrentDownload = null;
            }
            beginTask();
        } else {
            if (mCurrentDownload != null) {
                mCurrentDownload.download();
            }
        }

    }


    private URL URL(String url) {
        // TODO Auto-generated method stub
        return null;
    }

    public synchronized void addTask(String urlstring, String key) {

        URL url = checkAndGetUrl(urlstring);
        if (url != null) {
            Download dl = new Download(url, key);
            Log.d(TAG, "addTask() mDownloadQueue.size() = " + mDownloadQueue.size());
            if (dl != null) {
                if (!mDownloadQueue.contains(dl)) {
                    try {
                        mDownloadQueue.put(dl);
                        startDownload();
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private URL checkAndGetUrl(String urlstring) {

        String key = urlstring.substring(urlstring.lastIndexOf('/') + 1);
        URL url = null;
        if (!downloadUrlMap.containsKey(key)) {
            try {
                url = new URL(urlstring);
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                url = null;
            }

            if (url != null) {
                downloadUrlMap.put(key, url);
            }
        }
        return url;
    }

    private void beginTask() {

        if (mCurrentDownload == null || mCurrentDownload.getStatus() == Download.IDLE) {

            Log.d(TAG, "beginTask() mDownloadQueue.size() = " + mDownloadQueue.size());

            if (!mDownloadQueue.isEmpty()) {
                try {
                    mCurrentDownload = mDownloadQueue.take();
                    synchronized (mCurrentDownload) {
                        mCurrentDownload.addObserver(this);
                        mCurrentDownload.download();
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void startDownload() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                beginTask();
            }
        }).start();
    }


}
