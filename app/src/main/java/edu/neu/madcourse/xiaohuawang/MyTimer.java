package edu.neu.madcourse.xiaohuawang;

/**
 * Created by yangyangyy on 10/17/17.
 */

import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.Chronometer;
import android.widget.Toast;


public class MyTimer extends Chronometer {
    public MyTimer(Context context, AttributeSet attrs) {
        super(context, attrs);

        mTimeFormat = new SimpleDateFormat("mm:ss");
        this.setOnChronometerTickListener(listener);
    }

    private long mTime;
    private long mNextTime;
    private OnTimeCompleteListener mListener;
    private SimpleDateFormat mTimeFormat;
    private GameFragment mGame=new GameFragment();

    public MyTimer(Context context) {
        super(context);

    }

    /**
     * restart count down from begining
     */
    public void reStart(long _time_s) {
        if (_time_s == -1) {
            mNextTime = mTime;
        } else {
            mTime = mNextTime = _time_s;
        }
        this.start();
    }

    public void reStart() {
        reStart(-1);
    }

    /**
     * resume countdown
     */
    public void onResume() {
        this.start();
    }

    /**
     * stop countdown
     */
    public void onPause() {
        this.stop();
    }

    /**
     * set time format
     *
     * @param pattern
     */
    public void setTimeFormat(String pattern) {
        mTimeFormat = new SimpleDateFormat(pattern);
    }

    public void setOnTimeCompleteListener(OnTimeCompleteListener l) {
        mListener = l;
    }


    OnChronometerTickListener listener = new OnChronometerTickListener() {
        @Override
        public void onChronometerTick(Chronometer chronometer) {
            if (mNextTime <= 0) {
                if (mNextTime == 0) {
                    MyTimer.this.stop();
                    if (null != mListener)
                        mListener.onTimeComplete();
                }
                mNextTime = 0;
                updateTimeText();
                return;
            }

            mNextTime--;

            updateTimeText();

//            //stop at 01:30 for phase1
//            if (chronometer.getText().toString().equals("02:50")) {
//                chronometer.stop();

//
////                mGame.phase2Begin();
//                String phase2_begin = mGame.getString(R.string.phase2_begin);
//                Toast.makeText(mGame.getActivity(), phase2_begin, Toast.LENGTH_SHORT).show();
//            }
//
//            //stop at 00:00 for phase2
//            if (chronometer.getText().toString().equals("00:00")) {
//                chronometer.setText("Time up!");
//                chronometer.stop();
//                String phase2_end = mGame.getString(R.string.phase2_end);
//                Toast.makeText(mGame.getActivity(), phase2_end, Toast.LENGTH_SHORT).show();
////                mGame.GameEnd();
//            }

        }
    };

    /**
     * initial time
     *
     * @param _time_s
     */
    public void initTime(long _time_s) {
        mTime = mNextTime = _time_s;
        updateTimeText();
    }

    private void updateTimeText() {
        this.setText(mTimeFormat.format(new Date(mNextTime * 1000)));
    }

    interface OnTimeCompleteListener {
        void onTimeComplete();
    }
}

