package com.gitlab.myapplication.album.index;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatTextView;

import java.util.Locale;

/**
 * @author: created by ZhaoBeibei on 2020-09-04 15:50
 * @describe:
 */
public class CustomNumberIndicator extends AppCompatTextView {

    private static final String STR_NUM_FORMAT = "%s/%s";

    public CustomNumberIndicator(Context context) {
        this(context, null);
    }

    public CustomNumberIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomNumberIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initNumberIndicator();
    }

    private void initNumberIndicator() {
        setTextColor(Color.WHITE);
        setTextSize(18);
    }

    /**
     * 设置index
     *
     * @param position
     * @param count
     */
    public void setIndexTv(int position, int count) {
        setText(String.format(Locale.getDefault(), STR_NUM_FORMAT, position + 1, count));
    }
}
