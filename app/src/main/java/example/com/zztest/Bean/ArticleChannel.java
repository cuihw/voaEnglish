package example.com.zztest.Bean;

import com.google.gson.Gson;

public class ArticleChannel {
    String name;
    String eName;
    int colorResid;
    String url;


    public ArticleChannel(String name, String eName, int resid, String url) {
        this.name = name;
        this.eName = eName;
        colorResid = resid;
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String geteName() {
        return eName;
    }

    public void seteName(String eName) {
        this.eName = eName;
    }

    public int getColorResid() {
        return colorResid;
    }

    public void setColorResid(int colorResid) {
        this.colorResid = colorResid;
    }


    public String toJson (){
        return new Gson().toJson(this);
    }
    public static ArticleChannel fromJson(String json){
        return new Gson().fromJson(json, ArticleChannel.class);
    }
}
