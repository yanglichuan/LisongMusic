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

public class CreateABlumActivity extends BaseActivity {
    TwinklingRefreshLayout refreshLayout;
    RecyclerView recyclerView;
    private LastestStroryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(music.lisong.com.lisongmusic.R.layout.activity_main);

        refreshLayout =
                (TwinklingRefreshLayout) findViewById(music.lisong.com.lisongmusic.R.id.swipe_refresh_widget);
        TwinkingFreshLayout headerView = new TwinkingFreshLayout(this);
        refreshLayout.setHeaderView(headerView);
        refreshLayout.setOverScrollRefreshShow(false);
        refreshLayout.setEnableOverScroll(false);
        refreshLayout.setEnableLoadmore(false);
        refreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(final TwinklingRefreshLayout refreshLayout) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        CreateABlumActivity.this.onRefresh();
                    }
                }, 100);
            }
        });


        recyclerView = (RecyclerView) findViewById(music.lisong.com.lisongmusic.R.id.recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        // 设置item动画
        recyclerView.setAdapter(getAdapter());


        findViewById(music.lisong.com.lisongmusic.R.id.view_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CreateABlumActivity.this, SearchActivity.class));
            }
        });

        addHeader();

        onRefresh();
    }

    public void onRefresh() {
//        ArrayList<StoryBean> ssss = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            ssss.add(new StoryBean());
//        }
//        getAdapter().setNewData(ssss);
    }

    private BaseQuickAdapter getAdapter() {
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


