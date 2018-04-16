package music.lisong.com.lisongmusic.utils;

import android.content.Context;
import android.widget.Toast;

import music.lisong.com.lisongmusic.KaishuApplication;


public class ToastUtil {

    private static Toast toast1 = null;
    private static Toast toast2 = null;

    public static void showMessage(final String msg) {
        showToast(msg);
    }

    private static void showToast(String msg) {
        showToast(msg, Toast.LENGTH_SHORT);
    }

    private static void showToast(String msg, int toastShortOrLong) {
        Context context = KaishuApplication.getContext();
        if (context == null) {
            return;
        }
        try {
            if (toast1 == null) {
                toast1 = Toast.makeText(context, msg, toastShortOrLong);
            } else {
                toast1.setText(msg);
            }
            if (toast1 != null) {
                toast1.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void toast(String msg) {
        showToast(msg);
    }

    public static void showMessage(final String msg, int toastShortOrLong) {
        showToast(msg, toastShortOrLong);
    }

    public static void showMessage(final int msg, int toastShortOrLong) {
        String ss = KaishuApplication.getContext().getString(msg);
        showToast(ss, toastShortOrLong);
    }

}
