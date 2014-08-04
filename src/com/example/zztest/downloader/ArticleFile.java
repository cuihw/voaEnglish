package com.example.zztest.downloader;


public class ArticleFile {

    /*
     * http://www.51voa.com/lrc/201407/se-edu-us-college-education-30jul14.lrc
     * ivory-tower-explores-crushing-cost-of-us-college-education-57904: http://www.51voa.com/lrc/201407/se-edu-us-college-education-30jul14.lrc; mp3; text; translation; localfile
     * */

    public String key;
    public String localFileName;
    public String title;
    public String text;
    public String translation;
    public String audio;
    public String audioUrl;
    public String lrc;
    public String lrcUrl;
    public String urlstring;
    public String subChannel;

    @Override
    public boolean equals(Object o) {
        // TODO Auto-generated method stub
        if (o instanceof ArticleFile) {
            return this.key.equals(((ArticleFile) o).key);            
        }
        return false;
    }

    public static String getArticleFileByUrl(String afurlstring) {
        String afkey = afurlstring.substring(afurlstring.lastIndexOf("/") + 1);
        afkey = afkey.substring(0, afkey.lastIndexOf("."));
        return afkey;
    }

}
