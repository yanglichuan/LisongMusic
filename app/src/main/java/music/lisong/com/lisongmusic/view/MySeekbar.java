package music.lisong.com.lisongmusic.view;

import android.content.Context;
import android.util.AttributeSet;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;


public class MySeekbar extends DiscreteSeekBar {

    public MySeekbar(Context context) {
        super(context);
    }

    public MySeekbar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MySeekbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        int action = event.getAction();
//        switch (action) {
//            case MotionEvent.ACTION_DOWN:
//                // Disallow Drawer to intercept touch events.
//                enableInterceptParent(false);
//                break;
//
//            case MotionEvent.ACTION_UP:
//                // Allow Drawer to intercept touch events.
//                enableInterceptParent(true);
//                break;
//            default:
////                vp = getParent();
////                for (int i=0;i<6;i++){
////                    if(vp instanceof SwipeRefreshLoadingTest){
////                        break;
////                    }else {
////                        vp =  vp.getParent();
////                    }
////                }
////                tt = (SwipeRefreshLoadingTest) vp;
////                tt.disallowInterceptParent(false);
////                tt.requestDisallowInterceptTouchEvent(false);
//                break;
//        }
//
//        // Handle seekbar touch events.
//        super.onTouchEvent(event);
//        return true;
//    }
//
//
//    public void enableInterceptParent(boolean b) {
//        ViewParent vp = getParent();
//        for (int i = 0; i < 6; i++) {
//            if (vp instanceof SwipeRefreshLoadingTest) {
//                break;
//            } else {
//                vp = vp.getParent();
//            }
//        }
//        SwipeRefreshLoadingTest tt = (SwipeRefreshLoadingTest) vp;
//        tt.disallowInterceptParent(!b);
//        tt.requestDisallowInterceptTouchEvent(!b);
//    }
}
