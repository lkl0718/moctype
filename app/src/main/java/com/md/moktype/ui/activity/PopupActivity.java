package com.md.moktype.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.md.moktype.R;
import com.md.moktype.common.Constants;
import com.md.moktype.utils.Prefs;

public class PopupActivity extends BaseActivity {

    private String strNFC = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup);

        if(getIntent().hasExtra("NFC"))
            strNFC = getIntent().getStringExtra("NFC");

        // image
        Glide.with(this)
                .load(Constants.SERVER_URL + Prefs.getString(Constants.PREFS_PARTNER_INTRO_URL))
                .into((ImageView)findViewById(R.id.iv_popup));

        //
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(mContext, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                if(strNFC != null)
                    intent.putExtra("NFC", strNFC);
                startActivity(intent);
                finish();
            }
        }, 2000);
    }
}
