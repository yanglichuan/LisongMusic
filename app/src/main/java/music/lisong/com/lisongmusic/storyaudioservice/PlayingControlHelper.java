package music.lisong.com.lisongmusic.storyaudioservice;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import music.lisong.com.lisongmusic.bean.Song;

/**
 * 常量和静态变量
 */
// 播放前变量准备
//        PlayingControlHelper.setSystemAblumFlag(false);
//        PlayingControlHelper.setUserAblumFlag(true);
//
//        PlayingControlHelper.setAblumBean(ablumBean);
//        PlayingControlHelper.setAblumBean(null);

//        PlayingControlHelper.setPlayingList(mObjects);
//        PlayingControlHelper.setTitle("title");
//        PlayingControlHelper.setPlayFrom(currentPlayIndex);
//        PlayingControlHelper.play(getContext());

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

    public static void playIndex(int index){
        if (playList != null && playList.size() > 0 && index < playList.size()) {
            fromIndex = index;
        }
    }

    public static Song getPlayingStory() {
        if (playList != null && playList.size() > 0 && fromIndex < playList.size()) {
            Song s =  playList.get(fromIndex);
            if(TextUtils.isEmpty(s.getCoverImg())){
                s.setCoverImg("http://n.sinaimg.cn/ent/transform/20170920/M3G7-fykymue7408829.jpg");
            }
            return s;
        }
        return null;
    }


    public static Song nextSong() {
        fromIndex++;
        if (playList != null && playList.size() > 0 && fromIndex < playList.size()) {
            Song s =  playList.get(fromIndex);
            if(TextUtils.isEmpty(s.getCoverImg())){
                s.setCoverImg("http://n.sinaimg.cn/ent/transform/20170920/M3G7-fykymue7408829.jpg");
            }
            return s;
        }else {
            fromIndex--;
        }
        return null;
    }

    public static Song presong() {
        fromIndex--;
        if (playList != null && playList.size() > 0 && fromIndex < playList.size() && fromIndex >= 0) {
            Song s =  playList.get(fromIndex);
            if(TextUtils.isEmpty(s.getCoverImg())){
                s.setCoverImg("http://n.sinaimg.cn/ent/transform/20170920/M3G7-fykymue7408829.jpg");
            }
            return s;
        }else {
            fromIndex++;
        }
        return null;
    }


}
