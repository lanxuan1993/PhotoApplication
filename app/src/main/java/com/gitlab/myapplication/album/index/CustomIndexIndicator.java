package com.gitlab.myapplication.album.index;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.viewpager.widget.ViewPager;
import com.hitomi.tilibrary.style.IIndexIndicator;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * @author: created by ZhaoBeibei on 2020-09-04 14:18
 * @describe:
 */
public class CustomIndexIndicator implements IIndexIndicator {
    private ViewPager mViewPager;
    private CustomNumberIndicator numberIndicator;
    private OnChangePageSelected onChangePageSelected;

    @Override
    public void attach(FrameLayout parent) {
        FrameLayout.LayoutParams indexLp = new FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        indexLp.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        indexLp.topMargin = 30;

        numberIndicator = new CustomNumberIndicator(parent.getContext());
        numberIndicator.setLayoutParams(indexLp);

        parent.addView(numberIndicator);
    }

    @Override
    public void onShow(ViewPager viewPager) {
        if (numberIndicator == null) return;
        numberIndicator.setVisibility(View.VISIBLE);
        setViewPager(viewPager);
    }

    @Override
    public void onHide() {
        if (numberIndicator == null) return;
        numberIndicator.setVisibility(View.GONE);
    }

    @Override
    public void onRemove() {
        if (numberIndicator == null) return;
        ViewGroup vg = (ViewGroup) numberIndicator.getParent();
        if (vg != null) {
            vg.removeView(numberIndicator);
        }
    }


    public void setViewPager(ViewPager viewPager) {
        if (viewPager != null && viewPager.getAdapter() != null) {
            mViewPager = viewPager;
            mViewPager.removeOnPageChangeListener(mInternalPageChangeListener);
            mViewPager.addOnPageChangeListener(mInternalPageChangeListener);
            mInternalPageChangeListener.onPageSelected(mViewPager.getCurrentItem());
        }
    }

    private final ViewPager.OnPageChangeListener mInternalPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            if (mViewPager.getAdapter() == null || mViewPager.getAdapter().getCount() <= 0) {
                return;
            }

            numberIndicator.setIndexTv(position, mViewPager.getAdapter().getCount());
            onChangePageSelected.onPageSelected(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };


    public interface OnChangePageSelected {
        void onPageSelected(int position);
    }

    public void setOnChangePageSelected(OnChangePageSelected listener) {
        onChangePageSelected = listener;
    }
}
