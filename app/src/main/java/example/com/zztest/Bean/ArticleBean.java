package example.com.zztest.Bean;

import com.google.gson.Gson;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;

import example.com.zztest.utils.Utils;

@Entity
public class ArticleBean {
    public String textPath;
    public String lrcPath;
    String channel = "unknow";
    public String audioPath;
    String content;  // 正文

    String translationUrl;  // 翻译链接

    String lrcUrl;   // 字幕链接

    String audioUrl;   // 音频链接

    boolean isMyFavorite; // 收藏

    boolean isDownLoaded;     // 下载
    public String translationPath;
    @Unique
    String title;       // 文章标题
    @Unique
    String url;      // 正文链接
    @Id
    private Long id;

    private long dateL;

    @Generated(hash = 733578692)
    public ArticleBean(String textPath, String lrcPath, String channel, String audioPath,
                       String content, String translationUrl, String lrcUrl, String audioUrl,
                       boolean isMyFavorite, boolean isDownLoaded, String translationPath, String title,
                       String url, Long id, long dateL) {
        this.textPath = textPath;
        this.lrcPath = lrcPath;
        this.channel = channel;
        this.audioPath = audioPath;
        this.content = content;
        this.translationUrl = translationUrl;
        this.lrcUrl = lrcUrl;
        this.audioUrl = audioUrl;
        this.isMyFavorite = isMyFavorite;
        this.isDownLoaded = isDownLoaded;
        this.translationPath = translationPath;
        this.title = title;
        this.url = url;
        this.id = id;
        this.dateL = dateL;
    }

    @Generated(hash = 392728754)
    public ArticleBean() {
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getDateL() {
        return dateL;
    }

    public void setDateL(long dateL) {
        this.dateL = dateL;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    public String getLrcUrl() {
        return lrcUrl;
    }

    public void setLrcUrl(String lrcUrl) {
        this.lrcUrl = lrcUrl;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public boolean isDownLoaded() {
        return isDownLoaded;
    }

    public void setDownLoaded(boolean downLoaded) {
        isDownLoaded = downLoaded;
    }

    public boolean isMyFavorite() {
        return isMyFavorite;
    }

    public void setMyFavorite(boolean myFavorite) {
        isMyFavorite = myFavorite;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTranslationUrl() {
        return translationUrl;
    }

    public void setTranslationUrl(String translationUrl) {
        this.translationUrl = translationUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        dateL = Utils.getDateByTitle(title);

    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof  ArticleBean) {
            return ((ArticleBean) obj).url.equals(url);
        }
        return false;
    }

    public String toJson(){
        return new Gson().toJson(this);
    }

    public static ArticleBean fromJson(String json) throws Exception {
        Gson gson = new Gson();
        try {
            ArticleBean articleBean = gson.fromJson(json, ArticleBean.class);
            return articleBean;
        } catch (Exception e) {
            throw e;
        }
    }

    public String getTranslationPath() {
        return this.translationPath;
    }

    public void setTranslationPath(String translationPath) {
        this.translationPath = translationPath;
    }

    public String getAudioPath() {
        return this.audioPath;
    }

    public void setAudioPath(String audioPath) {
        this.audioPath = audioPath;
    }

    public String getLrcPath() {
        return this.lrcPath;
    }

    public void setLrcPath(String lrcPath) {
        this.lrcPath = lrcPath;
    }

    public String getTextPath() {
        return this.textPath;
    }

    public void setTextPath(String textPath) {
        this.textPath = textPath;
    }

    public boolean getIsDownLoaded() {
        return this.isDownLoaded;
    }

    public void setIsDownLoaded(boolean isDownLoaded) {
        this.isDownLoaded = isDownLoaded;
    }

    public boolean getIsMyFavorite() {
        return this.isMyFavorite;
    }

    public void setIsMyFavorite(boolean isMyFavorite) {
        this.isMyFavorite = isMyFavorite;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
