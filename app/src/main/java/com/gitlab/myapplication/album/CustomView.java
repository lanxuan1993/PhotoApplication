package com.gitlab.myapplication.album;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gitlab.myapplication.R;

/**
 * @author: created by ZhaoBeibei on 2020-09-03 13:39
 * @describe: transferee自定义保存, 分享等按钮控件组合
 */
public class CustomView extends RelativeLayout {
    private OnSaveClickListener onSaveClickListener;
    private OnShareClickListener onShareClickListener;
    private TextView saveTv;
    private TextView shareTv;

    public CustomView(Context context) {
        this(context, null);
    }

    public CustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        View view = (View) View.inflate(context, R.layout.custom_view, this);
        saveTv = (TextView) view.findViewById(R.id.tv_save);
        shareTv = (TextView) view.findViewById(R.id.tv_share);
        saveTv.setVisibility(View.GONE);
        shareTv.setVisibility(View.GONE);

        saveTv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onSaveClickListener.onSaveClick(v);
            }
        });

        shareTv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onShareClickListener.onShareClick(v);
            }
        });
    }

    public interface OnSaveClickListener {
        void onSaveClick(View v);
    }

    public interface OnShareClickListener {
        void onShareClick(View v);
    }

    public void setOnSaveClickListener(OnSaveClickListener listener) {
        onSaveClickListener = listener;
    }

    public void setOnShareClickListener(OnShareClickListener listener) {
        onShareClickListener = listener;
    }

    /**
     * 是否需要保存图片
     *
     * @param isNeedSave
     */
    public void setIsNeedSave(boolean isNeedSave) {
        if (isNeedSave) {
            saveTv.setVisibility(View.VISIBLE);
        } else {
            saveTv.setVisibility(View.GONE);
        }
    }

    /**
     * 是否需要分享图片
     *
     * @param isNeedShare
     */
    public void setIsNeedShare(boolean isNeedShare) {
        if (isNeedShare) {
            shareTv.setVisibility(View.VISIBLE);
        } else {
            shareTv.setVisibility(View.GONE);
        }
    }

}
