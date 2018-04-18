package music.lisong.com.lisongmusic.activity;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import music.lisong.com.lisongmusic.R;
import music.lisong.com.lisongmusic.storyaudioservice.Callback;
import music.lisong.com.lisongmusic.storyaudioservice.MusicServiceUtil;


public class BaseActivity extends AppCompatActivity implements Callback {
    protected ImageView mAudioPlayIcon;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MusicServiceUtil.addPlayingCallback(this);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

        mAudioPlayIcon = (ImageView) findViewById(R.id.title_audio_state_iv);
        if (mAudioPlayIcon != null) {
            mAudioPlayIcon.setVisibility(View.VISIBLE);
            mAudioPlayIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(BaseActivity.this, PlayingActivity.class));
                }
            });
        }
    }

    protected void freshPlayingIcon() {
        if (mAudioPlayIcon == null) {
            return;
        }
        if (MusicServiceUtil.isPlaying()) {
            AnimationDrawable animation = (AnimationDrawable)
                    ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_equalizer_newyear_red_36dp);
//                        DrawableCompat.setTintList(animation, sColorStatePlaying);
            mAudioPlayIcon.setImageDrawable(animation);
            animation.start();
        } else {
            Drawable playDrawable = ContextCompat.getDrawable(getApplicationContext(),
                    R.drawable.ic_equalizer1_red_36dp);
//                        DrawableCompat.setTintList(playDrawable, sColorStatePlaying);
            mAudioPlayIcon.setImageDrawable(playDrawable);
        }
//        DrawableCompat.setTintList(mAudioPlayIcon.getDrawable(),
//                ColorStateList.valueOf(getResources().getColor(R.color.kaishugreen)));
    }

    @Override
    protected void onResume() {
        super.onResume();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                freshPlayingIcon();
            }
        },500);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MusicServiceUtil.removePlayingCallBack(this);
    }

    @Override
    public void onCompletion(String currentPlayUrl, int currentVoiceId, String nextPlayUrl) {

    }

    @Override
    public void onPlaybackStatusChanged(String playUrl, int voiceId, int state) {
        freshPlayingIcon();
    }

    @Override
    public void onError(String playUrl, int voiceId, String error) {

    }

    @Override
    public void onProgress(String playUrl, int voiceId, long progress, long duration) {

    }

    @Override
    public void onPlayBegin(String playUrl, int voiceId) {

    }

    @Override
    public void onPlayOver() {

    }
}
