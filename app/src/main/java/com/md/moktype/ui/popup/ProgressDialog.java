package com.md.moktype.ui.popup;

import android.app.Activity;
import android.app.Dialog;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.Window;
import android.widget.ProgressBar;

import com.md.moktype.R;

public class ProgressDialog extends Dialog {

    private final long MAX_TIME = 1000 * 30;
    private final long COUNTDOWN_INTERVAL = MAX_TIME / 100;

    private ProgressBar progressBar;
    private CountDownTimer timer;

    private int count = 0;

    public ProgressDialog(Activity activity){
        super(activity);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_progress_dialog);

        progressBar = findViewById(R.id.progress);
    }

    /**
     * 프로그레스바 시작
     */
    public void startProgress() {
        show();
        count = 0;
        startCountDown();
    }

    /**
     * 프로그레스바 종료
     */
    public void stopProgress() {
        count = 0;
        timer.cancel();
        progressBar.setProgress(100);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                hide();
            }
        }, 300);
    }

    /**
     * 이메일 인증
     * 10분 타이머 시작
     */
    private void startCountDown() {
        timer = new CountDownTimer(MAX_TIME, COUNTDOWN_INTERVAL) {
            public void onFinish() {}
            public void onTick(long millisUntilFinished) {
                progressBar.setProgress(count++);
            }
        }.start();
    }

    /**
     * 타이머 취소
     */
    private void stopCountDown() {
        if(timer != null)
            timer.cancel();
    }
}