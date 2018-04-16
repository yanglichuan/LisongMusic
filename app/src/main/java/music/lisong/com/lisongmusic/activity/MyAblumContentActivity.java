package music.lisong.com.lisongmusic.activity;

import android.content.Intent;
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

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import music.lisong.com.lisongmusic.Ap;
import music.lisong.com.lisongmusic.R;
import music.lisong.com.lisongmusic.adapter.MyAblumContentAdapter;
import music.lisong.com.lisongmusic.bean.Ablum;
import music.lisong.com.lisongmusic.bean.Author;
import music.lisong.com.lisongmusic.bean.MyAblumIncludeSong;
import music.lisong.com.lisongmusic.bean.Song;
import music.lisong.com.lisongmusic.storyaudioservice.MusicServiceUtil;
import music.lisong.com.lisongmusic.storyaudioservice.PlayingControlHelper;
import music.lisong.com.lisongmusic.view.TwinkingFreshLayout;

//专辑 详细内容
public class MyAblumContentActivity extends AppCompatActivity {
    TwinklingRefreshLayout refreshLayout;
    Ablum ablum;
    Author author;
    RecyclerView recyclerView;
    private MyAblumContentAdapter adapter;
    private TextView tv_ablum_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ablum = (Ablum) getIntent().getSerializableExtra("data");
        author = (Author) getIntent().getSerializableExtra("author");


        setContentView(R.layout.ablum_content);
        tv_ablum_name = (TextView) findViewById(R.id.tv_ablum_name);
        if (ablum != null) {
            tv_ablum_name.setText(ablum.getName());
        }
        if (author != null) {
            tv_ablum_name.setText(author.getName());
        }

        refreshLayout =
                (TwinklingRefreshLayout) findViewById(R.id.swipe_refresh_widget);
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
                        MyAblumContentActivity.this.onRefresh();
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


        findViewById(R.id.view_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MyAblumContentActivity.this, SearchActivity.class));
            }
        });


        findViewById(R.id.view_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        findViewById(R.id.view_playall).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                ArrayList<Song> likongs = new ArrayList<>();
                for (int m = 0; m < adapter.getData().size(); m++) {
                    MyAblumIncludeSong ll = adapter.getData().get(m);
                    likongs.add(ll.toSong());
                }
                if(likongs==null || likongs.size()==0){
                    return;
                }

                //开始播放歌曲
                PlayingControlHelper.setPlayList(likongs, 0);
                MusicServiceUtil.play(Ap.application,
                        PlayingControlHelper.getPlayingStory().getMp3url(), 1);

            }
        });


        onRefresh();
    }

    public void onRefresh() {
        BmobQuery<MyAblumIncludeSong> songBmobQuery = new BmobQuery<>();
        if (ablum != null) {
            songBmobQuery.addWhereEqualTo("belongAblum", ablum.getName());
        }
//        if(author!=null){
//            songBmobQuery.addWhereEqualTo("author", author.getName());
//        }
        songBmobQuery.findObjects(new FindListener<MyAblumIncludeSong>() {
            @Override
            public void done(List<MyAblumIncludeSong> list, BmobException e) {
                if (list != null && list.size() > 0) {
                    getAdapter().setNewData(list);
                }
                if (refreshLayout != null) {
                    refreshLayout.onFinishRefresh();
                }
            }
        });


    }

    private BaseQuickAdapter getAdapter() {
        if (adapter == null) {
            adapter = new MyAblumContentAdapter(this);
            adapter.setAblum(ablum);
//            adapter.setOnLoadMoreListener(this);
            //杨立川
            recyclerView.addOnItemTouchListener(adapter.innerItemListner);
        }
        return adapter;
    }
}


