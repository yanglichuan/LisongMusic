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

import java.util.ArrayList;

import music.lisong.com.lisongmusic.Ap;
import music.lisong.com.lisongmusic.activity.PlayingActivity;
import music.lisong.com.lisongmusic.bean.Ablum;
import music.lisong.com.lisongmusic.bean.MyAblumIncludeSong;
import music.lisong.com.lisongmusic.bean.Song;
import music.lisong.com.lisongmusic.listener.BaseAdapterOnItemClickListener;
import music.lisong.com.lisongmusic.storyaudioservice.MusicServiceUtil;
import music.lisong.com.lisongmusic.storyaudioservice.PlayingControlHelper;

public class MyAblumContentAdapter extends BaseQuickAdapter<MyAblumIncludeSong, BaseViewHolder> {


    private Context context;
    public BaseAdapterOnItemClickListener innerItemListner = new BaseAdapterOnItemClickListener() {
        @Override
        public void onSimpleItemClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
            ObjectAnimator.ofFloat(view, "scaleY", 1, 1.05f, 1).setDuration(300).start();
            ObjectAnimator.ofFloat(view, "scaleX", 1, 1.05f, 1).setDuration(300).start();

            ArrayList<Song> likongs = new ArrayList<>();
            for (int m = 0; m < getData().size(); m++) {
                MyAblumIncludeSong ll = getData().get(m);
                likongs.add(ll.toSong());
            }

            if (likongs.size() > 0) {
                //开始播放歌曲
                PlayingControlHelper.setPlayList(likongs, i);
                MusicServiceUtil.play(Ap.application,
                        PlayingControlHelper.getPlayingStory().getMp3url(), 1);

                context.startActivity(new Intent(context, PlayingActivity.class));
            }
        }


        @Override
        public void onItemChildClick(BaseQuickAdapter adapter, View v, int position) {
        }
    };
    private Ablum ablum;

    public MyAblumContentAdapter(Context ct) {
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
    protected void convert(BaseViewHolder baseViewHolder, MyAblumIncludeSong storyBean) {

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
//        tvcount.setText("收录于" + storyBean.getBelongAblum());
        tvcount.setText("作者："+storyBean.getAuthor());

        if (ablum != null && !TextUtils.isEmpty(ablum.getCoverImg())) {
            sdv.setImageURI(ablum.getCoverImg());
        } else {
            if (TextUtils.isEmpty(storyBean.getCoverImg())) {
                sdv.setImageURI(Uri.parse("http://n.sinaimg.cn/ent/transform/20170920/M3G7-fykymue7408829.jpg"));
            } else {
                sdv.setImageURI(storyBean.getCoverImg());
            }
        }
        baseViewHolder.getConvertView().setTag(storyBean);

    }

}
