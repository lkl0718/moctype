package com.md.moktype.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.Toast;

import com.md.moktype.common.Constants;
import com.md.moktype.ui.popup.LoadingDialog;
import com.md.moktype.ui.popup.ProgressDialog;
import com.md.moktype.utils.Prefs;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

public class BaseActivity extends AppCompatActivity {

    protected Context mContext;
    private LoadingDialog loadingDialog;
    private ProgressDialog progressDialog;

    //토큰값
    protected HashMap<String, String> tokenMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = this;

        //루팅 체크
        if(checkRoot()) {
            Toast.makeText(this, "루팅된 단말 입니다.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        //loading
        loadingDialog = new LoadingDialog(this);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loadingDialog.setCancelable(false);

        //progress
        progressDialog = new ProgressDialog(this);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setCancelable(false);

        //header
        tokenMap.put("LOGIN_TOKEN", Prefs.getString(Constants.PREFS_LOGIN_TOKEN));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideLoading();
    }

    /**
     * 로딩다이얼로그 Show
     */
    public void showLoading() {
        if(loadingDialog != null && !loadingDialog.isShowing())
            loadingDialog.show();
    }

    /**
     * 로딩다이얼로그 Hide
     */
    public void hideLoading() {
        if(loadingDialog != null && loadingDialog.isShowing())
            loadingDialog.dismiss();
    }

    /**
     * 루팅 탐지 및 우회체크
     * 참고 URL - https://johyungen.tistory.com/223
     *
     * @return 루팅여부 true or false
     */
    private boolean checkRoot() {
        boolean isRooted = doesSuperuserApkExit();
        boolean isSU = doesSUexit();

        if(isRooted || isSU)
            return true;

        return false;
    }

    /**
     * 아래 파일이 존재할 경우 루팅으로 판단
     * @return 루팅여부 true or false
     */
    private boolean doesSuperuserApkExit() {
        String[] paths = {"/system/app/Superuser.apk", "/system/app/su.apk", "/system/app/Spapasu.apk"};

        for(String path : paths) {
            if(new File(path).exists())
                return true;
        }

        return false;
    }

    /**
     * SU 파일 존재여부 판단
     * @return 루팅여부 true or false
     */
    private boolean doesSUexit() {
        Process process = null;

        try {
            process = Runtime.getRuntime().exec(new String[] {"/system/bin/which", "su"});
            BufferedReader in = new BufferedReader((new InputStreamReader(process.getInputStream())));
            if(in.readLine() != null)
                return true;

            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (process != null)
                process.destroy();
        }
    }
}
