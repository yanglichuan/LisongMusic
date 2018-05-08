package music.lisong.com.lisongmusic.adapter;

import android.animation.ObjectAnimator;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import music.lisong.com.lisongmusic.listener.BaseAdapterOnItemClickListener;

public class SearchStringAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public BaseAdapterOnItemClickListener innerItemListner = new BaseAdapterOnItemClickListener() {
        @Override
        public void onSimpleItemClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
            ObjectAnimator.ofFloat(view, "scaleY", 1, 1.05f, 1).setDuration(300).start();
            ObjectAnimator.ofFloat(view, "scaleX", 1, 1.05f, 1).setDuration(300).start();

        }


        @Override
        public void onItemChildClick(BaseQuickAdapter adapter, View v, int position) {
        }
    };


    public SearchStringAdapter() {
        super(music.lisong.com.lisongmusic.R.layout.rrr_string, null);
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, String storyBean) {

        TextView tvname = baseViewHolder.getView(music.lisong.com.lisongmusic.R.id.tv_name);
        if (storyBean == null) {
            return;
        }
        tvname.setText(storyBean);

    }

}
