package com.md.moktype.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.URLUtil;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebHistoryItem;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.view.View;

import com.google.zxing.integration.android.IntentIntegrator;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.md.moktype.R;
import com.md.moktype.common.Constants;
import com.md.moktype.network.ApiCallback;
import com.md.moktype.network.ApiService;
import com.md.moktype.network.data.request.AlarmReq;
import com.md.moktype.network.data.request.PushReq;
import com.md.moktype.ui.activity.qr.QRScanActivity;
import com.md.moktype.ui.activity.setting.AlarmSettingActivity;
import com.md.moktype.ui.activity.setting.VersionActivity;
import com.md.moktype.ui.activity.view.NonLeakingWebView;
import com.md.moktype.ui.popup.MessageDialog;
import com.md.moktype.ui.popup.MyQRPopup;
import com.md.moktype.utils.CommonUtils;
import com.md.moktype.utils.LogUtil;
import com.md.moktype.utils.Prefs;
import com.md.moktype.BuildConfig;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class BaseWebActivity extends BaseActivity {

    private NonLeakingWebView mWebView = null;
    private boolean bFinish = false;

    protected String strNFC = null;
    protected String menuUrl = "";
    protected String sHolderQr = "";
    protected String sWoodenQr = "";
    protected String sSeqNo = "";

    protected int nPermissionMode = Constants.PERMISSION_IMAGE;
    protected int nQRcodeMode = Constants.QR_STACK;
    protected boolean bCamera = false;
    protected Uri mPhotoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        stopCountDown();

        // 캐시 삭제
        mWebView.clearCache(true);
        mWebView.clearFormData();
        mWebView.freeMemory();
        mWebView.doClearCache();
        WebStorage.getInstance().deleteAllData();
        CommonUtils.clearApplicationCache(mContext);

        // 쿠키 삭제
        if(this instanceof MainActivity) {
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookies(null);
        }

        //웹뷰 해제
        mWebView.removeJavascriptInterface("MoKtype");
        mWebView.removeAllViews();
        mWebView.destroyDrawingCache();
        mWebView.destroy();
        mWebView = null;
        System.gc();
    }

    /**
     * 백버튼 작업
     * 웹뷰에서 History Back 가능할 경우
     * 메인화면에서 앱 종료
     */
    @Override
    public void onBackPressed() {
        if(mWebView != null) {
            if(checkMainPages(mWebView.getUrl()) || !mWebView.canGoBack()) {

                // 앱 종료호출 - 현재 페이지가 메인이거나 히스토리가 없을때
                onFinish();
            }else{

                // WebView history stack
                // 서브메인이 있으면 해당 서브메인으로 이동
                int currentIndex = -1;
                WebBackForwardList histories = mWebView.copyBackForwardList();

                LogUtil.e("TEST", "HistoryBack : history size = " + histories.getSize());
                for(int i = histories.getSize() - 1; i >= 0; i--) {

                    WebHistoryItem history = histories.getItemAtIndex(i);
                    LogUtil.e("TEST", "HistoryBack : [" + i + "] getOriginalUrl = " +history.getOriginalUrl() + ",  url = " + history.getUrl());

                    if(checkMainPages(history.getUrl()) || history.getUrl().equalsIgnoreCase(Constants.URL_SETTING)) {
                        LogUtil.e("TEST", "HistoryBack : index = " + i + ", url = " + history.getUrl());
                        currentIndex = i;
                        break;
                    }
                }

                if(currentIndex == -1 || currentIndex == histories.getCurrentIndex()) {
                    LogUtil.e("TEST", "HistoryBack : 서브메인이 없거나 현재페이지가 서브메인");
                    mWebView.goBack();
                }else{
                    LogUtil.e("TEST", "HistoryBack");
                    mWebView.goBackOrForward(- (histories.getCurrentIndex() - currentIndex));
                }
            }
        }else{
            super.onBackPressed();
        }
    }

    /**
     * 메인 페이지 여부 확인
     * @param url - 페이지 URL
     * @return
     */
    private boolean checkMainPages(String url) {
        return url.equalsIgnoreCase(Constants.URL_MAIN)
                || url.equalsIgnoreCase(Constants.URL_REWARD_MAIN)
                || url.equalsIgnoreCase(Constants.URL_CHALLENGE_MAIN)
                || url.equalsIgnoreCase(Constants.URL_NOTICE_MAIN)
                || url.equalsIgnoreCase(Constants.SERVER_URL);
    }

   /**
     * 앱 종료
     */
    protected void onFinish() {
        if(bFinish) {
            super.onBackPressed();
        } else {
            Toast.makeText(mContext, "뒤로가기 버튼을 한번더 누르시면 종료됩니다.", Toast.LENGTH_LONG).show();
            bFinish = true;

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    bFinish = false;
                }
            }, 2000);
        }
    }

    /**
     * 페이지 로드 시작
     * @param url - 페이지 URL
     */
    protected void onPageLoadingStarted(String url) {}

    /**
     * 페이지 로드 완료
     * 페이지 완료시 특정 작업을 위해
     * @param url - 페이지 URL
     */
    protected void onPageLoadingFinished(String url) {}

    /**
     * 웹뷰 설정
     * @param context - context
     * @param webView - webview
     */
    @SuppressLint("SetJavaScriptEnabled")
    public void initWebView(Context context, final NonLeakingWebView webView) {
        this.mWebView = webView;

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);                // Javascript 사용하기
        settings.setDomStorageEnabled(true);                // Setting Local Storage
        settings.setBuiltInZoomControls(true);              // WebView 내장 줌 사용여부
        settings.setJavaScriptCanOpenWindowsAutomatically(true);    // 외부브라우저 실행
        settings.setSupportMultipleWindows(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);   // 캐시사용 정의 - 보안이슈
        settings.setAppCacheEnabled(false);                 // 앱 내부 캐시 사용 여부 설정입니다.

        // Setting UserAgent
        String userAgent = settings.getUserAgentString();
        settings.setUserAgentString(userAgent + getString(R.string.user_agent));

        // WebView 쿠키 관련
        webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.setAcceptThirdPartyCookies(webView, true);

        // 웹뷰 텍스트 고정 - 시스템 텍스트 크기 무시
        // 참고 URL - https://www.fun25.co.kr/blog/android-webview-text-zoom-setting/?category=003
        settings.setTextZoom(100);

        // 웹뷰 복사방지
        webView.setOnLongClickListener(v -> true);
        webView.setDrawingCacheEnabled(false);

        // custom
        webView.addJavascriptInterface(new WebViewBridge(context, webView), "Moktype");
        webView.setWebChromeClient(new MyWebChromeClient(context, webView));
        webView.setWebViewClient(new MyWebViewClient(context, webView));

        // WebView Chrome Debugging
        WebView.setWebContentsDebuggingEnabled(true);

        final TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);

        final String tmDevice, tmSerial, androidId;
        tmDevice = "";
        tmSerial = "";
        androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String deviceId = deviceUuid.toString();

        LogUtil.e("TEST", "deviceId :  " + deviceId + " PREFS_FCM_TOKEN :: " + Prefs.getString(Constants.PREFS_FCM_TOKEN));

    }

    /**
     * Custom WebView Client
     */
    public class MyWebViewClient extends WebViewClient {
        private Context context;
        private NonLeakingWebView webView;

        public MyWebViewClient(Context context, NonLeakingWebView webView) {
            this.context = context;
            this.webView = webView;
        }

        public void clearCache() {
            webView.post(() -> {
                webView.clearCache(true);
                webView.destroyDrawingCache();
                webView.clearFormData();
                webView.freeMemory();
                webView.doClearCache();
                WebStorage.getInstance().deleteAllData();
                CommonUtils.clearApplicationCache(context);
                System.gc();
            });
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {

            // 캐시삭제
            webView.post(() -> {
                webView.clearCache(true);
                webView.clearFormData();
                webView.freeMemory();
                webView.doClearCache();
                WebStorage.getInstance().deleteAllData();
                CommonUtils.clearApplicationCache(context);
            });

            request.getRequestHeaders().put("Cache-Control", "no-store");
            request.getRequestHeaders().put("LOGIN_TOKEN", Prefs.getString(Constants.PREFS_LOGIN_TOKEN));
            return super.shouldInterceptRequest(view, request);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            LogUtil.e("TEST", "url = " + url);

            // 캐시삭제
            webView.post(() -> {
                webView.clearCache(true);
                webView.clearFormData();
                webView.freeMemory();
                webView.doClearCache();
                WebStorage.getInstance().deleteAllData();
                CommonUtils.clearApplicationCache(context);
            });
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // 로그인 페이지 호출시 로딩제외
            if(!url.equalsIgnoreCase(Constants.URL_LOGIN))
                showLoading();

            // 페이지 호출시작 시 호출되는 함수
            onPageLoadingStarted(url);

            startCountDown();
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onPageFinished(WebView view, String url) {

            stopCountDown();

            // 로그인 페이지 호출시 로딩제외
            if(!url.equalsIgnoreCase(Constants.URL_LOGIN))
                hideLoading();

            // 페이지 호출완료 시 호출되는 함수
            onPageLoadingFinished(url);

            // 캐시 삭제
            webView.clearCache(true);
            webView.destroyDrawingCache();
            webView.clearFormData();
            webView.freeMemory();
            webView.doClearCache();
            WebStorage.getInstance().deleteAllData();
            CommonUtils.clearApplicationCache(context);
            System.gc();

        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            LogUtil.e("TEST", "[" + error.getUrl()+ "] = " + error.getPrimaryError());

            //super.onReceivedSslError(view, handler, error);
            new MessageDialog.Builder(context)
                    .setMessage("이 사이트의 보안 인증서는 신뢰할 수 없습니다.\n계속 진행하시겠습니까?")
                    .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            handler.cancel();
                        }
                    }).setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            handler.proceed();
                        }
                    })
                    .build().show();
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            int errorCode = error.getErrorCode();

            switch (errorCode) {
                case ERROR_AUTHENTICATION:          // 서버에서 사용자 인증 실패
                case ERROR_BAD_URL:                 // 잘못된 URL
                case ERROR_CONNECT:                 // 서버로 연결 실패
                case ERROR_FAILED_SSL_HANDSHAKE:    // SSL handshake 수행 실패
                case ERROR_FILE:                    // 일반 파일 오류
                case ERROR_FILE_NOT_FOUND:          // 파일을 찾을 수 없습니다
                case ERROR_HOST_LOOKUP:             // 서버 또는 프록시 호스트 이름 조회 실패
                case ERROR_IO:                      // 서버에서 읽거나 서버로 쓰기 실패
                case ERROR_PROXY_AUTHENTICATION:    // 프록시에서 사용자 인증 실패
                case ERROR_REDIRECT_LOOP:           // 너무 많은 리디렉션
                case ERROR_TIMEOUT:                 // 연결 시간 초과
                case ERROR_TOO_MANY_REQUESTS:       // 페이지 로드중 너무 많은 요청 발생
                case ERROR_UNKNOWN:                 // 일반 오류
                case ERROR_UNSUPPORTED_AUTH_SCHEME: // 지원되지 않는 인증 체계
                case ERROR_UNSUPPORTED_SCHEME:      // URI가 지원되지 않는 방식
                    break;
            }

            if(errorCode == ERROR_CONNECT || errorCode == ERROR_TIMEOUT){
                new MessageDialog.Builder(context)
                        .setMessage("서버접속이 원할하지 않습니다.")
                        .setPositiveButton(getString(R.string.finish), new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                finish();
                            }
                        })
                        .build().show();
            }

            LogUtil.e("TEST", "[" + error.getErrorCode() + "] = " + error.getDescription());
            super.onReceivedError(view, request, error);
        }
    }

    /**
     * Custom Web Chrome Client
     */
    public class MyWebChromeClient extends WebChromeClient {
        private Context context;
        private NonLeakingWebView webView;

        public MyWebChromeClient(Context context, NonLeakingWebView webView) {
            this.context = context;
            this.webView = webView;
        }

        @Override
        public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
            WebView newWebView = new WebView(context);
            WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
            transport.setWebView(newWebView);
            resultMsg.sendToTarget();
            return true;
        }

        @Override
        public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
            new MessageDialog.Builder(BaseWebActivity.this)
                    .setMessage(message)
                    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            result.confirm();
                        }
                    }).build().show();
            return true;

        }

        @Override
        public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
            new MessageDialog.Builder(BaseWebActivity.this)
                    .setMessage(message)
                    .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            result.cancel();
                        }
                    }).setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            result.confirm();
                        }
                    }).build().show();
            return true;
        }
    }

    /**
     * WebViewBridge
     */
    public class WebViewBridge {
        private Context context;
        private NonLeakingWebView webView;

        public WebViewBridge(Context context, NonLeakingWebView webView) {
            this.context = context;
            this.webView = webView;
        }

        /**
         * 알람설정 페이지로 이동
         */
        @JavascriptInterface
        public void appGoAlarmSetting() {
            startActivity(new Intent(context, AlarmSettingActivity.class));
        }

        /**
         * 버전 확인 페이지로 이동
         */
        @JavascriptInterface
        public void appGoVersion() {
            startActivity(new Intent(context, VersionActivity.class));
        }

        /**
         * 관리자 지급 코인 변경 팝업 노출
         * - 화면설계서 33 페이지의 팝업을 노출하기 위한 데이터 전달
         * @param pointName - 포인트 활동명
         * @param exPoint - 기존 포인트
         * @param newPoint - 변경될 포인트
         */
        @JavascriptInterface
        public void showAdminPointAlert(String pointName, int exPoint, int newPoint) {

        }

        /**
         * 내 QR코드 생성 호출
         */
        @JavascriptInterface
        public void createMyQR(String strParam) {
            Intent intent = new Intent(context, MyQRPopup.class);
            intent.putExtra("PARAM", strParam);
            startActivity(intent);
        }

        /**
         * QR코드 스캔 호출
         * - QR코드 스캔 요청시 호출
         */
        @JavascriptInterface
        public void requestQRScan(String strParam, String seqNo, String holderQr, String woodenQr) {

            if("STACK" == strParam){
                nQRcodeMode = Constants.QR_STACK;
            }else{
                nQRcodeMode = Constants.QR_EXPORT;
            }
            nPermissionMode = Constants.PERMISSION_QR_SCAN;
            sHolderQr = holderQr;
            sWoodenQr = woodenQr;
            sSeqNo = seqNo;

            checkPermission();
        }


        /**
         * 유저 정보 조회
         * - 네이티브에 저장된 로그인한 유저 ID, 세션 토큰, 코인 토큰, 코인 주소 등을 조회
         * @return
         *      0 : User ID
         *      1 : Login Token
         *      2 : Coin Token
         *      3 : Coin Address
         */
        @JavascriptInterface
        public String inquiryUserInfo() {
            return Prefs.getString(Constants.PREFS_FCM_TOKEN);
        }

        /**
         * 회원 탈퇴
         * - 웹에서 회원 탈퇴 하였음을 앱으로 전달
         */
        @JavascriptInterface
        public void withdrawMember() {
            Prefs.delete(Constants.PREFS_USER_ID);
            Prefs.delete(Constants.PREFS_USER_PW);
            Prefs.delete(Constants.PREFS_AUTO_LOGIN);
            Prefs.delete(Constants.PREFS_NFC_QR_SETTING);
            startActivity(new Intent(context, LoginActivity.class));
            finish();
        }

        /**
         * 자동로그인에 필요한값 호출
         * @return
         *      0 : 자동로그인여부(Y,N)
         *      1 : 로그인 ID
         *      2 : 로그인 PW
         */
        @JavascriptInterface
        public String getLoginInfo() {
            LogUtil.e("TEST", "call getLoginInfo()");
            return (Prefs.getBoolean(Constants.PREFS_AUTO_LOGIN)? "Y" : "N")
                    + "," + Prefs.getString(Constants.PREFS_USER_ID)
                    + "," + Prefs.getString(Constants.PREFS_USER_PW);
        }

        /**
         * 다음 자동로그인을 위해 값 저장
         * @param loginInfo
         *      0 : 자동로그인여부(Y,N)
         *      1 : 로그인 ID
         *      2 : 로그인 PW
         */
        @JavascriptInterface
        public void setLoginInfo(String[] loginInfo) {
            LogUtil.e("TEST", "call setLoginInfo()");
            Prefs.putBoolean(Constants.PREFS_AUTO_LOGIN, "Y".equals(loginInfo[0]));
            Prefs.putString(Constants.PREFS_USER_ID, loginInfo[1]);
            Prefs.putString(Constants.PREFS_USER_PW, loginInfo[2]);
        }

        /**
         * 자동로그인 실패
         */
        @JavascriptInterface
        public void loginFail() {
            LogUtil.e("TEST", "call loginFail()");
            webView.post(() -> {
                hideLoading();

                if(context instanceof SplashActivity) {
                    Intent intent = new Intent(context, LoginActivity.class);
                    if(strNFC != null)
                        intent.putExtra("NFC", strNFC);
                    startActivity(intent);
                    finish();
                }
            });
        }

        /**
         * 로그인 성공 시 값 저장
         * @param loginInfoArr
         *      arr[0] = userId
         *      arr[1] = loginToken
         *      arr[2] = coinToken
         *      arr[3] = coinAddress
         *      arr[4] = partner intro url
         */
        @JavascriptInterface
        public void LoginComplete(String[] loginInfoArr) {
            LogUtil.e("TEST", "call LoginComplete()");
            for(String arr : loginInfoArr)
                LogUtil.e("TEST", "loginComplete = " + arr);

            webView.post(() -> {
                Prefs.putString(Constants.PREFS_USER_ID, loginInfoArr[0]);
                Prefs.putString(Constants.PREFS_LOGIN_TOKEN, loginInfoArr[1]);
                Prefs.putString(Constants.PREFS_COIN_TOKEN, loginInfoArr[2]);
                Prefs.putString(Constants.PREFS_COIN_ADDR, loginInfoArr[3]);

                if(loginInfoArr.length > 4 && !TextUtils.isEmpty(loginInfoArr[4]))
                    Prefs.putString(Constants.PREFS_PARTNER_INTRO_URL, loginInfoArr[4]);

                //Y or N
                if(loginInfoArr.length > 5 && !TextUtils.isEmpty(loginInfoArr[5]))
                    Prefs.putString(Constants.PREFS_PARTNER_USING_STEPS, loginInfoArr[5]);

                // 푸시토큰 전송
                sendPushToken();
            });
        }

        /**
         * 로그인 시작시 호출
         */
        @JavascriptInterface
        public void startLogin() {
            webView.post(BaseWebActivity.this::showLoading);
        }

        /**
         * 로그아웃
         * - 앱에 저장된 회원정보를 지움
         */
        @JavascriptInterface
        public void logOut() {

            // 로그인정보 삭제
            Prefs.delete(Constants.PREFS_USER_ID);
            Prefs.delete(Constants.PREFS_USER_PW);
            Prefs.delete(Constants.PREFS_AUTO_LOGIN);
            Prefs.delete(Constants.PREFS_NFC_QR_SETTING);
            Prefs.delete(Constants.PREFS_LOGIN_TOKEN);
            Prefs.delete(Constants.PREFS_COIN_TOKEN);
            Prefs.delete(Constants.PREFS_COIN_ADDR);
            Prefs.delete(Constants.PREFS_PARTNER_INTRO_URL);

            // 로그인화면 이동
            startActivity(new Intent(context, LoginActivity.class));
            finish();
        }

        /**
         * 설정닫기
         * - 설정 화면에서 우측 상단의 닫기 버튼 클릭시 호출.
         * - 현재 탭의 Main URL 로 Redirection 시켜준다
         */
        @JavascriptInterface
        public void onClickCloseBtn() {
            webView.post(() -> {
                if(TextUtils.isEmpty(menuUrl)) {
                    webView.goBack();
                }else{
                    loadUrl(webView, menuUrl);
                }
            });
        }

        /**
         * 웹뷰 캐시 삭제
         */
        @JavascriptInterface
        public void clearCache() {
            webView.post(() -> {

                // 캐시 삭제
                //webView.stopLoading();
                webView.clearCache(true);
                webView.destroyDrawingCache();
                webView.clearFormData();
                //webView.clearHistory();
                webView.freeMemory();
                webView.doClearCache();
                WebStorage.getInstance().deleteAllData();
                CommonUtils.clearApplicationCache(context);

                System.gc();
            });
        }
    }

    /**
     * 푸시 전송
     */
    private void sendPushToken() {

        //sendDeviceInfo param
        PushReq req = new PushReq();
        req.setCMD("sendDeviceInfo");
        req.setDEV_CD("aos");
        req.setDEV_OS(Objects.toString(Build.VERSION.SDK_INT, ""));
        req.setDEV_INFO1(Objects.toString(Build.MODEL, ""));
        req.setDEV_INFO2(Objects.toString(BuildConfig.VERSION_NAME, ""));
        req.setPushToken(Prefs.getString(Constants.PREFS_FCM_TOKEN));

        //sendDeviceInfo call
        ApiService.getInstance().sendDeviceInfo(req).enqueue(new ApiCallback<Object>() {
            @Override
            public void onSuccess(@NonNull JSONObject object) {
                LogUtil.e("TEST", "sendDeviceInfo() onSuccess = " + object.toString());
                sendAlarmSetting();
            }

            @Override
            public void onFail(int code, @Nullable String message, @NonNull Throwable t) {
                LogUtil.e("TEST", "sendDeviceInfo() onFailure [" + code + "] " + message);
                sendAlarmSetting();
            }
        });
    }

    /**
     * 최초 알람 팝업 결과 전송
     */
    private void sendAlarmSetting() {
        if(Prefs.getBoolean(Constants.PREFS_FIRST_SET_PUSH, false)) {
            goMain();
        }else{

            final boolean isAgree = "Y".equals(Prefs.getString(Constants.PREFS_FIRST_AGREE_PUSH));

            //sendAlarmSetting param
            AlarmReq req = new AlarmReq();
            req.setCMD("sendAlarmSetting");
            req.setNoticeAlarm(isAgree);
            req.setChallengeAlarm(isAgree);
            req.setAnswerAlarm(isAgree);
            req.setQuizAlarm(isAgree);
            req.setEventAlarm(isAgree);

            //sendAlarmSetting call
            ApiService.getInstance().sendAlarmSetting(req).enqueue(new ApiCallback<Object>() {
                @Override
                public void onSuccess(@NonNull JSONObject object) {
                    LogUtil.e("TEST", "sendAlarmSetting() onSuccess = " + object.toString());
                    Prefs.putBoolean(Constants.PREFS_FIRST_SET_PUSH, true);
                    goMain();
                }

                @Override
                public void onFail(int code, @Nullable String message, @NonNull Throwable t) {
                    LogUtil.e("TEST", "sendAlarmSetting() onFailure [" + code + "] " + message);
                    goMain();
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void goMain() {
        hideLoading();
        Intent intent;

        if(Prefs.hasKey(Constants.PREFS_PARTNER_INTRO_URL)) {
            intent = new Intent(mContext, PopupActivity.class);

            startActivity(intent);
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        }else{
            intent = new Intent(mContext, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

            startActivity(intent);
        }

        finish();
    }

    protected void checkPermission() {
        TedPermission.with(mContext)
                .setPermissionListener(permissionlistener)
                .setPermissions(Manifest.permission.CAMERA)
                .check();
    }

    /**
     * 권한 요청 Listener
     */
    protected PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            if(nPermissionMode == Constants.PERMISSION_QR_SCAN)
                moveToQRScan(nQRcodeMode);
        }

        @Override
        public void onPermissionDenied(List<String> deniedPermissions) {
            Toast.makeText(mContext, "권한을 거부하셨습니다.", Toast.LENGTH_LONG).show();
        }
    };

    /**
     * QR 코드 스캔
     */
    protected void moveToQRScan(int bStack) {
        IntentIntegrator intentIntegrator = new IntentIntegrator(this);
        intentIntegrator.setOrientationLocked(false);
        intentIntegrator.setCaptureActivity(QRScanActivity.class);
        intentIntegrator.setRequestCode(bStack);
        intentIntegrator.setBeepEnabled(false);     //바코드 인식시 소리
        intentIntegrator.initiateScan();
    }

    /**
     * 페이지 로드시 캐시 삭제
     * @param webView - webview
     * @param url - page URL
     */
    protected void loadUrl(NonLeakingWebView webView, String url) {
        Map<String, String> header = new HashMap<>();
        header.put("Pragma", "no-cache");
        header.put("Cache-Control", "no-cache");

        webView.clearCache(true);
        webView.loadUrl(url, header);
    }

    /**
     * 페이지 로드시 캐시 삭제
     * @param webView - webview
     * @param url - page URL
     */
    protected void loadUrl(NonLeakingWebView webView, String url, Map<String, String> header) {
        header.put("Pragma", "no-cache");
        header.put("Cache-Control", "no-cache");

        webView.clearCache(true);
        webView.loadUrl(url, header);
    }

    private CountDownTimer timer;
    private final long AUTH_LIMIT_TIME = 1000 * 10;
    private final long COUNTDOWN_INTERVAL = 1000;
    private void startCountDown() {
        timer = new CountDownTimer(AUTH_LIMIT_TIME, COUNTDOWN_INTERVAL) {
            public void onTick(long millisUntilFinished) {}
            public void onFinish() {
                hideLoading();
                String url = mWebView.getUrl();

                // StopLoading 시 CSS 깨진 화면이 나와서 삭제
                //mWebView.stopLoading();
                new MessageDialog.Builder(BaseWebActivity.this)
                        .setMessage(getString(R.string.err_server_connection))
                        .setPositiveButton(getString(R.string.retry), new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                mWebView.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        loadUrl(mWebView, url);
                                    }
                                });
                                dialog.dismiss();
                            }
                        })
                        .setCancelable(false)
                        .build().show();
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
