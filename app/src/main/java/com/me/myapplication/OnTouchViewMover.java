package com.me.myapplication;

import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;


public class OnTouchViewMover implements OnTouchListener {
    public static String TAG = OnTouchViewMover.class.getSimpleName();

    private Handler handler;
    private Runnable runnableDelay;
    private long delay;
    private float dX, dY;
    private float x, y;
    private boolean performClick;

    private View viewToMove;    // 滑动按键
    private int[] viewToMoveSize;   // 滑动按键大小
    private ViewGroup mFollowViewsParent; // 菜单栏
    private int[] mFollowViewsParentSize; // 菜单栏大小
    private View[] mFollowViews;    // 菜单子控件
    /**
     * 菜单子控件 {@link #mFollowViews} 的大小参数：宽，高，leftMargin，rightMargin
     */
    private int[][] mFollowViewsSize;
    private int[] mScreenSize;  // 屏幕大小 px
    //private int[] mBasicPoint = new int[2];  // 核心控件绝对位置，基准原点
    /**
     * true：滑动按键在左半屏，菜单栏从右边弹出
     * false：滑动按键在右半屏，菜单栏从左边弹出
     */
    private boolean mMenuBarLocation = true;
    private ValueAnimator.AnimatorUpdateListener mAnimatorUpdateListener;
    private int mStatusBarHeight;


    public OnTouchViewMover(View viewToMove, Handler handler, Runnable runnableDelay, long delay) {
        this.viewToMove = viewToMove;
        this.handler = handler;
        this.runnableDelay = runnableDelay;
        this.delay = delay;
    }

    public OnTouchViewMover(View viewToMove) {
        this(viewToMove, null, null, 0);
    }

    public void setFollowViews(ViewGroup parent, View... followViews) {
        mFollowViewsParent = parent;
        mFollowViews = followViews;
        initSize(parent.getContext());
        //initAnim(parent.getContext());
    }

    private void initAnim(Context context) {
        mAnimatorUpdateListener = animator -> {
            int x = (int) animator.getAnimatedValue();
            ///
        };
    }

    private void initSize(Context context) {
        viewToMoveSize = ViewUtil.getSize(viewToMove);
        mFollowViewsParentSize = ViewUtil.getSize(mFollowViewsParent);
        mFollowViewsSize = new int[mFollowViews.length][4];
        for (int i = 0; i < mFollowViews.length; i++) {
            int[] size = ViewUtil.getSize(mFollowViews[i]);
            mFollowViewsSize[i][0] = size[0];
            mFollowViewsSize[i][1] = size[1];
        }
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        mScreenSize = new int[]{dm.widthPixels, dm.heightPixels};

        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            mStatusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
        } else {
            mStatusBarHeight = ViewUtil.dip2px(viewToMove.getContext(), 24);
        }

