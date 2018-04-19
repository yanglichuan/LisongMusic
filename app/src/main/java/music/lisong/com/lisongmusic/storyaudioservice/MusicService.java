/*
* Copyright (C) 2014 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package music.lisong.com.lisongmusic.storyaudioservice;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.media.session.PlaybackStateCompat;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import music.lisong.com.lisongmusic.activity.TickUtils;
import music.lisong.com.lisongmusic.utils.LogUtil;
import music.lisong.com.lisongmusic.utils.SpUtils;


public class MusicService extends Service implements Callback {
    //播放工具类
    private EXOPlayerManager mPlaybackManager;

    AudioManager mAudioManager;


    @Override
    public void onCreate() {
        super.onCreate();
        if (mPlaybackManager == null) {
            mPlaybackManager = new EXOPlayerManager(this);
            mPlaybackManager.setCallback(this);
        }

        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
    }

    public static final String ACTION_CMD = "com.example.android.uamp.ACTION_CMD";
    public static final String CMD_NAME = "CMD_NAME";
    public static final String OTHERCONTENT = "OTHERCONTENT";

    /**
     * (non-Javadoc)
     *
     * @see Service#onStartCommand(Intent, int, int)
     */
    @Override
    public int onStartCommand(Intent startIntent, int flags, int startId) {
        LogUtil.e("onStartCommand");
        if (startIntent != null) {
            String action = startIntent.getAction();
            String command = startIntent.getStringExtra(CMD_NAME);
            String url = startIntent.getStringExtra(OTHERCONTENT);
//            if (ACTION_CMD.equals(action)) {
//                if (CMD_PAUSE.equals(command)) {
//                    mPlaybackManager.pause();
//                } else if (CMD_TOPLAYURL.equals(command)) {
//                    play(url);
//                }
//            }
        }
        // Reset the delay handler to enqueue a message to stop the service if
        // nothing is playing.
        return START_STICKY;
    }

    /**
     * (non-Javadoc)
     *
     * @see Service#onDestroy()
     */
    @Override
    public void onDestroy() {
        // Service is being killed, so make sure we release our resources
        mPlaybackManager.relaxResources(true);
    }


    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        LogUtil.e("onRebind");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        //这里传递绑定过来的信息
        LogUtil.e("onBind");
        return new MusicBinder(this);
    }

    /**
     * 如果你想要在下次客户端绑定的时候接受一个onRebind()的调用
     * （而不是调用 onBind()），你可以选择在 onUnbind()中返回true。
     *
     * @param intent
     * @return
     */
    @Override
    public boolean onUnbind(Intent intent) {
        LogUtil.e("onUnbind");
        return true;
    }

    //开启定时器
    private Handler mHandler = new Handler();
    private Runnable progressRunnable = new Runnable() {
        @Override
        public void run() {
            if (mPlaybackManager != null &&
                    mPlaybackManager.getState() == PlaybackStateCompat.STATE_PLAYING) {
                onProgress(mPlaybackManager.getCurrentPlayUrl(),
                        mPlaybackManager.getCurrentVoiceId(),
                        mPlaybackManager.getCurrentPosition(),
                        mPlaybackManager.getDuration());
            }
            mHandler.postDelayed(this, 1000);
        }
    };

    boolean bProgressing = false;

    private void startProgressCallback() {
        if (!bProgressing) {
            bProgressing = true;
            mHandler.post(progressRunnable);
        }
    }

    private void stopProgressCallback() {
        if (bProgressing) {
            bProgressing = false;
            mHandler.removeCallbacks(progressRunnable);
        }
    }


    //===========用于监听回调=============================
    ArrayList<Callback> callbacks = new ArrayList<>();

    public void addCallback(Callback callback) {
        callbacks.add(callback);
        //没有开启需要开启
        startProgressCallback();
    }

    public void addCallback(ArrayList<Callback> callbacks) {
        for (Callback callback : callbacks) {
            addCallback(callback);
        }
    }

    public void removeCallBack(Callback callback) {
        callbacks.remove(callback);
    }

    public void removeAllCallBack() {
        callbacks.clear();
    }

    /**
     * 暂停
     */
    public void pause() {
        if (mPlaybackManager != null) {
            mPlaybackManager.pause();
            stopProgressCallback();
        }
    }

    public void reStart() {
        if (mPlaybackManager != null) {
            mPlaybackManager.reStart();
            startProgressCallback();
        }
    }


    public void seedTo(long postion) {
        if (mPlaybackManager != null) {
            mPlaybackManager.seekto(postion);
        }
    }

    /**
     * 添加到播放列表
     *
     * @param storyUrl
     */
    public void play(String storyUrl, int voiceId) {
        mPlaybackManager.playUri(storyUrl, voiceId);
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
    public int getPlayState() {
        return mPlaybackManager.getState();
    }


    //     ||getPlayState() ==  PlaybackStateCompat.STATE_BUFFERING
    public long getDuration() {
        if (getPlayState() == PlaybackStateCompat.STATE_PLAYING
                || getPlayState() == PlaybackStateCompat.STATE_STOPPED
                || getPlayState() == PlaybackStateCompat.STATE_PAUSED) {
            return mPlaybackManager.getDuration();
        }
        return 0;
    }

    public long getPosition() {
        return mPlaybackManager.getCurrentPosition();
    }

    //
    public String getCurrentPlayUrl() {
        return mPlaybackManager.getCurrentPlayUrl();
    }

    public int getCurrentVoiceId(){
        return mPlaybackManager.getCurrentVoiceId();
    }

    public static class MusicBinder extends Binder {
        private final WeakReference<MusicService> mWeakService;

        private MusicBinder(MusicService service) {
            mWeakService = new WeakReference<>(service);
        }


        public MusicService getService() {
            return mWeakService.get();
        }
    }

    /**
     * 播放状态回调
     */
    @Override
    public void onCompletion(String currentPlayUrl, int currentVoiceId, String nextPlayUrl) {
        ///更新回调
        for (int i = 0; i < callbacks.size(); i++) {
            Callback callback = callbacks.get(i);
            if (callback != null) {
                callback.onCompletion(currentPlayUrl, currentVoiceId, nextPlayUrl);
            }
        }


        if(TickUtils.getCurrent_close_mode() == TickUtils.CLOSE_MODE_OVER_NOW){
            pause();

            TickUtils.setCountdownTimer(TickUtils.CLOSE_MODE_NO_OPEN);
            SpUtils.putString("timechoice", "-1");
        }



    }

    @Override
    public void onPlayBegin(String playUrl, int voiceId) {
        ///更新回调
        for (int i = 0; i < callbacks.size(); i++) {
            Callback callback = callbacks.get(i);
            if (callback != null) {
                callback.onPlayBegin(playUrl, voiceId);
            }
        }
    }

    @Override
    public void onPlayOver() {
        stopProgressCallback();
    }

    /**
     * 播放状态回调
     *
     */
//
//        switch (playbackState) {
//        case ExoPlayer.STATE_BUFFERING:
//            text += "buffering";
//            playState = ExoPlayer.STATE_BUFFERING;
//            break;
//        case ExoPlayer.STATE_ENDED:
//            text += "ended";
//            playState = ExoPlayer.STATE_ENDED;
//            break;
//        case ExoPlayer.STATE_IDLE:
//            text += "idle";
//            playState = ExoPlayer.STATE_IDLE;
//            break;
//        case ExoPlayer.STATE_READY:
//            text += "ready";
//            playState = ExoPlayer.STATE_READY;
//            break;
//        default:
//            text += "unknown";
//            break;
//    }
    @Override
    public void onPlaybackStatusChanged(String playUrl, int voiceId, int state) {
        LogUtil.e("plyastate=" + state);

//        BusProvider.getInstance().post(new EXOPlayStateEvent(playUrl,voiceId, state));
        ///更新回调
        for (int i = 0; i < callbacks.size(); i++) {
            Callback callback = callbacks.get(i);
            if (callback != null) {
                callback.onPlaybackStatusChanged(playUrl, voiceId, state);
            }
        }
        //通知
        if (state == PlaybackStateCompat.STATE_PLAYING) {
//            updateLockScreen();
            //没有开启需要开启
            startProgressCallback();
        } else if (state == PlaybackStateCompat.STATE_PAUSED) {
        } else {
//             MediaNotificationManager.setPauseOrStart(context,false);
        }
    }

    /**
     * 播放状态回调
     */
    @Override
    public void onError(String playUrl, int voiceId, String error) {
        LogUtil.e(error + "播放出错");
        ///更新回调
        for (int i = 0; i < callbacks.size(); i++) {
            Callback callback = callbacks.get(i);
            if (callback != null) {
                callback.onError(playUrl, voiceId, error);
            }
        }
    }

    @Override
    public void onProgress(String playUrl,int voiceId, long progress, long duration) {
        LogUtil.e("musicService callback-size" + callbacks.size());
        long iCurrentStreamPosition = mPlaybackManager.getCurrentPosition();
        long iDuration = mPlaybackManager.getDuration();
        for (int i = 0; i < callbacks.size(); i++) {
            callbacks.get(i).onProgress(
                    playUrl,voiceId,
                    iCurrentStreamPosition,
                    iDuration);
        }

//        StoryBean currentPlayingStory = PlayingControlHelper.getPlayingStory();
//        //不断更新 并且固化   // TODO: 2017/6/8   sUrl有可能是本地的路径
//        if (iCurrentStreamPosition > 0 && currentPlayingStory != null
//                && currentPlayingStory.getVoiceurl().equals(playUrl)) {
//
//
//            new AsyncTask<Void, Void, Void>() {
//                @Override
//                protected Void doInBackground(Void... voids) {
//                    PlayCacheAndAnalysisBehaviorSPUtils.updateStoryPlaytime(
//                            iCurrentStreamPosition,
//                            currentPlayingStory.getStoryid(),
//                            currentPlayingStory);
//                    return null;
//                }
//            }.execute();
//        }
    }
}
