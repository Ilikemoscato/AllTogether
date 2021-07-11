package com.liuzifan.alltogether.myview;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.view.animation.LinearInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class FishDrawable extends Drawable {

    private Path mPath;
    private Paint mPaint;

    //除鱼身外的所有透明度
    private final static int OTHER_ALPHA = 190;
    //鱼身透明度
    private final static int BODY_ALPHA = 160;
    //转弯更自然的重心（身体的中心点）
    private PointF middlePoint;
    //鱼头圆心点
    private PointF headPoint;
    //鱼的主角度
    private float fishMainAngle = 90;
    //鱼头半径R
    public final static float HEAD_RADIUS = 50;
    //鱼身长度
    private final static float BODY_LENGTH = 3.2f * HEAD_RADIUS;

    //--------------鱼鳍-------------
    // 二阶贝赛尔曲线
    //寻找鱼鳍开始点的线长
    private final static float FIND_FINS_LENGTH = 0.9f * HEAD_RADIUS;
    //鱼鳍的长度
    private final static float FINS_LENGTH = 1.3f * HEAD_RADIUS;
    //鱼鳍控制点距离起始点的长度
    private final static float FINS_CONTROL_LENGTH = 1.8f * FINS_LENGTH;
    //------------------------------

    //--------------鱼尾-------------
    //尾部大圆半径
    private final static float BIG_CIRCLE_RADIUS = 0.7f * HEAD_RADIUS;
    //尾部中圆半径
    private final static float MIDDLE_CIRCLE_RADIUS = 0.42f * HEAD_RADIUS;
    //尾部小圆半径
    private final static float SMALL_CIRCLE_RADIUS = 0.168f * HEAD_RADIUS;
    //寻找尾部小圆圆心的距离
    private final static float FIND_SMALL_CIRCLE_LENGTH = (0.4f + 2.7f) * MIDDLE_CIRCLE_RADIUS;
    //寻找大三角底边中心点的距离
    private final static float FIND_TRIANGLE_LENGTH = 2.7f * MIDDLE_CIRCLE_RADIUS;
    //------------------------------

    //动画从0-360度的值
    private float currentValue;
    //鱼头摆动幅度
    public static float headRepeatExtent = 8;
    //节肢1摆动幅度
    public static float segment1RepeatExtent = 12;
    //节肢2摆动幅度
    public static float segment2RepeatExtent = 18;
    //身体摆动频率
    public static float bodyRepeatFrequency = 2;

    public FishDrawable() {
        init();
    }

    private void init() {
        mPath = new Path();//路径
        mPaint = new Paint();//画笔
        mPaint.setStyle(Paint.Style.FILL);//设置画笔类型，填充
        mPaint.setARGB(OTHER_ALPHA, 244, 92, 71);//设置颜色
        mPaint.setAntiAlias(true);//抗锯齿
        mPaint.setDither(true);//防抖
        middlePoint = new PointF(4.19f * HEAD_RADIUS, 4.19f * HEAD_RADIUS);//重心坐标

        //设置属性动画
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 360);//运动幅度方向
        valueAnimator.setDuration(1200);//运动周期 为1s
        valueAnimator.setRepeatMode(ValueAnimator.RESTART);//重复模式为 单向
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);//无限期重复
        valueAnimator.setInterpolator(new LinearInterpolator());//插值器 设置匀速运动
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentValue = (float)animation.getAnimatedValue();
                invalidateSelf();
            }
        });
        valueAnimator.start();
    }

    /**
     * 等同于自定义View中的onDraw();
     * @param canvas
     */
    @Override
    public void draw(@NonNull Canvas canvas) {
        //绘制鱼头
        float fishAngle = (float) (fishMainAngle + Math.sin(Math.toRadians(currentValue)) * headRepeatExtent);
        headPoint = calculatePoint(middlePoint, BODY_LENGTH/2, fishAngle);
        canvas.drawCircle(headPoint.x, headPoint.y, HEAD_RADIUS, mPaint);

        //绘制右鱼鳍（起始点，结束点，控制点）使用贝塞尔曲线
        //计算起始点
        PointF rightFinsPoint = calculatePoint(headPoint, FIND_FINS_LENGTH, fishAngle - 110);
        makeFins(canvas, rightFinsPoint, fishAngle, 115);

        //绘制左鱼鳍（起始点，结束点，控制点）使用贝塞尔曲线
        //计算起始点
        PointF leftFinsPoint = calculatePoint(headPoint, FIND_FINS_LENGTH, fishAngle + 110);
        makeFins(canvas, leftFinsPoint, fishAngle, -115);

        //绘制节肢1
        //计算梯形下底中点
        PointF bottomCenterPoint1 = calculatePoint(middlePoint, BODY_LENGTH/2, fishAngle -180);
        PointF bottomCenterPoint2 = makeSegment(canvas, bottomCenterPoint1, BIG_CIRCLE_RADIUS, MIDDLE_CIRCLE_RADIUS,
                BIG_CIRCLE_RADIUS + MIDDLE_CIRCLE_RADIUS, fishAngle, true);

        //绘制节肢2
        //计算梯形下底中点
        makeSegment(canvas, bottomCenterPoint2, MIDDLE_CIRCLE_RADIUS, SMALL_CIRCLE_RADIUS,
                FIND_SMALL_CIRCLE_LENGTH, fishAngle, false);

        //绘制尾巴
        makeTriangle(canvas, bottomCenterPoint2, FIND_TRIANGLE_LENGTH, BIG_CIRCLE_RADIUS, fishAngle);
        makeTriangle(canvas, bottomCenterPoint2,
                FIND_TRIANGLE_LENGTH - 10, BIG_CIRCLE_RADIUS - 20, fishAngle);

        //绘制身体
        makeBody(canvas, headPoint, bottomCenterPoint1, fishAngle);
    }

    private void makeBody(Canvas canvas, PointF headPoint, PointF bottomCenterPoint1, float fishAngle) {
        //身体的四个点
        PointF topLeftPoint = calculatePoint(headPoint, HEAD_RADIUS, fishAngle + 90);
        PointF topRightPoint = calculatePoint(headPoint, HEAD_RADIUS, fishAngle - 90);
        PointF bottomLeftPoint = calculatePoint(bottomCenterPoint1, BIG_CIRCLE_RADIUS, fishAngle + 90);
        PointF bottomRightPoint = calculatePoint(bottomCenterPoint1, BIG_CIRCLE_RADIUS, fishAngle - 90);
        //身体的两个控制点
        PointF leftCentrolPoint = calculatePoint(headPoint, BODY_LENGTH * 0.56f, fishAngle + 130);
        PointF rightCentrolPoint = calculatePoint(headPoint, BODY_LENGTH * 0.56f, fishAngle - 130);

        //绘制身体
        mPath.reset();
        mPath.moveTo(topRightPoint.x, topRightPoint.y);
        mPath.lineTo(topLeftPoint.x, topLeftPoint.y);
        mPath.quadTo(leftCentrolPoint.x, leftCentrolPoint.y, bottomLeftPoint.x, bottomLeftPoint.y);
        mPath.lineTo(bottomRightPoint.x, bottomRightPoint.y);
        mPath.quadTo(rightCentrolPoint.x, rightCentrolPoint.y, topRightPoint.x, topRightPoint.y);
        canvas.drawPath(mPath, mPaint);
    }

    private void makeTriangle(Canvas canvas, PointF startPoint,
                              float findTriangleLength, float bigCircleRadius, float fishAngle) {

        float triangleAngle = (float) (fishAngle + Math.sin(Math.toRadians(currentValue * bodyRepeatFrequency)) * 18);
        //三角形的点
        PointF centerPoint = calculatePoint(startPoint, findTriangleLength, triangleAngle - 180);
        PointF leftPoint = calculatePoint(centerPoint, bigCircleRadius, triangleAngle + 90);
        PointF rightPoint = calculatePoint(centerPoint, bigCircleRadius, triangleAngle - 90);

        //绘制三角形
        mPath.reset();
        mPath.moveTo(startPoint.x, startPoint.y);
        mPath.lineTo(leftPoint.x, leftPoint.y);
        mPath.lineTo(rightPoint.x, rightPoint.y);
        canvas.drawPath(mPath, mPaint);
    }


    private PointF makeSegment(Canvas canvas, PointF bottomCenterPoint, float bigCiclelRadius,
                             float smallCicleRadius, float segmentHeight, float fishAngle, boolean hasBigCircle) {
        float segmentAngle ;
        if(hasBigCircle) {
            segmentAngle = (float) (fishAngle + Math.cos(Math.toRadians(currentValue * bodyRepeatFrequency)) * segment1RepeatExtent);
        } else {
            segmentAngle = (float) (fishAngle + Math.sin(Math.toRadians(currentValue * bodyRepeatFrequency)) * segment2RepeatExtent);
        }

        //根据矩形下底边坐标，计算各点坐标
        PointF bottomLeftPoint = calculatePoint(bottomCenterPoint, bigCiclelRadius, segmentAngle - 90);
        PointF bottomRightPoint = calculatePoint(bottomCenterPoint, bigCiclelRadius, segmentAngle + 90);
        PointF topCenterPoint = calculatePoint(bottomCenterPoint, segmentHeight, segmentAngle - 180);
        PointF topLeftPoint = calculatePoint(topCenterPoint, smallCicleRadius, segmentAngle - 90);
        PointF topRightPoint = calculatePoint(topCenterPoint, smallCicleRadius, segmentAngle + 90);
        //开始绘制
        if(hasBigCircle) {
            canvas.drawCircle(bottomCenterPoint.x, bottomCenterPoint.y, bigCiclelRadius, mPaint);
        }
        canvas.drawCircle(topCenterPoint.x, topCenterPoint.y, smallCicleRadius, mPaint);
        mPath.reset();
        mPath.moveTo(bottomLeftPoint.x, bottomLeftPoint.y);
        mPath.lineTo(topLeftPoint.x, topLeftPoint.y);
        mPath.lineTo(topRightPoint.x, topRightPoint.y);
        mPath.lineTo(bottomRightPoint.x, bottomRightPoint.y);
        canvas.drawPath(mPath, mPaint);
        return topCenterPoint;
    }

    /**
     * 绘制鱼鳍
     * @param canvas 画布
     * @param finsStartPoint 鱼鳍起始点
     * @param fishAngle 起始点与结束点角度
     * @param controlAngle  控制点与起始点角度
     */
    private void makeFins(Canvas canvas, PointF finsStartPoint, float fishAngle, float controlAngle) {
        //控制点角度
        //结束点
        PointF endPoint = calculatePoint(finsStartPoint, FINS_LENGTH, fishAngle -180);
        //控制点
        PointF controlPoint = calculatePoint(finsStartPoint, FINS_CONTROL_LENGTH,
                fishAngle - controlAngle);
        //绘制
        mPath.reset();
        mPath.moveTo(finsStartPoint.x, finsStartPoint.y);
        mPath.quadTo(controlPoint.x, controlPoint.y, endPoint.x, endPoint.y);
        canvas.drawPath(mPath, mPaint);
    }

    /**
     * 根据重心点计算其他圆心坐标
     * @param startPoint 起始点
     * @param length 两点距离
     * @param angle 两点连线的角度
     * @return 返回目标点坐标
     */
    public static PointF calculatePoint(PointF startPoint, float length, float angle) {
        float deltaX = (float)(Math.cos(Math.toRadians(angle)) * length);
        float deltaY = -(float)(Math.sin(Math.toRadians(angle)) * length);
        return new PointF(startPoint.x + deltaX, startPoint.y + deltaY);
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;//根据透明度，确定返回值 只要不是0和255就用TRANSLUCENT
    }

    /**
     * 设置Drawable的宽
     * @return
     */
    @Override
    public int getIntrinsicHeight() {
        return (int) (8.38f * HEAD_RADIUS);
    }

    /**
     * 设置Drawable的高
     * @return
     */
    @Override
    public int getIntrinsicWidth() {
        return (int) (8.38f * HEAD_RADIUS);
    }

    public PointF getMiddlePoint() {
        return middlePoint;
    }

    public void setMiddlePoint(PointF middlePoint) {
        this.middlePoint = middlePoint;
    }

    public PointF getHeadPoint() {
        return headPoint;
    }

    public void setHeadPoint(PointF headPoint) {
        this.headPoint = headPoint;
    }

    public void setFishMainAngle(float fishMainAngle) {
        this.fishMainAngle = fishMainAngle;
    }
}
