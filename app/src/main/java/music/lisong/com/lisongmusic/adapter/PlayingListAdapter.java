package music.lisong.com.lisongmusic.adapter;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;

import music.lisong.com.lisongmusic.bean.Song;
import music.lisong.com.lisongmusic.listener.BaseAdapterOnItemClickListener;
import music.lisong.com.lisongmusic.storyaudioservice.MusicServiceUtil;
import music.lisong.com.lisongmusic.storyaudioservice.PlayingControlHelper;

public class PlayingListAdapter extends BaseQuickAdapter<Song, BaseViewHolder> {

    public BaseAdapterOnItemClickListener innerItemListner = new BaseAdapterOnItemClickListener() {
        @Override
        public void onSimpleItemClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {

        }


        @Override
        public void onItemChildClick(BaseQuickAdapter adapter, View v, int position) {
        }
    };

    private Context context;


    public PlayingListAdapter(Context ct) {
        super(music.lisong.com.lisongmusic.R.layout.rrr_main_page, null);
        this.context = ct;
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, final Song storyBean) {

        TextView tvname = baseViewHolder.getView(music.lisong.com.lisongmusic.R.id.tv_name);
        TextView tvcount = baseViewHolder.getView(music.lisong.com.lisongmusic.R.id.tv_count);
        SimpleDraweeView sdv = baseViewHolder.getView(music.lisong.com.lisongmusic.R.id.sdv_img);


        if (storyBean == null) {
            return;
        }

        tvname.setText(storyBean.getName());
        tvcount.setText(storyBean.getAuthor());

        if (!TextUtils.isEmpty(storyBean.getCoverImg())) {
            sdv.setImageURI(storyBean.getCoverImg());
        } else {
            sdv.setImageURI(Uri.parse("http://n.sinaimg.cn/ent/transform/20170920/M3G7-fykymue7408829.jpg"));
        }


        baseViewHolder.getConvertView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ObjectAnimator.ofFloat(view, "scaleY", 1, 1.05f, 1).setDuration(300).start();
                ObjectAnimator.ofFloat(view, "scaleX", 1, 1.05f, 1).setDuration(300).start();


                PlayingControlHelper.playIndex(getData().indexOf(storyBean));
                MusicServiceUtil.play(context, PlayingControlHelper.getPlayingStory().getMp3url(), PlayingControlHelper.getPlayingStory().getId());
            }
        });

    }

}
