package example.com.zztest.data;

import android.os.Environment;

import java.util.ArrayList;
import java.util.List;

import example.com.zztest.Bean.ArticleChannel;
import example.com.zztest.R;

public class Constants {

    public static final String BASE_URL = "http://www.51voa.com";
    public static final String CHANNEL = "channel";
    public static final String PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/51voa";
    public static final String PATH_DOWNLOAD = Environment.getExternalStorageDirectory().getAbsolutePath() + "/51voa/CACHE";

    public static List<ArticleChannel> LIST_ARTICLE = new ArrayList<>();

    static {
        ArticleChannel art = new ArticleChannel("慢速英语", "Special English", R.color.Blue2, "http://www.51voa.com/VOA_Special_English/");
        LIST_ARTICLE.add(art);
        art = new ArticleChannel("常速英语", "Standard English", R.color.orange1, "http://www.51voa.com/VOA_Standard_English/");
        LIST_ARTICLE.add(art);
        art = new ArticleChannel("日常语法 ", "Everyday Grammar", R.color.red2, "http://www.51voa.com/Everyday_Grammar_1.html");
        LIST_ARTICLE.add(art);
        art = new ArticleChannel("英语教学 ", "English Learning", R.color.Blue1, "http://www.51voa.com/VOA_English_Learning/");
        LIST_ARTICLE.add(art);
        art = new ArticleChannel("一分钟英语 ", "English in a Minute", R.color.red1, "http://www.51voa.com/English_in_a_Minute_Videos_1.html");
        LIST_ARTICLE.add(art);
        art = new ArticleChannel("字幕译文 ", "English with Subtitle", R.color.colorAccent, "http://www.51voa.com/VOA_Special_English/");
        LIST_ARTICLE.add(art);
    }

}
