package com.md.moktype.ui.activity;

import android.Manifest;
import android.app.ActivityManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.md.moktype.common.Constants;
import com.md.moktype.R;
import com.md.moktype.ui.activity.qr.QRScanActivity;
import com.md.moktype.ui.activity.view.NonLeakingWebView;
import com.md.moktype.utils.Prefs;

import java.util.List;

public class LoginActivity extends BaseWebActivity {

    private Button btnStack, btnExport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_login);
        setContentView(R.layout.activity_select_qr);

        btnStack = findViewById(R.id.btn_stack);
        btnExport = findViewById(R.id.btn_export);

        initView();

        // webView
//        NonLeakingWebView webView = findViewById(R.id.webView);
//        initWebView(this, webView);

        // 최초 실행시 앱 가이드
//        if(Prefs.getBoolean(Constants.PREFS_FIRST_APP_GUIDE, true)) {
//            Prefs.putBoolean(Constants.PREFS_FIRST_APP_GUIDE, false);
//            loadUrl(webView, Constants.URL_GUIDE);
//        }else{
//            loadUrl(webView, Constants.URL_LOGIN);
//        }

//        nPermissionMode = Constants.PERMISSION_QR_SCAN;
//        TedPermission.with(mContext)
//                .setPermissionListener(permissionlistener)
//                .setPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
//                .check();

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
        btnStack.setEnabled(true);
        btnStack.setTextColor(getColor(R.color.colorBtnTxtNormal));
        btnExport.setEnabled(true);
        btnExport.setTextColor(getColor(R.color.colorBtnTxtNormal));


    }

    public void onBtnClick(View view) {
        switch (view.getId()) {

            //인증코드 재전송
            case R.id.btn_stack:
                nPermissionMode = Constants.PERMISSION_QR_SCAN;
                checkPermission();
                break;

            //다음
            case R.id.btn_export:
                nPermissionMode = Constants.PERMISSION_QR_SCAN;
                checkPermission();
                break;
        }
    }

    protected void checkPermission() {
        TedPermission.with(mContext)
                .setPermissionListener(permissionlistener)
                .setPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();
    }

    /**
     * 권한 요청 Listener
     */
    protected PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            if(nPermissionMode == Constants.PERMISSION_QR_SCAN)
                moveToQRScan();
        }

        @Override
        public void onPermissionDenied(List<String> deniedPermissions) {
            Toast.makeText(mContext, "권한을 거부하셨습니다.", Toast.LENGTH_LONG).show();
        }
    };

    /**
     * QR 코드 스캔
     */
    protected void moveToQRScan() {
        IntentIntegrator intentIntegrator = new IntentIntegrator(this);
        intentIntegrator.setOrientationLocked(false);
        intentIntegrator.setCaptureActivity(QRScanActivity.class);
        intentIntegrator.setRequestCode(Constants.REQUEST_QR_SCAN);
        intentIntegrator.setBeepEnabled(false);     //바코드 인식시 소리
        intentIntegrator.initiateScan();
    }
}
