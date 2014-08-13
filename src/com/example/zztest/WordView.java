package com.example.zztest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.widget.TextView;

public class WordView extends TextView {
    private List<String> mWordsList = new ArrayList<String>();

    private Map<String, String> mWordsMap;

    private Paint mLoseFocusPaint;

    private Paint mOnFocusePaint;

    private float mX = 0;

    private float mMiddleY = 0;

    private float mY = 0;

    private static int DY = 70;

    private int mIndex = 0;

    private LrcParser mLrcParser;

    private int mFontsize = 40;

    public WordView(Context context) throws IOException {
        super(context);
        init();
    }

    public WordView(Context context, AttributeSet attrs) throws IOException {
        super(context, attrs);
        init();
    }

    public WordView(Context context, AttributeSet attrs, int defStyle) throws IOException {
        super(context, attrs, defStyle);
        init();
    }
    
    public void setLrcParser (LrcParser lrcParser) {
        mLrcParser = lrcParser;
        mWordsList = mLrcParser.getWords();
        mWordsMap = mLrcParser.getmWordsMap();
    }

    public void setFocuseTextKey(String key) {
        String words = mWordsMap.get(key);
        if (words != null) {
            int index = mWordsList.indexOf(words);
            if (index != -1) {
                setFocuseTextLine(index);
            }
        }
    }

    private void setFocuseTextLine(int line) {
        if (mWordsList.size() > line) {
            // redraw.
            mIndex = line;
            this.invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = canvas.getWidth(); //1080

        canvas.drawColor(Color.BLACK);
        Paint p = mLoseFocusPaint;
        p.setTextAlign(Paint.Align.CENTER);
        Paint p2 = mOnFocusePaint;
        p2.setTextAlign(Paint.Align.CENTER);

        String linestr = (String) mWordsList.get(mIndex);

        Rect bounds = new Rect();
        p2.getTextBounds(linestr, 0, linestr.length(), bounds);

        int widthbounds = bounds.width();

        canvas.drawText(linestr, mX, mMiddleY, p2);

        int alphaValue = 10;
        float tempY = mMiddleY;
        for (int i = mIndex - 1; i >= 0; i--) {
            tempY -= DY;
            if (tempY < 0) {
                break;
            }
            p.setColor(Color.argb(255 - alphaValue, 245, 245, 245));
            canvas.drawText((String)mWordsList.get(i), mX, tempY, p);
            alphaValue += 10;
        }

        alphaValue = 10;
        tempY = mMiddleY;
        for (int i = mIndex + 1, len = mWordsList.size(); i < len; i++) {
            tempY += DY;
            if (tempY > mY) {
                break;
            }
            p.setColor(Color.argb(255 - alphaValue, 245, 245, 245));
            canvas.drawText((String)mWordsList.get(i), mX, tempY, p);
            alphaValue += 10;
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int ow, int oh) {
        super.onSizeChanged(w, h, ow, oh);

        mX = w * 0.5f;
        mY = h;
        mMiddleY = h * 0.3f;
    }

    private void init() throws IOException {
        setFocusable(true);
        mWordsList.add("没有歌词文件，赶紧去下载");

        mLoseFocusPaint = new Paint();
        mLoseFocusPaint.setAntiAlias(true);
        mLoseFocusPaint.setTextSize(mFontsize);
        mLoseFocusPaint.setColor(Color.WHITE);
        mLoseFocusPaint.setTypeface(Typeface.SERIF);

        mOnFocusePaint = new Paint();
        mOnFocusePaint.setAntiAlias(true);
        mOnFocusePaint.setColor(Color.YELLOW);
        mOnFocusePaint.setTextSize(mFontsize + 10);
        mOnFocusePaint.setTypeface(Typeface.SANS_SERIF);
    }

    public void increaseFontsize() {
        mFontsize = mFontsize + 10;
        DY = mFontsize + 30;
        mLoseFocusPaint.setTextSize(mFontsize);
        mOnFocusePaint.setTextSize(mFontsize + 10);
        this.invalidate();
    }

    public void decreaseFontsize() {
        mFontsize = mFontsize - 10;
        DY = mFontsize + 30;
        mLoseFocusPaint.setTextSize(mFontsize);
        mOnFocusePaint.setTextSize(mFontsize + 10);
        this.invalidate();
    }
}
