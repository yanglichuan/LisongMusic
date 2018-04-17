package music.lisong.com.lisongmusic.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.nex3z.flowlayout.FlowLayout;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobBatch;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BatchResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListListener;
import cn.bmob.v3.listener.SaveListener;
import music.lisong.com.lisongmusic.R;
import music.lisong.com.lisongmusic.adapter.SearchStringAdapter;
import music.lisong.com.lisongmusic.bean.KeyWord;
import music.lisong.com.lisongmusic.utils.LogUtil;

public class SearchActivity extends AppCompatActivity {

    public static final String KY = "searchwords";
    List<KeyWord> keywords = new ArrayList<>();
    TwinklingRefreshLayout refreshLayout;
    RecyclerView recyclerView;
    private EditText et_key;
    private Button bt_search;
    private SearchStringAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(music.lisong.com.lisongmusic.R.layout.activity_search);


        keywords.clear();

        freshkeywd();


        et_key = (EditText) findViewById(music.lisong.com.lisongmusic.R.id.et_key);
        bt_search = (Button) findViewById(music.lisong.com.lisongmusic.R.id.bt_search);

        if (bt_search != null) {
            bt_search.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String key = et_key.getText().toString();
                    if (TextUtils.isEmpty(key)) {
                        key = "周杰伦";
                    }

                    KeyWord kw = new KeyWord(key);
                    if (!keywords.contains(kw)) {

                        kw.save(new SaveListener<String>() {
                            @Override
                            public void done(String s, BmobException e) {
                                if (e == null) {
                                    freshkeywd();
                                }
                            }
                        });
                    } else {
                    }


                    final String finalKey = key;
                    Intent it = new Intent(SearchActivity.this, SearchResultContentActivity.class);
                    it.putExtra("data", finalKey);
                    startActivity(it);


                }
            });
        }


        findViewById(R.id.iv_clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                final AlertDialog.Builder normalDialog =
                        new AlertDialog.Builder(SearchActivity.this);
                normalDialog.setTitle("历史记录");
                normalDialog.setMessage("确定要删除所有历史记录吗?");
                normalDialog.setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog, int which) {

                                List<BmobObject> ddd = new ArrayList<>();
                                ddd.addAll(keywords);

                                new BmobBatch().deleteBatch(ddd).doBatch(new QueryListListener<BatchResult>() {

                                    @Override
                                    public void done(List<BatchResult> o, BmobException e) {
                                        if (e == null) {
                                            for (int i = 0; i < o.size(); i++) {
                                                BatchResult result = o.get(i);
                                                BmobException ex = result.getError();
                                                if (ex == null) {
                                                    LogUtil.e("第" + i + "个数据批量删除成功");
                                                } else {
                                                    LogUtil.e("第" + i + "个数据批量删除失败：" + ex.getMessage() + "," + ex.getErrorCode());
                                                }
                                            }
                                        } else {
                                            LogUtil.e("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                                        }
                                    }
                                });


                                if (dialog != null) {
                                    dialog.dismiss();
                                }
                            }
                        });
                normalDialog.setNegativeButton("关闭",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //...To-do
                                if (dialog != null) {
                                    dialog.dismiss();
                                }
                            }
                        });
                // 显示
                normalDialog.show();


            }
        });
    }

    public void freshkeywd() {
        BmobQuery<KeyWord> keyWordBmobQuery = new BmobQuery<>();
        keyWordBmobQuery.findObjects(new FindListener<KeyWord>() {
            @Override
            public void done(List<KeyWord> list, BmobException e) {
                if (list != null && list.size() > 0) {
                    keywords = list;
                    fillAutoSpacingLayout(keywords);
                }
            }
        });
    }

    private void fillAutoSpacingLayout(List<KeyWord> list) {
        FlowLayout flowLayout = (FlowLayout) findViewById(R.id.flow);
        flowLayout.removeAllViews();
        for (KeyWord text : list) {
            TextView textView = buildLabel(text.getKyword());
            flowLayout.addView(textView);
        }
    }

    private TextView buildLabel(final String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        textView.setPadding((int) dpToPx(14), (int) dpToPx(7), (int) dpToPx(14), (int) dpToPx(7));
        textView.setBackgroundResource(R.drawable.label_bg);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String finalKey = text;
                Intent it = new Intent(SearchActivity.this, SearchResultContentActivity.class);
                it.putExtra("data", finalKey);
                startActivity(it);
            }
        });

        return textView;
    }

    private float dpToPx(float dp) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }
//
//    // 方法三：
//    public String listToString3(List list, String separator) {
//        StringBuilder sb = new StringBuilder();
//        for (int i = 0; i < list.size(); i++) {
//            sb.append(list.get(i));
//            if (i < list.size() - 1) {
//                sb.append(separator);
//            }
//        }
//        return sb.toString();
//    }
//
//    public void onRefresh() {
//        getAdapter().setNewData(keywords);
//    }
//
//    private BaseQuickAdapter getAdapter() {
//        if (adapter == null) {
//            adapter = new SearchStringAdapter();
////            adapter.setOnLoadMoreListener(this);
//            //杨立川
//            recyclerView.addOnItemTouchListener(adapter.innerItemListner);
//        }
//        return adapter;
//    }
//    public void onRefresh() {
//        ArrayList<Song> ssss = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            ssss.add(new Song("ddd",i));
//        }
//        getAdapter().setNewData(ssss);
//    }
}


