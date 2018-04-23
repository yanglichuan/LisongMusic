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

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import music.lisong.com.lisongmusic.R;
import music.lisong.com.lisongmusic.adapter.HotSongAdapter;
import music.lisong.com.lisongmusic.adapter.MyAblumAdapter;
import music.lisong.com.lisongmusic.bean.HotAblum;
import music.lisong.com.lisongmusic.bean.Song;
import music.lisong.com.lisongmusic.view.TwinkingFreshLayout;

public class HotAblumContentActivity extends BaseActivity {
    public static Song toAddSong = null;
    private HotSongAdapter adapter;
    public static HotAblum staticAB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutInt() {
        return R.layout.ablum_singerab;
    }

    @Override
    protected void initView() {
        super.initView();

        TextView tv_ablum_name = (TextView) findViewById(R.id.tv_ablum_name);
        if (tv_ablum_name != null) {
            tv_ablum_name.setText(staticAB.getName());
        }


        findViewById(R.id.view_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        toAddSong = null;
        staticAB = null;
    }

    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                final BmobQuery<Song> sss = new BmobQuery<>();
                sss.findObjects(new FindListener<Song>() {
                    @Override
                    public void done(List<Song> list, BmobException e) {
                        ArrayList<Song> thisSongs  = new ArrayList<>();
                        for (int i=0;i<list.size();i++){
                            Song s = list.get(i);
                            if(staticAB.getSongs()!=null && staticAB.getSongs().contains(String.valueOf(s.getObjectId()))){
                                thisSongs.add(s);
                            }
                        }
                        getAdapter().setNewData(thisSongs);
                        if (refreshLayout != null) {
                            refreshLayout.onFinishRefresh();
                        }
                    }
                });

            }
        }, 100);
    }

    protected BaseQuickAdapter getAdapter() {
        if (adapter == null) {
            adapter = new HotSongAdapter(this);
//            adapter.setOnLoadMoreListener(this);
            //杨立川
            recyclerView.addOnItemTouchListener(adapter.innerItemListner);
        }
        return adapter;
    }

    @Override
    protected void onResume() {
        onRefresh();
        super.onResume();
    }

}


