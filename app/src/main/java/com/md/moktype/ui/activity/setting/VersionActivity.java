package com.md.moktype.ui.activity.setting;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.md.moktype.BuildConfig;
import com.md.moktype.R;
import com.md.moktype.network.ApiCallback;
import com.md.moktype.network.ApiService;
import com.md.moktype.network.data.response.VersionRes;
import com.md.moktype.ui.activity.BaseActivity;
import com.md.moktype.utils.LogUtil;

import org.json.JSONObject;

public class VersionActivity extends BaseActivity {

    private TextView tvMessage, tvVersion;
    private Button btnOk;

    private String appVer = BuildConfig.VERSION_NAME;
    private String newVer = BuildConfig.VERSION_NAME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_version);

        tvMessage = findViewById(R.id.tv_version_message);
        tvVersion = findViewById(R.id.tv_version_now_new);
        btnOk = findViewById(R.id.btn_stack);

        //버전체크 API
        showLoading();
        ApiService.getInstance().getVersion().enqueue(new ApiCallback<Object>() {
            @Override
            public void onSuccess(@NonNull JSONObject object) {
                hideLoading();
                VersionRes res = new Gson().fromJson(object.toString(), VersionRes.class);
                LogUtil.e("TEST", "getVersion() onSuccess = " + res.toString());

                newVer = res.getAos();
                setData();
            }

            @Override
            public void onFail(int code, @Nullable String message, @NonNull Throwable t) {
                hideLoading();
                initDefualt();
                LogUtil.e("TEST", "getVersion() onFailure [" + code + "] " + message);
            }
        });
    }

    /**
     * 화면 초기화
     */
    public void setData() {
        String[] appVers = appVer.split("\\.");
        String[] marketVers = newVer.split("\\.");

        boolean isUpdate = false;
        if(Integer.parseInt(appVers[0]) < Integer.parseInt(marketVers[0])) {
            isUpdate = true;

        }else if(Integer.parseInt(appVers[0]) == Integer.parseInt(marketVers[0])
                && Integer.parseInt(appVers[1]) < Integer.parseInt(marketVers[1])) {
            isUpdate = true;

        }else if(Integer.parseInt(appVers[0]) == Integer.parseInt(marketVers[0])
                && Integer.parseInt(appVers[1]) == Integer.parseInt(marketVers[1])
                && Integer.parseInt(appVers[2]) < Integer.parseInt(marketVers[2])) {
            isUpdate = true;
        }


        if(isUpdate) {
            //신규 버전이 있을때
            tvMessage.setText(getString(R.string.version_message1));
            tvVersion.setText(Html.fromHtml(getString(R.string.version_now_new1, appVer, newVer)));
            btnOk.setEnabled(true);
            btnOk.setTextColor(getColor(R.color.colorBtnTxtNormal));
        }else{

            //최신 버전일때
            initDefualt();
        }
    }

    public void initDefualt() {
        tvMessage.setText(getString(R.string.version_message));
        tvVersion.setText(getString(R.string.version_now_new, appVer, newVer));
        btnOk.setEnabled(false);
        btnOk.setTextColor(getColor(R.color.colorBtnTxtDisable));
    }

    /**
     * 백버튼
     * @param view
     */
    public void clickBackBtn(View view) {
        finish();
    }

    /**
     * 업데이트
     * @param view
     */
    public void onBtnClick(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=" + getPackageName()));
        startActivity(intent);
        finish();
    }
}