        //onTaskStatusChange(true, false, false, true);
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        //Log.i(TAG, "Moving toolbar " + event);
        if (handler != null) {
            handler.removeCallbacks(runnableDelay);
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x = event.getRawX();
                y = event.getRawY();
                dX = viewToMove.getX() - x;
                dY = viewToMove.getY() - y;
                performClick = true;
                break;
            case MotionEvent.ACTION_MOVE:
                float x = event.getRawX() + dX;
                float y = event.getRawY() + dY;
                viewToMove.setX(x);
                viewToMove.setY(y);
                if (mMenuBarLocation) {
                    mFollowViewsParent.setX(x);
                    mFollowViewsParent.setY(y);
                } else {
                    mFollowViewsParent.setX(x - mFollowViewsParentSize[0] + viewToMoveSize[0]);
                    mFollowViewsParent.setY(y);
                }
                checkClick(event);
                /*if (!performClick && mFollowViewsParent.getVisibility() != View.GONE) {
                    mFollowViewsParent.setVisibility(View.GONE);
                }*/
                break;
            case MotionEvent.ACTION_UP:
                checkClick(event);

                boolean b1 = viewToMove.getX() <= (mScreenSize[0] >>> 1);
                if (b1 != mMenuBarLocation) {
                    mMenuBarLocation = b1;
                    onTaskStatusChange();
                }

                if (performClick) {
                    if (!view.performClick()) {
                        Log.w(TAG, "performClick return false");
                    }
                } else {
                    float tX = 0;
                    if (mMenuBarLocation) {
                        tX = mStatusBarHeight;
                    } else {
                        tX = mScreenSize[0] - viewToMoveSize[0] - mStatusBarHeight;
                    }
                    float tY = viewToMove.getY();
                    if (viewToMove.getY() <= 0) {
                        tY = 0;
                    } else if (viewToMove.getY() >= mScreenSize[1] - viewToMoveSize[1] - mStatusBarHeight + viewToMoveSize[0]) {
                        tY = mScreenSize[1] - viewToMoveSize[1] - mStatusBarHeight;
                    }
                    viewToMove.animate().x(tX).y(tY).setDuration(300).start();

                    if (mMenuBarLocation) {
                        mFollowViewsParent.animate().x(tX).y(tY).setDuration(300).start();
                    } else {
                        mFollowViewsParent.animate().x(tX - mFollowViewsParentSize[0] + viewToMoveSize[0]).y(tY).setDuration(300).start();
                    }
                }
                postRunnable();
                break;
            default:
                postRunnable();
                return false;
        }
        return true;
    }

    private void postRunnable() {
        if (handler != null && null != runnableDelay) {
            handler.postAtTime(runnableDelay, SystemClock.uptimeMillis() + delay);
            Log.i(TAG, "post runnable delay " + delay);
        }
    }

    private void checkClick(MotionEvent event) {
        if (performClick) {
            float rawX = event.getRawX();
            float rawY = event.getRawY();
            performClick = !(Math.abs(rawX - x) > 3.0 || Math.abs(rawY - y) > 3.0);
            x = rawX;
            y = rawY;
        }
        //Log.i(TAG, "checkClick --> performClick " + performClick);
    }

    /**
     * 1. 设置菜单子控件的可见性；
     * 2. 根据滑动按键左右位置，配置菜单子控件的 margin值；
     * 3. 测量菜单栏总大小；
     * 4. 根据滑动按键左右位置，设置菜单栏位置；
     *
     * @param visibilities 菜单子控件 {@link #mFollowViews} 的可见性配置，默认 true：View.VISIBLE。
     */
    public void onTaskStatusChange(boolean... visibilities) {

        if (visibilities.length > 0 && visibilities[0] == viewToMove.isSelected()) {
            viewToMove.setSelected(!visibilities[0]);
        }

        for (int i = 0; i < mFollowViews.length && i < visibilities.length; i++) {
            int vi = visibilities[i] ? View.VISIBLE : View.GONE;
            if (mFollowViews[i].getVisibility() != vi) {
                mFollowViews[i].setVisibility(vi);
            }
        }


        int childrenWidth = 0;

        if (mMenuBarLocation) {
            for (int i = 0; i < mFollowViews.length; i++) { // ABCD
                View view = mFollowViews[i];
                ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                if (view.getVisibility() != View.VISIBLE) {
                    mFollowViewsSize[i][2] = mlp.leftMargin = 0;
                    mFollowViewsSize[i][3] = mlp.rightMargin = 0;
                    continue;
                }

                mFollowViewsSize[i][2] = mlp.leftMargin = childrenWidth + viewToMoveSize[0] - mFollowViewsParent.getPaddingLeft();
                mFollowViewsSize[i][3] = mlp.rightMargin = 0;

                childrenWidth += mFollowViewsSize[i][0];
            }


        } else {
            for (int i = mFollowViews.length - 1; i >= 0; i--) { // DCBA
                View view = mFollowViews[i];
                ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                if (view.getVisibility() != View.VISIBLE) {
                    mFollowViewsSize[i][2] = mlp.leftMargin = 0;
                    mFollowViewsSize[i][3] = mlp.rightMargin = 0;
                    continue;
                }

                mFollowViewsSize[i][2] = mlp.leftMargin = childrenWidth;
                mFollowViewsSize[i][3] = mlp.rightMargin = 0;

                childrenWidth += mFollowViewsSize[i][0];
            }
            for (int i = 0; i < mFollowViews.length; i++) { // ABCD
                View view = mFollowViews[i];
                if (view.getVisibility() == View.VISIBLE) {
                    ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                    mFollowViewsSize[i][3] = mlp.rightMargin = viewToMoveSize[0] - mFollowViewsParent.getPaddingRight();
                    break;
                }
            }
        }

        mFollowViewsParent.requestLayout();
        mFollowViewsParent.measure(0, 0);
        mFollowViewsParentSize[0] = mFollowViewsParent.getMeasuredWidth();


        if (mMenuBarLocation) {
            mFollowViewsParent.setX(viewToMove.getX());
            mFollowViewsParent.setY(viewToMove.getY());
        } else {
            mFollowViewsParent.setX(viewToMove.getX() - mFollowViewsParentSize[0] + viewToMoveSize[0]);
            mFollowViewsParent.setY(viewToMove.getY());
        }
    }
}
