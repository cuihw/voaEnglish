package example.com.zztest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.text.TextUtils;
import android.util.Log;

import butterknife.BindView;
import example.com.zztest.Bean.ArticleBean;
import example.com.zztest.utils.ToastUtil;

public class GeneralActivity extends BaseActivity {

    private static final String TAG = "GeneralActivity";
    @BindView(R.id.tablayout)
    TabLayout tablayout;
    ArticleBean articleBean = null;

    public static void startActivity(Context context, Bundle bundle) {
        Intent intent = new Intent(context, GeneralActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        getArticle();
        initView();
    }

    private void getArticle() {
        Bundle extras = getIntent().getExtras();
        String articleJson = extras.getString("articleBean");
        Log.i(TAG, "articleJson = " + articleJson);
        try {
            articleBean = ArticleBean.fromJson(articleJson);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (articleBean == null) {
            ToastUtil.showTextToast(this, "article is null, return to article list.");
            finish();
        }
    }

    private void initView() {

        Log.i(TAG, "initView()");
        tablayout.addTab(tablayout.newTab().setText("text"));
        if (!TextUtils.isEmpty(articleBean.translationPath)) {
            tablayout.addTab(tablayout.newTab().setText("translation"));
        }
        if (!TextUtils.isEmpty(articleBean.lrcPath)) {
            tablayout.addTab(tablayout.newTab().setText("subtitle"));
        }
    }


}
