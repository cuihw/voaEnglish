package example.com.zztest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.classic.adapter.BaseAdapterHelper;
import com.classic.adapter.CommonAdapter;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import example.com.zztest.Bean.ArticleBean;
import example.com.zztest.Bean.ArticleChannel;
import example.com.zztest.data.CacheData;
import example.com.zztest.data.Constants;
import example.com.zztest.download.IOnDownload;
import example.com.zztest.download.Task;

public class ArticleListActivity extends BaseActivity {

    private static final String TAG = "ArticleListActivity";
    @BindView(R.id.listview)
    ListView listview;

    List<ArticleBean> list = new ArrayList<>();
    CommonAdapter<ArticleBean> adapter;

    public static void startActivity(Context context, Bundle bundle) {
        Intent intent = new Intent(context, ArticleListActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_list);
        initView();
        getChannel();
    }

    private void getChannel() {
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        String stringChannel = extras.getString(Constants.CHANNEL);
        Log.i(TAG, "stringChannel = " + stringChannel);
        if (!TextUtils.isEmpty(stringChannel)) {
            articleChannel = ArticleChannel.fromJson(stringChannel);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getArticles();
    }

    ArticleChannel articleChannel;
    Document document;

    private void getArticles() {
        if (articleChannel == null) {
            Log.i(TAG, "getArticles channel is null");
            finish();
        }
        final String url = articleChannel.getUrl();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "getArticles url = " + url);
                document = getDocument(url);
                parseDocument(document);
            }
        }).start();

    }

    private void parseDocument(Document doc) {
        if (doc != null) {
            Element elementList = document.getElementById("list");
            Log.i(TAG, "list = " + elementList.html());

            Elements li = elementList.getElementsByTag("li");


            for (int i = 0; i< li.size() ; i++) {
                Element element = li.get(i);
                Log.i(TAG, "element = " + element.html());
                ArticleBean articleBean = dealArticle(element);
                if (articleBean != null) {
                    list.add(articleBean);
                }
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.replaceAll(list);
                    adapter.notifyDataSetChanged();
                }
            });
        }
    }

    private ArticleBean dealArticle(Element element) {
        //<a href="/Technology_Report_1.html" target="_blank">[ Technology Report ] </a>
        // <a href="/VOA_Special_English/us-study-work-in-space-does-not-seem-to-shorten-astronauts-lives--81141.html"
        // target="_blank">US Study: Work in Space Does Not Seem to Shorten Astronauts’ Lives (2019-1-1)</a>

        // 1
       //  <a href="/as_it_is_1.html" target="_blank">[ AS IT IS ] </a>

        // <a href="/lrc/201812/the-world-says-hello-2019-goodbye-to-an-unsettling-year.lrc" target="_blank">
        // <img src="/images/lrc.gif" width="27" height="15" border="0"></a>

        // <a href="/VOA_Special_English/the-world-says-hello--goodbye-to-an-unsettling-year-81142_1.html" target="_blank">
        // <img src="/images/yi.gif" width="27" height="15" border="0"></a>
        //
        // <a href="/VOA_Special_English/the-world-says-hello--goodbye-to-an-unsettling-year-81142.html" target="_blank">
        // The World Says ‘Hello’ to 2019, ‘Goodbye’ to an Unsettling Year (2019-1-1)</a>

        try{
            ArticleBean articleBean = new ArticleBean();
            Log.i(TAG, "list element = " + element.toString());
            Elements links = element.getElementsByTag("a");

            for (Element ele: links) {
                String linkHref = ele.attr("href");
                if (!linkHref.startsWith(Constants.BASE_URL)) {
                    linkHref = Constants.BASE_URL + linkHref;
                }

                Log.i(TAG, "linkHref = " + linkHref);

                if (linkHref.endsWith(".lrc")) {
                    Log.d(TAG, "lrc = " + linkHref);
                    articleBean.setLrcUrl(linkHref);
                }
                String text = null;
                if (ele.hasText()) {
                    text = ele.text().trim();
                    if (text.startsWith("[") && text.endsWith("]")) {
                        Log.d(TAG, "subChannle = " + text);
                        articleBean.setChannel(text);
                    } else {
                        Log.d(TAG, "title = " + text);
                        articleBean.setTitle(text);
                        articleBean.setUrl(linkHref);
                    }
                } else {
                    Elements medias = ele.getElementsByTag("img");
                    if (medias.size() > 0) {
                        Element media = medias.first();
                        String mediaSrc = media.attr("src");

                        if (mediaSrc.endsWith("yi.gif")) {
                            articleBean.setTranslationUrl(linkHref);
                            Log.d(TAG, "translation : " + linkHref);
                        }
                    }
                }
            }
            List<ArticleBean> localArticleBeanList = CacheData.getLocalArticleBeanList();
            if (localArticleBeanList.contains(articleBean)){
                articleBean = localArticleBeanList.get(localArticleBeanList.indexOf(articleBean));
            }
            Log.i(TAG, "articleBean : " + articleBean.toJson());
            return articleBean;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private  Document getDocument(String url) {
        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return doc;
    }

    KProgressHUD hud;
    private void initView() {
        adapter = new CommonAdapter<ArticleBean>(this, R.layout.item_article, list) {
            @Override
            public void onUpdate(BaseAdapterHelper helper, ArticleBean item, int position) {
                helper.setText(R.id.title, item.getTitle());
                helper.setText(R.id.channel, item.getChannel());

                if (TextUtils.isEmpty(item.getTranslationUrl())) {
                    helper.getView(R.id.translation).setVisibility(View.INVISIBLE);
                } else {
                    helper.getView(R.id.translation).setVisibility(View.VISIBLE);
                }
                if (TextUtils.isEmpty(item.getLrcUrl())) {
                    helper.getView(R.id.lrc).setVisibility(View.INVISIBLE);
                } else {
                    helper.getView(R.id.lrc).setVisibility(View.VISIBLE);
                }

                if (item.isDownLoaded()) {
                    helper.getView(R.id.download).setVisibility(View.VISIBLE);
                } else {
                    helper.getView(R.id.download).setVisibility(View.INVISIBLE);
                }

                ImageView favorite =  helper.getView(R.id.favorite);
                if (item.isMyFavorite()) {
                    favorite.setImageResource(R.mipmap.icon_favorite_small_selected);
                } else {
                    favorite.setImageResource(R.mipmap.icon_favorite_small);
                }
            }
        };
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position < list.size()) {
                    ArticleBean articleBean = list.get(position);
                    if (articleBean.isDownLoaded()) {
                        showArticle(articleBean);
                    } else {
                        downloadArticle(articleBean);
                    }
                }
            }
        });
        hud = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(false);

        hud.setLabel("opening");
        hud.setGraceTime(100);

    }

    private void downloadArticle(final ArticleBean articleBean) {
        Task task = Task.genericTask(articleBean);
//        DownloadManager instances = DownloadManager.getInstances();
//        DownloadManager.getInstances().addTask(task);

        if (!hud.isShowing()) {
            hud.show();
        }
        task.setOnDownload(new IOnDownload() {
            @Override
            public void onFinished(String status) {
                Log.i(TAG, "status = " + status);
                if ("ok--1".equals(status)) {
                    hud.dismiss();
                    CacheData.getInstence().save(articleBean);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showArticle(articleBean);
                        }
                    });

                }
            }
        });
        task.startDownload();
    }

    private void showArticle(ArticleBean articleBean) {
        Log.i(TAG, "showArticle");
        Bundle bundle = new Bundle();
        bundle.putString("articleBean", articleBean.toJson());
        GeneralActivity.startActivity(this, bundle);
    }
}
