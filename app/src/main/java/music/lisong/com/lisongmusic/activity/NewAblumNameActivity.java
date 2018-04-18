package music.lisong.com.lisongmusic.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import music.lisong.com.lisongmusic.R;
import music.lisong.com.lisongmusic.adapter.SearchStringAdapter;
import music.lisong.com.lisongmusic.bean.MyAblum;
import music.lisong.com.lisongmusic.utils.ToastUtil;

public class NewAblumNameActivity extends BaseActivity {

    public static final String KY = "searchwords";
    ArrayList<String> keywords = new ArrayList<>();
    TwinklingRefreshLayout refreshLayout;
    RecyclerView recyclerView;
    private EditText et_key;
    private Button bt_search;
    private SearchStringAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newablumname);


        findViewById(R.id.view_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        final EditText editText = (EditText) findViewById(R.id.et_key);

        findViewById(R.id.bt_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String str = editText.getText().toString();
                if (TextUtils.isEmpty(str)) {
                    ToastUtil.showMessage("名称不能为空");
                    return;
                }


                BmobQuery<MyAblum> songBmobQuery = new BmobQuery<>();
                songBmobQuery.addWhereEqualTo("name", str);
                songBmobQuery.findObjects(new FindListener<MyAblum>() {
                    @Override
                    public void done(List<MyAblum> list, BmobException e) {
                        if (list != null && list.size() > 0) {
                            ToastUtil.showMessage("名称已经存在");
                        } else {
                            MyAblum a = new MyAblum(str, 0);
                            a.save(new SaveListener<String>() {
                                @Override
                                public void done(String s, BmobException e) {
                                    if (e == null) {
                                        ToastUtil.showMessage("新建<" + str + ">成功");
                                        finish();
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
    }


//    public void onRefresh() {
//        ArrayList<Song> ssss = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            ssss.add(new Song("ddd",i));
//        }
//        getAdapter().setNewData(ssss);
//    }
}


