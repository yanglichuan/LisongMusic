package music.lisong.com.lisongmusic.storyaudioservice;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;

import java.util.ArrayList;

import music.lisong.com.lisongmusic.KaishuApplication;
import music.lisong.com.lisongmusic.bean.Song;
import music.lisong.com.lisongmusic.utils.CommonUtils;
import music.lisong.com.lisongmusic.utils.DatabaseHelper;
import music.lisong.com.lisongmusic.utils.LogUtil;

public class MusicServiceUtil {
    //记得释放
    public static ServiceConnection conn = null;
    public static MusicService musicService;

    /**
     * 开启播放服务
     */
    private static void startMusicService() {
        try {
            Intent i = new Intent(KaishuApplication.getContext(), MusicService.class);
            if (!CommonUtils.isServiceWork(
                    "music.lisong.com.lisongmusic.storyaudioservice.MusicService")) {
//            i.setAction(MusicService.ACTION_CMD);
//            i.putExtra(MusicService.CMD_NAME, MusicService.CMD_TOPLAYURL);
//            i.putExtra(MusicService.OTHERCONTENT, source);
                KaishuApplication.getContext().startService(i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 绑定服务
     */
    public static void bindMusicService(final ServiceConnection connection) {
        if (musicService != null && conn != null) {
            return;
        }
        startMusicService();
        Intent i = new Intent(KaishuApplication.getContext(), MusicService.class);
        if (conn == null) {
            conn = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    LogUtil.e("MusicServiceUtil onSiceConnected");
                    try {
                        MusicService tempService = ((MusicService.MusicBinder) service).getService();
                        if (tempService != null && tempService != musicService) {
                            musicService = tempService;
                            if (connection != null) {
                                connection.onServiceConnected(name, service);
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                //当和service的连接意外丢失时，系统会调用这个方法。如果是客户端解除绑定，系统不会调用这个方法。
                @Override
                public void onServiceDisconnected(ComponentName name) {
                    LogUtil.e("ervice的连接意外丢失");
                    if (connection != null) {
                        connection.onServiceDisconnected(name);
                    }
                    musicService = null;
                }
            };
        }
        if (musicService == null) {
            KaishuApplication.getContext().bindService(i, conn, Service.BIND_AUTO_CREATE);
        }
    }

    public static void unbindMusicService(Context context) {
        if (musicService != null) {
            musicService.removeAllCallBack();
            musicService = null;
        }
        if (conn != null) {
            LogUtil.e("执行了解绑操作");
            KaishuApplication.getContext().unbindService(conn);
            conn = null;
        }
    }


    /**
     * 获取播放状态
     *
     * @return
     */
    //     PlaybackStateCompat.STATE_PLAYING;
    //     PlaybackStateCompat.STATE_PAUSED;
    //     PlaybackStateCompat.STATE_STOPPED;
    //     PlaybackStateCompat.STATE_BUFFERING;
    //     PlaybackStateCompat.STATE_NONE;
    public static int getCurrentPlayState() {
        if (musicService != null) {
            return musicService.getPlayState();
        }
        return PlaybackStateCompat.STATE_NONE;
    }


    public static boolean isPlaying() {
        if (musicService != null) {
            return musicService.getPlayState() == PlaybackStateCompat.STATE_PLAYING;
        }
        return false;
    }

    public static String getPlayUrl() {
        if (musicService != null) {
            return musicService.getCurrentPlayUrl();
        }
        return null;
    }

    public static int getVoiceId() {
        if (musicService != null) {
            return musicService.getCurrentVoiceId();
        }
        return -1;
    }

    public static void pausePlay(Context context) {
        //确保开启了
        if (conn != null && musicService != null) {
            musicService.pause();
            return;
        }
        bindMusicService(new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                if (musicService != null) {
                    musicService.pause();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        });
    }


    /**
     * 暂停后重新开始
     */
    public static void reStart() {
        //确保开启了
        if (conn != null && musicService != null && !TextUtils.isEmpty(musicService.getCurrentPlayUrl())) {
            musicService.reStart();
        }
    }


    public static void play(Context context, final String source,final int voiceId) {
        Song curSong = PlayingControlHelper.getPlayingStory();
        if(curSong!=null){
            try {
                DatabaseHelper helper = DatabaseHelper.getInstance();
                helper.saveItem(curSong);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        //确保开启了
        if (conn != null && musicService != null) {
            musicService.play(source,voiceId);
            return;
        }
        bindMusicService(new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                if (musicService != null) {
                    musicService.play(source,voiceId);
                }
            }
            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        });
    }


    //重要的辅助 * ylcadd
    static ArrayList<Callback> cacheCallbacks = new ArrayList<>();
    static boolean bBindingOk = false;


    static ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            bBindingOk = true;
            if (cacheCallbacks.size() > 0 && musicService != null) {
                musicService.addCallback(cacheCallbacks);
                cacheCallbacks.clear();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    /**
     * w
     */
    public static void addPlayingCallback(final Callback callback) {
        if (conn != null && musicService != null) {
            LogUtil.e("addCallback sucess");
            if (callback != null) {
                musicService.addCallback(callback);
            }
            return;
        }
        bindMusicService(serviceConnection);
        if (!bBindingOk) {
            cacheCallbacks.add(callback);
        }
    }


    public static void removePlayingCallBack(Callback callback) {
        if (musicService != null) {
            LogUtil.e("remove Callback success");
            musicService.removeCallBack(callback);
        }
    }

    public static void removeAllPlayingCallBack() {
        if (musicService != null) {
            musicService.removeAllCallBack();
        }
    }

    public static void destroyNoStop(Context context) {
        unbindMusicService(KaishuApplication.getContext());
    }

    public static void destroyAndStop(Context context) {
        if (musicService != null) {
            musicService.removeAllCallBack();
            musicService = null;
        }
        if (conn != null) {
            LogUtil.e("执行了解绑操作");
            KaishuApplication.getContext().unbindService(conn);
            conn = null;
        }
        Intent i = new Intent(KaishuApplication.getContext(), MusicService.class);
        KaishuApplication.getContext().stopService(i);
    }


    /**
     * 返回int数组 【0】为position  【1】为duration
     */
    public static long[] getPlayProgress() {
        if (musicService != null) {
            if (musicService.getPlayState() != PlaybackStateCompat.STATE_BUFFERING) {
                long duration = musicService.getDuration();
                long position = musicService.getPosition();
                if (duration < 0 || position < 0) {
                    return new long[]{0, 0};
                }
                if (duration < position) {
                    //throw new IllegalArgumentException();
                    return new long[]{0, 0};
                }
                return new long[]{position, duration};
            }
        }
        return new long[]{0, 0};
    }

    /**
     * 百分比  0 - 100
     *
     * @param progress
     */
    public static void seekToInPercent(long progress) {
        if (conn != null && musicService != null) {
            musicService.seedTo((int) (progress / 100.0 * musicService.getDuration()));
        }
    }

    public static void seekToInMs(long msProgress) {
        if (conn != null && musicService != null) {
            musicService.seedTo(msProgress);
        }
    }
}
