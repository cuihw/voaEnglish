package com.example.zztest.downloader;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import android.util.Log;
import android.util.Xml;

public class LocalFileCache {
    private static final String TAG = "LocalFileCache";

    private final String LocalFileCacheFile = "/data/data/com.example.zztest/LocalFileList.xml";

    private HashMap<String, ArticleFile> mLocalFileMap = new HashMap<String, ArticleFile>();
    
    private static LocalFileCache intences = null;

    public static synchronized LocalFileCache getInstance() {
        if (intences == null) {
            intences = new LocalFileCache();
        }
        return intences;
    }

    private LocalFileCache() {

        Log.d(TAG, "LocalFileCacheFile: " + LocalFileCacheFile);

        Thread readThread = new Thread(new Runnable() {
            @Override
            public void run() {
                mLocalFileMap = readLocalFileList();
            }
        });
        readThread.start();
    }

    public HashMap<String, ArticleFile> wirteFileCacheList(final HashMap<String, ArticleFile> mLocalFile) {

        mLocalFileMap = mLocalFile;
        // new a thread to write the file.
        Thread writeThread = new Thread(new Runnable() {
            @Override
            public void run() {
                wirteFile(mLocalFileMap);
            }
        });
        writeThread.start();
        return mLocalFile;
    }
    
    public void wirteFile() {
        wirteFileCacheList(mLocalFileMap);
    }

    public HashMap<String, ArticleFile> getLocalFileMap() {
        return mLocalFileMap;
    }

    public void setmLocalFileMap(HashMap<String, ArticleFile> localFileMap) {
        this.mLocalFileMap = localFileMap;
    }

    private synchronized HashMap<String, ArticleFile> readLocalFileList() {

        File file = new File(LocalFileCacheFile);
        HashMap<String, ArticleFile> map = null;
        if (file.exists()) {
            file.setReadable(true, false);
            file.setWritable(true, false);
            InputStream in = null;
            try {
                in = new BufferedInputStream(new FileInputStream(file));
                map = getArticleFiles(in);
            } catch (FileNotFoundException e) {
                Log.e(TAG, "new BufferedInputStream failed ...");
                e.printStackTrace();
            } catch (Exception e) {
                Log.e(TAG, "getRadioStations failed ...");
                e.printStackTrace();
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        Log.e(TAG, " in.close() failed ...");
                        e.printStackTrace();
                    }
                }
            }
        }
        return map;
    }

    private HashMap<String, ArticleFile> getArticleFiles(InputStream xml) throws Exception {

        HashMap<String, ArticleFile> localFileMap = new HashMap<String, ArticleFile>();
        ArticleFile localFile = null;

        XmlPullParser pullParser = Xml.newPullParser();

        // set the pull parser XML file date.
        pullParser.setInput(xml, "UTF-8");

        int event = pullParser.getEventType();

        while (event != XmlPullParser.END_DOCUMENT) {
            switch (event) {
                case XmlPullParser.START_DOCUMENT:
                    Log.d(TAG, "find start document.");
                    break;
                case XmlPullParser.START_TAG:
                    if ("LocalFile".equals(pullParser.getName())) {
                        Log.d(TAG, "find RadioStation tag .");
                        localFile = new ArticleFile();
                        localFile.key = pullParser.getAttributeValue(null, "key");
                        localFile.localFileName = pullParser.getAttributeValue(null, "localFileName");
                        localFile.title = pullParser.getAttributeValue(null, "title");
                        localFile.translation = pullParser.getAttributeValue(null, "translation");
                        localFile.audio = pullParser.getAttributeValue(null, "audio");
                        localFile.audioUrl = pullParser.getAttributeValue(null, "audioUrl");
                        localFile.lrc = pullParser.getAttributeValue(null, "lrc");
                        localFile.lrcUrl = pullParser.getAttributeValue(null, "lrcUrl");
                        localFile.urlstring = pullParser.getAttributeValue(null, "urlstring");
                        localFile.subChannel = pullParser.getAttributeValue(null, "subChannel");

                        Log.d(TAG, "station from cache is: " + localFile.toString());
                    }
                    break;

                case XmlPullParser.END_TAG:
                    if ("RadioStation".equals(pullParser.getName())) {
                        Log.d(TAG, "end RadioStation tag.");
                        localFileMap.put(localFile.key, localFile);
                        localFile = null;
                    }
                    break;

            }

            event = pullParser.next();
        }

        return localFileMap;
    }

    /**
     * save data to xml file.
     * 
     * @param radioStationList
     * @param out
     * @throws Exception
     * 
     * 
     *         public String key; public String fileName; public String title; public String text;
     *         public String translation; public String audio; public String audioUrl; public String
     *         lrc; public String lrcUrl; public String urlstring;
     */
    private void save(HashMap<String, ArticleFile> localFileMap, OutputStream out) throws Exception {
        XmlSerializer serializer = Xml.newSerializer();
        serializer.setOutput(out, "UTF-8");
        serializer.startDocument("UTF-8", true);
        serializer.startTag(null, "LocalFiles");

        Iterator<Entry<String, ArticleFile>> iter = localFileMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            ArticleFile localFile = (ArticleFile) entry.getValue();
            serializer.startTag(null, "LocalFile");
            serializer.attribute(null, "key", localFile.key);
            if (localFile.localFileName != null)
                serializer.attribute(null, "localFileName", localFile.localFileName);

            if (localFile.title != null)
                serializer.attribute(null, "title", localFile.title);

            if (localFile.translation != null)
                serializer.attribute(null, "translation", localFile.translation);

            if (localFile.audio != null)
                serializer.attribute(null, "audio", localFile.audio);

            if (localFile.audioUrl != null)
                serializer.attribute(null, "audioUrl", localFile.audioUrl);

            if (localFile.lrc != null)
                serializer.attribute(null, "lrc", localFile.lrc);

            if (localFile.lrcUrl != null)
                serializer.attribute(null, "lrcUrl", localFile.lrcUrl);

            if (localFile.urlstring != null)
                serializer.attribute(null, "urlstring", localFile.urlstring);

            if (localFile.subChannel != null)
                serializer.attribute(null, "subChannel", localFile.subChannel);

            serializer.endTag(null, "LocalFile");

        };

        serializer.endTag(null, "LocalFiles");
        serializer.endDocument();
        out.flush();
    }

    private synchronized void wirteFile(HashMap<String, ArticleFile> localFile) {
        File file = new File(LocalFileCacheFile);
        if (!file.exists()) {
            try {
                file.createNewFile();
                file.setReadable(true, false);
                file.setWritable(true, false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        OutputStream out = null;
        try {
            out = new BufferedOutputStream(new FileOutputStream(file));
            save(localFile, out);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "BufferedOutputStream save the radio station failed.");
            e.printStackTrace();
        } catch (Exception e) {
            Log.e(TAG, "save the radio station failed.");
            e.printStackTrace();
        } finally {
            try {
                if (out != null)
                    out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
