package music.lisong.com.lisongmusic.storyaudioservice;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import music.lisong.com.lisongmusic.activity.PlayingActivity;
import music.lisong.com.lisongmusic.bean.Song;
import music.lisong.com.lisongmusic.utils.SpUtils;

/**
 * 常量和静态变量
 */
public class PlayingControlHelper {
    private PlayingControlHelper() {
    }

    public static List<Song> getPlayList() {
        return playList;
    }

    static List<Song> playList = new ArrayList<>();
    static int fromIndex = 0;

    public static void setPlayList(List<Song> list, int a) {
        if (list == null) {
            return;
        }
        playList.clear();
        playList.addAll(list);

        if (a >= 0 && a < list.size()) {
            fromIndex = a;
        } else {
        }
    }

    public static void playIndex(int index) {
        if (playList != null && playList.size() > 0 && index < playList.size()) {
            fromIndex = index;
        }
    }

    public static Song getPlayingStory() {
        if (playList != null && playList.size() > 0 && fromIndex < playList.size()) {
            Song s = playList.get(fromIndex);
            if (TextUtils.isEmpty(s.getCoverImg())) {
                s.setCoverImg("http://n.sinaimg.cn/ent/transform/20170920/M3G7-fykymue7408829.jpg");
            }
            return s;
        }
        return null;
    }

    public static Song nextSong() {
        int imode = PlayingActivity.MODE_CIRCLE;
        String mode = SpUtils.get(PlayingActivity.PLAYMODE);
        if (!TextUtils.isEmpty(mode)) {
            imode = Integer.parseInt(mode);
        }
        fromIndex++;
        if (playList != null && playList.size() > 0) {
            if (imode == PlayingActivity.MODE_CIRCLE) {
                if (fromIndex < playList.size()) {

                } else if (fromIndex >= playList.size()) {
                    fromIndex = 0;
                }

            } else if (imode == PlayingActivity.MODE_REPEATONE) {
                fromIndex--;
            } else if (imode == PlayingActivity.MODE_RANDOM) {
                fromIndex = new Random().nextInt(playList.size());
            }
            Song s = playList.get(fromIndex);
            if (TextUtils.isEmpty(s.getCoverImg())) {
                s.setCoverImg("http://n.sinaimg.cn/ent/transform/20170920/M3G7-fykymue7408829.jpg");
            }
            return s;
        } else {
            fromIndex--;
        }
        return null;
    }

    public static Song presong() {
        int imode = PlayingActivity.MODE_CIRCLE;
        String mode = SpUtils.get(PlayingActivity.PLAYMODE);
        if (!TextUtils.isEmpty(mode)) {
            imode = Integer.parseInt(mode);
        }
        fromIndex--;
        if (playList != null && playList.size() > 0) {
            if (imode == PlayingActivity.MODE_CIRCLE) {
                if (fromIndex < 0) {
                    fromIndex = playList.size() - 1;
                } else if (fromIndex >= 0) {

                }

            } else if (imode == PlayingActivity.MODE_REPEATONE) {
                fromIndex++;
            } else if (imode == PlayingActivity.MODE_RANDOM) {
                fromIndex = new Random().nextInt(playList.size());
            }

            Song s = playList.get(fromIndex);
            if (TextUtils.isEmpty(s.getCoverImg())) {
                s.setCoverImg("http://n.sinaimg.cn/ent/transform/20170920/M3G7-fykymue7408829.jpg");
            }
            return s;
        } else {
            fromIndex++;
        }
        return null;
    }
}
