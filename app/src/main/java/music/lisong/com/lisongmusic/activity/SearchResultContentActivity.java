package music.lisong.com.lisongmusic.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import music.lisong.com.lisongmusic.Ap;
import music.lisong.com.lisongmusic.R;
import music.lisong.com.lisongmusic.adapter.SearchResultContentAdapter;
import music.lisong.com.lisongmusic.bean.Song;
import music.lisong.com.lisongmusic.storyaudioservice.MusicServiceUtil;
import music.lisong.com.lisongmusic.storyaudioservice.PlayingControlHelper;

//专辑 详细内容
public class SearchResultContentActivity extends BaseActivity {
    String keyword;
    private SearchResultContentAdapter adapter;
    private TextView tv_ablum_name;


    @Override
    protected void initView() {
        super.initView();
        tv_ablum_name = (TextView) findViewById(R.id.tv_ablum_name);
        if (keyword != null) {
            tv_ablum_name.setText(keyword);
        }

        findViewById(R.id.view_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SearchResultContentActivity.this, SearchActivity.class));
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
                if (adapter.getData() == null || adapter.getData().size() == 0) {
                    return;
                }
                //开始播放歌曲
                PlayingControlHelper.setPlayList(adapter.getData(), 0);
                MusicServiceUtil.play(Ap.application,
                        PlayingControlHelper.getPlayingStory().getMp3url(), 1);
            }
        });

        onRefresh();


    }


    @Override
    protected int getLayoutInt() {
        return (R.layout.search_result_activity);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        keyword = (String) getIntent().getStringExtra("data");
        super.onCreate(savedInstanceState);
    }

    public void onRefresh() {
        BmobQuery<Song> songBmobQuery = new BmobQuery<>();
        songBmobQuery.addWhereEqualTo("author", keyword);
        songBmobQuery.findObjects(new FindListener<Song>() {
            @Override
            public void done(List<Song> list, BmobException e) {
                if (list != null && list.size() > 0) {
                    getAdapter().setNewData(list);
                } else {

                    BmobQuery<Song> songBmobQuery = new BmobQuery<>();
                    songBmobQuery.addWhereEqualTo("name", keyword);
                    songBmobQuery.findObjects(new FindListener<Song>() {
                        @Override
                        public void done(List<Song> list, BmobException e) {
                            if (list != null && list.size() > 0) {
                                getAdapter().setNewData(list);
                            }
                        }
                    });

                }


                if (refreshLayout != null) {
                    refreshLayout.onFinishRefresh();
                }
            }
        });
    }

    protected BaseQuickAdapter getAdapter() {
        if (adapter == null) {
            adapter = new SearchResultContentAdapter(this);
//            adapter.setOnLoadMoreListener(this);
            //杨立川
            recyclerView.addOnItemTouchListener(adapter.innerItemListner);
        }
        return adapter;
    }
}


