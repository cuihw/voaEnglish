package com.example.zztest;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.example.zztest.downloader.ArticleFile;
import com.example.zztest.downloader.CacheToFile;
import com.example.zztest.downloader.LocalFileCache;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class GrepArticleWebPage {

    public int __index;

    private Handler mHandler;

    private static final String TAG = "GrepArticleWebPage";

    private int mRetry = 5;

    private Document mDocument;

    private String mAtricle;

    private String mLrcUrl;

    private String mTranslation;

    private String mMp3webUrl;

    private String mUrl;

    private String mTranslationlink;

    ArticleFile mArticleInfo;

    int mProgress = 0;

    private int mRetryDownloadfile = 5;

    public GrepArticleWebPage(Handler handler, int index, ArticleFile item) {
        mHandler = handler;
        __index = index;
        mArticleInfo = item;

        mUrl = mArticleInfo.urlstring;
        if (mArticleInfo.localFileName == null) {
            getArticleInfo(mArticleInfo.urlstring);
        }
    }

    public String getAtricle() {
        return mAtricle;
    }


    public String getUrl() {
        return mUrl;
    }

    public String getLrcUrl() {
        return mLrcUrl;
    }

    public String getTranstion() {
        return mTranslation;
    }

    public String getMp3webUrl() {
        return mMp3webUrl;
    }

    private void getArticleInfo(final String url) {

        // run in background thread.
        new Thread(new Runnable() {

            @Override
            public void run() {

                mDocument = getWebpageDoc(url);

                parserDocument(mDocument);
            }

        }).start();
    }

    protected void parserDocument(Document doc) {
        if (doc == null) {
            if (mRetry > 0) {
                mRetry--;
                Log.d(TAG, "retry to get the data.");
                getArticleInfo(mUrl);
            } else {
                Message msg = mHandler.obtainMessage();
                msg.what = Constant.FAILED_UPDATE;
                mHandler.sendMessage(msg);
            }
            return;
        }
        Elements elements = doc.getElementsByTag("body");
        Element mBody = elements.first();
        Element content = mBody.getElementById("content");
        mAtricle = content.html();

        Element menubar = mBody.getElementById("menubar");
        Elements links = menubar.select("a[href]");
        for (int i = 0; i < links.size(); i++) {
            Element ele = links.get(i);

            String linkHref = ele.attr("href");
            if (ele.id().equalsIgnoreCase("mp3")) {
                mMp3webUrl = linkHref;
            } else if (ele.id().equalsIgnoreCase("lrc")) {
                mLrcUrl = Constant.VOA_ROOT.link + linkHref;
            } else if (ele.id().equalsIgnoreCase("EnPage")) {
                mTranslationlink = linkHref;
            }
        }

        if (mTranslationlink != null) {
            mRetry = 5;
            getTranslationContent(mTranslationlink);
        }

        Message msg = mHandler.obtainMessage();
        msg.what = Constant.UPDATE_TEXT;
        msg.obj = GrepArticleWebPage.this;
        mHandler.sendMessage(msg);

        if (mLrcUrl != null) {
            downloadlrcFile(mLrcUrl);
        }

        if (mMp3webUrl != null) {
            downloadAudioFile(mMp3webUrl);
        } else {
            String filename = CACHE_PATH + "/" + mArticleInfo.key + ".txt";

            CacheToFile.writeFile(filename, mAtricle.getBytes());

            mArticleInfo.localFileName = filename;
        }

        HashMap<String, ArticleFile> map = LocalFileCache.getInstance().getLocalFileMap();
        map.put(mArticleInfo.key, mArticleInfo);
        LocalFileCache.getInstance().setmLocalFileMap(map);
        LocalFileCache.getInstance().wirteFile();
    }


    private void getTranslationContent(final String link) {

        Log.d(TAG, "link = " + link);

        new Thread(new Runnable() {

            @Override
            public void run() {

                Log.d(TAG, "mTranslationlink = " + mTranslationlink);
                if (mUrl != null) {
                    String translationPath = mUrl.substring(0, mUrl.lastIndexOf("/") + "/".length());

                    mTranslationlink = translationPath + link;

                    Log.d(TAG, "mTranslationlink = " + mTranslationlink);
                    Document doc = getWebpageDoc(mTranslationlink);

                    getTranslationContent(doc);
                }
            }

        }).start();

    }

    private void getTranslationContent(Document doc) {
        if (doc == null) {
            if (mRetry > 0) {
                mRetry--;
                getTranslationContent(mTranslationlink);
            }
            return;
        }
        Element ele = doc.getElementById("content");
        mTranslation = ele.html();

        if (mTranslation != null) {

            String filename = CACHE_PATH + "/" + mArticleInfo.key + "_1.txt";
            CacheToFile.writeFile(filename, mTranslation.getBytes());
            mArticleInfo.translation = filename;
        }

        HashMap<String, ArticleFile> map = LocalFileCache.getInstance().getLocalFileMap();
        map.put(mArticleInfo.key, mArticleInfo);
        LocalFileCache.getInstance().setmLocalFileMap(map);
        LocalFileCache.getInstance().wirteFile();
    }

    private Document getWebpageDoc(String url) {
        Document doc = null;
        try {
            doc = Jsoup.connect(url).timeout(5000).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return doc;
    }


    public static String CACHE_PATH = Environment.getExternalStorageDirectory().toString() + "/51voa/cache";

    private void downloadlrcFile(String urlstring) {
        String filename = CACHE_PATH + getFileName(urlstring);
        mRetryDownloadfile = 5;
        downloadFile(urlstring, filename);

    }

    private void downloadAudioFile(String urlstring) {
        String filename = CACHE_PATH + getFileName(urlstring);
        mRetryDownloadfile = 5;
        downloadFile(urlstring, filename);

        mArticleInfo.audio = filename;
        mArticleInfo.audioUrl = urlstring;
        filename = CACHE_PATH + "/" + mArticleInfo.key + ".txt";

        CacheToFile.writeFile(filename, mAtricle.getBytes());

        mArticleInfo.localFileName = filename;
    }

    private String getFileName(String urlstring) {
        return urlstring.substring(urlstring.lastIndexOf('/'));
    }


    private static final int MAX_BUFFER_SIZE = 1024;

    private void downloadFile(String urlString, String localFilename) {

        RandomAccessFile file = null;

        InputStream stream = null;

        try {
            URL url = new URL(urlString);
            // Open connection to URL.
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Specify what portion of file to download.
            connection.setRequestProperty("Range", "bytes=" + 0 + "-");

            // Connect to server.
            connection.connect();

            // Make sure response code is in the 200 range.
            if (connection.getResponseCode() / 100 != 2) {
                error(urlString, localFilename);
                return;
            }
            // Check for valid content length.
            int size = connection.getContentLength();
            if (size < 1) {
                error(urlString, localFilename);
                return;
            }

            int downloaded = 0;

            file = new RandomAccessFile(localFilename, "rw");

            long length = file.length();

            downloaded = (int) length;

            file.seek(length);
            if (length == size + 1 || length == size) {

                notifyTheProgress(size, size, urlString);
                return;
            }

            stream = connection.getInputStream();
            byte buffer[];
            int read = -1;
            do {
                if (size - downloaded > MAX_BUFFER_SIZE) {
                    buffer = new byte[MAX_BUFFER_SIZE];
                } else {
                    buffer = new byte[size - downloaded];
                }
                read = stream.read(buffer);
                Log.d(TAG, "read is " + read + buffer.toString());

                if (read == -1 || read == 0) {
                    Log.d(TAG, "read length is -1");
                    notifyTheProgress(size, downloaded, urlString);
                    break;
                }

                file.write(buffer, 0, read);
                downloaded = downloaded + read;
                notifyTheProgress(size, downloaded, urlString);
            } while (read != -1);
        } catch (IOException e) {
            Log.e(TAG, e.toString());
            error(urlString, localFilename);
            return;
        } finally {
            // Close file.
            if (file != null) {
                try {
                    file.close();
                } catch (Exception e) {
                }
            }

            // Close connection to server.
            if (stream != null) {
                try {
                    stream.close();
                } catch (Exception e) {
                }
            }
        }
    }

    private void notifyTheProgress(int total, int downloaded, String urlString) {
        int progress = downloaded * 100 / total;

        if (downloaded == total) {

            mProgress = 100;

            Message msg = mHandler.obtainMessage();
            msg.what = Constant.DOWNLOAD_COMPLETED;
            msg.arg1 = mProgress;
            msg.obj = urlString;
            mHandler.sendMessage(msg);
            Log.d(TAG, "urlString = " + urlString + ", notifyTheProgress = " + mProgress);
        }

        if (mProgress != progress) {
            mProgress = progress;

            Message msg = mHandler.obtainMessage();
            msg.what = Constant.DOWNLOAD_PROGRESS;
            msg.arg1 = mProgress;
            msg.obj = urlString;
            mHandler.sendMessage(msg);
            Log.d(TAG, "urlString = " + urlString + ", notifyTheProgress = " + mProgress);
        }
    }

    private void error(String urlString, String localFilename) {
        if (mRetryDownloadfile != 0) {
            mRetryDownloadfile--;
            downloadFile(urlString, localFilename);
        }
    }

}
