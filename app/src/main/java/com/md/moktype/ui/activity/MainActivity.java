package com.md.moktype.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.android.gms.fitness.FitnessOptions;
import com.md.moktype.common.Constants;
import com.md.moktype.R;
import com.md.moktype.ui.activity.view.NonLeakingWebView;
import com.md.moktype.network.ApiCallback;
import com.md.moktype.network.ApiService;
import com.md.moktype.utils.CommonUtils;
import com.md.moktype.utils.Formatter;
import com.md.moktype.utils.LogUtil;
import com.md.moktype.utils.Prefs;
import com.airbnb.lottie.LottieAnimationView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.gun0912.tedpermission.TedPermission;

public class MainActivity extends BaseWebActivity {

    private FitnessOptions fitnessOptions;

    private LottieAnimationView nav01View, nav02View, nav03View, nav04View, nav05View;
    private NonLeakingWebView webView;
    private RelativeLayout webViewContainer;

    // google fit 중복 실행관련 flag 추가
    private boolean isReading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.e("TEST", "=== onResume()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webViewContainer.removeViewAt(0);
    }

    private void initView() {

        //TODO - 안드로이드 Overview Screen
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            TypedValue typedValue = new TypedValue();
            Resources.Theme theme = getTheme();
            theme.resolveAttribute(R.attr.colorPrimary, typedValue, true);
            int color = typedValue.data;

            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
            ActivityManager.TaskDescription td = new ActivityManager.TaskDescription(null, bm, color);

            setTaskDescription(td);
            bm.recycle();
        }

        //버튼
        nav01View = findViewById(R.id.nav01View);
        nav02View = findViewById(R.id.nav02View);
        nav03View = findViewById(R.id.nav03View);
        nav04View = findViewById(R.id.nav04View);
        nav05View = findViewById(R.id.nav05View);

        //웹뷰
        webViewContainer = findViewById(R.id.webViewContainer);
        webView = findViewById(R.id.webView);
        initWebView(this, webView);
        loadUrl(webView, Constants.URL_MAIN, tokenMap);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    /**
     * 페이지 로딩 시작시 호출
     * @param url - 페이지 URL
     */
    @Override
    protected void onPageLoadingStarted(String url) {
        if(url.equals(Constants.URL_MAIN) || url.equals(Constants.URL_REWARD_MAIN) || url.equals(Constants.URL_CHALLENGE_MAIN) || url.equals(Constants.URL_NOTICE_MAIN)) {
            showButtonAnimation(url);
        }
    }

    /**
     * 페이지 로딩 완료시 호출
     * @param url - 페이지 URL
     */
    @Override
    protected void onPageLoadingFinished(String url) {

        // 앱 설치후 리워드 메인화면 최초 진입시 한번 호출
        if(url.equals(Constants.URL_REWARD_MAIN) && Prefs.getBoolean(Constants.PREFS_FIRST_REWARD, true)) {

            // App To Web - SV리워드 자동적립 알림 팝업 호출
            Prefs.putBoolean(Constants.PREFS_FIRST_REWARD, false);
            loadUrl(webView, "javascript:svRewardAutoChg()", tokenMap);
        }
    }

    /**
     * 버튼 클릭시 애니메이션
     * @param url - 페이지 URL
     */
    public void showButtonAnimation(String url) {
        menuUrl = url;
        nav01View.setImageResource(R.drawable.ic_nav_01_n);
        nav02View.setImageResource(R.drawable.ic_nav_02_n);
        nav04View.setImageResource(R.drawable.ic_nav_04_n);
        nav05View.setImageResource(R.drawable.ic_nav_05_n);

        if(url.equals(Constants.URL_MAIN)) {
            nav01View.setAnimation("ic_nav_01_ac.json");
            nav01View.playAnimation();
        }else if(url.equals(Constants.URL_REWARD_MAIN)) {
            nav02View.setAnimation("ic_nav_02_ac.json");
            nav02View.playAnimation();
        }else if(url.equals(Constants.URL_CHALLENGE_MAIN)) {
            nav04View.setAnimation("ic_nav_04_ac.json");
            nav04View.playAnimation();
        }else if(url.equals(Constants.URL_NOTICE_MAIN)) {
            nav05View.setAnimation("ic_nav_05_ac.json");
            nav05View.playAnimation();
        }
    }

    /**
     * 하단 내비게이션 바 클릭시 동작
     * @param view - button
     */
    public void onNavigationClick(View view) {
        switch (view.getId()) {
            case R.id.nav1Layout:
                loadUrl(webView, Constants.URL_MAIN, tokenMap);
                break;
            case R.id.nav2Layout:
                loadUrl(webView, Constants.URL_REWARD_MAIN, tokenMap);
                break;
            case R.id.nav3Layout:
                nPermissionMode = Constants.PERMISSION_QR_SCAN;
                checkPermission();
                break;
            case R.id.nav4Layout:
                loadUrl(webView, Constants.URL_CHALLENGE_MAIN, tokenMap);
                break;
            case R.id.nav5Layout:
                loadUrl(webView, Constants.URL_NOTICE_MAIN, tokenMap);
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        LogUtil.e("TEST", "QR코드 스캔 requestCode :: " + requestCode + " resultCode :: " + resultCode + " seqNo :: " + sSeqNo + "sHolderQr :: " + sHolderQr + " sWoodenQr :: " + sWoodenQr);

        String mngCd = "11";

        loadUrl(webView, "javascript:qrScanSuccessPop('" + sSeqNo + "', '" + sHolderQr + "', '" + sWoodenQr + "', '" + mngCd + "')", tokenMap);

        // QR코드 스캔
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "QR코드 스캔 취소", Toast.LENGTH_LONG).show();
            } else {
                // App to Web - QR 코드 결과값 전송
                webView.clearCache(true);

                LogUtil.e("TEST", "result.getContents() : " + result.getContents());

                //loadUrl(webView, "javascript:qrScanSuccessPop('" + result.getContents() + "')", tokenMap);

                //loadUrl(webView, "javascript:qrScanSuccessPop('" + result.getContents() + "')", tokenMap);

                //loadUrl(webView, "javascript:qrScanSuccessPop('" + result.getContents() + "')", tokenMap);
            }
        }

//        }else if(requestCode == Constants.REQUEST_QR_SCAN) {
//
//        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
