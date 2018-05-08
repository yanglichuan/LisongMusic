package music.lisong.com.lisongmusic.adapter;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import music.lisong.com.lisongmusic.activity.MyAblumActivity;
import music.lisong.com.lisongmusic.activity.MyAblumContentActivity;
import music.lisong.com.lisongmusic.bean.Likong;
import music.lisong.com.lisongmusic.bean.MyAblum;
import music.lisong.com.lisongmusic.bean.MyAblumIncludeSong;
import music.lisong.com.lisongmusic.listener.BaseAdapterOnItemClickListener;
import music.lisong.com.lisongmusic.utils.LogUtil;
import music.lisong.com.lisongmusic.utils.ToastUtil;

public class MyAblumAdapter extends BaseQuickAdapter<MyAblum, BaseViewHolder> {

    private Context context;
    public BaseAdapterOnItemClickListener innerItemListner = new BaseAdapterOnItemClickListener() {

        @Override
        public void onItemLongClick(BaseQuickAdapter adapter, View view, final int position) {
            super.onItemLongClick(adapter, view, position);

            final AlertDialog.Builder normalDialog =
                    new AlertDialog.Builder(context);
            normalDialog.setTitle("我的歌单");
            normalDialog.setMessage("确定要删除该歌单吗?");
            normalDialog.setPositiveButton("确定",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialog, int which) {

                            MyAblum todelete = getData().get(position);
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
                                            ToastUtil.showMessage("删除失败");
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
        public void onSimpleItemClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
            ObjectAnimator.ofFloat(view, "scaleY", 1, 1.05f, 1).setDuration(300).start();
            ObjectAnimator.ofFloat(view, "scaleX", 1, 1.05f, 1).setDuration(300).start();

            MyAblum myAblum = (MyAblum) view.getTag();

            if (MyAblumActivity.toAddSong != null) {

                final MyAblumIncludeSong myAblumIncludeSong = new MyAblumIncludeSong(MyAblumActivity.toAddSong);
                myAblumIncludeSong.setBelongAblum(myAblum.getName());

                BmobQuery<MyAblumIncludeSong> songBmobQuery = new BmobQuery<>();
                songBmobQuery.addWhereEqualTo("belongAblum", myAblum.getName());
                songBmobQuery.addWhereEqualTo("mp3url", MyAblumActivity.toAddSong.getMp3url());
                songBmobQuery.findObjects(new FindListener<MyAblumIncludeSong>() {
                    @Override
                    public void done(List<MyAblumIncludeSong> list, BmobException e) {
                        if (list != null && list.size() > 0) {
                            ToastUtil.showMessage("已经存在");
                        } else {
                            myAblumIncludeSong.save(new SaveListener<String>() {
                                @Override
                                public void done(String s, BmobException e) {
                                    if (e == null) {
                                        ToastUtil.showMessage("添加成功");
                                        MyAblumActivity.toAddSong = null;
                                    }
                                }
                            });
                        }
                    }
                });
            } else {
                Intent it = new Intent(context, MyAblumContentActivity.class);
                it.putExtra("data", myAblum);
                context.startActivity(it);
            }
        }

        @Override
        public void onItemChildClick(BaseQuickAdapter adapter, View v, int position) {
        }
    };


    public MyAblumAdapter(Context ct) {
        super(music.lisong.com.lisongmusic.R.layout.rrr_main_page, null);
        this.context = ct;
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, MyAblum ha) {
        TextView tvname = baseViewHolder.getView(music.lisong.com.lisongmusic.R.id.tv_name);
        TextView tvcount = baseViewHolder.getView(music.lisong.com.lisongmusic.R.id.tv_count);
        SimpleDraweeView sdv = baseViewHolder.getView(music.lisong.com.lisongmusic.R.id.sdv_img);


        if (ha == null) {
            return;
        }

        tvname.setText(ha.getName());
        tvcount.setText(ha.getAuthor());
        if (TextUtils.isEmpty(ha.getCoverImg())) {
            sdv.setImageURI(Uri.parse("http://n.sinaimg.cn/ent/transform/20170920/M3G7-fykymue7408829.jpg"));
        } else {
            sdv.setImageURI(Uri.parse(ha.getCoverImg()));
        }


        baseViewHolder.getConvertView().setTag(ha);
    }

}
