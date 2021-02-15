package com.md.moktype.common;

public class Constants {

    //URL
    public static final String DOMAIN_REAL = "https://fronthangarae.skcc.com/";
    public static final String DOMAIN_DEV = "https://devfhangarae.skcc.com/";
    public static final String DOMAIN_TEST = "http://10.0.2.2:8080/";
    public static final String SERVER_URL = DOMAIN_TEST;

    //Page URL
    public static final String URL_GUIDE = SERVER_URL + "app/APP_guide.html";           //앱 가이드
    public static final String URL_LOGIN = SERVER_URL + "login.do";                     //로그인
    public static final String URL_MAIN = SERVER_URL + "front/login";          //메인화면
    public static final String URL_REWARD_MAIN = SERVER_URL + "svreward/svRewardMain.do";      //메뉴 - 리워드
    public static final String URL_CHALLENGE_MAIN = SERVER_URL + "challenge/challengeMain.do"; //메뉴 - 챌린지
    public static final String URL_NOTICE_MAIN = SERVER_URL + "community/COM_submain.do";      //메뉴 - 공지
    public static final String URL_SETTING = SERVER_URL + "appsystem/appsystemMain.do";        //설정

    //Permission
    public static final int PERMISSION_QR_SCAN      = 1001;
    public static final int PERMISSION_IMAGE        = 1002;
    public static final int PERMISSION_CAMERA       = 1003;
    public static final int FROM_CAMERA             = 1020;

    //REQUEST CODE
    public static final int REQUEST_NETWORK_SETTING         = 20000;
    public static final int REQUEST_ACTIVITY_RECOGNITION    = 20001;
    public static final int REQUEST_GOOGLE_SIGN_IN          = 20002;
    public static final int REQUEST_GALLERY                 = 20003;
    public static final int REQUEST_PERMISSION_CHECK        = 20004;
    public static final int REQUEST_PAYMENT_PW_INPUT        = 30001;
    public static final int REQUEST_PAYMENT_PW_SET          = 30002;
    public static final int REQUEST_PAYMENT_PW_RESET        = 30003;
    public static final int REQUEST_QR_SCAN                 = 49374;
    public static final int QR_STACK                        = 50000;
    public static final int QR_EXPORT                       = 50001;

    //Prefs 암호화 키
    public static final char[] SECURITYKEY = {'h', 'a', 'n', 'g', 'a', 'r', 'a', 'e', '0', '0', '1'};

    //Prefs
    public static final String PREFS_FIRST_PERMISSION_POPUP = "prefs_first_permission_popup";
    public static final String PREFS_FIRST_APP_GUIDE = "prefs_first_app_guide";
    public static final String PREFS_FIRST_AGREE_PUSH = "prefs_first_agree_push";
    public static final String PREFS_FIRST_SET_PUSH = "prefs_first_set_push";
    public static final String PREFS_FIRST_REWARD = "prefs_first_reward";
    public static final String PREFS_PAYMENT_PASSWORD = "prefs_payment_password";
    public static final String PREFS_AUTO_LOGIN = "prefs_auto_login";
    public static final String PREFS_USER_ID = "prefs_user_id";
    public static final String PREFS_USER_PW = "prefs_user_pw";
    public static final String PREFS_COIN_TOKEN = "prefs_coin_token";
    public static final String PREFS_COIN_ADDR = "prefs_coin_addr";
    public static final String PREFS_LOGIN_TOKEN = "prefs_login_token";
    public static final String PREFS_FCM_TOKEN = "prefs_fcm_token";
    public static final String PREFS_BADGE_COUNT = "prefs_badge_count";
    public static final String PREFS_NFC_QR_SETTING = "prefs_nfc_qr_setting";
    public static final String PREFS_FIRST_QR = "prefs_first_qr";
    public static final String PREFS_PARTNER_INTRO_URL = "prefs_partner_intro_url";
    public static final String PREFS_PARTNER_USING_STEPS = "prefs_partner_using_steps";
    public static final String PREFS_FIRST_USE_PERMISSION = "prefs_first_use_permission";
}
