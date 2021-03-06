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
import music.lisong.com.lisongmusic.adapter.HotAuthorAdapter;
import music.lisong.com.lisongmusic.bean.Author;
import music.lisong.com.lisongmusic.view.TwinkingFreshLayout;

public class HotAuthorActivity extends BaseActivity {
    private HotAuthorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutInt() {
        return R.layout.activity_hotablum;
    }

    @Override
    protected void initView() {
        super.initView();
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
            tv_ablum_name.setText("歌手");
        }


        onRefresh();
    }

    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                final BmobQuery<Author> sss = new BmobQuery<>();
                sss.findObjects(new FindListener<Author>() {
                    @Override
                    public void done(List<Author> list, BmobException e) {
                        if (list != null && list.size() > 0) {
                            getAdapter().setNewData(list);
                        }
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
            adapter = new HotAuthorAdapter(this);
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


