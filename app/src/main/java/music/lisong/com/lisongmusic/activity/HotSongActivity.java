package music.lisong.com.lisongmusic.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import music.lisong.com.lisongmusic.R;
import music.lisong.com.lisongmusic.adapter.HotSongAdapter;
import music.lisong.com.lisongmusic.bean.Song;
import music.lisong.com.lisongmusic.view.TwinkingFreshLayout;

//热门歌曲
public class HotSongActivity extends AppCompatActivity {

    TwinklingRefreshLayout refreshLayout;
    RecyclerView recyclerView;
    private HotSongAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotablum);

        refreshLayout = (TwinklingRefreshLayout) findViewById(R.id.swipe_refresh_widget);
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
                        HotSongActivity.this.onRefresh();
                    }
                }, 100);
            }
        });


        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        // 设置item动画
        recyclerView.setAdapter(getAdapter());


        View view_back = findViewById(R.id.view_back);
        if (view_back != null) {
            view_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        }

        TextView tv_ablum_name = (TextView) findViewById(R.id.tv_ablum_name);
        if (tv_ablum_name != null) {
            tv_ablum_name.setText("热门歌曲");
        }


        onRefresh();
    }

    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                final BmobQuery<Song> sss = new BmobQuery<>();
                sss.findObjects(new FindListener<Song>() {
                    @Override
                    public void done(List<Song> list, BmobException e) {
                        getAdapter().setNewData(list);

                        if (refreshLayout != null) {
                            refreshLayout.finishRefreshing();
                        }
                    }
                });


            }
        }, 100);
    }

    private BaseQuickAdapter getAdapter() {
        if (adapter == null) {
            adapter = new HotSongAdapter(this);
//            adapter.setOnLoadMoreListener(this);
            //杨立川
            recyclerView.addOnItemTouchListener(adapter.innerItemListner);
        }
        return adapter;
    }

    private void addHeader() {
        View vvv = View.inflate(this, R.layout.rrr_title, null);
        adapter.addHeaderView(vvv);
    }
}


