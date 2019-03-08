package example.com.zztest;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.Formatter;
import java.util.Locale;

import butterknife.BindView;
import example.com.zztest.Bean.ArticleBean;
import example.com.zztest.utils.FileUtils;
import example.com.zztest.utils.ToastUtil;
import example.com.zztest.view.WordView;

public class GeneralActivity extends BaseActivity {

    private static final String TAG = "GeneralActivity";
    private static final int ARTICLE_FILE = 0;
    private static final int ARTICLE_TARNSLATION = 1;
    private static final int ARTICLE_WORD = 2;

    @BindView(R.id.tablayout)
    TabLayout tablayout;

    ArticleBean articleBean = null;

    @BindView(R.id.webview)
    WebView mWebView;

    @BindView(R.id.textview)
    TextView textview;

    @BindView(R.id.wordview)
    WordView wordview;

    @BindView(R.id.timetotal)
    TextView TVTotalTime;

    @BindView(R.id.seekbar)
    SeekBar mSeekBar;

    int totaltime;

    public static MediaPlayer mp;

    String currentAudioPath;

    public static void startActivity(Context context, Bundle bundle) {
        Intent intent = new Intent(context, GeneralActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        getArticle();
        initView();
        initWebview();
        loadAudio();
        initListener();
    }

    private void initListener() {
        tablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                showView(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void showView(int position) {
        if (position == ARTICLE_FILE){
            mWebView.setVisibility(View.VISIBLE);
            textview.setVisibility(View.GONE);
            wordview.setVisibility(View.GONE);
        }
        if (position == ARTICLE_TARNSLATION) {
            mWebView.setVisibility(View.GONE);
            textview.setVisibility(View.VISIBLE);
            wordview.setVisibility(View.GONE);
        }
        if (position == ARTICLE_WORD) {
            wordview.setVisibility(View.VISIBLE);
            mWebView.setVisibility(View.GONE);
            textview.setVisibility(View.GONE);
        }

    }

    private void loadAudio() {
        if (mp == null) {
            mp = new MediaPlayer();

            mp.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {

                    Log.i(TAG, "what = " + what + ", extra = " + extra);
                    return false;
                }
            });
        }

        if (TextUtils.isEmpty(articleBean.audioPath)) {
            return;
        }

        try {

            if (articleBean.audioPath.equals(currentAudioPath)) {
                // do not stop. continue play.

            } else {
                // stop and new a media play.
                currentAudioPath = articleBean.audioPath;
                if (mp.isPlaying()) {
                    mp.stop();
                }
                Log.i(TAG, "currentAudioPath = " + currentAudioPath);
                mp.reset();
                mp.setDataSource("file://" + currentAudioPath);
                mp.prepareAsync();
                mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.start();
                    }
                });
            }

            totaltime = mp.getDuration();
            TVTotalTime.setText(getTime(totaltime));
            mSeekBar.setMax(totaltime);
            mSeekBar.setProgress(mp.getCurrentPosition());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String getTime(int timeMs) {
        int totalSeconds = timeMs / 1000;
        StringBuilder mFormatBuilder = new StringBuilder();
        Formatter mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        mFormatBuilder.setLength(0);

        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    private void getArticle() {
        Bundle extras = getIntent().getExtras();
        String articleJson = extras.getString("articleBean");
        Log.i(TAG, "articleJson = " + articleJson);
        try {
            articleBean = ArticleBean.fromJson(articleJson);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (articleBean == null) {
            ToastUtil.showTextToast(this, "article is null, return to article list.");
            finish();
        }
    }

    private void initView() {

        Log.i(TAG, "initView()");
        tablayout.addTab(tablayout.newTab().setText("text"));
        if (!TextUtils.isEmpty(articleBean.translationPath)) {
            tablayout.addTab(tablayout.newTab().setText("translation"));
        }
        if (!TextUtils.isEmpty(articleBean.lrcPath)) {
            tablayout.addTab(tablayout.newTab().setText("subtitle"));
        }
        showView(0);
    }

    private void initWebview() {
        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });
        WebSettings webSettings = mWebView.getSettings();
        // 设置与Js交互的权限
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(false);// 开启 DOM storage API 功能
        webSettings.setDatabaseEnabled(false);// 开启 database storage API 功能
        webSettings.setAppCacheEnabled(false);//开启 Application Caches 功能
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE); // 关闭webview中缓存
        // 将Android里面定义的类对象AndroidJs暴露给javascript

        webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式
        webSettings.setBuiltInZoomControls(false);
        webSettings.setUseWideViewPort(false); //将图片调整到适合webview的大小
        webSettings.supportMultipleWindows(); //多窗口
        webSettings.setAllowFileAccess(true); // 设置可以访问文件
        webSettings.setNeedInitialFocus(true); // 当webview调用requestFocus时为webview设置节点
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); // 支持通过JS打开新窗口
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        webSettings.setLoadsImagesAutomatically(true); // 支持自动加载图片
        webSettings.setDisplayZoomControls(false); // 显示放大缩小按钮
        webSettings.setTextZoom(120);
        webSettings.setSupportZoom(false); // 支持缩放

        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN); // 禁止横向滚动
        mWebView.setHorizontalScrollBarEnabled(false);//水平不显示
        mWebView.setOverScrollMode(WebView.OVER_SCROLL_NEVER);
        mWebView.loadUrl("file://" + articleBean.textPath);

        try {
            if (TextUtils.isEmpty(articleBean.translationPath)) {
                return;
            }
            String s = FileUtils.readFile(articleBean.translationPath);
            Spanned spanned = Html.fromHtml(s);
            textview.setText(spanned);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
