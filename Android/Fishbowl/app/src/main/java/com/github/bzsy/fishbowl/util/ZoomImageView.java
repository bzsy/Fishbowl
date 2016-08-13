package com.github.bzsy.fishbowl.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

/**
 * Created by bzsy on 15/8/9.
 */
public class ZoomImageView extends ImageView implements ViewTreeObserver.OnGlobalLayoutListener,
        ScaleGestureDetector.OnScaleGestureListener, View.OnTouchListener {

    protected float minScale = 0f;//最小缩放比例
    protected float clickScale = 1.0f;//双击时的缩放比例
    protected float maxScale = 2.0f;//最大缩放比例
    //以上三个Scale参数的设置在initImage中

    protected Matrix imageMatrix;
    protected boolean hasInit = false;
    protected ScaleGestureDetector scaleGestureDetector;//多点触控
    protected int lastPointCount;//记录多点触控的手指数变化
    protected int scaledTouchSlop;//判断是否触发“移动”的阈值
    protected float lastCenterX;//移动时记录上一次的坐标
    protected float lastCenterY;
    protected boolean canMove;//是否触发移动
    protected GestureDetector gestureDetector;//双击检测
    protected boolean isAutoScaling = false;//是否处在双击缩放状态过程中

    public ZoomImageView(Context context) {
        this(context, null);
    }

    public ZoomImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZoomImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        imageMatrix = new Matrix();
        setScaleType(ScaleType.MATRIX);

        scaleGestureDetector = new ScaleGestureDetector(context, this);
        setOnTouchListener(this);
        scaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if (isAutoScaling) {
                    return true;
                }
                float centerX = e.getX();
                float centerY = e.getY();
                isAutoScaling = true;
                post(new AutoScaleRunnable(centerX, centerY));
                return super.onDoubleTap(e);
            }
        });
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            getViewTreeObserver().removeGlobalOnLayoutListener(this);
        } else {
            getViewTreeObserver().removeOnGlobalLayoutListener(this);
        }
    }

    @Override
    public void onGlobalLayout() {
        if (!hasInit) {
            initImage();
        }
        hasInit = true;
    }

    protected void initImage() {
        Drawable drawable = getDrawable();
        if (drawable == null) {
            return;
        }
        int imageWidth = drawable.getIntrinsicWidth();
        int imageHeight = drawable.getIntrinsicHeight();
        int width = getWidth();
        int height = getHeight();
        minScale = Math.min(width * 1.0f / imageWidth, height * 1.0f / imageHeight);
        int dx = width / 2 - imageWidth / 2;
        int dy = height / 2 - imageHeight / 2;

        imageMatrix.postScale(minScale, minScale, imageWidth / 2, imageHeight / 2);
        imageMatrix.postTranslate(dx, dy);
        setImageMatrix(imageMatrix);
        if (minScale > clickScale) {
            float tempScale = clickScale;
            clickScale = minScale;
            maxScale = minScale * 4;
            minScale = tempScale;
        }
        DebugLog.i(imageWidth + "x" + imageHeight + ", screen:" + width + "x" + height);
        DebugLog.i("minScale:" + minScale + ",maxScale:" + maxScale + ",clickScale:" + clickScale);
    }

    @Override
    public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
        if (getDrawable() == null) {
            return true;
        }
        float currentScale = getCurrentScale();
        float scaleFactor = scaleGestureDetector.getScaleFactor();

        if (currentScale < minScale - 0.01f ||
                currentScale > maxScale + 0.01f) {
            return true;
        }
        float scale = scaleFactor * currentScale;
        if (scale > maxScale) {
            scale = maxScale / currentScale;
        } else if (scale < minScale) {
            scale = minScale / currentScale;
        } else {
            scale = scale / currentScale;
        }
        imageMatrix.postScale(scale, scale, scaleGestureDetector.getFocusX(), scaleGestureDetector.getFocusY());
        adjustImageBorderAndCenterOnScale();
        setImageMatrix(imageMatrix);
        return true;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {

    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
//        if (gestureDetector.onTouchEvent(motionEvent)) {//双击
//            return true;
//        }
        scaleGestureDetector.onTouchEvent(motionEvent);//缩放

        //以下为移动
        float centerX = 0;
        float centerY = 0;
        int pointCount = motionEvent.getPointerCount();
        for (int i = 0; i < pointCount; ++i) {
            centerX += motionEvent.getX(i);
            centerY += motionEvent.getY(i);
        }
        centerX /= pointCount;
        centerY /= pointCount;

        if (lastPointCount != pointCount) {//手指数目变化时，重新判断状态
            canMove = false;
            lastCenterX = centerX;
            lastCenterY = centerY;
        }
        lastPointCount = pointCount;
        RectF rectF = getImageMatrixRectF();
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if (rectF.width() > getWidth() + 0.01f || rectF.height() > getHeight() + 0.01f) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                float dx = centerX - lastCenterX;
                float dy = centerY - lastCenterY;
                if (!canMove) {
                    canMove = (Math.sqrt(dx * dx + dy * dy) > scaledTouchSlop);
                }
                if (canMove) {
                    rectF = getImageMatrixRectF();
                    int width = getWidth();
                    int height = getHeight();
                    if (rectF.width() < width) {
                        dx = 0;//小于控件不能移动
                    } else {//移动时进行边界检测
                        if (rectF.left > 0) {
                            dx = 0;
                        } else if (rectF.left + dx > 0) {
                            dx = -rectF.left;
                        }
                        if (rectF.right < width) {
                            dx = 0;
                        } else if (rectF.right + dx < width) {
                            dx = width - rectF.right;
                        }
                    }
                    if (rectF.height() < height) {
                        dy = 0;
                    } else {
                        if (rectF.top > 0) {
                            dy = 0;
                        } else if (rectF.top + dy > 0) {
                            dy = -rectF.top;
                        }
                        if (rectF.bottom < height) {
                            dy = 0;
                        } else if (rectF.bottom + dy < height) {
                            dy = height - rectF.bottom;
                        }
                    }
                    imageMatrix.postTranslate(dx, dy);
                    setImageMatrix(imageMatrix);
                }
                lastCenterX = centerX;
                lastCenterY = centerY;
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                lastPointCount = 0;
                break;
            case MotionEvent.ACTION_DOWN:
                if (rectF.width() > getWidth() + 0.01f || rectF.height() > getHeight() + 0.01f) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 获取当前的图像缩放比例
     *
     * @return
     */
    protected float getCurrentScale() {
        float[] values = new float[9];
        imageMatrix.getValues(values);
        return values[Matrix.MSCALE_X];
    }

    /**
     * 获取当前的图像位置和宽高
     *
     * @return
     */
    protected RectF getImageMatrixRectF() {
        RectF rectF = new RectF();
        Drawable drawable = getDrawable();
        if (drawable != null) {
            rectF.set(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            imageMatrix.mapRect(rectF);
        }
        return rectF;
    }

    /**
     * 防止缩放时因图像偏移而出现缝隙
     */
    protected void adjustImageBorderAndCenterOnScale() {
        RectF rectF = getImageMatrixRectF();
        float dx = 0;
        float dy = 0;
        int width = getWidth();
        int height = getHeight();

        if (rectF.width() >= width) {
            if (rectF.left > 0) {
                dx = -rectF.left;
            } else if (rectF.right < width) {
                dx = width - rectF.right;
            }
        } else {
            dx = width / 2 - rectF.right + rectF.width() / 2;

        }

        if (rectF.height() >= height) {
            if (rectF.top > 0) {
                dy = -rectF.top;
            } else if (rectF.bottom < height) {
                dy = height - rectF.bottom;
            }
        } else {
            dy = height / 2 - rectF.bottom + rectF.height() / 2;
        }
        imageMatrix.postTranslate(dx, dy);
    }

    private class AutoScaleRunnable implements Runnable {
        private float centerX;
        private float centerY;
        private final float SCALE_BIGGER = 1.1f;
        private final float SCALE_SMALLER = 0.9f;
        private float scale;
        private float currentScale;

        public AutoScaleRunnable(float centerX, float centerY) {
            this.centerX = centerX;
            this.centerY = centerY;
            scale = SCALE_BIGGER;
            if (getCurrentScale() > minScale + 0.01f) {
                scale = SCALE_SMALLER;
            }
        }

        @Override
        public void run() {
            imageMatrix.postScale(scale, scale, centerX, centerY);
            adjustImageBorderAndCenterOnScale();
            setImageMatrix(imageMatrix);
            currentScale = getCurrentScale();
            if ((currentScale > minScale && scale == SCALE_SMALLER) ||
                    (currentScale < clickScale && scale == SCALE_BIGGER)) {
                postDelayed(this, 1);
            } else {
                if (scale == SCALE_BIGGER) {
                    imageMatrix.postScale(clickScale / currentScale, clickScale / currentScale, centerX, centerY);
                } else {
                    imageMatrix.postScale(minScale / currentScale, minScale / currentScale, centerX, centerY);
                }
                adjustImageBorderAndCenterOnScale();
                setImageMatrix(imageMatrix);
                isAutoScaling = false;
            }
        }
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        if (hasInit) {//下载完大图的时候重新初始化
            imageMatrix = new Matrix();
            initImage();
        }
    }
}
