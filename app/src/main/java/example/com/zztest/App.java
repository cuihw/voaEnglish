package example.com.zztest;

import android.app.Application;

import org.greenrobot.greendao.database.Database;

import example.com.zztest.Bean.DaoMaster;
import example.com.zztest.Bean.DaoSession;
import example.com.zztest.download.HttpRequest;

public class App extends Application {

    static App instences = null;
    public DaoSession daoSession;

    public static App getInstences() {
        return instences;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instences = this;
        HttpRequest.initOkGo(this);
        initGreenDao();
    }

    private void initGreenDao() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "info-db");
        Database db = helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();
    }
}
