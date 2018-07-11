package com.me.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private OnTouchViewMover mFloatMenuMover;
    private ImageView mIvFloat;
    private FrameLayout mLayoutMenu;
    private TextView mIvStart, mIvStop, mIvSwitch, mIvExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mIvFloat = findViewById(R.id.iv_float);
        mIvFloat.setOnClickListener(v -> mLayoutMenu.setVisibility(mLayoutMenu.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE));
        mLayoutMenu = findViewById(R.id.layout_menu);
        mIvStart = findViewById(R.id.iv_start);
        mIvStart.setOnClickListener(v -> Toast.makeText(this, "AAA", Toast.LENGTH_SHORT).show());
        mIvStop = findViewById(R.id.iv_stop);
        mIvStop.setOnClickListener(v -> Toast.makeText(this, "BBB", Toast.LENGTH_SHORT).show());
        mIvSwitch = findViewById(R.id.iv_switch);
        mIvSwitch.setOnClickListener(v -> Toast.makeText(this, "CCC", Toast.LENGTH_SHORT).show());
        mIvExit = findViewById(R.id.iv_exit);
        mIvExit.setOnClickListener(v -> Toast.makeText(this, "DDD", Toast.LENGTH_SHORT).show());

        mFloatMenuMover = new OnTouchViewMover(mIvFloat);
        mFloatMenuMover.setFollowViews(mLayoutMenu, mIvStart, mIvStop, mIvSwitch, mIvExit);
        mIvFloat.setOnTouchListener(mFloatMenuMover);

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        moveTaskToBack(false);
    }

    public void abcd(View view) {
        mFloatMenuMover.onTaskStatusChange(true, true, true, true);
    }

    public void bcd(View view) {
        mFloatMenuMover.onTaskStatusChange(false, true, true, true);
    }

    public void abc(View view) {
        mFloatMenuMover.onTaskStatusChange(true, false, false, true);
    }

    public void eeee(View view) {
        mFloatMenuMover.onTaskStatusChange(false, false, false, false);
    }
}
