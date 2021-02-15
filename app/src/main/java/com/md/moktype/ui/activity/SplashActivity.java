package com.md.moktype.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.md.moktype.BuildConfig;
import com.md.moktype.common.Constants;
import com.md.moktype.R;
import com.md.moktype.network.ApiCallback;
import com.md.moktype.network.ApiService;
import com.md.moktype.network.data.response.VersionRes;
import com.md.moktype.ui.activity.view.NonLeakingWebView;
import com.md.moktype.ui.popup.AlarmDialog;
import com.md.moktype.ui.popup.MessageDialog;
import com.md.moktype.ui.popup.PermissionDialog;
import com.md.moktype.utils.CommonUtils;
import com.md.moktype.utils.Formatter;
import com.md.moktype.utils.LogUtil;
import com.md.moktype.utils.Prefs;

import org.json.JSONObject;

public class SplashActivity extends BaseWebActivity {

    public final int CHECK_NETWORK_CONNECTED = 1;
    public final int CHECK_PERMISSION_POPUP = 2;
    public final int CHECK_UPDATE_APP = 3;
    public final int CHECK_PUSH_ALRAM = 4;
    public final int CHECK_START_PAGE_LOADING = 5;
    public final int GO_MAIN = 6;

    private boolean isSplash = false;
    private boolean isCheck = false;

    private Context context;
    private NonLeakingWebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        context = this;

        if(BuildConfig.IS_MANAGER) {
            Toast.makeText(this, "목형 관리자 입니다.", Toast.LENGTH_SHORT).show();
        }

