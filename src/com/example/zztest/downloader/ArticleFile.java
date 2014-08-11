package com.example.zztest.downloader;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.util.Log;

public class ArticleFile implements Comparable{

    /*
     * http://www.51voa.com/lrc/201407/se-edu-us-college-education-30jul14.lrc
     * ivory-tower-explores-crushing-cost-of-us-college-education-57904:
     * http://www.51voa.com/lrc/201407/se-edu-us-college-education-30jul14.lrc; mp3; text;
     * translation; localfile
     */

    private static final String TAG = "ArticleFile";
    static private String regStr =  "\\(([^)]*)\\)";
    static private Pattern mPattern = Pattern.compile(regStr);
    public String key;
    public String title;
    public String localFileName;
    public String urlstring;
    public String translation;
    public String translationUrl;
    public String audio;
    public String audioUrl;
    public String lrc;
    public String lrcUrl;
    public String subChannel;
    public String progress;
    public long date;

    public static String getArticleFileByUrl(String afurlstring) {
        String afkey = afurlstring.substring(afurlstring.lastIndexOf("/") + 1);
        afkey = afkey.substring(0, afkey.lastIndexOf("."));
        return afkey;
    }

    public long getDateByTitle(String title) {
        long retDate = 0;
        String dateString = getStringInBracket(title);
        if (dateString != null) {
            // 2014-8-6
            String number = dateString.substring(0, dateString.indexOf("-"));
            dateString = dateString.substring(dateString.indexOf("-") + 1);
            String number2 = dateString.substring(0, dateString.indexOf("-"));
            if (number2.length() == 1) {
                number = number + "0" + number2;
            } else {
                number = number + number2;
            }

            dateString = dateString.substring(dateString.indexOf("-") + 1);
            number2 = dateString.substring(0);
            if (number2.length() == 1) {
                number = number + "0" + number2;
            } else {
                number = number + number2;
            }

            retDate = Long.parseLong(number);
        }
        Log.d(TAG, "date = " + retDate + ", title = " + title);
        return retDate;
    }

    private static String getStringInBracket(String expression) {

        Matcher matcher = mPattern.matcher(expression);
        while (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    @Override
    public int compareTo(Object arg0) {
        ArticleFile s = (ArticleFile)arg0;
        return date < s.date ? 1 : (date == s.date ? 0 : -1);
    }

    @Override
    public boolean equals(Object o) {
        // TODO Auto-generated method stub
        if (o instanceof ArticleFile) {
            return this.key.equals(((ArticleFile) o).key);
        }
        return false;
    }

    @Override
    public String toString() {

        return "key = " + key + ", title = " + title + ", localFileName = " + localFileName + ", urlstring = "
                + urlstring + ", translation = " + translation + ", translationUrl = " + translationUrl + ", audio = "
                + audio + ", audioUrl = " + audioUrl + ", lrc = " + lrc + ", lrcUrl = " + lrcUrl + ", subChannel = "
                + subChannel;
    }

}
