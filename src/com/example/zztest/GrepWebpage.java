package com.example.zztest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.example.zztest.downloader.ArticleFile;
import com.example.zztest.downloader.LocalFileCache;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class GrepWebpage {

    private static final String TAG = "GrepWebpage";

    private int mRetry = 5;

    private ArrayList<HashMap<String, Object>> mListItem;

    private Handler mHandler;

    private WebPageLink mWebPageLink;

    private Document mDocument;

    private Element mRightNav;

    private Element mBody;

    private Element mTitle;
    
    private String mArticleKey;

    public String getTitle() {
        if (mTitle != null) {
            return mTitle.text();
        }
        return null;
    }

    private Element mList;

    private Elements items;

    public GrepWebpage(Handler handler) {
        mHandler = handler;
    }

    public ArrayList<HashMap<String, Object>> getListItem() {
        return mListItem;
    }

    public void getListItemFromWebPage(WebPageLink wpl) {

        if (!wpl.equals(mWebPageLink)) {
            mWebPageLink = wpl;
        }

        // run in background thread.
        new Thread(new Runnable() {

            @Override
            public void run() {

                mDocument = getWebpageDoc(mWebPageLink.link);

                parserDocument(mDocument);
            }
        }).start();

    }

    private void parserDocument(Document doc) {
        if (doc == null) {
            if (mRetry > 0) {
                mRetry--;
                Log.d(TAG, "retry to get the data.");
                getListItemFromWebPage(mWebPageLink);
            } else {
                Message msg = mHandler.obtainMessage();
                msg.what = Constant.FAILED_UPDATE;
                mHandler.sendMessage(msg);
            }
            return;
        }
        Elements elements = doc.getElementsByTag("body");
        mBody = elements.first();
        mRightNav = mBody.getElementById("right_box");

        mTitle = mRightNav.getElementById("title");

        mList = mRightNav.getElementById("list");
        Elements uls = mList.getElementsByTag("ul");
        Element ul = uls.first();
        items = ul.getElementsByTag("li");

        parserItems(items);
    }

    /*
     * <li><a href="/Agriculture_Report_1.html" target="_blank"><font color=#E43026>[ Agriculture Report ]</font></a> 
     * <a href="/lrc/201407/se-ag-thailand-21jul14.lrc" target=_blank>
     * <img src="/images/lrc.gif" width="27" height="15" border="0"></a>
     * <a href="/VOA_Special_English/thailand-ends-controversial-rice-support-program-57742_1.html" target="_blank">
     * <img src="/images/yi.gif" width="27" height="15" border="0"></a>
     * <a href="/VOA_Special_English/thailand-ends-controversial-rice-support-program-57742.html" target="_blank">
     * Thailand Ends Controversial Rice Support Program (2014-7-22)</a> </li>
     */

    /*
    <li>  
    <a href="/VOA_Standard_English/airbus-adds-metal-three-d-printed-parts-to-new-jets-57875.html" target="_blank">
    Airbus Adds Metal 3-D-Printed Parts to New Jets  (2014-7-25)</a></li>*/
    
    private void parserItems(Elements items) {

        if (items.size() > 0) {
            mListItem = new ArrayList<HashMap<String, Object>>();
        } else {
            mListItem = null;
        }

        for (Element item : items) {

            Elements links = item.getElementsByTag("a");
            int count = links.size();

            HashMap<String, Object> map;
            map = new HashMap<String, Object>();

            for (int i = 0; i < count; i++) {
                Element link = links.get(i);

                String linkHref = link.attr("href");

                String text = null;
                ArticleFile af = new ArticleFile();
                if (link.hasText()) {
                    text = link.text().trim();
                    if (text.startsWith("[") && text.endsWith("]")) {
                        Log.d(TAG, "subChannle: " + text);
                        map.put(Constant.subChannle, text);
                        af.subChannel = text;
                    } else {
                        if (i == count - 1) {
                            map.put(Constant.title, text);
                            map.put(Constant.titlelink, linkHref);
                            af.title = text;
                            af.urlstring = linkHref;
                            af.key = ArticleFile.getArticleFileByUrl(linkHref);
                            
                            if (ArticleIsExistInLocal(af.key)) {
                                af = getArticleFromLocal(af.key);
                            } else {
                                putArticleToLocal(af);
                            }

                            if (af.localFileName != null) {
                                map.put(Constant.already_downloaded, "[已下载]");
                            }

                            Log.d(TAG, "title: " + text + ", linkhref = " + linkHref);
                        }
                    }
                } else {

                    Elements medias = link.getElementsByTag("img");

                    if (medias.size() > 0) {
                        Element media = medias.first();
                        String mediaSrc = media.attr("src");

                        if (mediaSrc.endsWith("lrc.gif")) {
                            map.put(Constant.zimu, "[字幕]");
                            map.put(Constant.zimulink, linkHref);
                            Log.d(TAG, "items: zimu");
                        }
                        if (mediaSrc.endsWith("yi.gif")) {
                            map.put(Constant.fanyi, "[翻译]");
                            map.put(Constant.fanyilink, linkHref);
                            Log.d(TAG, "items: fanyi");
                        }
                    }

                }

                Log.d(TAG, "items: linkHref = " + linkHref);
            }

            LocalFileCache lfc = LocalFileCache.getInstance();
            lfc.wirteFile();
            mListItem.add(map);
        }

        notifyUpdateData();
    }

    private void putArticleToLocal(ArticleFile af) {

        LocalFileCache lfc = LocalFileCache.getInstance();
        HashMap<String, ArticleFile> mLocalFileMap  = lfc.getLocalFileMap();
        if (mLocalFileMap == null) {
            mLocalFileMap = new HashMap<String, ArticleFile> ();
        }
        mLocalFileMap.put(af.key, af);
        lfc.setmLocalFileMap(mLocalFileMap);
    }

    private ArticleFile getArticleFromLocal(String key) {
        LocalFileCache lfc = LocalFileCache.getInstance();
        HashMap<String, ArticleFile> mLocalFileMap  = lfc.getLocalFileMap();
        ArticleFile af =  mLocalFileMap.get(key);
        
        return af;
    }

    private boolean ArticleIsExistInLocal(String key) {
        LocalFileCache lfc = LocalFileCache.getInstance();
        HashMap<String, ArticleFile> mLocalFileMap  = lfc.getLocalFileMap();
        if (mLocalFileMap != null) {
            return mLocalFileMap.containsKey(key);            
        }
        return false;
    }

    private void notifyUpdateData() {
        Message msg = mHandler.obtainMessage();
        msg.what = Constant.UP_DATE_DATA;
        mHandler.sendMessage(msg);
    }

    public Document getWebpageDoc(String url) {

        Document doc = null;
        try {
            doc = Jsoup.connect(url).timeout(5000).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return doc;
    }

}
