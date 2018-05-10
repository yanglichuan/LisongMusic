package music.lisong.com.lisongmusic.activity;

import android.os.CountDownTimer;

import java.util.ArrayList;
import java.util.List;

import music.lisong.com.lisongmusic.Ap;
import music.lisong.com.lisongmusic.storyaudioservice.MusicServiceUtil;
import music.lisong.com.lisongmusic.utils.SpUtils;

public class TickUtils {

    //     倒计时模式
    public static final int CLOSE_MODE_NO_OPEN = 0;
    public static final int CLOSE_MODE_OVER_NOW = 1;
    public static final int CLOSE_MODE_15 = 2;
    public static final int CLOSE_MODE_30 = 3;
    public static final int CLOSE_MODE_60 = 4;
    public static final int CLOSE_MODE_90 = 5;


    public interface TimeTickCallBack {
        void onTimeTick(long millisUntilFinished);

        void onTimeFinish();
    }


    private static List<TimeTickCallBack> tickCallBacks = new ArrayList<>();
    //定时播放
    private static CountDownTimer countdownTimer;
    private static int current_close_mode = 0;

    public static CountDownTimer getCountDownTimer() {
        return countdownTimer;
    }

    public static int getCurrent_close_mode() {
        return current_close_mode;
    }


    public static void setCountdownTimer(int closeMode) {
        current_close_mode = closeMode;
        if (countdownTimer != null) {
            countdownTimer.cancel();
            countdownTimer = null;
        }

        int totalTime = 0;
        if (current_close_mode == CLOSE_MODE_NO_OPEN) {
            SpUtils.putString("timechoice", "1");

            return;
        } else if (current_close_mode == CLOSE_MODE_OVER_NOW) {
            return;
        } else if (current_close_mode == CLOSE_MODE_15) {
            totalTime = 15 * 60 * 1000;
        } else if (current_close_mode == CLOSE_MODE_30) {
            totalTime = 30 * 60 * 1000;
        } else if (current_close_mode == CLOSE_MODE_60) {
            totalTime = 60 * 60 * 1000;
        } else if (current_close_mode == CLOSE_MODE_90) {
            totalTime = 90 * 60 * 1000;
        } else {
        }

        countdownTimer = new CountDownTimer(totalTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (tickCallBacks.size() > 0) {
                    for (TickUtils.TimeTickCallBack tcb : tickCallBacks) {
                        tcb.onTimeTick(millisUntilFinished);
                    }
                }
            }

            @Override
            public void onFinish() {
                if (tickCallBacks.size() > 0) {
                    for (TickUtils.TimeTickCallBack tcb : tickCallBacks) {
                        tcb.onTimeFinish();
                    }
                }

                //做处理了
                MusicServiceUtil.pausePlay(Ap.application);
                //
                setCountdownTimer(CLOSE_MODE_NO_OPEN);
            }
        }.start();
    }

    public static void addTickCallBack(TickUtils.TimeTickCallBack tb) {
        if (tb == null) {
            return;
        }
        if (!tickCallBacks.contains(tb)) {
            tickCallBacks.add(tb);
        }
    }

    public static void removeTickCallBack(TickUtils.TimeTickCallBack tb) {
        if (tb == null) {
            return;
        }
        tickCallBacks.remove(tb);
    }
}
