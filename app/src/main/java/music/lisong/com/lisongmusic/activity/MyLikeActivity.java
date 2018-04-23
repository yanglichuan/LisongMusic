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
import music.lisong.com.lisongmusic.adapter.MyLikeAdapter;
import music.lisong.com.lisongmusic.bean.Likong;
import music.lisong.com.lisongmusic.view.TwinkingFreshLayout;

public class MyLikeActivity extends BaseActivity {

    private MyLikeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutInt() {
        return R.layout.ablum_mylike;
    }

    @Override
    protected void initView() {
        super.initView();
        TextView et_title = (TextView) findViewById(R.id.tv_ablum_name);
        et_title.setText("我喜欢的");

        findViewById(R.id.view_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        onRefresh();
    }

    public void onRefresh() {
        BmobQuery<Likong> query = new BmobQuery<>();

        query.findObjects(new FindListener<Likong>() {
            @Override
            public void done(List<Likong> list, BmobException e) {
                getAdapter().setNewData(list);
                if (refreshLayout != null) {
                    refreshLayout.onFinishRefresh();
                }
            }
        });
    }

    protected BaseQuickAdapter getAdapter() {
        if (adapter == null) {
            adapter = new MyLikeAdapter(this);
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


