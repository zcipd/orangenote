package com.orangenote.orangenote;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by PHK on 2015-07-08.
 */
public class signView extends View {

    public Paint mPaint;
    public MaskFilter mEmboss;
    public MaskFilter mBlur;
    public Bitmap mBitmap;
    public Canvas mCanvas;

    public Canvas m2Canvas;
    public boolean mEraserMode;
    public int mPrevAction;

    private Path mPath;
    private Paint mBitmapPaint;
    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 0;
    private ArrayList<Path> paths = new ArrayList<Path>();
    private ArrayList<Path> undonePaths = new ArrayList<Path>();
    private int color;
    int bold;

    public void colorChanged(int color) {
        if (MemoDraw.D) Log.e(getClass().getSimpleName(), "컬러 : " + color);

        mPaint.setColor(color);
    }



    public signView(Context context, AttributeSet attrs) {
        super(context, attrs);

        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels; // 현재 핸드폰의 해상도를 알아냅니다.
        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        //캔버스 도화지의 크기를 해상도 만큼 지정

        mCanvas = new Canvas(mBitmap);
        mPath = new Path();
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);

        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(1);           // 선을 3의 크기로 그립니다.

        mBlur = new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL);
        //MaskFilter를 통해서 선의 스타일을 바꿉니다.
    }

    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.WHITE);
          canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
            if (mEraserMode == false) {
                canvas.drawPath(mPath, mPaint);
            }
        }

     //캔버스의 색, 비트맵을 가져옵니다.

    private void touch_start(float x, float y) {
        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }
    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(x - mY);
        if(dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX,mY, (x + mX) / 2, (y+mY) / 2);
            mX = x;
            mY = y;
        }
    }

    private  void touch_up() {
        mPath.lineTo(mX, mY);
        mCanvas.drawPath(mPath, mPaint);

    }

    public boolean onTouchEvent(MotionEvent event) {
        return draw(mPath,event,0);
    }

    private boolean draw(Path path, MotionEvent event, int type) {
        boolean ret = true;
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                path.reset();
                path.moveTo(x, y);
                mX = x;
                mY = y;
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                path.quadTo(mX, mY, (mX + x) / 2, (mY + y) / 2);
                mX = x;
                mY = y;
                if(mEraserMode == true) {
                    mCanvas.drawPath(path, mPaint);
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                if(mPrevAction == MotionEvent.ACTION_DOWN){
                    mCanvas.drawPoint(x , y, mPaint);
                    invalidate();
                }else {
                    path.quadTo(mX, mY, (mX + x) / 2, (mY + y) / 2);
                    mCanvas.drawPath(mPath, mPaint);
                    mPath = new Path();
                    paths.add(mPath);
                    invalidate();
                }
                break;
            default:
                ret = false;
        }
        mPrevAction = event.getAction();
        return ret;
    }

    public void onClickUndo() {
        if(paths.size() > 0 ) {
           undonePaths.add(paths.remove(paths.size() - 1));
            invalidate();
        } else {

        }
    }

    public void onClickRedo() {
        if (undonePaths.size() > 0) {
            paths.add(undonePaths.remove(undonePaths.size() - 1));
            invalidate();
        } else {

        }
        // toast the user
    }



    class PathPoints {
        private Path path;
        // private Paint mPaint;
        private int color;

        private boolean isTextToDraw;
        private int x, y;

        public PathPoints(Path path, int color ) {
            this.path = path;
            this.color = color;
        }

        public PathPoints(int color,int x, int y) {
            this.color = color;
            this.x = x;
            this.y = y;
        }

        public Path getPath() {
            return path;
        }

        public void setPath(Path path) {
            this.path = path;
        }

		/*
		 * private Paint getPaint() { mPaint = new Paint();
		 * mPaint.setAntiAlias(true); mPaint.setColor(color);
		 * mPaint.setStyle(Paint.Style.STROKE);
		 * mPaint.setStrokeJoin(Paint.Join.ROUND);
		 * mPaint.setStrokeCap(Paint.Cap.ROUND); mPaint.setStrokeWidth(6);
		 * return mPaint; }
		 */

        public int getColor() {
            return color;
        }

        public void setColor(int color) {
            this.color = color;
        }

        public boolean isTextToDraw() {
            return isTextToDraw;
        }

        public void setTextToDraw(boolean isTextToDraw) {
            this.isTextToDraw = isTextToDraw;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

    }
}
