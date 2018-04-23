package music.lisong.com.lisongmusic.activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import music.lisong.com.lisongmusic.R;
import music.lisong.com.lisongmusic.adapter.SingerAblumAdapter;
import music.lisong.com.lisongmusic.bean.Ablum;

public class SingerAblumActivity extends BaseActivity {

    List<Ablum> ablums = new ArrayList<>();
    private SingerAblumAdapter adapter;

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
        ablums.clear();

        TextView tv_ablum_name = (TextView) findViewById(R.id.tv_ablum_name);
        if (tv_ablum_name != null) {
            tv_ablum_name.setText("专辑");
        }

        findViewById(R.id.view_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                final BmobQuery<Ablum> sss = new BmobQuery<>();
                sss.findObjects(new FindListener<Ablum>() {
                    @Override
                    public void done(List<Ablum> list, BmobException e) {
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

    protected BaseQuickAdapter getAdapter() {
        if (adapter == null) {
            adapter = new SingerAblumAdapter(this);
//            adapter.setOnLoadMoreListener(this);
            //杨立川
            recyclerView.addOnItemTouchListener(adapter.innerItemListner);
        }
        return adapter;
    }

    @Override
    protected void onResume() {
        super.onResume();
        onRefresh();
    }
}


