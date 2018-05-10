package music.lisong.com.lisongmusic.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.facebook.imagepipeline.request.Postprocessor;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import jp.wasabeef.fresco.processors.BlurPostprocessor;
import music.lisong.com.lisongmusic.Ap;
import music.lisong.com.lisongmusic.R;
import music.lisong.com.lisongmusic.adapter.PlayingListAdapter;
import music.lisong.com.lisongmusic.bean.Likong;
import music.lisong.com.lisongmusic.bean.Song;
import music.lisong.com.lisongmusic.bean.TimeItem;
import music.lisong.com.lisongmusic.storyaudioservice.Callback;
import music.lisong.com.lisongmusic.storyaudioservice.MusicServiceUtil;
import music.lisong.com.lisongmusic.storyaudioservice.PlayingControlHelper;
import music.lisong.com.lisongmusic.utils.CommonUtils;
import music.lisong.com.lisongmusic.utils.SpUtils;
import music.lisong.com.lisongmusic.utils.ToastUtil;
import music.lisong.com.lisongmusic.view.MySeekbar;

public class PlayingActivity extends BaseActivity implements Callback, TickUtils.TimeTickCallBack {

    MySeekbar seekbar;

    TextView tv_currenttime;
    TextView tv_endtime;

    SimpleDraweeView sdvPlayControl;
    SimpleDraweeView sdvPlayNext;
    SimpleDraweeView sdvPlayPre;

    SimpleDraweeView view_blur;

    boolean bSeekBarTouching;
    SimpleDraweeView song_img;

    TextView tv_songname;
    TextView tv_songauthor;

    //
    private PlayingListAdapter adapter;

    private ImageView view_tolike;
    private ImageView view_addablum;
    private ImageView view_clock;
    private ImageView view_list;
    private boolean bLiked = false;
    private View.OnClickListener inn = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.view_tolike:

                    Song s = PlayingControlHelper.getPlayingStory();
                    if (s != null) {
                        if (!bLiked) {
                            final Likong likong = new Likong(s);

                            likong.save(new SaveListener<String>() {
                                @Override
                                public void done(String s, BmobException e) {
                                    if (e == null) {
                                        ToastUtil.showMessage("已添加到我喜欢的");
                                        view_tolike.setImageResource(R.drawable.lisong_like);
                                        bLiked = true;

                                        Ap.queryLike();
                                    } else {
                                        ToastUtil.showMessage("收藏失败");
                                    }
                                }
                            });
                        } else {
                            List<Likong> likongs = Ap.likongList;
                            for (int i = 0; i < likongs.size(); i++) {
                                if (likongs.get(i).getMp3url().equals(s.getMp3url())) {
                                    likongs.get(i).delete(new UpdateListener() {
                                        @Override
                                        public void done(BmobException e) {
                                            if (e == null) {
                                                bLiked = false;
                                                ToastUtil.showMessage("取消收藏成功");
                                                view_tolike.setImageResource(R.drawable.lisong_tolike);
                                            } else {
                                                ToastUtil.showMessage("取消收藏失败");
                                            }
                                        }
                                    });
                                    break;
                                }
                            }
                        }
                    }

                    break;
                case R.id.view_addablum:
                    addtoAb();
                    break;
                case R.id.view_clock:
                    showtime();
                    break;
                case R.id.view_list:

                    showList();

