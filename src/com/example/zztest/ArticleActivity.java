package com.example.zztest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.example.zztest.downloader.ArticleFile;
import com.example.zztest.downloader.CacheToFile;
import com.example.zztest.downloader.LocalFileCache;

public class ArticleActivity extends Activity {

    protected static final String TAG = "ArticleActivity";

    private static final int FRESH_TIME = 1;

    private static final int SHOW_LRC = 2;

    private WebView mWebView;

    boolean mModifyFlag = false;

    private ImageView mPlay, mBackward, mForward;

    private Button mLrcButton, textButton, translationButton;

    public static MediaPlayer mp;

    private ArticleFile mArticleFile;

    private TextView mTimeTotalText;

    private TextView mTimePlayedText;

    private SeekBar mSeekBar;

    private WordView mWordView;

    private LinearLayout mLrc_view;

    private LrcParser mLrcParser;

    private static ArticleFile mBeginRepeatArticleFile = null;

    private int mRepeatMode = SINGLE_REPEAT;

    private static final int SINGLE_REPEAT = 1;

    private static final int REPEAT_ALL = 2;

    private static final int REPEAT_ONCE = 3;

    private final Handler handler = new Handler() {

        public void handleMessage(Message msg) { // handle message
            switch (msg.what) {
                case FRESH_TIME:

                    if (mp != null && mp.isPlaying()) {
                        int played = mp.getCurrentPosition();
                        mSeekBar.setProgress(played);
                        mTimePlayedText.setText(getTime(played));
                    } else {
                        return;
                    }

                    Message message = handler.obtainMessage(FRESH_TIME);
                    handler.sendMessageDelayed(message, 1000);
                    break;

                case SHOW_LRC:
                    if (mp != null && mp.isPlaying() && mLrcParser != null) {
                        List<Integer> timeList = mLrcParser.getTimeList();
                        int current = mp.getCurrentPosition();
                        for (int i = 0; i < timeList.size(); i++) {
                            int offset = timeList.get(i) - current;
                            if (offset >= 0) {
                                if (i > 0) {
                                    mWordView.setFocuseTextKey(timeList.get(i - 1) + "");
                                }
                                Message msg1 = handler.obtainMessage(SHOW_LRC);
                                handler.sendMessageDelayed(msg1, offset);
                                break;
                            }
                        }
                    }
                    break;

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

        mWordView = (WordView) findViewById(R.id.lrc_text);

        mLrc_view = (LinearLayout) findViewById(R.id.lrc_view);

        mBeginRepeatArticleFile = null;

        ZoomControls zoomControls = (ZoomControls) findViewById(R.id.zoomcontrols);
        zoomControls.setOnZoomInClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                mWordView.increaseFontsize();
            }
        });
        zoomControls.setOnZoomOutClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mWordView.decreaseFontsize();
            }
        });

        Intent intent = getIntent();

        String articleKey = intent.getStringExtra("article_key");

        HashMap<String, ArticleFile> map = LocalFileCache.getInstance().getLocalFileMap();

        mArticleFile = map.get(articleKey);

        mBeginRepeatArticleFile = mArticleFile;

        if (mArticleFile != null) {

            loadText(false);

            if (mArticleFile.translation == null) {
                translationButton.setVisibility(View.INVISIBLE);
            }

            showLrcButton();

            loadAudio();
        }
    }

    public void onclickPlayPause(View view) {
        if (mArticleFile.audio == null) {
            Toast.makeText(this, "Sorry, this article has no audio file. \r\n本篇文章没有同步音频！", Toast.LENGTH_SHORT).show();
            return;
        }

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

    public void onclickLrc(View view) {
        mLrc_view.setVisibility(View.VISIBLE);
        mWebView.setVisibility(View.INVISIBLE);
        loadLrcfile();

        translationButton.setBackgroundResource(R.drawable.btn_default_normal);
        textButton.setBackgroundResource(R.drawable.btn_default_normal);
        mLrcButton.setBackgroundResource(R.drawable.btn_default_pressed);
    }

    public void onclickPreviously(View view) {
        mBeginRepeatArticleFile = mArticleFile;
        playPreviously();
    }
    
    public void onclickNext(View view) {
        mBeginRepeatArticleFile = mArticleFile;
        playNext();
    }

    public void onclickTranslation(View view) {
        loadText(true);
    }

    public void onclickExit(View view) {
        ArticleActivity.this.finish();
    }

    public void onclickRepeat(View view) {
        ImageView repeat_view = (ImageView) findViewById(R.id.repeat);

        if (SINGLE_REPEAT == mRepeatMode) {
            mRepeatMode = REPEAT_ALL;
            repeat_view.setImageResource(R.drawable.repeat_all_select);

        } else if (REPEAT_ALL == mRepeatMode) {
            mRepeatMode = REPEAT_ONCE;
            repeat_view.setImageResource(R.drawable.repeat_sequence_select);

        } else if (REPEAT_ONCE == mRepeatMode) {
            mRepeatMode = SINGLE_REPEAT;
            repeat_view.setImageResource(R.drawable.repeat_single_select);
        }
    }

    private void loadText(boolean isTranslation) {
        String content = null;

        mLrc_view.setVisibility(View.INVISIBLE);
        mWebView.setVisibility(View.VISIBLE);

        if (isTranslation) {
            content = CacheToFile.readFile(mArticleFile.translation);
            translationButton.setBackgroundResource(R.drawable.btn_default_pressed);
            textButton.setBackgroundResource(R.drawable.button_select);
            mLrcButton.setBackgroundResource(R.drawable.button_select);
        } else {
            content = CacheToFile.readFile(mArticleFile.localFileName);
            translationButton.setBackgroundResource(R.drawable.button_select);
            textButton.setBackgroundResource(R.drawable.btn_default_pressed);
            mLrcButton.setBackgroundResource(R.drawable.button_select);
        }

        if (content != null) {
            content = "<P>" + mArticleFile.title + "</P><P></P>" + content;

            // mWebView.loadUrl(mArticleFile.localFileName);

            mWebView.getSettings().setLoadsImagesAutomatically(true);

            mWebView.loadDataWithBaseURL(null, content, "text/html", "UTF-8", null);
        }

    }
    OnSeekBarChangeListener mOnSeekBarChangeListener = new OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar arg0, int arg1, boolean fromUser) {
            Log.d(TAG, "onProgressChanged () arg1 = " + arg1 + ", fromUser = " + fromUser);
            if (fromUser) {
                mp.seekTo(arg1);
                if (mLrc_view.getVisibility() == View.VISIBLE) {
                    loadLrcfile();
                }
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

    private void loadAudio() {

        if (mp == null) {
            mp = new MediaPlayer();
        }

        if (mArticleFile.audio != null) {

            try {
                if (Constant.PLAYING_ARTICLE_FILE != mArticleFile) {
                    mp.stop();
                    mp = new MediaPlayer();
                    mp.setDataSource(mArticleFile.audio);
                    mp.prepare();
                    mp.start();
                    Constant.PLAYING_ARTICLE_FILE = mArticleFile;
                }

                int length = mp.getDuration();

                mp.setOnCompletionListener(new OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer arg0) {
                        Log.d(TAG, "onCompletion () ....");

                        if (SINGLE_REPEAT == mRepeatMode) {
                            mp.seekTo(1);
                            mp.start();
                        } else {
                            playNext();
                        }
                    }
                });

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

        if (REPEAT_ALL == mRepeatMode) {

        } else if (REPEAT_ONCE == mRepeatMode) {

        }

        ArrayList<ArticleFile> list = getListItem ();
        int index = list.indexOf(mArticleFile);
        if (list.size() == index +1) {
            mArticleFile = list.get(0);
        } else {
            mArticleFile = list.get(index + 1);
        }

        if (REPEAT_ALL == mRepeatMode) {
            ;
        } else if (REPEAT_ONCE == mRepeatMode) {
            if (mArticleFile == mBeginRepeatArticleFile) {
                mArticleFile = null;
            };
        }

        if (mArticleFile != null) {

            loadText(false);

            if (mArticleFile.translation == null) {
                translationButton.setVisibility(View.INVISIBLE);
            } else {
                translationButton.setVisibility(View.VISIBLE);
            }

            showLrcButton();

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

            showLrcButton();

            if (mArticleFile.audio != null) {
                loadAudio();
            }
        }
    }

    public void showLrcButton() {
        mLrcParser = null;
        if (mArticleFile.lrc != null) {
            mLrcButton.setVisibility(View.VISIBLE);

            mLrcParser = new LrcParser();

            mLrcParser.readLRC(mArticleFile.lrc);

            mWordView.setLrcParser(mLrcParser);
        } else {
            mLrcButton.setVisibility(View.INVISIBLE);
        }
    }

    private void loadLrcfile() {
        if (mArticleFile.lrc != null && mLrcParser != null) {
            List<Integer> timeList = mLrcParser.getTimeList();
            int current = mp.getCurrentPosition();
            for (int i = 0; i < timeList.size(); i++) {
                int offset = timeList.get(i) - current;
                if (offset >= 0) {
                    if (i > 0) {
                        mWordView.setFocuseTextKey(timeList.get(i-1) + "");
                    }
                    Message msg = handler.obtainMessage(SHOW_LRC);
                    handler.sendMessageDelayed(msg, offset);
                    break;
                }
            }
        }
    }

}
