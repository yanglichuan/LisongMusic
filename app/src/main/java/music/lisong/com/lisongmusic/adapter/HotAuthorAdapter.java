package music.lisong.com.lisongmusic.adapter;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;

import music.lisong.com.lisongmusic.R;
import music.lisong.com.lisongmusic.activity.AblumContentActivity;
import music.lisong.com.lisongmusic.bean.Author;
import music.lisong.com.lisongmusic.listener.BaseAdapterOnItemClickListener;

public class HotAuthorAdapter extends BaseQuickAdapter<Author, BaseViewHolder> {

    //判断是否是专辑
    private Button buyBtn;
    // 专辑图标显示器
    private SimpleDraweeView tv_ablum_flag;
    // 专辑或者故事图标
    private SimpleDraweeView seed_icon;
    // 专辑或者故事名字
    private TextView storyName;
    private TextView tvShoulu;
    // =========================================
    // 专辑或者故事播放时长
    private TextView tv_show_time;
    private View relativeLayout_show_count;
    private TextView tv_show_count;
    // 添加某专辑
    private ImageView iv_addtoAblum;

    private Context context;
    public BaseAdapterOnItemClickListener innerItemListner = new BaseAdapterOnItemClickListener() {
        @Override
        public void onSimpleItemClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
            ObjectAnimator.ofFloat(view, "scaleY", 1, 1.05f, 1).setDuration(300).start();
            ObjectAnimator.ofFloat(view, "scaleX", 1, 1.05f, 1).setDuration(300).start();

            Author author = (Author) view.getTag();
            Intent it = new Intent(context, AblumContentActivity.class);
            it.putExtra("author", author);
            context.startActivity(it);
        }

        @Override
        public void onItemChildClick(BaseQuickAdapter adapter, View v, int position) {
        }
    };


    public HotAuthorAdapter(Context ct) {
        super(R.layout.rrr_hotauthor_page, null);
        this.context = ct;
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, Author authr) {


        baseViewHolder.getConvertView().setTag(authr);
        TextView tvname = baseViewHolder.getView(music.lisong.com.lisongmusic.R.id.tv_name);
        TextView tvcount = baseViewHolder.getView(music.lisong.com.lisongmusic.R.id.tv_count);
        SimpleDraweeView sdv = baseViewHolder.getView(music.lisong.com.lisongmusic.R.id.sdv_img);


        if (authr == null) {
            return;
        }

        tvname.setText(authr.getName());
        tvcount.setText(authr.getSex());

        if (TextUtils.isEmpty(authr.getHeadicon())) {
            sdv.setImageURI(Uri.parse("http://n.sinaimg.cn/ent/transform/20170920/M3G7-fykymue7408829.jpg"));
        } else {
            sdv.setImageURI(authr.getHeadicon());
        }

    }

}
