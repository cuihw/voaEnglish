package com.example.zztest;

import java.io.IOException;
import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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
	
	HashMap<String, Object> mArticleInfo ;

	public GrepArticleWebPage(Handler handler, int index, HashMap<String, Object> articleInfo) {
		mHandler = handler;
		__index = index;
		mArticleInfo = articleInfo;
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

	public void getArticleInfo(final String url) {
		mUrl = url;
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
	}

	private void getTranslationContent(String link) {

		new Thread(new Runnable() {

			@Override
			public void run() {

				if (mUrl != null) {
					String translationPath = mUrl.substring(0,
							mUrl.lastIndexOf("/") + "/".length());
					mTranslationlink = translationPath + mTranslationlink;

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

}
