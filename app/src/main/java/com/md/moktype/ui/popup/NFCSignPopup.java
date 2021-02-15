package com.md.moktype.ui.popup;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

public class NFCSignPopup extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.popup_nfcsign);
    }

    public void clickOKBtn(View view) {
        finish();
    }
}
