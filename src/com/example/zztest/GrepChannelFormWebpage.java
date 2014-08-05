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

public class GrepChannelFormWebpage {

    private static final String TAG = "GrepChannelFormWebpage";

    private int mRetry = 5;

    private ArrayList<ArticleFile> mListArticleFile;

    private Handler mHandler;

    private WebPageLink mWebPageLink;

    private Document mDocument;

    private Element mRightNav;

    private Element mBody;

    private Element mChannelTitle;

    public String getChannelTitle() {
        if (mChannelTitle != null) {
            return mChannelTitle.text();
        }
        return null;
    }

    private Element mList;

    private Elements items;

    public GrepChannelFormWebpage(Handler handler) {
        mHandler = handler;
    }

    public ArrayList<ArticleFile> getListArticleFile() {
        return mListArticleFile;
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

        mChannelTitle = mRightNav.getElementById("title");

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
            mListArticleFile = new ArrayList<ArticleFile>();
        } else {
            mListArticleFile = null;
        }

        for (Element item : items) {

            Elements links = item.getElementsByTag("a");
            int count = links.size();

            ArticleFile af= new ArticleFile();

            for (int i = 0; i < count; i++) {

                Element link = links.get(i);

                String linkHref = link.attr("href");

                if (!linkHref.startsWith(Constant.VOA_ROOT.link)) {
                    linkHref = Constant.VOA_ROOT.link + linkHref;
                }

                String text = null;

                if (link.hasText()) {
                    text = link.text().trim();
                    if (text.startsWith("[") && text.endsWith("]")) {
                        Log.d(TAG, "subChannle: " + text);
                        af.subChannel = text;
                    } else {
                        if (i == count - 1) {
                            af.title = text;
                            af.urlstring = linkHref;
                            af.key = ArticleFile.getArticleFileByUrl(linkHref);
                            
                            if (ArticleIsExistInLocal(af.key)) {
                                af = getArticleFromLocal(af.key);
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
                            af.lrcUrl = linkHref;
                        }
                        if (mediaSrc.endsWith("yi.gif")) {
                            af.translationUrl = linkHref;
                            Log.d(TAG, "items: fanyi");
                        }
                    }
                }

                Log.d(TAG, "items: linkHref = " + linkHref);
            }

            mListArticleFile.add(af);
        }

        notifyUpdateData();
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
