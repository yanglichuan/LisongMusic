package music.lisong.com.lisongmusic.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import music.lisong.com.lisongmusic.Ap;
import music.lisong.com.lisongmusic.R;
import music.lisong.com.lisongmusic.adapter.AblumContentAdapter;
import music.lisong.com.lisongmusic.bean.Ablum;
import music.lisong.com.lisongmusic.bean.Author;
import music.lisong.com.lisongmusic.bean.Song;
import music.lisong.com.lisongmusic.storyaudioservice.MusicServiceUtil;
import music.lisong.com.lisongmusic.storyaudioservice.PlayingControlHelper;

//专辑的具体内容  可以是专辑  也可以是作者
public class AblumContentActivity extends BaseActivity {
    Ablum ablum;
    Author author;
    private AblumContentAdapter adapter;
    private TextView tv_ablum_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    protected int getLayoutInt() {
        return R.layout.activity_latest;
    }

    @Override
    protected void initView() {
        super.initView();
        ablum = (Ablum) getIntent().getSerializableExtra("data");
        author = (Author) getIntent().getSerializableExtra("author");

        tv_ablum_name = (TextView) findViewById(R.id.tv_ablum_name);
        if (ablum != null) {
            tv_ablum_name.setText(ablum.getName());
        }
        if (author != null) {
            tv_ablum_name.setText(author.getName());
        }


        findViewById(music.lisong.com.lisongmusic.R.id.view_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AblumContentActivity.this, SearchActivity.class));
            }
        });


        findViewById(music.lisong.com.lisongmusic.R.id.view_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        findViewById(music.lisong.com.lisongmusic.R.id.view_playall).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(adapter.getData()==null || adapter.getData().size()==0){
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
    public void onRefresh() {
        BmobQuery<Song> songBmobQuery = new BmobQuery<>();
        if (ablum != null) {
            songBmobQuery.addWhereEqualTo("belongAblum", ablum.getName());
        }
        if (author != null) {
            songBmobQuery.addWhereEqualTo("author", author.getName());
        }
        songBmobQuery.findObjects(new FindListener<Song>() {
            @Override
            public void done(List<Song> list, BmobException e) {
                if (list != null && list.size() > 0) {
                    getAdapter().setNewData(list);
                }
                if (refreshLayout != null) {
                    refreshLayout.onFinishRefresh();
                }
            }
        });
    }

    @Override
    protected BaseQuickAdapter getAdapter() {
        if (adapter == null) {
            adapter = new AblumContentAdapter(this);
            adapter.setAblum(ablum);
//            adapter.setOnLoadMoreListener(this);
            //杨立川
            recyclerView.addOnItemTouchListener(adapter.innerItemListner);
        }
        return adapter;
    }
}


