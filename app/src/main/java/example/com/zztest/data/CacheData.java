package example.com.zztest.data;

import android.util.Log;

import org.greenrobot.greendao.query.Query;

import java.util.ArrayList;
import java.util.List;

import example.com.zztest.App;
import example.com.zztest.Bean.ArticleBean;
import example.com.zztest.Bean.ArticleBeanDao;

public class CacheData {
    private static final String TAG = "CacheData";
    static  CacheData instence = new CacheData();

    public static CacheData getInstence() {
        return instence;
    }

    public void init() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ArticleBeanDao articleBeanDao = App.getInstences().daoSession.getArticleBeanDao();
                    Query<ArticleBean> build = articleBeanDao.queryBuilder()
                            .orderAsc(ArticleBeanDao.Properties.Id).build();
                    //build.setLimit(40);

                    List<ArticleBean> list = build.list();
                    localArticleBeanList.addAll(list);
                    for (ArticleBean bean : localArticleBeanList) {
                        Log.i(TAG, "bean [" + bean.getId() + "] = " + bean.toJson());
                        if (!bean.isDownLoaded()) {
                            bean.setDownLoaded(true);
                            articleBeanDao.update(bean);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    List<ArticleBean> localArticleBeanList = new ArrayList<>();

    public static List<ArticleBean> getLocalArticleBeanList() {
        return instence.localArticleBeanList;
    }

    public static void setLocalArticleBeanList(List<ArticleBean> localArticleBeanList) {
        if (localArticleBeanList != null) {
            instence.localArticleBeanList = localArticleBeanList;
        }
    }

    public void save(ArticleBean bean) {
        ArticleBeanDao articleBeanDao = App.getInstences().daoSession.getArticleBeanDao();
        long insert = articleBeanDao.insertOrReplace(bean);
        bean.setDownLoaded(true);
        Log.i(TAG, "save bean = " + bean.toJson());
        localArticleBeanList.add(bean);
    }


}
