package music.lisong.com.lisongmusic.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import music.lisong.com.lisongmusic.Ap;
import music.lisong.com.lisongmusic.KaishuApplication;

public class CommonUtils {

    public static int dp2px(float dpValue) {
        final float scale = Ap.application.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    public static void bindFrescoFromResource(
            SimpleDraweeView draweeView,
            int id) {
        String uri = "res://music.lisong.com.lisongmusic/" + id;

        ImageRequest imageRequest =
                ImageRequestBuilder.newBuilderWithSource(Uri.parse(uri))
                        .setProgressiveRenderingEnabled(true)
                        .build();
        DraweeController draweeController = Fresco.newDraweeControllerBuilder()
                .setImageRequest(imageRequest)
                .setOldController(draweeView.getController())
                .setControllerListener(null)
                .setAutoPlayAnimations(true)
                .setTapToRetryEnabled(true)
                .build();
        draweeView.setController(draweeController);
    }


    public static boolean isServiceWork(String serviceName) {
        boolean isWork = false;
        ActivityManager myAM = (ActivityManager) KaishuApplication.getContext()
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(40);
        if (myList.size() <= 0) {
            return false;
        }
        for (int i = 0; i < myList.size(); i++) {
            String mName = myList.get(i).service.getClassName().toString();
            if (mName.equals(serviceName)) {
                isWork = true;
                break;
            }
        }
        return isWork;
    }

    public static boolean isNetworkAvailableNoTip() {
        if (KaishuApplication.getContext() != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) KaishuApplication.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    public static boolean is3GConnected() {
        if (KaishuApplication.getContext() != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) KaishuApplication.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null
                    && mNetworkInfo.isAvailable()
                    && mNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                return true;
            }
        }

        return false;
    }

    public static String getString(int resId) {
        return KaishuApplication.getContext().getResources().getString(resId);
    }

    public static String timeExchange(long timelong) {
        if (timelong < 0) {
            return "0";
        }
        //排除掉小于0的情况
        Date date = new Date(timelong);
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
        String tempDataText = formatter.format(date);

        int index = tempDataText.indexOf(":");
        String indexText = tempDataText.substring(0, index);
        if ("00".equals(indexText)) {
            return tempDataText.substring(index + 1);
        }

        String[] mytimes = tempDataText.split(":");
        int hour = 0;
        int min = 0;
        int sec = 0;
        try {
            hour = Integer.parseInt(mytimes[0]);
            min = Integer.parseInt(mytimes[1]);
            sec = Integer.parseInt(mytimes[2]);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        if (hour > 0) {
            min = min + (hour * 60);
        }

        if (sec < 10) {
            return min + ":0" + sec;
        } else {
            return min + ":" + sec;
        }
    }

    public static String timeExchangeV2(long seconds, Integer[] minutesecond) {
        //排除掉小于0的情况
        if (seconds > 0) {
            int minutesleft = (int) (seconds / 60);
            int secondleft = (int) (seconds % 60);

            minutesecond[0] = minutesleft;
            minutesecond[1] = secondleft;

            if (minutesleft > 0) {
                return minutesleft + "\'";
            } else {
                return secondleft + "\'\'";
            }
        } else {
            minutesecond[0] = 0;
            minutesecond[1] = 0;
            return "0\'\'";
        }
    }
}
