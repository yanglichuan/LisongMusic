package music.lisong.com.lisongmusic.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;

import music.lisong.com.lisongmusic.Ap;
import music.lisong.com.lisongmusic.R;
import music.lisong.com.lisongmusic.adapter.LastestStroryAdapter;
import music.lisong.com.lisongmusic.storyaudioservice.MusicServiceUtil;
import music.lisong.com.lisongmusic.storyaudioservice.PlayingControlHelper;
import music.lisong.com.lisongmusic.utils.DatabaseHelper;
import music.lisong.com.lisongmusic.view.TwinkingFreshLayout;

public class LatestPlayActivity extends BaseActivity {
    private LastestStroryAdapter adapter;
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

        findViewById(R.id.view_back).setOnClickListener(new View.OnClickListener() {
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

    public void onRefresh() {
        DatabaseHelper helper = DatabaseHelper.getInstance();
        getAdapter().setNewData(helper.getAllSong());
        if (refreshLayout != null) {
            refreshLayout.onFinishRefresh();
        }

    }

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


