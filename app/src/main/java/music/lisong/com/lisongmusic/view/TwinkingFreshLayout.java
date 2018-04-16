package music.lisong.com.lisongmusic.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.lcodecore.tkrefreshlayout.IHeaderView;
import com.lcodecore.tkrefreshlayout.OnAnimEndListener;

import music.lisong.com.lisongmusic.utils.CommonUtils;

public class TwinkingFreshLayout extends FrameLayout implements IHeaderView {

    private static final int MAX_ALPHA = 255;
    private ImageView mCircleView;

    private AnimationDrawable animationDrawable;
    private boolean mIsBeingDragged = false;

    public TwinkingFreshLayout(Context context) {
        this(context, null);
    }

    public TwinkingFreshLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TwinkingFreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        createProgressView();
        ViewCompat.setChildrenDrawingOrderEnabled(this, true);
        // the absolute offset has to take into account that the circle starts at an offset
    }

    private void createProgressView() {


        mCircleView = new ImageView(getContext());
        LayoutParams params = new LayoutParams(CommonUtils.dp2px(36), CommonUtils.dp2px(36), Gravity.CENTER);
        mCircleView.setLayoutParams(params);

        mCircleView.setImageResource(music.lisong.com.lisongmusic.R.drawable.header_dropdown_refresh_3);
        mCircleView.setVisibility(View.GONE);
        addView(mCircleView);
    }

    /**
     * One of DEFAULT, or LARGE.
     */
    public void setSize(int size) {
        // force the bounds of the progress circle inside the circle view to
        // update by setting it to null before updating its size and then
        // re-setting it
        mCircleView.setImageDrawable(null);
        mCircleView.setImageResource(music.lisong.com.lisongmusic.R.drawable.anim_loading_view);
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void onPullingDown(float fraction, float maxHeadHeight, float headHeight) {

//        mCircleView.setImageDrawable(null);
//        mCircleView.setImageResource(R.drawable.header_dropdown_refresh_1);
        if (!mIsBeingDragged) {
            mIsBeingDragged = true;
        }

        if (mCircleView.getVisibility() != View.VISIBLE) {
            mCircleView.setVisibility(View.VISIBLE);
        }

        if (fraction >= 1f) {
            ViewCompat.setScaleX(mCircleView, 1f);
            ViewCompat.setScaleY(mCircleView, 1f);
        } else {
            ViewCompat.setScaleX(mCircleView, fraction);
            ViewCompat.setScaleY(mCircleView, fraction);
        }
    }

    @Override
    public void onPullReleasing(float fraction, float maxHeadHeight, float headHeight) {
        mIsBeingDragged = false;
        if (fraction >= 1f) {
            ViewCompat.setScaleX(mCircleView, 1f);
            ViewCompat.setScaleY(mCircleView, 1f);
        } else {
            ViewCompat.setScaleX(mCircleView, fraction);
            ViewCompat.setScaleY(mCircleView, fraction);
        }
    }

    @Override
    public void startAnim(float maxHeadHeight, float headHeight) {
        mCircleView.setVisibility(View.VISIBLE);
        ViewCompat.setScaleX(mCircleView, 1f);
        ViewCompat.setScaleY(mCircleView, 1f);

//        ObjectAnimator anim = ObjectAnimator.ofFloat(mCircleView, "rotation", 0f, 360f);
//        anim.setDuration(1000);
//        anim.start();

        mCircleView.setImageDrawable(null);
        mCircleView.setImageResource(music.lisong.com.lisongmusic.R.drawable.anim_dropdown_refresh_loading_view);
        animationDrawable = (AnimationDrawable) mCircleView.getDrawable();
        animationDrawable.start();

    }

    @Override
    public void onFinish(final OnAnimEndListener animEndListener) {
        mCircleView.animate().scaleX(0).scaleY(0).alpha(0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                reset();
                animEndListener.onAnimEnd();
            }
        }).start();
    }

    @Override
    public void reset() {


        mCircleView.clearAnimation();
        mCircleView.setVisibility(View.GONE);
        mCircleView.setImageDrawable(null);
        mCircleView.setImageResource(music.lisong.com.lisongmusic.R.drawable.header_dropdown_refresh_3);

        ViewCompat.setScaleX(mCircleView, 0);
        ViewCompat.setScaleY(mCircleView, 0);
        ViewCompat.setAlpha(mCircleView, 1);
    }
}
