package com.example.zztest;

import java.util.ArrayList;
import java.util.List;

public class Constant {

    public final static String sharedpreferences = "local_article";

    public final static int UP_DATE_DATA = 1;

    public final static int FAILED_UPDATE = 2;

    public final static int UPDATE_TEXT = 3;

    public final static int DOWNLOAD_MP3_PROGRESS = 4;

    public final static int DOWNLOAD_MP3_COMPLETED = 5;

    public final static int DOWNLOAD_UPDATE = 6;

    public static String title = "title";
    public static String subChannle = "subChannle";
    public static String fanyi = "fanyi";
    public static String zimu = "zimu";

    public static String zimulink = "zimuLink";
    public static String fanyilink = "fanyilink";
    public static String titlelink = "titlelink";
    public static String already_downloaded = "already_downloaded";
    
    public static final String [] channels = { "每日更新",
             "慢速英语",
             "标准英语",
             "英语教学",
             "本地文档"};

    public static final WebPageLink VOA_ROOT = new WebPageLink("http://www.51voa.com", "VOA英语");
    public static final WebPageLink VOA_Standard_1 = new WebPageLink("http://www.51voa.com/VOA_Standard_1.html", "VOA常速英语最新");
    public static final WebPageLink VOA_Special_English = new WebPageLink("http://www.51voa.com/VOA_Special_English/", "VOA慢速英语");
    public static final WebPageLink VOA_English_Learning = new WebPageLink("http://www.51voa.com/VOA_English_Learning/", "VOA英语教学");

    public static List<WebPageLink> mStandard = new ArrayList<WebPageLink>();
    public static List<WebPageLink> mSpecial = new ArrayList<WebPageLink>();
    public static List<WebPageLink> mLearning = new ArrayList<WebPageLink>();

    /*<div class="left_nav_title"><a href="/VOA_Standard_English/" >VOA常速英语</a></div>
    <ul>
    <li><a href="/VOA_Standard_1.html">VOA常速英语最新(2012-2014)
               <img src="/images/new.gif" width="19" height="9" border="0"></a></li> 
    http://www.51voa.com/VOA_Standard_1.html
    <li><a href="/VOA_Standard_1_archiver.html">VOA常速英语存档(2005-2011)</a></li> 
    </ul>

    <div class="left_nav_title"><a href="/VOA_Special_English/" >VOA慢速英语</a></div>
    <ul>
    <li><a href="/Technology_Report_1.html">Technology Report科技报道</a></li>
    <li><a href="/This_is_America_1.html">This is America今日美国</a></li>
    <li><a href="/Agriculture_Report_1.html">Agriculture Report农业报道</a></li>
    <li><a href="/Science_in_the_News_1.html">Science in the News科学报道</a></li>
    <li><a href="/Health_Report_1.html">Health Report健康报道</a></li>
    <li><a href="/Explorations_1.html">Explorations自然探索</a></li>
    <li><a href="/Education_Report_1.html">Education Report教育报道</a></li>
    <li><a href="/The_Making_of_a_Nation_1.html">The Making of a Nation建国史话</a></li>
    <li><a href="/Economics_Report_1.html">Economics Report经济报道</a></li>
    <li><a href="/American_Mosaic_1.html">American Mosaic美国万花筒</a></li>
    <li><a href="/In_the_News_1.html">In the News时事新闻</a></li>
    <li><a href="/American_Stories_1.html">American Stories美国故事</a></li>
    <li><a href="/Words_And_Their_Stories_1.html">Words And Their Stories词汇掌故</a></li>

    <li><a href="/People_in_America_1.html">People in America美国人物志</a></li>
    <li><a href="/as_it_is_1.html">AS IT IS慢速新闻杂志</a></li>*/
    
    private static WebPageLink Technology_Report_1 = new WebPageLink("/Technology_Report_1.html", "Technology Report科技报道");

    private static WebPageLink This_is_America_1 = new WebPageLink("/This_is_America_1.html", "This is America今日美国");

    private static WebPageLink Agriculture_Report_1 = new WebPageLink("/Agriculture_Report_1.html", "Agriculture Report农业报道");

    private static WebPageLink Science_in_the_News_1 = new WebPageLink("/Science_in_the_News_1.html", "Science in the News科学报道");

    private static WebPageLink Health_Report_1 = new WebPageLink("/Health_Report_1.html", "Health Report健康报道");

    private static WebPageLink Explorations_1 = new WebPageLink("/Explorations_1.html", "Explorations自然探索");

    private static WebPageLink Education_Report_1 = new WebPageLink("/Education_Report_1.html", "Education Report教育报道");

    private static WebPageLink The_Making_of_a_Nation_1 = new WebPageLink("/The_Making_of_a_Nation_1.html", "The Making of a Nation建国史话");

    private static WebPageLink Economics_Report_1 = new WebPageLink("/Economics_Report_1.html", "Economics Report经济报道");

    private static WebPageLink American_Mosaic_1 = new WebPageLink("/American_Mosaic_1.html", "American Mosaic美国万花筒");

    private static WebPageLink In_the_News_1 = new WebPageLink("/In_the_News_1.html", "In the News时事新闻");

    private static WebPageLink American_Stories_1 = new WebPageLink("/American_Stories_1.html", "American Stories美国故事");

    private static WebPageLink Words_And_Their_Stories_1 = new WebPageLink("/Words_And_Their_Stories_1.html", "Words And Their Stories词汇掌故");

