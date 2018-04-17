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

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import music.lisong.com.lisongmusic.R;
import music.lisong.com.lisongmusic.adapter.MainPageAdapter;
import music.lisong.com.lisongmusic.bean.Ablum;
import music.lisong.com.lisongmusic.bean.HotAblum;
import music.lisong.com.lisongmusic.storyaudioservice.MusicServiceUtil;
import music.lisong.com.lisongmusic.view.TwinkingFreshLayout;

public class MainActivity extends AppCompatActivity {


    List<HotAblum> ablums = new ArrayList<>();
    RecyclerView recyclerView;
    TwinklingRefreshLayout refreshLayout;
    private MainPageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(music.lisong.com.lisongmusic.R.layout.activity_main);

        ablums.clear();


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
                MainActivity.this.onRefresh();
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
                startActivity(new Intent(MainActivity.this, SearchActivity.class));
            }
        });

        addHeader();

        MusicServiceUtil.bindMusicService(null);

    }

    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                final BmobQuery<HotAblum> sss = new BmobQuery<>();
                sss.findObjects(new FindListener<HotAblum>() {
                    @Override
                    public void done(List<HotAblum> list, BmobException e) {
                        if (refreshLayout != null) {
                            refreshLayout.onFinishRefresh();
                        }

                        ablums = list;
                        getAdapter().setNewData(ablums);
                    }
                });


            }
        }, 10);

    }

    private BaseQuickAdapter getAdapter() {
        if (adapter == null) {
            adapter = new MainPageAdapter(this);
//            adapter.setOnLoadMoreListener(this);
            //杨立川
            recyclerView.addOnItemTouchListener(adapter.innerItemListner);
        }
        return adapter;
    }

    private void addHeader() {
        View vvv = View.inflate(this, music.lisong.com.lisongmusic.R.layout.rrr_title, null);
        vvv.findViewById(R.id.view_hotauthor).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, HotAuthorActivity.class));
            }
        });
        vvv.findViewById(R.id.view_hotsong).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, HotSongActivity.class));
            }
        });
        vvv.findViewById(R.id.view_ablum).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SingerAblumActivity.class));
            }
        });
        vvv.findViewById(R.id.view_latestplay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, LatestPlayActivity.class));
            }
        });
        vvv.findViewById(R.id.view_mylike).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, MyLikeActivity.class));
            }
        });

        vvv.findViewById(R.id.view_myab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, MyAblumActivity.class));
            }
        });



        adapter.addHeaderView(vvv);
    }

    @Override
    protected void onResume() {
        super.onResume();
        onRefresh();
    }
}


