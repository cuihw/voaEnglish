package com.example.zztest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnInfoListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.example.zztest.downloader.ArticleFile;
import com.example.zztest.downloader.CacheToFile;
import com.example.zztest.downloader.LocalFileCache;

public class ArticleActivity extends Activity {

    protected static final String TAG = "ArticleActivity";

    private static final int FRESH_TIME = 1;

    private WebView mWebView;

    boolean mModifyFlag = false;

    private ImageView mPlay, mBackward, mForward;

    private Button mLrcButton, textButton, translationButton;

    static private MediaPlayer mp = new MediaPlayer();

    private ArticleFile mArticleFile;

    private static ArticleFile mPlayingArticleFile;

    private TextView mTimeTotalText;

    private TextView mTimePlayedText;

    private SeekBar mSeekBar;

    final Handler handler = new Handler() {

        public void handleMessage(Message msg) { // handle message
            switch (msg.what) {
                case FRESH_TIME:

                    if (mp.isPlaying()) {
                        int played = mp.getCurrentPosition();
                        mSeekBar.setProgress(played);
                        mTimePlayedText.setText(getTime(played));
                    }

                    Message message = handler.obtainMessage(FRESH_TIME);
                    handler.sendMessageDelayed(message, 1000);
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.article_main);

        mWebView = (WebView) findViewById(R.id.webView1);

        mPlay = (ImageView) findViewById(R.id.play);

        mBackward = (ImageView) findViewById(R.id.backward);

        mForward = (ImageView) findViewById(R.id.forward);

        textButton = (Button) findViewById(R.id.text);

        translationButton = (Button) findViewById(R.id.translation);

        mLrcButton = (Button) findViewById(R.id.zimu);

        mTimeTotalText = (TextView) findViewById(R.id.timetotal);

        mTimePlayedText = (TextView) findViewById(R.id.timeplayed);

        mSeekBar = (SeekBar) findViewById(R.id.seekbar);
        
        mSeekBar.setOnSeekBarChangeListener(mOnSeekBarChangeListener);

        Intent intent = getIntent();

        String articleKey = intent.getStringExtra("article_key");

        HashMap<String, ArticleFile> map = LocalFileCache.getInstance().getLocalFileMap();

        mArticleFile = map.get(articleKey);

        if (mArticleFile != null) {

            loadText(false);

            if (mArticleFile.translation == null) {
                translationButton.setVisibility(View.INVISIBLE);
            }

            if (mArticleFile.audio != null) {
                loadAudio();
            }
        }
    }

    public void onclickPlayPause(View view) {
        if (mp.isPlaying()) {
            mp.pause();
            mPlay.setBackgroundResource(R.drawable.play_select);
        } else {
            mp.start();
            mPlay.setBackgroundResource(R.drawable.pause_select);
        }
    }

    public void onclickText(View view) {
        loadText(false);
    }
    

    public void onclickPreviously(View view) {
        playPreviously();
    }
    
    public void onclickNext(View view) {
        playNext();
    }

    public void onclickTranslation(View view) {
        loadText(true);
    }

    public void onclickExit(View view) {
        ArticleActivity.this.finish();
    }

    private void loadText(boolean isTranslation) {
        String content = null;

        if (isTranslation) {
            content = CacheToFile.readFile(mArticleFile.translation);
            translationButton.setBackgroundResource(R.drawable.btn_default_pressed);
            textButton.setBackgroundResource(R.drawable.button_select);

        } else {
            content = CacheToFile.readFile(mArticleFile.localFileName);
            translationButton.setBackgroundResource(R.drawable.button_select);
            textButton.setBackgroundResource(R.drawable.btn_default_pressed);
        }

        if (content != null) {
            content = "<P>" + mArticleFile.getTitle() + "</P><P></P>" + content;

            mWebView.loadUrl(mArticleFile.localFileName);
            mWebView.loadDataWithBaseURL(null, content, "text/html", "UTF-8", null);
        }

    }
    OnSeekBarChangeListener mOnSeekBarChangeListener = new OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar arg0, int arg1, boolean fromUser) {
            // TODO Auto-generated method stub
            Log.d(TAG, "onProgressChanged () arg1 = " + arg1 + ", fromUser = " + fromUser);
            if (fromUser) {
                mp.seekTo(arg1);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar arg0) {
            Log.d(TAG, "onStartTrackingTouch () arg1 = ");
        }

        @Override
        public void onStopTrackingTouch(SeekBar arg0) {
            Log.d(TAG, "onStopTrackingTouch () arg1 = ");
        }

    };
    
