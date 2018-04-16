package music.lisong.com.lisongmusic;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import music.lisong.com.lisongmusic.bean.Likong;
import music.lisong.com.lisongmusic.fresco.FrescoImagePipelineConfigFactory;

/**
 * 作者：ylc on 2018/3/20 0020 19:04
 * 邮箱：yanglichuancom@.163com
 * LisongMusic
 */

public class Ap extends Application {

    public static Context application;
    public static List<Likong> likongList = new ArrayList<>();
    public String userAgent;

    @Override
    public void onCreate() {
        application = this;
        super.onCreate();
        Bmob.initialize(this, "d245bfb636171cf1244d15829c76290e");

        initFresco();
        userAgent = Util.getUserAgent(this, "ExoPlayerDemo");

        queryLike();
    }

    private void initFresco() {
        Fresco.initialize(application,
                FrescoImagePipelineConfigFactory.getOkHttpImagePipelineConfig(this));
    }

    public static void queryLike() {
        BmobQuery<Likong> query = new BmobQuery<>();
        query.findObjects(new FindListener<Likong>() {
            @Override
            public void done(List<Likong> list, BmobException e) {
                if (list != null && list.size() > 0) {
                    likongList = list;
                }
            }
        });
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public DataSource.Factory buildDataSourceFactory(DefaultBandwidthMeter bandwidthMeter) {
        return new DefaultDataSourceFactory(this, bandwidthMeter,
                buildHttpDataSourceFactory(bandwidthMeter));
    }

    public HttpDataSource.Factory buildHttpDataSourceFactory(DefaultBandwidthMeter bandwidthMeter) {
        return new DefaultHttpDataSourceFactory(userAgent, bandwidthMeter);
    }
}
