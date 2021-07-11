package com.liuzifan.alltogether.myview;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class FlowLayout extends ViewGroup {

    private static final String TAG = "FlowLayout";

    private int mHorizontalSpacing = dp2px(16);
    private int mVerticalSpacing = dp2px(8);

    private List<List<View>> allLineList;//记录所有的行，一行一行的存储，用于了layout
    private List<Integer> lineHeightList;//记录每一行的行高，用于layout

    public FlowLayout(Context context) {
        super(context);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //初始化集合
        allLineList = new ArrayList<>();
        lineHeightList = new ArrayList<>();

        //获取当前组件的padding
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();

        List<View> lineViews = new ArrayList<>();// 保存一行中所用的view
        int lineWidthUsed = 0;//记录这行已经使用了多宽的size
        int lineHeightUsed = 0;//记录一行的高度

        int parentNeededHeight = 0;//measure过程中，子view要求的父ViewGroup的宽
        int parentNeededWidth = 0;//measure过程中，子view要求的父ViewGroup的高

        int selfWidth = MeasureSpec.getSize(widthMeasureSpec);//ViewGroup解析的宽度
        int selfHeight = MeasureSpec.getSize(heightMeasureSpec);//ViewGroup解析的高度

        //测量子view
        int childCount = getChildCount();
        for(int i=0; i<childCount; i++) {
            View childView = getChildAt(i);
            LayoutParams childLP = childView.getLayoutParams();
            int childWidthMeasureSpac = getChildMeasureSpec(widthMeasureSpec, paddingLeft + paddingRight, childLP.width );
            int childHeightMeasureSpac = getChildMeasureSpec(heightMeasureSpec, paddingTop + paddingBottom, childLP.height);
            //测量子view
            childView.measure(childWidthMeasureSpac, childHeightMeasureSpac);

            //获取子view的宽高
            int childMeasureWidth = childView.getMeasuredWidth();
            int childMeasureHeight = childView.getMeasuredHeight();

            //通过宽度来判断是否需要换行，通过换行后的每行的行高来获取整个viewGroup的行高
            //如果需要换行
            if(childMeasureWidth + lineWidthUsed + mHorizontalSpacing > selfWidth) {
                allLineList.add(lineViews);
                lineHeightList.add(lineHeightUsed);

                //一旦换行，我们就可以判断当前行需要的宽和高了，所以此时需要记录下来
                parentNeededHeight = parentNeededHeight + lineHeightUsed + mVerticalSpacing;
                parentNeededWidth = Math.max(parentNeededWidth, lineWidthUsed + mHorizontalSpacing);

                //行的集合清空，行高行宽归零
                lineViews = new ArrayList<>();
                lineHeightUsed = 0;
                lineWidthUsed = 0;
            }

            //view是分行layout的，所以要记录每一行有哪些view，这样可以方便layout布局
            lineViews.add(childView);

            //每行都会有自己的宽和高
            lineWidthUsed = lineWidthUsed + childMeasureWidth + mHorizontalSpacing;
            lineHeightUsed = Math.max(lineHeightUsed, childMeasureHeight);

            if(lineWidthUsed <= selfWidth && i == childCount-1) {
                allLineList.add(lineViews);
                lineHeightList.add(lineHeightUsed);
                parentNeededHeight = parentNeededHeight + lineHeightUsed + mVerticalSpacing;
                parentNeededWidth = Math.max(parentNeededWidth, lineWidthUsed + mHorizontalSpacing);
            }
        }

        //根据子View的测量结果，来重新测量自己ViewGroup
        //作为一个ViewGroup，他自己也是一个View，它的大小也需要根据他父亲给他提供的宽高来度量
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int realWidth = (widthMode == MeasureSpec.EXACTLY) ? selfWidth : parentNeededWidth;
        int realHeight = (heightMode == MeasureSpec.EXACTLY) ? selfHeight : parentNeededHeight;

        //测量自己
        setMeasuredDimension(realWidth, realHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        //获取所有的行数
        int lineCount = allLineList.size();

        int curL = getPaddingLeft();
        int curT = getPaddingTop();

        for(int i=0; i<lineCount; i++) {
            List<View> lineViews = allLineList.get(i);
            int lineHeight = lineHeightList.get(i);
            for(int j=0; j<lineViews.size(); j++) {
                View childView = lineViews.get(j);
                int left = curL;
                int top = curT;
                int right = left + childView.getMeasuredWidth();
                int bottom = top + childView.getMeasuredHeight();
                childView.layout(left, top, right, bottom);
                curL = right + mHorizontalSpacing;
            }
            curL = getPaddingLeft();
            curT = curT + lineHeight + mVerticalSpacing;
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {

    }

    public static int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, Resources.getSystem().getDisplayMetrics());
    }

}
