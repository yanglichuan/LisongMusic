package music.lisong.com.lisongmusic.bean;

import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.Serializable;


public class TimeItem implements Serializable {

    public TextView tv;
    public ImageView iv;
    public View wholeView;


    public TimeItem(TextView tv, ImageView iv) {
        this.tv = tv;
        this.iv = iv;
        this.wholeView = wholeView;
    }

    public void setSelect(boolean b) {
        if (tv == null) {
            return;
        }
        if (b) {
            tv.setTextColor(Color.parseColor("#ffac2d"));
            iv.setVisibility(View.VISIBLE);
        } else {
            tv.setTextColor(Color.parseColor("#4a4a4a"));
            iv.setVisibility(View.GONE);
        }
    }
}
