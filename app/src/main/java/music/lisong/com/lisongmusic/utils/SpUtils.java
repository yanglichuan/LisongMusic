package music.lisong.com.lisongmusic.utils;

import android.content.SharedPreferences;

import music.lisong.com.lisongmusic.Ap;

import static android.content.Context.MODE_PRIVATE;

/**
 * 作者：ylc on 2018/3/21 0021 11:26
 * 邮箱：yanglichuancom@.163com
 * LisongMusic
 */

public class SpUtils {
    static SharedPreferences pref = Ap.application.getSharedPreferences("lisongdata", MODE_PRIVATE);
    static SharedPreferences.Editor editor = pref.edit();

    public static void putString(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }

    public static void putStringNocommit(String key, String value) {
        editor.putString(key, value);
    }


    public static void commmit() {
        editor.commit();
    }


    public static String get(String key) {
        return pref.getString(key, "");
    }


}
