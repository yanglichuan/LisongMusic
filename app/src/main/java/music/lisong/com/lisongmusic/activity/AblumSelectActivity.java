package music.lisong.com.lisongmusic.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;

import music.lisong.com.lisongmusic.adapter.LastestStroryAdapter;
import music.lisong.com.lisongmusic.view.TwinkingFreshLayout;

public class AblumSelectActivity extends BaseActivity {
    private LastestStroryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutInt() {
        return music.lisong.com.lisongmusic.R.layout.activity_main;
    }

    @Override
    protected void initView() {
        super.initView();
        findViewById(music.lisong.com.lisongmusic.R.id.view_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AblumSelectActivity.this, SearchActivity.class));
            }
        });

        addHeader();

        onRefresh();
    }

    @Override
    public void onRefresh() {
//        ArrayList<StoryBean> ssss = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            ssss.add(new StoryBean());
//        }
//        getAdapter().setNewData(ssss);
    }

    @Override
    protected BaseQuickAdapter getAdapter() {
        if (adapter == null) {
            adapter = new LastestStroryAdapter(this);
//            adapter.setOnLoadMoreListener(this);
            //杨立川
            recyclerView.addOnItemTouchListener(adapter.innerItemListner);
        }
        return adapter;
    }

    private void addHeader() {
        View vvv = View.inflate(this, music.lisong.com.lisongmusic.R.layout.rrr_title, null);
        adapter.addHeaderView(vvv);
    }
}


