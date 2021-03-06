package music.lisong.com.lisongmusic.adapter;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import music.lisong.com.lisongmusic.Ap;
import music.lisong.com.lisongmusic.activity.PlayingActivity;
import music.lisong.com.lisongmusic.bean.Likong;
import music.lisong.com.lisongmusic.bean.Song;
import music.lisong.com.lisongmusic.listener.BaseAdapterOnItemClickListener;
import music.lisong.com.lisongmusic.storyaudioservice.MusicServiceUtil;
import music.lisong.com.lisongmusic.storyaudioservice.PlayingControlHelper;
import music.lisong.com.lisongmusic.utils.LogUtil;

public class MyLikeAdapter extends BaseQuickAdapter<Likong, BaseViewHolder> {

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


            ArrayList<Song> likongs = new ArrayList<>();
            for (int m = 0; m < getData().size(); m++) {
                Likong ll = getData().get(m);
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
        public void onItemLongClick(BaseQuickAdapter adapter, View view, final int position) {
            super.onItemLongClick(adapter, view, position);

            final AlertDialog.Builder normalDialog =
                    new AlertDialog.Builder(context);
            normalDialog.setTitle("我喜欢的");
            normalDialog.setMessage("确定要删除该歌曲吗?");
            normalDialog.setPositiveButton("确定",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialog, int which) {

                            Likong todelete = getData().get(position);
                            if (todelete != null) {
                                LogUtil.e(todelete.getObjectId() + "   todelete");
                                todelete.delete(new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {
                                        if (e == null) {
                                            //...To-do
                                            getData().remove(position);
                                            notifyItemRemoved(position);
                                            if (dialog != null) {
                                                dialog.dismiss();
                                            }
                                        } else {

                                        }
                                    }
                                });
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


        @Override
        public void onItemChildClick(BaseQuickAdapter adapter, View v, int position) {
        }
    };


    public MyLikeAdapter(Context ct) {
        super(music.lisong.com.lisongmusic.R.layout.rrr_main_page, null);
        this.context = ct;
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, Likong storyBean) {

        TextView tvname = baseViewHolder.getView(music.lisong.com.lisongmusic.R.id.tv_name);
        TextView tvcount = baseViewHolder.getView(music.lisong.com.lisongmusic.R.id.tv_count);
        SimpleDraweeView sdv = baseViewHolder.getView(music.lisong.com.lisongmusic.R.id.sdv_img);

        if (storyBean == null) {
            return;
        }

        tvname.setText(storyBean.getName());
        tvcount.setText(storyBean.getAuthor());

        if (TextUtils.isEmpty(storyBean.getCoverImg())) {
            sdv.setImageURI(Uri.parse("http://n.sinaimg.cn/ent/transform/20170920/M3G7-fykymue7408829.jpg"));
        } else {
            sdv.setImageURI(storyBean.getCoverImg());
        }
    }

}
