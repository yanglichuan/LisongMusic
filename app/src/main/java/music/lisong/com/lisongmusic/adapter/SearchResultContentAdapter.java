package music.lisong.com.lisongmusic.adapter;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;

import music.lisong.com.lisongmusic.Ap;
import music.lisong.com.lisongmusic.activity.PlayingActivity;
import music.lisong.com.lisongmusic.bean.Ablum;
import music.lisong.com.lisongmusic.bean.Song;
import music.lisong.com.lisongmusic.listener.BaseAdapterOnItemClickListener;
import music.lisong.com.lisongmusic.storyaudioservice.MusicServiceUtil;
import music.lisong.com.lisongmusic.storyaudioservice.PlayingControlHelper;

public class SearchResultContentAdapter extends BaseQuickAdapter<Song, BaseViewHolder> {

    private Context context;
    public BaseAdapterOnItemClickListener innerItemListner = new BaseAdapterOnItemClickListener() {
        @Override
        public void onSimpleItemClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
            ObjectAnimator.ofFloat(view, "scaleY", 1, 1.05f, 1).setDuration(300).start();
            ObjectAnimator.ofFloat(view, "scaleX", 1, 1.05f, 1).setDuration(300).start();

            Song song = (Song) view.getTag();

            //开始播放歌曲
            PlayingControlHelper.setPlayList(getData(), i);
            MusicServiceUtil.play(Ap.application,
                    PlayingControlHelper.getPlayingStory().getMp3url(), 1);
            if (song != null) {
                context.startActivity(new Intent(context, PlayingActivity.class));
            }
        }


        @Override
        public void onItemChildClick(BaseQuickAdapter adapter, View v, int position) {
        }
    };
    private Ablum ablum;

    public SearchResultContentAdapter(Context ct) {
        super(music.lisong.com.lisongmusic.R.layout.rrr_main_page, null);
        this.context = ct;
    }

    public Ablum getAblum() {
        return ablum;
    }

    public void setAblum(Ablum ablum) {
        this.ablum = ablum;
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, Song storyBean) {

        TextView tvname = baseViewHolder.getView(music.lisong.com.lisongmusic.R.id.tv_name);
        TextView tvcount = baseViewHolder.getView(music.lisong.com.lisongmusic.R.id.tv_count);
        SimpleDraweeView sdv = baseViewHolder.getView(music.lisong.com.lisongmusic.R.id.sdv_img);

        if (storyBean == null) {
            return;
        }

        if (ablum != null && TextUtils.isEmpty(storyBean.getCoverImg())) {
            storyBean.setCoverImg(ablum.getCoverImg());
        }
        tvname.setText(storyBean.getName());
        tvcount.setText("收录于" + storyBean.getBelongAblum());

        if (ablum != null) {
            sdv.setImageURI(ablum.getCoverImg());
        } else {
            if (!TextUtils.isEmpty(storyBean.getCoverImg())) {
                sdv.setImageURI(storyBean.getCoverImg());
            } else {
                sdv.setImageURI(Uri.parse("http://n.sinaimg.cn/ent/transform/20170920/M3G7-fykymue7408829.jpg"));
            }
        }
        baseViewHolder.getConvertView().setTag(storyBean);
    }
}