        initView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Constants.REQUEST_NETWORK_SETTING) {
            initView();
        }
    }

    public void initView() {

        // webView
        webView = findViewById(R.id.webView);
        initWebView(this, webView);

        // image
        Glide.with(this)
                .load(R.drawable.intro)
                .into((ImageView)findViewById(R.id.iv_intro));

        // 스플래시 이미지 0.5초 노출
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isSplash = true;
                startMain();
            }
        }, 500);

        // 네트워크, 업데이트, 푸시알림 체크
        mHandler.sendEmptyMessage(CHECK_NETWORK_CONNECTED);
    }

    /**
     * 스플래쉬 화면에서 Back Button 막음
     */
    @Override
    public void onBackPressed() {
        onFinish();
    }

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {

                // 네트워크 연결 확인
                case CHECK_NETWORK_CONNECTED:
                    LogUtil.e("TEST", "call CHECK_NETWORK_CONNECTED ");
                    if(isNetworkConnected()) {
                        mHandler.sendEmptyMessage(CHECK_PERMISSION_POPUP);
                    }else{
                        showNetworkErrorAlert();
                    }
                    break;

                // 퍼미션 팝업 (최초 1회)
                case CHECK_PERMISSION_POPUP:
                    LogUtil.e("TEST", "call CHECK_PERMISSION_POPUP ");
//                    if(Prefs.getBoolean(Constants.PREFS_FIRST_PERMISSION_POPUP, true)) {
//                        Prefs.putBoolean(Constants.PREFS_FIRST_PERMISSION_POPUP, false);
//                        showPermissionAlert();
//                    }else{
//                    }
                    mHandler.sendEmptyMessage(CHECK_UPDATE_APP);
                    break;

                // 앱 업데이트 확인
                case CHECK_UPDATE_APP:
                    LogUtil.e("TEST", "call CHECK_UPDATE_APP ");
//                    getCurrentVersion();
                    mHandler.sendEmptyMessage(CHECK_PUSH_ALRAM);
                    break;

                // 푸시 알림동의 (최초 1회)
                case CHECK_PUSH_ALRAM:
                    LogUtil.e("TEST", "call CHECK_PUSH_ALRAM ");
//                    if(TextUtils.isEmpty(Prefs.getString(Constants.PREFS_FIRST_AGREE_PUSH))) {
//                        showPushAgreeAlert();   //최초 알림푸시 동의
//                    }else{
//                    }
                    mHandler.sendEmptyMessage(CHECK_START_PAGE_LOADING);
                    break;

                //로그인 페이지 load
                case CHECK_START_PAGE_LOADING:
                    LogUtil.e("TEST", "call CHECK_START_PAGE_LOADING ");
                    try{
                        loadUrl(webView, Constants.URL_LOGIN);
                    }catch (Exception e) {
                        LogUtil.e("TEST", "failed load loginPage  = " + e.getMessage());
                    }finally {
                        mHandler.sendEmptyMessage(GO_MAIN);
                    }
                    break;

                //메인으로
                case GO_MAIN:
                    LogUtil.e("TEST", "call GO_MAIN ");
                    isCheck = true;
                    startMain();
                    break;
            }
        }
    };

    /**
     * 네트워크 연결 체크
     * @return 연결여부
     */
    private boolean isNetworkConnected() {
        int status = CommonUtils.getConnectivityStatus(this);
        return status == CommonUtils.TYPE_MOBILE || status == CommonUtils.TYPE_WIFI;
    }

    /**
     * 네트워크 오류 얼럿
     */
    public void showNetworkErrorAlert() {
        new MessageDialog.Builder(context)
                .setCancelable(false)
                .setTitle(getString(R.string.err_network_title))
                .setMessage(getString(R.string.err_network_message))
                .setNegativeButton(getString(R.string.app_finish), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setPositiveButton(getString(R.string.now_setting), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                startActivityForResult(intent, Constants.REQUEST_NETWORK_SETTING);
            }
        }).build().show();
    }

    /**
     * 앱내 사용되는 권한 안내팝업
     */
    public void showPermissionAlert() {
        LogUtil.e("TEST", "=== showPermissionAlert() ===");
        new PermissionDialog.Builder(context)
                .setCancelable(false)
                .setPositiveButton(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mHandler.sendEmptyMessage(CHECK_UPDATE_APP);
                    }
                }).build().show();
    }

    /**
     * 최신버전 가져오기
     */
    public void getCurrentVersion() {
        LogUtil.e("TEST", "=== getCurrentVersion() ===");

        ApiService.getInstance().getVersion().enqueue(new ApiCallback<Object>() {
            @Override
            public void onSuccess(@NonNull JSONObject object) {
                VersionRes res = new Gson().fromJson(object.toString(), VersionRes.class);

                LogUtil.e("TEST", "getVersion() onSuccess = " + res.toString());
                checkAppUpdate(res.getAos());
            }

            @Override
            public void onFail(int code, @Nullable String message, @NonNull Throwable t) {
                LogUtil.e("TEST", "getVersion() onFailure = " + t.getMessage());
                mHandler.sendEmptyMessage(CHECK_PUSH_ALRAM);
            }
        });
    }

    /**
     * 버전업데이트 체크
     * @param newVer
     */
    public void checkAppUpdate(String newVer) {
        LogUtil.e("TEST", "=== checkAppUpdate() ===" + newVer);

        if(!TextUtils.isEmpty(newVer)) {
            String[] appVers = BuildConfig.VERSION_NAME.split("\\.");
            String[] newVers = newVer.split("\\.");

            if(Integer.parseInt(appVers[0]) < Integer.parseInt(newVers[0])) {

                // 메이저/강제 업데이트
                // - 1자리가 커졌을때
                showMajorUpdateAlert();
                return;

            }else if(Integer.parseInt(appVers[0]) == Integer.parseInt(newVers[0])
                    && Integer.parseInt(appVers[1]) < Integer.parseInt(newVers[1])) {

                // 마이너/선택 업데이트
                // - 1자리는 같고 2자리가 커졌을때
                showMinorUpdateAlert();
                return;

            }else if(Integer.parseInt(appVers[0]) == Integer.parseInt(newVers[0])
                    && Integer.parseInt(appVers[1]) == Integer.parseInt(newVers[1])
                    && Integer.parseInt(appVers[2]) < Integer.parseInt(newVers[2])) {

                // 마이너/선택 업데이트
                // - 2자리는 같고 3자리가 커졌을때
                showMinorUpdateAlert();
                return;
            }
        }

        mHandler.sendEmptyMessage(CHECK_PUSH_ALRAM);
    }

    /**
     * 메이저 업데이트
     * 앱종료 or 업데이트
     */
    public void showMajorUpdateAlert() {
        new MessageDialog.Builder(this)
                .setCancelable(false)
                .setVisibleCloseBtn(false)
                .setTitle(getString(R.string.alert_update_title))
                .setMessage(getString(R.string.alert_update_contents))
                .setNegativeButton(getString(R.string.app_finish), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setPositiveButton(getString(R.string.update), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("market://details?id=" + getPackageName()));
                        startActivity(intent);
                        finish();
                    }
                })
                .build().show();
    }

    /**
     * 마이너 업데이트
     * 다음에 or 업데이트
     */
    public void showMinorUpdateAlert() {
        new MessageDialog.Builder(this)
                .setCancelable(false)
                .setVisibleCloseBtn(false)
                .setTitle(getString(R.string.alert_update_title))
                .setMessage(getString(R.string.alert_update_contents))
                .setNegativeButton(getString(R.string.next_time), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mHandler.sendEmptyMessage(CHECK_PUSH_ALRAM);
                    }
                })
                .setPositiveButton(getString(R.string.update), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("market://details?id=" + getPackageName()));
                        startActivity(intent);
                        finish();
                    }
                })
                .build().show();
    }

    /**
     * 푸시알람동의
     *      - 최초 1회
     */
    public void showPushAgreeAlert() {
        LogUtil.e("TEST", "=== showPushAgreeAlert() ===");
        new AlarmDialog.Builder(context)
                .setNegativeButton(getString(R.string.disagree), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showPushAgreeTime("N");
                    }
                }).setPositiveButton(getString(R.string.agree), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showPushAgreeTime("Y");
            }
        }).build().show();
    }

    /**
     * 푸시알람동의 결과
     * @param agree
     */
    public void showPushAgreeTime(final String agree) {
        LogUtil.e("TEST", "=== showPushAgreeTime() === " + agree);
        new MessageDialog.Builder(this)
                .setMessage("Y".equals(agree) ? getString(R.string.alert_push_agree_time, Formatter.getNow()) : getString(R.string.alert_push_disagree_time, Formatter.getNow()))
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Prefs.putString(Constants.PREFS_FIRST_AGREE_PUSH, agree);
                        mHandler.sendEmptyMessage(CHECK_START_PAGE_LOADING);
                    }
                })
                .setCancelable(false)
                .build().show();
    }

    /**
     * 실행
     * 자동로그인 or 로그인 화면으로 이동
     */
    public void startMain() {
        LogUtil.e("TEST", "=== startMain() === ");

        if(isSplash && isCheck) {
            if(!Prefs.getBoolean(Constants.PREFS_AUTO_LOGIN, false)) {

                //로그인 화면으로 이동
                Intent intent = new Intent(context, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_RETAIN_IN_RECENTS);

                startActivity(intent);
                finish();
            }
        }
    }
}