    private OnInfoListener mpListener = new OnInfoListener() {

        @Override
        public boolean onInfo(MediaPlayer arg0, int arg1, int arg2) {
            return false;
        }
    };

    private void loadAudio() {

        if (mArticleFile.audio != null) {

            try {
                if (mPlayingArticleFile != mArticleFile) {
                    mp.stop();
                    mp = new MediaPlayer();
                    mp.setDataSource(mArticleFile.audio);
                    mp.prepare();
                    mp.start();
                    mPlayingArticleFile = mArticleFile;
                }

                int length = mp.getDuration();

                mp.setOnCompletionListener(new OnCompletionListener(){
                    @Override
                    public void onCompletion(MediaPlayer arg0) {

                        Log.d(TAG, "onCompletion () ....................................................................");
                        playNext();
                    }});

                mTimeTotalText.setText(getTime(length));

                mSeekBar.setMax(length);
                mSeekBar.setProgress(mp.getCurrentPosition());

                Message message = handler.obtainMessage(FRESH_TIME);
                handler.sendMessage(message);
                if (mp.isPlaying()) {
                    mPlay.setBackgroundResource(R.drawable.pause_select);
                } else {
                    mPlay.setBackgroundResource(R.drawable.play_select);
                }

            } catch (IllegalArgumentException e) {

                e.printStackTrace();

            } catch (IllegalStateException e) {

                e.printStackTrace();

            } catch (IOException e) {

                e.printStackTrace();

            }
        }

        Constant.PLAYING_ARTICLE_FILE = mArticleFile;
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

    static private ArrayList<ArticleFile> getListItem () {

        ArrayList<ArticleFile> listItem  = null;

        HashMap<String, ArticleFile> map = LocalFileCache.getInstance().getLocalFileMap();
        if (map != null) {
            Iterator<Entry<String, ArticleFile>> iter = map.entrySet().iterator();

            listItem = new ArrayList<ArticleFile>();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                ArticleFile localFile = (ArticleFile) entry.getValue();
                listItem.add(localFile);
            }
        }
        return listItem;
    }
    
    private void playNext() {
        ArrayList<ArticleFile> list = getListItem ();
        int index = list.indexOf(mArticleFile);
        if (list.size() == index +1) {
            mArticleFile = list.get(0);
        } else {
            mArticleFile = list.get(index + 1);
        }
        

        if (mArticleFile != null) {

            loadText(false);

            if (mArticleFile.translation == null) {
                translationButton.setVisibility(View.INVISIBLE);
            } else {
                translationButton.setVisibility(View.VISIBLE);
            }

            if (mArticleFile.audio != null) {
                loadAudio();
            }
        }
    }

    private void playPreviously() {
        ArrayList<ArticleFile> list = getListItem ();
        int index = list.indexOf(mArticleFile);
        if (0 == index) {
            mArticleFile = list.get(list.size() -1);
        } else {
            mArticleFile = list.get(index - 1);
        }
        

        if (mArticleFile != null) {

            loadText(false);

            if (mArticleFile.translation == null) {
                translationButton.setVisibility(View.INVISIBLE);
            } else {
                translationButton.setVisibility(View.VISIBLE);
            }

            if (mArticleFile.audio != null) {
                loadAudio();
            }
        }
    }

}
