package example.com.zztest.download;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.cookie.CookieJarImpl;
import com.lzy.okgo.cookie.store.MemoryCookieStore;
import com.lzy.okgo.interceptor.HttpLoggingInterceptor;
import com.lzy.okgo.model.Progress;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import example.com.zztest.data.Constants;
import example.com.zztest.utils.FileUtils;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public class HttpRequest {

    private static final String TAG = "HttpRequest";
    private static OkHttpClient.Builder builder;
    private static OkHttpClient okHttpclient;

    public static void initOkGo(Application appContext) {

        okHttpclient = new OkHttpClient();
        builder = okHttpclient.newBuilder();

        // 全局的读取超时时间
        builder.readTimeout(5000, TimeUnit.MILLISECONDS);
        // 全局的写入超时时间
        builder.writeTimeout(5000, TimeUnit.MILLISECONDS);
        // 全局的连接超时时间
        builder.connectTimeout(5000, TimeUnit.MILLISECONDS);
        builder.cookieJar(new CookieJarImpl(new MemoryCookieStore()));
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor("LINGSHI");
        // log打印级别，决定了log显示的详细程度
        loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY);
        // log颜色级别，决定了log在控制台显示的颜色
        loggingInterceptor.setColorLevel(Level.INFO);

        //if (BuildConfig.DEBUG)
        builder.addInterceptor(loggingInterceptor);

        OkGo.getInstance().init(appContext)
                .setOkHttpClient(builder.build());
    }

    public static void download(String url,  final ReqListener<String> listener){

        String fileName = FileUtils.getFileName(url);
        Log.i(TAG, "downloadfile： path = " + Constants.PATH_DOWNLOAD + ", fileName = " + fileName);
        download( url, Constants.PATH_DOWNLOAD, fileName, listener);
    }

    public static void download(String url, String destFileDir, String destFileName, final ReqListener<String> listener) {

        OkGo.<File>get(url).execute(new FileCallback(destFileDir, destFileName) {
            String fileName = "error";
            @Override
            public void onSuccess(com.lzy.okgo.model.Response<File> response) {
                fileName = response.body().getAbsolutePath();
            }

            @Override
            public void downloadProgress(Progress progress) {
                listener.onFinished("progress:" + progress);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                listener.onFinished("download: " + fileName);
            }
        });
    }

    public static void getDocument(final String url, final ReqListener<Document> response) {

        new AsyncTask<String, Integer, Document>() {
            @Override
            protected Document doInBackground(String... strings) {

                Document doc = null;
                try {
                    doc = Jsoup.connect(url).get();
                    //Log.i(TAG, "doc = " + doc.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return doc;
            }

            @Override
            protected void onPostExecute(Document document) {
                if (response !=null) {
                    response.onFinished(document);
                }
            }
        }.execute();
    }

    interface ReqListener<T>{
        void onFinished (T data);
    }
}
