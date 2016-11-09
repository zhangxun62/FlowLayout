package com.alvin.flowlayout;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * @Title FlowLayout
 * @Description: 流式布局 适用于热标签,本文是参照张鸿洋在慕课网的视频编写的
 * @Author: alvin
 * @Date: 2016/11/8.15:11
 * @E-mail: 49467306@qq.com
 */
public class FlowLayout extends ViewGroup {
    private static final String TAG = FlowLayout.class.getSimpleName();


    public FlowLayout(Context context) {
        this(context, null);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);


        // 当计算方式 AT_MOST时,也就是wrap_content的时候
        int width = 0;
        int height = 0;

        /**
         *  计算所有子View的宽和高
         */
        // 定义行高;行宽
        int lineWidth = 0;
        int lineHeight = 0;
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);

            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
            // View所占宽度
            int childWidth = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            // View所占高度
            int childHeight = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
            // 当前行宽加上子View的宽度大于父容器的宽度 需要换行
            if ((lineWidth + childWidth) > sizeWidth - getPaddingRight() - getPaddingLeft()) {
                // 记录当前行的最大宽度
                width = Math.max(width, lineWidth);
                // 新起一行,重置行宽
                lineWidth = childWidth;
                // 记录行高
                height += lineHeight;
                // 新起一行,重置行高
                lineHeight = childHeight;
            } else {
                // 因为没换行 直接叠加
                lineWidth += childWidth;
                // 行高是当前行最高的那个子View
                lineHeight = Math.max(lineHeight, childHeight);
            }
            // 最后一个View,如果是当前行,那没有记录当前行高,如果是新起一行,那就是没有进行,行宽比较
            if (i == childCount - 1) {
                width = Math.max(width, lineWidth);
                height += lineHeight;
            }
        }
        setMeasuredDimension(modeWidth == MeasureSpec.EXACTLY ? sizeWidth : width + getPaddingLeft() + getPaddingRight()
                , modeHeight == MeasureSpec.EXACTLY ? sizeHeight : height + getPaddingTop() + getPaddingBottom());


    }

    /**
     * 所有子View,按行分组
     */
    private List<List<View>> mAllViews = new ArrayList<>();
    /**
     * 所有行高
     */
    private List<Integer> mLineHeight = new ArrayList<>();

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mAllViews.clear();
        mLineHeight.clear();
        int width = getWidth();
        int height = getHeight();
        int childCount = getChildCount();
        List<View> mLineViews = new ArrayList<>();
        // 行宽
        int lineWidth = 0;
        // 行高
        int lineHeight = 0;
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();
            if (lineWidth + childWidth + lp.leftMargin + lp.rightMargin > width - getPaddingLeft() - getPaddingRight()) {
                mAllViews.add(mLineViews);
                mLineHeight.add(lineHeight);
                // 重置行高
                lineHeight = childHeight + lp.topMargin + lp.bottomMargin;
                // 重置行宽
                lineWidth = 0;
                mLineViews = new ArrayList<>();
            }
            lineWidth += childWidth + lp.leftMargin + lp.rightMargin;
            lineHeight = Math.max(lineHeight, childHeight + lp.topMargin + lp.bottomMargin);
            mLineViews.add(child);

        } //for循环结束
        mAllViews.add(mLineViews);
        mLineHeight.add(lineHeight);
        int top = getPaddingTop();
        int left = getPaddingLeft();
        // 通过行 遍历所有子View
        for (int i = 0; i < mAllViews.size(); i++) {
            mLineViews = mAllViews.get(i);
            lineHeight = mLineHeight.get(i);

            for (int j = 0; j < mLineViews.size(); j++) {
                View child = mLineViews.get(j);
                MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
                if (child.getVisibility() == GONE) {
                    continue;
                }
                int lc = left + lp.leftMargin;
                int tc = top + lp.topMargin;
                int rc = lc + child.getMeasuredWidth();
                int bc = tc + child.getMeasuredHeight();
                // 设置每一个子View的位置
                child.layout(lc, tc, rc, bc);
                left += child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            }

            left = getPaddingLeft();
            top += lineHeight;

        }
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @SuppressWarnings("ResourceType")
    public void setTextViewFormat(List<String> mStringList, int textColorId, Drawable drawable) {
        if (null != mStringList && mStringList.size() > 0) {
            for (int i = 0; i < mStringList.size(); i++) {
                final TextView textView = new TextView(getContext());
                MarginLayoutParams lp = new MarginLayoutParams(MarginLayoutParams.WRAP_CONTENT
                        , MarginLayoutParams.WRAP_CONTENT);
                textView.setText(mStringList.get(i));
                textView.setClickable(true);
                textView.setFocusable(true);
                lp.leftMargin = (int) (getResources().getDisplayMetrics().density * 5);
                lp.bottomMargin = (int) (getResources().getDisplayMetrics().density * 5);
                textView.setTextColor(getResources().getColor(textColorId));
                textView.setBackgroundDrawable(drawable);

                textView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (null != mOnItemClickListenter)
                            mOnItemClickListenter.onClick(v, ((TextView) v).getText().toString());
                    }
                });
                addView(textView, lp);
            }
        }

    }

    @SuppressWarnings("ResourceType")
    public void setTextViewFormat(List<String> mStringList, int textColorId, int resId) {
        if (null != mStringList && mStringList.size() > 0) {
            for (int i = 0; i < mStringList.size(); i++) {
                TextView textView = new TextView(getContext());
                MarginLayoutParams lp = new MarginLayoutParams(MarginLayoutParams.WRAP_CONTENT
                        , MarginLayoutParams.WRAP_CONTENT);
                textView.setText(mStringList.get(i));
                textView.setTextColor(getResources().getColor(textColorId));
                textView.setClickable(true);
                textView.setFocusable(true);
                lp.leftMargin = (int) (getResources().getDisplayMetrics().density * 5);
                lp.bottomMargin = (int) (getResources().getDisplayMetrics().density * 5);
                textView.setBackgroundDrawable(getResources().getDrawable(resId));

                textView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (null != mOnItemClickListenter)
                            mOnItemClickListenter.onClick(v, ((TextView) v).getText().toString());
                    }
                });
                addView(textView, lp);
            }
        }
    }

    private OnItemClickListenter mOnItemClickListenter;

    public void setOnItemClickListenter(OnItemClickListenter onItemClickListenter) {
        mOnItemClickListenter = onItemClickListenter;
    }

    public interface OnItemClickListenter {
        void onClick(View v, String string);
    }
}
