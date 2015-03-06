package de.tabscript.cw.traj;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class WritingView extends View {

    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Path mPath;
    private Paint mBitmapPaint;
    private Paint mPaint;
    private List<Point> mPointList;
    private float mX, mY;
    private float mTouchTolerance = 7;
    private Bitmap mBackgroundImage;

    public WritingView(Context c, AttributeSet as) {
        super(c, as);
        mPointList = new ArrayList<Point>();
        mBackgroundImage = BitmapFactory.decodeResource(getResources(), R.drawable.karopapier);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if(w == 0 || h == 0) {
            return;
        }
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        mPath = new Path();
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(0xFF000000);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(10);
        mBackgroundImage = Bitmap.createScaledBitmap(mBackgroundImage, w, h, false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // canvas.drawColor(0xFFAAAAAA);
        canvas.drawBitmap(mBackgroundImage, 0, 0, null);
        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        canvas.drawPath(mPath, mPaint);
    }

    public void raiseTouchTolerance() {
        mTouchTolerance++;
    }

    public void lowerTouchTolerance() {
        mTouchTolerance--;
        if (mTouchTolerance < 0) {
            mTouchTolerance = 0;
        }
    }

    public float getmTouchTolerance() {
        return mTouchTolerance;
    }

    public List<Point> getPointList() {
        return mPointList;
    }

    public void clearCanvas() {
        mCanvas.drawBitmap(mBackgroundImage, 0, 0, null);
        mPointList.clear();
        invalidate();
    }

    private void touch_start(float x, float y) {
        mPath.reset();
        mPath.moveTo(x, y);

        mX = x;
        mY = y;

        mPointList.add(new Point((int) x, (int) y));
    }

    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= mTouchTolerance || dy >= mTouchTolerance) {
            // mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mPath.lineTo(x, y);
            mX = x;
            mY = y;

            mPointList.add(new Point((int) x, (int) y));
        }
    }

    private void touch_up() {
        mPath.lineTo(mX, mY);
        // commit the path to our offscreen
        mCanvas.drawPath(mPath, mPaint);
        // kill this so we don't double draw
        mPath.reset();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touch_start(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touch_move(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touch_up();
                invalidate();
                break;
        }
        return true;
    }
}