    private static WebPageLink People_in_America_1 = new WebPageLink("/People_in_America_1.html", "People in America美国人物志");

    private static WebPageLink as_it_is_1 = new WebPageLink("/Words_And_Their_Stories_1.html", "AS IT IS慢速新闻杂志");

    /*
    
    </ul>
    <div class="left_nav_title"><a href="/VOA_English_Learning/" >VOA英语教学</a></div>
    <ul>
    
    <li><a href="/Bilingual_News_1.html">Bilingual News双语新闻</a></li>

    <li><a href="/News_Words_1.html">News Words新闻词汇</a></li>

    <li><a href="/Learn_A_Word_1.html">Learn A Word学个词</a></li>
    <li><a href="/Words_And_Idioms_1.html">Words And Idioms美国习惯用语</a></li>
    <li><a href="/English_in_a_Minute_1.html">English in a Minute一分钟英语</a></li>
    <li><a href="/How_American_English_1.html">How to Say it美语怎么说</a></li>

    <li><a href="/Business_Etiquette_1.html">Business Etiquette商务礼节美语</a></li>
    <li><a href="/American_English_Mosaic_1.html">American English Mosaic美语训练班</a></li>
    <li><a href="/Popular_American_1.html">Popular American流行美语</a></li>

    <li><a href="/Sports_English_1.html">Sports English体育美语</a></li>
    <li><a href="/Go_English_1.html">Go English美语三级跳</a></li>
    <li><a href="/Word_Master_1.html">Wordmaster词汇大师</a></li>
    
    
    <li><a href="/American_Cafe_1.html">American Cafe美语咖啡屋</a></li>
    <li><a href="/Intermediate_American_English_1.html">Intermediate American English中级美国英语</a></li>
    <li><a href="/President_Address_1.html">President's Address美国总统演说</a></li>
    </ul>*/

    private static WebPageLink Bilingual_News_1 = new WebPageLink("/Bilingual_News_1.html", "Bilingual News双语新闻");

    private static WebPageLink News_Words_1 = new WebPageLink("/News_Words_1.html", "News Words新闻词汇");

    private static WebPageLink Learn_A_Word_1 = new WebPageLink("/Bilingual_News_1.html", "Learn A Word学个词");

    private static WebPageLink Words_And_Idioms_1 = new WebPageLink("/Words_And_Idioms_1.html", "Words And Idioms美国习惯用语");

    private static WebPageLink English_in_a_Minute_1 = new WebPageLink("/English_in_a_Minute_1.html", "English in a Minute一分钟英语");

    private static WebPageLink How_American_English_1 = new WebPageLink("/How_American_English_1.html", "How to Say it美语怎么说");

    private static WebPageLink Business_Etiquette_1 = new WebPageLink("/Business_Etiquette_1.html", "Business Etiquette商务礼节美语");

    private static WebPageLink American_English_Mosaic_1 = new WebPageLink("/American_English_Mosaic_1.html", "American English Mosaic美语训练班");

    private static WebPageLink Popular_American_1 = new WebPageLink("/Popular_American_1.html", "Popular American流行美语");

    private static WebPageLink Sports_English_1 = new WebPageLink("/Sports_English_1.html", "Sports English体育美语");

    private static WebPageLink Go_English_1 = new WebPageLink("/Go_English_1.html", "Go English美语三级跳");

    private static WebPageLink Word_Master_1 = new WebPageLink("/Word_Master_1.html", "Wordmaster词汇大师");

    private static WebPageLink American_Cafe_1 = new WebPageLink("/American_Cafe_1.html", "American Cafe美语咖啡屋");

    private static WebPageLink Intermediate_American_English_1 = new WebPageLink("/Intermediate_American_English_1.html", "Intermediate American English中级美国英语");

    private static WebPageLink President_Address_1 = new WebPageLink("/President_Address_1.html", "President's Address美国总统演说");



    static {
        // Learning.
        mLearning.add(Bilingual_News_1);
        mLearning.add(News_Words_1);
        mLearning.add(Learn_A_Word_1);
        mLearning.add(Words_And_Idioms_1);
        mLearning.add(English_in_a_Minute_1);
        mLearning.add(How_American_English_1);
        mLearning.add(Business_Etiquette_1);
        mLearning.add(American_English_Mosaic_1);
        mLearning.add(Popular_American_1);
        mLearning.add(Sports_English_1);
        mLearning.add(Go_English_1);
        mLearning.add(Word_Master_1);
        mLearning.add(American_Cafe_1);
        mLearning.add(Intermediate_American_English_1);
        mLearning.add(President_Address_1);

        // standard.
        mStandard.add(VOA_Standard_1);

        // special. 
        mSpecial.add(Technology_Report_1);
        mSpecial.add(This_is_America_1);
        mSpecial.add(Agriculture_Report_1);
        mSpecial.add(Science_in_the_News_1);
        mSpecial.add(Health_Report_1);
        mSpecial.add(Explorations_1);
        mSpecial.add(Education_Report_1);
        mSpecial.add(The_Making_of_a_Nation_1);
        mSpecial.add(Economics_Report_1);
        mSpecial.add(American_Mosaic_1);
        mSpecial.add(In_the_News_1);
        mSpecial.add(American_Stories_1);
        mSpecial.add(Words_And_Their_Stories_1);
        mSpecial.add(People_in_America_1);
        mSpecial.add(as_it_is_1);
    }
}