                    break;


            }
        }
    };

    private void addtoAb() {
        Song s = PlayingControlHelper.getPlayingStory();
        if (s != null) {
            MyAblumActivity.toAddSong = s;
            startActivity(new Intent(getApplicationContext(), MyAblumActivity.class));
        }
    }

    private void showList() {
        ViewHolder holder = new ViewHolder(R.layout.playing_list);
        final DialogPlus dialog = DialogPlus.newDialog(PlayingActivity.this)
                .setContentHolder(holder)
                .setContentWidth(ViewGroup.LayoutParams.MATCH_PARENT)  // or any custom width ie: 300
                .setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setGravity(Gravity.BOTTOM)
                .setOnDismissListener(null)
                .setOnCancelListener(null)
                .setExpanded(false)
                .setCancelable(true)
                .create();

        RecyclerView recyclerView = (RecyclerView) dialog.findViewById(music.lisong.com.lisongmusic.R.id.recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        // 设置item动画
        recyclerView.setAdapter(getAdapter(recyclerView));

        getAdapter(recyclerView).setNewData(PlayingControlHelper.getPlayList());

        dialog.findViewById(R.id.view_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        view_playmode = dialog.findViewById(R.id.view_playmode);
        iv_playmode = (ImageView) dialog.findViewById(R.id.iv_playmode);
        tv_playmode = (TextView) dialog.findViewById(R.id.tv_playmode);

        String  mode = SpUtils.get(PLAYMODE);
        if(!TextUtils.isEmpty(mode)){
            int imode = Integer.parseInt(mode);
            setPlayMode(imode);
        }else {
            setPlayMode(MODE_CIRCLE);
        }

        iv_playmode.setClickable(false);
        tv_playmode.setClickable(false);
        view_playmode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String  mode = SpUtils.get(PLAYMODE);
                if(!TextUtils.isEmpty(mode)){
                    int imode = Integer.parseInt(mode);
                    if(imode == MODE_CIRCLE){
                        setPlayMode(MODE_REPEATONE);
                        SpUtils.putString(PLAYMODE, String.valueOf(MODE_REPEATONE));
                    }
                    if(imode == MODE_REPEATONE){
                        setPlayMode(MODE_RANDOM);
                        SpUtils.putString(PLAYMODE, String.valueOf(MODE_RANDOM));
                    }
                    if(imode == MODE_RANDOM){
                        setPlayMode(MODE_CIRCLE);
                        SpUtils.putString(PLAYMODE, String.valueOf(MODE_CIRCLE));
                    }
                }else {
                    setPlayMode(MODE_RANDOM);
                    SpUtils.putString(PLAYMODE, String.valueOf(MODE_RANDOM));
                }
            }
        });
        dialog.show();
    }

    private BaseQuickAdapter getAdapter(RecyclerView recyclerView) {
        if (adapter == null) {
            adapter = new PlayingListAdapter(this);
//            adapter.setOnLoadMoreListener(this);
            //杨立川
            recyclerView.addOnItemTouchListener(adapter.innerItemListner);
        }
        return adapter;
    }

    private void showtime() {
        ViewHolder holder = new ViewHolder(R.layout.playing_timeitem);
        final DialogPlus dialog = DialogPlus.newDialog(PlayingActivity.this)
                .setContentHolder(holder)
                .setContentWidth(ViewGroup.LayoutParams.MATCH_PARENT)  // or any custom width ie: 300
                .setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setGravity(Gravity.BOTTOM)
                .setOnDismissListener(null)
                .setOnCancelListener(null)
                .setExpanded(false)
                .setCancelable(true)
                .create();

        dialog.findViewById(R.id.view_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });


        final ArrayList<TimeItem> timeItems = new ArrayList<>();
        TextView tv1 = (TextView) dialog.findViewById(R.id.tv1);
        TextView tv2 = (TextView) dialog.findViewById(R.id.tv2);
        TextView tv3 = (TextView) dialog.findViewById(R.id.tv3);
        TextView tv4 = (TextView) dialog.findViewById(R.id.tv4);
        TextView tv5 = (TextView) dialog.findViewById(R.id.tv5);
        ImageView iv1 = (ImageView) dialog.findViewById(R.id.iv1);
        ImageView iv2 = (ImageView) dialog.findViewById(R.id.iv2);
        ImageView iv3 = (ImageView) dialog.findViewById(R.id.iv3);
        ImageView iv4 = (ImageView) dialog.findViewById(R.id.iv4);
        ImageView iv5 = (ImageView) dialog.findViewById(R.id.iv5);

        final TimeItem itm1 = new TimeItem(tv1, iv1);
        final TimeItem itm2 = new TimeItem(tv2, iv2);
        final TimeItem itm3 = new TimeItem(tv3, iv3);
        final TimeItem itm4 = new TimeItem(tv4, iv4);
        final TimeItem itm5 = new TimeItem(tv5, iv5);
        timeItems.add(itm1);
        timeItems.add(itm2);
        timeItems.add(itm3);
        timeItems.add(itm4);
        timeItems.add(itm5);


        String oldi = SpUtils.get("timechoice");
        switch (oldi) {
            case "1":
                for (TimeItem tttt : timeItems) {
                    tttt.setSelect(false);
                }
                itm1.setSelect(true);

                break;
            case "2":
                for (TimeItem tttt : timeItems) {
                    tttt.setSelect(false);
                }
                itm2.setSelect(true);

                break;
            case "3":
                for (TimeItem tttt : timeItems) {
                    tttt.setSelect(false);
                }
                itm3.setSelect(true);

                break;
            case "4":
                for (TimeItem tttt : timeItems) {
                    tttt.setSelect(false);
                }
                itm4.setSelect(true);

                break;
            case "5":
                for (TimeItem tttt : timeItems) {
                    tttt.setSelect(false);
                }
                itm5.setSelect(true);

                break;
            default:
                for (TimeItem tttt : timeItems) {
                    tttt.setSelect(false);
                }
                break;
        }
        View v1 = dialog.findViewById(R.id.view1);
        View v2 = dialog.findViewById(R.id.view2);
        View v3 = dialog.findViewById(R.id.view3);
        View v4 = dialog.findViewById(R.id.view4);
        View v5 = dialog.findViewById(R.id.view5);
        View v6 = dialog.findViewById(R.id.view6);
        View.OnClickListener xx = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.view1:
                        for (TimeItem tttt : timeItems) {
                            tttt.setSelect(false);
                        }
                        itm1.setSelect(true);
                        SpUtils.putString("timechoice", "1");
                        TickUtils.setCountdownTimer(TickUtils.CLOSE_MODE_15);
                        dialog.dismiss();
                        break;
                    case R.id.view2:
                        for (TimeItem tttt : timeItems) {
                            tttt.setSelect(false);
                        }
                        itm2.setSelect(true);
                        SpUtils.putString("timechoice", "2");
                        TickUtils.setCountdownTimer(TickUtils.CLOSE_MODE_15);
                        dialog.dismiss();
                        break;
                    case R.id.view3:
                        for (TimeItem tttt : timeItems) {
                            tttt.setSelect(false);
                        }
                        itm3.setSelect(true);
                        SpUtils.putString("timechoice", "3");
                        TickUtils.setCountdownTimer(TickUtils.CLOSE_MODE_15);
                        dialog.dismiss();
                        break;
                    case R.id.view4:
                        for (TimeItem tttt : timeItems) {
                            tttt.setSelect(false);
                        }
                        itm4.setSelect(true);
                        SpUtils.putString("timechoice", "4");
                        TickUtils.setCountdownTimer(TickUtils.CLOSE_MODE_15);
                        dialog.dismiss();
                        break;
                    case R.id.view5:
                        for (TimeItem tttt : timeItems) {
                            tttt.setSelect(false);
                        }
                        itm5.setSelect(true);
                        SpUtils.putString("timechoice", "5");
                        TickUtils.setCountdownTimer(TickUtils.CLOSE_MODE_OVER_NOW);
                        dialog.dismiss();
                        break;
                    case R.id.view6:
                        for (TimeItem tttt : timeItems) {
                            tttt.setSelect(false);
                        }
                        SpUtils.putString("timechoice", "-1");
                        TickUtils.setCountdownTimer(TickUtils.CLOSE_MODE_NO_OPEN);
                        dialog.dismiss();
                        break;
                }
            }
        };

        v1.setOnClickListener(xx);
        v2.setOnClickListener(xx);
        v3.setOnClickListener(xx);
        v4.setOnClickListener(xx);
        v5.setOnClickListener(xx);
        v6.setOnClickListener(xx);

        dialog.show();
    }


    private TextView tv_timeleft;


    @Override
    protected void onDestroy() {
        super.onDestroy();
        MusicServiceUtil.removePlayingCallBack(this);
        TickUtils.removeTickCallBack(this);

    }


    private View view_playmode;
    private ImageView iv_playmode;
    private TextView tv_playmode;



    public static final int MODE_CIRCLE =1;
    public static final int MODE_REPEATONE = 2;
    public static final int MODE_RANDOM = 3;

    private void setPlayMode(int iMode){
       switch (iMode) {
           case MODE_CIRCLE:
               iv_playmode.setImageResource(R.drawable.icon_play_mode_repeat_list);
               tv_playmode.setText("循环播放");
               break;
           case MODE_REPEATONE:
               iv_playmode.setImageResource(R.drawable.icon_play_mode_repeat_one);
               tv_playmode.setText("单曲循环");
               break;
           case MODE_RANDOM:
               iv_playmode.setImageResource(R.drawable.icon_play_mode_list_one);
               tv_playmode.setText("随机播放");
               break;
       }
    }


    public static final String PLAYMODE = "lsPlaymode";
    @Override
    protected void initView() {
        super.initView();
        MusicServiceUtil.addPlayingCallback(this);
        TickUtils.addTickCallBack(this);




        tv_timeleft = (TextView) findViewById(R.id.tv_timeleft);
        view_tolike = (ImageView) findViewById(R.id.view_tolike);
        view_addablum = (ImageView) findViewById(R.id.view_addablum);
        view_clock = (ImageView) findViewById(R.id.view_clock);
        view_list = (ImageView) findViewById(R.id.view_list);

        view_tolike.setOnClickListener(inn);
        view_addablum.setOnClickListener(inn);
        view_clock.setOnClickListener(inn);
        view_list.setOnClickListener(inn);


        song_img = (SimpleDraweeView) findViewById(R.id.song_img);
        tv_songauthor = (TextView) findViewById(R.id.tv_songauthor);
        tv_songname = (TextView) findViewById(R.id.tv_songname);

        updateSong();


        seekbar = (MySeekbar) findViewById(R.id.playing_seek_bar);
        seekbar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {
                bSeekBarTouching = true;
            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
                //防止拖动到最后
                if (seekBar.getProgress() >= seekBar.getMax()) {
                    seekBar.setProgress(99);
                }

                MusicServiceUtil.seekToInPercent(seekBar.getProgress());
                bSeekBarTouching = false;

            }
        });
        tv_currenttime = (TextView) findViewById(R.id.tv_currenttime);
        tv_endtime = (TextView) findViewById(R.id.tv_endtime);

        if (MusicServiceUtil.getCurrentPlayState() == PlaybackStateCompat.STATE_PAUSED ||
                MusicServiceUtil.getCurrentPlayState() == PlaybackStateCompat.STATE_PLAYING) {
            String endT = CommonUtils.timeExchange(MusicServiceUtil.getPlayProgress()[1]);
            String progressT = CommonUtils.timeExchange(MusicServiceUtil.getPlayProgress()[0]);
            tv_endtime.setText(endT);
            tv_currenttime.setText(progressT);
            long progress = MusicServiceUtil.getPlayProgress()[0];
            long duration = MusicServiceUtil.getPlayProgress()[1];
            if (duration == 0) {
                seekbar.setProgress(0);
            } else {
                seekbar.setProgress((int) (progress * 100 / duration));
            }
        }


        sdvPlayNext = (SimpleDraweeView) findViewById(R.id.play_next);
        sdvPlayPre = (SimpleDraweeView) findViewById(R.id.play_pre);
        sdvPlayControl = (SimpleDraweeView) findViewById(R.id.play_control);

        sdvPlayControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MusicServiceUtil.isPlaying()) {
                    MusicServiceUtil.pausePlay(Ap.application);
                    CommonUtils.bindFrescoFromResource(sdvPlayControl, R.drawable.lisong_play3);
                } else {
                    MusicServiceUtil.reStart();
                    CommonUtils.bindFrescoFromResource(sdvPlayControl, R.drawable.lisong_pause);
                }
            }
        });

        sdvPlayPre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Song s = PlayingControlHelper.presong();
                if (s != null) {
                    MusicServiceUtil.play(Ap.application, s.getMp3url(), 1);
                }
            }
        });

        sdvPlayNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Song s = PlayingControlHelper.nextSong();
                if (s != null) {
                    MusicServiceUtil.play(Ap.application, s.getMp3url(), 1);
                }
            }
        });


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutInt() {
        return R.layout.activity_playing;
    }

    @Override
    protected RecyclerView.Adapter getAdapter() {
        return null;
    }

    @Override
    protected void onResume() {
        super.onResume();

        freshOnlyControlButtonState();
    }

    private void updateSong() {
        Song s = PlayingControlHelper.getPlayingStory();

        if (s != null) {

            dealWithBlur();

            tv_songname.setText(s.getName());
            tv_songauthor.setText(s.getAuthor());


            List<Likong> likongs = Ap.likongList;
            bLiked = false;
            for (int i = 0; i < likongs.size(); i++) {
                if (likongs.get(i).getMp3url().equals(s.getMp3url())) {
                    bLiked = true;
                    break;
                }
            }
            if (bLiked) {
                view_tolike.setImageResource(R.drawable.lisong_like);
            } else {
                view_tolike.setImageResource(R.drawable.lisong_nolike);
            }
        }
    }

    /**
     * 处理高斯模糊
     */
    private void dealWithBlur() {
        view_blur = (SimpleDraweeView) findViewById(R.id.view_blur);
        Song currentPlayingStory = PlayingControlHelper.getPlayingStory();
        try {
            if (!TextUtils.isEmpty(currentPlayingStory.getCoverImg())) {
                blur(getApplicationContext(), view_blur, Uri.parse(currentPlayingStory.getCoverImg()));
                song_img.setImageURI(Uri.parse(currentPlayingStory.getCoverImg()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 高斯模糊实现
     *
     * @param bandImage
     * @param uri
     */
    public static void blur(Context ctx, SimpleDraweeView bandImage, Uri uri) {
        try {
            Postprocessor blurPostprocessor = new BlurPostprocessor(ctx, 60);
            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(
                    uri)
                    .setPostprocessor(blurPostprocessor)
                    .build();

            PipelineDraweeController controller = (PipelineDraweeController)
                    Fresco.newDraweeControllerBuilder()
                            .setImageRequest(request)
                            .setOldController(bandImage.getController())
                            .build();
            bandImage.setController(controller);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCompletion(String currentPlayUrl, int currentVoiceId, String nextPlayUrl) {

    }

    @Override
    public void onPlaybackStatusChanged(String playUrl, int voiceId, int state) {

        freshOnlyControlButtonState();

        updateSong();

    }

    /**
     * 刷新开始 暂停按钮
     */
    private void freshOnlyControlButtonState() {
        int iState = MusicServiceUtil.getCurrentPlayState();
        if (iState == PlaybackStateCompat.STATE_PLAYING) {
            CommonUtils.bindFrescoFromResource(sdvPlayControl, R.drawable.lisong_pause);
        } else if (iState == PlaybackStateCompat.STATE_PAUSED) {
            CommonUtils.bindFrescoFromResource(sdvPlayControl, R.drawable.lisong_play3);
        } else if (iState == PlaybackStateCompat.STATE_STOPPED) {
            CommonUtils.bindFrescoFromResource(sdvPlayControl, R.drawable.lisong_play3);
        } else {
            CommonUtils.bindFrescoFromResource(sdvPlayControl, R.drawable.lisong_play3);
        }
    }


    @Override
    public void onError(String playUrl, int voiceId, String error) {

    }

    @Override
    public void onProgress(String playUrl, int voiceId, long progress, long duration) {
        freshProgressAndTime(progress, duration);


        if (tv_timeleft != null && TickUtils.getCurrent_close_mode() == TickUtils.CLOSE_MODE_OVER_NOW) {
            Integer restSeconds = Long.valueOf((duration * 1000 - progress) / 1000).intValue();

            Integer[] miniteSecond = new Integer[2];
            String timeExchangeV2 = CommonUtils.timeExchangeV2(restSeconds, miniteSecond);
            tv_timeleft.setText(timeExchangeV2);
        }
    }

    @Override
    public void onPlayBegin(String playUrl, int voiceId) {

    }

    @Override
    public void onPlayOver() {

    }

    /**
     * 更新进度和时间
     *
     * @param progress
     * @param duration
     */
    private void freshProgressAndTime(long progress, long duration) {
        //addylc
        if (progress > duration) {
            progress = duration;
        }
        if (!bSeekBarTouching) {
            seekbar.setProgress((int) (((float) progress / (float) duration) * 100));
        }
        new AsyncTask<Long, Long, String[]>() {
            @Override
            protected String[] doInBackground(Long... params) {
                String progressT = CommonUtils.timeExchange(params[0]);
                String endT = CommonUtils.timeExchange(params[1]);
                return new String[]{progressT, endT};
            }

            @Override
            protected void onPostExecute(String[] strings) {
                if (!tv_endtime.getText().toString().equals(strings[1])) {
                    tv_endtime.setText(strings[1]);
                }
                tv_currenttime.setText(strings[0]);
            }
        }.execute(progress, duration);
    }


    @Override
    public void onTimeTick(long millisUntilFinished) {
        if (tv_timeleft != null) {
            Integer restSeconds = Long.valueOf(millisUntilFinished / 1000).intValue();

            Integer[] miniteSecond = new Integer[2];
            String timeExchangeV2 = CommonUtils.timeExchangeV2(restSeconds, miniteSecond);
            tv_timeleft.setText(timeExchangeV2);
        }
    }

    @Override
    public void onTimeFinish() {
        TickUtils.setCountdownTimer(TickUtils.CLOSE_MODE_NO_OPEN);
        tv_timeleft.setText("");
    }
}


