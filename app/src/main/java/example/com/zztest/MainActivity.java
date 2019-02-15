package example.com.zztest;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.classic.adapter.BaseAdapterHelper;
import com.classic.adapter.CommonAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import example.com.zztest.Bean.ArticleChannel;
import example.com.zztest.data.CacheData;
import example.com.zztest.data.Constants;

public class MainActivity extends BaseActivity {

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
}
