package music.lisong.com.lisongmusic.utils;

import android.util.Log;


/**
 * Log统一管理类
 */
public class LogUtil {
    private static final String TAG = "kaishu";
    private static boolean bEnable = true;// 是否需要打印bug，可以在application的onCreate函数里面初始化
    private static boolean bInitLog = false;
    private static int logIndex = 1;

    private LogUtil() {
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public static void w(String msg) {
        if (bEnable) {
            Log.w(TAG, ">>>" + (logIndex++) + ":   " + msg);
        }
    }

    // 下面四个是默认tag的函数
    public static void i(String msg) {
        if (bEnable) {
            Log.i(TAG, ">>>" + (logIndex++) + ":   " + msg);
        }
    }

    public static void d(String msg) {
        if (bEnable) {
            Log.d(TAG, ">>>" + (logIndex++) + ":   " + msg);
        }
    }

    public static void e(String msg) {
        if (bEnable)
            Log.e(TAG, ">>>" + (logIndex++) + ":   " + msg);
    }

    public static void v(String msg) {
        if (bEnable) {
            Log.v(TAG, ">>>" + (logIndex++) + ":   " + msg);
        }
    }

    public static void w(String tag, String msg) {
        if (bEnable) {
            Log.w(tag, ">>>" + (logIndex++) + ":   " + msg);
        }
    }


    // 下面是传入自定义tag的函数
    public static void i(String tag, String msg) {
        if (bEnable) {
            Log.i(tag, ">>>" + (logIndex++) + ":   " + msg);
        }
    }

    public static void d(String tag, String msg) {
        if (bEnable) {
            Log.d(tag, ">>>" + (logIndex++) + ":   " + msg);
        }
    }

    public static void e(String tag, String msg) {
        if (bEnable) {
            Log.e(tag, ">>>" + (logIndex++) + ":   " + msg);
        }
    }

    public static void v(String tag, String msg) {
        if (bEnable) {
            Log.v(tag, ">>>" + (logIndex++) + ":   " + msg);
        }
    }

}
