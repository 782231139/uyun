package com.uyun.hummer.zxing.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.google.zxing.ResultPoint;
import com.uyun.hummer.R;
import com.uyun.hummer.zxing.camera.CameraManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * Created by yangyu on 17/10/30.
 */

public class ScannerView extends View {
    private static final int[] SCANNER_ALPHA = {0, 64, 128, 192, 255, 192, 128, 64};
    private static final long ANIMATION_DELAY = 90L;
    private Paint mBgPaint;
    private Paint mCornerPaint;
    private Paint mFocusFramePaint;
    private Paint mTipPaint;
    private Paint mLaserPaint;
    private CameraManager cameraManager;

    private int mCornerLength;
    private int mCornerThick;
    private int mTipPaddingTop;
    private int mFocusLineThick;
    private int scannerAlpha;

    private int slideTop;
    private boolean isFirst;
    private List<ResultPoint> possibleResultPoints;
    private List<ResultPoint> lastPossibleResultPoints;
    private static float density;
    private static final int OPAQUE = 0xFF;

    public ScannerView(Context context) {
        this(context, null);
    }

    public ScannerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        density = context.getResources().getDisplayMetrics().density;
        init();
    }

    /**
     * 一些初始化操作
     */
    private void init() {
        mBgPaint = new Paint();
        mCornerPaint = new Paint();
        mFocusFramePaint = new Paint();
        mTipPaint = new Paint();
        mLaserPaint = new Paint();

        mBgPaint.setColor(getResources().getColor(R.color.scan_view_bg));
        mBgPaint.setAntiAlias(true);

        mCornerPaint.setAntiAlias(true);
        mCornerPaint.setColor(getResources().getColor(R.color.scan_frame_green_color));

        mFocusFramePaint.setAntiAlias(true);
        mFocusFramePaint.setColor(getResources().getColor(R.color.scan_frame_green_color));
        mFocusFramePaint.setAlpha(60);

        mTipPaint.setAntiAlias(true);
        mTipPaint.setTextSize(getResources().getDimensionPixelSize(R.dimen.scanner_view_tip_size));
        mTipPaint.setColor(getResources().getColor(R.color.scan_view_tip_color));

        mLaserPaint.setAntiAlias(true);
        mLaserPaint.setColor(getResources().getColor(R.color.scan_line_color));

        mCornerLength = getResources().getDimensionPixelSize(R.dimen.scanner_view_corner_width);
        mCornerThick = getResources().getDimensionPixelSize(R.dimen.scanner_view_corner_thick);
        mTipPaddingTop = getResources().getDimensionPixelSize(R.dimen.scanner_view_tip_top);
        mFocusLineThick = getResources().getDimensionPixelSize(R.dimen.scanner_view_focus_line_thick);
        scannerAlpha = 0;

    }

    public void setCameraManager(CameraManager cameraManager) {
        this.cameraManager = cameraManager;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (cameraManager == null) return;
        Rect mScannerRect = cameraManager.getFramingRect();
        if (mScannerRect == null) return;
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        canvas.drawRect(0, 0, width, mScannerRect.top, mBgPaint);
        canvas.drawRect(0, mScannerRect.top, mScannerRect.left, mScannerRect.bottom + 1, mBgPaint);
        canvas.drawRect(mScannerRect.right + 1, mScannerRect.top, width, mScannerRect.bottom + 1, mBgPaint);
        canvas.drawRect(0, mScannerRect.bottom + 1, width, height, mBgPaint);
        drawCorner(canvas, mScannerRect);
        drawFocusRect(canvas, mScannerRect);
        drawTipText(canvas, getResources().getDisplayMetrics().widthPixels, mScannerRect.bottom + mTipPaddingTop);
        drawLaser(canvas, mScannerRect);
        //实现动画效果
        postInvalidateDelayed(ANIMATION_DELAY, mScannerRect.left, mScannerRect.top, mScannerRect.right, mScannerRect.bottom);

    }

    /**
     * 绘制矩形框的四个角
     *
     * @param canvas
     * @param rect
     */
    private void drawCorner(Canvas canvas, Rect rect) {
        if (rect == null) return;
        //绘制左上角
        canvas.drawRect(rect.left, rect.top, rect.left + mCornerLength, rect.top + mCornerThick, mCornerPaint);
        canvas.drawRect(rect.left, rect.top, rect.left + mCornerThick, rect.top + mCornerLength, mCornerPaint);

        //绘制左下角
        canvas.drawRect(rect.left, rect.bottom - mCornerThick, rect.left + mCornerLength, rect.bottom, mCornerPaint);
        canvas.drawRect(rect.left, rect.bottom - mCornerLength, rect.left + mCornerThick, rect.bottom, mCornerPaint);

        //绘制右上角
        canvas.drawRect(rect.right - mCornerLength, rect.top, rect.right, rect.top + mCornerThick, mCornerPaint);
        canvas.drawRect(rect.right - mCornerThick, rect.top, rect.right, rect.top + mCornerLength, mCornerPaint);

        //绘制右下角
        canvas.drawRect(rect.right - mCornerLength, rect.bottom - mCornerThick, rect.right, rect.bottom, mCornerPaint);
        canvas.drawRect(rect.right - mCornerThick, rect.bottom - mCornerLength, rect.right, rect.bottom, mCornerPaint);
    }

    /**
     * 绘制聚焦框
     */
    private void drawFocusRect(Canvas canvas, Rect rect) {
        canvas.drawRect(rect.left + mCornerLength, rect.top, rect.right - mCornerLength, rect.top + mFocusLineThick, mFocusFramePaint);
        canvas.drawRect(rect.right - mFocusLineThick, rect.top + mCornerLength, rect.right, rect.bottom - mCornerLength, mFocusFramePaint);
        canvas.drawRect(rect.left + mCornerLength, rect.bottom - mFocusLineThick, rect.right - mCornerLength, rect.bottom, mFocusFramePaint);
        canvas.drawRect(rect.left, rect.top + mCornerLength, rect.left + mFocusLineThick, rect.bottom - mCornerLength, mFocusFramePaint);
    }

    //绘制提示语
    private void drawTipText(Canvas canvas, int w, int h) {
        String tip = getResources().getString(R.string.scanner_view_tip_text);
        float l = (w - tip.length() * mTipPaint.getTextSize()) / 2;
        canvas.drawText(tip, l, h, mTipPaint);
    }

    /**
     * 绘制激光线
     */
    private void drawLaser(Canvas canvas, Rect rect) {
        /*mLaserPaint.setAlpha(SCANNER_ALPHA[scannerAlpha]);
        scannerAlpha = (scannerAlpha + 1) % SCANNER_ALPHA.length;
        int middle = rect.height() / 2 + rect.top;
        canvas.drawRect(rect.left + 2, middle - 1, rect.right - 1, middle + 2, mLaserPaint);*/
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        possibleResultPoints = new ArrayList<ResultPoint>(5);
        lastPossibleResultPoints = null;
        if (!isFirst) {
            isFirst = true;
            slideTop = rect.top - 5;
        }
        slideTop += 5;
        if (slideTop >= rect.bottom - 10) {
            slideTop = rect.top - 5;
        }
        Rect lineRect = new Rect();
        lineRect.left = rect.left;
        lineRect.right = rect.right;
        lineRect.top = slideTop;
        lineRect.bottom = slideTop + 3;
        canvas.drawBitmap(((BitmapDrawable) (getResources()
                        .getDrawable(R.drawable.qrcode_scan_line))).getBitmap(),
                null, lineRect, mLaserPaint);
        mLaserPaint.setColor(Color.WHITE);
        mLaserPaint.setTextSize(15 * density);
        mLaserPaint.setAlpha(0xee);
        mLaserPaint.setTypeface(Typeface.create("System", Typeface.BOLD));
            /*canvas.drawText(getResources().getString(R.string.scan_qrcode),
                    rect.left - 20, (float) (rect.bottom + (float) 40
                            * density), mLaserPaint);*/

        List<ResultPoint> currentPossible = possibleResultPoints;
        Collection<ResultPoint> currentLast = lastPossibleResultPoints;
        if (currentPossible.isEmpty()) {
            lastPossibleResultPoints = null;
        } else {
            possibleResultPoints = new ArrayList<ResultPoint>(5);
            lastPossibleResultPoints = currentPossible;
            mLaserPaint.setAlpha(OPAQUE);
            //mLaserPaint.setColor(resultPointColor);
            for (ResultPoint point : currentPossible) {
                canvas.drawCircle(rect.left + point.getX(), rect.top
                        + point.getY(), 6.0f, mLaserPaint);
            }
        }
        if (currentLast != null) {
            mLaserPaint.setAlpha(OPAQUE / 2);
            //mLaserPaint.setColor(resultPointColor);
            for (ResultPoint point : currentLast) {
                canvas.drawCircle(rect.left + point.getX(), rect.top
                        + point.getY(), 3.0f, mLaserPaint);
            }
        }
        postInvalidateDelayed(10L, 0, 0, width, height);
    }


}
