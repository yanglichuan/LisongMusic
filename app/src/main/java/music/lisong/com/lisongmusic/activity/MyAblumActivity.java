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

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import music.lisong.com.lisongmusic.R;
import music.lisong.com.lisongmusic.adapter.MyAblumAdapter;
import music.lisong.com.lisongmusic.bean.MyAblum;
import music.lisong.com.lisongmusic.bean.Song;
import music.lisong.com.lisongmusic.view.TwinkingFreshLayout;

public class MyAblumActivity extends BaseActivity {
    public static Song toAddSong = null;
    private MyAblumAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutInt() {
        return R.layout.activity_myalbum_andnew;
    }

    @Override
    protected void initView() {
        super.initView();
        TextView tv_ablum_name = (TextView) findViewById(R.id.tv_ablum_name);
        if (tv_ablum_name != null) {
            tv_ablum_name.setText("我的歌单");
        }

        findViewById(R.id.view_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        findViewById(R.id.view_new).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), NewAblumNameActivity.class));
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        toAddSong = null;
    }

    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                final BmobQuery<MyAblum> sss = new BmobQuery<>();
                sss.findObjects(new FindListener<MyAblum>() {
                    @Override
                    public void done(List<MyAblum> list, BmobException e) {
                        getAdapter().setNewData(list);
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
            adapter = new MyAblumAdapter(this);
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

    private void addHeader() {
        View vvv = View.inflate(this, R.layout.rrr_title, null);
        adapter.addHeaderView(vvv);
    }
}


