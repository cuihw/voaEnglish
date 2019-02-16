package example.com.zztest;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.classic.adapter.BaseAdapterHelper;
import com.classic.adapter.CommonAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import example.com.zztest.Bean.ArticleChannel;
import example.com.zztest.data.CacheData;
import example.com.zztest.data.Constants;
import example.com.zztest.utils.ToastUtil;

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";
    @BindView(R.id.gridview)
    GridView gridview;

    List<ArticleChannel> list = new ArrayList<>();
    CommonAdapter<ArticleChannel> adapter;

    private Context getContext(){
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        CacheData.getInstence().init();
        checkWritePermission();
    }


    private void initView() {
        list = Constants.LIST_ARTICLE;
        adapter = new CommonAdapter<ArticleChannel>(this, R.layout.item_class, list) {
            @Override
            public void onUpdate(BaseAdapterHelper helper, ArticleChannel item, int position) {
                helper.setText(R.id.name,item.getName());
                helper.setText(R.id.ename, item.geteName());
                helper.getView(R.id.name).setBackgroundColor(getResources().getColor(item.getColorResid()));
            }
        };
        gridview.setAdapter(adapter);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position < list.size()) {
                    ArticleChannel articleChannel = list.get(position);
                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.CHANNEL, articleChannel.toJson());
                    ArticleListActivity.startActivity(getContext(),bundle);
                }
            }
        });
    }


    private void checkWritePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return;
            } else {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},0);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        int length = permissions.length;

        for (int i = 0; i < length; i++) {
            Log.i(TAG, permissions[i] + " permission is " + grantResults[i]);
            if (permissions[i].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                if (grantResults[i] == 0) {
                } else {
                    Log.i(TAG, "refuse the permission.");
                    ToastUtil.showTextToast(getContext(), "refuse wirte permission.");
                    finish();
                }
            }
            if (permissions[i].equals(Manifest.permission.READ_PHONE_STATE)) {
                if (grantResults[i] == 0) {

                } else {
                    Log.i(TAG, "refuse the permission.");
                    ToastUtil.showTextToast(getContext(), "refuse wirte permission.");
                }
            }
        }
    }

}
