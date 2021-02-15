package com.md.moktype.ui.activity.setting;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.gson.Gson;
import com.md.moktype.R;
import com.md.moktype.network.ApiCallback;
import com.md.moktype.network.ApiService;
import com.md.moktype.network.data.request.AlarmReq;
import com.md.moktype.network.data.BaseReq;
import com.md.moktype.network.data.response.AlarmRes;
import com.md.moktype.ui.activity.BaseActivity;
import com.md.moktype.ui.popup.MessageDialog;
import com.md.moktype.utils.Formatter;
import com.md.moktype.utils.LogUtil;

import org.json.JSONObject;

public class AlarmSettingActivity extends BaseActivity implements CheckBox.OnCheckedChangeListener, View.OnClickListener {

    private CheckBox ckbPushAll, ckbNotice, ckbChallenge, ckbQna, ckbEvent, ckbQuickPoll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_alarm);

        ckbPushAll = findViewById(R.id.ckb_push_all);
        ckbPushAll.setOnClickListener(this);

        ckbNotice = findViewById(R.id.ckb_notice);
        ckbNotice.setOnClickListener(this);
        ckbNotice.setOnCheckedChangeListener(this);
        ckbChallenge = findViewById(R.id.ckb_challenge);
        ckbChallenge.setOnClickListener(this);
        ckbChallenge.setOnCheckedChangeListener(this);
        ckbQna = findViewById(R.id.ckb_qna);
        ckbQna.setOnClickListener(this);
        ckbQna.setOnCheckedChangeListener(this);
        ckbEvent = findViewById(R.id.ckb_event);
        ckbEvent.setOnClickListener(this);
        ckbEvent.setOnCheckedChangeListener(this);
        ckbQuickPoll = findViewById(R.id.ckb_quick_poll);
        ckbQuickPoll.setOnClickListener(this);
        ckbQuickPoll.setOnCheckedChangeListener(this);

        requestAlarmSetting();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ckb_push_all:
                boolean check = ckbPushAll.isChecked();

                ckbNotice.setChecked(check);
                ckbChallenge.setChecked(check);
                ckbQna.setChecked(check);
                ckbEvent.setChecked(check);
                ckbQuickPoll.setChecked(check);

                sendAlarmSetting(true, check);
                break;
            case R.id.ckb_notice:
            case R.id.ckb_challenge:
            case R.id.ckb_qna:
            case R.id.ckb_event:
            case R.id.ckb_quick_poll:
                sendAlarmSetting(false, false);
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton view, boolean isChecked) {
        switch (view.getId()) {
            case R.id.ckb_notice:
            case R.id.ckb_challenge:
            case R.id.ckb_qna:
            case R.id.ckb_event:
            case R.id.ckb_quick_poll:
                if(ckbNotice.isChecked() && ckbChallenge.isChecked() && ckbQna.isChecked() && ckbEvent.isChecked() && ckbQuickPoll.isChecked()) {
                    ckbPushAll.setChecked(true);
                }else{
                    ckbPushAll.setChecked(false);
                }
                break;
        }
    }

    /**
     * 알람 설정 가져오기
     */
    private void requestAlarmSetting() {
        showLoading();

        //requestAlarmSetting param
        BaseReq req = new BaseReq();
        req.setCMD("requestAlarmSetting");

        //requestAlarmSetting call
        ApiService.getInstance().requestAlarmSetting(req).enqueue(new ApiCallback<Object>() {
            @Override
            public void onSuccess(@NonNull JSONObject object) {
                hideLoading();
                AlarmRes alarmRes = new Gson().fromJson(object.toString(), AlarmRes.class);
                LogUtil.e("TEST", "requestAlarmSetting() onSuccess = " + alarmRes.toString());

                ckbNotice.setChecked(alarmRes.isNoticeAlarm());
                ckbChallenge.setChecked(alarmRes.isChallengeAlarm());
                ckbQna.setChecked(alarmRes.isAnswerAlarm());
                ckbEvent.setChecked(alarmRes.isEventAlarm());
                ckbQuickPoll.setChecked(alarmRes.isQuizAlarm());
            }

            @Override
            public void onFail(int code, @Nullable String message, @NonNull Throwable t) {
                hideLoading();
                LogUtil.e("TEST", "requestAlarmSetting() onFailure [" + code + "] " + message);
                Toast.makeText(AlarmSettingActivity.this, "서버와의 통신에 실패했습니다. 잠시 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 알람 설정 결과 전송
     * @param isAll - 전체 체크박스 클릭여부
     * @param isAgree - 전체 동의결과
     */
    private void sendAlarmSetting(final boolean isAll, final boolean isAgree) {
        showLoading();

        //sendAlarmSetting param
        AlarmReq req = new AlarmReq();
        req.setCMD("sendAlarmSetting");
        req.setNoticeAlarm(ckbNotice.isChecked());
        req.setChallengeAlarm(ckbChallenge.isChecked());
        req.setAnswerAlarm(ckbQna.isChecked());
        req.setEventAlarm(ckbEvent.isChecked());
        req.setQuizAlarm(ckbQuickPoll.isChecked());

        //sendAlarmSetting call
        ApiService.getInstance().sendAlarmSetting(req).enqueue(new ApiCallback<Object>() {
            @Override
            public void onSuccess(@NonNull JSONObject object) {
                hideLoading();
                LogUtil.e("TEST", "sendAlarmSetting() onSuccess = " + object.toString());
                if(isAll)
                    showPushAgreeTime(isAgree);
            }

            @Override
            public void onFail(int code, @Nullable String message, @NonNull Throwable t) {
                hideLoading();
                LogUtil.e("TEST", "sendAlarmSetting() onFailure [" + code + "] " + message);
                Toast.makeText(AlarmSettingActivity.this, "알람 설정 전송에 실패했습니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * 알람 설정 결과 팝업
     * @param isAgree
     */
    public void showPushAgreeTime(final boolean isAgree) {
        new MessageDialog.Builder(this)
                .setMessage(isAgree ? getString(R.string.alert_push_agree_time, Formatter.getNow()) : getString(R.string.alert_push_disagree_time, Formatter.getNow()))
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).build().show();
    }

    /**
     * 백버튼
     * @param view
     */
    public void clickBackBtn(View view) {
        finish();
    }
}
