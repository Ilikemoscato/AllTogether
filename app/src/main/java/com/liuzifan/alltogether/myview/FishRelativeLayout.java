package com.liuzifan.alltogether.myview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PointF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.RequiresApi;

public class FishRelativeLayout extends RelativeLayout {

    private Paint mPaint;
    private ImageView imageView;
    private FishDrawable fishDrawable;

    private float touchX;
    private float touchY;
    private float ripper;
    private int alpha;

    public FishRelativeLayout(Context context) {
        this(context, null);
    }

    public FishRelativeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FishRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setWillNotDraw(false);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);//抗锯齿
        mPaint.setDither(true);//防抖
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(8);

        imageView = new ImageView(context);
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        fishDrawable = new FishDrawable();
        imageView.setImageDrawable(fishDrawable);
        addView(imageView);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setAlpha(alpha);
        canvas.drawCircle(touchX, touchY, ripper * 150, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        touchX = event.getX();
        touchY = event.getY();

        ObjectAnimator objectAnimator = ObjectAnimator
                .ofFloat(this, "ripper", 0, 1f).setDuration(1000);
        objectAnimator.start();

        makeTrail();

        return super.onTouchEvent(event);
    }

    private void makeTrail() {
        PointF fishRelativeMiddle = fishDrawable.getMiddlePoint();
        PointF headPoint = fishDrawable.getHeadPoint();

        //相对于屏幕的起始点结束点
        //起始点
        PointF fishMiddle = new PointF(imageView.getX() + fishRelativeMiddle.x, imageView.getY() + fishRelativeMiddle.y);
        //结束点
        PointF touch = new PointF(touchX, touchY);

        //计算控制点(向量夹角计算公式)
        //第一个控制点
        PointF fishHead = new PointF(imageView.getX() + headPoint.x, imageView.getY() + headPoint.y);
        float angle = includeAngle(fishMiddle, fishHead, touch);
        float delta = includeAngle(fishMiddle, new PointF(fishMiddle.x +1, fishMiddle.y), fishHead);
        //第二个控制点
        PointF controlPoint = FishDrawable.calculatePoint(fishMiddle,
                FishDrawable.HEAD_RADIUS * 1.6f, angle/2 + delta);

        //开始移动
        final Path path = new Path();
        path.moveTo(fishMiddle.x - fishRelativeMiddle.x, fishMiddle.y - fishRelativeMiddle.y);
        path.cubicTo(fishHead.x - fishRelativeMiddle.x, fishHead.y - fishRelativeMiddle.y,
                controlPoint.x - fishRelativeMiddle.x, controlPoint.y - fishRelativeMiddle.y,
                touchX - fishRelativeMiddle.x, touchY - fishRelativeMiddle.y);

        ObjectAnimator objectAnimator = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            objectAnimator = ObjectAnimator.ofFloat(imageView, "x", "y", path);
        }
        objectAnimator.setDuration(2000);
        //设置鱼头按path切线角度转向
        final PathMeasure pathMeasure = new PathMeasure(path, false);
        final float[] tan = new float[2];
        objectAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float fraction = animation.getAnimatedFraction();
                pathMeasure.getPosTan(pathMeasure.getLength() * fraction, null, tan);
                float angle = (float) Math.toDegrees(Math.atan2(-tan[1], tan[0]));
                fishDrawable.setFishMainAngle(angle);
            }
        });
        objectAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation, boolean isReverse) {
                FishDrawable.bodyRepeatFrequency = 4;
            }
            @Override
            public void onAnimationEnd(Animator animation) {
                FishDrawable.bodyRepeatFrequency = 2;
            }
        });
        objectAnimator.start();
    }

    /**
     * 计算两条线的夹角角度
     * @param O 中心点
     * @param A 第一个控制点
     * @param B 结束点
     * @return
     */
    public static float includeAngle(PointF O, PointF A, PointF B) {
        // OA*OB=(Ax-Ox)*(Bx-Ox)+(Ay-Oy)*(By-Oy)
        float AOB = (A.x - O.x)*(B.x - O.x) + (A.y -O.y)*(B.y - O.y);
        // OA的长度
        float OALength = (float)Math.sqrt((A.x - O.x)*(A.x - O.x) + (A.y - O.y)*(A.y - O.y));
        // OB的长度
        float OBLength = (float)Math.sqrt((B.x - O.x)*(B.x - O.x) + (B.y - O.y)*(B.y - O.y));
        // cosAOB = (OA*OB)/(|OA|*|OB|)
        float cosAOB = AOB / (OALength * OBLength);

        // toDegrees：将弧度转为度数。Math.acos：反余弦。angleAOB：计算得出AOB的角度大小
        float angleAOB = (float)Math.toDegrees(Math.acos(cosAOB));
        //判断方向  正左侧 负右侧 0线上
        //AB与X轴的夹角的tan值 - OB与X轴的夹角的tan值 --> 角度是直角三角形里面的，肯定是0-90度，tan角度越大值越大
        float direction = (A.y - B.y)/(A.x - B.x) - (O.y - B.y)/(O.x - B.x);
        if(direction == 0) {
            if(AOB >= 0) {
                return 0;
            } else {
                return 180;
            }
        } else {
            if(direction > 0) {
                return -angleAOB;
            } else {
                return angleAOB;
            }
        }
    }

    public void setRipper(float ripper) {
        alpha = (int) (150 * (1 - ripper));
        this.ripper = ripper;
        invalidate();
    }
}
