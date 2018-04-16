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

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.mediacodec.MediaCodecRenderer;
import com.google.android.exoplayer2.mediacodec.MediaCodecUtil;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveVideoTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelections;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.util.Util;

import java.io.File;
import java.net.CookieManager;
import java.net.CookiePolicy;

import music.lisong.com.lisongmusic.Ap;
import music.lisong.com.lisongmusic.R;
import music.lisong.com.lisongmusic.utils.CommonUtils;
import music.lisong.com.lisongmusic.utils.LogUtil;
import music.lisong.com.lisongmusic.utils.ToastUtil;


public class EXOPlayerManager implements
        ExoPlayer.EventListener, TrackSelector.EventListener<MappingTrackSelector.MappedTrackInfo>,
        AudioManager.OnAudioFocusChangeListener {

    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
    private static final CookieManager DEFAULT_COOKIE_MANAGER;

    static {
        DEFAULT_COOKIE_MANAGER = new CookieManager();
        DEFAULT_COOKIE_MANAGER.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER);
    }

    private Handler mainHandler;
    private EventLogger eventLogger;
    private DataSource.Factory mediaDataSourceFactory;
    private SimpleExoPlayer player;
    private MappingTrackSelector trackSelector;
    private final boolean shouldAutoPlay = true;
    private int playerWindow;

    private long playerPosition = 0;
    private String mContentUri;
    private int mVoiceId = -1;
    private final int contentType = C.TYPE_OTHER;
    private Context context;


    private Callback mCallback;

    public void setCallback(Callback callback) {
        this.mCallback = callback;
    }


    // The volume we set the media player to when we lose audio focus, but are
    // allowed to reduce the volume instead of stopping playback.
    public static final float VOLUME_DUCK = 0.2f;
    // The volume we set the media player when we have audio focus.
    public static final float VOLUME_NORMAL = 1.0f;
    // we don't have audio focus, and can't duck (play at a low volume)
    private static final int AUDIO_NO_FOCUS_NO_DUCK = 0;
    // we don't have focus, but can duck (play at a low volume)
    private static final int AUDIO_NO_FOCUS_CAN_DUCK = 1;
    // we have full audio focus
    private static final int AUDIO_FOCUSED = 2;

    // Type of audio focus we have:
    private int mAudioFocus = AUDIO_NO_FOCUS_NO_DUCK;
    private final AudioManager mAudioManager;
    private WifiManager.WifiLock mWifiLock;
    private PowerManager.WakeLock wakeLock;


    public static int implcount = 0;

    public EXOPlayerManager(Context context) {
        this.context = context;
        EXOPlayerManager.implcount++;

        mainHandler = new Handler();
        mediaDataSourceFactory = buildDataSourceFactory(context, true);
        this.mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        // Create the Wifi lock (this does not acquire the lock, this just creates it)

        try {
            this.mWifiLock = ((WifiManager) context.getSystemService(Context.WIFI_SERVICE))
                    .createWifiLock(getWifiLockMode(), "uAmp_lock" + EXOPlayerManager.implcount);
            mWifiLock.setReferenceCounted(false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "EXOPlayerManager" + EXOPlayerManager.implcount);
    }

    @SuppressLint("InlinedApi")
    private static final int getWifiLockMode() {
        return Util.SDK_INT < 12 ? WifiManager.WIFI_MODE_FULL : WifiManager.WIFI_MODE_FULL_HIGH_PERF;
    }

    /**
     * Try to get the system audio focus.
     */
    private void tryToGetAudioFocus() {
        LogUtil.e("tryToGetAudioFocus");
        if (mAudioFocus != AUDIO_FOCUSED) {
            int result = mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN);
            if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                mAudioFocus = AUDIO_FOCUSED;
            }
        }
    }

    /**
     * Give up the audio focus.
     */
    private void giveUpAudioFocus() {
        LogUtil.e("giveUpAudioFocus");
        if (mAudioFocus == AUDIO_FOCUSED) {
            if (mAudioManager.abandonAudioFocus(this) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                mAudioFocus = AUDIO_NO_FOCUS_NO_DUCK;
            }
        }
    }

    public void setVolume(boolean bsilence) {
        if (player != null) {
            if (bsilence) {
                player.setVolume(0);
            } else {
                player.setVolume(1);
            }
        }
    }

    /**
     * Called by AudioManager on audio focus changes.
     * Implementation of {@link AudioManager.OnAudioFocusChangeListener}
     */
    @Override
    public void onAudioFocusChange(int focusChange) {
        LogUtil.e("onAudioFocusChange. focusChange=" + focusChange);
        if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
            // We have gained focus:
            mAudioFocus = AUDIO_FOCUSED;

        } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS ||
                focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT ||
                focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
            // We have lost focus. If we can duck (low playback volume), we can keep playing.
            // Otherwise, we need to pause the playback.
            boolean canDuck = focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK;
            mAudioFocus = canDuck ? AUDIO_NO_FOCUS_CAN_DUCK : AUDIO_NO_FOCUS_NO_DUCK;
        } else {
            LogUtil.e("onAudioFocusChange: Ignoring unsupported focusChange: " + focusChange);
        }
        configMediaPlayerState();
    }

    private void configMediaPlayerState() {
        LogUtil.e("configMediaPlayerState. mAudioFocus=" + mAudioFocus);
        if (mAudioFocus == AUDIO_NO_FOCUS_NO_DUCK) {
            // If we don't have audio focus and can't duck, we have to pause,
            if (getState() == PlaybackStateCompat.STATE_PLAYING) {
                pause();
            }
        } else if (mAudioFocus == AUDIO_NO_FOCUS_CAN_DUCK) {  // we have audio focus:
//                mMediaPlayer.setVolumeSilence(VOLUME_DUCK, VOLUME_DUCK); // we'll be relatively quiet
            if (getState() == PlaybackStateCompat.STATE_PLAYING) {
                pause();
            }
        } else if (mAudioFocus == AUDIO_FOCUSED) {
            if (getState() != PlaybackStateCompat.STATE_PLAYING) {
                reStart();
            }
        }
        // If we were playing when we lost focus, we need to resume playing.
    }


    /**
     * Releases resources used by the service for playback. This includes the
     * "foreground service" status, the wake locks and possibly the MediaPlayer.
     *
     * @param releaseMediaPlayer Indicates whether the Media Player should also
     *                           be released or not
     */
    public void relaxResources(boolean releaseMediaPlayer) {
        LogUtil.e("relaxResources. releaseMediaPlayer=" + releaseMediaPlayer);
        // stop and release the Media Player, if it's available
        if (releaseMediaPlayer) {
            releasePlayer();
        }
        // we can also release the Wifi lock, if we're holding it
        relaxWifiLock();

        // TODO: 2017/3/28
        giveUpAudioFocus();
    }

    public void relaxWifiLock() {
        // we can also release the Wifi lock, if we're holding it
        if (mWifiLock != null && mWifiLock.isHeld()) {
            mWifiLock.release();
        }
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
        }
    }

    public void acquireWifiLock() {
        // we can also release the Wifi lock, if we're holding it
        if (mWifiLock != null) {
            mWifiLock.acquire();
        }
        if (wakeLock != null) {
            wakeLock.acquire();
        }
    }


    private boolean isExis(String url) {
        File f = new File(url);
        return f.exists() && f.length() > 0;
    }

    public void playUri(String uri, int voiceId) {
        playUri(uri, voiceId, false, 0);
    }

    public void playUri(String webUri, int voiceId, boolean onlyLocal, long progressLong) {
        if (TextUtils.isEmpty(webUri)) {
            return;
        }
        if (getState() == PlaybackStateCompat.STATE_PLAYING && TextUtils.equals(webUri, mContentUri)) {
            return;
        } else if (getState() == PlaybackStateCompat.STATE_PAUSED && TextUtils.equals(webUri, mContentUri)) {
            reStart();
            return;
        } else if (getState() == PlaybackStateCompat.STATE_STOPPED && TextUtils.equals(webUri, mContentUri)) {
            //放行
        }

        boolean bexitLocal = false;
        if (!bexitLocal) {
            //判断网络
            if (!CommonUtils.isNetworkAvailableNoTip()) {
                ToastUtil.showMessage("无网络，请检查网络设置");
            } else {
                if (CommonUtils.is3GConnected()) {
                    ToastUtil.showMessage("您正在使用移动网络收听");
                }
            }
        }

        relaxWifiLock();
        tryToGetAudioFocus();
        acquireWifiLock();


        //调用一次事件
        if (mCallback != null) {
            mCallback.onPlayBegin(webUri, voiceId);
        }

        mContentUri = webUri;
        mVoiceId = voiceId;
        if (player == null) {
            initializePlayer(context);
        } else {
            initializePlayer(context);
        }
    }

    public String getCurrentPlayUrl() {
        return mContentUri;
    }

    public int getCurrentVoiceId() {
        return mVoiceId;
    }

    public void pause() {
        if (getState() == PlaybackStateCompat.STATE_PLAYING) {
            relaxWifiLock();
            giveUpAudioFocus();
        }
        if (player != null) {
            player.setPlayWhenReady(false);
        }
    }

    public void reStart() {
        relaxWifiLock();
        tryToGetAudioFocus();
        acquireWifiLock();

        if (player != null) {
            player.setPlayWhenReady(true);
        }
    }

    public void seekto(long timeMillis) {
        if (player != null) {
            player.seekTo(timeMillis);
        }
    }


    public long getCurrentPosition() {
        if (player != null) {
            return player.getCurrentPosition();
        }
        return 0;
    }

    public long getDuration() {
        if (player != null) {
            return player.getDuration();
        }
        return 0;
    }

    private void initializePlayer(Context context) {
        if (context == null) {
            return;
        }
        if (player == null) {
            eventLogger = new EventLogger();
            TrackSelection.Factory videoTrackSelectionFactory =
                    new AdaptiveVideoTrackSelection.Factory(BANDWIDTH_METER);
            trackSelector = new DefaultTrackSelector(mainHandler, videoTrackSelectionFactory);
            trackSelector.addListener(this);
            trackSelector.addListener(eventLogger);
            player = ExoPlayerFactory.newSimpleInstance(context, trackSelector,
                    new DefaultLoadControl(),
                    null, false);//modify ylc
            player.addListener(this);
            player.addListener(eventLogger);
            player.setAudioDebugListener(eventLogger);
            player.setVideoDebugListener(eventLogger);
            player.setId3Output(eventLogger);
            player.setPlayWhenReady(shouldAutoPlay);
        }
//            if (Util.maybeRequestReadExternalStoragePermission(this, uris)) {
//                // The player will be reinitialized if the permission is granted.
//                return;
//            }
        MediaSource mediaSource = buildMediaSource(null);
        player.setPlayWhenReady(shouldAutoPlay);
        player.prepare(mediaSource, true, true);
    }

    private MediaSource buildMediaSource(String overrideExtension) {
        int type = Util.inferContentType(!TextUtils.isEmpty(overrideExtension) ? "." + overrideExtension
                : Uri.parse(mContentUri).getLastPathSegment());
        switch (type) {
            case C.TYPE_OTHER:
                return new ExtractorMediaSource(Uri.parse(mContentUri),
                        mediaDataSourceFactory,
                        new DefaultExtractorsFactory(),
                        mainHandler, eventLogger);
            default: {
                throw new IllegalStateException("Unsupported type: " + type);
            }
        }
    }


    private void releasePlayer() {
        if (player != null) {
            playerWindow = player.getCurrentWindowIndex();
            playerPosition = C.TIME_UNSET;
            player.release();
            player = null;
            trackSelector = null;
            eventLogger = null;
        }
    }

    //     EXODemoPlayer.Listener implementation
    //     PlaybackStateCompat.STATE_PLAYING;
    //     PlaybackStateCompat.STATE_PAUSED;
    //     PlaybackStateCompat.STATE_STOPPED;
    //     PlaybackStateCompat.STATE_BUFFERING;
    //     PlaybackStateCompat.STATE_NONE;
    public int getState() {
        if (playWhenReady && (playState == ExoPlayer.STATE_READY)) {
            return PlaybackStateCompat.STATE_PLAYING;
        } else if (!playWhenReady && (playState == ExoPlayer.STATE_READY)) {
            return PlaybackStateCompat.STATE_PAUSED;
        } else if (playState == ExoPlayer.STATE_ENDED) {
            return PlaybackStateCompat.STATE_STOPPED;
        } else if (playState == ExoPlayer.STATE_BUFFERING) {
            return PlaybackStateCompat.STATE_BUFFERING;
        } else {
            return PlaybackStateCompat.STATE_NONE;
        }
    }


    public int playState = ExoPlayer.STATE_IDLE;
    public boolean playWhenReady = true;


    @Override
    public void onLoadingChanged(boolean isLoading) {
        // Do nothing.
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if (playbackState == ExoPlayer.STATE_ENDED) {
//      showControls();
        }
        //
        this.playWhenReady = playWhenReady;

        String text = "playWhenReady=" + playWhenReady + ", playbackState=";
        switch (playbackState) {
            case ExoPlayer.STATE_BUFFERING:
                text += "buffering";
                playState = ExoPlayer.STATE_BUFFERING;
                break;
            case ExoPlayer.STATE_ENDED:
                text += "ended";
                playState = ExoPlayer.STATE_ENDED;
                break;
            case ExoPlayer.STATE_IDLE:
                text += "idle";
                playState = ExoPlayer.STATE_IDLE;
                break;
            case ExoPlayer.STATE_READY:
                text += "ready";
                playState = ExoPlayer.STATE_READY;
                break;
            default:
                text += "unknown";
                break;
        }
        LogUtil.e(text + "    %%%%%%%%" + playWhenReady);

        if (mCallback != null) {
            mCallback.onPlaybackStatusChanged(mContentUri, mVoiceId, getState());
            //自动播放下一个
            if (playWhenReady && playState == ExoPlayer.STATE_ENDED) {
//                StoryBean nextStory = null;
//
//                if (nextStory == null) {
//                    nextStory = PlayingControlHelper.extractNextByMode();
//                }
//                if (nextStory == null) {
//                    mCallback.onPlayOver();
//                } else {
//                    //播放下一个
//                    playUri(nextStory.getVoiceurl(), nextStory.getStoryid());
//                }
            } else if (playWhenReady && playState == ExoPlayer.STATE_IDLE) {
//                StoryBean currentPlayingStory = PlayingControlHelper.getPlayingStory();
//                //
//                if (currentPlayingStory != null) {
//                    long progressNow = getCurrentPosition();
//                    playUri(currentPlayingStory.getVoiceurl(), currentPlayingStory.getStoryid(), true, progressNow);
//                }

            }
        }
    }

    @Override
    public void onPositionDiscontinuity() {
        // Do nothing.
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {
    }

    @Override
    public void onPlayerError(ExoPlaybackException e) {
        String errorString = null;
        if (e.type == ExoPlaybackException.TYPE_RENDERER) {
            Exception cause = e.getRendererException();
            if (cause instanceof MediaCodecRenderer.DecoderInitializationException) {
                // Special case for decoder initialization failures.
                MediaCodecRenderer.DecoderInitializationException decoderInitializationException =
                        (MediaCodecRenderer.DecoderInitializationException) cause;
                if (decoderInitializationException.decoderName == null) {
                    if (decoderInitializationException.getCause() instanceof MediaCodecUtil.DecoderQueryException) {
                        errorString = CommonUtils.getString(R.string.error_querying_decoders);
                    } else if (decoderInitializationException.secureDecoderRequired) {
                        errorString = context.getString(R.string.error_no_secure_decoder,
                                decoderInitializationException.mimeType);
                    } else {
                        errorString = context.getString(R.string.error_no_decoder,
                                decoderInitializationException.mimeType);
                    }
                } else {
                    errorString = context.getString(R.string.error_instantiating_decoder,
                            decoderInitializationException.decoderName);
                }
            }
        }
        if (errorString != null) {
            ToastUtil.showMessage(errorString);
        }
    }

    @Override
    public void onTrackSelectionsChanged(TrackSelections<? extends MappingTrackSelector.MappedTrackInfo> trackSelections) {
        MappingTrackSelector.MappedTrackInfo trackInfo = trackSelections.info;
        if (trackInfo.hasOnlyUnplayableTracks(C.TRACK_TYPE_VIDEO)) {
//            showToast(R.string.error_unsupported_video);
            ToastUtil.showMessage("不支持该视频类型");
        }

        if (trackInfo.hasOnlyUnplayableTracks(C.TRACK_TYPE_AUDIO)) {
//            showToast(R.string.error_unsupported_audio);
            ToastUtil.showMessage("不支持该音频类型");
        }
    }

    /**
     * Returns a new DataSource factory.
     *
     * @param useBandwidthMeter Whether to set {@link #BANDWIDTH_METER} as a listener to the new
     *                          DataSource factory.
     * @return A new DataSource factory.
     */
    private DataSource.Factory buildDataSourceFactory(Context context, boolean useBandwidthMeter) {
        return ((Ap) context.getApplicationContext())
                .buildDataSourceFactory(useBandwidthMeter ? BANDWIDTH_METER : null);
    }

    /**
     * Returns a new HttpDataSource factory.
     *
     * @param useBandwidthMeter Whether to set {@link #BANDWIDTH_METER} as a listener to the new
     *                          DataSource factory.
     * @return A new HttpDataSource factory.
     */
    private HttpDataSource.Factory buildHttpDataSourceFactory(Context context, boolean useBandwidthMeter) {
        return ((Ap) context.getApplicationContext())
                .buildHttpDataSourceFactory(useBandwidthMeter ? BANDWIDTH_METER : null);
    }
}
