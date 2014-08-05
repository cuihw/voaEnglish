package com.example.zztest;

import java.util.HashMap;

import com.example.zztest.downloader.ArticleFile;
import com.example.zztest.downloader.CacheToFile;
import com.example.zztest.downloader.LocalFileCache;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;


public class ArticleActivity extends Activity {

    protected static final String TAG = "ArticleActivity";

    String testHtml = "<div id=\"content\"><SPAN class=byline>By VOA</SPAN><BR><SPAN class=datetime>24 July, 2014</SPAN>"
                    + "<BR>51VOA听写整理，转载请注明出处。文本仅供参考，欢迎纠错！"
                    + "<P>From VOA Learning English, this is the Economics Report.</P> "
                    + "<P>The political crisis in Ukraine has forced many European nations to reconsider their dependence on Russia for energy. Some nations are looking at new ways to recover natural gas at home, these methods include a disputed process known as hydraulic fracturing or fracking. </P>"
                    + "<P>The process involves the use of liquid chemicals to break rock. Experts believe Europe might have trillions of cubic meters of shale gas. Shale is a kind of rock. </P>"
                    + "<P>France, Poland and Ukraine are thought to have the largest amounts of shale gas. Large supplies have been found in Romania, Bulgaria and Britain. </P>"
                    + "<P>Lucia Seybert is with the the Wilson Center in Washington. She says the presence of large shale gas reserves and political problems in Ukraine have increased interest in Europe's shale gas.</P>"
                    + "<P>\"With energy security it's not just a matter of supply, it's also a question of reliability. And one thing that this may do, it may expedite some of these explorations of shale gas within the European Union,\" Seybert said.</P>"
                    + "<P>But Europe is believed to be years away from major shale gas production. Poland, Britain and Romania are expected to start exploration by 2020. </P>"
                    + "<P>Removing shale gas through hydraulic fracking is the subject of often intense debate. Most drilling areas in Europe are near populated areas and environmental groups have raised concerns about water and air pollution from fracking. </P>"
                    + "<P>There also are political concerns. Eric Stewart is the president of the Romanian-American and Polish-American Business Councils. He says European taxes and rules on the industry make it difficult for gas removal companies. Energy companies also must battle a strong environmental movement and public opposition. </P>"
                    + "<P>Keith Smith is a former U.S. ambassador to Lithuania. He says the fracking can help Europe meet its energy needs if the plan includes fossil fuels and renewable sources of energy like solar and wind.</P>"
                    + "<P>Much of Europe's gas flows through a pipeline from Russia across Ukraine. But Ukraine has had difficulty paying Russia's Gazprom energy company. And earlier this year, Ukrainian protesters ousted the country's pro-Russia president. The new government signed economic agreements with the European Union, over Russian objections. Now Russia has signaled it may cut off gas to Ukraine, and to much of Europe.</P>"
                    + "<P>The Wilson Center's Lucia Seybert says shale gas exploration will not provide Europe complete energy independence from Russian imports, but it will reduce that dependence in the long-term.</P>"
                    + "<P>And that's the Economics Report from VOA Learning English. I'm Mario Ritter.</P></div>";

    WebView mWebView;

    boolean mModifyFlag = false;

    RelativeLayout mControlRelativeLayout;
    LinearLayout mLinearWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.article_main);

        mWebView = (WebView) findViewById(R.id.webView1);
        // mWebView.loadData(testHtml, "text/html", "UTF-8");

        Intent intent = getIntent();

        String articleKey = intent.getStringExtra("article_key");


        HashMap<String, ArticleFile> map = LocalFileCache.getInstance().getLocalFileMap();
        ArticleFile af = map.get(articleKey);
        if (af != null) {
            String content = CacheToFile.readFile(af.localFileName);
            if (content != null) {
                mWebView.loadData(content, "text/html", "UTF-8");
            }
        }
    }
}
