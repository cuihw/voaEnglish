package example.com.zztest.download;

import android.text.TextUtils;
import android.util.Log;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import example.com.zztest.Bean.ArticleBean;
import example.com.zztest.data.Constants;
import example.com.zztest.utils.FileUtils;
import example.com.zztest.utils.Utils;

public class Task {
    private static final String TAG = "TASK";

    ArticleBean articleBean;

    IOnDownload onDownload;

    public ArticleBean getArticleBean() {
        return articleBean;
    }

    public void setArticleBean(ArticleBean articleBean) {
        this.articleBean = articleBean;
    }

    public Task(ArticleBean articleBean) {
        this.articleBean = articleBean;
    }

    public static Task genericTask(ArticleBean articleBean) {
        return new Task(articleBean);
    }

    public IOnDownload getOnDownload() {
        return onDownload;
    }

    public void setOnDownload(IOnDownload onDownload) {
        this.onDownload = onDownload;
    }

    public void startDownload() {
        String url = articleBean.getUrl();
        Log.i(TAG, "url = " + url);

        HttpRequest.getDocument(url, new HttpRequest.ReqListener<Document>() {
            @Override
            public void onFinished(Document data) {
                parseData(data);
            }
        });

        if (!TextUtils.isEmpty(articleBean.getTranslationUrl())) {
            HttpRequest.getDocument(articleBean.getTranslationUrl(), new HttpRequest.ReqListener<Document>() {
                @Override
                public void onFinished(Document data) {
                    parseTranslationData(data);
                }
            });
        }
    }

    private void parseTranslationData(Document data) {
        Element content = data.getElementById("content");
        if (!TextUtils.isEmpty(articleBean.getTranslationUrl())) {
            String filename = Constants.PATH_DOWNLOAD + "/" + articleBean.getTitle() + "_yi.html";
            FileUtils.TextToFile(filename, content.toString());
            articleBean.translationPath = filename;
        }
        if (onDownload != null) {
            onDownload.onFinished("ok--2");
        }
    }

    private void parseData(Document data) {
        if (data == null) {
            Log.i(TAG, "data = null");
            return;
        }
        Element content = data.getElementById("content");

        Element menubar = data.getElementById("menubar");
        Element mp3 = menubar.getElementById("mp3");
        String elementLink = Utils.getElementLink(mp3);
        Log.i(TAG, "mp3 = " + elementLink);
        articleBean.setAudioUrl(elementLink);
        Elements contentImages = content.getElementsByClass("contentImage");

        getImages(contentImages);

        saveAllContent(content.toString());
    }

    //     <div class="contentImage">
//  <img alt="Edda Mueller, chairwoman of Transparency International Germany, stands for the media with the Corruption Perceptions Index 2018, before the presentation of the yearly report at a news conference in Berlin, Germany, Jan. 29, 2019."
//   src="http://static.51voa.com/1/201901/F6F8DE7A-2B14-4BEE-AD31-69699EE1CD5F_cx0_cy1_cw0_w268_r1_s.jpg">
//  <br>
//  <span class="imagecaption">Edda Mueller, chairwoman of Transparency International Germany, stands for the media with the Corruption Perceptions Index 2018, before the presentation of the yearly report at a news conference in Berlin, Germany, Jan. 29, 2019.</span>
// </div>
    private void getImages(Elements contentImages) {
        Elements contentImages1 = contentImages;

        for (Element element : contentImages) {
            Elements imgs = element.getElementsByTag("img");
            for (final Element img : imgs) {
                img.getElementsByAttribute("src");
                String src = img.attr("abs:src");
                Log.i(TAG, "image src = " + src);
                String fileName = FileUtils.getFileName(src);
                Log.i(TAG, "downloadfile： path = " + Constants.PATH_DOWNLOAD + ", fileName = " + fileName);
                fileName = Constants.PATH_DOWNLOAD + "/" + fileName;
                img.attr("src", fileName);
                HttpRequest.download(src, new HttpRequest.ReqListener<String>() {
                    @Override
                    public void onFinished(String data) {
                        if (data.contains("download")) {
                            String ssss = data.substring(data.indexOf(":") + 1);
                            Log.i(TAG, "image fileName = " + ssss);
                        }
                    }
                });
            }
        }
    }

    private void saveAllContent(String content) {

        // download audio
        HttpRequest.download(articleBean.getAudioUrl(), new HttpRequest.ReqListener<String>() {
            @Override
            public void onFinished(String data) {
                Log.i(TAG, "onFinished = " + data);
                if (data.contains("download")) {
                    articleBean.audioPath = data.substring(data.indexOf(":") + 1);

                    Log.i(TAG, "audioPath = " + articleBean.audioPath);
                    if (onDownload != null) {
                        onDownload.onFinished("ok--1");
                        articleBean.setDownLoaded(true);
                    }
                }
            }
        });

        // download lrc
        if (!TextUtils.isEmpty(articleBean.getLrcUrl())) {
            HttpRequest.download(articleBean.getLrcUrl(), new HttpRequest.ReqListener<String>() {
                @Override
                public void onFinished(String data) {
                    Log.i(TAG, "onFinished = " + data);
                    if (data.contains("download")) {
                        articleBean.lrcPath = data.substring(data.indexOf(":") + 1);
                        Log.i(TAG, "lrcPath = " + articleBean.lrcPath);
                    }
                }
            });
        }

        String filename = Constants.PATH_DOWNLOAD + "/" + articleBean.getTitle() + ".html";
        FileUtils.TextToFile(filename, content);
        articleBean.textPath = filename;
    }
}
