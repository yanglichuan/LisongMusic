package music.lisong.com.lisongmusic.adapter;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;

import music.lisong.com.lisongmusic.activity.HotAblumContentActivity;
import music.lisong.com.lisongmusic.bean.Ablum;
import music.lisong.com.lisongmusic.bean.HotAblum;
import music.lisong.com.lisongmusic.listener.BaseAdapterOnItemClickListener;

public class SingerAblumAdapter extends BaseQuickAdapter<Ablum, BaseViewHolder> {

    private Context context;
    public BaseAdapterOnItemClickListener innerItemListner = new BaseAdapterOnItemClickListener() {
        @Override
        public void onSimpleItemClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
            ObjectAnimator.ofFloat(view, "scaleY", 1, 1.05f, 1).setDuration(300).start();
            ObjectAnimator.ofFloat(view, "scaleX", 1, 1.05f, 1).setDuration(300).start();

            HotAblum o = (HotAblum) view.getTag();


            Intent it = new Intent(context, HotAblumContentActivity.class);
            HotAblumContentActivity.staticAB = o;
            context.startActivity(it);

        }


        @Override
        public void onItemChildClick(BaseQuickAdapter adapter, View v, int position) {
        }
    };


    public SingerAblumAdapter(Context ct) {
        super(music.lisong.com.lisongmusic.R.layout.rrr_main_page, null);
        this.context = ct;
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, Ablum storyBean) {

        TextView tvname = baseViewHolder.getView(music.lisong.com.lisongmusic.R.id.tv_name);
        TextView tvcount = baseViewHolder.getView(music.lisong.com.lisongmusic.R.id.tv_count);
        SimpleDraweeView sdv = baseViewHolder.getView(music.lisong.com.lisongmusic.R.id.sdv_img);


        if (storyBean == null) {
            return;
        }

        tvname.setText(storyBean.getName());
        tvcount.setText(storyBean.getCount() + "首歌曲");

        sdv.setImageURI(Uri.parse(storyBean.getCoverImg()));

        baseViewHolder.getConvertView().setTag(storyBean);


    }

}
