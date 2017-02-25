package com.nopu70.myview;

/**
 * Created by Nopu70 on 2016/2/1.
 */
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Scroller;

import com.nopu70.dict.Dictionary;
import com.nopu70.dict.MainActivity;
import com.nopu70.dict.R;
import com.nopu70.tools.ConnectUrl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PullDoorView extends RelativeLayout {



    private Context mContext;
    private Scroller mScroller;
    private int mScreenWidth = 0;
    private int mScreenHeigh = 0;
    private int mLastDownY = 0;
    private int mCurryY;
    private int mDelY;
    private  Thread thread;
    Bitmap bmp;
    private boolean mCloseFlag = false;
    private ImageView mImgView;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 123){
                mImgView.setImageBitmap(bmp);
            }
        }
    };

    ConnectUrl connectUrl;

    public PullDoorView(Context context) {
        super(context);
        mContext = context;
        setupView();
    }

    public PullDoorView(Context context, AttributeSet attrs) {
        super(context, attrs);;
        mContext = context;
        setupView();
    }

    @SuppressLint("NewApi")
    private void setupView() {

        // 这个Interpolator你可以设置别的 我这里选择的是有弹跳效果的Interpolator
        Interpolator polator = new BounceInterpolator();
        mScroller = new Scroller(mContext, polator);


        // 获取屏幕分辨率
        WindowManager wm = (WindowManager) (mContext
                .getSystemService(Context.WINDOW_SERVICE));
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        mScreenHeigh = dm.heightPixels;
        mScreenWidth = dm.widthPixels;

        // 这里你一定要设置成透明背景,不然会影响你看到底层布局
        this.setBackgroundColor(Color.argb(0, 0, 0, 0));
        mImgView = new ImageView(mContext);
        mImgView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
        mImgView.setScaleType(ImageView.ScaleType.FIT_XY);// 填充整个屏幕
        mImgView.setImageResource(R.drawable.bg1); // 默认背景
        addView(mImgView);


        thread = new Thread(){
            @Override
            public void run() {
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
                String str =  sp.getString("DAILY_PNG", "");
                try {
                    if (str.equals(Dictionary.DATE_NOW)){
                        FileInputStream fis = mContext.openFileInput("daily.png");
                        bmp = BitmapFactory.decodeStream(fis);
                        fis.close();
                        Message msg = new Message();
                        msg.what = 123;
                        handler.sendMessage(msg);
                    } else {
                        connectUrl = new ConnectUrl();
                        bmp = connectUrl.connectToImg();
                        if (bmp!=null){
                            Message msg = new Message();
                            msg.what = 123;
                            handler.sendMessage(msg);
                            FileOutputStream fos = mContext.openFileOutput("daily.png", mContext.MODE_PRIVATE);
                            sp.edit().putString("DAILY_PNG", Dictionary.DATE_NOW).commit();
                            bmp.compress(Bitmap.CompressFormat.PNG, 90, fos);
                            fos.close();
                        }
                    }
                }catch (FileNotFoundException e){
                } catch (IOException e){}
            }
        };
        thread.start();
    }

    // 设置推动门背景
    public void setBgImage(int id) {
        mImgView.setImageResource(id);
    }

    // 设置推动门背景
    public void setBgImage(Drawable drawable) {
        mImgView.setImageDrawable(drawable);
    }

    // 推动门的动画
    public void startBounceAnim(int startY, int dy, int duration) {
        mScroller.startScroll(0, startY, 0, dy, duration);
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastDownY = (int) event.getY();
                System.err.println("ACTION_DOWN=" + mLastDownY);
                return true;
            case MotionEvent.ACTION_MOVE:
                mCurryY = (int) event.getY();
                System.err.println("ACTION_MOVE=" + mCurryY);
                mDelY = mCurryY - mLastDownY;
                // 只准上滑有效
                if (mDelY < 0) {
                    scrollTo(0, -mDelY);
                }
                System.err.println("-------------  " + mDelY);

                break;
            case MotionEvent.ACTION_UP:
                mCurryY = (int) event.getY();
                mDelY = mCurryY - mLastDownY;
                if (mDelY < 0) {

                    if (Math.abs(mDelY) > mScreenHeigh / 2) {

                        // 向上滑动超过半个屏幕高的时候 开启向上消失动画
                        startBounceAnim(this.getScrollY(), mScreenHeigh, 450);
                        mCloseFlag = true;
                    } else {
                        // 向上滑动未超过半个屏幕高的时候 开启向下弹动动画
                        startBounceAnim(this.getScrollY(), -this.getScrollY(), 1000);

                    }
                }

                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void computeScroll() {

        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            Log.i("scroller", "getCurrX()= " + mScroller.getCurrX()
                    + "     getCurrY()=" + mScroller.getCurrY()
                    + "  getFinalY() =  " + mScroller.getFinalY());
            // 不要忘记更新界面
            postInvalidate();
        } else {
            if (mCloseFlag) {
                this.setVisibility(View.GONE);
            }
        }
    }
}
