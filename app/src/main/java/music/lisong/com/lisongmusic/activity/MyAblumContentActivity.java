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
import cn.bmob.v3.listener.SaveListener;
import music.lisong.com.lisongmusic.Ap;
import music.lisong.com.lisongmusic.R;
import music.lisong.com.lisongmusic.adapter.MyAblumContentAdapter;
import music.lisong.com.lisongmusic.bean.Ablum;
import music.lisong.com.lisongmusic.bean.Author;
import music.lisong.com.lisongmusic.bean.MyAblumIncludeSong;
import music.lisong.com.lisongmusic.bean.Song;
import music.lisong.com.lisongmusic.storyaudioservice.MusicServiceUtil;
import music.lisong.com.lisongmusic.storyaudioservice.PlayingControlHelper;
import music.lisong.com.lisongmusic.utils.ToastUtil;
import music.lisong.com.lisongmusic.view.TwinkingFreshLayout;

//专辑 详细内容
public class MyAblumContentActivity extends BaseActivity {
    Ablum ablum;
    Author author;
    private MyAblumContentAdapter adapter;
    private TextView tv_ablum_name;

    public static Song selectSong = null;
    public static boolean toSlect = false;

    @Override
    protected void onDestroy() {
        toSlect = false;
        selectSong = null;
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();


        if (selectSong != null) {
            final MyAblumIncludeSong myAblumIncludeSong = new MyAblumIncludeSong(selectSong);
            myAblumIncludeSong.setBelongAblum(ablum.getName());

            BmobQuery<MyAblumIncludeSong> songBmobQuery = new BmobQuery<>();
            songBmobQuery.addWhereEqualTo("belongAblum", ablum.getName());
            songBmobQuery.addWhereEqualTo("mp3url", selectSong.getMp3url());
            songBmobQuery.findObjects(new FindListener<MyAblumIncludeSong>() {
                @Override
                public void done(List<MyAblumIncludeSong> list, BmobException e) {
                    if (list != null && list.size() > 0) {
                        ToastUtil.showMessage("已经存在");
                    } else {
                        myAblumIncludeSong.save(new SaveListener<String>() {
                            @Override
                            public void done(String s, BmobException e) {
                                if (e == null) {
                                    ToastUtil.showMessage("添加成功");
                                    selectSong = null;
                                    onRefresh();
                                }
                            }
                        });
                    }
                }
            });
        } else {
            onRefresh();
        }
    }


    @Override
    protected void initView() {
        super.initView();

        tv_ablum_name = (TextView) findViewById(R.id.tv_ablum_name);
        if (ablum != null) {
            tv_ablum_name.setText(ablum.getName());
        }
        if (author != null) {
            tv_ablum_name.setText(author.getName());
        }

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

        findViewById(R.id.view_playall1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                ArrayList<Song> likongs = new ArrayList<>();
                for (int m = 0; m < adapter.getData().size(); m++) {
                    MyAblumIncludeSong ll = adapter.getData().get(m);
                    likongs.add(ll.toSong());
                }
                if (likongs == null || likongs.size() == 0) {
                    return;
                }

                //开始播放歌曲
                PlayingControlHelper.setPlayList(likongs, 0);
                MusicServiceUtil.play(Ap.application,
                        PlayingControlHelper.getPlayingStory().getMp3url(), 1);

            }
        });


        findViewById(R.id.view_playall2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                ArrayList<Song> likongs = new ArrayList<>();
                for (int m = 0; m < adapter.getData().size(); m++) {
                    MyAblumIncludeSong ll = adapter.getData().get(m);
                    likongs.add(ll.toSong());
                }
                if (likongs == null || likongs.size() == 0) {
                    return;
                }

                //开始播放歌曲
                PlayingControlHelper.setPlayList(likongs, 0);
                MusicServiceUtil.play(Ap.application,
                        PlayingControlHelper.getPlayingStory().getMp3url(), 1);

            }
        });


        findViewById(R.id.view_selectsong).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toSlect = true;
                startActivity(new Intent(MyAblumContentActivity.this, HotSongActivity.class));
            }
        });
    }


    @Override
    protected int getLayoutInt() {
        return R.layout.ablum_content;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        ablum = (Ablum) getIntent().getSerializableExtra("data");
        author = (Author) getIntent().getSerializableExtra("author");

        toSlect = false;
        selectSong = null;
        super.onCreate(savedInstanceState);
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

    protected BaseQuickAdapter getAdapter() {
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


